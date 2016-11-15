package org.opencb.biodata.tools.variant.filters;

import org.opencb.biodata.models.variant.protobuf.VariantProto;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantProtoFilters extends VariantFilters<VariantProto.Variant> {

    @Override
    public VariantFilters<VariantProto.Variant> addTypeFilter(String type) {
        filters.add(variant -> variant.getType().equals(type));
        return this;
    }

    @Override
    public VariantFilters<VariantProto.Variant> addSNPFilter() {
        filters.add(variant -> !variant.getId().equalsIgnoreCase(".")
                && !variant.getId().equalsIgnoreCase(""));
        return this;
    }
}
