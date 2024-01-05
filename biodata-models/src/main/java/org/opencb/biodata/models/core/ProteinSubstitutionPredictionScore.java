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

public class ProteinSubstitutionPredictionScore {

    private String alternate;
    private String aaAlternate;
    private double score;
    private String effect;

    public ProteinSubstitutionPredictionScore() {
    }

    public ProteinSubstitutionPredictionScore(String alternate, String aaAlternate, double score, String effect) {
        this.alternate = alternate;
        this.aaAlternate = aaAlternate;
        this.score = score;
        this.effect = effect;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProteinSubstitutionPredictionScore{");
        sb.append("alternate='").append(alternate).append('\'');
        sb.append(", aaAlternate='").append(aaAlternate).append('\'');
        sb.append(", score=").append(score);
        sb.append(", effect='").append(effect).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAlternate() {
        return alternate;
    }

    public ProteinSubstitutionPredictionScore setAlternate(String alternate) {
        this.alternate = alternate;
        return this;
    }

    public String getAaAlternate() {
        return aaAlternate;
    }

    public ProteinSubstitutionPredictionScore setAaAlternate(String aaAlternate) {
        this.aaAlternate = aaAlternate;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ProteinSubstitutionPredictionScore setScore(double score) {
        this.score = score;
        return this;
    }

    public String getEffect() {
        return effect;
    }

    public ProteinSubstitutionPredictionScore setEffect(String effect) {
        this.effect = effect;
        return this;
    }
}
