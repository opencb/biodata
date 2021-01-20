package org.opencb.biodata.models.variant.stats;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class VariantSampleStats {
    private int numVariants;

    // Stats counters
    private Map<String, Integer> chromosomeCounter;
    private Map<String, Integer> consequenceTypeCounter;
    private Map<String, Integer> biotypeCounter;
    private Map<String, Integer> typeCounter;
    private Map<String, Integer> genotypeCounter;

    // ti/tv ratio
    private double tiTvRatio;

    // Heterozigosity, missingness scores
    private double heterozigosityScore;
    private double missingnessScore;

    // Most affected genes
    private List<Pair<String, Integer>> mostMutatedGenes;

    // Indel length
    private List<Integer> indelLength;

    // Loss of function genes
    private LoF lof;

    // Most affected genes
    private List<Pair<String, Integer>> mostFrequentVarTraits;

    // Mendelian error counters
    private Map<Integer, Integer> mendelianErrorCounters;

    // Relatedness scores (IBD/IBS scores)
    private Map<String, IdentityByDescent> relatednessScores;

    public VariantSampleStats() {
        chromosomeCounter = new HashMap<>();
        consequenceTypeCounter = new HashMap<>();
        biotypeCounter = new HashMap<>();
        typeCounter = new HashMap<>();
        genotypeCounter = new HashMap<>();

        indelLength = Arrays.asList(new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0});

        mendelianErrorCounters = new HashMap<>();
        relatednessScores = new HashMap<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariantSampleStats{");
        sb.append("numVariants=").append(numVariants);
        sb.append(", chromosomeCounter=").append(chromosomeCounter);
        sb.append(", consequenceTypeCounter=").append(consequenceTypeCounter);
        sb.append(", biotypeCounter=").append(biotypeCounter);
        sb.append(", typeCounter=").append(typeCounter);
        sb.append(", genotypeCounter=").append(genotypeCounter);
        sb.append(", tiTvRatio=").append(tiTvRatio);
        sb.append(", heterozigosityScore=").append(heterozigosityScore);
        sb.append(", missingnessScore=").append(missingnessScore);
        sb.append(", mostMutatedGenes=").append(mostMutatedGenes);
        sb.append(", indelLength=").append(indelLength);
        sb.append(", lof=").append(lof);
        sb.append(", mostFrequentVarTraits=").append(mostFrequentVarTraits);
        sb.append(", mendelianErrorCounters=").append(mendelianErrorCounters);
        sb.append(", relatednessScores=").append(relatednessScores);
        sb.append('}');
        return sb.toString();
    }

    public int getNumVariants() {
        return numVariants;
    }

    public VariantSampleStats setNumVariants(int numVariants) {
        this.numVariants = numVariants;
        return this;
    }

    public Map<String, Integer> getChromosomeCounter() {
        return chromosomeCounter;
    }

    public VariantSampleStats setChromosomeCounter(Map<String, Integer> chromosomeCounter) {
        this.chromosomeCounter = chromosomeCounter;
        return this;
    }

    public Map<String, Integer> getConsequenceTypeCounter() {
        return consequenceTypeCounter;
    }

    public VariantSampleStats setConsequenceTypeCounter(Map<String, Integer> consequenceTypeCounter) {
        this.consequenceTypeCounter = consequenceTypeCounter;
        return this;
    }

    public Map<String, Integer> getBiotypeCounter() {
        return biotypeCounter;
    }

    public VariantSampleStats setBiotypeCounter(Map<String, Integer> biotypeCounter) {
        this.biotypeCounter = biotypeCounter;
        return this;
    }

    public Map<String, Integer> getTypeCounter() {
        return typeCounter;
    }

    public VariantSampleStats setTypeCounter(Map<String, Integer> typeCounter) {
        this.typeCounter = typeCounter;
        return this;
    }

    public Map<String, Integer> getGenotypeCounter() {
        return genotypeCounter;
    }

    public VariantSampleStats setGenotypeCounter(Map<String, Integer> genotypeCounter) {
        this.genotypeCounter = genotypeCounter;
        return this;
    }

    public double getTiTvRatio() {
        return tiTvRatio;
    }

    public VariantSampleStats setTiTvRatio(double tiTvRatio) {
        this.tiTvRatio = tiTvRatio;
        return this;
    }

    public double getHeterozigosityScore() {
        return heterozigosityScore;
    }

    public VariantSampleStats setHeterozigosityScore(double heterozigosityScore) {
        this.heterozigosityScore = heterozigosityScore;
        return this;
    }

    public double getMissingnessScore() {
        return missingnessScore;
    }

    public VariantSampleStats setMissingnessScore(double missingnessScore) {
        this.missingnessScore = missingnessScore;
        return this;
    }

    public List<Pair<String, Integer>> getMostMutatedGenes() {
        return mostMutatedGenes;
    }

    public VariantSampleStats setMostMutatedGenes(List<Pair<String, Integer>> mostMutatedGenes) {
        this.mostMutatedGenes = mostMutatedGenes;
        return this;
    }

    public List<Pair<String, Integer>> getMostFrequentVarTraits() {
        return mostFrequentVarTraits;
    }

    public VariantSampleStats setMostFrequentVarTraits(List<Pair<String, Integer>> mostFrequentVarTraits) {
        this.mostFrequentVarTraits = mostFrequentVarTraits;
        return this;
    }

    public List<Integer> getIndelLength() {
        return indelLength;
    }

    public VariantSampleStats setIndelLength(List<Integer> indelLength) {
        this.indelLength = indelLength;
        return this;
    }

    public LoF getLof() {
        return lof;
    }

    public VariantSampleStats setLof(LoF lof) {
        this.lof = lof;
        return this;
    }

    public Map<Integer, Integer> getMendelianErrorCounters() {
        return mendelianErrorCounters;
    }

    public VariantSampleStats setMendelianErrorCounters(Map<Integer, Integer> mendelianErrorCounters) {
        this.mendelianErrorCounters = mendelianErrorCounters;
        return this;
    }

    public Map<String, IdentityByDescent> getRelatednessScores() {
        return relatednessScores;
    }

    public VariantSampleStats setRelatednessScores(Map<String, IdentityByDescent> relatednessScores) {
        this.relatednessScores = relatednessScores;
        return this;
    }
}
