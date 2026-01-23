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

package org.opencb.biodata.models.core.pgs;

import java.util.List;

public class VariantPolygenicScore {

    private String chromosome;
    private int position;
    private String effectAllele;
    private String otherAllele;
    private List<PolygenicScore> polygenicScores;

    public VariantPolygenicScore() {
    }

    public VariantPolygenicScore(String chromosome, int position, String effectAllele, String otherAllele,
                                 List<PolygenicScore> polygenicScores) {
        this.chromosome = chromosome;
        this.position = position;
        this.effectAllele = effectAllele;
        this.otherAllele = otherAllele;
        this.polygenicScores = polygenicScores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariantPolygenicScore{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", effectAllele='").append(effectAllele).append('\'');
        sb.append(", otherAllele='").append(otherAllele).append('\'');
        sb.append(", polygenicScores=").append(polygenicScores);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public VariantPolygenicScore setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public VariantPolygenicScore setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getEffectAllele() {
        return effectAllele;
    }

    public VariantPolygenicScore setEffectAllele(String effectAllele) {
        this.effectAllele = effectAllele;
        return this;
    }

    public String getOtherAllele() {
        return otherAllele;
    }

    public VariantPolygenicScore setOtherAllele(String otherAllele) {
        this.otherAllele = otherAllele;
        return this;
    }

    public List<PolygenicScore> getPolygenicScores() {
        return polygenicScores;
    }

    public VariantPolygenicScore setPolygenicScores(List<PolygenicScore> polygenicScores) {
        this.polygenicScores = polygenicScores;
        return this;
    }
}
