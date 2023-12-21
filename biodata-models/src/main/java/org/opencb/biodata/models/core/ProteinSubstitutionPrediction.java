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

import java.util.ArrayList;
import java.util.List;

public class ProteinSubstitutionPrediction {

    private String transcriptId;
    private String uniprotId;
    private int position;
    private String aaReference;
    private String source;
    private List<ProteinSubstitutionScore> scores;

    public ProteinSubstitutionPrediction() {
        this.scores = new ArrayList<>();
    }

    public ProteinSubstitutionPrediction(String transcriptId, String uniprotId, int position, String aaReference, String source,
                                         List<ProteinSubstitutionScore> scores) {
        this.transcriptId = transcriptId;
        this.uniprotId = uniprotId;
        this.position = position;
        this.aaReference = aaReference;
        this.source = source;
        this.scores = scores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProteinSubstitutionPrediction{");
        sb.append("transcriptId='").append(transcriptId).append('\'');
        sb.append(", uniprotId='").append(uniprotId).append('\'');
        sb.append(", position=").append(position);
        sb.append(", aaReference='").append(aaReference).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", scores=").append(scores);
        sb.append('}');
        return sb.toString();
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public ProteinSubstitutionPrediction setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
        return this;
    }

    public String getUniprotId() {
        return uniprotId;
    }

    public ProteinSubstitutionPrediction setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public ProteinSubstitutionPrediction setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getAaReference() {
        return aaReference;
    }

    public ProteinSubstitutionPrediction setAaReference(String aaReference) {
        this.aaReference = aaReference;
        return this;
    }

    public String getSource() {
        return source;
    }

    public ProteinSubstitutionPrediction setSource(String source) {
        this.source = source;
        return this;
    }

    public List<ProteinSubstitutionScore> getScores() {
        return scores;
    }

    public ProteinSubstitutionPrediction setScores(List<ProteinSubstitutionScore> scores) {
        this.scores = scores;
        return this;
    }
}
