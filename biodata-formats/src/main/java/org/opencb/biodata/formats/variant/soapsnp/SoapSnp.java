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

package org.opencb.biodata.formats.variant.soapsnp;

public class SoapSnp {

    private String chromosome;
    private int start;
    private String referenceGenotype;
    private String consensusGenotype;
    private double consensusQualityScore;
    private String bestBaseQualityScore;
    private int bestBaseUniquelyMapped;
    private int bestBaseAllMapped;
    private String secondBestBaseQualityScore;
    private int secondBestBaseUniquelyMapped;
    private int secondBestBaseAllMapped;
    private double sequencingDepth;
    private double averageCopyNumber;
    private String isDbSnp;

    /**
     * @param chromosome
     * @param start
     * @param referenceGenotype
     * @param consensusGenotype
     * @param consensusQualityScore
     * @param bestBaseQualityScore
     * @param bestBaseUniquelyMapped
     * @param bestBaseAllMapped
     * @param secondBestBaseQualityScore
     * @param secondBestBaseUniquelyMapped
     * @param secondBestBaseAllMapped
     * @param sequencingDepth
     * @param averageCopyNumber
     * @param isDbSnp
     */
    public SoapSnp(String chromosome, int start, String referenceGenotype, String consensusGenotype, double consensusQualityScore, String bestBaseQualityScore, int bestBaseUniquelyMapped, int bestBaseAllMapped, String secondBestBaseQualityScore, int secondBestBaseUniquelyMapped, int secondBestBaseAllMapped, double sequencingDepth, double averageCopyNumber, String isDbSnp) {
        this.chromosome = chromosome;
        this.start = start;
        this.referenceGenotype = referenceGenotype;
        this.consensusGenotype = consensusGenotype;
        this.consensusQualityScore = consensusQualityScore;
        this.bestBaseQualityScore = bestBaseQualityScore;
        this.bestBaseUniquelyMapped = bestBaseUniquelyMapped;
        this.bestBaseAllMapped = bestBaseAllMapped;
        this.secondBestBaseQualityScore = secondBestBaseQualityScore;
        this.secondBestBaseUniquelyMapped = secondBestBaseUniquelyMapped;
        this.secondBestBaseAllMapped = secondBestBaseAllMapped;
        this.sequencingDepth = sequencingDepth;
        this.averageCopyNumber = averageCopyNumber;
        this.isDbSnp = isDbSnp;
    }

