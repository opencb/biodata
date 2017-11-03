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

package org.opencb.biodata.tools.alignment.filters;

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.CommonsFilters;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by pfurio on 26/10/16.
 */
public abstract class AlignmentFilters<T> extends CommonsFilters<T> {

    public AlignmentFilters() {
    }

    public AlignmentFilters(List<Predicate<T>> filters) {
        super(filters);
    }

    @Override
    public AlignmentFilters<T> addFilter(Predicate<T> predicate) {
        super.addFilter(predicate);
        return this;
    }

    @Override
    public AlignmentFilters<T> addFilterList(List<Predicate<T>> predicates) {
        super.addFilterList(predicates);
        return this;
    }

    @Override
    public AlignmentFilters<T> addFilterList(List<Predicate<T>> predicates, boolean or) {
        super.addFilterList(predicates, or);
        return this;
    }

    public abstract AlignmentFilters<T> addMappingQualityFilter(int mappingQuality);

    /**
     * Add a filter for reads with <= maxNumberMismatches, by default all are returned
     * @param maxNumberMismatches Max number of mismatches allowed
     * @return A filter for reads with <= maxNumberMismatches
     */
    public abstract AlignmentFilters<T> addMaxNumberMismatchesFilter(int maxNumberMismatches);

    public abstract AlignmentFilters<T> addMaxNumberHitsFilter(int maxNumberHits);

    public abstract AlignmentFilters<T> addProperlyPairedFilter();

    public abstract AlignmentFilters<T> addUnmappedFilter();

    public abstract AlignmentFilters<T> addDuplicatedFilter();

    public abstract AlignmentFilters<T> addRegionFilter(Region region, boolean contained);

    public abstract AlignmentFilters<T> addRegionFilter(List<Region> regions, boolean contained);
}
