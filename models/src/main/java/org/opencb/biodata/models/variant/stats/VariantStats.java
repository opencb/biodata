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
 */
public class VariantStats {

    private String chromosome;
    private long position;
    private String refAllele;
    @Deprecated
    private String[] altAlleles;
    private String altAllele;
    private String id;
    private String mafAllele;
    private String mgfGenotype;
    private int numAlleles;
    private int[] allelesCount;
    private int[] genotypesCount;
    private List<Genotype> genotypes;
    private float[] allelesFreq;
    private float[] genotypesFreq;
    private float maf;
    private float mgf;
    private int missingAlleles;
    private int missingGenotypes;
    private int mendelianErrors;
    private boolean isIndel;
    private boolean isSNP;
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
        this.isIndel = false;
        this.genotypes = new ArrayList<>((int) Math.pow(this.numAlleles, 2));
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.hw = new VariantHardyWeinbergStats();
    }

    public VariantStats(Variant variant) {
        this.chromosome = variant.getChromosome();
        this.position = variant.getStart();
        this.refAllele = variant.getReference();
        this.altAlleles = new String[] { variant.getAlternate() };
        this.altAllele = variant.getAlternate();
        this.numAlleles = 2;
        this.id = variant.getId();
        this.isIndel = variant.getType() == VariantType.INDEL;
        this.isSNP = variant.getType() == VariantType.SNV;

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
        this.isIndel = isIndel;

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

    public String getRefAlleles() {
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
        return isIndel;
    }

    public void setIndel(boolean indel) {
        this.isIndel = indel;
    }

    public boolean isSNP() {
        return isSNP;
    }

    public void setSNP(boolean SNP) {
        isSNP = SNP;
    }

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
                + ", altAlleles=" + Arrays.toString(altAlleles)
                + ", mafAllele='" + mafAllele + '\''
                + ", mgfAllele='" + mgfGenotype + '\''
                + ", numAlleles=" + numAlleles
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
                + ", isIndel=" + isIndel
                + ", casesPercentDominant=" + casesPercentDominant
                + ", controlsPercentDominant=" + controlsPercentDominant
                + ", casesPercentRecessive=" + casesPercentRecessive
                + ", controlsPercentRecessive=" + controlsPercentRecessive
                + ", transitionsCount=" + transitionsCount
                + ", transversionsCount=" + transversionsCount
                + '}';
    }

    public static VariantStats calculate(Variant variant, ArchivedVariantFile file, Pedigree pedigree) {
        int genotypeCurrentPos;
        int totalAllelesCount = 0;
        int totalGenotypesCount = 0;

        float controlsDominant = 0;
        float casesDominant = 0;
        float controlsRecessive = 0;
        float casesRecessive = 0;

        VariantStats vcfStat = new VariantStats(variant);
        vcfStat.setNumSamples(file.getSampleNames().size());

//                int[] allelesCount = new int[vcfStat.getNumAlleles()];
//                int[] genotypesCount = new int[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];
        for (Map.Entry<String, Map<String, String>> sample : file.getSamplesData().entrySet()) {
            String sampleName = sample.getKey();
            Genotype g = new Genotype(sample.getValue().get("GT"));
            vcfStat.addGenotype(g);

            // Check missing alleles and genotypes
            switch (g.getCode()) {
                case ALLELES_OK:
                    // Both alleles set
                    genotypeCurrentPos = g.getAllele1() * (vcfStat.getNumAlleles()) + g.getAllele2();

                    vcfStat.allelesCount[g.getAllele1()]++;
                    vcfStat.allelesCount[g.getAllele2()]++;
                    vcfStat.genotypesCount[genotypeCurrentPos]++;

                    totalAllelesCount += 2;
                    totalGenotypesCount++;

                    // Counting genotypes for Hardy-Weinberg (all phenotypes)
                    if (g.isAllele1Ref() && g.isAllele2Ref()) { // 0|0
                        vcfStat.getHw().incN_AA();
                    } else if ((g.isAllele1Ref() && g.getAllele2() == 1) || (g.getAllele1() == 1 && g.isAllele2Ref())) {  // 0|1, 1|0
                        vcfStat.getHw().incN_Aa();

                    } else if (g.getAllele1() == 1 && g.getAllele2() == 1) {
                        vcfStat.getHw().incN_aa();
                    }

                    break;
                case HAPLOID:
                    // Haploid (chromosome X/Y)
                    try {
                        vcfStat.allelesCount[g.getAllele1()]++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("vcfRecord = " + variant);
                        System.out.println("g = " + g);
                    }
                    totalAllelesCount++;
                    break;
                default:
                    // Missing genotype (one or both alleles missing)
                    vcfStat.setMissingGenotypes(vcfStat.getMissingGenotypes() + 1);
                    if (g.getAllele1() == null) {
                        vcfStat.setMissingAlleles(vcfStat.getMissingAlleles() + 1);
                    } else {
                        vcfStat.allelesCount[g.getAllele1()]++;
                        totalAllelesCount++;
                    }

                    if (g.getAllele2() == null) {
                        vcfStat.setMissingAlleles(vcfStat.getMissingAlleles() + 1);
                    } else {
                        vcfStat.allelesCount[g.getAllele2()]++;
                        totalAllelesCount++;
                    }
                    break;

            }

            // Include statistics that depend on pedigree information
            if (pedigree != null) {
                if (g.getCode() == AllelesCode.ALLELES_OK || g.getCode() == AllelesCode.HAPLOID) {
                    Individual ind = pedigree.getIndividual(sampleName);
//                            if (isMendelianError(ind, g, variant, file)) {
//                                vcfStat.setMendelinanErrors(vcfStat.getMendelinanErrors() + 1);
//
//                            }
                    if (g.getCode() == AllelesCode.ALLELES_OK) {
                        // Check inheritance models
                        if (ind.getCondition() == Condition.UNAFFECTED) {
                            if (g.isAllele1Ref() && g.isAllele2Ref()) { // 0|0
                                controlsDominant++;
                                controlsRecessive++;

                            } else if ((g.isAllele1Ref() && !g.isAllele2Ref()) || (!g.isAllele1Ref() || g.isAllele2Ref())) { // 0|1 or 1|0
                                controlsRecessive++;

                            }
                        } else if (ind.getCondition() == Condition.AFFECTED) {
                            if (!g.isAllele1Ref() && !g.isAllele2Ref() && g.getAllele1().equals(g.getAllele2())) {// 1|1, 2|2, and so on
                                casesRecessive++;
                                casesDominant++;
                            } else if (!g.isAllele1Ref() || !g.isAllele2Ref()) { // 0|1, 1|0, 1|2, 2|1, 1|3, and so on
                                casesDominant++;

                            }
                        }

                    }

                }
            }

        }  // Finish all samples loop

        // Calculate MAF and MGF
        vcfStat.calculateAlleleAndGenotypeFrequencies(vcfStat, totalAllelesCount, totalGenotypesCount);

        // Calculate Hardy-Weinberg statistic
        vcfStat.getHw().calculate();

        // Indels
//        /*
//         * 3 possibilities for being an INDEL:
//         * - The value of the ALT field is <DEL> or <INS>
//         * - The REF allele is not . but the ALT is
//         * - The REF allele is . but the ALT is not
//         * - The REF field length is different than the ALT field length
//         */
//        if ((!vcfStat.getRefAlleles().equals(".") && variant.getAlternate().equals(".")) ||
//                (variant.getAlternate().equals(".") && !vcfStat.getRefAlleles().equals(".")) ||
//                (variant.getAlternate().equals("<INS>")) ||
//                (variant.getAlternate().equals("<DEL>")) ||
//                variant.getReference().length() != variant.getAlternate().length()) {
//            vcfStat.setIndel(true);
//        } else {
//            vcfStat.setIndel(false);
//        }
//
//        if (variant.getId() != null && !variant.getId().equals(".")) {
//            vcfStat.setSNP(true);
//        }
        
        // Transitions and transversions
        vcfStat.calculateTransitionsAndTransversions(vcfStat, variant.getReference(), variant.getAlternate());

        // Update variables finally used to update file_stats_t structure
        if (file.hasAttribute("FILTER") && "PASS".equalsIgnoreCase(file.getAttribute("FILTER"))) {
            vcfStat.setPassedFilters(true);
        }

        if (file.hasAttribute("QUAL") && !(".").equals(file.getAttribute("QUAL"))) {
            float qualAux = Float.valueOf(file.getAttribute("QUAL"));
            if (qualAux >= 0) {
                vcfStat.setQual(qualAux);
            }
        }

        // Once all samples have been traversed, calculate % that follow inheritance model
        controlsDominant = controlsDominant * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
        casesDominant = casesDominant * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
        controlsRecessive = controlsRecessive * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());
        casesRecessive = casesRecessive * 100 / (vcfStat.getNumSamples() - vcfStat.getMissingGenotypes());

        vcfStat.setCasesPercentDominant(casesDominant);
        vcfStat.setControlsPercentDominant(controlsDominant);
        vcfStat.setCasesPercentRecessive(casesRecessive);
        vcfStat.setControlsPercentRecessive(controlsRecessive);

        return vcfStat;
    }

    public static void calculateStatsForVariantsList(List<Variant> variants, Pedigree ped) {
        for (Variant variant : variants) {
            for (ArchivedVariantFile file : variant.getFiles().values()) {
                VariantStats vcfStat = calculate(variant, file, ped);
                file.setStats(vcfStat); // TODO Correct?
            }
        }
    }

    private void calculateAlleleAndGenotypeFrequencies(VariantStats vcfStat, int totalAllelesCount, int totalGenotypesCount) {
        String mgfGenotype = "";

        float maf = Float.MAX_VALUE;
        float mgf = Float.MAX_VALUE;
        float currentGtFreq;

        float[] allelesFreq = new float[vcfStat.getNumAlleles()];
        float[] genotypesFreq = new float[vcfStat.getNumAlleles() * vcfStat.getNumAlleles()];

        // MAF
        for (int i = 0; i < vcfStat.getNumAlleles(); i++) {
            allelesFreq[i] = (totalAllelesCount > 0) ? vcfStat.getAllelesCount()[i] / (float) totalAllelesCount : 0;
            if (allelesFreq[i] < maf) {
                maf = allelesFreq[i];
                vcfStat.setMafAllele((i == 0) ? vcfStat.getRefAlleles() : vcfStat.getAltAlleles()[i - 1]);
            }
        }

        for (int i = 0; i < vcfStat.getNumAlleles() * vcfStat.getNumAlleles(); i++) {
            genotypesFreq[i] = (totalGenotypesCount > 0) ? vcfStat.getGenotypesCount()[i] / (float) totalGenotypesCount : 0;
        }

        // MGF
        for (int i = 0; i < vcfStat.getNumAlleles(); i++) {
            for (int j = 0; j < vcfStat.getNumAlleles(); j++) {
                int idx1 = i * vcfStat.getNumAlleles() + j;
                if (i == j) {
                    currentGtFreq = genotypesFreq[idx1];
                } else {
                    int idx2 = j * vcfStat.getNumAlleles() + i;
                    currentGtFreq = genotypesFreq[idx1] + genotypesFreq[idx2];
                }

                if (currentGtFreq < mgf) {
                    String firstAllele = (i == 0) ? vcfStat.getRefAlleles() : vcfStat.getAltAlleles()[i - 1];
                    String secondAllele = (j == 0) ? vcfStat.getRefAlleles() : vcfStat.getAltAlleles()[j - 1];
                    mgfGenotype = firstAllele + "/" + secondAllele;
                    mgf = currentGtFreq;

                }
            }
        }

        vcfStat.setMaf(maf);
        vcfStat.setMgf(mgf);
        vcfStat.setMgfGenotype(mgfGenotype);

        vcfStat.setAllelesFreq(allelesFreq);
        vcfStat.setGenotypesFreq(genotypesFreq);
    }

    private void calculateTransitionsAndTransversions(VariantStats vcfStat, String reference, String alternate) {
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

        vcfStat.setTransitionsCount(numTransitions);
        vcfStat.setTransversionsCount(numTranversions);
    }

}
