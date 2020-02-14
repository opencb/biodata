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

package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantScore;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** 
 * Entry that associates a variant and a file in a variant archive. It contains 
 * information related to samples, statistics and specifics of the file format.
 * 
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "samplesDataAsMap", "samplesPosition", "samplesName", "orderedSamplesName", "formatAsString",
        "formatPositions", "fileId", "attributes", "allAttributes", "cohortStats", "secondaryAlternatesAlleles"})
public class StudyEntry implements Serializable {

    private volatile LinkedHashMap<String, Integer> samplesPosition = null;
    private final AtomicReference<Map<String, Integer>> formatPosition = new AtomicReference<>();
    private volatile Map<String, VariantStats> cohortStats = null;
    private final org.opencb.biodata.models.variant.avro.StudyEntry impl;

    public static final String DEFAULT_COHORT = "ALL";
    public static final String QUAL = "QUAL";
    public static final String FILTER = "FILTER";
    public static final String VCF_ID = "VCF_ID";
    @Deprecated
    public static final String SRC = "src";

    public StudyEntry() {
        this(null, null);
    }
    
    public StudyEntry(org.opencb.biodata.models.variant.avro.StudyEntry other) {
        impl = other;
    }

    public StudyEntry(String studyId) {
        this(studyId, new ArrayList<>(), null);
    }

    public StudyEntry(String fileId, String studyId) {
        this(studyId, new ArrayList<>(), null);
        if (fileId != null) {
            setFileId(fileId);
        }
    }

    /**
     * @deprecated Use {@link #StudyEntry(String, List, List)}
     */
    @Deprecated
    public StudyEntry(String fileId, String studyId, String[] secondaryAlternates, String format) {
        this(fileId, studyId, secondaryAlternates, format == null ? null : Arrays.asList(format.split(":")));
    }

    /**
     * @deprecated Use {@link #StudyEntry(String, List, List)}
     */
    @Deprecated
    public StudyEntry(String fileId, String studyId, String[] secondaryAlternates, List<String> format) {
        this(fileId, studyId, Arrays.asList(secondaryAlternates), format);
    }

    /**
     * @deprecated Use {@link #StudyEntry(String, List, List)}
     */
    @Deprecated
    public StudyEntry(String fileId, String studyId, List<String> secondaryAlternates, List<String> format) {
        this.impl = new org.opencb.biodata.models.variant.avro.StudyEntry(studyId,
                new ArrayList<>(), null, format, new ArrayList<>(), new LinkedHashMap<>(), new ArrayList<>());
        setSecondaryAlternatesAlleles(secondaryAlternates);
        if (fileId != null) {
            setFileId(fileId);
        }
    }

    public StudyEntry(String studyId, List<AlternateCoordinate> secondaryAlternates, List<String> format) {
        this.impl = new org.opencb.biodata.models.variant.avro.StudyEntry(studyId,
                new ArrayList<>(), null, format, new ArrayList<>(), new LinkedHashMap<>(), new ArrayList<>());
        setSecondaryAlternates(secondaryAlternates);
    }

    public LinkedHashMap<String, Integer> getSamplesPosition() {
        return samplesPosition;
    }

    public void setSamplesPosition(Map<String, Integer> samplesPosition) {
        setSamplesPosition(samplesPosition, true);
    }

    public void setSortedSamplesPosition(LinkedHashMap<String, Integer> samplesPosition) {
        setSamplesPosition(samplesPosition, false);
    }

