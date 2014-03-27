package org.opencb.biodata.models.variant.stats;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/29/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariantGlobalStats {

    private int variantsCount;
    private int samplesCount;
    private int snpsCount;
    private int indelsCount;
    private int passCount;
    private int transitionsCount;
    private int transversionsCount;
    private int biallelicsCount;
    private int multiallelicsCount;
    private float accumQuality;

    public VariantGlobalStats() {
        this.variantsCount = 0;
        this.samplesCount = 0;
        this.snpsCount = 0;
        this.indelsCount = 0;
        this.passCount = 0;
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.biallelicsCount = 0;
        this.multiallelicsCount = 0;
        this.accumQuality = 0;
    }

    public VariantGlobalStats(List<VariantGlobalStats> variantGlobalStatsList) {
        this();

        for (VariantGlobalStats gs : variantGlobalStatsList) {
            this.updateStats(gs.getVariantsCount(), gs.samplesCount, gs.getSnpsCount(), gs.indelsCount, gs.passCount, gs.transitionsCount, gs.transversionsCount, gs.getBiallelicsCount(), gs.getMultiallelicsCount(), gs.getAccumQuality());
        }

    }


    public void updateStats(int variantsCount, int samplesCount, int snpsCount, int indelsCount, int passCount, int transitionsCount, int transversionsCount, int biallelicsCount, int multiallelicsCount, float accumQuality) {
        this.variantsCount += variantsCount;
        if (this.samplesCount == 0)
            this.samplesCount += samplesCount;
        this.snpsCount += snpsCount;
        this.indelsCount += indelsCount;
        this.passCount += passCount;
        this.transitionsCount += transitionsCount;
        this.transversionsCount += transversionsCount;
        this.biallelicsCount += biallelicsCount;
        this.multiallelicsCount += multiallelicsCount;
        this.accumQuality += accumQuality;
    }

    public int getVariantsCount() {
        return variantsCount;
    }

    public void setVariantsCount(int variantsCount) {
        this.variantsCount = variantsCount;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
        this.samplesCount = samplesCount;
    }

    public int getSnpsCount() {
        return snpsCount;
    }

    public void setSnpsCount(int snpsCount) {
        this.snpsCount = snpsCount;
    }

    public int getIndelsCount() {
        return indelsCount;
    }

    public void setIndelsCount(int indelsCount) {
        this.indelsCount = indelsCount;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
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

    public int getBiallelicsCount() {
        return biallelicsCount;
    }

    public void setBiallelicsCount(int biallelicsCount) {
        this.biallelicsCount = biallelicsCount;
    }

    public int getMultiallelicsCount() {
        return multiallelicsCount;
    }

    public void setMultiallelicsCount(int multiallelicsCount) {
        this.multiallelicsCount = multiallelicsCount;
    }

    public float getAccumQuality() {
        return accumQuality;
    }

    public void setAccumQuality(float accumQuality) {
        this.accumQuality = accumQuality;
    }

    public void addVariant() {
        this.variantsCount++;
    }
    
    public void addIndel() {
        this.indelsCount++;
    }

    public void addSNP() {
        this.snpsCount++;
    }

    public void addPass() {
        this.passCount++;
    }

    public void addTransitions(int transitionsCount) {
        this.transitionsCount += transitionsCount;
    }

    public void addTransversions(int transversionsCount) {
        this.transversionsCount += transversionsCount;
    }

    public void addMultiallelic() {
        this.multiallelicsCount++;
    }

    public void addBiallelic() {
        this.biallelicsCount++;
    }

    public void addAccumQuality(float qual) {
        this.accumQuality += qual;
    }

    @Override
    public String toString() {
        return "VariantGlobalStats{" +
                "variantsCount=" + variantsCount +
                ", samplesCount=" + samplesCount +
                ", snpsCount=" + snpsCount +
                ", indelsCount=" + indelsCount +
                ", passCount=" + passCount +
                ", transitionsCount=" + transitionsCount +
                ", transversionsCount=" + transversionsCount +
                ", biallelicsCount=" + biallelicsCount +
                ", multiallelicsCount=" + multiallelicsCount +
                ", accumQuality=" + accumQuality +
                '}';
    }
}
