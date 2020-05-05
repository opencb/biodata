package org.opencb.biodata.models.alignment;

import java.util.Arrays;
import java.util.List;

public class TranscriptCoverageStats {

    private String transcriptId;
    private int length; // as the sum of lengths of the exons
    private double[] depths; // % coverage for 1x, 5x, 10x, 15x, 20x, 25x, 30x, 40x, 50x, 60x
    private int lowCoverageThreshold;
    private List<LowCoverageRegion> lowCoverageRegions;

    public TranscriptCoverageStats() {
        depths = new double[10];
    }

    public TranscriptCoverageStats(String transcriptId, int length, double[] depths, int lowCoverageThreshold,
                                   List<LowCoverageRegion> lowCoverageRegions) {
        this.transcriptId = transcriptId;
        this.length = length;
        this.depths = depths;
        this.lowCoverageThreshold = lowCoverageThreshold;
        this.lowCoverageRegions = lowCoverageRegions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranscriptCoverageStats{");
        sb.append("transcriptId='").append(transcriptId).append('\'');
        sb.append(", length=").append(length);
        sb.append(", depths=").append(Arrays.toString(depths));
        sb.append(", lowCoverageThreshold=").append(lowCoverageThreshold);
        sb.append(", lowCoverageRegions=").append(lowCoverageRegions);
        sb.append('}');
        return sb.toString();
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public TranscriptCoverageStats setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
        return this;
    }

    public int getLength() {
        return length;
    }

    public TranscriptCoverageStats setLength(int length) {
        this.length = length;
        return this;
    }

    public double[] getDepths() {
        return depths;
    }

    public TranscriptCoverageStats setDepths(double[] depths) {
        this.depths = depths;
        return this;
    }

    public int getLowCoverageThreshold() {
        return lowCoverageThreshold;
    }

    public TranscriptCoverageStats setLowCoverageThreshold(int lowCoverageThreshold) {
        this.lowCoverageThreshold = lowCoverageThreshold;
        return this;
    }

    public List<LowCoverageRegion> getLowCoverageRegions() {
        return lowCoverageRegions;
    }

    public TranscriptCoverageStats setLowCoverageRegions(List<LowCoverageRegion> lowCoverageRegions) {
        this.lowCoverageRegions = lowCoverageRegions;
        return this;
    }
}