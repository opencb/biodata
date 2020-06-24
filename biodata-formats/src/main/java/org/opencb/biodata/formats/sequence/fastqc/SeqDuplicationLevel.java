package org.opencb.biodata.formats.sequence.fastqc;

import java.util.ArrayList;
import java.util.List;

public class SeqDuplicationLevel {
// #Duplication Level	Percentage of deduplicated	Percentage of total

    private List<Value> values;
    private String file;

    public SeqDuplicationLevel() {
        this(new ArrayList<>(), "");
    }

    public SeqDuplicationLevel(List<Value> values, String file) {
        this.values = values;
        this.file = file;
    }

    public static class Value {
        private String level;
        private Double percDeduplicated;
        private Double percTotal;

        public Value() {
        }

        public Value(String level, Double percDeduplicated, Double percTotal) {
            this.level = level;
            this.percDeduplicated = percDeduplicated;
            this.percTotal = percTotal;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("SeqDuplicationLevel{");
            sb.append("level='").append(level).append('\'');
            sb.append(", percDeduplicated=").append(percDeduplicated);
            sb.append(", percTotal=").append(percTotal);
            sb.append('}');
            return sb.toString();
        }

        public String getLevel() {
            return level;
        }

        public Value setLevel(String level) {
            this.level = level;
            return this;
        }

        public Double getPercDeduplicated() {
            return percDeduplicated;
        }

        public Value setPercDeduplicated(Double percDeduplicated) {
            this.percDeduplicated = percDeduplicated;
            return this;
        }

        public Double getPercTotal() {
            return percTotal;
        }

        public Value setPercTotal(Double percTotal) {
            this.percTotal = percTotal;
            return this;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SeqDuplicationLevel{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public List<Value> getValues() {
        return values;
    }

    public SeqDuplicationLevel setValues(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public SeqDuplicationLevel setFile(String file) {
        this.file = file;
        return this;
    }
}
