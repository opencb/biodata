package org.opencb.biodata.models.clinical.qc;

import java.util.List;

public class SampleRelatednessReport {

    private String method;
    private List<SampleRelatednessScore> scores;

    public SampleRelatednessReport() {
    }

    public SampleRelatednessReport(String method, List<SampleRelatednessScore> scores) {
        this.method = method;
        this.scores = scores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampleRelatednessReport{");
        sb.append("method='").append(method).append('\'');
        sb.append(", scores=").append(scores);
        sb.append('}');
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public SampleRelatednessReport setMethod(String method) {
        this.method = method;
        return this;
    }

    public List<SampleRelatednessScore> getScores() {
        return scores;
    }

    public SampleRelatednessReport setScores(List<SampleRelatednessScore> scores) {
        this.scores = scores;
        return this;
    }
}
