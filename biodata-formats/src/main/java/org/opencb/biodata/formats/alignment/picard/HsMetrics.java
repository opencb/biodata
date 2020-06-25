package org.opencb.biodata.formats.alignment.picard;

import java.util.Arrays;

/**
 * Hs metrics report (from the picard/CollecHsMetrics command)
 */
public class HsMetrics {

    /**
     * A measure of how undercovered <= 50% GC regions are relative to the mean
     */
    private double atDropout;

    /**
     * Target terrirtoy / bait territory. 1 == perfectly efficient, 0.5 = half of baited bases are not target
     */
    private double baitDesignEfficiency;


    /**
     * The number of bases which have one or more baits on top of them
     */
    private int baitTerritory;


    /**
     * The fold over-coverage necessary to raise 80% of bases in \"non-zero-cvg\" targets to the mean coverage level in those targets
     */
    private double fold80BasePenalty;

    /**
     * The fold by which the baited region has been amplified above genomic background
     */
    private double foldEnrichment;

    /**
     * A measure of how undercovered >= 50% GC regions are relative to the mean
     */
    private double gcDropout;

    /**
     * The Phred Scaled Q Score of the theoretical HET SNP sensitivity
     */
    private double hetSnpQ;

    /**
     * The theoretical HET SNP sensitivity
     */
    private double hetSnpSensitivity;

    /**
     * The number of PF aligned bases that mapped to within a fixed interval of a baited region, but not on a baited region
     */
    private int nearBaitBases;

    /**
     * The number of PF aligned bases that mapped to neither on or near a bait
     */
    private int offBaitBases;

    /**
     * The number of PF aligned bases that mapped to a baited region of the genome
     */
    private int onBaitBases;

    /**
     * The number of PF aligned bases that mapped to a targeted region of the genome
     */
    private int onTargetBases;

    /**
     * Pf bases
     */
    private int pfBases;

    /**
     * The number of PF unique bases that are aligned with mapping score > 0 to the reference genome
     */
    private int pfBasesAligned;

    /**
     * The number of reads that pass the vendor's filter
     */
    private int pfReads;

    /**
     * The number of PF reads that are not marked as duplicates
     */
    private int pfUniqueReads;

    /**
     * The number of bases in the PF aligned reads that are mapped to a reference base. Accounts for clipping and gaps
     */
    private int pfUqBasesAligned;

    /**
     * The number of PF unique reads that are aligned with mapping score > 0 to the reference genome
     */
    private int pfUqReadsAligned;

    /**
     * The total number of reads in the SAM or BAM file examine
     */
    private int totalReads;

    /**
     * Max target coverage
     */
    private double maxTargetCoverage;

    /**
     * The mean coverage of all baits in the experiment
     */
    private double meanBaitCoverage;

    /**
     * The mean coverage of targets
     */
    private double meanTargetCoverage;

    /**
     * The median coverage of targets
     */
    private double medianTargetCoverage;

    /**
     * The minimum coverage of targets
     */
    private double minTargetCoverage;

    /**
     * The percentage of on+near bait bases that are on as opposed to near
     */
    private double onBaitVsSelected;

    /**
     * The unique number of target bases in the experiment where target is usually exons etc.
     */
    private int targetTerritory;

    /**
     * The fraction of targets that did not reach coverage=1 over any base
     */
    private double zeroCvgTargetsPct;

    /**
     * The estimated number of unique molecules in the selected part of the library
     */
    private int hsLibrarySize;

    /**
     * The name of the bait set used in the hybrid selection
     */
    private String baitSet;

    /**
     * The number of bases in the reference genome used for alignment
     */
    private int genomeSize;

    /**
     * The fraction of all target bases achieving 1x, 2x, 10x, 20x, 30x, 40x, 50x and 100x
     */
    private double[] pctTargetBases;

    /**
     * The hybrid selection penalty incurred to get 80% of target bases to 10X, 20x, 30x, 40x, 50x and 100x
    */
    private double[] hsPenalty;

