package org.opencb.biodata.models.alignment;

public class LowCoverageRegion {

    private int start;
    private int end;
    private float meanDepth;
    private float minimumDepth;

    public LowCoverageRegion() {
    }

    public LowCoverageRegion(int start, int end, float meanDepth, float minimumDepth) {
        this.start = start;
        this.end = end;
        this.meanDepth = meanDepth;
        this.minimumDepth = minimumDepth;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LowCoverageRegion{");
        sb.append("start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", meanDepth=").append(meanDepth);
        sb.append(", minimumDepth=").append(minimumDepth);
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

    public float getMeanDepth() {
        return meanDepth;
    }

    public LowCoverageRegion setMeanDepth(float meanDepth) {
        this.meanDepth = meanDepth;
        return this;
    }

    public float getMinimumDepth() {
        return minimumDepth;
    }

    public LowCoverageRegion setMinimumDepth(float minimumDepth) {
        this.minimumDepth = minimumDepth;
        return this;
    }
}
