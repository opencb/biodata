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

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * 
 * TODO Mendelian errors must be calculated
 */
public class VariantStats {

    private String refAllele;
    private String altAllele;
    private Variant.VariantType variantType;
    
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
    
    private boolean transition;
    private boolean transversion;
    
    private float quality;
    private int numSamples;
    private VariantHardyWeinbergStats hw;

    
    public VariantStats() {
        this(null, -1, null, null, Variant.VariantType.SNV, -1, -1, null, null, -1, -1, -1, false, -1, -1, -1, -1);
    }

    public VariantStats(Variant variant) {
        this(null, -1, 
            variant != null ? variant.getReference() : null, 
            variant != null ? variant.getAlternate() : null, 
            Variant.VariantType.SNV, -1, -1, null, null, -1, -1, -1, false, -1, -1, -1, -1);
    }

    public VariantStats(String referenceAllele, String alternateAllele, Variant.VariantType type) {
        this(null, -1, referenceAllele, alternateAllele, type, -1, -1, null, null, -1, -1, -1, false, -1, -1, -1, -1);
    }
    
    public VariantStats(String chromosome, int position, String referenceAllele, String alternateAlleles, 
            Variant.VariantType variantType, float maf, float mgf, String mafAllele, String mgfGenotype, 
            int numMissingAlleles, int numMissingGenotypes, int numMendelErrors, boolean isIndel, 
            float percentCasesDominant, float percentControlsDominant,
            float percentCasesRecessive, float percentControlsRecessive) {
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

        this.hw = new VariantHardyWeinbergStats();
    }

    public String getRefAllele() {
        return refAllele;
    }

    void setRefAllele(String refAllele) {
        this.refAllele = refAllele;
    }

    public String getAltAllele() {
        return altAllele;
    }

    void setAltAllele(String altAllele) {
        this.altAllele = altAllele;
    }

    public Variant.VariantType getVariantType() {
        return variantType;
    }

    void setVariantType(Variant.VariantType variantType) {
        this.variantType = variantType;
    }

    public int getRefAlleleCount() {
        return refAlleleCount;
    }

    void setRefAlleleCount(int refAlleleCount) {
        this.refAlleleCount = refAlleleCount;
    }

    public int getAltAlleleCount() {
        return altAlleleCount;
    }

    void setAltAlleleCount(int altAlleleCount) {
        this.altAlleleCount = altAlleleCount;
    }

    public float getRefAlleleFreq() {
        return refAlleleFreq;
    }

    void setRefAlleleFreq(float refAlleleFreq) {
        this.refAlleleFreq = refAlleleFreq;
    }

    public float getAltAlleleFreq() {
        return altAlleleFreq;
    }

