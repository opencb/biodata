package org.opencb.biodata.models.variant.stats;

import org.opencb.biodata.models.clinical.MutationalSignature;

import java.util.Map;

public class SampleQcVariantStats {
    private String id;
    private String sampleId;
    private Map<String, Object> query;

    private MutationalSignature.Signature signature;

    public SampleQcVariantStats() {
    }

    public SampleQcVariantStats(String id, String sampleId, Map<String, Object> query, MutationalSignature.Signature signature) {
        this.id = id;
        this.sampleId = sampleId;
        this.query = query;
        this.signature = signature;
    }

    public String getId() {
        return id;
    }

    public SampleQcVariantStats setId(String id) {
        this.id = id;
        return this;
    }

    public String getSampleId() {
        return sampleId;
    }

    public SampleQcVariantStats setSampleId(String sampleId) {
        this.sampleId = sampleId;
        return this;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public SampleQcVariantStats setQuery(Map<String, Object> query) {
        this.query = query;
        return this;
    }

    public MutationalSignature.Signature getSignature() {
        return signature;
    }

    public SampleQcVariantStats setSignature(MutationalSignature.Signature signature) {
        this.signature = signature;
        return this;
    }

    public static class QcVariantStats {
        private int variantCount;
        private int passCount;
        private Map<String, Integer> chromosomeCount;
        private Map<String, Integer> typeCount;
        private Map<String, Integer> biotypeCount;
        private Map<String, Integer> consequenceTypeCount;
        private Map<String, Integer> genotypeCount;

        public QcVariantStats() {
        }

        public QcVariantStats(int variantCount, int passCount, Map<String, Integer> chromosomeCount, Map<String, Integer> typeCount,
                              Map<String, Integer> biotypeCount, Map<String, Integer> consequenceTypeCount,
                              Map<String, Integer> genotypeCount) {
            this.variantCount = variantCount;
            this.passCount = passCount;
            this.chromosomeCount = chromosomeCount;
            this.typeCount = typeCount;
            this.biotypeCount = biotypeCount;
            this.consequenceTypeCount = consequenceTypeCount;
            this.genotypeCount = genotypeCount;
        }

        public int getVariantCount() {
            return variantCount;
        }

        public QcVariantStats setVariantCount(int variantCount) {
            this.variantCount = variantCount;
            return this;
        }

        public int getPassCount() {
            return passCount;
        }

        public QcVariantStats setPassCount(int passCount) {
            this.passCount = passCount;
            return this;
        }

        public Map<String, Integer> getChromosomeCount() {
            return chromosomeCount;
        }

        public QcVariantStats setChromosomeCount(Map<String, Integer> chromosomeCount) {
            this.chromosomeCount = chromosomeCount;
            return this;
        }

        public Map<String, Integer> getTypeCount() {
            return typeCount;
        }

        public QcVariantStats setTypeCount(Map<String, Integer> typeCount) {
            this.typeCount = typeCount;
            return this;
        }

        public Map<String, Integer> getBiotypeCount() {
            return biotypeCount;
        }

        public QcVariantStats setBiotypeCount(Map<String, Integer> biotypeCount) {
            this.biotypeCount = biotypeCount;
            return this;
        }

        public Map<String, Integer> getConsequenceTypeCount() {
            return consequenceTypeCount;
        }

        public QcVariantStats setConsequenceTypeCount(Map<String, Integer> consequenceTypeCount) {
            this.consequenceTypeCount = consequenceTypeCount;
            return this;
        }

        public Map<String, Integer> getGenotypeCount() {
            return genotypeCount;
        }

        public QcVariantStats setGenotypeCount(Map<String, Integer> genotypeCount) {
            this.genotypeCount = genotypeCount;
            return this;
        }
    }
}
