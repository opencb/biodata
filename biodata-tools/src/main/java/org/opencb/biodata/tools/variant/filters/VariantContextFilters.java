/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

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
    public VariantContextFilters addTypeFilter(String type) {
        filters.add(variantContext -> variantContext.getType().equals(type));
        return this;
    }

    @Override
    public VariantContextFilters addSNPFilter() {
        filters.add(variantContext -> !variantContext.getID().equalsIgnoreCase(".")
                && !variantContext.getID().equalsIgnoreCase(""));
        return this;
    }

    @Override
    public VariantContextFilters addQualFilter(double minQual) {
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
    public VariantContextFilters addPassFilter() {
        return addPassFilter("PASS");
    }

    @Override
    public VariantContextFilters addPassFilter(String name) {
        filters.add(variantContext -> containFilter(variantContext, name));
        return this;
    }

    @Override
    public VariantContextFilters addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Arrays.asList(region), contained);
    }

    @Override
    public VariantContextFilters addRegionFilter(List<Region> regions, boolean contained) {
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