    void setAltAlleleFreq(float altAlleleFreq) {
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

    void setMgfGenotype(String mgfGenotype) {
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

    void setMgf(float mgf) {
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

    void setMissingGenotypes(int missingGenotypes) {
        this.missingGenotypes = missingGenotypes;
    }

    public int getMendelianErrors() {
        return mendelianErrors;
    }

    void setMendelianErrors(int mendelianErrors) {
        this.mendelianErrors = mendelianErrors;
    }

    public float getCasesPercentDominant() {
        return casesPercentDominant;
    }

    void setCasesPercentDominant(float casesPercentDominant) {
        this.casesPercentDominant = casesPercentDominant;
    }

    public float getControlsPercentDominant() {
        return controlsPercentDominant;
    }

    void setControlsPercentDominant(float controlsPercentDominant) {
        this.controlsPercentDominant = controlsPercentDominant;
    }

    public float getCasesPercentRecessive() {
        return casesPercentRecessive;
    }

    void setCasesPercentRecessive(float casesPercentRecessive) {
        this.casesPercentRecessive = casesPercentRecessive;
    }

    public float getControlsPercentRecessive() {
        return controlsPercentRecessive;
    }

    void setControlsPercentRecessive(float controlsPercentRecessive) {
        this.controlsPercentRecessive = controlsPercentRecessive;
    }

    public boolean isTransition() {
        return transition;
    }

    public void setTransition(boolean transition) {
        this.transition = transition;
    }

    public boolean isTransversion() {
        return transversion;
    }

    public void setTransversion(boolean transversion) {
        this.transversion = transversion;
    }

    public VariantHardyWeinbergStats getHw() {
        return hw;
    }

    public boolean hasPassedFilters() {
        return passedFilters;
    }

    void setPassedFilters(boolean passedFilters) {
        this.passedFilters = passedFilters;
    }

    public float getQuality() {
        return quality;
    }

    void setQuality(float quality) {
        this.quality = quality;
    }

    public int getNumSamples() {
        return numSamples;
    }

    void setNumSamples(int numSamples) {
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

    public VariantStats calculate(Map<String, Map<String, String>> samplesData, Map<String, String> attributes, Pedigree pedigree) {
        int[] allelesCount = new int[2];
        int totalAllelesCount = 0, totalGenotypesCount = 0;

        float controlsDominant = 0, casesDominant = 0;
        float controlsRecessive = 0, casesRecessive = 0;

        this.setNumSamples(samplesData.size());

        for (Map.Entry<String, Map<String, String>> sample : samplesData.entrySet()) {
            String sampleName = sample.getKey();
            Genotype g = new Genotype(sample.getValue().get("GT"), this.getRefAllele(), this.getAltAllele());
            this.addGenotype(g);

            // Check missing alleles and genotypes
            switch (g.getCode()) {
                case ALLELES_OK:
                    // Both alleles set
                    allelesCount[g.getAllele(0)]++;
                    allelesCount[g.getAllele(1)]++;

                    totalAllelesCount += 2;
                    totalGenotypesCount++;

                    // Counting genotypes for Hardy-Weinberg (all phenotypes)
                    if (g.isAlleleRef(0) && g.isAlleleRef(1)) { // 0|0
                        this.getHw().incN_AA();
                    } else if ((g.isAlleleRef(0) && g.getAllele(1) == 1) || (g.getAllele(0) == 1 && g.isAlleleRef(1))) {  // 0|1, 1|0
                        this.getHw().incN_Aa();

                    } else if (g.getAllele(0) == 1 && g.getAllele(1) == 1) {
                        this.getHw().incN_aa();
                    }

                    break;
                case HAPLOID:
                    // Haploid (chromosome X/Y)
                    allelesCount[g.getAllele(0)]++;
                    totalAllelesCount++;
                    break;
                case MULTIPLE_ALTERNATES:
                    // Alternate with different "index" than the one that is being handled
                    break;
                default:
                    // Missing genotype (one or both alleles missing)
                    this.setMissingGenotypes(this.getMissingGenotypes() + 1);
                    if (g.getAllele(0) < 0) {
                        this.setMissingAlleles(this.getMissingAlleles() + 1);
                    } else {
                        allelesCount[g.getAllele(0)]++;
                        totalAllelesCount++;
                    }

                    if (g.getAllele(1) < 0) {
                        this.setMissingAlleles(this.getMissingAlleles() + 1);
                    } else {
                        allelesCount[g.getAllele(1)]++;
                        totalAllelesCount++;
                    }
                    break;

            }

            // Include statistics that depend on pedigree information
            if (pedigree != null) {
                if (g.getCode() == AllelesCode.ALLELES_OK || g.getCode() == AllelesCode.HAPLOID) {
                    Individual ind = pedigree.getIndividual(sampleName);
//                    if (MendelChecker.isMendelianError(ind, g, variant.getChromosome(), file.getSamplesData())) {
//                        this.setMendelianErrors(this.getMendelianErrors() + 1);
//                    }
                    if (g.getCode() == AllelesCode.ALLELES_OK) {
                        // Check inheritance models
                        if (ind.getCondition() == Condition.UNAFFECTED) {
                            if (g.isAlleleRef(0) && g.isAlleleRef(1)) { // 0|0
                                controlsDominant++;
                                controlsRecessive++;

                            } else if ((g.isAlleleRef(0) && !g.isAlleleRef(1)) || (!g.isAlleleRef(0) || g.isAlleleRef(1))) { // 0|1 or 1|0
                                controlsRecessive++;

                            }
                        } else if (ind.getCondition() == Condition.AFFECTED) {
                            if (!g.isAlleleRef(0) && !g.isAlleleRef(1) && g.getAllele(0) == g.getAllele(1)) {// 1|1, 2|2, and so on
                                casesRecessive++;
                                casesDominant++;
                            } else if (!g.isAlleleRef(0) || !g.isAlleleRef(1)) { // 0|1, 1|0, 1|2, 2|1, 1|3, and so on
                                casesDominant++;

                            }
                        }

                    }
                }
            } 

        }  // Finish all samples loop
        
        // Set counts for each allele
        this.setRefAlleleCount(allelesCount[0]);
        this.setAltAlleleCount(allelesCount[1]);

        // Calculate MAF and MGF
        this.calculateAlleleFrequencies(totalAllelesCount);
        this.calculateGenotypeFrequencies(totalGenotypesCount);

        // Calculate Hardy-Weinberg statistic
        this.getHw().calculate();

        // Transitions and transversions
        this.calculateTransitionsAndTransversions();//refAllele, altAllele);

        // Update variables finally used to update file_stats_t structure
        if ("PASS".equalsIgnoreCase(attributes.get("FILTER"))) {
            this.setPassedFilters(true);
        }

        if (attributes.containsKey("QUAL") && !(".").equals(attributes.get("QUAL"))) {
            float qualAux = Float.valueOf(attributes.get("QUAL"));
            if (qualAux >= 0) {
                this.setQuality(qualAux);
            }
        }

        // Once all samples have been traversed, calculate % that follow inheritance model
        controlsDominant = controlsDominant * 100 / (this.getNumSamples() - this.getMissingGenotypes());
        casesDominant = casesDominant * 100 / (this.getNumSamples() - this.getMissingGenotypes());
        controlsRecessive = controlsRecessive * 100 / (this.getNumSamples() - this.getMissingGenotypes());
        casesRecessive = casesRecessive * 100 / (this.getNumSamples() - this.getMissingGenotypes());

        this.setCasesPercentDominant(casesDominant);
        this.setControlsPercentDominant(controlsDominant);
        this.setCasesPercentRecessive(casesRecessive);
        this.setControlsPercentRecessive(controlsRecessive);

        return this;
    }

    /**
     * Calculates the statistics for some variants read from a set of files, and 
     * optionally given pedigree information. Some statistics like inheritance 
     * patterns can only be calculated if pedigree information is provided.
     * 
     * @param variants The variants whose statistics will be calculated
     * @param ped Optional pedigree information to calculate some statistics
     */
    public static void calculateStatsForVariantsList(List<Variant> variants, Pedigree ped) {
        for (Variant variant : variants) {
            for (VariantSourceEntry file : variant.getSourceEntries().values()) {
                VariantStats stats = new VariantStats(variant).calculate(file.getSamplesData(), file.getAttributes(), ped);
                file.setStats(stats); // TODO Correct?
            }
        }
    }

    private void calculateAlleleFrequencies(int totalAllelesCount) {
        // MAF
        refAlleleFreq = (totalAllelesCount > 0) ? refAlleleCount / (float) totalAllelesCount : 0;
        altAlleleFreq = (totalAllelesCount > 0) ? altAlleleCount / (float) totalAllelesCount : 0;
        if (refAlleleFreq <= altAlleleFreq) {
            this.setMaf(refAlleleFreq);
            this.setMafAllele(refAllele);
        } else {
            this.setMaf(altAlleleFreq);
            this.setMafAllele(altAllele);
        }
    }

    private void calculateGenotypeFrequencies(int totalGenotypesCount) {
        if (genotypesCount.isEmpty()) {
            // Nothing to do here
            return;
        }
        
        // Set all combinations of genotypes to zero
        genotypesFreq.put(new Genotype("0/0", refAllele, altAllele), 0.0f);
        genotypesFreq.put(new Genotype("0/1", refAllele, altAllele), 0.0f);
        genotypesFreq.put(new Genotype("1/1", refAllele, altAllele), 0.0f);
        
        // Insert the genotypes found in the file
        for (Map.Entry<Genotype, Integer> gtCount : genotypesCount.entrySet()) {
            if (gtCount.getKey().getCode() == AllelesCode.ALLELES_MISSING) {
                // Missing genotypes shouldn't have frequencies calculated
                continue;
            }
            
            float freq = (totalGenotypesCount > 0) ? gtCount.getValue() / (float) totalGenotypesCount : 0;
            genotypesFreq.put(gtCount.getKey(), freq);
        }
        
        // Traverse the genotypes to see which one has the MGF
        float currMgf = Float.MAX_VALUE;
        Genotype currMgfGenotype = null;
        
        for (Map.Entry<Genotype, Float> gtCount : genotypesFreq.entrySet()) {
            float freq = gtCount.getValue();
            if (freq < currMgf) {
                currMgf = freq;
                currMgfGenotype = gtCount.getKey();
            }
        }
        
        if (currMgfGenotype != null) {
            this.setMgf(currMgf);
            this.setMgfGenotype(currMgfGenotype.toString());
        }
    }

    private void calculateTransitionsAndTransversions() {
        if (refAllele.length() == 1 && altAllele.length() == 1) {
            switch (refAllele.toUpperCase()) {
                case "C":
                    if (altAllele.equalsIgnoreCase("T")) {
                        this.setTransition(true);
                    } else {
                        this.setTransversion(true);
                    }
                    break;
                case "T":
                    if (altAllele.equalsIgnoreCase("C")) {
                        this.setTransition(true);
                    } else {
                        this.setTransversion(true);
                    }
                    break;
                case "A":
                    if (altAllele.equalsIgnoreCase("G")) {
                        this.setTransition(true);
                    } else {
                        this.setTransversion(true);
                    }
                    break;
                case "G":
                    if (altAllele.equalsIgnoreCase("A")) {
                        this.setTransition(true);
                    } else {
                        this.setTransversion(true);
                    }
                    break;
            }
        }
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
