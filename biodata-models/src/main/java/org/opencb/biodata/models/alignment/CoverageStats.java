package org.opencb.biodata.models.alignment;

import java.util.List;

public class CoverageStats {

    private String fileId;
    private String sampleId;

    private String geneName;
    List<TranscriptCoverageStats> stats;

    public CoverageStats() {
    }

    public CoverageStats(String fileId, String sampleId, String geneName, List<TranscriptCoverageStats> stats) {
        this.fileId = fileId;
        this.sampleId = sampleId;
        this.geneName = geneName;
        this.stats = stats;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CoverageStats{");
        sb.append("fileId='").append(fileId).append('\'');
        sb.append(", sampleId='").append(sampleId).append('\'');
        sb.append(", geneName='").append(geneName).append('\'');
        sb.append(", stats=").append(stats);
        sb.append('}');
        return sb.toString();
    }

    public String getFileId() {
        return fileId;
    }

    public CoverageStats setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getSampleId() {
        return sampleId;
    }

    public CoverageStats setSampleId(String sampleId) {
        this.sampleId = sampleId;
        return this;
    }

    public String getGeneName() {
        return geneName;
    }

    public CoverageStats setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public List<TranscriptCoverageStats> getStats() {
        return stats;
    }

    public CoverageStats setStats(List<TranscriptCoverageStats> stats) {
        this.stats = stats;
        return this;
    }
}

