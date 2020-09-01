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

import java.util.Objects;

public class TranscriptMissenseVariantFunctionalScore {

    private String transcriptId;
    private String alternate;
    private String aaReference;
    private String aaAlternate;
    private double score;

    public TranscriptMissenseVariantFunctionalScore() {
    }

    public TranscriptMissenseVariantFunctionalScore(String transcriptId, String alternate, String aaReference, String aaAlternate, double score) {
        this.transcriptId = transcriptId;
        this.alternate = alternate;
        this.aaReference = aaReference;
        this.aaAlternate = aaAlternate;
        this.score = score;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MissensePredictedScore{");
        sb.append("transcriptId='").append(transcriptId).append('\'');
        sb.append(", alternate='").append(alternate).append('\'');
        sb.append(", aaReference='").append(aaReference).append('\'');
        sb.append(", aaAlternate='").append(aaAlternate).append('\'');
        sb.append(", score=").append(score);
        sb.append('}');
        return sb.toString();
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public TranscriptMissenseVariantFunctionalScore setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
        return this;
    }

    public String getAlternate() {
        return alternate;
    }

    public TranscriptMissenseVariantFunctionalScore setAlternate(String alternate) {
        this.alternate = alternate;
        return this;
    }

    public String getAaReference() {
        return aaReference;
    }

    public TranscriptMissenseVariantFunctionalScore setAaReference(String aaReference) {
        this.aaReference = aaReference;
        return this;
    }

    public String getAaAlternate() {
        return aaAlternate;
    }

    public TranscriptMissenseVariantFunctionalScore setAaAlternate(String aaAlternate) {
        this.aaAlternate = aaAlternate;
        return this;
    }

    public double getScore() {
        return score;
    }

    public TranscriptMissenseVariantFunctionalScore setScore(double score) {
        this.score = score;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranscriptMissenseVariantFunctionalScore)) return false;
        TranscriptMissenseVariantFunctionalScore that = (TranscriptMissenseVariantFunctionalScore) o;
        return Double.compare(that.getScore(), getScore()) == 0 &&
                Objects.equals(getTranscriptId(), that.getTranscriptId()) &&
                Objects.equals(getAlternate(), that.getAlternate()) &&
                Objects.equals(getAaReference(), that.getAaReference()) &&
                Objects.equals(getAaAlternate(), that.getAaAlternate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTranscriptId(), getAlternate(), getAaReference(), getAaAlternate(), getScore());
    }
}
