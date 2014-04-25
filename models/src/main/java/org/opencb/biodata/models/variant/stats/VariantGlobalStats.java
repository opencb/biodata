package org.opencb.biodata.models.variant.stats;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantGlobalStats {

    private int variantsCount;
    private int samplesCount;
    private int snpsCount;
    private int indelsCount;
    private int passCount;
    private int transitionsCount;
    private int transversionsCount;
    private float accumQuality;
    @Deprecated
    private int biallelicsCount;
    @Deprecated
    private int multiallelicsCount;

    public VariantGlobalStats() {
        this.variantsCount = 0;
        this.samplesCount = 0;
        this.snpsCount = 0;
        this.indelsCount = 0;
        this.passCount = 0;
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.accumQuality = 0;
        this.biallelicsCount = 0;
        this.multiallelicsCount = 0;
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

    public float getAccumQuality() {
        return accumQuality;
    }

    public void setAccumQuality(float accumQuality) {
        this.accumQuality = accumQuality;
    }

    @Deprecated
    public int getBiallelicsCount() {
        return biallelicsCount;
    }

    @Deprecated
    public int getMultiallelicsCount() {
        return multiallelicsCount;
    }

    
    public void update(VariantStats stats) {
        if (stats.isIndel()) {
            indelsCount++;
        }
        if (stats.isSNP()) {
            snpsCount++;
        }
        if (stats.hasPassedFilters()) {
            passCount++;
        }

        transitionsCount += stats.getTransitionsCount();
        transversionsCount += stats.getTransversionsCount();
        accumQuality += stats.getQual();
    }

    @Deprecated
    public void updateStats(int variantsCount, int samplesCount, int snpsCount, int indelsCount, int passCount, 
            int transitionsCount, int transversionsCount, int biallelicsCount, int multiallelicsCount, float accumQuality) {
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
    
    @Override
    public String toString() {
        return "VariantGlobalStats{"
                + "variantsCount=" + variantsCount
                + ", samplesCount=" + samplesCount
                + ", snpsCount=" + snpsCount
                + ", indelsCount=" + indelsCount
                + ", passCount=" + passCount
                + ", transitionsCount=" + transitionsCount
                + ", transversionsCount=" + transversionsCount
                + ", accumQuality=" + accumQuality
                + '}';
    }

}
