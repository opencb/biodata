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
public class VariantStats {

    private String refAllele;
    private String altAllele;
    private VariantType variantType;
    
    private int refAlleleCount;
    private int altAlleleCount;
    private Map<Genotype, Integer> genotypesCount;
    
    private int missingAlleles;
    private int missingGenotypes;
    
    private float refAlleleFreq;
    private float altAlleleFreq;
    private Map<Genotype, Float> genotypesFreq;
    private float maf;
    private float mgf;
    private String mafAllele;
    private String mgfGenotype;
    
    private boolean passedFilters;
    
    private int mendelianErrors;
    
    private float casesPercentDominant;
    private float controlsPercentDominant;
    private float casesPercentRecessive;
    private float controlsPercentRecessive;
    
    private float quality;
    private int numSamples;
    private VariantHardyWeinbergStats hw;

    
    public VariantStats() {
        this(null, -1, null, null, VariantType.SNV, -1, -1, null, null, -1, -1, -1, -1, -1, -1, -1);
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
        this.refAllele = referenceAllele;
        this.altAllele = alternateAlleles;
        this.variantType = variantType;
        
        this.maf = maf;
        this.mgf = mgf;
        this.mafAllele = mafAllele;
        this.mgfGenotype = mgfGenotype;
        this.genotypesCount = new HashMap<>();
        this.genotypesFreq = new LinkedHashMap<>();

        this.missingAlleles = numMissingAlleles;
        this.missingGenotypes = numMissingGenotypes;
        this.mendelianErrors = numMendelErrors;

        this.casesPercentDominant = percentCasesDominant;
        this.controlsPercentDominant = percentControlsDominant;
        this.casesPercentRecessive = percentCasesRecessive;
        this.controlsPercentRecessive = percentControlsRecessive;
        
        this.quality = -1;

        this.hw = new VariantHardyWeinbergStats();
    }

    public String getRefAllele() {
        return refAllele;
    }

    public void setRefAllele(String refAllele) {
        this.refAllele = refAllele;
    }

    public String getAltAllele() {
        return altAllele;
    }

    public void setAltAllele(String altAllele) {
        this.altAllele = altAllele;
    }

    public VariantType getVariantType() {
        return variantType;
    }

    public void setVariantType(VariantType variantType) {
        this.variantType = variantType;
    }

    public int getRefAlleleCount() {
        return refAlleleCount;
    }

    public void setRefAlleleCount(int refAlleleCount) {
        this.refAlleleCount = refAlleleCount;
    }

    public int getAltAlleleCount() {
        return altAlleleCount;
    }

    public void setAltAlleleCount(int altAlleleCount) {
        this.altAlleleCount = altAlleleCount;
    }

    public float getRefAlleleFreq() {
        return refAlleleFreq;
    }

    public void setRefAlleleFreq(float refAlleleFreq) {
        this.refAlleleFreq = refAlleleFreq;
    }

    public float getAltAlleleFreq() {
        return altAlleleFreq;
    }

    public void setAltAlleleFreq(float altAlleleFreq) {
        this.altAlleleFreq = altAlleleFreq;
    }
    
    public String getMafAllele() {
        return mafAllele;
    }

    public void setMafAllele(String mafAllele) {
        this.mafAllele = mafAllele;
    }

    public String getMgfGenotype() {
        return mgfGenotype;
    }

    public void setMgfGenotype(String mgfGenotype) {
        this.mgfGenotype = mgfGenotype;
    }

    public Map<Genotype, Integer> getGenotypesCount() {
        return genotypesCount;
    }

    public void addGenotype(Genotype g) {
        this.addGenotype(g, 1);
    }
    
