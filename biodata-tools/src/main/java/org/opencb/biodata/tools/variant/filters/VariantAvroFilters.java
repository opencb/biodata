package org.opencb.biodata.tools.variant.filters;

import org.opencb.biodata.models.variant.Variant;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantAvroFilters extends VariantFilters<Variant> {
    @Override
    public VariantFilters<Variant> addTypeFilter(String type) {
        filters.add(variant -> variant.getType().equals(type));
        return this;
    }

    @Override
    public VariantFilters<Variant> addSNPFilter() {
        filters.add(variant -> !variant.getId().equalsIgnoreCase(".")
                && !variant.getId().equalsIgnoreCase(""));
        return this;
    }
}
