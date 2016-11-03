package org.opencb.biodata.models.alignment;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.opencb.biodata.models.core.Region;

import java.io.IOException;
import java.util.Arrays;


public class RegionCoverage extends Region {

    private short[] values;
    private int windowSize;

    public RegionCoverage() {
    }

    public RegionCoverage(Region region) {
        this(region.getChromosome(), region.getStart(), region.getEnd());
    }

    public RegionCoverage(String chromosome, int start, int end) {
        super(chromosome, start, end);
        windowSize = 1;
        if (end >= start) {
            this.values = new short[end - start + 1];
        } else {
            this.values = new short[0];
        }
    }

    public RegionCoverage(Region region, int windowSize, short[] values) {
        super(region.getChromosome(), region.getStart(), region.getEnd());
        this.windowSize = windowSize;
        this.values = values;
    }

    public int meanCoverage() {
        int mean = 0;
        if (values.length == 0) {
            return mean;
        }

        for (short value : values) {
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
        sb.append("chromosome='").append(getChromosome()).append('\'');
        sb.append(", start=").append(getStart());
        sb.append(", end=").append(getEnd());
        sb.append(", array=").append(Arrays.toString(values));
        sb.append(", windowSize=").append(windowSize);
        sb.append('}');
        return sb.toString();
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
