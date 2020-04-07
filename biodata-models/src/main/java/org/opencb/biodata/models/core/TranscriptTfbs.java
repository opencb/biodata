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

package org.opencb.biodata.models.core;

import java.util.List;

public class TranscriptTfbs {

    /**
     * Ensembl motif feature ID, eg. ENSM00209489825
     */
    private String id;

    /**
     * Ensembl PFM binding matrix ID, eg. ENSPFM0571
     */
    private String pfmId;
    private String chromosome;
    private int start;
    private int end;
    private String strand;

    /**
     * At the moment always TF_binding_site
     */
    private String type;
    private String regulatoryId;  // ENSRXXX - get from API
    private List<String> transcriptionFactors;
    private int relativeStart;
    private int relativeEnd;
    private float score;

    @Deprecated
    private String tfName;
    @Deprecated
    private String pwm;


    public TranscriptTfbs() {
    }

    public TranscriptTfbs(String id, String pfmId, String type, List<String> transcriptionFactors, String chromosome, Integer start,
                          Integer end, Integer relativeStart, Integer relativeEnd, Float score) {
        this.id = id;
        this.pfmId = pfmId;
        this.type = type;
        this.transcriptionFactors = transcriptionFactors;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.relativeStart = relativeStart;
        this.relativeEnd = relativeEnd;
        this.score = score;
    }

    @Deprecated
    public TranscriptTfbs(String tfName, String pwm, String chromosome, Integer start, Integer end, String strand, Integer relativeStart,
            Integer relativeEnd, Float score) {
        super();
        this.tfName = tfName;
        this.pwm = pwm;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.relativeStart = relativeStart;
        this.relativeEnd = relativeEnd;
        this.score = score;
    }

    public TranscriptTfbs(String id, String pfmId, String chromosome, int start, int end, String strand, String type, String regulatoryId,
                          List<String> transcriptionFactors, int relativeStart, int relativeEnd, float score) {
        this.id = id;
        this.pfmId = pfmId;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.type = type;
        this.regulatoryId = regulatoryId;
        this.transcriptionFactors = transcriptionFactors;
        this.relativeStart = relativeStart;
        this.relativeEnd = relativeEnd;
        this.score = score;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranscriptTfbs{");
        sb.append("id='").append(id).append('\'');
        sb.append(", pfmId='").append(pfmId).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", strand='").append(strand).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", regulatoryId='").append(regulatoryId).append('\'');
        sb.append(", transcriptionFactors=").append(transcriptionFactors);
        sb.append(", relativeStart=").append(relativeStart);
        sb.append(", relativeEnd=").append(relativeEnd);
        sb.append(", score=").append(score);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public TranscriptTfbs setId(String id) {
        this.id = id;
        return this;
    }

    public String getPfmId() {
        return pfmId;
    }

    public TranscriptTfbs setPfmId(String pfmId) {
        this.pfmId = pfmId;
        return this;
    }

    public String getType() {
        return type;
    }

    public TranscriptTfbs setType(String type) {
        this.type = type;
        return this;
    }

    public String getRegulatoryId() {
        return regulatoryId;
    }

    public TranscriptTfbs setRegulatoryId(String regulatoryId) {
        this.regulatoryId = regulatoryId;
        return this;
    }

    public List<String> getTranscriptionFactors() {
        return transcriptionFactors;
    }

    public TranscriptTfbs setTranscriptionFactors(List<String> transcriptionFactors) {
        this.transcriptionFactors = transcriptionFactors;
        return this;
    }

    @Deprecated
    public String getTfName() {
        return tfName;
    }

    @Deprecated
    public void setTfName(String tfName) {
        this.tfName = tfName;
    }

    @Deprecated
    public String getPwm() {
        return pwm;
    }

    @Deprecated
    public void setPwm(String pwm) {
        this.pwm = pwm;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public int getRelativeStart() {
        return relativeStart;
    }

    public void setRelativeStart(int relativeStart) {
        this.relativeStart = relativeStart;
    }

    public int getRelativeEnd() {
        return relativeEnd;
    }

    public void setRelativeEnd(int relativeEnd) {
        this.relativeEnd = relativeEnd;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

}