    protected void setSamplesPosition(Map<String, Integer> samplesPosition, boolean checkSorted) {
        if (samplesPosition == null) {
            this.samplesPosition = null;
            return;
        }
        if (samplesPosition instanceof LinkedHashMap) {
            if (!checkSorted || isSamplesPositionMapSorted((LinkedHashMap<String, Integer>) samplesPosition)) {
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

    public String getFormatAsString() {
        return impl.getFormat() == null ? null : String.join(":", impl.getFormat());
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

    public StudyEntry setFormat(List<String> value) {
        this.formatPosition.set(null);
        impl.setFormat(value);
        return this;
    }

    public StudyEntry addFormat(String value) {
        Map<String, Integer> formatPositions = getFormatPositions();
        if (formatPositions.containsKey(value)) {
            return this;
        } else {
            List<String> format = impl.getFormat();
            if (format == null) {
                format = new ArrayList<>(1);
                format.add(value);
                impl.setFormat(format);
            } else {
                actOnList(format, f -> f.add(value), impl::setFormat);
            }
            formatPositions.put(value, formatPositions.size());
        }
        return this;
    }

    public Map<String, Integer> getFormatPositions() {
        if (Objects.isNull(this.formatPosition.get())) {
            Map<String, Integer> map = new HashMap<>();
            int pos = 0;
            if (getFormat() != null) {
                for (String format : getFormat()) {
                    map.put(format, pos++);
                }
            }
            this.formatPosition.compareAndSet(null, map);
        }
        return formatPosition.get();
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
            samplesDataMap.put(entry.getKey(), getSampleDataAsMap(entry.getKey()));
        }

        return Collections.unmodifiableMap(samplesDataMap);
    }

    public String getSampleData(String sampleName, String field) {
        requireSamplesPosition();
        if (samplesPosition.containsKey(sampleName)) {
            Map<String, Integer> formatPositions = getFormatPositions();
            if (formatPositions.containsKey(field)) {
                List<String> sampleData = impl.getSamplesData().get(samplesPosition.get(sampleName));
                Integer formatIdx = formatPositions.get(field);
                return  formatIdx < sampleData.size() ? sampleData.get(formatIdx) : null;
            }
        }
        return null;
    }

    public List<String> getSampleData(String sampleName) {
        requireSamplesPosition();
        Integer samplePosition = samplesPosition.get(sampleName);
        if (samplePosition == null) {
            return null;
        } else {
            return getSampleData(samplePosition);
        }

    }

    public List<String> getSampleData(int samplePosition) {
        if (samplePosition >= 0 && samplePosition < impl.getSamplesData().size()) {
            return impl.getSamplesData().get(samplePosition);
        } else {
            return null;
        }
    }

    public Map<String, String> getSampleDataAsMap(String sampleName) {
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

    public StudyEntry addSampleData(String sampleName, Map<String, String> sampleData) {
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
        return this;
    }

    public StudyEntry addSampleData(String sampleName, List<String> sampleDataList) {
        if (samplesPosition == null && impl.getSamplesData().isEmpty()) {
            samplesPosition = new LinkedHashMap<>();
        }
        if (samplesPosition != null) {
            if (samplesPosition.containsKey(sampleName)) {
                int position = samplesPosition.get(sampleName);
                addSampleData(position, sampleDataList);
            } else {
                int position = samplesPosition.size();
                samplesPosition.put(sampleName, position);
                actOnSamplesDataList((l) -> l.add(sampleDataList));
            }
        } else {
            actOnSamplesDataList((l) -> l.add(sampleDataList));
        }
        return this;
    }

    public StudyEntry addSampleData(int samplePosition, List<String> sampleDataList) {
        while (impl.getSamplesData().size() <= samplePosition) {
            actOnSamplesDataList((l) -> l.add(null));
        }
        actOnSamplesDataList((l) -> l.set(samplePosition, sampleDataList));
        return this;
    }

    /**
     * Acts on the SamplesDataList. If the action throws an UnsupportedOperationException, the list is copied
     * into a modifiable list (ArrayList) and the action is executed again.
     *
     * @param action Action to execute
     */
    private void actOnSamplesDataList(Consumer<List<List<String>>> action) {
        actOnList(impl.getSamplesData(), action, impl::setSamplesData);
    }

    private <T> List<T> actOnList(List<T> list, Consumer<List<T>> action, Consumer<List<T>> update) {
        try {
            action.accept(list);
        } catch (UnsupportedOperationException e) {
            list = new ArrayList<>(list);
            action.accept(list);
            if (update != null) {
                update.accept(list);
            }
        }
        return list;
    }

    public StudyEntry addSampleData(String sampleName, String format, String value) {
        return addSampleData(sampleName, format, value, null);
    }

    public StudyEntry addSampleData(String sampleName, String format, String value, String defaultValue) {
        requireSamplesPosition();
        Integer formatIdx = getFormatPositions().get(format);
        Integer samplePosition = getSamplesPosition().get(sampleName);
        return addSampleData(samplePosition, formatIdx, value, defaultValue);
    }

    public StudyEntry addSampleData(Integer samplePosition, Integer formatIdx, String value, String defaultValue) {
        Consumer<List<String>> update = sampleData -> getSamplesData().set(samplePosition, sampleData);

        if (formatIdx != null && samplePosition != null) {
            List<String> sampleData = getSamplesData().get(samplePosition);
            if (sampleData == null) {
                sampleData = new ArrayList<>(getFormat().size());
                getSamplesData().set(samplePosition, sampleData);
            }
            if (formatIdx < sampleData.size()) {
                actOnList(sampleData, l -> l.set(formatIdx, value), update);
            } else {
                while (formatIdx > sampleData.size()) {
                    sampleData = actOnList(sampleData, l -> l.add(defaultValue), update);
                }
                actOnList(sampleData, l -> l.add(value), update);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
        return this;
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
        Map<String, String> attributes = getAttributes();
        return attributes == null ? null : attributes.get(key);
    }

    @Deprecated
    public void addAttribute(String key, String value) {
        getAttributes().put(key, value);
    }

    public void addAttribute(String fileId, String key, String value) {
        getFile(fileId).getAttributes().put(key, value);
    }

    public void addAttributes(String fileId, Map<String, String> attributes) {
        getFile(fileId).getAttributes().putAll(attributes);
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

    public StudyEntry setStudyId(String value) {
        impl.setStudyId(value);
        return this;
    }

    public List<FileEntry> getFiles() {
        return impl.getFiles();
    }

    public StudyEntry setFiles(List<FileEntry> files) {
        impl.setFiles(files);
        return this;
    }

    public FileEntry getFile(String fileId) {
        for (FileEntry fileEntry : impl.getFiles()) {
            if (fileEntry.getFileId().equals(fileId)) {
                return fileEntry;
            }
        }
        return null;
    }

    public String getFileId() {
        return !impl.getFiles().isEmpty() ? impl.getFiles().get(0).getFileId() : null;
    }

    public void setFileId(String fileId) {
        if (impl.getFiles().isEmpty()) {
            impl.getFiles().add(new FileEntry(fileId, "", new HashMap<>()));
        } else {
            impl.getFiles().get(0).setFileId(fileId);
        }
    }

    /**
     * @deprecated Use {@link #getSecondaryAlternates()}
     */
    @Deprecated
    public List<String> getSecondaryAlternatesAlleles() {
        return impl.getSecondaryAlternates() == null
                ? null
                : Collections.unmodifiableList(impl.getSecondaryAlternates().stream()
                .map(AlternateCoordinate::getAlternate).collect(Collectors.toList()));
    }

    /**
     * @deprecated Use {@link #setSecondaryAlternates(List)}
     */
    @Deprecated
    public void setSecondaryAlternatesAlleles(List<String> value) {
        List<AlternateCoordinate> secondaryAlternatesMap = null;
        if (value != null) {
            secondaryAlternatesMap = new ArrayList<>(value.size());
            for (String secondaryAlternate : value) {
                secondaryAlternatesMap.add(new AlternateCoordinate(null, null, null, null, secondaryAlternate, VariantType.SNV));
            }
        }
        impl.setSecondaryAlternates(secondaryAlternatesMap);
    }

    public List<AlternateCoordinate> getSecondaryAlternates() {
        return impl.getSecondaryAlternates();
    }

    public void setSecondaryAlternates(List<AlternateCoordinate> value) {
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

    public List<VariantScore> getScores() {
        return impl.getScores();
    }

    public StudyEntry setScores(List<VariantScore> scores) {
        impl.setScores(scores);
        return this;
    }

    public StudyEntry addScore(VariantScore score) {
        List<VariantScore> scores = impl.getScores();
        if (scores == null) {
            scores = new LinkedList<>();
            setScores(scores);
        }
        scores.add(score);
        return this;
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
