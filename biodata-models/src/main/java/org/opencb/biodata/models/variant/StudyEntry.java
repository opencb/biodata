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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;

/** 
 * Entry that associates a variant and a file in a variant archive. It contains 
 * information related to samples, statistics and specifics of the file format.
 * 
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "samplesDataAsMap", "samplesPosition", "samplesName", "orderedSamplesName", "formatAsString", "formatPositions", "fileId", "attributes", "allAttributes", "cohortStats"})
public class StudyEntry {

    private LinkedHashMap<String, Integer> samplesPosition = null;
    private Map<String, Integer> formatPosition = null;
    private Map<String, VariantStats> cohortStats = null;
    private final org.opencb.biodata.models.variant.avro.StudyEntry impl;
    public static final String DEFAULT_COHORT = "ALL";

    public StudyEntry() {
        this(null, null);
    }
    
    public StudyEntry(org.opencb.biodata.models.variant.avro.StudyEntry other) {
        impl = other;
    }

    public StudyEntry(String studyId) {
        this(null, studyId, new String[0], (List<String>) null);
    }

    public StudyEntry(String fileId, String studyId) {
        this(fileId, studyId, new String[0], (List<String>) null);
    }

    public StudyEntry(String fileId, String studyId, String[] secondaryAlternates, String format) {
        this(fileId, studyId, secondaryAlternates, format == null ? null : Arrays.asList(format.split(":")));
    }

    public StudyEntry(String fileId, String studyId, String[] secondaryAlternates, List<String> format) {
        this(fileId, studyId, Arrays.asList(secondaryAlternates), format);
    }

    public StudyEntry(String fileId, String studyId, List<String> secondaryAlternates, List<String> format) {
        this.impl = new org.opencb.biodata.models.variant.avro.StudyEntry(studyId,
                new LinkedList<>(), secondaryAlternates, format, new LinkedList<>(), new LinkedHashMap<>());
        if (fileId != null) {
            setFileId(fileId);
        }
    }

    public LinkedHashMap<String, Integer> getSamplesPosition() {
        return samplesPosition;
    }

    public void setSamplesPosition(Map<String, Integer> samplesPosition) {
        if (samplesPosition == null) {
            this.samplesPosition = null;
            return;
        }
        if (samplesPosition instanceof LinkedHashMap) {
            if (isSamplesPositionMapSorted((LinkedHashMap<String, Integer>) samplesPosition)) {
                this.samplesPosition = ((LinkedHashMap<String, Integer>) samplesPosition);
            } else {
                this.samplesPosition = sortSamplesPositionMap(samplesPosition);
            }
        } else {
            //Sort samples position
            this.samplesPosition = sortSamplesPositionMap(samplesPosition);
        }
        if (getSamplesData() == null || getSamplesData().isEmpty()) {
            for (int size = samplesPosition.size(); size > 0; size--) {
                getSamplesData().add(null);
            }
        }
    }

    public static boolean isSamplesPositionMapSorted(LinkedHashMap<String, Integer> samplesPosition) {
        int idx = 0;
        for (Map.Entry<String, Integer> entry : samplesPosition.entrySet()) {
            if (entry.getValue() != idx) {
                break;
            }
            idx++;
        }
        return idx == samplesPosition.size();
    }

    public static LinkedHashMap<String, Integer> sortSamplesPositionMap(Map<String, Integer> samplesPosition) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        String[] samples = new String[samplesPosition.size()];
        for (Map.Entry<String, Integer> entry : samplesPosition.entrySet()) {
            samples[entry.getValue()] = entry.getKey();
        }
        for (int i = 0; i < samples.length; i++) {
            map.put(samples[i], i);
        }
        return map;
    }

    public org.opencb.biodata.models.variant.avro.StudyEntry getImpl() {
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
    public String getFormatAsString() {
        return getFormat().stream().collect(Collectors.joining(":"));
    }

    public void setFormatAsString(String format) {
        setFormat(Arrays.asList(format.split(":")));
    }

    /**
     * Do not modify this list
     * @return
     */
    public List<String> getFormat() {
        return impl.getFormat() == null? null : Collections.unmodifiableList(impl.getFormat());
    }

    public void setFormat(List<String> value) {
        formatPosition = null;
        impl.setFormat(value);
    }

    public Map<String, Integer> getFormatPositions() {
        if (formatPosition == null) {
            formatPosition = new HashMap<>();
            int pos = 0;
            for (String format : getFormat()) {
                formatPosition.put(format, pos++);
            }
        }
        return formatPosition;
    }

    public List<List<String>> getSamplesData() {
        return impl.getSamplesData();
    }

    public void setSamplesData(List<List<String>> value) {
        impl.setSamplesData(value);
    }

    @Deprecated
    public Map<String, Map<String, String>> getSamplesDataAsMap() {
        requireSamplesPosition();

        Map<String, Map<String, String>> samplesDataMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : samplesPosition.entrySet()) {
            samplesDataMap.put(entry.getKey(), getSampleData(entry.getKey()));
        }

        return Collections.unmodifiableMap(samplesDataMap);
    }

    public String getSampleData(String sampleName, String field) {
        requireSamplesPosition();
        if (samplesPosition.containsKey(sampleName)) {
            Map<String, Integer> formatPositions = getFormatPositions();
            if (formatPositions.containsKey(field)) {
                return impl.getSamplesData().get(samplesPosition.get(sampleName)).get(formatPositions.get(field));
            }
        }
        return null;
    }

    public Map<String, String> getSampleData(String sampleName) {
        requireSamplesPosition();
        if (samplesPosition.containsKey(sampleName)) {
            HashMap<String, String> sampleDataMap = new HashMap<>();
            Iterator<String> iterator = getFormat().iterator();
            List<String> sampleDataList = impl.getSamplesData().get(samplesPosition.get(sampleName));
            for (String data : sampleDataList) {
                sampleDataMap.put(iterator.next(), data);
            }

            return Collections.unmodifiableMap(sampleDataMap);
        }
        return null;
    }

    public void addSampleData(String sampleName, Map<String, String> sampleData) {
        if (getFormat() == null) {
            setFormat(new ArrayList<>(sampleData.keySet()));
        }
        List<String> sampleDataList = new ArrayList<>(getFormat().size());
        for (String field : getFormat()) {
            sampleDataList.add(sampleData.get(field));
        }
        if (sampleData.size() != sampleDataList.size()) {
            List<String> extraFields = sampleData.keySet().stream().filter(f -> getFormat().contains(f)).collect(Collectors.toList());
            throw new IllegalArgumentException("Some sample data fields were not in the format field: " + extraFields);
        }
        addSampleData(sampleName, sampleDataList);
    }

    public void addSampleData(String sampleName, List<String> sampleDataList) {
        List<List<String>> samplesDataList = impl.getSamplesData();
        if (samplesPosition == null && samplesDataList.isEmpty()) {
            samplesPosition = new LinkedHashMap<>();
        }
        if (samplesPosition != null) {
            if (samplesPosition.containsKey(sampleName)) {
                int position = samplesPosition.get(sampleName);
                while (samplesDataList.size() <= position) {
                    samplesDataList.add(null);
                }
                samplesDataList.set(position, sampleDataList);
            } else {
                int position = samplesPosition.size();
                samplesPosition.put(sampleName, position);
                samplesDataList.add(sampleDataList);
            }
        } else {
            samplesDataList.add(sampleDataList);
        }
    }

    public Set<String> getSamplesName() {
        requireSamplesPosition();
        return samplesPosition.keySet();
    }

    public List<String> getOrderedSamplesName() {
        requireSamplesPosition();
        return new ArrayList<>(samplesPosition.keySet());
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

    @Deprecated
    public String getAttribute(String key) {
        return getAttributes().get(key);
    }

    @Deprecated
    public void addAttribute(String key, String value) {
        getAttributes().put(key, value);
    }

    public void addAttribute(String fileId, String key, String value) {
        getFile(fileId).getAttributes().put(key, value);
    }

    @Deprecated
    public boolean hasAttribute(String key) {
        return getAttributes().containsKey(key);
    }

    private void requireSamplesPosition() {
        if (samplesPosition == null) {
            throw new IllegalArgumentException("Require sample positions array to use this method!");
        }
    }

    public String getStudyId() {
        return impl.getStudyId();
    }

    public void setStudyId(String value) {
        impl.setStudyId(value);
    }

    public List<FileEntry> getFiles() {
        return impl.getFiles();
    }

    public void setFiles(List<FileEntry> files) {
        impl.setFiles(files);
    }

    public FileEntry getFile(String fileId) {
        for (FileEntry fileEntry : impl.getFiles()) {
            if (fileEntry.getFileId().equals(fileId)) {
                return fileEntry;
            }
        }
        return null;
    }

    @Deprecated
    public String getFileId() {
        return !impl.getFiles().isEmpty() ? impl.getFiles().get(0).getFileId() : null;
    }

    @Deprecated
    public void setFileId(String fileId) {
        if (impl.getFiles().isEmpty()) {
            impl.getFiles().add(new FileEntry(fileId, "", new HashMap<>()));
        } else {
            impl.getFiles().get(0).setFileId(fileId);
        }
    }

    public List<String> getSecondaryAlternates() {
        return impl.getSecondaryAlternates();
    }

    public void setSecondaryAlternates(List<String> value) {
        impl.setSecondaryAlternates(value);
    }

    @Deprecated
    public Map<String, String> getAttributes() {
        return !impl.getFiles().isEmpty() ? impl.getFiles().get(0).getAttributes() : null;
    }

    public Map<String, String> getAllAttributes() {
        Map<String, String> attributes = new HashMap<>();
        impl.getFiles().stream().forEach(fileEntry ->
                attributes.putAll(fileEntry.getAttributes().entrySet().stream()
                        .collect(Collectors.toMap(entry -> fileEntry.getFileId() + "_" + entry.getKey(), Map.Entry::getValue))
                )
        );
        return Collections.unmodifiableMap(attributes);
    }

    @Deprecated
    public void setAttributes(Map<String, String> attributes) {
        if (impl.getFiles().isEmpty()) {
            impl.getFiles().add(new FileEntry("", null, attributes));
        } else {
            impl.getFiles().get(0).setAttributes(attributes);
        }
    }

    @Override
    public String toString() {
        return impl.toString();
    }

    @Override
    public int hashCode() {
        return impl.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StudyEntry) {
            return impl.equals(((StudyEntry) obj).getImpl());
        } else {
            return false;
        }
    }
}
