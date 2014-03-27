package org.opencb.biodata.models.variant.stats;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.Variant.VariantType;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantStats {
    private String chromosome;
    private long position;
    private String refAllele;
    private String[] altAlleles;
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
    private boolean pass;
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
        this.numAlleles = 1 + this.altAlleles.length;
        
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


    @Override
    public String toString() {
        return "VariantStats{" +
                "chromosome='" + chromosome + '\'' +
                ", position=" + position +
                ", refAllele='" + refAllele + '\'' +
                ", altAlleles=" + Arrays.toString(altAlleles) +
                ", mafAllele='" + mafAllele + '\'' +
                ", mgfAllele='" + mgfGenotype + '\'' +
                ", numAlleles=" + numAlleles +
                ", allelesCount=" + Arrays.toString(allelesCount) +
                ", genotypesCount=" + Arrays.toString(genotypesCount) +
                ", genotypes=" + genotypes +
                ", allelesFreq=" + Arrays.toString(allelesFreq) +
                ", genotypesFreq=" + Arrays.toString(genotypesFreq) +
                ", maf=" + maf +
                ", mgf=" + mgf +
                ", missingAlleles=" + missingAlleles +
                ", missingGenotypes=" + missingGenotypes +
                ", mendelinanErrors=" + mendelianErrors +
                ", isIndel=" + isIndel +
                ", casesPercentDominant=" + casesPercentDominant +
                ", controlsPercentDominant=" + controlsPercentDominant +
                ", casesPercentRecessive=" + casesPercentRecessive +
                ", controlsPercentRecessive=" + controlsPercentRecessive +
                ", transitionsCount=" + transitionsCount +
                ", transversionsCount=" + transversionsCount +
                '}';
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

    public String[] getAltAlleles() {
        return altAlleles;
    }

    public void setAltAlleles(String[] altAlleles) {
        this.altAlleles = altAlleles;
    }

    public String getMafAllele() {
        return mafAllele;
    }

    public void setMafAllele(String mafAllele) {
        this.mafAllele = mafAllele;
    }

    public String getMgfAllele() {
        return mgfGenotype;
    }

    public void setMgfAllele(String mgfAllele) {
        this.mgfGenotype = mgfAllele;
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

    public int getMendelinanErrors() {
        return mendelianErrors;
    }

    public void setMendelinanErrors(int mendelinanErrors) {
        this.mendelianErrors = mendelinanErrors;
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

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
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
    
}
