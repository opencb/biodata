package org.opencb.biodata.tools.variant.converter;

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfRecordToVariantConverter implements Converter<VcfSliceProtos.VcfRecord, Variant> {

    private VcfSliceProtos.Fields fields;

    private final LinkedHashMap<String, Integer> samplePosition;
    private final String fileId;
    private final String studyId;



    public VcfRecordToVariantConverter(VcfSliceProtos.Fields fields, Map<String, Integer> samplePosition, String fileId, String studyId) {
        this(fields, StudyEntry.sortSamplesPositionMap(samplePosition), fileId, studyId);
    }

    public VcfRecordToVariantConverter(LinkedHashMap<String, Integer> samplePosition, String fileId, String studyId) {
        this(null, samplePosition, fileId, studyId);
    }

    public VcfRecordToVariantConverter(VcfSliceProtos.Fields fields, LinkedHashMap<String, Integer> samplePosition, String fileId, String studyId) {

        this.samplePosition = samplePosition;

        this.fields = fields;
        this.fileId = fileId;
        this.studyId = studyId;

    }

    @Override
    public Variant convert(VcfSliceProtos.VcfRecord vcfRecord) {
        return convert(vcfRecord, "0", 0);
    }

    public Variant convert(VcfSliceProtos.VcfRecord vcfRecord, String chromosome, int slicePosition) {
        int start = slicePosition + vcfRecord.getRelativeStart();
        int end = getRealEnd(slicePosition, start, vcfRecord.getRelativeEnd());

        Variant variant = new Variant(chromosome, start, end, vcfRecord.getReference(), vcfRecord.getAlternate());

        variant.setType(getVariantType(vcfRecord.getType()));
        variant.setIds(vcfRecord.getIdNonDefaultList());

        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileId(fileId);
        Map<String, String> attributes = getFileAttributes(vcfRecord);
        fileEntry.setAttributes(attributes);
        fileEntry.setCall(vcfRecord.getCall());
        if (vcfRecord.getType().equals(VariantProto.VariantType.NO_VARIATION)) {
            attributes.put("END", Integer.toString(end));
        }

        StudyEntry studyEntry = new StudyEntry(studyId);
        studyEntry.setFiles(Collections.singletonList(fileEntry));
        studyEntry.setFormat(getFormat(vcfRecord));
        studyEntry.setSamplesData(getSamplesData(vcfRecord, studyEntry.getFormatPositions()));
        studyEntry.setSamplesPosition(samplePosition);
        List<VariantProto.AlternateCoordinate> alts = vcfRecord.getSecondaryAlternatesList();
        studyEntry.setSecondaryAlternates(getAlternateCoordinates(alts));
        variant.addStudyEntry(studyEntry);

        return variant;
    }

    private int getRealEnd(int slicePosition, int start, int relativeEnd) {
        final int end;
        if (relativeEnd == 0) {
            end = start;
        } else {
            if (relativeEnd < 0) {
                // Negative values are stored with one position less.
                // Restore the position adding one.
                end = slicePosition + relativeEnd + 1;
            } else {
                //Positive values are right
                end = slicePosition + relativeEnd;
            }
        }
        return end;
    }

    private List<List<String>> getSamplesData(VcfSliceProtos.VcfRecord vcfRecord, Map<String, Integer> formatPositions) {
        List<List<String>> samplesData = new ArrayList<>(vcfRecord.getSamplesCount());
        Integer gtPosition = formatPositions.get("GT");
        if (gtPosition == null) {
            for (VcfSliceProtos.VcfSample vcfSample : vcfRecord.getSamplesList()) {
                samplesData.add(vcfSample.getSampleValuesList());
            }
        } else {
            if (gtPosition != 0) {
                throw new IllegalArgumentException("GT must be in the first position or missing");
            }
            for (VcfSliceProtos.VcfSample vcfSample : vcfRecord.getSamplesList()) {
                List<String> data = new ArrayList<>(formatPositions.size());
                data.add(fields.getGts(vcfSample.getGtIndex()));
                data.addAll(vcfSample.getSampleValuesList());
                samplesData.add(data);
            }
        }

        return samplesData;
    }

    private Map<String, String> getFileAttributes(VcfSliceProtos.VcfRecord vcfRecord) {
        Map<String, String> attributes = new HashMap<>(vcfRecord.getInfoKeyIndexCount());
        Iterator<Integer> keyIdxIterator;
        if (vcfRecord.getInfoKeyIndexCount() != vcfRecord.getInfoValueCount()) {
            if (fields.getDefaultInfoKeysCount() != vcfRecord.getInfoValueCount()) {
                throw new UnsupportedOperationException("Number of info keys and info values mismatch");
            }
            keyIdxIterator = fields.getDefaultInfoKeysList().iterator();
        } else {
            keyIdxIterator = vcfRecord.getInfoKeyIndexList().iterator();
        }
        Iterator<String> valueIterator = vcfRecord.getInfoValueList().iterator();

        while (keyIdxIterator.hasNext()) {
            attributes.put(fields.getInfoKeys(keyIdxIterator.next()), valueIterator.next());
        }

        attributes.put(VariantVcfFactory.QUAL, getQuality(vcfRecord));
        attributes.put(VariantVcfFactory.FILTER, getFilter(vcfRecord));
        return attributes;
    }

    private String getQuality(VcfSliceProtos.VcfRecord vcfRecord) {
        float quality = vcfRecord.getQuality();
        return getQuality(quality);
    }

    /**
     * Decodes the Quality float value.
     *
     * See {@link VariantToProtoVcfRecord#encodeQuality(String)}
     * Decrements one to the quality value.
     * 0 means missing or unknown, and will return null.
     *
     * @param quality String quality value
     * @return Quality string
     */
    static String getQuality(float quality) {
        quality -= 1;
        if (quality == -1) {
            return null;    //Quality missing
        } else {
            String q = Float.toString(quality);
            if (q.endsWith(".0")){
                return q.substring(0, q.lastIndexOf("."));
            } else {
                return q;
            }
        }
    }

    private String getFilter(VcfSliceProtos.VcfRecord vcfRecord) {
        return fields.getFilters(vcfRecord.getFilterIndex());
    }

    private List<String> getFormat(VcfSliceProtos.VcfRecord vcfRecord) {
        String format = fields.getFormats(vcfRecord.getFormatIndex());
        return Arrays.asList(format.split(":"));
    }

    public static VariantType getVariantType(VariantProto.VariantType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case SNV: return VariantType.SNV;
            case SNP: return VariantType.SNP;
            case MNV: return VariantType.MNV;
            case MNP: return VariantType.MNP;
            case INDEL: return VariantType.INDEL;
            case SV: return VariantType.SV;
            case INSERTION: return VariantType.INSERTION;
            case DELETION: return VariantType.DELETION;
            case TRANSLOCATION: return VariantType.TRANSLOCATION;
            case INVERSION: return VariantType.INVERSION;
            case CNV: return VariantType.CNV;
            case NO_VARIATION: return VariantType.NO_VARIATION;
            case SYMBOLIC: return VariantType.SYMBOLIC;
            case MIXED: return VariantType.MIXED;
            default: return VariantType.valueOf(type.name());
        }
    }

    private List<AlternateCoordinate> getAlternateCoordinates(List<VariantProto.AlternateCoordinate> alts) {
        List<AlternateCoordinate> alternateCoordinates = new ArrayList<>(alts.size());
        if (!alts.isEmpty()) {
            for (VariantProto.AlternateCoordinate alt : alts) {
                AlternateCoordinate alternateCoordinate = new AlternateCoordinate(
                        alt.getChromosome().isEmpty() ? null : alt.getChromosome(),
                        alt.getStart() == 0? null : alt.getStart(),
                        alt.getEnd() == 0? null : alt.getEnd(),
                        alt.getReference(),
                        alt.getAlternate(),
                        getVariantType(alt.getType()));
                alternateCoordinates.add(alternateCoordinate);
            }
        }
        return alternateCoordinates;
    }

    public VcfSliceProtos.Fields getFields() {
        return fields;
    }

    public VcfRecordToVariantConverter setFields(VcfSliceProtos.Fields fields) {
        this.fields = fields;
        return this;
    }
}
