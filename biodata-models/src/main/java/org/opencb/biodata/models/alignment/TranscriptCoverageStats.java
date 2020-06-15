package org.opencb.biodata.models.alignment;

import java.util.Arrays;
import java.util.List;

public class TranscriptCoverageStats {

    private String id;
    private String name;
    private String biotype;
    private String chromosome;
    private int start;
    private int end;
    private int length; // as the sum of lengths of the exons
    private double[] depths; // % coverage for 1x, 5x, 10x, 15x, 20x, 25x, 30x, 40x, 50x, 60x, 75x, 100x
    private int lowCoverageThreshold;
    private List<LowCoverageRegionStats> lowCoverageRegionStats;
    private List<ExonCoverageStats> exonStats;

    public TranscriptCoverageStats() {
        depths = new double[12];
    }

    public TranscriptCoverageStats(String id, String name, String biotype, String chromosome, int start, int end, int length,
                                   double[] depths, int lowCoverageThreshold, List<LowCoverageRegionStats> lowCoverageRegionStats,
                                   List<ExonCoverageStats> exonStats) {
        this.id = id;
        this.name = name;
        this.biotype = biotype;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.length = length;
        this.depths = depths;
        this.lowCoverageThreshold = lowCoverageThreshold;
        this.lowCoverageRegionStats = lowCoverageRegionStats;
        this.exonStats = exonStats;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranscriptCoverageStats{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", biotype='").append(biotype).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", length=").append(length);
        sb.append(", depths=").append(Arrays.toString(depths));
        sb.append(", lowCoverageThreshold=").append(lowCoverageThreshold);
        sb.append(", lowCoverageRegionStats=").append(lowCoverageRegionStats);
        sb.append(", exonStats=").append(exonStats);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public TranscriptCoverageStats setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TranscriptCoverageStats setName(String name) {
        this.name = name;
        return this;
    }

    public String getBiotype() {
        return biotype;
    }

    public TranscriptCoverageStats setBiotype(String biotype) {
        this.biotype = biotype;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public TranscriptCoverageStats setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public TranscriptCoverageStats setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public TranscriptCoverageStats setEnd(int end) {
        this.end = end;
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

    public List<LowCoverageRegionStats> getLowCoverageRegionStats() {
        return lowCoverageRegionStats;
    }

    public TranscriptCoverageStats setLowCoverageRegionStats(List<LowCoverageRegionStats> lowCoverageRegionStats) {
        this.lowCoverageRegionStats = lowCoverageRegionStats;
        return this;
    }

    public List<ExonCoverageStats> getExonStats() {
        return exonStats;
    }

    public TranscriptCoverageStats setExonStats(List<ExonCoverageStats> exonStats) {
        this.exonStats = exonStats;
        return this;
    }
}