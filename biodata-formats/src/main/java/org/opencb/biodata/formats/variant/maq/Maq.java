package org.opencb.biodata.formats.variant.maq;

public class Maq {

    private String chromosome;
    private int position;
    private String referenceGenotype;
    private String consensusGenotype;
    private int consensusQuality;
    private double sequencingDepth;
    private double averageNumberOfReads;
    private double highestMappingQuality;
    private double minimumConsensusQuality;
    private String secondBestBase;
    private double secondBestBaseLogLikelihoodRatio;
    private double thirdBestBaseLogLikelihoodRatio;
    private String thirdBestBase;

    /**
     * @param chromosome
     * @param position
     * @param referenceGenotype
     * @param consensusGenotype
     * @param consensusQuality
     * @param sequencingDepth
     * @param averageNumberOfReads
     * @param highestMappingQuality
     * @param minimumConsensusQuality
     * @param secondBestBase
     * @param secondBestBaseLogLikelihoodRatio
     *
     * @param thirdBestBaseLogLikelihoodRatio
     *
     * @param thirdBestBase
     */
    public Maq(String chromosome, int position, String referenceGenotype, String consensusGenotype, int consensusQuality, double sequencingDepth, double averageNumberOfReads, double highestMappingQuality, double minimumConsensusQuality, String secondBestBase, double secondBestBaseLogLikelihoodRatio, double thirdBestBaseLogLikelihoodRatio, String thirdBestBase) {
        this.chromosome = chromosome;
        this.position = position;
        this.referenceGenotype = referenceGenotype;
        this.consensusGenotype = consensusGenotype;
        this.consensusQuality = consensusQuality;
        this.sequencingDepth = sequencingDepth;
        this.averageNumberOfReads = averageNumberOfReads;
        this.highestMappingQuality = highestMappingQuality;
        this.minimumConsensusQuality = minimumConsensusQuality;
        this.secondBestBase = secondBestBase;
        this.secondBestBaseLogLikelihoodRatio = secondBestBaseLogLikelihoodRatio;
        this.thirdBestBaseLogLikelihoodRatio = thirdBestBaseLogLikelihoodRatio;
        this.thirdBestBase = thirdBestBase;
    }

    /**
     * @param chromosome
     * @param position
     * @param referenceGenotype
     * @param consensusGenotype
     * @param consensusQuality
     * @param sequencingDepth
     * @param averageNumberOfReads
     * @param highestMappingQuality
     * @param minimumConsensusQuality
     * @param secondBestBase
     * @param secondBestBaseLogLikelihoodRatio
     *
     * @param thirdBestBaseLogLikelihoodRatio
     *
     * @param thirdBestBase
     */
    public Maq(String chromosome, Integer position, String referenceGenotype, String consensusGenotype, Integer consensusQuality, Double sequencingDepth, Double averageNumberOfReads, Double highestMappingQuality, Double minimumConsensusQuality, String secondBestBase, Double secondBestBaseLogLikelihoodRatio, Double thirdBestBaseLogLikelihoodRatio, String thirdBestBase) {
        this.chromosome = chromosome;
        this.position = position;
        this.referenceGenotype = referenceGenotype;
        this.consensusGenotype = consensusGenotype;
        this.consensusQuality = consensusQuality;
        this.sequencingDepth = sequencingDepth;
        this.averageNumberOfReads = averageNumberOfReads;
        this.highestMappingQuality = highestMappingQuality;
        this.minimumConsensusQuality = minimumConsensusQuality;
        this.secondBestBase = secondBestBase;
        this.secondBestBaseLogLikelihoodRatio = secondBestBaseLogLikelihoodRatio;
        this.thirdBestBaseLogLikelihoodRatio = thirdBestBaseLogLikelihoodRatio;
        this.thirdBestBase = thirdBestBase;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(chromosome).append("\t");
        builder.append(position).append("\t");
        builder.append(referenceGenotype).append("\t");
        builder.append(consensusGenotype).append("\t");
        builder.append(consensusQuality).append("\t");
        builder.append(sequencingDepth).append("\t");
        builder.append(averageNumberOfReads).append("\t");
        builder.append(highestMappingQuality).append("\t");
        builder.append(minimumConsensusQuality).append("\t");
        builder.append(secondBestBase).append("\t");
        builder.append(secondBestBaseLogLikelihoodRatio).append("\t");
        builder.append(thirdBestBaseLogLikelihoodRatio).append("\t");
        builder.append(thirdBestBase);
        return builder.toString();
    }

