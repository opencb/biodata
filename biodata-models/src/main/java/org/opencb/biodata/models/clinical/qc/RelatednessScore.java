package org.opencb.biodata.models.clinical.qc;

import java.util.LinkedHashMap;
import java.util.Map;

public class RelatednessScore {
    // Pair of samples
    private String sampleId1;
    private String sampleId2;

    // Reported relationship according to pedigree
    private String reportedRelationship;

    // Inferred relationship according to relatedness scores
    private String inferredRelationship;

    // Scores depending on the relatedness method
    private Map<String, Object> values;

    public RelatednessScore() {
        this("", "", "", "", new LinkedHashMap<>());
    }

    @Deprecated
    public RelatednessScore(String sampleId1, String sampleId2, String inferredRelationship, Map<String, Object> values) {
        this(sampleId1, sampleId2, "", inferredRelationship, values);
    }

    public RelatednessScore(String sampleId1, String sampleId2, String reportedRelationship, String inferredRelationship,
                            Map<String, Object> values) {
        this.sampleId1 = sampleId1;
        this.sampleId2 = sampleId2;
        this.reportedRelationship = reportedRelationship;
        this.inferredRelationship = inferredRelationship;
        this.values = values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RelatednessScore{");
        sb.append("sampleId1='").append(sampleId1).append('\'');
        sb.append(", sampleId2='").append(sampleId2).append('\'');
        sb.append(", reportedRelationship='").append(reportedRelationship).append('\'');
        sb.append(", inferredRelationship='").append(inferredRelationship).append('\'');
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public String getSampleId1() {
        return sampleId1;
    }

    public RelatednessScore setSampleId1(String sampleId1) {
        this.sampleId1 = sampleId1;
        return this;
    }

    public String getSampleId2() {
        return sampleId2;
    }

    public RelatednessScore setSampleId2(String sampleId2) {
        this.sampleId2 = sampleId2;
        return this;
    }

    public String getReportedRelationship() {
        return reportedRelationship;
    }

    public RelatednessScore setReportedRelationship(String reportedRelationship) {
        this.reportedRelationship = reportedRelationship;
        return this;
    }

    public String getInferredRelationship() {
        return inferredRelationship;
    }

    public RelatednessScore setInferredRelationship(String inferredRelationship) {
        this.inferredRelationship = inferredRelationship;
        return this;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public RelatednessScore setValues(Map<String, Object> values) {
        this.values = values;
        return this;
    }
}
