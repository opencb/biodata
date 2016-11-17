package org.opencb.biodata.tools.variant.filters;

import htsjdk.variant.variantcontext.VariantContext;
import org.opencb.biodata.models.core.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

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

    @Override
    public VariantFilters<VariantContext> addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Arrays.asList(region), contained);
    }

    @Override
    public VariantFilters<VariantContext> addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<VariantContext>> predicates = new ArrayList<>();
        for (Region region: regions) {
            if (contained) {
                predicates.add(variant -> variant.getContig().equals(region.getChromosome())
                        && variant.getStart() >= region.getStart()
                        && variant.getEnd() <= region.getEnd());
            } else {
                predicates.add(variant -> variant.getContig().equals(region.getChromosome())
                        && variant.getStart() <= region.getEnd()
                        && variant.getEnd() >= region.getStart());
            }
        }
        addFilterList(predicates);
        return this;
    }
}
