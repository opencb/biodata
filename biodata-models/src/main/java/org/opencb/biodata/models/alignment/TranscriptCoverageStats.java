package org.opencb.biodata.models.alignment;

import java.util.List;

public class TranscriptCoverageStats {

    private String transcriptId;
    private List<Float> depths; // % coverage for 1x, 5x, 10x, 15x, 20x, 25x, 30x, 40x, 50x
    private int lowCoverageThreshold;
    private List<LowCoverageRegion> lowCoverageRegions;

    public TranscriptCoverageStats() {
    }

    public TranscriptCoverageStats(String transcriptId, List<Float> depths, int lowCoverageThreshold,
                                   List<LowCoverageRegion> lowCoverageRegions) {
        this.transcriptId = transcriptId;
        this.depths = depths;
        this.lowCoverageThreshold = lowCoverageThreshold;
        this.lowCoverageRegions = lowCoverageRegions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranscriptCoverageStats{");
        sb.append("transcriptId='").append(transcriptId).append('\'');
        sb.append(", depths=").append(depths);
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

    public List<Float> getDepths() {
        return depths;
    }

    public TranscriptCoverageStats setDepths(List<Float> depths) {
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