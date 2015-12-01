package org.opencb.biodata.tools.variant.converter;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.protobuf.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfSliceToVariantListConverter implements Converter<VcfSliceProtos.VcfSlice, List<Variant>> {

    private final VcfRecordToVariantConverter recordConverter;

    public VcfSliceToVariantListConverter(VcfMeta meta) {
        this.recordConverter = new VcfRecordToVariantConverter(meta);
    }

    public VcfSliceToVariantListConverter(VariantSource source) {
        this.recordConverter = new VcfRecordToVariantConverter(new VcfMeta(source));
    }

    @Override
    public List<Variant> convert(VcfSliceProtos.VcfSlice vcfSlice) {

        List<Variant> variants = new ArrayList<>(vcfSlice.getRecordsCount());
        for (VcfSliceProtos.VcfRecord vcfRecord : vcfSlice.getRecordsList()) {
            variants.add(recordConverter.convert(vcfRecord, vcfSlice.getChromosome(), vcfSlice.getPosition()));
        }

        return variants;
    }
}
