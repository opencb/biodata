package org.opencb.biodata.formats.sequence.fastqc;

import java.util.LinkedHashMap;
import java.util.Map;

public class PerBaseNContent {
    private Map<String, Double> values;
    private String file;

    public PerBaseNContent() {
        this(new LinkedHashMap<>(), "");
    }

    public PerBaseNContent(Map<String, Double> values, String file) {
        this.values = values;
        this.file = file;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerBaseNContent{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public PerBaseNContent setValues(Map<String, Double> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public PerBaseNContent setFile(String file) {
        this.file = file;
        return this;
    }
}
