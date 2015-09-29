/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.variant;

import java.util.*;
import java.util.stream.Collectors;

import org.opencb.biodata.models.variant.stats.VariantStats;

/** 
 * Entry that associates a variant and a file in a variant archive. It contains 
 * information related to samples, statistics and specifics of the file format.
 * 
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantSourceEntry {

    private Map<String, Integer> samplePositions = null;
    private Map<String, Integer> formatPosition = null;
    private Map<String, VariantStats> cohortStats = null;
    private final org.opencb.biodata.models.variant.avro.VariantSourceEntry impl;

    /**
     * Unique identifier of the archived file.
     */
//    private String fileId;
    
    /**
     * Unique identifier of the study containing the archived file.
     */
//    private String studyId;
    
    /**
     * Alternate alleles that appear along with a variant alternate.
     */
//    private String[] secondaryAlternates;
    
    /**
     * Fields stored for each sample.
     */
//    private String format;
    
    /**
     * Genotypes and other sample-related information. The keys are the names
     * of the samples. The values are pairs (field name, field value), such as
     * (GT, A/C).
     */
//    private Map<String, Map<String, String>> samplesData;
    
    /**
     * Statistics of the genomic variation, such as its alleles/genotypes count 
     * or its minimum allele frequency, grouped by cohort name.
     */
//    private Map<String, VariantStats> cohortStats;
    public static final String DEFAULT_COHORT = "ALL";
    
    /**
     * Optional attributes that probably depend on the format of the file the
     * variant was initially read from.
     */