    /**
     * @param chromosome
     * @param start
     * @param referenceGenotype
     * @param consensusGenotype
     * @param consensusQualityScore
     * @param bestBaseQualityScore
     * @param bestBaseUniquelyMapped
     * @param bestBaseAllMapped
     * @param secondBestBaseQualityScore
     * @param secondBestBaseUniquelyMapped
     * @param secondBestBaseAllMapped
     * @param sequencingDepth
     * @param averageCopyNumber
     * @param isDbSnp
     */
    public SoapSnp(String chromosome, Integer start, String referenceGenotype, String consensusGenotype, Double consensusQualityScore, String bestBaseQualityScore, Integer bestBaseUniquelyMapped, Integer bestBaseAllMapped, String secondBestBaseQualityScore, Integer secondBestBaseUniquelyMapped, Integer secondBestBaseAllMapped, Double sequencingDepth, Double averageCopyNumber, String isDbSnp) {
        this.chromosome = chromosome;
        this.start = start;
        this.referenceGenotype = referenceGenotype;
        this.consensusGenotype = consensusGenotype;
        this.consensusQualityScore = consensusQualityScore;
        this.bestBaseQualityScore = bestBaseQualityScore;
        this.bestBaseUniquelyMapped = bestBaseUniquelyMapped;
        this.bestBaseAllMapped = bestBaseAllMapped;
        this.secondBestBaseQualityScore = secondBestBaseQualityScore;
        this.secondBestBaseUniquelyMapped = secondBestBaseUniquelyMapped;
        this.secondBestBaseAllMapped = secondBestBaseAllMapped;
        this.sequencingDepth = sequencingDepth;
        this.averageCopyNumber = averageCopyNumber;
        this.isDbSnp = isDbSnp;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(chromosome).append("\t");
        builder.append(start).append("\t");
        builder.append(referenceGenotype).append("\t");
        builder.append(consensusGenotype).append("\t");
        builder.append(consensusQualityScore).append("\t");
        builder.append(bestBaseQualityScore).append("\t");
        builder.append(bestBaseUniquelyMapped).append("\t");
        builder.append(bestBaseAllMapped).append("\t");
        builder.append(secondBestBaseQualityScore).append("\t");
        builder.append(secondBestBaseUniquelyMapped).append("\t");
        builder.append(secondBestBaseAllMapped).append("\t");
        builder.append(sequencingDepth).append("\t");
        builder.append(averageCopyNumber).append("\t");
        builder.append(isDbSnp);
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
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
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
     * @return the consensusQualityScore
     */
    public double getConsensusQualityScore() {
        return consensusQualityScore;
    }

    /**
     * @param consensusQualityScore the consensusQualityScore to set
     */
    public void setConsensusQualityScore(double consensusQualityScore) {
        this.consensusQualityScore = consensusQualityScore;
    }


    /**
     * @return the bestBaseQualityScore
     */
    public String getBestBaseQualityScore() {
        return bestBaseQualityScore;
    }

    /**
     * @param bestBaseQualityScore the bestBaseQualityScore to set
     */
    public void setBestBaseQualityScore(String bestBaseQualityScore) {
        this.bestBaseQualityScore = bestBaseQualityScore;
    }


    /**
     * @return the bestBaseUniquelyMapped
     */
    public int getBestBaseUniquelyMapped() {
        return bestBaseUniquelyMapped;
    }

    /**
     * @param bestBaseUniquelyMapped the bestBaseUniquelyMapped to set
     */
    public void setBestBaseUniquelyMapped(int bestBaseUniquelyMapped) {
        this.bestBaseUniquelyMapped = bestBaseUniquelyMapped;
    }


    /**
     * @return the bestBaseAllMapped
     */
    public int getBestBaseAllMapped() {
        return bestBaseAllMapped;
    }

    /**
     * @param bestBaseAllMapped the bestBaseAllMapped to set
     */
    public void setBestBaseAllMapped(int bestBaseAllMapped) {
        this.bestBaseAllMapped = bestBaseAllMapped;
    }


    /**
     * @return the secondBestBaseQualityScore
     */
    public String getSecondBestBaseQualityScore() {
        return secondBestBaseQualityScore;
    }

    /**
     * @param secondBestBaseQualityScore the secondBestBaseQualityScore to set
     */
    public void setSecondBestBaseQualityScore(String secondBestBaseQualityScore) {
        this.secondBestBaseQualityScore = secondBestBaseQualityScore;
    }


    /**
     * @return the secondBestBaseUniquelyMapped
     */
    public int getSecondBestBaseUniquelyMapped() {
        return secondBestBaseUniquelyMapped;
    }

    /**
     * @param secondBestBaseUniquelyMapped the secondBestBaseUniquelyMapped to set
     */
    public void setSecondBestBaseUniquelyMapped(int secondBestBaseUniquelyMapped) {
        this.secondBestBaseUniquelyMapped = secondBestBaseUniquelyMapped;
    }


    /**
     * @return the secondBestBaseAllMapped
     */
    public int getSecondBestBaseAllMapped() {
        return secondBestBaseAllMapped;
    }

    /**
     * @param secondBestBaseAllMapped the secondBestBaseAllMapped to set
     */
    public void setSecondBestBaseAllMapped(int secondBestBaseAllMapped) {
        this.secondBestBaseAllMapped = secondBestBaseAllMapped;
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
     * @return the averageCopyNumber
     */
    public double getAverageCopyNumber() {
        return averageCopyNumber;
    }

    /**
     * @param averageCopyNumber the averageCopyNumber to set
     */
    public void setAverageCopyNumber(double averageCopyNumber) {
        this.averageCopyNumber = averageCopyNumber;
    }


    /**
     * @return the isDbSnp
     */
    public String getIsDbSnp() {
        return isDbSnp;
    }

    /**
     * @param isDbSnp the isDbSnp to set
     */
    public void setIsDbSnp(String isDbSnp) {
        this.isDbSnp = isDbSnp;
    }

}
