package org.opencb.biodata.formats.alignment.samtools;

public class SamtoolsFlagstats {

    /**
     * Total number of reads
     */
    private int totalReads;

    /**
     * Total number of reads marked as not passing quality controls
     */
    private int totalQcPassed;

    /**
     * Total number of reads which are mapped (0x4 bit not set)
     */
    private int mapped;

    /**
     * Total number of reads which are secondary (0x100 bit set)
     */
    private int secondaryAlignments;

    /**
     * Total number of reads which are supplementary (0x800 bit set)
     */
    private int supplementary;
    /**
     * Total number of reads which are duplicates (0x400 bit set)
     */
    private int duplicates;

    /**
     * Total number of reads which are paired in sequencing (0x1 bit set)
     */
    private int pairedInSequencing;

    /**
     * Total number of reads with both 0x1 and 0x40 bits set
     */
    private int read1;


    /**
     * Total number reads with both 0x1 and 0x80 bits set
     */
    private int read2;

    /**
     * Total number of reads which are properly paired (both 0x1 and 0x2 bits set and 0x4 bit not set)
     */
    private int properlyPaired;


    /**
     * Total number of reads with itself and mate mapped (0x1 bit set and neither 0x4 nor 0x8 bits set)
     */
    private int selfAndMateMapped;

    /**
     * Total number of reads which are singletons (both 0x1 and 0x8 bits set and bit 0x4 not set)
     */
    private int singletons;

    /**
     * Total number of reads with mate mapped to a different chromosome (0x1 bit set and neither 0x4 nor 0x8 bits set and MRNM not
     * equal to RNAME)
     */
    private int mateMappedToDiffChr;

    /**
     * Total number of reads with mate mapped to a different chromosome and with mapping quality at least 5 (0x1 bit set and neither 0x4
     * nor 0x8 bits set and MRNM not equal to RNAME and MAPQ >= 5)
     */
    private int diffChrMapQ5;

    public SamtoolsFlagstats() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SamtoolsFlagstats{");
        sb.append("totalReads=").append(totalReads);
        sb.append(", totalQcPassed=").append(totalQcPassed);
        sb.append(", mapped=").append(mapped);
        sb.append(", secondaryAlignments=").append(secondaryAlignments);
        sb.append(", supplementary=").append(supplementary);
        sb.append(", duplicates=").append(duplicates);
        sb.append(", pairedInSequencing=").append(pairedInSequencing);
        sb.append(", read1=").append(read1);
        sb.append(", read2=").append(read2);
        sb.append(", properlyPaired=").append(properlyPaired);
        sb.append(", selfAndMateMapped=").append(selfAndMateMapped);
        sb.append(", singletons=").append(singletons);
        sb.append(", mateMappedToDiffChr=").append(mateMappedToDiffChr);
        sb.append(", diffChrMapQ5=").append(diffChrMapQ5);
        sb.append('}');
        return sb.toString();
    }

    public int getTotalReads() {
        return totalReads;
    }

    public SamtoolsFlagstats setTotalReads(int totalReads) {
        this.totalReads = totalReads;
        return this;
    }

    public int getTotalQcPassed() {
        return totalQcPassed;
    }

    public SamtoolsFlagstats setTotalQcPassed(int totalQcPassed) {
        this.totalQcPassed = totalQcPassed;
        return this;
    }

    public int getMapped() {
        return mapped;
    }

    public SamtoolsFlagstats setMapped(int mapped) {
        this.mapped = mapped;
        return this;
    }

    public int getSecondaryAlignments() {
        return secondaryAlignments;
    }

    public SamtoolsFlagstats setSecondaryAlignments(int secondaryAlignments) {
        this.secondaryAlignments = secondaryAlignments;
        return this;
    }

    public int getSupplementary() {
        return supplementary;
    }

    public SamtoolsFlagstats setSupplementary(int supplementary) {
        this.supplementary = supplementary;
        return this;
    }

    public int getDuplicates() {
        return duplicates;
    }

    public SamtoolsFlagstats setDuplicates(int duplicates) {
        this.duplicates = duplicates;
        return this;
    }

    public int getPairedInSequencing() {
        return pairedInSequencing;
    }

    public SamtoolsFlagstats setPairedInSequencing(int pairedInSequencing) {
        this.pairedInSequencing = pairedInSequencing;
        return this;
    }

    public int getRead1() {
        return read1;
    }

    public SamtoolsFlagstats setRead1(int read1) {
        this.read1 = read1;
        return this;
    }

    public int getRead2() {
        return read2;
    }

    public SamtoolsFlagstats setRead2(int read2) {
        this.read2 = read2;
        return this;
    }

    public int getProperlyPaired() {
        return properlyPaired;
    }

    public SamtoolsFlagstats setProperlyPaired(int properlyPaired) {
        this.properlyPaired = properlyPaired;
        return this;
    }

    public int getSelfAndMateMapped() {
        return selfAndMateMapped;
    }

    public SamtoolsFlagstats setSelfAndMateMapped(int selfAndMateMapped) {
        this.selfAndMateMapped = selfAndMateMapped;
        return this;
    }

    public int getSingletons() {
        return singletons;
    }

    public SamtoolsFlagstats setSingletons(int singletons) {
        this.singletons = singletons;
        return this;
    }

    public int getMateMappedToDiffChr() {
        return mateMappedToDiffChr;
    }

    public SamtoolsFlagstats setMateMappedToDiffChr(int mateMappedToDiffChr) {
        this.mateMappedToDiffChr = mateMappedToDiffChr;
        return this;
    }

    public int getDiffChrMapQ5() {
        return diffChrMapQ5;
    }

    public SamtoolsFlagstats setDiffChrMapQ5(int diffChrMapQ5) {
        this.diffChrMapQ5 = diffChrMapQ5;
        return this;
    }
}
