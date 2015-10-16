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

package org.opencb.biodata.models.variant.stats;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "accumulatedQuality"})
public class VariantGlobalStats {

    private final org.opencb.biodata.models.variant.avro.VariantGlobalStats impl;
    private double accumulatedQuality;

    public VariantGlobalStats(org.opencb.biodata.models.variant.avro.VariantGlobalStats impl) {
        this.impl = impl;
    }

    public VariantGlobalStats() {
        this.impl = new org.opencb.biodata.models.variant.avro.VariantGlobalStats();
        this.setVariantsCount(0);
        this.setSamplesCount(0);
        this.setSnpsCount(0);
        this.setIndelsCount(0);
        this.setStructuralCount(0);
        this.setPassCount(0);
        this.setTransitionsCount(0);
        this.setTransversionsCount(0);
        this.setAccumulatedQuality(0);
        this.setConsequenceTypesCount(new LinkedHashMap<>(20));
    }

    public VariantGlobalStats(int variantsCount, int samplesCount, int snpsCount, int indelsCount, int structuralCount, 
            int passCount, int transitionsCount, int transversionsCount, float accumulatedQuality, double meanQuality,
            Map<String, Integer> consequenceTypesCount) {
        this.impl = new org.opencb.biodata.models.variant.avro.VariantGlobalStats();
        this.setVariantsCount(variantsCount);
        this.setSamplesCount(samplesCount);
        this.setSnpsCount(snpsCount);
        this.setIndelsCount(indelsCount);
        this.setStructuralCount(structuralCount);
        this.setPassCount(passCount);
        this.setTransitionsCount(transitionsCount);
        this.setTransversionsCount(transversionsCount);
        this.setAccumulatedQuality(accumulatedQuality);
        this.setMeanQuality(meanQuality);
        this.setConsequenceTypesCount(consequenceTypesCount);
    }

    public org.opencb.biodata.models.variant.avro.VariantGlobalStats getImpl() {
        return impl;
    }

    public int getVariantsCount() {
        return impl.getVariantsCount();
    }

    public void setVariantsCount(int variantsCount) {
        this.impl.setVariantsCount(variantsCount);
    }

    public int getSamplesCount() {
        return impl.getSamplesCount();
    }

    public void setSamplesCount(int samplesCount) {
        this.impl.setSamplesCount(samplesCount);
    }

    public int getSnpsCount() {
        return impl.getSnpsCount();
    }

    public void setSnpsCount(int snpsCount) {
        this.impl.setSnpsCount(snpsCount);
    }

    public int getIndelsCount() {
        return impl.getIndelsCount();
    }

    public void setIndelsCount(int indelsCount) {
        this.impl.setIndelsCount(indelsCount);
    }

    public int getStructuralCount() {
        return impl.getStructuralCount();
    }

    public void setStructuralCount(int structuralCount) {
        this.impl.setStructuralCount(structuralCount);
    }

    public int getPassCount() {
        return impl.getPassCount();
    }

    public void setPassCount(int passCount) {
        this.impl.setPassCount(passCount);
    }

    public int getTransitionsCount() {
        return impl.getTransitionsCount();
    }

    public void setTransitionsCount(int transitionsCount) {
        this.impl.setTransitionsCount(transitionsCount);
    }

    public int getTransversionsCount() {
        return impl.getTransversionsCount();
    }

    public void setTransversionsCount(int transversionsCount) {
        this.impl.setTransversionsCount(transversionsCount);
    }

    public double getAccumulatedQuality() {
        return accumulatedQuality;
    }

    public void setAccumulatedQuality(double accumulatedQuality) {
        this.accumulatedQuality = accumulatedQuality;
    }

    public Double getMeanQuality() {
        if (impl.getMeanQuality() <= 0) {
            impl.setMeanQuality(getAccumulatedQuality() / getVariantsCount());
        }
        return impl.getMeanQuality();
    }

    public void setMeanQuality(Double meanQuality) {
        this.impl.setMeanQuality(meanQuality);
    }

    public Map<String, Integer> getConsequenceTypesCount() {
        return impl.getConsequenceTypesCount();
    }

    public void setConsequenceTypesCount(Map<String, Integer> consequenceTypesCount) {
        this.impl.setConsequenceTypesCount(consequenceTypesCount);
    }
    
    public void addConsequenceTypeCount(String ct, int count) {
        if (!getConsequenceTypesCount().containsKey(ct)) {
            getConsequenceTypesCount().put(ct, 0);
        } else {
            getConsequenceTypesCount().put(ct, getConsequenceTypesCount().get(ct) + 1);
        }
    }
    
    
    public void update(VariantStats stats) {
        setVariantsCount(getVariantsCount() + 1);
        
        switch (stats.getVariantType()) {
            case SNV:
                setSnpsCount(getSnpsCount() + 1);
                break;
            case MNV:
                setSnpsCount(getSnpsCount() + stats.getRefAllele().length());
                break;
            case INDEL:
                setIndelsCount(getIndelsCount() + 1);
                break;
            default:
                setStructuralCount(getStructuralCount() + 1);
                break;
        }
        
        if (stats.hasPassedFilters()) {
            setPassCount(getPassCount() + 1);
        }

        setSamplesCount(stats.getNumSamples());
        setTransitionsCount(getTransitionsCount() + (stats.isTransition() ? 1 : 0));
        setTransversionsCount(getTransversionsCount() + (stats.isTransversion() ? 1 : 0));
        setAccumulatedQuality(getAccumulatedQuality() + stats.getQuality());
    }

    @Override
    public String toString() {
        return "VariantGlobalStats{"
                + "variantsCount=" + getVariantsCount()
                + ", samplesCount=" + getSamplesCount()
                + ", snpsCount=" + getSnpsCount()
                + ", indelsCount=" + getIndelsCount()
                + ", passCount=" + getPassCount()
                + ", transitionsCount=" + getTransitionsCount()
                + ", transversionsCount=" + getTransversionsCount()
                + ", accumQuality=" + getAccumulatedQuality()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariantGlobalStats)) return false;

        VariantGlobalStats that = (VariantGlobalStats) o;

        if (Double.compare(that.accumulatedQuality, accumulatedQuality) != 0) return false;
        return !(impl != null ? !impl.equals(that.impl) : that.impl != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = impl != null ? impl.hashCode() : 0;
        temp = Double.doubleToLongBits(accumulatedQuality);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
