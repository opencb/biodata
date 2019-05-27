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
import org.opencb.biodata.models.variant.protobuf.VariantProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantProtoFilters extends VariantFilters<VariantProto.Variant> {

    private String studyId;
    private String fileId;

    public VariantProtoFilters(String studyId, String fileId) {
        super();

        this.studyId = studyId;
        this.fileId = fileId;
    }

    @Override
    public VariantProtoFilters addTypeFilter(String type) {
        filters.add(variant -> variant.getType().equals(type));
        return this;
    }

    @Override
    public VariantProtoFilters addSNPFilter() {
        filters.add(variant -> !variant.getId().equalsIgnoreCase(".") && !variant.getId().equalsIgnoreCase(""));
        return this;
    }

    @Override
    public VariantProtoFilters addQualFilter(double minQual) {
        throw new UnsupportedOperationException("Filter VariantProto.Variant by quality not supported yet!");
    }

    @Override
    public VariantProtoFilters addPassFilter() {
        return addPassFilter("PASS");
    }

    @Override
    public VariantProtoFilters addPassFilter(String name) {
        throw new UnsupportedOperationException("Filter VariantProto.Variant by PASS not supported yet!");
    }

    @Override
    public VariantProtoFilters addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Collections.singletonList(region), contained);
    }

    @Override
    public VariantProtoFilters addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<VariantProto.Variant>> predicates = new ArrayList<>();
        for (Region region: regions) {
            if (contained) {
                predicates.add(variant -> variant.getChromosome().equals(region.getChromosome())
                        && variant.getStart() >= region.getStart()
                        && variant.getEnd() <= region.getEnd());
            } else {
                predicates.add(variant -> variant.getChromosome().equals(region.getChromosome())
                        && variant.getStart() <= region.getEnd()
                        && variant.getEnd() >= region.getStart());
            }
        }
        addFilterList(predicates);
        return this;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
