package org.opencb.biodata.tools.variant.converter;

import com.google.common.base.Function;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfSliceToVariantListConverter implements Converter<VcfSliceProtos.VcfSlice, List<Variant>> {

    private final VcfMeta meta;

    public VcfSliceToVariantListConverter(VcfMeta meta) {
        this.meta = meta;
    }

    public VcfSliceToVariantListConverter(VariantSource source) {
        meta = new VcfMeta(source);
    }

    @Override
    public List<Variant> convert(VcfSliceProtos.VcfSlice vcfSlice) {

        VcfRecordToVariantConverter recordConverter = new VcfRecordToVariantConverter(vcfSlice.getFields(),
                  meta.getVariantSource().getSamplesPosition(), meta.getVariantSource().getFileId(), meta.getVariantSource().getStudyId());

        List<Variant> variants = new ArrayList<>(vcfSlice.getRecordsCount());
        for (VcfSliceProtos.VcfRecord vcfRecord : vcfSlice.getRecordsList()) {
            variants.add(recordConverter.convert(vcfRecord, vcfSlice.getChromosome(), vcfSlice.getPosition()));
        }

        return variants;
    }

}
