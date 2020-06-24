package org.opencb.biodata.formats.sequence.fastqc;

import java.util.Arrays;

public class PerSeqGcContent {

    private double[] values;
    private String file;

    public PerSeqGcContent() {
        this(new double[101], "");
    }

    public PerSeqGcContent(double[] values, String file) {
        this.values = values;
        this.file = file;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerSeqGcContent{");
        sb.append("values=").append(Arrays.toString(values));
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public double[] getValues() {
        return values;
    }

    public PerSeqGcContent setValues(double[] values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public PerSeqGcContent setFile(String file) {
        this.file = file;
        return this;
    }
}
