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

public class ProteinSubstitutionScore {

    private String aaAlternate;
    private double score;
    private String effect;

    public ProteinSubstitutionScore() {
    }

    public ProteinSubstitutionScore(String aaAlternate, double score, String effect) {
        this.aaAlternate = aaAlternate;
        this.score = score;
        this.effect = effect;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProteinSubstitutionScore{");
        sb.append("aaAlternate='").append(aaAlternate).append('\'');
        sb.append(", score=").append(score);
        sb.append(", effect='").append(effect).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAaAlternate() {
        return aaAlternate;
    }

    public ProteinSubstitutionScore setAaAlternate(String aaAlternate) {
        this.aaAlternate = aaAlternate;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ProteinSubstitutionScore setScore(double score) {
        this.score = score;
        return this;
    }

    public String getEffect() {
        return effect;
    }

    public ProteinSubstitutionScore setEffect(String effect) {
        this.effect = effect;
        return this;
    }
}
