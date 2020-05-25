package org.opencb.biodata.models.alignment;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.opencb.biodata.models.core.Region;

import java.io.IOException;
import java.util.Arrays;


public class RegionCoverage extends Region {

    private int windowSize;
    private double[] values;
    private RegionCoverageStats stats;

    public RegionCoverage() {
    }

    public RegionCoverage(Region region) {
        this(region.getChromosome(), region.getStart(), region.getEnd());
    }

    public RegionCoverage(String chromosome, int start, int end) {
        super(chromosome, start, end);

        windowSize = 1;
        if (end >= start) {
            this.values = new double[end - start + 1];
        } else {
            this.values = new double[0];
        }
    }

    public RegionCoverage(Region region, int windowSize, double[] values) {
        super(region.getChromosome(), region.getStart(), region.getEnd());

        this.windowSize = windowSize;
        this.values = values;
        updateStats();
    }

    public RegionCoverage(Region region, int windowSize, double[] values, RegionCoverageStats stats) {
        super(region.getChromosome(), region.getStart(), region.getEnd());

        this.windowSize = windowSize;
        this.values = values;
        this.stats = stats;
    }

    public void updateStats() {
        if (values.length > 0) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double agg = 0;
            for (double value : values) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
                agg += value;
            }
            stats = new RegionCoverageStats((int) Math.round(min), (int) Math.round(max), agg / values.length);
        }
    }

    public int meanCoverage() {
        int mean = 0;
        if (values.length == 0) {
            return mean;
        }

        for (double value: values) {
            mean += value;
        }
        return Math.round(1.0f * mean / values.length);
    }

    public String toJSON() throws IOException {
        ObjectWriter objectWriter = new ObjectMapper().writer();
        return objectWriter.writeValueAsString(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegionCoverage{");
        sb.append("windowSize=").append(windowSize);
        sb.append(", values=").append(Arrays.toString(values));
        sb.append(", stats=").append(stats);
        sb.append('}');
        return sb.toString();
    }

    public int getWindowSize() {
        return windowSize;
    }

    public RegionCoverage setWindowSize(int windowSize) {
        this.windowSize = windowSize;
        return this;
    }

    public double[] getValues() {
        return values;
    }

    public RegionCoverage setValues(double[] values) {
        this.values = values;
        return this;
    }

    public RegionCoverageStats getStats() {
        return stats;
    }

    public RegionCoverage setStats(RegionCoverageStats stats) {
        this.stats = stats;
        return this;
    }
}
