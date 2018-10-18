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

import htsjdk.variant.vcf.VCFConstants;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.*;
import java.util.function.Predicate;

import static org.opencb.biodata.models.variant.StudyEntry.FILTER;
import static org.opencb.biodata.models.variant.StudyEntry.QUAL;

/**
 * Created by joaquin on 11/14/16.
 */
public class VariantAvroFilters extends VariantFilters<Variant> {

    private String datasetId;
    private String fileId;

    public VariantAvroFilters() {
        super();
    }

    public VariantAvroFilters(Predicate<Variant>... filters) {
        super(Arrays.asList(filters));
    }

    public VariantAvroFilters(String datasetId, String fileId) {
        super();
        this.datasetId = datasetId;
        this.fileId = fileId;
    }

    @Override
    public VariantAvroFilters addTypeFilter(String type) {
        return addTypeFilter(VariantType.valueOf(type));
    }

    public VariantAvroFilters addTypeFilter(VariantType variantType) {
        filters.add(variant -> variant.getType().equals(variantType));
        return this;
    }

    @Override
    public VariantAvroFilters addSNPFilter() {
        filters.add(variant -> !variant.getId().equals(".") && !variant.getId().isEmpty());
        return this;
    }

    public VariantAvroFilters addSampleFormatFilter(String formatKey, Predicate<String> valueValidator) {
        filters.add(variant -> filterSampleFormat(variant, formatKey, true, valueValidator));
        return this;
    }

    @Override
    public VariantAvroFilters addQualFilter(double minQual) {
        filters.add(variant -> filterQuality(variant, minQual));
        return this;
    }

    @Override
    public VariantAvroFilters addPassFilter() {
        return addPassFilter("PASS");
    }

    @Override
    public VariantAvroFilters addPassFilter(String name) {
        filters.add(variant -> filterFilter(variant, name));
        return this;
    }

    @Override
    public VariantAvroFilters addRegionFilter(Region region, boolean contained) {
        return addRegionFilter(Collections.singletonList(region), contained);
    }

