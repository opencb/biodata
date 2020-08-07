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
import java.util.Objects;

public class MissenseVariantFunctionalScore {

    private String chromosome;
    private int position;
    private String reference;
    private String source;
    private List<TranscriptMissenseVariantFunctionalScore> scores;

    public MissenseVariantFunctionalScore() {
    }

    public MissenseVariantFunctionalScore(String chromosome, int position, String reference, String source,
                                          List<TranscriptMissenseVariantFunctionalScore> scores) {
        this.chromosome = chromosome;
        this.position = position;
        this.reference = reference;
        this.source = source;
        this.scores = scores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MissenseVariantFunctionalScore{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", reference='").append(reference).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", scores=").append(scores);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public MissenseVariantFunctionalScore setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public MissenseVariantFunctionalScore setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public MissenseVariantFunctionalScore setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getSource() {
        return source;
    }

    public MissenseVariantFunctionalScore setSource(String source) {
        this.source = source;
        return this;
    }

    public List<TranscriptMissenseVariantFunctionalScore> getScores() {
        return scores;
    }

    public MissenseVariantFunctionalScore setScores(List<TranscriptMissenseVariantFunctionalScore> scores) {
        this.scores = scores;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MissenseVariantFunctionalScore)) return false;
        MissenseVariantFunctionalScore that = (MissenseVariantFunctionalScore) o;
        return getPosition() == that.getPosition() &&
                Objects.equals(getChromosome(), that.getChromosome()) &&
                Objects.equals(getReference(), that.getReference()) &&
                Objects.equals(getSource(), that.getSource()) &&
                Objects.equals(getScores(), that.getScores());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChromosome(), getPosition(), getReference(), getSource(), getScores());
    }
}
