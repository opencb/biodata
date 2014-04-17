package org.opencb.biodata.models.variant.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Condition;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.Variant.VariantType;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * 
 * @TODO Mendelian errors must be calculated
 */
public class VariantStats {

    private Variant variant;
    
    private String chromosome;
    private long position;
    private String refAllele;
    @Deprecated
    private String[] altAlleles;
    private String altAllele;
    @Deprecated
    private String id;
    
    private int numAlleles;
    private int[] allelesCount;
    private int[] genotypesCount;
    private List<Genotype> genotypes;
    private int missingAlleles;
    private int missingGenotypes;
    
    private float[] allelesFreq;
    private float[] genotypesFreq;
    private float maf;
    private float mgf;
    private String mafAllele;
    private String mgfGenotype;
    
    private int mendelianErrors;
//    @Deprecated
//    private boolean isIndel;
//    @Deprecated
//    private boolean isSNP;
    private boolean passedFilters;
    
    private float casesPercentDominant;
    private float controlsPercentDominant;
    private float casesPercentRecessive;
    private float controlsPercentRecessive;
    
    private int transitionsCount;
    private int transversionsCount;
    
    private float qual;
    private int numSamples;
    private VariantHardyWeinbergStats hw;

    public VariantStats() {
        this.chromosome = "";
        this.refAllele = "";
        this.altAlleles = null;
        this.altAllele = "";

        this.mafAllele = "";
        this.mgfGenotype = "";
        this.position = (long) 0;
        this.numAlleles = 0;
        this.allelesCount = null;
        this.genotypesCount = null;
        this.missingAlleles = 0;
        this.missingGenotypes = 0;
        this.mendelianErrors = 0;
        this.allelesFreq = null;
        this.genotypesFreq = null;
        this.maf = 0;
        this.mgf = 0;
        this.casesPercentDominant = 0;
        this.controlsPercentDominant = 0;
        this.casesPercentRecessive = 0;
        this.controlsPercentRecessive = 0;
//        this.isIndel = false;
        this.genotypes = new ArrayList<>((int) Math.pow(this.numAlleles, 2));
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.hw = new VariantHardyWeinbergStats();
    }

    public VariantStats(Variant variant) {
        this.variant = variant;
        this.chromosome = variant.getChromosome();
        this.position = variant.getStart();
        this.refAllele = variant.getReference();
        this.altAlleles = new String[] { variant.getAlternate() };
        this.altAllele = variant.getAlternate();
        this.numAlleles = 2;
        
        this.id = variant.getId();
//        this.isIndel = variant.getType() == VariantType.INDEL;
//        this.isSNP = variant.getType() == VariantType.SNV;

        this.genotypes = new ArrayList<>((int) Math.pow(this.numAlleles, 2));
        this.allelesCount = new int[numAlleles];
        this.allelesFreq = new float[numAlleles];
        this.genotypesCount = new int[(int) Math.pow(this.numAlleles, 2)];
        this.genotypesFreq = new float[(int) Math.pow(this.numAlleles, 2)];

        this.missingAlleles = 0;
        this.missingGenotypes = 0;
        this.mendelianErrors = 0;
        this.maf = 0;
        this.mgf = 0;

        this.casesPercentDominant = 0;
        this.controlsPercentDominant = 0;
        this.casesPercentRecessive = 0;
        this.controlsPercentRecessive = 0;
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.hw = new VariantHardyWeinbergStats();
    }

