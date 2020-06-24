package org.opencb.biodata.formats.sequence.fastqc;

import java.util.ArrayList;
import java.util.List;

public class PerBaseSeqContent {

    private List<Value> values;
    private String file;

    public PerBaseSeqContent() {
        this(new ArrayList<>(), "");
    }

    public PerBaseSeqContent(List<Value> values, String file) {
        this.values = values;
        this.file = file;
    }

    // #Base	G	A	T	C
    public static class Value {
        private String base;
        private double g;
        private double a;
        private double t;
        private double c;

        public Value() {
        }

        public Value(String base, double g, double a, double t, double c) {
            this.base = base;
            this.g = g;
            this.a = a;
            this.t = t;
            this.c = c;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PerBaseSeqContent{");
            sb.append("base='").append(base).append('\'');
            sb.append(", g=").append(g);
            sb.append(", a=").append(a);
            sb.append(", t=").append(t);
            sb.append(", c=").append(c);
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

        public double getG() {
            return g;
        }

        public Value setG(double g) {
            this.g = g;
            return this;
        }

        public double getA() {
            return a;
        }

        public Value setA(double a) {
            this.a = a;
            return this;
        }

        public double getT() {
            return t;
        }

        public Value setT(double t) {
            this.t = t;
            return this;
        }

        public double getC() {
            return c;
        }

        public Value setC(double c) {
            this.c = c;
            return this;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerBaseSeqContent{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public List<Value> getValues() {
        return values;
    }

    public PerBaseSeqContent setValues(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public PerBaseSeqContent setFile(String file) {
        this.file = file;
        return this;
    }
}
