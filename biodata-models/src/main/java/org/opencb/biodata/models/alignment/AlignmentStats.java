package org.opencb.biodata.models.alignment;

import java.util.List;

public class AlignmentStats {
    private String fileId;
    private List<String> sampleIds;
    private int rawTotalSequences;
    private int filteredSequences;
    private int sequences;
    private int isSorted;
    private int firstFragments;
    private int lastFragments;
    private int readsMapped;
    private int readsMappedAndPaired;
    private int readsUnmapped;
    private int readsProperlyPaired;
    private int readsPaired;
    private int readsDuplicated;
    private int readsMq0;
    private int readsQcFailed;
    private int nonPrimaryAlignments;
    private int totalLength;
    private int totalFirstFragmentLength;
    private int totalLastFragmentLength;
    private int basesMapped;
    private int basesMappedCigar;
    private int basesTrimmed;
    private int basesDuplicated;
    private int mismatches;
    private double errorRate;
    private double averageLength;
    private double averageFirstFragmentLength;
    private double averageLastFragmentLength;
    private int maximumLength;
    private int maximumFirstFragmentLength;
    private int maximumLastFragmentLength;
    private double averageQuality;
    private double insertSizeAverage;
    private double insertSizeStandardDeviation;
    private int inwardOrientedPairs;
    private int outwardOrientedPairs;
    private int pairsWithOtherOrientation;
    private int pairsOnDifferentChromosomes;
    private double percentageOfProperlyPairedReads;

    public AlignmentStats() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlignmentStats{");
        sb.append("fileId='").append(fileId).append('\'');
        sb.append(", sampleIds=").append(sampleIds);
        sb.append(", rawTotalSequences=").append(rawTotalSequences);
        sb.append(", filteredSequences=").append(filteredSequences);
        sb.append(", sequences=").append(sequences);
        sb.append(", isSorted=").append(isSorted);
        sb.append(", firstFragments=").append(firstFragments);
        sb.append(", lastFragments=").append(lastFragments);
        sb.append(", readsMapped=").append(readsMapped);
        sb.append(", readsMappedAndPaired=").append(readsMappedAndPaired);
        sb.append(", readsUnmapped=").append(readsUnmapped);
        sb.append(", readsProperlyPaired=").append(readsProperlyPaired);
        sb.append(", readsPaired=").append(readsPaired);
        sb.append(", readsDuplicated=").append(readsDuplicated);
        sb.append(", readsMq0=").append(readsMq0);
        sb.append(", readsQcFailed=").append(readsQcFailed);
        sb.append(", nonPrimaryAlignments=").append(nonPrimaryAlignments);
        sb.append(", totalLength=").append(totalLength);
        sb.append(", totalFirstFragmentLength=").append(totalFirstFragmentLength);
        sb.append(", totalLastFragmentLength=").append(totalLastFragmentLength);
        sb.append(", basesMapped=").append(basesMapped);
        sb.append(", basesMappedCigar=").append(basesMappedCigar);
        sb.append(", basesTrimmed=").append(basesTrimmed);
        sb.append(", basesDuplicated=").append(basesDuplicated);
        sb.append(", mismatches=").append(mismatches);
        sb.append(", errorRate=").append(errorRate);
        sb.append(", averageLength=").append(averageLength);
        sb.append(", averageFirstFragmentLength=").append(averageFirstFragmentLength);
        sb.append(", averageLastFragmentLength=").append(averageLastFragmentLength);
        sb.append(", maximumLength=").append(maximumLength);
        sb.append(", maximumFirstFragmentLength=").append(maximumFirstFragmentLength);
        sb.append(", maximumLastFragmentLength=").append(maximumLastFragmentLength);
        sb.append(", averageQuality=").append(averageQuality);
        sb.append(", insertSizeAverage=").append(insertSizeAverage);
        sb.append(", insertSizeStandardDeviation=").append(insertSizeStandardDeviation);
        sb.append(", inwardOrientedPairs=").append(inwardOrientedPairs);
        sb.append(", outwardOrientedPairs=").append(outwardOrientedPairs);
        sb.append(", pairsWithOtherOrientation=").append(pairsWithOtherOrientation);
        sb.append(", pairsOnDifferentChromosomes=").append(pairsOnDifferentChromosomes);
        sb.append(", percentageOfProperlyPairedReads=").append(percentageOfProperlyPairedReads);
        sb.append('}');
        return sb.toString();
    }

