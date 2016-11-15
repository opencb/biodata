package org.opencb.biodata.tools.variant.iterators;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import org.opencb.biodata.tools.variant.filters.VariantFilters;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantContextVcfIterator extends VcfIterator<VariantContext> {

    public VariantContextVcfIterator(CloseableIterator<VariantContext> variantContextIterator) {
        this(variantContextIterator, null);
    }

    public VariantContextVcfIterator(CloseableIterator<VariantContext> variantContextIterator, VariantFilters<VariantContext> filters) {
        super(variantContextIterator, filters);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public VariantContext next() {
        VariantContext next = prevNext;
        findNextMatch();
        return next;
    }
}
