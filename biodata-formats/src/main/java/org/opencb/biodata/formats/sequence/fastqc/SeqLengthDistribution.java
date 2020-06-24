package org.opencb.biodata.formats.sequence.fastqc;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeqLengthDistribution {

    private Map<Integer, Double> values;
    private String file;

    public SeqLengthDistribution() {
        this(new LinkedHashMap(), "");
    }

    public SeqLengthDistribution(Map<Integer, Double> values, String file) {
        this.values = values;
        this.file = file;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SeqLengthDistribution{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Map<Integer, Double> getValues() {
        return values;
    }

    public SeqLengthDistribution setValues(Map<Integer, Double> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public SeqLengthDistribution setFile(String file) {
        this.file = file;
        return this;
    }
}
