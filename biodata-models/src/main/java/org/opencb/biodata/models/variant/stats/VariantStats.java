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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.avro.VariantHardyWeinbergStats;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 *         <p>
 *         TODO Mendelian errors must be calculated
 */
@JsonIgnoreProperties({"impl", "transversion", "transition"})
public class VariantStats {

    private final org.opencb.biodata.models.variant.avro.VariantStats impl;

    public VariantStats() {
        this(null, -1, null, null, VariantType.SNV, -1, -1, null, null, -1, -1, -1, -1, -1, -1, -1);
    }

    public VariantStats(org.opencb.biodata.models.variant.avro.VariantStats other) {
        impl = other;
    }

    public VariantStats(Variant variant) {
        this(null, -1,
                variant != null ? variant.getReference() : null,
                variant != null ? variant.getAlternate() : null,
                variant != null ? variant.getType() : VariantType.SNV,
                -1, -1, null, null, -1, -1, -1, -1, -1, -1, -1);
    }

    public VariantStats(String referenceAllele, String alternateAllele, VariantType type) {
        this(null, -1, referenceAllele, alternateAllele, type, -1, -1, null, null, -1, -1, -1, -1, -1, -1, -1);
    }

    public VariantStats(String chromosome, int position, String referenceAllele, String alternateAlleles,
                        VariantType variantType, float maf, float mgf, String mafAllele, String mgfGenotype,
                        int numMissingAlleles, int numMissingGenotypes, int numMendelErrors, float percentCasesDominant,
                        float percentControlsDominant, float percentCasesRecessive, float percentControlsRecessive) {
        impl = new org.opencb.biodata.models.variant.avro.VariantStats(referenceAllele, alternateAlleles,
                -1, -1,
                new HashMap<>(), new HashMap<>(),
                numMissingAlleles, numMissingGenotypes,
                -1F, -1F, maf, mgf, mafAllele, mgfGenotype,
                false, -1,
                percentCasesDominant, percentControlsDominant, percentCasesRecessive, percentControlsRecessive,
                -1F, -1, variantType, null);

//        this.hw = new VariantHardyWeinbergStats();
    }

    public org.opencb.biodata.models.variant.avro.VariantStats getImpl() {
        return impl;
    }

    public String getRefAllele() {
        return impl.getRefAllele();
    }

    public void setRefAllele(String value) {
        impl.setRefAllele(value);
    }

    public String getAltAllele() {
        return impl.getAltAllele();
    }

    public void setAltAllele(String value) {
        impl.setAltAllele(value);
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

    public Map<Genotype, Integer> getGenotypesCount() {
        return impl.getGenotypesCount();
    }

    public void setGenotypesCount(Map<Genotype, Integer> value) {
        impl.setGenotypesCount(value);
    }

    public Map<Genotype, Float> getGenotypesFreq() {
        return impl.getGenotypesFreq();
    }

    public void setGenotypesFreq(Map<Genotype, Float> value) {
        impl.setGenotypesFreq(value);
    }

    public Integer getMissingAlleles() {
        return impl.getMissingAlleles();
    }

    public void setMissingAlleles(Integer value) {
        impl.setMissingAlleles(value);
    }

    public Integer getMissingGenotypes() {
        return impl.getMissingGenotypes();
    }

    public void setMissingGenotypes(Integer value) {
        impl.setMissingGenotypes(value);
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

    public Boolean getPassedFilters() {
        return impl.getPassedFilters();
    }

    public boolean hasPassedFilters() {
        return impl.getPassedFilters();
    }

    public void setPassedFilters(Boolean value) {
        impl.setPassedFilters(value);
    }

    public Integer getMendelianErrors() {
        return impl.getMendelianErrors();
    }

    public void setMendelianErrors(Integer value) {
        impl.setMendelianErrors(value);
    }

    public Float getCasesPercentDominant() {
        return impl.getCasesPercentDominant();
    }

    public void setCasesPercentDominant(Float value) {
        impl.setCasesPercentDominant(value);
    }

    public Float getControlsPercentDominant() {
        return impl.getControlsPercentDominant();
    }

    public void setControlsPercentDominant(Float value) {
        impl.setControlsPercentDominant(value);
    }

    public Float getCasesPercentRecessive() {
        return impl.getCasesPercentRecessive();
    }

    public void setCasesPercentRecessive(Float value) {
        impl.setCasesPercentRecessive(value);
    }

    public Float getControlsPercentRecessive() {
        return impl.getControlsPercentRecessive();
    }

    public void setControlsPercentRecessive(Float value) {
        impl.setControlsPercentRecessive(value);
    }

    public Float getQuality() {
        return impl.getQuality();
    }

    public void setQuality(Float value) {
        impl.setQuality(value);
    }

    public Integer getNumSamples() {
        return impl.getNumSamples();
    }

    public void setNumSamples(Integer value) {
        impl.setNumSamples(value);
    }

    public VariantType getVariantType() {
        return impl.getVariantType();
    }

    public void setVariantType(VariantType value) {
        impl.setVariantType(value);
    }

    public VariantHardyWeinbergStats getHw() {
        return impl.getHw();
    }

    public void setHw(VariantHardyWeinbergStats value) {
        impl.setHw(value);
    }

    public void addGenotype(Genotype g) {
        this.addGenotype(g, 1);
    }

    public void addGenotype(Genotype g, int addedCount) {
        addGenotype(g, addedCount, true);
    }

    public void addGenotype(Genotype g, int addedCount, boolean normalize) {
//        Genotype normalizedGenotype = normalizeGenotypeAlleles(g);
        if (normalize) {
            g = normalizeGenotypeAlleles(g);
        }
        getGenotypesCount().compute(g, (key, prev) -> prev == null ? addedCount : prev + addedCount);
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

    public boolean isTransition() {
        return isTransition(getRefAllele(), getAltAllele());
    }

    public boolean isTransversion() {
        return isTransversion(getRefAllele(), getAltAllele());
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
