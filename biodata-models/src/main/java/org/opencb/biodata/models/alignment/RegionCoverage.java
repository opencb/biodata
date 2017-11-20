package org.opencb.biodata.models.alignment;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.opencb.biodata.models.core.Region;

import java.io.IOException;
import java.util.Arrays;


public class RegionCoverage extends Region {

    private int windowSize;
    private float[] values;

    public RegionCoverage() {
    }

    public RegionCoverage(Region region) {
        this(region.getChromosome(), region.getStart(), region.getEnd());
    }

    public RegionCoverage(String chromosome, int start, int end) {
        super(chromosome, start, end);

        windowSize = 1;
        if (end >= start) {
            this.values = new float[end - start + 1];
        } else {
            this.values = new float[0];
        }
    }

    public RegionCoverage(Region region, int windowSize, float[] values) {
        super(region.getChromosome(), region.getStart(), region.getEnd());
        this.windowSize = windowSize;
        this.values = values;
    }

    public int meanCoverage() {
        int mean = 0;
        if (values.length == 0) {
            return mean;
        }

        for (float value: values) {
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
        sb.append(", windowSize=").append(windowSize);
        sb.append(", values=").append(Arrays.toString(values));
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

    public float[] getValues() {
        return values;
    }

    public RegionCoverage setValues(float[] values) {
        this.values = values;
        return this;
    }
}
