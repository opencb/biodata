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

import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.models.core.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by imedina on 11/11/16.
 */
public class ReadAlignmentFilters extends AlignmentFilters<ReadAlignment> {

    public ReadAlignmentFilters() {
    }

    public ReadAlignmentFilters(List<Predicate<ReadAlignment>> filters) {
        this.filters = filters;
    }

    public static ReadAlignmentFilters create() {
        return new ReadAlignmentFilters();
    }

    @Override
    public ReadAlignmentFilters addMappingQualityFilter(int mappingQuality) {
        filters.add(readAlignment ->
                readAlignment.getAlignment() != null && readAlignment.getAlignment().getMappingQuality() >= mappingQuality);
        return this;
    }

    @Override
    public ReadAlignmentFilters addMaxNumberMismatchesFilter(int maxNumberMismatches) {
        filters.add(readAlignment -> {
            List<String> nmFields = readAlignment.getInfo().get("NM");
            if (nmFields != null && nmFields.size() == 2) {
                int nm = Integer.parseInt(nmFields.get(1));
                return nm <= maxNumberMismatches;
            } else {
                return true;
            }
        });
        return this;
    }

    @Override
    public ReadAlignmentFilters addMaxNumberHitsFilter(int maxNumberHits) {
        filters.add(readAlignment -> {
            List<String> nhFields = readAlignment.getInfo().get("NH");
            if (nhFields != null && nhFields.size() == 2) {
                int nh = Integer.parseInt(nhFields.get(1));
                return nh <= maxNumberHits;
            } else {
                return true;
            }
        });
        return this;
    }

    @Override
    public ReadAlignmentFilters addProperlyPairedFilter() {
        filters.add(readAlignment -> !readAlignment.getImproperPlacement());
        return this;
    }

    @Override
    public ReadAlignmentFilters addInsertSizeFilter(int maxInsertSize) {
        filters.add(readAlignment -> {
            if (readAlignment.getNextMatePosition() != null) {
                int end = readAlignment.getAlignment().getPosition().getPosition().intValue() + readAlignment.getAlignedSequence().length();
                int mateStart = readAlignment.getNextMatePosition().getPosition().intValue();
                return Math.abs(end - mateStart) <= maxInsertSize;
            }
            return false;
        });
        return this;
    }

    @Override
    public ReadAlignmentFilters addUnmappedFilter() {
        filters.add(readAlignment -> readAlignment.getAlignment() == null);
        return this;
    }

    @Override
    public ReadAlignmentFilters addDuplicatedFilter() {
        filters.add(readAlignment -> !readAlignment.getDuplicateFragment());
        return this;
    }

    @Override
    public ReadAlignmentFilters addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Arrays.asList(region), contained);
    }

    @Override
    public ReadAlignmentFilters addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<ReadAlignment>> predicates = new ArrayList<>();
        for (Region region: regions) {
            // estimate the end position of the alignment, it does not take into account the CIGAR code
            if (contained) {
                predicates.add(readAlignment -> readAlignment.getAlignment() != null
                        && readAlignment.getAlignment().getPosition().getReferenceName().equals(region.getChromosome())
                        && readAlignment.getAlignment().getPosition().getPosition() >= region.getStart()
                        && readAlignment.getAlignment().getPosition().getPosition()
                        + readAlignment.getAlignedSequence().length() <= region.getEnd());
            } else {
                predicates.add(readAlignment -> readAlignment.getAlignment() != null
                        && readAlignment.getAlignment().getPosition().getReferenceName().equals(region.getChromosome())
                        && readAlignment.getAlignment().getPosition().getPosition() <= region.getEnd()
                        && readAlignment.getAlignment().getPosition().getPosition()
                        + readAlignment.getAlignedSequence().length() >= region.getStart());
            }
        }
        addFilterList(predicates);
        return this;
    }
}
