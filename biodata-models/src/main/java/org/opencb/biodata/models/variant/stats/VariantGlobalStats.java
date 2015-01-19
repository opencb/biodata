package org.opencb.biodata.models.variant.stats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantGlobalStats {

    private int variantsCount;
    private int samplesCount;
    
    private int snpsCount;
    private int indelsCount;
    private int structuralCount;
    
    private int passCount;
    
    private int transitionsCount;
    private int transversionsCount;
    
    private float accumulatedQuality;
    private float meanQuality;
    
    private Map<String, Integer> consequenceTypesCount;

    public VariantGlobalStats() {
        this.variantsCount = 0;
        this.samplesCount = 0;
        this.snpsCount = 0;
        this.indelsCount = 0;
        this.structuralCount = 0;
        this.passCount = 0;
        this.transitionsCount = 0;
        this.transversionsCount = 0;
        this.accumulatedQuality = 0;
        this.consequenceTypesCount = new LinkedHashMap<>(20);
    }

    public VariantGlobalStats(int variantsCount, int samplesCount, int snpsCount, int indelsCount, int structuralCount, 
            int passCount, int transitionsCount, int transversionsCount, float accumulatedQuality, float meanQuality, 
            Map<String, Integer> consequenceTypesCount) {
        this.variantsCount = variantsCount;
        this.samplesCount = samplesCount;
        this.snpsCount = snpsCount;
        this.indelsCount = indelsCount;
        this.structuralCount = structuralCount;
        this.passCount = passCount;
        this.transitionsCount = transitionsCount;
        this.transversionsCount = transversionsCount;
        this.accumulatedQuality = accumulatedQuality;
        this.meanQuality = meanQuality;
        this.consequenceTypesCount = consequenceTypesCount;
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

    public int getStructuralCount() {
        return structuralCount;
    }

    public void setStructuralCount(int structuralCount) {
        this.structuralCount = structuralCount;
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

    public float getMeanQuality() {
        if (meanQuality <= 0) {
            meanQuality = getAccumulatedQuality() / getVariantsCount();
        }
        return meanQuality;
    }

    public void setMeanQuality(float meanQuality) {
        this.meanQuality = meanQuality;
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
        
        switch (stats.getVariantType()) {
            case SNV:
                snpsCount++;
                break;
            case MNV:
                snpsCount += stats.getRefAllele().length();
                break;
            case INDEL:
                indelsCount++;
                break;
            default:
                structuralCount++;
                break;
        }
        
        if (stats.hasPassedFilters()) {
            passCount++;
        }

        samplesCount = stats.getNumSamples();
        transitionsCount += stats.isTransition() ? 1 : 0;
        transversionsCount += stats.isTransversion() ? 1 : 0;
        accumulatedQuality += stats.getQuality();
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.variantsCount;
        hash = 19 * hash + this.samplesCount;
        hash = 19 * hash + this.snpsCount;
        hash = 19 * hash + this.indelsCount;
        hash = 19 * hash + this.structuralCount;
        hash = 19 * hash + this.passCount;
        hash = 19 * hash + this.transitionsCount;
        hash = 19 * hash + this.transversionsCount;
        hash = 19 * hash + Float.floatToIntBits(this.accumulatedQuality);
        hash = 19 * hash + Float.floatToIntBits(this.meanQuality);
        hash = 19 * hash + Objects.hashCode(this.consequenceTypesCount);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariantGlobalStats other = (VariantGlobalStats) obj;
        if (this.variantsCount != other.variantsCount) {
            return false;
        }
        if (this.samplesCount != other.samplesCount) {
            return false;
        }
        if (this.snpsCount != other.snpsCount) {
            return false;
        }
        if (this.indelsCount != other.indelsCount) {
            return false;
        }
        if (this.structuralCount != other.structuralCount) {
            return false;
        }
        if (this.passCount != other.passCount) {
            return false;
        }
        if (this.transitionsCount != other.transitionsCount) {
            return false;
        }
        if (this.transversionsCount != other.transversionsCount) {
            return false;
        }
        if (Float.floatToIntBits(this.accumulatedQuality) != Float.floatToIntBits(other.accumulatedQuality)) {
            return false;
        }
        if (Float.floatToIntBits(this.meanQuality) != Float.floatToIntBits(other.meanQuality)) {
            return false;
        }
        if (!Objects.equals(this.consequenceTypesCount, other.consequenceTypesCount)) {
            return false;
        }
        return true;
    }

}
