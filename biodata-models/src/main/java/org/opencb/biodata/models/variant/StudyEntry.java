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
import org.opencb.biodata.models.variant.avro.*;
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
    private final AtomicReference<Map<String, Integer>> sampleDataKeysPosition = new AtomicReference<>();
//    private volatile Map<String, VariantStats> cohortStats = null;
    private volatile List<VariantStats> stats = null;
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

    public StudyEntry(String studyId, List<AlternateCoordinate> secondaryAlternates, List<String> format) {
        this.impl = new org.opencb.biodata.models.variant.avro.StudyEntry(studyId,
                new ArrayList<>(), null, format, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        setSecondaryAlternates(secondaryAlternates);
    }

    public org.opencb.biodata.models.variant.avro.StudyEntry getImpl() {
        return impl;
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
        if (getSamples() == null) {
            setSamples(new ArrayList<>(samplesPosition.size()));
        }
        if (getSamples().isEmpty()) {
            for (int size = samplesPosition.size(); size > 0; size--) {
                getSamples().add(null);
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

    public String getSampleDataKeysAsString() {
        return impl.getSampleDataKeys() == null ? null : String.join(":", impl.getSampleDataKeys());
    }

    /**
     * Do not modify this list
     * @return
     */
    public List<String> getSampleDataKeys() {
        return impl.getSampleDataKeys() == null? null : Collections.unmodifiableList(impl.getSampleDataKeys());
    }

    public StudyEntry setSampleDataKeys(List<String> value) {
        this.sampleDataKeysPosition.set(null);
        impl.setSampleDataKeys(value);
        return this;
    }

    public StudyEntry addSampleDataKey(String value) {
        Map<String, Integer> formatPositions = getSampleDataKeyPositions();
        if (formatPositions.containsKey(value)) {
            return this;
        } else {
            List<String> format = impl.getSampleDataKeys();
            if (format == null) {
                format = new ArrayList<>(1);
                format.add(value);
                impl.setSampleDataKeys(format);
            } else {
                actOnList(format, f -> f.add(value), impl::setSampleDataKeys);
            }
            formatPositions.put(value, formatPositions.size());
        }
        return this;
    }

    public Set<String> getSampleDataKeySet() {
        return getSampleDataKeyPositions().keySet();
    }

    public Integer getSampleDataKeyPosition(String key) {
        return getSampleDataKeyPositions().get(key);
    }

    public Map<String, Integer> getSampleDataKeyPositions() {
        if (Objects.isNull(this.sampleDataKeysPosition.get())) {
            Map<String, Integer> map = new HashMap<>();
            int pos = 0;
            if (getSampleDataKeys() != null) {
                for (String format : getSampleDataKeys()) {
                    map.put(format, pos++);
                }
            }
            this.sampleDataKeysPosition.compareAndSet(null, map);
        }
        return sampleDataKeysPosition.get();
    }

    public List<SampleEntry> getSamples() {
        return impl.getSamples();
    }

    public StudyEntry setSamples(List<SampleEntry> samples) {
        impl.setSamples(samples);
        return this;
    }

    public SampleEntry getSample(String sample) {
        requireSamplesPosition();
        if (samplesPosition.containsKey(sample)) {
            return getSamples().get(samplesPosition.get(sample));
        }
        return null;
    }

    public SampleEntry getSample(int samplePosition) {
        if (samplePosition >= 0 && samplePosition < impl.getSamples().size()) {
            return impl.getSamples().get(samplePosition);
        } else {
            return null;
        }
    }

    public List<String> getSampleData(String sample) {
        SampleEntry sampleEntry = getSample(sample);
        if (sampleEntry == null) {
            return null;
        } else {
            return sampleEntry.getData();
        }

    }

    public String getSampleData(String sample, String field) {
        SampleEntry sampleEntry = getSample(sample);
        if (sampleEntry != null) {
            Map<String, Integer> formatPositions = getSampleDataKeyPositions();
            if (formatPositions.containsKey(field)) {
                Integer formatIdx = formatPositions.get(field);
                return  formatIdx < sampleEntry.getData().size() ? sampleEntry.getData().get(formatIdx) : null;
            }
        }
        return null;
    }

    public List<String> getSampleData(int samplePosition) {
        SampleEntry sampleEntry = getSample(samplePosition);
        if (sampleEntry == null) {
            return null;
        } else {
            return sampleEntry.getData();
        }
    }

    public StudyEntry addSampleData(String sampleName, Map<String, String> sampleData) {
        if (getSampleDataKeys() == null) {
            setSampleDataKeys(new ArrayList<>(sampleData.keySet()));
        }
        List<String> sampleDataList = new ArrayList<>(getSampleDataKeys().size());
        for (String field : getSampleDataKeys()) {
            sampleDataList.add(sampleData.get(field));
        }
        if (sampleData.size() != sampleDataList.size()) {
            List<String> extraFields = sampleData.keySet().stream().filter(f -> getSampleDataKeys().contains(f)).collect(Collectors.toList());
            throw new IllegalArgumentException("Some sample data fields were not in the format field: " + extraFields);
        }
        addSampleData(sampleName, sampleDataList);
        return this;
    }

    public StudyEntry addSampleData(String sampleId, List<String> sampleDataList) {
        if (samplesPosition == null && impl.getSamples().isEmpty()) {
            samplesPosition = new LinkedHashMap<>();
        }
        SampleEntry sampleEntry = new SampleEntry(null, null, sampleDataList);
        if (samplesPosition != null) {
            if (samplesPosition.containsKey(sampleId)) {
                int position = samplesPosition.get(sampleId);
                addSampleData(position, sampleEntry);
            } else {
                int position = samplesPosition.size();
                samplesPosition.put(sampleId, position);
                actOnSamplesList((l) -> l.add(sampleEntry));
            }
        } else {
            actOnSamplesList((l) -> l.add(sampleEntry));
        }
        return this;
    }

    public StudyEntry addSampleData(int samplePosition, SampleEntry sampleEntry) {
        while (impl.getSamples().size() <= samplePosition) {
            actOnSamplesList((l) -> l.add(null));
        }
        actOnSamplesList((l) -> l.set(samplePosition, sampleEntry));
        return this;
    }

    /**
     * Acts on the SamplesDataList. If the action throws an UnsupportedOperationException, the list is copied
     * into a modifiable list (ArrayList) and the action is executed again.
     *
     * @param action Action to execute
     */
    private void actOnSamplesList(Consumer<List<SampleEntry>> action) {
        actOnList(impl.getSamples(), action, impl::setSamples);
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
        Integer formatIdx = getSampleDataKeyPositions().get(format);
        Integer samplePosition = getSamplesPosition().get(sampleName);
        return addSampleData(samplePosition, formatIdx, value, defaultValue);
    }

    public StudyEntry addSampleData(Integer samplePosition, Integer formatIdx, String value, String defaultValue) {
        if (formatIdx != null && samplePosition != null) {
            SampleEntry sampleEntry = getSample(samplePosition);
            if (sampleEntry == null) {
                sampleEntry = new SampleEntry(null, null, new ArrayList<>(getSampleDataKeys().size()));
                addSampleData(samplePosition, sampleEntry);
            }
            if (formatIdx < sampleEntry.getData().size()) {
                actOnList(sampleEntry.getData(), l -> l.set(formatIdx, value), sampleEntry::setData);
            } else {
                while (formatIdx > sampleEntry.getData().size()) {
                    actOnList(sampleEntry.getData(), l -> l.add(defaultValue), sampleEntry::setData);
                }
                actOnList(sampleEntry.getData(), l -> l.add(value), sampleEntry::setData);
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

    public List<IssueEntry> getIssues() {
        return impl.getIssues();
    }

    public StudyEntry setIssues(List<IssueEntry> issues) {
        impl.setIssues(issues);
        return this;
    }

    public List<VariantStats> getStats() {
        resetStatsList();
        return Collections.unmodifiableList(stats);
    }

    public void setStats(List<VariantStats> stats) {
        impl.setStats(new ArrayList<>(stats.size()));
        stats.forEach((v) -> impl.getStats().add(v.getImpl()));
        this.stats = stats;
    }

    public void addStats(VariantStats stats) {
        resetStatsList();
        impl.getStats().add(stats.getImpl());
        this.stats.add(stats);

    }

    public VariantStats getStats(String cohortId) {
        resetStatsList();
        for (VariantStats stats : stats) {
            if (stats.getCohortId().equals(cohortId)) {
                return stats;
            }
        }
        return null;
    }

    private void resetStatsList() {
        if (stats == null) {
            if (impl.getStats() == null) {
                impl.setStats(new ArrayList<>());
                stats = new ArrayList<>();
            } else {
                stats = new ArrayList<>(impl.getStats().size());
                for (org.opencb.biodata.models.variant.avro.VariantStats v : impl.getStats()) {
                    stats.add(new VariantStats(v));
                }
            }
        }
    }

    public void addFileData(String fileId, String key, String value) {
        getFile(fileId).getData().put(key, value);
    }

    public void addFileData(String fileId, Map<String, String> data) {
        getFile(fileId).getData().putAll(data);
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

    public FileEntry getFile(int fileIndex) {
        return impl.getFiles().get(fileIndex);
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
            impl.getFiles().add(new FileEntry(fileId, null, new HashMap<>()));
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
