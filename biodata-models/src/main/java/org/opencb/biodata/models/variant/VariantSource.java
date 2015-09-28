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

import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;

import java.util.*;


/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantSource {

    private String fileName;
    private String fileId;

    private String studyId;
    private String studyName;

    public enum Aggregation { NONE, BASIC, EVS, EXAC;
        public static boolean isAggregated(Aggregation agg) {return !NONE.equals(agg);}
    }
    private Aggregation aggregation;

    private Map<String, Integer> samplesPosition;

    private Pedigree pedigree; // TODO Decide something about this field

    private Map<String, Object> metadata;

    private VariantStudy.StudyType type;
    
    private VariantGlobalStats stats;

    VariantSource() {
    }

    public VariantSource(String fileName, String fileId, String studyId, String studyName) {
        this(fileName, fileId, studyId, studyName, VariantStudy.StudyType.CASE_CONTROL, Aggregation.NONE);
    }

    public VariantSource(String fileName, String fileId, String studyId, String studyName, VariantStudy.StudyType type, Aggregation aggregation) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.studyId = studyId;
        this.studyName = studyName;
        this.aggregation = aggregation;
        this.samplesPosition = new LinkedHashMap<>();
        this.metadata = new HashMap<>();
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public Aggregation getAggregation() {
        return aggregation;
    }

    public void setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
    }

    public Map<String, Integer> getSamplesPosition() {
        return samplesPosition;
    }

    public void setSamplesPosition(Map<String, Integer> samplesPosition) {
        this.samplesPosition = samplesPosition;
    }

    public List<String> getSamples() {
        return new ArrayList(samplesPosition.keySet());
    }

    public void setSamples(List<String> newSamples) {
        int index = samplesPosition.size();
        for (String s : newSamples) {
            samplesPosition.put(s, index++);
        }
    }

    public Pedigree getPedigree() {
        return pedigree;
    }

    public void setPedigree(Pedigree pedigree) {
        this.pedigree = pedigree;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public VariantStudy.StudyType getType() {
        return type;
    }

    public void setType(VariantStudy.StudyType type) {
        this.type = type;
    }

    public VariantGlobalStats getStats() {
        return stats;
    }

    public void setStats(VariantGlobalStats stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "VariantStudy{" +
                "name='" + fileName + '\'' +
                ", alias='" + fileId + '\'' +
                ", samples=" + samplesPosition +
                ", metadata=" + metadata +
                ", stats=" + stats +
                '}';
    }

    

}
