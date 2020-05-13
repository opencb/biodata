package org.opencb.biodata.models.alignment;

public class LowCoverageRegion {

    private int start;
    private int end;
    private double depthAvg;
    private double depthMin;

    public LowCoverageRegion() {
    }

    public LowCoverageRegion(int start, int end, double depthAvg, double depthMin) {
        this.start = start;
        this.end = end;
        this.depthAvg = depthAvg;
        this.depthMin = depthMin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LowCoverageRegion{");
        sb.append("start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", depthAvg=").append(depthAvg);
        sb.append(", depthMin=").append(depthMin);
        sb.append('}');
        return sb.toString();
    }

    public int getStart() {
        return start;
    }

    public LowCoverageRegion setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public LowCoverageRegion setEnd(int end) {
        this.end = end;
        return this;
    }

    public double getDepthAvg() {
        return depthAvg;
    }

    public LowCoverageRegion setDepthAvg(double depthAvg) {
        this.depthAvg = depthAvg;
        return this;
    }

    public double getDepthMin() {
        return depthMin;
    }

    public LowCoverageRegion setDepthMin(double depthMin) {
        this.depthMin = depthMin;
        return this;
    }
}
