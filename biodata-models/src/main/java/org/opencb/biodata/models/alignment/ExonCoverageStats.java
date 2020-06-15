package org.opencb.biodata.models.alignment;

public class ExonCoverageStats {

    private String id;
    private String chromosome;
    private int start;
    private int end;
    private double depthAvg;
    private double depthMin;
    private double depthMax;

    public ExonCoverageStats() {
    }

    public ExonCoverageStats(String id, String chromosome, int start, int end, double depthAvg, double depthMin, double depthMax) {
        this.id = id;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.depthAvg = depthAvg;
        this.depthMin = depthMin;
        this.depthMax = depthMax;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExonCoverageStats{");
        sb.append("id='").append(id).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", depthAvg=").append(depthAvg);
        sb.append(", depthMin=").append(depthMin);
        sb.append(", depthMax=").append(depthMax);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ExonCoverageStats setId(String id) {
        this.id = id;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public ExonCoverageStats setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public ExonCoverageStats setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public ExonCoverageStats setEnd(int end) {
        this.end = end;
        return this;
    }

    public double getDepthAvg() {
        return depthAvg;
    }

    public ExonCoverageStats setDepthAvg(double depthAvg) {
        this.depthAvg = depthAvg;
        return this;
    }

    public double getDepthMin() {
        return depthMin;
    }

    public ExonCoverageStats setDepthMin(double depthMin) {
        this.depthMin = depthMin;
        return this;
    }

    public double getDepthMax() {
        return depthMax;
    }

    public ExonCoverageStats setDepthMax(double depthMax) {
        this.depthMax = depthMax;
        return this;
    }
}
