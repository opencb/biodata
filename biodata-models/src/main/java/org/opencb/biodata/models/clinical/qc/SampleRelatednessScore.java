package org.opencb.biodata.models.clinical.qc;

public class SampleRelatednessScore {

    private String sampleId1;
    private String sampleId2;
    private double score;

    public SampleRelatednessScore() {

    }

    public SampleRelatednessScore(String sampleId1, String sampleId2, double score) {
        this.sampleId1 = sampleId1;
        this.sampleId2 = sampleId2;
        this.score = score;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampleRelatednessScore{");
        sb.append("sampleId1='").append(sampleId1).append('\'');
        sb.append(", sampleId2='").append(sampleId2).append('\'');
        sb.append(", score=").append(score);
        sb.append('}');
        return sb.toString();
    }

    public String getSampleId1() {
        return sampleId1;
    }

    public SampleRelatednessScore setSampleId1(String sampleId1) {
        this.sampleId1 = sampleId1;
        return this;
    }

    public String getSampleId2() {
        return sampleId2;
    }

    public SampleRelatednessScore setSampleId2(String sampleId2) {
        this.sampleId2 = sampleId2;
        return this;
    }

    public double getScore() {
        return score;
    }

    public SampleRelatednessScore setScore(double score) {
        this.score = score;
        return this;
    }
}
