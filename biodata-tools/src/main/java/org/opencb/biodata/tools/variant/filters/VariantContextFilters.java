package org.opencb.biodata.tools.variant.filters;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantContextFilters extends VariantFilters<VariantContext> {

    @Override
    public VariantFilters<VariantContext> addTypeFilter(String type) {
        filters.add(variantContext -> variantContext.getType().equals(type));
        return this;
    }

    @Override
    public VariantFilters<VariantContext> addSNPFilter() {
        filters.add(variantContext -> !variantContext.getID().equalsIgnoreCase(".")
                && !variantContext.getID().equalsIgnoreCase(""));
        return this;
    }
}
