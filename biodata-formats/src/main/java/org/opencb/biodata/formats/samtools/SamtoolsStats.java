package org.opencb.biodata.formats.samtools;

public class SamtoolsStats {
    
    private String file;
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
    private long totalLength;
    private long totalFirstFragmentLength;
    private long totalLastFragmentLength;
    private long basesMapped;
    private long basesMappedCigar;
    private long basesTrimmed;
    private long basesDuplicated;
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

    public SamtoolsStats() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SamtoolsStats{");
        sb.append("file='").append(file).append('\'');
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

    public String getFile() {
        return file;
    }

    public SamtoolsStats setFileId(String file) {
        this.file = file;
        return this;
    }

    public int getRawTotalSequences() {
        return rawTotalSequences;
    }

    public SamtoolsStats setRawTotalSequences(int rawTotalSequences) {
        this.rawTotalSequences = rawTotalSequences;
        return this;
    }

    public int getFilteredSequences() {
        return filteredSequences;
    }

    public SamtoolsStats setFilteredSequences(int filteredSequences) {
        this.filteredSequences = filteredSequences;
        return this;
    }

    public int getSequences() {
        return sequences;
    }

    public SamtoolsStats setSequences(int sequences) {
        this.sequences = sequences;
        return this;
    }

    public int getIsSorted() {
        return isSorted;
    }

    public SamtoolsStats setIsSorted(int isSorted) {
        this.isSorted = isSorted;
        return this;
    }

    public int getFirstFragments() {
        return firstFragments;
    }

    public SamtoolsStats setFirstFragments(int firstFragments) {
        this.firstFragments = firstFragments;
        return this;
    }

    public int getLastFragments() {
        return lastFragments;
    }

    public SamtoolsStats setLastFragments(int lastFragments) {
        this.lastFragments = lastFragments;
        return this;
    }

    public int getReadsMapped() {
        return readsMapped;
    }

    public SamtoolsStats setReadsMapped(int readsMapped) {
        this.readsMapped = readsMapped;
        return this;
    }

    public int getReadsMappedAndPaired() {
        return readsMappedAndPaired;
    }

    public SamtoolsStats setReadsMappedAndPaired(int readsMappedAndPaired) {
        this.readsMappedAndPaired = readsMappedAndPaired;
        return this;
    }

    public int getReadsUnmapped() {
        return readsUnmapped;
    }

    public SamtoolsStats setReadsUnmapped(int readsUnmapped) {
        this.readsUnmapped = readsUnmapped;
        return this;
    }

    public int getReadsProperlyPaired() {
        return readsProperlyPaired;
    }

    public SamtoolsStats setReadsProperlyPaired(int readsProperlyPaired) {
        this.readsProperlyPaired = readsProperlyPaired;
        return this;
    }

    public int getReadsPaired() {
        return readsPaired;
    }

    public SamtoolsStats setReadsPaired(int readsPaired) {
        this.readsPaired = readsPaired;
        return this;
    }

    public int getReadsDuplicated() {
        return readsDuplicated;
    }

    public SamtoolsStats setReadsDuplicated(int readsDuplicated) {
        this.readsDuplicated = readsDuplicated;
        return this;
    }

    public int getReadsMq0() {
        return readsMq0;
    }

    public SamtoolsStats setReadsMq0(int readsMq0) {
        this.readsMq0 = readsMq0;
        return this;
    }

    public int getReadsQcFailed() {
        return readsQcFailed;
    }

    public SamtoolsStats setReadsQcFailed(int readsQcFailed) {
        this.readsQcFailed = readsQcFailed;
        return this;
    }

    public int getNonPrimaryAlignments() {
        return nonPrimaryAlignments;
    }

    public SamtoolsStats setNonPrimaryAlignments(int nonPrimaryAlignments) {
        this.nonPrimaryAlignments = nonPrimaryAlignments;
        return this;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public SamtoolsStats setTotalLength(long totalLength) {
        this.totalLength = totalLength;
        return this;
    }

    public long getTotalFirstFragmentLength() {
        return totalFirstFragmentLength;
    }

    public SamtoolsStats setTotalFirstFragmentLength(long totalFirstFragmentLength) {
        this.totalFirstFragmentLength = totalFirstFragmentLength;
        return this;
    }

    public long getTotalLastFragmentLength() {
        return totalLastFragmentLength;
    }

    public SamtoolsStats setTotalLastFragmentLength(long totalLastFragmentLength) {
        this.totalLastFragmentLength = totalLastFragmentLength;
        return this;
    }

    public long getBasesMapped() {
        return basesMapped;
    }

    public SamtoolsStats setBasesMapped(long basesMapped) {
        this.basesMapped = basesMapped;
        return this;
    }

    public long getBasesMappedCigar() {
        return basesMappedCigar;
    }

    public SamtoolsStats setBasesMappedCigar(long basesMappedCigar) {
        this.basesMappedCigar = basesMappedCigar;
        return this;
    }

    public long getBasesTrimmed() {
        return basesTrimmed;
    }

    public SamtoolsStats setBasesTrimmed(long basesTrimmed) {
        this.basesTrimmed = basesTrimmed;
        return this;
    }

    public long getBasesDuplicated() {
        return basesDuplicated;
    }

    public SamtoolsStats setBasesDuplicated(long basesDuplicated) {
        this.basesDuplicated = basesDuplicated;
        return this;
    }

    public int getMismatches() {
        return mismatches;
    }

    public SamtoolsStats setMismatches(int mismatches) {
        this.mismatches = mismatches;
        return this;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public SamtoolsStats setErrorRate(double errorRate) {
        this.errorRate = errorRate;
        return this;
    }

    public double getAverageLength() {
        return averageLength;
    }

    public SamtoolsStats setAverageLength(double averageLength) {
        this.averageLength = averageLength;
        return this;
    }

    public double getAverageFirstFragmentLength() {
        return averageFirstFragmentLength;
    }

    public SamtoolsStats setAverageFirstFragmentLength(double averageFirstFragmentLength) {
        this.averageFirstFragmentLength = averageFirstFragmentLength;
        return this;
    }

    public double getAverageLastFragmentLength() {
        return averageLastFragmentLength;
    }

    public SamtoolsStats setAverageLastFragmentLength(double averageLastFragmentLength) {
        this.averageLastFragmentLength = averageLastFragmentLength;
        return this;
    }

    public int getMaximumLength() {
        return maximumLength;
    }

    public SamtoolsStats setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
        return this;
    }

    public int getMaximumFirstFragmentLength() {
        return maximumFirstFragmentLength;
    }

    public SamtoolsStats setMaximumFirstFragmentLength(int maximumFirstFragmentLength) {
        this.maximumFirstFragmentLength = maximumFirstFragmentLength;
        return this;
    }

    public int getMaximumLastFragmentLength() {
        return maximumLastFragmentLength;
    }

    public SamtoolsStats setMaximumLastFragmentLength(int maximumLastFragmentLength) {
        this.maximumLastFragmentLength = maximumLastFragmentLength;
        return this;
    }

    public double getAverageQuality() {
        return averageQuality;
    }

    public SamtoolsStats setAverageQuality(double averageQuality) {
        this.averageQuality = averageQuality;
        return this;
    }

    public double getInsertSizeAverage() {
        return insertSizeAverage;
    }

    public SamtoolsStats setInsertSizeAverage(double insertSizeAverage) {
        this.insertSizeAverage = insertSizeAverage;
        return this;
    }

    public double getInsertSizeStandardDeviation() {
        return insertSizeStandardDeviation;
    }

    public SamtoolsStats setInsertSizeStandardDeviation(double insertSizeStandardDeviation) {
        this.insertSizeStandardDeviation = insertSizeStandardDeviation;
        return this;
    }

    public int getInwardOrientedPairs() {
        return inwardOrientedPairs;
    }

    public SamtoolsStats setInwardOrientedPairs(int inwardOrientedPairs) {
        this.inwardOrientedPairs = inwardOrientedPairs;
        return this;
    }

    public int getOutwardOrientedPairs() {
        return outwardOrientedPairs;
    }

    public SamtoolsStats setOutwardOrientedPairs(int outwardOrientedPairs) {
        this.outwardOrientedPairs = outwardOrientedPairs;
        return this;
    }

    public int getPairsWithOtherOrientation() {
        return pairsWithOtherOrientation;
    }

    public SamtoolsStats setPairsWithOtherOrientation(int pairsWithOtherOrientation) {
        this.pairsWithOtherOrientation = pairsWithOtherOrientation;
        return this;
    }

    public int getPairsOnDifferentChromosomes() {
        return pairsOnDifferentChromosomes;
    }

    public SamtoolsStats setPairsOnDifferentChromosomes(int pairsOnDifferentChromosomes) {
        this.pairsOnDifferentChromosomes = pairsOnDifferentChromosomes;
        return this;
    }

    public double getPercentageOfProperlyPairedReads() {
        return percentageOfProperlyPairedReads;
    }

    public SamtoolsStats setPercentageOfProperlyPairedReads(double percentageOfProperlyPairedReads) {
        this.percentageOfProperlyPairedReads = percentageOfProperlyPairedReads;
        return this;
    }
}
