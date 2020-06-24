package org.opencb.biodata.formats.sequence.fastqc;

import java.util.ArrayList;
import java.util.List;

public class PerTileSeqQuality {

    private List<Value> values;
    private String file;

    public PerTileSeqQuality() {
        this(new ArrayList<>(), "");
    }

    public PerTileSeqQuality(List<Value> values, String file) {
        this.values = values;
        this.file = file;
    }

    // #Tile	Base	Mean
    public static class Value {
        private String tile;
        private String base;
        private double mean;

        public Value() {
        }

        public Value(String tile, String base, double mean) {
            this.tile = tile;
            this.base = base;
            this.mean = mean;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Value{");
            sb.append("tile='").append(tile).append('\'');
            sb.append(", base='").append(base).append('\'');
            sb.append(", mean=").append(mean);
            sb.append('}');
            return sb.toString();
        }

        public String getTile() {
            return tile;
        }

        public Value setTile(String tile) {
            this.tile = tile;
            return this;
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
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerTileSeqQuality{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public List<Value> getValues() {
        return values;
    }

    public PerTileSeqQuality setValues(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public PerTileSeqQuality setFile(String file) {
        this.file = file;
        return this;
    }
}
