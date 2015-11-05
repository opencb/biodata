package org.opencb.biodata.tools.variant.converter;

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfRecordToVariantConverter implements Converter<VcfSliceProtos.VcfRecord, Variant> {

    private final VcfSliceProtos.VcfMeta meta;
    private final LinkedHashMap<String, Integer> samplePosition;

    public VcfRecordToVariantConverter(VcfSliceProtos.VcfMeta meta) {
        this.meta = meta;
        samplePosition = new LinkedHashMap<>(meta.getSamplesCount());
        for (String sample : meta.getSamplesList()) {
            samplePosition.put(sample, samplePosition.size());
        }
    }

    @Override
    public Variant convert(VcfSliceProtos.VcfRecord vcfRecord) {
        return convert(vcfRecord, "0", 0);
    }


    public Variant convert(VcfSliceProtos.VcfRecord vcfRecord, String chromosome, int slicePosition) {
        Variant variant = new Variant(chromosome, slicePosition + vcfRecord.getRelativeStart(),
                slicePosition + vcfRecord.getRelativeEnd(),
                vcfRecord.getReference(), vcfRecord.getAlternate());

        variant.setIds(vcfRecord.getIdNonDefaultList());

        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileId(meta.getFileId());
        fileEntry.setAttributes(getFileAttributes(vcfRecord));
        //fileEntry.setCall(""); //TODO

        StudyEntry studyEntry = new StudyEntry(meta.getStudyId());
        studyEntry.setFiles(Collections.singletonList(fileEntry));
        studyEntry.setFormat(getFormat(vcfRecord));
        studyEntry.setSamplesData(getSamplesData(vcfRecord));
        studyEntry.setSamplesPosition(samplePosition);
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
            if (meta.getInfoDefaultCount() != vcfRecord.getInfoValueCount()) {
                throw new UnsupportedOperationException("Number of info keys and info values mismatch");
            }
            keyIterator = meta.getInfoDefaultList().iterator();
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
            formatList = meta.getFormatDefaultList();
        }
        return formatList;
    }

}
