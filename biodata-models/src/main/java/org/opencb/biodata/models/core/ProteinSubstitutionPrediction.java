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

    private String chromosome;
    private int position;
    private String reference;
    private String transcriptId;
    private String uniprotId;
    private int aaPosition;
    private String aaReference;
    private String source;
    private String version;
    private List<ProteinSubstitutionPredictionScore> scores;

    public ProteinSubstitutionPrediction() {
        this.scores = new ArrayList<>();
    }

    public ProteinSubstitutionPrediction(String chromosome, int position, String reference, String transcriptId, String uniprotId,
                                         int aaPosition, String aaReference, String source, String version,
                                         List<ProteinSubstitutionPredictionScore> scores) {
        this.chromosome = chromosome;
        this.position = position;
        this.reference = reference;
        this.transcriptId = transcriptId;
        this.uniprotId = uniprotId;
        this.aaPosition = aaPosition;
        this.aaReference = aaReference;
        this.source = source;
        this.version = version;
        this.scores = scores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProteinSubstitutionPrediction{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", reference='").append(reference).append('\'');
        sb.append(", transcriptId='").append(transcriptId).append('\'');
        sb.append(", uniprotId='").append(uniprotId).append('\'');
        sb.append(", aaPosition=").append(aaPosition);
        sb.append(", aaReference='").append(aaReference).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", scores=").append(scores);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public ProteinSubstitutionPrediction setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public ProteinSubstitutionPrediction setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public ProteinSubstitutionPrediction setReference(String reference) {
        this.reference = reference;
        return this;
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

    public int getAaPosition() {
        return aaPosition;
    }

    public ProteinSubstitutionPrediction setAaPosition(int aaPosition) {
        this.aaPosition = aaPosition;
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

    public String getVersion() {
        return version;
    }

    public ProteinSubstitutionPrediction setVersion(String version) {
        this.version = version;
        return this;
    }

    public List<ProteinSubstitutionPredictionScore> getScores() {
        return scores;
    }

    public ProteinSubstitutionPrediction setScores(List<ProteinSubstitutionPredictionScore> scores) {
        this.scores = scores;
        return this;
    }
}
