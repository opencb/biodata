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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@JsonIgnoreProperties({"impl", "accumulatedQuality", "variantsCount", "snpsCount", "indelsCount", "structuralCount"})
public class VariantGlobalStats {

    private final org.opencb.biodata.models.variant.avro.VariantGlobalStats impl;
    private double accumulatedQuality;

    public VariantGlobalStats(org.opencb.biodata.models.variant.avro.VariantGlobalStats impl) {
        this.impl = impl;
    }

    public VariantGlobalStats() {
        this.impl = new org.opencb.biodata.models.variant.avro.VariantGlobalStats();
        this.setVariantTypeCounts(new HashMap<>());
        this.setChromosomeCounts(new HashMap<>());
        this.setNumRecords(0);
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

    public VariantGlobalStats(int numRecords, int samplesCount, int snpsCount, int indelsCount, int structuralCount,
            int passCount, int transitionsCount, int transversionsCount, float accumulatedQuality, double meanQuality,
            Map<String, Integer> consequenceTypesCount) {
        this.impl = new org.opencb.biodata.models.variant.avro.VariantGlobalStats();
        this.setVariantTypeCounts(new HashMap<>());
        this.setChromosomeCounts(new HashMap<>());
        this.setNumRecords(numRecords);
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

    @Deprecated
    public int getVariantsCount() {
        return getNumRecords();
    }

    @Deprecated
    public void setVariantsCount(int variantsCount) {
        this.impl.setNumRecords(variantsCount);
    }

    public int getNumRecords() {
        return impl.getNumRecords() == null ? 0 : impl.getNumRecords();
    }

    public void setNumRecords(Integer numRecords) {
        this.impl.setNumRecords(numRecords);
    }

    public int getSamplesCount() {
        return impl.getSamplesCount();
    }

    public void setSamplesCount(int samplesCount) {
        this.impl.setSamplesCount(samplesCount);
    }

    public int getSnpsCount() {
        return getVariantTypeCount(VariantType.SNP);
    }

    public void setSnpsCount(int snpsCount) {
        setVariantTypeCount(VariantType.SNP, snpsCount);
    }

    public int getIndelsCount() {
        return getVariantTypeCount(VariantType.INDEL);
    }

    public void setIndelsCount(int indelsCount) {
        setVariantTypeCount(VariantType.INDEL, indelsCount);
    }

    public int getStructuralCount() {
        return getVariantTypeCount(VariantType.SV);
    }

    public void setStructuralCount(int structuralCount) {
        setVariantTypeCount(VariantType.SV, structuralCount);
    }

    public Map<String, Integer> getVariantTypeCounts() {
        return impl.getVariantTypeCounts();
    }

    public void setVariantTypeCounts(Map<String, Integer> count) {
        impl.setVariantTypeCounts(count);
    }

    public int getVariantTypeCount(VariantType key) {
        return impl.getVariantTypeCounts().getOrDefault(key.toString(), 0);
    }

    public void setVariantTypeCount(VariantType key, int count) {
        impl.getVariantTypeCounts().put(key.toString(), count);
    }

    public void addVariantTypeCount(VariantType key, int count) {
        impl.getVariantTypeCounts().put(key.toString(), getVariantTypeCount(key) + count);
    }

    public Map<String, Integer> getChromosomeCounts() {
        return impl.getChromosomeCounts();
    }

    public void setChromosomeCounts(Map<String, Integer> counts) {
        impl.setChromosomeCounts(counts);
    }

    private int getChromosomeCount(String chromosome) {
        return impl.getChromosomeCounts().getOrDefault(chromosome, 0);
    }

    public void setChromosomeCount(String chromosome, int count) {
        impl.getChromosomeCounts().put(chromosome, count);
    }

    public void addChromosomeCount(String chromosome, int count) {
        impl.getChromosomeCounts().put(chromosome, getChromosomeCount(chromosome) + count);
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


    @Deprecated
    public void update(VariantStats stats) {
        setNumRecords(getNumRecords() + 1);

        addVariantTypeCount(stats.getVariantType(), 1);

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