    public void addGenotype(Genotype g, int addedCount) {
        Integer count;
        Genotype normalizedGenotype = normalizeGenotypeAlleles(g);
        if (genotypesCount.containsKey(normalizedGenotype)) {
            count = genotypesCount.get(normalizedGenotype) + addedCount;
        } else {
            count = addedCount;
        }
        genotypesCount.put(normalizedGenotype, count);
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
    
    void setGenotypesCount(Map<Genotype, Integer> genotypesCount) {
        this.genotypesCount = genotypesCount;
    }

    public Map<Genotype, Float> getGenotypesFreq() {
        return genotypesFreq;
    }

    void setGenotypesFreq(Map<Genotype, Float> genotypesFreq) {
        this.genotypesFreq = genotypesFreq;
    }

    public float getMaf() {
        return maf;
    }

    public void setMaf(float maf) {
        this.maf = maf;
    }

    public float getMgf() {
        return mgf;
    }

    public void setMgf(float mgf) {
        this.mgf = mgf;
    }

    public int getMissingAlleles() {
        return missingAlleles;
    }

    public void setMissingAlleles(int missingAlleles) {
        this.missingAlleles = missingAlleles;
    }

    public int getMissingGenotypes() {
        return missingGenotypes;
    }

    public void setMissingGenotypes(int missingGenotypes) {
        this.missingGenotypes = missingGenotypes;
    }

    public int getMendelianErrors() {
        return mendelianErrors;
    }

    public void setMendelianErrors(int mendelianErrors) {
        this.mendelianErrors = mendelianErrors;
    }

    public float getCasesPercentDominant() {
        return casesPercentDominant;
    }

    public void setCasesPercentDominant(float casesPercentDominant) {
        this.casesPercentDominant = casesPercentDominant;
    }

    public float getControlsPercentDominant() {
        return controlsPercentDominant;
    }

    public void setControlsPercentDominant(float controlsPercentDominant) {
        this.controlsPercentDominant = controlsPercentDominant;
    }

    public float getCasesPercentRecessive() {
        return casesPercentRecessive;
    }

    public void setCasesPercentRecessive(float casesPercentRecessive) {
        this.casesPercentRecessive = casesPercentRecessive;
    }

    public float getControlsPercentRecessive() {
        return controlsPercentRecessive;
    }

    public void setControlsPercentRecessive(float controlsPercentRecessive) {
        this.controlsPercentRecessive = controlsPercentRecessive;
    }

    public boolean isTransition() {
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

    public boolean isTransversion() {
        switch (refAllele.toUpperCase()) {
            case "C":
                return !altAllele.equalsIgnoreCase("T");
            case "T":
                return !altAllele.equalsIgnoreCase("C");
            case "A":
                return !altAllele.equalsIgnoreCase("G");
            case "G":
                return !altAllele.equalsIgnoreCase("A");
            default:
                return false;
        }
    }

    public VariantHardyWeinbergStats getHw() {
        return hw;
    }

    public boolean hasPassedFilters() {
        return passedFilters;
    }

    public void setPassedFilters(boolean passedFilters) {
        this.passedFilters = passedFilters;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    @Override
    public String toString() {
        return "VariantStats{"
                + "refAllele='" + refAllele + '\''
                + ", altAllele='" + altAllele + '\''
                + ", mafAllele='" + mafAllele + '\''
                + ", mgfAllele='" + mgfGenotype + '\''
                + ", maf=" + maf
                + ", mgf=" + mgf
                + ", missingAlleles=" + missingAlleles
                + ", missingGenotypes=" + missingGenotypes
                + ", mendelinanErrors=" + mendelianErrors
                + ", casesPercentDominant=" + casesPercentDominant
                + ", controlsPercentDominant=" + controlsPercentDominant
                + ", casesPercentRecessive=" + casesPercentRecessive
                + ", controlsPercentRecessive=" + controlsPercentRecessive
                + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.refAllele);
        hash = 79 * hash + Objects.hashCode(this.altAllele);
        hash = 79 * hash + this.refAlleleCount;
        hash = 79 * hash + this.altAlleleCount;
        hash = 79 * hash + Objects.hashCode(this.genotypesCount);
        hash = 79 * hash + this.missingAlleles;
        hash = 79 * hash + this.missingGenotypes;
        hash = 79 * hash + Float.floatToIntBits(this.refAlleleFreq);
        hash = 79 * hash + Float.floatToIntBits(this.altAlleleFreq);
        hash = 79 * hash + Objects.hashCode(this.genotypesFreq);
        hash = 79 * hash + Float.floatToIntBits(this.maf);
        hash = 79 * hash + Float.floatToIntBits(this.mgf);
        hash = 79 * hash + Objects.hashCode(this.mafAllele);
        hash = 79 * hash + Objects.hashCode(this.mgfGenotype);
        hash = 79 * hash + (this.passedFilters ? 1 : 0);
        hash = 79 * hash + this.mendelianErrors;
        hash = 79 * hash + Float.floatToIntBits(this.casesPercentDominant);
        hash = 79 * hash + Float.floatToIntBits(this.controlsPercentDominant);
        hash = 79 * hash + Float.floatToIntBits(this.casesPercentRecessive);
        hash = 79 * hash + Float.floatToIntBits(this.controlsPercentRecessive);
        hash = 79 * hash + Float.floatToIntBits(this.quality);
        hash = 79 * hash + this.numSamples;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariantStats other = (VariantStats) obj;
        if (!Objects.equals(this.refAllele, other.refAllele)) {
            return false;
        }
        if (!Objects.equals(this.altAllele, other.altAllele)) {
            return false;
        }
        if (this.refAlleleCount != other.refAlleleCount) {
            return false;
        }
        if (this.altAlleleCount != other.altAlleleCount) {
            return false;
        }
        if (!Objects.equals(this.genotypesCount, other.genotypesCount)) {
            return false;
        }
        if (this.missingAlleles != other.missingAlleles) {
            return false;
        }
        if (this.missingGenotypes != other.missingGenotypes) {
            return false;
        }
        if (Float.floatToIntBits(this.refAlleleFreq) != Float.floatToIntBits(other.refAlleleFreq)) {
            return false;
        }
        if (Float.floatToIntBits(this.altAlleleFreq) != Float.floatToIntBits(other.altAlleleFreq)) {
            return false;
        }
        if (!Objects.equals(this.genotypesFreq, other.genotypesFreq)) {
            return false;
        }
        if (Float.floatToIntBits(this.maf) != Float.floatToIntBits(other.maf)) {
            return false;
        }
        if (Float.floatToIntBits(this.mgf) != Float.floatToIntBits(other.mgf)) {
            return false;
        }
        if (!Objects.equals(this.mafAllele, other.mafAllele)) {
            return false;
        }
        if (!Objects.equals(this.mgfGenotype, other.mgfGenotype)) {
            return false;
        }
        if (this.passedFilters != other.passedFilters) {
            return false;
        }
        if (this.mendelianErrors != other.mendelianErrors) {
            return false;
        }
        if (Float.floatToIntBits(this.casesPercentDominant) != Float.floatToIntBits(other.casesPercentDominant)) {
            return false;
        }
        if (Float.floatToIntBits(this.controlsPercentDominant) != Float.floatToIntBits(other.controlsPercentDominant)) {
            return false;
        }
        if (Float.floatToIntBits(this.casesPercentRecessive) != Float.floatToIntBits(other.casesPercentRecessive)) {
            return false;
        }
        if (Float.floatToIntBits(this.controlsPercentRecessive) != Float.floatToIntBits(other.controlsPercentRecessive)) {
            return false;
        }
        if (Float.floatToIntBits(this.quality) != Float.floatToIntBits(other.quality)) {
            return false;
        }
        if (this.numSamples != other.numSamples) {
            return false;
        }
        return true;
    }
    

}
