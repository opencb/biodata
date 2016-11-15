package org.opencb.biodata.tools.variant.iterators;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.tools.variant.converter.VariantContextToVariantProtoConverter;
import org.opencb.biodata.tools.variant.filters.VariantFilters;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantContextToProtoVariantVcfIterator extends VcfIterator<VariantProto.Variant> {

    private VariantContextToVariantProtoConverter variantContextToVariantProtoConverter;

    public VariantContextToProtoVariantVcfIterator(CloseableIterator<VariantContext> contextVariantIterator) {
        this(contextVariantIterator, null);
    }

    public VariantContextToProtoVariantVcfIterator(CloseableIterator<VariantContext> contextVariantIterator,
                                                  VariantFilters<VariantContext> filters) {
        super(contextVariantIterator, filters);
        variantContextToVariantProtoConverter = new VariantContextToVariantProtoConverter("", "");
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public VariantProto.Variant next() {
        VariantProto.Variant variant = variantContextToVariantProtoConverter.convert(prevNext);
        findNextMatch();
        return variant;
    }
}
