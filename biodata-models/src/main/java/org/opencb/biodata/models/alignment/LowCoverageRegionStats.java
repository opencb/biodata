package org.opencb.biodata.models.alignment;

public class LowCoverageRegionStats {

    private String chromosome;
    private int start;
    private int end;
    private double depthAvg;
    private double depthMin;

    public LowCoverageRegionStats() {
    }

    public LowCoverageRegionStats(String chromosome, int start, int end, double depthAvg, double depthMin) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.depthAvg = depthAvg;
        this.depthMin = depthMin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LowCoverageRegionStats{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", depthAvg=").append(depthAvg);
        sb.append(", depthMin=").append(depthMin);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public LowCoverageRegionStats setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public LowCoverageRegionStats setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public LowCoverageRegionStats setEnd(int end) {
        this.end = end;
        return this;
    }

    public double getDepthAvg() {
        return depthAvg;
    }

    public LowCoverageRegionStats setDepthAvg(double depthAvg) {
        this.depthAvg = depthAvg;
        return this;
    }

    public double getDepthMin() {
        return depthMin;
    }

    public LowCoverageRegionStats setDepthMin(double depthMin) {
        this.depthMin = depthMin;
        return this;
    }
}