    public String getFileId() {
        return fileId;
    }

    public AlignmentStats setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public List<String> getSampleIds() {
        return sampleIds;
    }

    public AlignmentStats setSampleIds(List<String> sampleIds) {
        this.sampleIds = sampleIds;
        return this;
    }

    public int getRawTotalSequences() {
        return rawTotalSequences;
    }

    public AlignmentStats setRawTotalSequences(int rawTotalSequences) {
        this.rawTotalSequences = rawTotalSequences;
        return this;
    }

    public int getFilteredSequences() {
        return filteredSequences;
    }

    public AlignmentStats setFilteredSequences(int filteredSequences) {
        this.filteredSequences = filteredSequences;
        return this;
    }

    public int getSequences() {
        return sequences;
    }

    public AlignmentStats setSequences(int sequences) {
        this.sequences = sequences;
        return this;
    }

    public int getIsSorted() {
        return isSorted;
    }

    public AlignmentStats setIsSorted(int isSorted) {
        this.isSorted = isSorted;
        return this;
    }

    public int getFirstFragments() {
        return firstFragments;
    }

    public AlignmentStats setFirstFragments(int firstFragments) {
        this.firstFragments = firstFragments;
        return this;
    }

    public int getLastFragments() {
        return lastFragments;
    }

    public AlignmentStats setLastFragments(int lastFragments) {
        this.lastFragments = lastFragments;
        return this;
    }

    public int getReadsMapped() {
        return readsMapped;
    }

    public AlignmentStats setReadsMapped(int readsMapped) {
        this.readsMapped = readsMapped;
        return this;
    }

    public int getReadsMappedAndPaired() {
        return readsMappedAndPaired;
    }

    public AlignmentStats setReadsMappedAndPaired(int readsMappedAndPaired) {
        this.readsMappedAndPaired = readsMappedAndPaired;
        return this;
    }

    public int getReadsUnmapped() {
        return readsUnmapped;
    }

    public AlignmentStats setReadsUnmapped(int readsUnmapped) {
        this.readsUnmapped = readsUnmapped;
        return this;
    }

    public int getReadsProperlyPaired() {
        return readsProperlyPaired;
    }

    public AlignmentStats setReadsProperlyPaired(int readsProperlyPaired) {
        this.readsProperlyPaired = readsProperlyPaired;
        return this;
    }

    public int getReadsPaired() {
        return readsPaired;
    }

    public AlignmentStats setReadsPaired(int readsPaired) {
        this.readsPaired = readsPaired;
        return this;
    }

    public int getReadsDuplicated() {
        return readsDuplicated;
    }

    public AlignmentStats setReadsDuplicated(int readsDuplicated) {
        this.readsDuplicated = readsDuplicated;
        return this;
    }

    public int getReadsMq0() {
        return readsMq0;
    }

    public AlignmentStats setReadsMq0(int readsMq0) {
        this.readsMq0 = readsMq0;
        return this;
    }

    public int getReadsQcFailed() {
        return readsQcFailed;
    }

    public AlignmentStats setReadsQcFailed(int readsQcFailed) {
        this.readsQcFailed = readsQcFailed;
        return this;
    }

    public int getNonPrimaryAlignments() {
        return nonPrimaryAlignments;
    }

