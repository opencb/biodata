package org.opencb.biodata.models.variant.stats;

import java.util.LinkedHashMap;
import java.util.Map;

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
    private float accumulatedQuality;
    @Deprecated
    private int biallelicsCount;
    @Deprecated
    private int multiallelicsCount;
    
    private Map<String, Integer> consequenceTypesCount;

    
    public VariantGlobalStats() {
        this.variantsCount = 0;
        this.samplesCount = 0;
        this.snpsCount = 0;
        this.indelsCount = 0;
        this.passCount = 0;
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.accumulatedQuality = 0;
        this.biallelicsCount = 0;
        this.multiallelicsCount = 0;
        this.consequenceTypesCount = new LinkedHashMap<>(20);
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

    public float getAccumulatedQuality() {
        return accumulatedQuality;
    }

    public void setAccumulatedQuality(float accumulatedQuality) {
        this.accumulatedQuality = accumulatedQuality;
    }

    @Deprecated
    int getBiallelicsCount() {
        return biallelicsCount;
    }

    @Deprecated
    int getMultiallelicsCount() {
        return multiallelicsCount;
    }

    public Map<String, Integer> getConsequenceTypesCount() {
        return consequenceTypesCount;
    }

    public void setConsequenceTypesCount(Map<String, Integer> consequenceTypesCount) {
        this.consequenceTypesCount = consequenceTypesCount;
    }
    
    public void addConsequenceTypeCount(String ct, int count) {
        if (!consequenceTypesCount.containsKey(ct)) {
            consequenceTypesCount.put(ct, 0);
        } else {
            consequenceTypesCount.put(ct, consequenceTypesCount.get(ct) + 1);
        }
    }
    
    
    public void update(VariantStats stats) {
        variantsCount++;
        
        if (stats.isIndel()) {
            indelsCount++;
        }
        if (stats.isSNP()) {
            snpsCount++;
        }
        if (stats.hasPassedFilters()) {
            passCount++;
        }

        samplesCount = stats.getNumSamples();
        transitionsCount += stats.getTransitionsCount();
        transversionsCount += stats.getTransversionsCount();
        accumulatedQuality += stats.getQuality();
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
        this.accumulatedQuality += accumQuality;
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
                + ", accumQuality=" + accumulatedQuality
                + '}';
    }

}
