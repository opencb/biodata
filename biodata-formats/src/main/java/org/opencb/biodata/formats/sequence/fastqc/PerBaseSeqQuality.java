package org.opencb.biodata.formats.sequence.fastqc;

import java.util.ArrayList;
import java.util.List;

public class PerBaseSeqQuality {

    private List<Value> values;
    private String file;

    public PerBaseSeqQuality() {
        this(new ArrayList<>(), "");
    }

    public PerBaseSeqQuality(List<Value> values, String file) {
        this.values = values;
        this.file = file;
    }

    // #Base	Mean	Median	Lower Quartile	Upper Quartile	10th Percentile	90th Percentile
    public static class Value {

        private String base;
        private double mean;
        private double median;
        private double quartileLower;
        private double quartileUpper;
        private double percentile10th;
        private double percentile90th;

        public Value() {
        }

        public Value(String base, double mean, double median, double quartileLower, double quartileUpper, double percentile10th,
                                 double percentile90th) {
            this.base = base;
            this.mean = mean;
            this.median = median;
            this.quartileLower = quartileLower;
            this.quartileUpper = quartileUpper;
            this.percentile10th = percentile10th;
            this.percentile90th = percentile90th;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PerBaseSeqQuality{");
            sb.append("base='").append(base).append('\'');
            sb.append(", mean=").append(mean);
            sb.append(", median=").append(median);
            sb.append(", quartileLower=").append(quartileLower);
            sb.append(", quartileUpper=").append(quartileUpper);
            sb.append(", percentile10th=").append(percentile10th);
            sb.append(", percentile90th=").append(percentile90th);
            sb.append('}');
            return sb.toString();
        }

        public String getBase() {
            return base;
        }

        public Value setBase(String base) {
            this.base = base;
            return this;
        }

        public double getMean() {
            return mean;
        }

        public Value setMean(double mean) {
            this.mean = mean;
            return this;
        }

        public double getMedian() {
            return median;
        }

        public Value setMedian(double median) {
            this.median = median;
            return this;
        }

        public double getQuartileLower() {
            return quartileLower;
        }

        public Value setQuartileLower(double quartileLower) {
            this.quartileLower = quartileLower;
            return this;
        }

        public double getQuartileUpper() {
            return quartileUpper;
        }

        public Value setQuartileUpper(double quartileUpper) {
            this.quartileUpper = quartileUpper;
            return this;
        }

        public double getPercentile10th() {
            return percentile10th;
        }

        public Value setPercentile10th(double percentile10th) {
            this.percentile10th = percentile10th;
            return this;
        }

        public double getPercentile90th() {
            return percentile90th;
        }

        public Value setPercentile90th(double percentile90th) {
            this.percentile90th = percentile90th;
            return this;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerBaseSeqQuality{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public List<Value> getValues() {
        return values;
    }

    public PerBaseSeqQuality setValues(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public PerBaseSeqQuality setFile(String file) {
        this.file = file;
        return this;
    }
}
