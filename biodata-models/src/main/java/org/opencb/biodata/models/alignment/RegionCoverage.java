package org.opencb.biodata.models.alignment;

import org.opencb.biodata.models.core.Region;

import java.util.Arrays;


public class RegionCoverage {

    private String chromosome;
    private int start;
    private int end;
    private short[] values;
    private int windowSize;

    public RegionCoverage() {
    }

    public RegionCoverage(Region region) {
        this(region.getChromosome(), region.getStart(), region.getEnd());
    }

    public RegionCoverage(String chromosome, int start, int end) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        if (end >= start) {
            this.values = new short[end - start];
        } else {
            this.values = new short[0];
        }
    }

    public RegionCoverage(String chromosome, int start, int end, short[] values, int windowSize) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.values = values;
        this.windowSize = windowSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegionDepth{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", array=").append(Arrays.toString(values));
        sb.append(", windowSize=").append(windowSize);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public RegionCoverage setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public RegionCoverage setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public RegionCoverage setEnd(int end) {
        this.end = end;
        return this;
    }

    public short[] getValues() {
        return values;
    }

    public RegionCoverage setValues(short[] values) {
        this.values = values;
        return this;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public RegionCoverage setWindowSize(int windowSize) {
        this.windowSize = windowSize;
        return this;
    }

}
