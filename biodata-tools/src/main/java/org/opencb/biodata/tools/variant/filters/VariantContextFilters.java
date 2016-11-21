package org.opencb.biodata.tools.variant.filters;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.Region;

import java.util.*;
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
    public VariantFilters<VariantContext> addQualFilter(double minQual) {
        filters.add(variantContext -> variantContext.getPhredScaledQual() >= minQual);
        return this;
    }

    private boolean containFilter(VariantContext variantContext, String name) {
        if (variantContext.getFilters().size() == 0 && "PASS".equals(name)) {
            // PASS is not contained in the getFilters(),
            // but the . neither!!
            return true;
        }
        Iterator<String> iterator = variantContext.getFilters().iterator();
        while (iterator.hasNext()) {
            String filterName = iterator.next();
            List<String> list = Arrays.asList(StringUtils.split(filterName, ","));
            for (String element: list) {
                if (element.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public VariantFilters<VariantContext> addPassFilter() {
        return addPassFilter("PASS");
    }

    @Override
    public VariantFilters<VariantContext> addPassFilter(String name) {
        filters.add(variantContext -> containFilter(variantContext, name));
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
