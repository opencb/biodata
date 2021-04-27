package org.opencb.biodata.formats.sequence.fastqc;

import java.util.*;

public class FastQcMetrics {

    private Summary summary;
    private Map<String, String> basicStats;
    private List<String> images;

    public FastQcMetrics() {
        summary = new Summary();
        basicStats = new LinkedHashMap<>();
        images = new ArrayList<>();
    }

    public FastQcMetrics(Summary summary, Map<String, String> basicStats, List<String> images) {
        this.summary = summary;
        this.basicStats = basicStats;
        this.images = images;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FastQcMetrics{");
        sb.append("summary=").append(summary);
        sb.append(", basicStats=").append(basicStats);
        sb.append(", images=").append(images);
        sb.append('}');
        return sb.toString();
    }

    public Summary getSummary() {
        return summary;
    }

    public FastQcMetrics setSummary(Summary summary) {
        this.summary = summary;
        return this;
    }

    public Map<String, String> getBasicStats() {
        return basicStats;
    }

    public FastQcMetrics setBasicStats(Map<String, String> basicStats) {
        this.basicStats = basicStats;
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public FastQcMetrics setImages(List<String> images) {
        this.images = images;
        return this;
    }
}
