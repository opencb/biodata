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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.avro.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.VcfHeader;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;

import java.util.*;


/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "samplesPosition", "type"})
public class VariantSource {

    private final VariantFileMetadata impl;
    private LinkedHashMap<String, Integer> samplesPosition;

    public enum Aggregation { NONE, BASIC, EVS, EXAC;
        public static boolean isAggregated(Aggregation agg) {return !NONE.equals(agg);}
    }

    VariantSource() {
        impl = new VariantFileMetadata();
        samplesPosition = null;
    }

    public VariantSource(VariantFileMetadata variantFileMetadata) {
        impl = variantFileMetadata;
        samplesPosition = null;
    }

    public VariantSource(String fileName, String fileId, String studyId, String studyName) {
        this(fileName, fileId, studyId, studyName, VariantStudy.StudyType.CASE_CONTROL, Aggregation.NONE);
    }

    public VariantSource(String fileName, String fileId, String studyId, String studyName, VariantStudy.StudyType type, Aggregation aggregation) {
        impl = new VariantFileMetadata(fileId, studyId, fileName, studyName, new LinkedList<>(),
                org.opencb.biodata.models.variant.avro.Aggregation.NONE, null, new HashMap<>(), null);
        samplesPosition = null;
    }

    public String getFileName() {
        return impl.getFileName();
    }

    public void setFileName(String fileName) {
        this.impl.setFileName(fileName);
    }

    public String getFileId() {
        return impl.getFileId();
    }

    public void setFileId(String fileId) {
        this.impl.setFileId(fileId);
    }

    public String getStudyId() {
        return impl.getStudyId();
    }

    public void setStudyId(String studyId) {
        this.impl.setStudyId(studyId);
    }

    public String getStudyName() {
        return impl.getStudyName();
    }

    public void setStudyName(String studyName) {
        this.impl.setStudyName(studyName);
    }

    public Aggregation getAggregation() {
        return impl.getAggregation() == null ? null
                : Aggregation.valueOf(impl.getAggregation().toString());
    }

    public void setAggregation(Aggregation aggregation) {
        impl.setAggregation(aggregation == null ? null
                : org.opencb.biodata.models.variant.avro.Aggregation.valueOf(aggregation.toString()));
    }

    public Map<String, Integer> getSamplesPosition() {
        if (samplesPosition == null) {
            updateSamplesPosition();
        }
        return Collections.unmodifiableMap(samplesPosition);
    }

    public void setSamplesPosition(Map<String, Integer> samplesPosition) {
        if (samplesPosition == null) {
            setSamples(null);
        } else {
            ArrayList<String> samples = new ArrayList<>(samplesPosition.size());
            for (int i = 0; i < samplesPosition.size(); i++) {
                samples.add(null);    //Populate empty array
            }
            for (Map.Entry<String, Integer> entry : samplesPosition.entrySet()) {
                samples.set(entry.getValue(), entry.getKey());
            }
            setSamples(samples);
        }
        updateSamplesPosition();
    }

    private synchronized void updateSamplesPosition() {
        if (samplesPosition == null) {
            List<String> samples = getSamples();
            if (samples == null) {
                samplesPosition = null;
            } else {
                LinkedHashMap<String, Integer> newSamplesPosition = new LinkedHashMap<>(samples.size());
                int idx = 0;
                for (String sample : samples) {
                    newSamplesPosition.put(sample, idx++);
                }
                samplesPosition = newSamplesPosition;
            }
        }
    }

    public List<String> getSamples() {
        return impl.getSamples() == null ? null : Collections.unmodifiableList(impl.getSamples());
    }

    public void setSamples(List<String> samples) {
        impl.setSamples(samples);
        samplesPosition = null;
    }

    public void addSamples(List<String> newSamples) {
        impl.getSamples().addAll(newSamples);
        samplesPosition = null;
    }

    @Deprecated
    public Pedigree getPedigree() {
        return null;
    }

    @Deprecated
    public void setPedigree(Pedigree pedigree) {
    }

    public Map<String, Object> getMetadata() {
        return impl.getMetadata();
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.impl.setMetadata(metadata);
    }

    public void addMetadata(String key, Object value) {
        this.getMetadata().put(key, value);
    }

    @Deprecated
    public VariantStudy.StudyType getType() {
        return null;
    }

    @Deprecated
    public void setType(VariantStudy.StudyType type) {
//        this.type = type;
    }

    public VariantGlobalStats getStats() {
        return impl.getStats() == null ? null : new VariantGlobalStats(impl.getStats());
    }

    public void setStats(VariantGlobalStats stats) {
        impl.setStats(stats == null ? null : stats.getImpl());
    }

    public VcfHeader getHeader() {
        return impl.getHeader();
    }

    public void setHeader(VcfHeader value) {
        impl.setHeader(value);
    }

    public VariantFileMetadata getImpl() {
        return impl;
    }

    @Override
    public String toString() {
        return "VariantStudy{" +
                "fileName='" + getFileName() + '\'' +
                ", fleId='" + getFileId() + '\'' +
                ", samples=" + getSamples() +
                ", metadata=" + getMetadata() +
//                ", stats=" + stats +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariantSource)) return false;

        VariantSource that = (VariantSource) o;

        return !(impl != null ? !impl.equals(that.impl) : that.impl != null);

    }

    @Override
    public int hashCode() {
        return impl != null ? impl.hashCode() : 0;
    }
}
