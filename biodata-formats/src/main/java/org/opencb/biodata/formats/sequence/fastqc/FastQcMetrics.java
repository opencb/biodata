package org.opencb.biodata.formats.sequence.fastqc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FastQcMetrics {

    private Summary summary;
    private Map<String, String> basicStats;
    private List<String> files;

    public FastQcMetrics() {
        summary = new Summary();
        basicStats = new LinkedHashMap<>();
        files = new ArrayList<>();
    }

    public FastQcMetrics(Summary summary, Map<String, String> basicStats, List<String> files) {
        this.summary = summary;
        this.basicStats = basicStats;
        this.files = files;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FastQcMetrics{");
        sb.append("summary=").append(summary);
        sb.append(", basicStats=").append(basicStats);
        sb.append(", files=").append(files);
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

    public List<String> getFiles() {
        return files;
    }

    public FastQcMetrics setFiles(List<String> files) {
        this.files = files;
        return this;
    }
}