    public HsMetrics() {
        pctTargetBases = new double[8];
        hsPenalty = new double[6];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HsMetrics{");
        sb.append("atDropout=").append(atDropout);
        sb.append(", baitDesignEfficiency=").append(baitDesignEfficiency);
        sb.append(", baitTerritory=").append(baitTerritory);
        sb.append(", fold80BasePenalty=").append(fold80BasePenalty);
        sb.append(", foldEnrichment=").append(foldEnrichment);
        sb.append(", gcDropout=").append(gcDropout);
        sb.append(", hetSnpQ=").append(hetSnpQ);
        sb.append(", hetSnpSensitivity=").append(hetSnpSensitivity);
        sb.append(", nearBaitBases=").append(nearBaitBases);
        sb.append(", offBaitBases=").append(offBaitBases);
        sb.append(", onBaitBases=").append(onBaitBases);
        sb.append(", onTargetBases=").append(onTargetBases);
        sb.append(", pfBases=").append(pfBases);
        sb.append(", pfBasesAligned=").append(pfBasesAligned);
        sb.append(", pfReads=").append(pfReads);
        sb.append(", pfUniqueReads=").append(pfUniqueReads);
        sb.append(", pfUqBasesAligned=").append(pfUqBasesAligned);
        sb.append(", pfUqReadsAligned=").append(pfUqReadsAligned);
        sb.append(", totalReads=").append(totalReads);
        sb.append(", maxTargetCoverage=").append(maxTargetCoverage);
        sb.append(", meanBaitCoverage=").append(meanBaitCoverage);
        sb.append(", meanTargetCoverage=").append(meanTargetCoverage);
        sb.append(", medianTargetCoverage=").append(medianTargetCoverage);
        sb.append(", minTargetCoverage=").append(minTargetCoverage);
        sb.append(", onBaitVsSelected=").append(onBaitVsSelected);
        sb.append(", targetTerritory=").append(targetTerritory);
        sb.append(", zeroCvgTargetsPct=").append(zeroCvgTargetsPct);
        sb.append(", zeroCvgTargetsPct=").append(zeroCvgTargetsPct);
        sb.append(", hsLibrarySize=").append(hsLibrarySize);
        sb.append(", baitSet='").append(baitSet).append('\'');
        sb.append(", genomeSize=").append(genomeSize);
        sb.append(", pctTargetBases=").append(Arrays.toString(pctTargetBases));
        sb.append(", hsPenalty=").append(Arrays.toString(hsPenalty));
        sb.append('}');
        return sb.toString();
    }

    public double getAtDropout() {
        return atDropout;
    }

    public HsMetrics setAtDropout(double atDropout) {
        this.atDropout = atDropout;
        return this;
    }

    public double getBaitDesignEfficiency() {
        return baitDesignEfficiency;
    }

    public HsMetrics setBaitDesignEfficiency(double baitDesignEfficiency) {
        this.baitDesignEfficiency = baitDesignEfficiency;
        return this;
    }

    public int getBaitTerritory() {
        return baitTerritory;
    }

    public HsMetrics setBaitTerritory(int baitTerritory) {
        this.baitTerritory = baitTerritory;
        return this;
    }

    public double getFold80BasePenalty() {
        return fold80BasePenalty;
    }

    public HsMetrics setFold80BasePenalty(double fold80BasePenalty) {
        this.fold80BasePenalty = fold80BasePenalty;
        return this;
    }

    public double getFoldEnrichment() {
        return foldEnrichment;
    }

    public HsMetrics setFoldEnrichment(double foldEnrichment) {
        this.foldEnrichment = foldEnrichment;
        return this;
    }

    public double getGcDropout() {
        return gcDropout;
    }

    public HsMetrics setGcDropout(double gcDropout) {
        this.gcDropout = gcDropout;
        return this;
    }

    public double getHetSnpQ() {
        return hetSnpQ;
    }

    public HsMetrics setHetSnpQ(double hetSnpQ) {
        this.hetSnpQ = hetSnpQ;
        return this;
    }

    public double getHetSnpSensitivity() {
        return hetSnpSensitivity;
    }

    public HsMetrics setHetSnpSensitivity(double hetSnpSensitivity) {
        this.hetSnpSensitivity = hetSnpSensitivity;
        return this;
    }

    public int getNearBaitBases() {
        return nearBaitBases;
    }

    public HsMetrics setNearBaitBases(int nearBaitBases) {
        this.nearBaitBases = nearBaitBases;
        return this;
    }

    public int getOffBaitBases() {
        return offBaitBases;
    }

    public HsMetrics setOffBaitBases(int offBaitBases) {
        this.offBaitBases = offBaitBases;
        return this;
    }

    public int getOnBaitBases() {
        return onBaitBases;
    }

    public HsMetrics setOnBaitBases(int onBaitBases) {
        this.onBaitBases = onBaitBases;
        return this;
    }

    public int getOnTargetBases() {
        return onTargetBases;
    }

    public HsMetrics setOnTargetBases(int onTargetBases) {
        this.onTargetBases = onTargetBases;
        return this;
    }

    public int getPfBases() {
        return pfBases;
    }

    public HsMetrics setPfBases(int pfBases) {
        this.pfBases = pfBases;
        return this;
    }

    public int getPfBasesAligned() {
        return pfBasesAligned;
    }

    public HsMetrics setPfBasesAligned(int pfBasesAligned) {
        this.pfBasesAligned = pfBasesAligned;
        return this;
    }

    public int getPfReads() {
        return pfReads;
    }

    public HsMetrics setPfReads(int pfReads) {
        this.pfReads = pfReads;
        return this;
    }

    public int getPfUniqueReads() {
        return pfUniqueReads;
    }

    public HsMetrics setPfUniqueReads(int pfUniqueReads) {
        this.pfUniqueReads = pfUniqueReads;
        return this;
    }

    public int getPfUqBasesAligned() {
        return pfUqBasesAligned;
    }

    public HsMetrics setPfUqBasesAligned(int pfUqBasesAligned) {
        this.pfUqBasesAligned = pfUqBasesAligned;
        return this;
    }

    public int getPfUqReadsAligned() {
        return pfUqReadsAligned;
    }

    public HsMetrics setPfUqReadsAligned(int pfUqReadsAligned) {
        this.pfUqReadsAligned = pfUqReadsAligned;
        return this;
    }

    public int getTotalReads() {
        return totalReads;
    }

    public HsMetrics setTotalReads(int totalReads) {
        this.totalReads = totalReads;
        return this;
    }

    public double getMaxTargetCoverage() {
        return maxTargetCoverage;
    }

    public HsMetrics setMaxTargetCoverage(double maxTargetCoverage) {
        this.maxTargetCoverage = maxTargetCoverage;
        return this;
    }

    public double getMeanBaitCoverage() {
        return meanBaitCoverage;
    }

    public HsMetrics setMeanBaitCoverage(double meanBaitCoverage) {
        this.meanBaitCoverage = meanBaitCoverage;
        return this;
    }

    public double getMeanTargetCoverage() {
        return meanTargetCoverage;
    }

    public HsMetrics setMeanTargetCoverage(double meanTargetCoverage) {
        this.meanTargetCoverage = meanTargetCoverage;
        return this;
    }

    public double getMedianTargetCoverage() {
        return medianTargetCoverage;
    }

    public HsMetrics setMedianTargetCoverage(double medianTargetCoverage) {
        this.medianTargetCoverage = medianTargetCoverage;
        return this;
    }

    public double getMinTargetCoverage() {
        return minTargetCoverage;
    }

    public HsMetrics setMinTargetCoverage(double minTargetCoverage) {
        this.minTargetCoverage = minTargetCoverage;
        return this;
    }

    public double getOnBaitVsSelected() {
        return onBaitVsSelected;
    }

    public HsMetrics setOnBaitVsSelected(double onBaitVsSelected) {
        this.onBaitVsSelected = onBaitVsSelected;
        return this;
    }

    public int getTargetTerritory() {
        return targetTerritory;
    }

    public HsMetrics setTargetTerritory(int targetTerritory) {
        this.targetTerritory = targetTerritory;
        return this;
    }

    public double getZeroCvgTargetsPct() {
        return zeroCvgTargetsPct;
    }

    public HsMetrics setZeroCvgTargetsPct(int zeroCvgTargetsPct) {
        this.zeroCvgTargetsPct = zeroCvgTargetsPct;
        return this;
    }

    public int getHsLibrarySize() {
        return hsLibrarySize;
    }

    public HsMetrics setHsLibrarySize(int hsLibrarySize) {
        this.hsLibrarySize = hsLibrarySize;
        return this;
    }

    public String getBaitSet() {
        return baitSet;
    }

    public HsMetrics setBaitSet(String baitSet) {
        this.baitSet = baitSet;
        return this;
    }

    public int getGenomeSize() {
        return genomeSize;
    }

    public HsMetrics setGenomeSize(int genomeSize) {
        this.genomeSize = genomeSize;
        return this;
    }

    public double[] getPctTargetBases() {
        return pctTargetBases;
    }

    public HsMetrics setPctTargetBases(double[] pctTargetBases) {
        this.pctTargetBases = pctTargetBases;
        return this;
    }

    public double[] getHsPenalty() {
        return hsPenalty;
    }

    public HsMetrics setHsPenalty(double[] hsPenalty) {
        this.hsPenalty = hsPenalty;
        return this;
    }

    public HsMetrics setZeroCvgTargetsPct(double zeroCvgTargetsPct) {
        this.zeroCvgTargetsPct = zeroCvgTargetsPct;
        return this;
    }
}
