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

public class PolygenicScore {

    private String id;
    private double effectWeight;
    private double alleleFrequencyEffect;
    private double Or;
    private String locusName;

    public PolygenicScore() {
    }

    public PolygenicScore(String id, double effectWeight, double alleleFrequencyEffect, double or, String locusName) {
        this.id = id;
        this.effectWeight = effectWeight;
        this.alleleFrequencyEffect = alleleFrequencyEffect;
        Or = or;
        this.locusName = locusName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolygenicScore{");
        sb.append("id='").append(id).append('\'');
        sb.append(", effectWeight=").append(effectWeight);
        sb.append(", alleleFrequencyEffect=").append(alleleFrequencyEffect);
        sb.append(", Or=").append(Or);
        sb.append(", locusName='").append(locusName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PolygenicScore setId(String id) {
        this.id = id;
        return this;
    }

    public double getEffectWeight() {
        return effectWeight;
    }

    public PolygenicScore setEffectWeight(double effectWeight) {
        this.effectWeight = effectWeight;
        return this;
    }

    public double getAlleleFrequencyEffect() {
        return alleleFrequencyEffect;
    }

    public PolygenicScore setAlleleFrequencyEffect(double alleleFrequencyEffect) {
        this.alleleFrequencyEffect = alleleFrequencyEffect;
        return this;
    }

    public double getOr() {
        return Or;
    }

    public PolygenicScore setOr(double or) {
        Or = or;
        return this;
    }

    public String getLocusName() {
        return locusName;
    }

    public PolygenicScore setLocusName(String locusName) {
        this.locusName = locusName;
        return this;
    }
}
