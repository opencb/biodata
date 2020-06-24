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

package org.opencb.biodata.models.clinical.qc.sample;

import java.util.Map;

@Deprecated
public class QcVariantStats {

    private int variantCount;
    private int passCount;
    private Map<String, Integer> chromosomeCount;
    private Map<String, Integer> typeCount;
    private Map<String, Integer> biotypeCount;
    private Map<String, Integer> consequenceTypeCount;
    private Map<String, Integer> genotypeCount;

    public QcVariantStats() {
    }

    public QcVariantStats(int variantCount, int passCount, Map<String, Integer> chromosomeCount,
                          Map<String, Integer> typeCount, Map<String, Integer> biotypeCount,
                          Map<String, Integer> consequenceTypeCount, Map<String, Integer> genotypeCount) {
        this.variantCount = variantCount;
        this.passCount = passCount;
        this.chromosomeCount = chromosomeCount;
        this.typeCount = typeCount;
        this.biotypeCount = biotypeCount;
        this.consequenceTypeCount = consequenceTypeCount;
        this.genotypeCount = genotypeCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QcVariantStats{");
        sb.append("variantCount=").append(variantCount);
        sb.append(", passCount=").append(passCount);
        sb.append(", chromosomeCount=").append(chromosomeCount);
        sb.append(", typeCount=").append(typeCount);
        sb.append(", biotypeCount=").append(biotypeCount);
        sb.append(", consequenceTypeCount=").append(consequenceTypeCount);
        sb.append(", genotypeCount=").append(genotypeCount);
        sb.append('}');
        return sb.toString();
    }

    public int getVariantCount() {
        return variantCount;
    }

    public QcVariantStats setVariantCount(int variantCount) {
        this.variantCount = variantCount;
        return this;
    }

    public int getPassCount() {
        return passCount;
    }

    public QcVariantStats setPassCount(int passCount) {
        this.passCount = passCount;
        return this;
    }

    public Map<String, Integer> getChromosomeCount() {
        return chromosomeCount;
    }

    public QcVariantStats setChromosomeCount(Map<String, Integer> chromosomeCount) {
        this.chromosomeCount = chromosomeCount;
        return this;
    }

    public Map<String, Integer> getTypeCount() {
        return typeCount;
    }

    public QcVariantStats setTypeCount(Map<String, Integer> typeCount) {
        this.typeCount = typeCount;
        return this;
    }

    public Map<String, Integer> getBiotypeCount() {
        return biotypeCount;
    }

    public QcVariantStats setBiotypeCount(Map<String, Integer> biotypeCount) {
        this.biotypeCount = biotypeCount;
        return this;
    }

    public Map<String, Integer> getConsequenceTypeCount() {
        return consequenceTypeCount;
    }

    public QcVariantStats setConsequenceTypeCount(Map<String, Integer> consequenceTypeCount) {
        this.consequenceTypeCount = consequenceTypeCount;
        return this;
    }

    public Map<String, Integer> getGenotypeCount() {
        return genotypeCount;
    }

    public QcVariantStats setGenotypeCount(Map<String, Integer> genotypeCount) {
        this.genotypeCount = genotypeCount;
        return this;
    }
}
