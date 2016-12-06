package org.opencb.biodata.tools.variant.iterators;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.tools.variant.converters.avro.VariantContextToVariantConverter;
import org.opencb.biodata.tools.variant.filters.VariantFilters;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantContextToAvroVariantVcfIterator extends VcfIterator<Variant> {

    private VariantContextToVariantConverter variantContextToVariantConverter;

    public VariantContextToAvroVariantVcfIterator(CloseableIterator<VariantContext> contextVariantIterator) {
        this(contextVariantIterator, null);
    }

    public VariantContextToAvroVariantVcfIterator(CloseableIterator<VariantContext> contextVariantIterator,
                                                  VariantFilters<VariantContext> filters) {
        super(contextVariantIterator, filters);
        variantContextToVariantConverter = new VariantContextToVariantConverter("", "", null);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public Variant next() {
        Variant variant = variantContextToVariantConverter.convert(prevNext);
        findNextMatch();
        return variant;
    }
}
