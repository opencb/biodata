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

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.CommonsFilters;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by joaquin on 11/14/16.
 */
public abstract class VariantFilters<T> extends CommonsFilters<T> {

    public VariantFilters() {
    }

    public VariantFilters(List<Predicate<T>> filters) {
        super(filters);
    }

    @Override
    public VariantFilters<T> addFilter(Predicate<T> predicate) {
        super.addFilter(predicate);
        return this;
    }

    @Override
    public VariantFilters<T> addFilterList(List<Predicate<T>> predicates) {
        super.addFilterList(predicates);
        return this;
    }

    @Override
    public VariantFilters<T> addFilterList(List<Predicate<T>> predicates, boolean or) {
        super.addFilterList(predicates, or);
        return this;
    }

    public abstract VariantFilters<T> addTypeFilter(String type);

    public abstract VariantFilters<T> addSNPFilter();

    public abstract VariantFilters<T> addQualFilter(double minQual);

    public abstract VariantFilters<T> addPassFilter();

    public abstract VariantFilters<T> addPassFilter(String name);

    public abstract VariantFilters<T> addRegionFilter(Region region, boolean contained);

    public abstract VariantFilters<T> addRegionFilter(List<Region> regions, boolean contained);

    //( vaiant -> variant.getStudies().forEach(studyEntry -> studyEntry.getFiles().forEach(fileEntry -> fileEntry.get("FILTER").equals("PASS"))); //
    //( vaiant -> variant.getStudies().forEach(studyEntry -> studyEntry.getFiles().forEach(fileEntry -> Integer.parseInt(fileEntry.get("QUAL")) > 20)); //
}