    public AlignmentStats setNonPrimaryAlignments(int nonPrimaryAlignments) {
        this.nonPrimaryAlignments = nonPrimaryAlignments;
        return this;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public AlignmentStats setTotalLength(int totalLength) {
        this.totalLength = totalLength;
        return this;
    }

    public int getTotalFirstFragmentLength() {
        return totalFirstFragmentLength;
    }

    public AlignmentStats setTotalFirstFragmentLength(int totalFirstFragmentLength) {
        this.totalFirstFragmentLength = totalFirstFragmentLength;
        return this;
    }

    public int getTotalLastFragmentLength() {
        return totalLastFragmentLength;
    }

    public AlignmentStats setTotalLastFragmentLength(int totalLastFragmentLength) {
        this.totalLastFragmentLength = totalLastFragmentLength;
        return this;
    }

    public int getBasesMapped() {
        return basesMapped;
    }

    public AlignmentStats setBasesMapped(int basesMapped) {
        this.basesMapped = basesMapped;
        return this;
    }

    public int getBasesMappedCigar() {
        return basesMappedCigar;
    }

    public AlignmentStats setBasesMappedCigar(int basesMappedCigar) {
        this.basesMappedCigar = basesMappedCigar;
        return this;
    }

    public int getBasesTrimmed() {
        return basesTrimmed;
    }

    public AlignmentStats setBasesTrimmed(int basesTrimmed) {
        this.basesTrimmed = basesTrimmed;
        return this;
    }

    public int getBasesDuplicated() {
        return basesDuplicated;
    }

    public AlignmentStats setBasesDuplicated(int basesDuplicated) {
        this.basesDuplicated = basesDuplicated;
        return this;
    }

    public int getMismatches() {
        return mismatches;
    }

    public AlignmentStats setMismatches(int mismatches) {
        this.mismatches = mismatches;
        return this;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public AlignmentStats setErrorRate(double errorRate) {
        this.errorRate = errorRate;
        return this;
    }

    public double getAverageLength() {
        return averageLength;
    }

    public AlignmentStats setAverageLength(double averageLength) {
        this.averageLength = averageLength;
        return this;
    }

    public double getAverageFirstFragmentLength() {
        return averageFirstFragmentLength;
    }

    public AlignmentStats setAverageFirstFragmentLength(double averageFirstFragmentLength) {
        this.averageFirstFragmentLength = averageFirstFragmentLength;
        return this;
    }

    public double getAverageLastFragmentLength() {
        return averageLastFragmentLength;
    }

    public AlignmentStats setAverageLastFragmentLength(double averageLastFragmentLength) {
        this.averageLastFragmentLength = averageLastFragmentLength;
        return this;
    }

    public int getMaximumLength() {
        return maximumLength;
    }

    public AlignmentStats setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
        return this;
    }

    public int getMaximumFirstFragmentLength() {
        return maximumFirstFragmentLength;
    }

    public AlignmentStats setMaximumFirstFragmentLength(int maximumFirstFragmentLength) {
        this.maximumFirstFragmentLength = maximumFirstFragmentLength;
        return this;
    }

    public int getMaximumLastFragmentLength() {
        return maximumLastFragmentLength;
    }

    public AlignmentStats setMaximumLastFragmentLength(int maximumLastFragmentLength) {
        this.maximumLastFragmentLength = maximumLastFragmentLength;
        return this;
    }

    public double getAverageQuality() {
        return averageQuality;
    }

    public AlignmentStats setAverageQuality(double averageQuality) {
        this.averageQuality = averageQuality;
        return this;
    }

    public double getInsertSizeAverage() {
        return insertSizeAverage;
    }

    public AlignmentStats setInsertSizeAverage(double insertSizeAverage) {
        this.insertSizeAverage = insertSizeAverage;
        return this;
    }

    public double getInsertSizeStandardDeviation() {
        return insertSizeStandardDeviation;
    }

    public AlignmentStats setInsertSizeStandardDeviation(double insertSizeStandardDeviation) {
        this.insertSizeStandardDeviation = insertSizeStandardDeviation;
        return this;
    }

    public int getInwardOrientedPairs() {
        return inwardOrientedPairs;
    }

    public AlignmentStats setInwardOrientedPairs(int inwardOrientedPairs) {
        this.inwardOrientedPairs = inwardOrientedPairs;
        return this;
    }

    public int getOutwardOrientedPairs() {
        return outwardOrientedPairs;
    }

    public AlignmentStats setOutwardOrientedPairs(int outwardOrientedPairs) {
        this.outwardOrientedPairs = outwardOrientedPairs;
        return this;
    }

    public int getPairsWithOtherOrientation() {
        return pairsWithOtherOrientation;
    }

    public AlignmentStats setPairsWithOtherOrientation(int pairsWithOtherOrientation) {
        this.pairsWithOtherOrientation = pairsWithOtherOrientation;
        return this;
    }

    public int getPairsOnDifferentChromosomes() {
        return pairsOnDifferentChromosomes;
    }

    public AlignmentStats setPairsOnDifferentChromosomes(int pairsOnDifferentChromosomes) {
        this.pairsOnDifferentChromosomes = pairsOnDifferentChromosomes;
        return this;
    }

    public double getPercentageOfProperlyPairedReads() {
        return percentageOfProperlyPairedReads;
    }

    public AlignmentStats setPercentageOfProperlyPairedReads(double percentageOfProperlyPairedReads) {
        this.percentageOfProperlyPairedReads = percentageOfProperlyPairedReads;
        return this;
    }
}
