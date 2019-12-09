package org.opencb.biodata.models.alignment;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.opencb.biodata.models.core.Region;

import java.io.IOException;
import java.util.Arrays;


public class RegionCoverage extends Region {

    private int windowSize;
    private float[] values;
    private Stats stats;

    public class Stats {
        private int min;
        private int max;
        private float average;

        public Stats() {
        }

        public Stats(int min, int max, float average) {
            this.min = min;
            this.max = max;
            this.average = average;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Stats{");
            sb.append("min=").append(min);
            sb.append(", max=").append(max);
            sb.append(", average=").append(average);
            sb.append('}');
            return sb.toString();
        }

        public int getMin() {
            return min;
        }

        public Stats setMin(int min) {
            this.min = min;
            return this;
        }

        public int getMax() {
            return max;
        }

        public Stats setMax(int max) {
            this.max = max;
            return this;
        }

        public float getAverage() {
            return average;
        }

        public Stats setAverage(float average) {
            this.average = average;
            return this;
        }
    }

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
        updateStats();
    }

    public void updateStats() {
        if (values.length > 0) {
            float min = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            float agg = 0;
            for (float value : values) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
                agg += value;
            }
            stats = new Stats(Math.round(min), Math.round(max), agg / values.length);
        }
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

    public float[] getValues() {
        return values;
    }

    public RegionCoverage setValues(float[] values) {
        this.values = values;
        return this;
    }

    public Stats getStats() {
        return stats;
    }

    public RegionCoverage setStats(Stats stats) {
        this.stats = stats;
        return this;
    }
}