    public VariantStats(String chromosome, int position, String referenceAllele, String alternateAlleles, double maf,
            double mgf, String mafAllele, String mgfGenotype, int numMissingAlleles, int numMissingGenotypes,
            int numMendelErrors, boolean isIndel, double percentCasesDominant, double percentControlsDominant,
            double percentCasesRecessive, double percentControlsRecessive) {
        this.chromosome = chromosome;
        this.position = position;
        this.refAllele = referenceAllele;
        this.altAlleles = alternateAlleles.split(",");
        this.altAllele = alternateAlleles;
//        this.numAlleles = 1 + this.altAlleles.length;
        this.numAlleles = 2;

        this.genotypes = new ArrayList<>((int) Math.pow(this.numAlleles, 2));
        this.allelesCount = new int[numAlleles];
        this.allelesFreq = new float[numAlleles];
        this.genotypesCount = new int[(int) Math.pow(this.numAlleles, 2)];
        this.genotypesFreq = new float[(int) Math.pow(this.numAlleles, 2)];

        this.maf = (float) maf;
        this.mgf = (float) mgf;
        this.mafAllele = mafAllele;
        this.mgfGenotype = mgfGenotype;

        this.missingAlleles = numMissingAlleles;
        this.missingGenotypes = numMissingGenotypes;
        this.mendelianErrors = numMendelErrors;
//        this.isIndel = isIndel;

        this.casesPercentDominant = (float) percentCasesDominant;
        this.controlsPercentDominant = (float) percentControlsDominant;
        this.casesPercentRecessive = (float) percentCasesRecessive;
        this.controlsPercentRecessive = (float) percentControlsRecessive;

    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getRefAllele() {
        return refAllele;
    }

    @Deprecated
    public String[] getAltAlleles() {
        return altAlleles;
    }

    @Deprecated
    public void setAltAlleles(String[] altAlleles) {
        this.altAlleles = altAlleles;
    }

    public String getAltAllele() {
        return altAllele;
    }

    public void setAltAllele(String altAllele) {
        this.altAllele = altAllele;
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

    public Integer getNumAlleles() {
        return numAlleles;
    }

    public void setNumAlleles(int numAlleles) {
        this.numAlleles = numAlleles;
    }

    public int[] getAllelesCount() {
        return allelesCount;
    }

    public void setAllelesCount(int[] allelesCount) {
        this.allelesCount = allelesCount;
    }

    public int[] getGenotypesCount() {
        return genotypesCount;
    }

    public void setGenotypesCount(int[] genotypesCount) {
        this.genotypesCount = genotypesCount;
    }

    public float[] getAllelesFreq() {
        return allelesFreq;
    }

    public void setAllelesFreq(float[] allelesFreg) {
        this.allelesFreq = allelesFreg;
    }

    public float[] getGenotypesFreq() {
        return genotypesFreq;
    }

    public void setGenotypesFreq(float[] genotypesFreq) {
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

    public void setRefAllele(String refAllele) {
        this.refAllele = refAllele;
    }

    public List<Genotype> getGenotypes() {
        return genotypes;
    }

    public void setGenotypes(List<Genotype> genotypes) {
        this.genotypes = genotypes;
    }

    public void addGenotype(Genotype g) {
        int index = genotypes.indexOf(g);
        if (index >= 0) {
            Genotype auxG = genotypes.get(index);
            auxG.setCount(auxG.getCount() + 1);
        } else {
            g.setCount(g.getCount() + 1);
            genotypes.add(g);
        }
    }

    public int getTransitionsCount() {
        return transitionsCount;
    }

    public void setTransitionsCount(int transitionsCount) {
        this.transitionsCount = transitionsCount;
    }

    public int getTransversionsCount() {
        return transversionsCount;
    }

    public void setTransversionsCount(int transversionsCount) {
        this.transversionsCount = transversionsCount;
    }

    public VariantHardyWeinbergStats getHw() {
        return hw;
    }

    public boolean isIndel() {
//        return isIndel;
        return variant.getType() == VariantType.INDEL;
    }

//    public void setIndel(boolean indel) {
//        this.isIndel = indel;
//    }

    public boolean isSNP() {
//        return isSNP;
        return variant.getType() == VariantType.SNV;
    }

//    public void setSNP(boolean SNP) {
//        isSNP = SNP;
//    }

    public boolean hasPassedFilters() {
        return passedFilters;
    }

    public void setPassedFilters(boolean passedFilters) {
        this.passedFilters = passedFilters;
    }

    public float getQual() {
        return qual;
    }

    public void setQual(float qual) {
        this.qual = qual;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "VariantStats{"
                + "chromosome='" + chromosome + '\''
                + ", position=" + position
                + ", refAllele='" + refAllele + '\''
//                + ", altAlleles=" + Arrays.toString(altAlleles)
                + ", altAllele='" + altAllele + '\''
                + ", mafAllele='" + mafAllele + '\''
                + ", mgfAllele='" + mgfGenotype + '\''
//                + ", numAlleles=" + numAlleles
                + ", allelesCount=" + Arrays.toString(allelesCount)
                + ", genotypesCount=" + Arrays.toString(genotypesCount)
                + ", genotypes=" + genotypes
                + ", allelesFreq=" + Arrays.toString(allelesFreq)
                + ", genotypesFreq=" + Arrays.toString(genotypesFreq)
                + ", maf=" + maf
                + ", mgf=" + mgf
                + ", missingAlleles=" + missingAlleles
                + ", missingGenotypes=" + missingGenotypes
                + ", mendelinanErrors=" + mendelianErrors
//                + ", isIndel=" + isIndel
                + ", casesPercentDominant=" + casesPercentDominant
                + ", controlsPercentDominant=" + controlsPercentDominant
                + ", casesPercentRecessive=" + casesPercentRecessive
                + ", controlsPercentRecessive=" + controlsPercentRecessive
                + ", transitionsCount=" + transitionsCount
                + ", transversionsCount=" + transversionsCount
                + '}';
    }

    public VariantStats calculate(Map<String, Map<String, String>> samplesData, Map<String, String> attributes, Pedigree pedigree) {
        int totalAllelesCount = 0;
        int totalGenotypesCount = 0;

        float controlsDominant = 0;
        float casesDominant = 0;
        float controlsRecessive = 0;
        float casesRecessive = 0;

        this.setNumSamples(samplesData.size());

        for (Map.Entry<String, Map<String, String>> sample : samplesData.entrySet()) {
            String sampleName = sample.getKey();
            Genotype g = new Genotype(sample.getValue().get("GT"), this.getRefAllele(), this.getAltAllele());
            this.addGenotype(g);

            // Check missing alleles and genotypes
            switch (g.getCode()) {
                case ALLELES_OK:
                    // Both alleles set
                    int genotypeCurrentPos = g.getAllele(0) * (this.getNumAlleles()) + g.getAllele(1);

                    this.allelesCount[g.getAllele(0)]++;
                    this.allelesCount[g.getAllele(1)]++;
                    this.genotypesCount[genotypeCurrentPos]++;

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
                    try {
                        this.allelesCount[g.getAllele(0)]++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("vcfRecord = " + variant);
                        System.out.println("g = " + g);
                    }
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
                        this.allelesCount[g.getAllele(0)]++;
                        totalAllelesCount++;
                    }

                    if (g.getAllele(1) < 0) {
                        this.setMissingAlleles(this.getMissingAlleles() + 1);
                    } else {
                        this.allelesCount[g.getAllele(1)]++;
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

        // Calculate MAF and MGF
        this.calculateAlleleAndGenotypeFrequencies(totalAllelesCount, totalGenotypesCount);

        // Calculate Hardy-Weinberg statistic
        this.getHw().calculate();

        // Transitions and transversions
        this.calculateTransitionsAndTransversions(variant.getReference(), variant.getAlternate());

        // Update variables finally used to update file_stats_t structure
        if ("PASS".equalsIgnoreCase(attributes.get("FILTER"))) {
            this.setPassedFilters(true);
        }

        if (attributes.containsKey("QUAL") && !(".").equals(attributes.get("QUAL"))) {
            float qualAux = Float.valueOf(attributes.get("QUAL"));
            if (qualAux >= 0) {
                this.setQual(qualAux);
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
            for (ArchivedVariantFile file : variant.getFiles().values()) {
                VariantStats stats = new VariantStats(variant).calculate(file.getSamplesData(), file.getAttributes(), ped);
                file.setStats(stats); // TODO Correct?
            }
        }
    }

    private void calculateAlleleAndGenotypeFrequencies(int totalAllelesCount, int totalGenotypesCount) {
        String mgfGenotype = "";

        float maf = Float.MAX_VALUE;
        float mgf = Float.MAX_VALUE;
        float currentGtFreq;

        float[] allelesFreq = new float[this.getNumAlleles()];
        float[] genotypesFreq = new float[this.getNumAlleles() * this.getNumAlleles()];

        // MAF
        for (int i = 0; i < this.getNumAlleles(); i++) {
            allelesFreq[i] = (totalAllelesCount > 0) ? this.getAllelesCount()[i] / (float) totalAllelesCount : 0;
            if (allelesFreq[i] < maf) {
                maf = allelesFreq[i];
//                this.setMafAllele((i == 0) ? this.getRefAllele() : this.getAltAlleles()[i - 1]);
                this.setMafAllele((i == 0) ? this.getRefAllele() : this.getAltAllele());
            }
        }

        for (int i = 0; i < this.getNumAlleles() * this.getNumAlleles(); i++) {
            genotypesFreq[i] = (totalGenotypesCount > 0) ? this.getGenotypesCount()[i] / (float) totalGenotypesCount : 0;
        }

        // MGF
        for (int i = 0; i < this.getNumAlleles(); i++) {
            for (int j = 0; j < this.getNumAlleles(); j++) {
                int idx1 = i * this.getNumAlleles() + j;
                if (i == j) {
                    currentGtFreq = genotypesFreq[idx1];
                } else {
                    int idx2 = j * this.getNumAlleles() + i;
                    currentGtFreq = genotypesFreq[idx1] + genotypesFreq[idx2];
                }

                if (currentGtFreq < mgf) {
//                    String firstAllele = (i == 0) ? this.getRefAllele() : this.getAltAlleles()[i - 1];
//                    String secondAllele = (j == 0) ? this.getRefAllele() : this.getAltAlleles()[j - 1];
                    String firstAllele = (i == 0) ? this.getRefAllele() : this.getAltAllele();
                    String secondAllele = (j == 0) ? this.getRefAllele() : this.getAltAllele();
                    mgfGenotype = firstAllele + "/" + secondAllele;
                    mgf = currentGtFreq;

                }
            }
        }

        this.setMaf(maf);
        this.setMgf(mgf);
        this.setMgfGenotype(mgfGenotype);

        this.setAllelesFreq(allelesFreq);
        this.setGenotypesFreq(genotypesFreq);
    }

    private void calculateTransitionsAndTransversions(String reference, String alternate) {
        int numTransitions = 0, numTranversions = 0;

        if (reference.length() == 1 && alternate.length() == 1) {
            switch (reference.toUpperCase()) {
                case "C":
                    if (alternate.equalsIgnoreCase("T")) {
                        numTransitions++;
                    } else {
                        numTranversions++;
                    }
                    break;
                case "T":
                    if (alternate.equalsIgnoreCase("C")) {
                        numTransitions++;
                    } else {
                        numTranversions++;
                    }
                    break;
                case "A":
                    if (alternate.equalsIgnoreCase("G")) {
                        numTransitions++;
                    } else {
                        numTranversions++;
                    }
                    break;
                case "G":
                    if (alternate.equalsIgnoreCase("A")) {
                        numTransitions++;
                    } else {
                        numTranversions++;
                    }
                    break;
            }
        }

        this.setTransitionsCount(numTransitions);
        this.setTransversionsCount(numTranversions);
    }

}