    @Override
    public VariantAvroFilters addRegionFilter(List<Region> regions, boolean contained) {
        List<Predicate<Variant>> predicates = new ArrayList<>(regions.size());
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

    /**
     * Add a list of comma semi colon filters. Must pass all filters.
     * Each filter can be defined with {type}:{key}[>|<|=]{value}
     * Example:
     *  FORMAT:GQ>10;INFO:DP>40;FILTER=lowGQ,lowDP
     *
     * @param filters          Filters
     */
    public VariantAvroFilters addFilter(String filters) {
        String[] filtersSplit = filters.split(";");
        return addFilter(filtersSplit);
    }

    /**
     * Add a list of filters. Must pass all filters. Each filter can be defined with {type}:{key}[>|<|=]{value}
     * Example:
     *  FORMAT:GQ>10
     *  INFO:DP>40
     *  FILTER=lowGQ,lowDP
     *
     * @param filters          Filters
     */
    public VariantAvroFilters addFilter(String... filters) {
        return addFilter(true, filters);
    }

    /**
     * Add a list of filters. Each filter can be defined with {type}:{key}[>|<|=]{value}
     * Example:
     *  FORMAT:GQ>10
     *  INFO:DP>40
     *  FILTER=lowGQ,lowDP
     *
     * @param mustPassAll      Must pass all filters
     * @param filters          Filters
     */
    public VariantAvroFilters addFilter(boolean mustPassAll, String... filters) {
        return addFilter(mustPassAll, false, filters);
    }

    /**
     * Add a list of filters. Each filter can be defined with {type}:{key}[>|<|=]{value}
     * Example:
     *  FORMAT:GQ>10
     *  INFO:DP>40
     *  FILTER=lowGQ,lowDP
     *
     * @param mustPassAll      Must pass all filters
     * @param acceptNull       Pass filter if the value is null
     * @param filters          Filters
     */
    public VariantAvroFilters addFilter(boolean mustPassAll, boolean acceptNull, String... filters) {
        List<Predicate<Variant>> filtersList = new ArrayList<>(filters.length);

        for (String filter : filters) {

            String[] keyOpValue = splitOperator(filter);
            String[] typeKey = keyOpValue[0].split(":");

            String type;
            String key;
            if (typeKey.length == 1) {
                if (typeKey[0].equals(FILTER) || typeKey[0].equals(QUAL)) {
                    type = "FILE";
                    key = typeKey[0];
                } else {
                    throw new IllegalArgumentException("");
                }
            } else {
                type = typeKey[0];
                key = typeKey[1];
            }
            String op = keyOpValue[1];
            String value = keyOpValue[2];

            Predicate<String> predicate;
            switch (type) {
                case "FORMAT":
                    predicate = buildPredicate(op, value, acceptNull);
                    filtersList.add(v -> filterSampleFormat(v, key, true, predicate));
                    break;
                case "INFO":
                case "FILE":
                    if (key.equals(FILTER)) {
                        if (op.equals("=") || op.equals("==")) {
                            boolean containsFilter;
                            if (value.startsWith("!")) {
                                value = value.replace("!", "");
                                containsFilter = false;
                            } else {
                                containsFilter = true;
                            }
                            Set<String> values;
                            if (value.contains(",") || acceptNull) {
                                values = new HashSet<>(Arrays.asList(value.split(",")));
                                if (acceptNull) {
                                    values.add(null);
                                }
                            } else {
                                values = Collections.singleton(value);
                            }
                            predicate = filterValue -> {
                                for (String v : filterValue.split(VCFConstants.FILTER_CODE_SEPARATOR)) {
                                    if (values.contains(v)) {
                                        return containsFilter;
                                    }
                                }
                                return !containsFilter;
                            };

                        } else {
                            throw new IllegalArgumentException("Invalid operator " + op + " for FILE:FILTER");
                        }
                    } else {
                        predicate = buildPredicate(op, value, acceptNull);
                    }
                    filtersList.add(v -> filterFileAttribute(v, key, predicate));
                    break;
                default:
                    throw new IllegalArgumentException("unknown " + type);
            }
        }
        addFilterList(filtersList, !mustPassAll);
        return this;
    }

    private boolean filterQuality(Variant variant, double minQual) {
        try {
            double qual;
            if (datasetId == null || datasetId.isEmpty()) {
                if (fileId == null || fileId.isEmpty()) {
                    for (StudyEntry studyEntry : variant.getStudies()) {
                        for (FileEntry fileEntry : studyEntry.getFiles()) {
                            qual = Double.parseDouble(fileEntry.getAttributes().get(QUAL));
                            if (qual >= minQual) {
                                return true;
                            }
                        }
                    }
                } else {
                    for (StudyEntry studyEntry : variant.getStudies()) {
                        if (studyEntry.getFile(fileId) != null) {
                            qual = Double.parseDouble(studyEntry.getFile(fileId).getAttributes().get(QUAL));
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
                        qual = Double.parseDouble(fileEntry.getAttributes().get(QUAL));
                        if (qual >= minQual) {
                            return true;
                        }
                    }
                } else {
                    FileEntry fileEntry = studyEntry.getFile(fileId);
                    qual = Double.parseDouble(fileEntry.getAttributes().get(QUAL));
                    return (qual >= minQual);
                }
            }
        } catch (NumberFormatException e) {
//            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean filterFilter(Variant variant, String pass) {
        if (datasetId == null || datasetId.isEmpty()) {
            if (fileId == null || fileId.isEmpty()) {
                for (StudyEntry studyEntry : variant.getStudies()) {
                    for (FileEntry fileEntry : studyEntry.getFiles()) {
                        if (inString(fileEntry.getAttributes().get(FILTER), pass)) {
                            return true;
                        }
                    }
                }
            } else {
                for (StudyEntry studyEntry : variant.getStudies()) {
                    if (studyEntry.getFile(fileId) != null) {
                        if (inString(studyEntry.getFile(fileId).getAttributes().get(FILTER), pass)) {
                            return true;
                        }
                    }
                }
            }
        } else {
            StudyEntry studyEntry = variant.getStudy(datasetId);
            if (fileId == null || fileId.isEmpty()) {
                for (FileEntry fileEntry : studyEntry.getFiles()) {
                    if (inString(fileEntry.getAttributes().get(FILTER), pass)) {
                        return true;
                    }
                }
            } else {
                FileEntry fileEntry = studyEntry.getFile(fileId);
                return inString(fileEntry.getAttributes().get(FILTER), pass);
            }
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

    private boolean filterSampleFormat(Variant variant, String formatKey, boolean mustPassAll, Predicate<String> valueValidator) {
        StudyEntry studyEntry = getStudyEntry(variant, datasetId);
        Integer idx = studyEntry.getFormatPositions().get(formatKey);
        if (idx == null || idx < 0) {
            return valueValidator.test(null);
        }
        if (mustPassAll) {
            for (List<String> data : studyEntry.getSamplesData()) {
                String value = data.get(idx);
                if (!valueValidator.test(value)) {
                    return false;
                }
            }
            return true;
        } else {
            for (List<String> data : studyEntry.getSamplesData()) {
                String value = data.get(idx);
                if (valueValidator.test(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean filterFileAttribute(Variant variant, String attributeKey, Predicate<String> valueValidator) {
        FileEntry fileEntry = getFileEntry(variant, datasetId, fileId);
        String value = fileEntry == null ? null : fileEntry.getAttributes().get(attributeKey);

        return valueValidator.test(value);
    }

    private static StudyEntry getStudyEntry(Variant variant, String studyId) {
        StudyEntry studyEntry;
        if (studyId == null) {
            if (variant.getStudies().size() != 1) {
                throw new IllegalArgumentException("Required one Study per variant. Found " + variant.getStudies().size()
                        + " studies instead");
            }
            studyEntry = variant.getStudies().get(0);
        } else {
            studyEntry = variant.getStudy(studyId);
        }
        return studyEntry;
    }

    private static FileEntry getFileEntry(Variant variant, String studyId, String fileId) {
        StudyEntry studyEntry = getStudyEntry(variant, studyId);
        if (studyEntry == null) {
            return null;
        }
        if (fileId == null) {
            if (studyEntry.getFiles().size() != 1) {
                throw new IllegalArgumentException("Required one File per variant. Found " + studyEntry.getFiles().size()
                        + " files instead");
            }
            return studyEntry.getFiles().get(0);
        } else {
            return studyEntry.getFile(fileId);
        }
    }
}
