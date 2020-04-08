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

package org.opencb.biodata.models.variant.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.variant.Genotype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl"})
public class VariantStats {

    private final org.opencb.biodata.models.variant.avro.VariantStats impl;

    public VariantStats(String cohortId) {
        this();
        impl.setCohortId(cohortId);
    }

    public VariantStats() {
        this(-1f, -1f, null, null, -1, -1);
    }

    public VariantStats(org.opencb.biodata.models.variant.avro.VariantStats other) {
        impl = other;
    }

    public VariantStats(float maf, float mgf, String mafAllele, String mgfGenotype,
                        int missingAlleleCount, int missingGenotypeCount) {
        impl = new org.opencb.biodata.models.variant.avro.VariantStats("", -1, -1, -1, -1F, -1F,
                missingAlleleCount, missingGenotypeCount,
                new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), -1F,
                maf, mgf, mafAllele, mgfGenotype);
    }

    public org.opencb.biodata.models.variant.avro.VariantStats getImpl() {
        return impl;
    }

    public VariantStats setCohortId(String cohortId) {
        impl.setCohortId(cohortId);
        return this;
    }

    public String getCohortId() {
        return impl.getCohortId();
    }

    public Integer getAlleleCount() {
        return impl.getAlleleCount();
    }

    public void setAlleleCount(Integer value) {
        impl.setAlleleCount(value);
    }

    public Integer getRefAlleleCount() {
        return impl.getRefAlleleCount();
    }

    public void setRefAlleleCount(Integer value) {
        impl.setRefAlleleCount(value);
    }

    public Integer getAltAlleleCount() {
        return impl.getAltAlleleCount();
    }

    public void setAltAlleleCount(Integer value) {
        impl.setAltAlleleCount(value);
    }

    public Map<Genotype, Integer> getGenotypeCount() {
        return impl.getGenotypeCount();
    }

    public void setGenotypeCount(Map<Genotype, Integer> value) {
        impl.setGenotypeCount(value);
    }

    public Map<Genotype, Float> getGenotypeFreq() {
        return impl.getGenotypeFreq();
    }

    public void setGenotypeFreq(Map<Genotype, Float> value) {
        impl.setGenotypeFreq(value);
    }

    public Integer getMissingAlleleCount() {
        return impl.getMissingAlleleCount();
    }

    public void setMissingAlleleCount(Integer value) {
        impl.setMissingAlleleCount(value);
    }

    public Integer getMissingGenotypeCount() {
        return impl.getMissingGenotypeCount();
    }

    public void setMissingGenotypeCount(Integer value) {
        impl.setMissingGenotypeCount(value);
    }

    public Float getRefAlleleFreq() {
        return impl.getRefAlleleFreq();
    }

    public void setRefAlleleFreq(Float value) {
        impl.setRefAlleleFreq(value);
    }

    public Float getAltAlleleFreq() {
        return impl.getAltAlleleFreq();
    }

    public void setAltAlleleFreq(Float value) {
        impl.setAltAlleleFreq(value);
    }

    public Float getMaf() {
        return impl.getMaf();
    }

    public void setMaf(Float value) {
        impl.setMaf(value);
    }

    public Float getMgf() {
        return impl.getMgf();
    }

    public void setMgf(Float value) {
        impl.setMgf(value);
    }

    public String getMafAllele() {
        return impl.getMafAllele();
    }

    public void setMafAllele(String value) {
        impl.setMafAllele(value);
    }

    public String getMgfGenotype() {
        return impl.getMgfGenotype();
    }

    public void setMgfGenotype(String value) {
        impl.setMgfGenotype(value);
    }

    public VariantStats addGenotype(Genotype g) {
        this.addGenotype(g, 1);
        return this;
    }

    public VariantStats addGenotype(Genotype g, int addedCount) {
        addGenotype(g, addedCount, true);
        return this;
    }

    public VariantStats addGenotype(Genotype g, int addedCount, boolean normalize) {
        if (normalize) {
            g = normalizeGenotypeAlleles(g);
        }
        getGenotypeCount().merge(g, addedCount, Integer::sum);
        return this;
    }

    public Map<String, Integer> getFilterCount() {
        return impl.getFilterCount();
    }

    public void setFilterCount(Map<String, Integer> value) {
        this.impl.setFilterCount(value);
    }

    public Map<String, Float> getFilterFreq() {
        return impl.getFilterFreq();
    }

    public void setFilterFreq(Map<String, Float> value) {
        this.impl.setFilterFreq(value);
    }

    public Float getQualityAvg() {
        return impl.getQualityAvg();
    }

    public void setQualityAvg(Float value) {
        this.impl.setQualityAvg(value);
    }

    private Genotype normalizeGenotypeAlleles(Genotype g) {
        // Get alleles sorted in ascending order
        int[] sortedAlleles = g.getNormalizedAllelesIdx();

        if (Arrays.equals(sortedAlleles, g.getAllelesIdx())) {
            // If the alleles do not change, no need to do anything
            return g;
        } else {
            // If the alleles have changed, a new genotype must be build
            StringBuilder joinedAlleles = new StringBuilder();
            joinedAlleles.append(sortedAlleles[0]);
            char separator = g.isPhased() ? '|' : '/';
            for (int i = 1; i < sortedAlleles.length; i++) {
                joinedAlleles.append(separator).append(sortedAlleles[i]);
            }
            return new Genotype(joinedAlleles.toString(), g.getReference(), g.getAlternates());
        }
    }

    public static boolean isTransition(String refAllele, String altAllele) {
        switch (refAllele.toUpperCase()) {
            case "C":
                return altAllele.equalsIgnoreCase("T");
            case "T":
                return altAllele.equalsIgnoreCase("C");
            case "A":
                return altAllele.equalsIgnoreCase("G");
            case "G":
                return altAllele.equalsIgnoreCase("A");
            default:
                return false;
        }
    }

    public static boolean isTransversion(String refAllele, String altAllele) {
        switch (refAllele.toUpperCase()) {
            case "C":
                return altAllele.equalsIgnoreCase("G") || altAllele.equalsIgnoreCase("A");
            case "T":
                return altAllele.equalsIgnoreCase("G") || altAllele.equalsIgnoreCase("A");
            case "A":
                return altAllele.equalsIgnoreCase("T") || altAllele.equalsIgnoreCase("C");
            case "G":
                return altAllele.equalsIgnoreCase("T") || altAllele.equalsIgnoreCase("C");
            default:
                return false;
        }
    }


    @Override
    public String toString() {
        return impl.toString();
    }

    @Override
    public int hashCode() {
        return impl.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariantStats) {
            return impl.equals(((VariantStats) obj).getImpl());
        } else {
            return false;
        }
    }

}
