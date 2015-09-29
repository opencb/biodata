/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.variant.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Condition;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantType;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 *
 * TODO Mendelian errors must be calculated
 */
public class VariantStats extends org.opencb.biodata.models.variant.avro.VariantStats {
//
//    private String refAllele;
//    private String altAllele;
//    private VariantType variantType;
//
//    private int refAlleleCount;
//    private int altAlleleCount;
//    private Map<Genotype, Integer> genotypesCount;
//
//    private int missingAlleles;
//    private int missingGenotypes;
//
//    private float refAlleleFreq;
//    private float altAlleleFreq;
//    private Map<Genotype, Float> genotypesFreq;
//    private float maf;
//    private float mgf;
//    private String mafAllele;
//    private String mgfGenotype;
//
//    private boolean passedFilters;
//
//    private int mendelianErrors;
//
//    private float casesPercentDominant;
//    private float controlsPercentDominant;
//    private float casesPercentRecessive;
//    private float controlsPercentRecessive;
//
//    private float quality;
//    private int numSamples;
//    private VariantHardyWeinbergStats hw;

    
    public VariantStats() {
        this(null, -1, null, null, VariantType.SNV, -1, -1, null, null, -1, -1, -1, -1, -1, -1, -1);
    }

    public VariantStats(org.opencb.biodata.models.variant.avro.VariantStats other) {
        super(other.getRefAllele(), other.getAltAllele(),
                other.getRefAlleleCount(), other.getAltAlleleCount(),
                other.getGenotypesCount(), other.getGenotypesFreq(),
                other.getMissingAlleles(), other.getMissingGenotypes(),
                other.getRefAlleleFreq(), other.getAltAlleleFreq(), other.getMaf(), other.getMgf(), other.getMafAllele(), other.getMgfGenotype(),
                other.getPassedFilters(), other.getMendelianErrors(),
                other.getCasesPercentDominant(), other.getControlsPercentDominant(), other.getCasesPercentRecessive(), other.getControlsPercentRecessive(),
                other.getQuality(), other.getNumSamples(), other.getVariantType(), other.getHw());
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
        super(referenceAllele, alternateAlleles,
                -1, -1,
                new HashMap<>(), new HashMap<>(),
                numMissingAlleles, numMissingGenotypes,
                -1F, -1F, maf, mgf, mafAllele, mgfGenotype,
                false, -1,
                percentCasesDominant, percentControlsDominant, percentCasesRecessive, percentControlsRecessive,
                -1F, -1, variantType, null);

//        this.hw = new VariantHardyWeinbergStats();
    }

    public void addGenotype(Genotype g) {
        this.addGenotype(g, 1);
    }

    public void addGenotype(Genotype g, int addedCount) {
        String normalizedGenotype = normalizeGenotypeAlleles(g).toString();
        addGenotype(normalizedGenotype, addedCount);
    }

    public void addGenotype(String normalizedGenotype, int addedCount) {
        Integer count;
        if (getGenotypesCount().containsKey(normalizedGenotype)) {
            count = getGenotypesCount().get(normalizedGenotype) + addedCount;
        } else {
            count = addedCount;
        }
        getGenotypesCount().put(normalizedGenotype, count);
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
            return new Genotype(joinedAlleles.toString(), g.getReference(), g.getAlternate());
        }
    }
    
//    void setGenotypesCount(Map<Genotype, Integer> genotypesCount) {
//        this.genotypesCount = genotypesCount;
//    }
//
//    public Map<Genotype, Float> getGenotypesFreq() {
//        return genotypesFreq;
//    }
//
//    void setGenotypesFreq(Map<Genotype, Float> genotypesFreq) {
//        this.genotypesFreq = genotypesFreq;
//    }

    public boolean isTransition() {
        switch (getRefAllele().toUpperCase()) {
            case "C":
                return getAltAllele().equalsIgnoreCase("T");
            case "T":
                return getAltAllele().equalsIgnoreCase("C");
            case "A":
                return getAltAllele().equalsIgnoreCase("G");
            case "G":
                return getAltAllele().equalsIgnoreCase("A");
            default:
                return false;
        }
    }

    public boolean isTransversion() {
        switch (getRefAllele().toUpperCase()) {
            case "C":
                return !getAltAllele().equalsIgnoreCase("T");
            case "T":
                return !getAltAllele().equalsIgnoreCase("C");
            case "A":
                return !getAltAllele().equalsIgnoreCase("G");
            case "G":
                return !getAltAllele().equalsIgnoreCase("A");
            default:
                return false;
        }
    }

//    public VariantHardyWeinbergStats getHw() {
//        return hw;
//    }

    public boolean hasPassedFilters() {
        return super.getPassedFilters();
    }



    @Override
    public String toString() {
        return "VariantStats{"
                + "refAllele='" + getRefAllele() + '\''
                + ", altAllele='" + getAltAllele() + '\''
                + ", mafAllele='" + getMafAllele() + '\''
                + ", mgfAllele='" + getMgfGenotype() + '\''
                + ", maf=" + getMaf()
                + ", mgf=" + getMgf()
                + ", missingAlleles=" + getMissingAlleles()
                + ", missingGenotypes=" + getMissingGenotypes()
                + ", mendelinanErrors=" + getMendelianErrors()
                + ", casesPercentDominant=" + getCasesPercentDominant()
                + ", controlsPercentDominant=" + getControlsPercentDominant()
                + ", casesPercentRecessive=" + getCasesPercentRecessive()
                + ", controlsPercentRecessive=" + getControlsPercentRecessive()
                + '}';
    }
    

}
