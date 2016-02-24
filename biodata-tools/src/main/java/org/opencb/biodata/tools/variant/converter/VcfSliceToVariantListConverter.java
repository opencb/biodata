package org.opencb.biodata.tools.variant.converter;

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.protobuf.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfSliceToVariantListConverter implements Converter<VcfSliceProtos.VcfSlice, List<Variant>> {

    private VcfMeta meta;
    private LinkedHashMap<String, Integer> samplesPosition;
    private String fileId;
    private String studyId;

    public VcfSliceToVariantListConverter(VcfMeta meta) {
        this.meta = meta;
        samplesPosition = StudyEntry.sortSamplesPositionMap(meta.getVariantSource().getSamplesPosition());
        fileId = meta.getVariantSource().getFileId();
        studyId = meta.getVariantSource().getStudyId();
    }

    public VcfSliceToVariantListConverter(VariantSource source) {
        meta = new VcfMeta(source);
        samplesPosition = StudyEntry.sortSamplesPositionMap(meta.getVariantSource().getSamplesPosition());
        fileId = meta.getVariantSource().getFileId();
        studyId = meta.getVariantSource().getStudyId();
    }

    public VcfSliceToVariantListConverter(Map<String, Integer> samplesPosition, String fileId, String studyId) {
        this.samplesPosition = StudyEntry.sortSamplesPositionMap(samplesPosition);
        this.fileId = fileId;
        this.studyId = studyId;
    }

    @Override
    public List<Variant> convert(VcfSliceProtos.VcfSlice vcfSlice) {

        VcfRecordToVariantConverter recordConverter = new VcfRecordToVariantConverter(vcfSlice.getFields(),
                samplesPosition, fileId, studyId);

        List<Variant> variants = new ArrayList<>(vcfSlice.getRecordsCount());
        for (VcfSliceProtos.VcfRecord vcfRecord : vcfSlice.getRecordsList()) {
            variants.add(recordConverter.convert(vcfRecord, vcfSlice.getChromosome(), vcfSlice.getPosition()));
        }

        return variants;
    }

}
