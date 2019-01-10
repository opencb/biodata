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

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import org.opencb.biodata.models.core.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by imedina on 11/11/16.
 */
public class SamRecordFilters extends AlignmentFilters<SAMRecord> {

    public SamRecordFilters() {
    }

    public SamRecordFilters(List<Predicate<SAMRecord>> filters) {
        this.filters = filters;
    }

    public static AlignmentFilters<SAMRecord> create() {
        return new SamRecordFilters();
    }

    @Override
    public AlignmentFilters<SAMRecord> addMappingQualityFilter(int mappingQuality) {
        filters.add(samRecord -> samRecord.getMappingQuality() >= mappingQuality);
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addMaxNumberMismatchesFilter(int maxNumberMismatches) {
        filters.add(samRecord -> {
            Object nmAttribute = samRecord.getAttribute(SAMTag.NM.name());
            if (nmAttribute != null) {
                try {
                    Integer nm = (Integer) nmAttribute;
                    return nm <= maxNumberMismatches;
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            } else {
                return true;
            }
        });
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addMaxNumberHitsFilter(int maxNumberHits) {
        filters.add(samRecord -> {
            Object nhAttribute = samRecord.getAttribute(SAMTag.NH.name());
            if (nhAttribute != null) {
                try {
                    Integer nm = (Integer) nhAttribute;
                    return nm <= maxNumberHits;
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            } else {
                return true;
            }
        });
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addProperlyPairedFilter() {
        filters.add(samRecord -> samRecord.getProperPairFlag());
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addInsertSizeFilter(int maxInsertSize) {
        filters.add(samRecord -> samRecord.getInferredInsertSize() <= maxInsertSize);
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addUnmappedFilter() {
        filters.add(samRecord -> samRecord.getReadUnmappedFlag());
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addDuplicatedFilter() {
        filters.add(samRecord -> !samRecord.getDuplicateReadFlag());
        return this;
    }

    @Override
    public AlignmentFilters<SAMRecord> addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Collections.singletonList(region), contained);
    }

    @Override
    public AlignmentFilters<SAMRecord> addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<SAMRecord>> predicates = new ArrayList<>();
        for (Region region: regions) {
            if (contained) {
                predicates.add(samRecord -> samRecord.getReferenceName().equals(region.getChromosome())
                        && samRecord.getAlignmentStart() >= region.getStart()
                        && samRecord.getAlignmentEnd() <= region.getEnd());
            } else {
                predicates.add(samRecord -> samRecord.getReferenceName().equals(region.getChromosome())
                        && samRecord.getAlignmentStart() <= region.getEnd()
                        && samRecord.getAlignmentEnd() >= region.getStart());
            }
        }
        addFilterList(predicates);
        return this;
    }
}
