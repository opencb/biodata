package org.opencb.biodata.formats.sequence.fastqc;

import java.util.LinkedHashMap;
import java.util.Map;

public class PerSeqQualityScore {

    private Map<Integer, Double> values;
    private String file;

    public PerSeqQualityScore() {
        this(new LinkedHashMap(), "");
    }

    public PerSeqQualityScore(Map<Integer, Double> values, String file) {
        this.values = values;
        this.file = file;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerSeqQualityScore{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Map<Integer, Double> getValues() {
        return values;
    }

    public PerSeqQualityScore setValues(Map<Integer, Double> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public PerSeqQualityScore setFile(String file) {
        this.file = file;
        return this;
    }
}
