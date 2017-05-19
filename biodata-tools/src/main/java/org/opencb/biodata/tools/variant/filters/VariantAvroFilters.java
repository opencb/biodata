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
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.FileEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantAvroFilters extends VariantFilters<Variant> {

    private String datasetId;
    private String fileId;

    public VariantAvroFilters(String datasetId, String fileId) {
        super();
        this.datasetId = datasetId;
        this.fileId = fileId;
    }

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

    @Override
    public VariantFilters<Variant> addQualFilter(double minQual) {
        filters.add(variant -> filterQuality(variant, minQual));
        return this;
    }

    @Override
    public VariantFilters<Variant> addPassFilter() {
        return addPassFilter("PASS");
    }

    @Override
    public VariantFilters<Variant> addPassFilter(String name) {
        filters.add(variant -> filterPass(variant, name));
        return this;
    }

    @Override
    public VariantFilters<Variant> addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Arrays.asList(region), contained);
    }

    @Override
    public VariantFilters<Variant> addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<Variant>> predicates = new ArrayList<>();
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

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    private boolean filterQuality(Variant variant, double minQual) {
        try {
            double qual;
            if (datasetId == null || datasetId.isEmpty()) {
                if (fileId == null || fileId.isEmpty()) {
                    for (StudyEntry studyEntry : variant.getStudies()) {
                        for (FileEntry fileEntry : studyEntry.getFiles()) {
                            qual = Double.parseDouble(fileEntry.getAttributes().get("QUAL"));
                            if (qual >= minQual) {
                                return true;
                            }
                        }
                    }
                } else {
                    for (StudyEntry studyEntry : variant.getStudies()) {
                        if (studyEntry.getFile(fileId) != null) {
                            qual = Double.parseDouble(studyEntry.getFile(fileId).getAttributes().get("QUAL"));
                            if (qual >= minQual) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                StudyEntry studyEntry = variant.getStudy(datasetId);
                if (fileId == null || fileId.isEmpty()) {
                    for (FileEntry fileEntry : studyEntry.getFiles()) {
                        qual = Double.parseDouble(fileEntry.getAttributes().get("QUAL"));
                        if (qual >= minQual) {
                            return true;
                        }
                    }
                } else {
                    FileEntry fileEntry = studyEntry.getFile(fileId);
                    qual = Double.parseDouble(fileEntry.getAttributes().get("QUAL"));
                    return (qual >= minQual);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean filterPass(Variant variant, String pass) {
        try {
            if (datasetId == null || datasetId.isEmpty()) {
                if (fileId == null || fileId.isEmpty()) {
                    for (StudyEntry studyEntry : variant.getStudies()) {
                        for (FileEntry fileEntry : studyEntry.getFiles()) {
                            if (inString(fileEntry.getAttributes().get("FILTER"), pass)) {
                                return true;
                            }
                        }
                    }
                } else {
                    for (StudyEntry studyEntry : variant.getStudies()) {
                        if (studyEntry.getFile(fileId) != null) {
                            if (inString(studyEntry.getFile(fileId).getAttributes().get("QUAL"), pass)) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                StudyEntry studyEntry = variant.getStudy(datasetId);
                if (fileId == null || fileId.isEmpty()) {
                    for (FileEntry fileEntry : studyEntry.getFiles()) {
                        if (inString(fileEntry.getAttributes().get("QUAL"), pass)) {
                            return true;
                        }
                    }
                } else {
                    FileEntry fileEntry = studyEntry.getFile(fileId);
                    return inString(fileEntry.getAttributes().get("QUAL"), pass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean inString(String values, String toFind) {
        if (values != null && !values.isEmpty()) {
            String fields[] = values.split("[,;]");
            for (String field: fields) {
                if (field.equals(toFind)) {
                    return true;
                }
            }
        }
        return false;
    }
}