    /**
     * @return the chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }


    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }


    /**
     * @return the referenceGenotype
     */
    public String getReferenceGenotype() {
        return referenceGenotype;
    }

    /**
     * @param referenceGenotype the referenceGenotype to set
     */
    public void setReferenceGenotype(String referenceGenotype) {
        this.referenceGenotype = referenceGenotype;
    }


    /**
     * @return the consensusGenotype
     */
    public String getConsensusGenotype() {
        return consensusGenotype;
    }

    /**
     * @param consensusGenotype the consensusGenotype to set
     */
    public void setConsensusGenotype(String consensusGenotype) {
        this.consensusGenotype = consensusGenotype;
    }


    /**
     * @return the consensusQuality
     */
    public int getConsensusQuality() {
        return consensusQuality;
    }

    /**
     * @param consensusQuality the consensusQuality to set
     */
    public void setConsensusQuality(int consensusQuality) {
        this.consensusQuality = consensusQuality;
    }


    /**
     * @return the sequencingDepth
     */
    public double getSequencingDepth() {
        return sequencingDepth;
    }

    /**
     * @param sequencingDepth the sequencingDepth to set
     */
    public void setSequencingDepth(double sequencingDepth) {
        this.sequencingDepth = sequencingDepth;
    }


    /**
     * @return the averageNumberOfReads
     */
    public double getAverageNumberOfReads() {
        return averageNumberOfReads;
    }

    /**
     * @param averageNumberOfReads the averageNumberOfReads to set
     */
    public void setAverageNumberOfReads(double averageNumberOfReads) {
        this.averageNumberOfReads = averageNumberOfReads;
    }


    /**
     * @return the highestMappingQuality
     */
    public double getHighestMappingQuality() {
        return highestMappingQuality;
    }

    /**
     * @param highestMappingQuality the highestMappingQuality to set
     */
    public void setHighestMappingQuality(double highestMappingQuality) {
        this.highestMappingQuality = highestMappingQuality;
    }


    /**
     * @return the minimumConsensusQuality
     */
    public double getMinimumConsensusQuality() {
        return minimumConsensusQuality;
    }

    /**
     * @param minimumConsensusQuality the minimumConsensusQuality to set
     */
    public void setMinimumConsensusQuality(double minimumConsensusQuality) {
        this.minimumConsensusQuality = minimumConsensusQuality;
    }


    /**
     * @return the secondBestBase
     */
    public String getSecondBestBase() {
        return secondBestBase;
    }

    /**
     * @param secondBestBase the secondBestBase to set
     */
    public void setSecondBestBase(String secondBestBase) {
        this.secondBestBase = secondBestBase;
    }


    /**
     * @return the secondBestBaseLogLikelihoodRatio
     */
    public double getSecondBestBaseLogLikelihoodRatio() {
        return secondBestBaseLogLikelihoodRatio;
    }

    /**
     * @param secondBestBaseLogLikelihoodRatio
     *         the secondBestBaseLogLikelihoodRatio to set
     */
    public void setSecondBestBaseLogLikelihoodRatio(
            double secondBestBaseLogLikelihoodRatio) {
        this.secondBestBaseLogLikelihoodRatio = secondBestBaseLogLikelihoodRatio;
    }


    /**
     * @return the thirdBestBaseLogLikelihoodRatio
     */
    public double getThirdBestBaseLogLikelihoodRatio() {
        return thirdBestBaseLogLikelihoodRatio;
    }

    /**
     * @param thirdBestBaseLogLikelihoodRatio
     *         the thirdBestBaseLogLikelihoodRatio to set
     */
    public void setThirdBestBaseLogLikelihoodRatio(
            double thirdBestBaseLogLikelihoodRatio) {
        this.thirdBestBaseLogLikelihoodRatio = thirdBestBaseLogLikelihoodRatio;
    }


    /**
     * @return the thirdBestBase
     */
    public String getThirdBestBase() {
        return thirdBestBase;
    }

    /**
     * @param thirdBestBase the thirdBestBase to set
     */
    public void setThirdBestBase(String thirdBestBase) {
        this.thirdBestBase = thirdBestBase;
    }

}