//    private Map<String, String> attributes;


    public VariantSourceEntry() {
        this(null, null);
    }
    
    public VariantSourceEntry(org.opencb.biodata.models.variant.avro.VariantSourceEntry other) {
        impl = other;
    }

    public VariantSourceEntry(String fileId, String studyId) {
        this(fileId, studyId, new String[0], (List<String>) null);
    }

    public VariantSourceEntry(String fileId, String studyId, String[] secondaryAlternates, String format) {
        this(fileId, studyId, secondaryAlternates, format == null ? null : Arrays.asList(format.split(":")));
    }
    public VariantSourceEntry(String fileId, String studyId, String[] secondaryAlternates, List<String> format) {
        this.impl = new org.opencb.biodata.models.variant.avro.VariantSourceEntry(studyId, fileId, Arrays.asList(secondaryAlternates), format, new LinkedList<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public void setSamplePositions(Map<String, Integer> samplePositions) {
        this.samplePositions = samplePositions;
        if (getSamplesDataList() == null || getSamplesDataList().isEmpty()) {
            for (int size = samplePositions.size(); size > 0; size--) {
                getSamplesDataList().add(null);
            }
        }
    }

    public org.opencb.biodata.models.variant.avro.VariantSourceEntry getImpl() {
        return impl;
    }

//    public void setSamplePositions(List<String> samplePositions) {
//        this.samplePositions = new HashMap<>(samplePositions.size());
//        int position = 0;
//        for (String sample : samplePositions) {
//            this.samplePositions.put(sample, position++);
//        }
//    }
//
//    @Deprecated
//    public void setSecondaryAlternates(String[] secondaryAlternates) {
//        impl.setSecondaryAlternates(Arrays.asList(secondaryAlternates));
//    }

    @Deprecated
    public String getFormat() {
        return getFormatList().stream().collect(Collectors.joining(":"));
    }

    public void setFormat(String format) {
        setFormatList(Arrays.asList(format.split(":")));
    }

    /**
     * Do not modify this list
     * @return
     */
    public List<String> getFormatList() {
        return Collections.unmodifiableList(impl.getFormat());
    }

    public void setFormatList(List<String> value) {
        formatPosition = null;
        impl.setFormat(value);
    }

    public Map<String, Integer> getFormatPositions() {
        if (formatPosition == null) {
            formatPosition = new HashMap<>();
            int pos = 0;
            for (String format : getFormatList()) {
                formatPosition.put(format, pos++);
            }
        }
        return formatPosition;
    }

    public Map<String, Map<String, String>> getSamplesData() {
        requireSamplePositions();

        Map<String, Map<String, String>> samplesDataMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : samplePositions.entrySet()) {
            samplesDataMap.put(entry.getKey(), getSampleData(entry.getKey()));
        }

        return samplesDataMap;
    }

    public String getSampleData(String sampleName, String field) {
        requireSamplePositions();
        if (samplePositions.containsKey(sampleName)) {
            Map<String, Integer> formatPositions = getFormatPositions();
            if (formatPositions.containsKey(field)) {
                return impl.getSamplesData().get(samplePositions.get(sampleName)).get(formatPositions.get(field));
            }
        }
        return null;
    }

    public Map<String, String> getSampleData(String sampleName) {
        requireSamplePositions();
        if (samplePositions.containsKey(sampleName)) {
            HashMap<String, String> sampleDataMap = new HashMap<>();
            Iterator<String> iterator = getFormatList().iterator();
            List<String> sampleDataList = impl.getSamplesData().get(samplePositions.get(sampleName));
            for (String data : sampleDataList) {
                sampleDataMap.put(iterator.next(), data);
            }

            return sampleDataMap;
        }
        return null;
    }

    public void addSampleData(String sampleName, Map<String, String> sampleData) {
        List<List<String>> samplesDataList = getSamplesDataList();
        if (samplePositions == null && samplesDataList.isEmpty()) {
            samplePositions = new LinkedHashMap<>();
        }
        List<String> sampleDataList = new ArrayList<>(getFormatList().size());
        for (String field : getFormatList()) {
            sampleDataList.add(sampleData.get(field));
        }
        if (sampleData.size() != sampleDataList.size()) {
            List<String> extraFields = sampleData.keySet().stream().filter(f -> getFormatList().contains(f)).collect(Collectors.toList());
            throw new IllegalArgumentException("Some sample data fields were not in the format field: " + extraFields);
        }
        if (samplePositions != null) {
            if (samplePositions.containsKey(sampleName)) {
                int position = samplePositions.get(sampleName);
                while (samplesDataList.size() <= position) {
                    samplesDataList.add(null);
                }
                samplesDataList.set(position, sampleDataList);
            } else {
                int position = samplePositions.size();
                samplePositions.put(sampleName, position);
                samplesDataList.add(sampleDataList);
            }
        } else {
            samplesDataList.add(sampleDataList);
        }
    }

    public Set<String> getSampleNames() {
        requireSamplePositions();
        return samplePositions.keySet();
    }


    public Map<String, VariantStats> getStats() {
        resetStatsMap();
        return Collections.unmodifiableMap(cohortStats);
    }

    private void resetStatsMap() {
        if (cohortStats == null) {
            cohortStats = new HashMap<>();
            impl.getStats().forEach((k, v) -> cohortStats.put(k, new VariantStats(v)));
        }
    }

    public void setStats(Map<String, VariantStats> stats) {
        this.cohortStats = stats;
        impl.setStats(new HashMap<>(stats.size()));
        stats.forEach((k, v) -> impl.getStats().put(k, v.getImpl()));
    }

    public void setStats(VariantStats stats) {
        setStats(DEFAULT_COHORT, stats);
    }

    public void setStats(String cohortName, VariantStats stats) {
        resetStatsMap();
        cohortStats.put(cohortName, stats);
        impl.getStats().put(cohortName, stats.getImpl());
    }

    public VariantStats getStats(String cohortName) {
        resetStatsMap();
        return cohortStats.get(cohortName);
    }

    @Deprecated
    public VariantStats getCohortStats(String cohortName) {
        return getStats(cohortName);
    }

    @Deprecated
    public void setCohortStats(String cohortName, VariantStats stats) {
        setStats(cohortName, stats);
    }

    @Deprecated
    public Map<String, VariantStats> getCohortStats() {
        return getStats();
    }

    @Deprecated
    public void setCohortStats(Map<String, VariantStats> cohortStats) {
        setStats(cohortStats);
    }

    public String getAttribute(String key) {
        return getAttributes().get(key);
    }

    public void addAttribute(String key, String value) {
        getAttributes().put(key, value);
    }

    public boolean hasAttribute(String key) {
        return getAttributes().containsKey(key);
    }

    private void requireSamplePositions() {
        if (samplePositions == null) {
            throw new IllegalArgumentException("Require sample positions array to use this method!"); //TODO Unknown sample positions!
        }
    }

    public String getStudyId() {
        return impl.getStudyId();
    }

    public void setStudyId(String value) {
        impl.setStudyId(value);
    }

    public String getFileId() {
        return impl.getFileId();
    }

    public void setFileId(String value) {
        impl.setFileId(value);
    }

    public List<String> getSecondaryAlternates() {
        return impl.getSecondaryAlternates();
    }

    public void setSecondaryAlternates(List<String> value) {
        impl.setSecondaryAlternates(value);
    }

    public List<List<String>> getSamplesDataList() {
        return impl.getSamplesData();
    }

    public void setSamplesDataList(List<List<String>> value) {
        impl.setSamplesData(value);
    }

    public Map<String, String> getAttributes() {
        return impl.getAttributes();
    }

    public void setAttributes(Map<String, String> value) {
        impl.setAttributes(value);
    }
}
