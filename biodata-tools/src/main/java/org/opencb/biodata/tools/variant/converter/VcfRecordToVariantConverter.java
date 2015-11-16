package org.opencb.biodata.tools.variant.converter;

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfRecordToVariantConverter implements Converter<VcfSliceProtos.VcfRecord, Variant> {

    //    private final VcfSliceProtos.VcfMeta meta;
    private final VcfMeta meta;
    private final LinkedHashMap<String, Integer> samplePosition;

    public VcfRecordToVariantConverter(VariantSource meta) {
        this(new VcfMeta(meta));
    }

    public VcfRecordToVariantConverter(VcfMeta meta) {
        this.meta = meta;
        samplePosition = new LinkedHashMap<>(meta.getVariantSource().getSamples().size());
        for (String sample : meta.getVariantSource().getSamples()) {
            samplePosition.put(sample, samplePosition.size());
        }
    }

    @Override
    public Variant convert(VcfSliceProtos.VcfRecord vcfRecord) {
        return convert(vcfRecord, "0", 0);
    }


    public Variant convert(VcfSliceProtos.VcfRecord vcfRecord, String chromosome, int slicePosition) {
        int start = slicePosition + vcfRecord.getRelativeStart();
        int end = vcfRecord.getRelativeEnd() != 0 ? slicePosition + vcfRecord.getRelativeEnd() : start;

        List<String> alts = vcfRecord.getAlternateList();
        Variant variant = new Variant(chromosome, start, end, vcfRecord.getReference(), alts.get(0));

        variant.setIds(vcfRecord.getIdNonDefaultList());

        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileId(meta.getVariantSource().getFileId());
        fileEntry.setAttributes(getFileAttributes(vcfRecord));
        //fileEntry.setCall(""); //TODO

        StudyEntry studyEntry = new StudyEntry(meta.getVariantSource().getStudyId());
        studyEntry.setFiles(Collections.singletonList(fileEntry));
        studyEntry.setFormat(getFormat(vcfRecord));
        studyEntry.setSamplesData(getSamplesData(vcfRecord));
        studyEntry.setSamplesPosition(samplePosition);
        if (alts.size() > 1) { // TODO check
            studyEntry.setSecondaryAlternates(alts.subList(1, alts.size()));
        }
        variant.addStudyEntry(studyEntry);

        return variant;
    }

    private List<List<String>> getSamplesData(VcfSliceProtos.VcfRecord vcfRecord) {
        List<List<String>> samplesData = new ArrayList<>(vcfRecord.getSamplesCount());
        vcfRecord.getSamplesList().forEach(vcfSample -> samplesData.add(vcfSample.getSampleValuesList()));
        return samplesData;
    }

    private Map<String, String> getFileAttributes(VcfSliceProtos.VcfRecord vcfRecord) {
        Map<String, String> attributes = new HashMap<>(vcfRecord.getInfoKeyCount());
        Iterator<String> keyIterator;
        if (vcfRecord.getInfoKeyCount() != vcfRecord.getInfoValueCount()) {
            if (meta.getInfoDefault().size() != vcfRecord.getInfoValueCount()) {
                throw new UnsupportedOperationException("Number of info keys and info values mismatch");
            }
            keyIterator = meta.getInfoDefault().iterator();
        } else {
            keyIterator = vcfRecord.getInfoKeyList().iterator();
        }
        Iterator<String> valueIterator = vcfRecord.getInfoValueList().iterator();
        while (keyIterator.hasNext()) {
            attributes.put(keyIterator.next(), valueIterator.next());
        }
        attributes.put(VariantVcfFactory.QUAL, vcfRecord.getQuality());
        attributes.put(VariantVcfFactory.FILTER, getFilter(vcfRecord));
        return attributes;
    }

    private String getFilter(VcfSliceProtos.VcfRecord vcfRecord) {
        if (vcfRecord.getFilterNonDefault() == null || vcfRecord.getFilterNonDefault().isEmpty()) {
            return meta.getFilterDefault();
        } else {
            return vcfRecord.getFilterNonDefault();
        }
    }

    private List<String> getFormat(VcfSliceProtos.VcfRecord vcfRecord) {
        List<String> formatList = vcfRecord.getSampleFormatNonDefaultList();
        if (formatList.isEmpty()) {
            formatList = meta.getFormatDefault();
        }
        return formatList;
    }

}
