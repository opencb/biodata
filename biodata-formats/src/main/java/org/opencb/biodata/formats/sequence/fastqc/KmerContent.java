package org.opencb.biodata.formats.sequence.fastqc;

import java.util.ArrayList;
import java.util.List;

public class KmerContent {

    private List<Value> values;
    private String file;

    public KmerContent() {
        this(new ArrayList<>(), "");
    }

    public KmerContent(List<Value> values, String file) {
        this.values = values;
        this.file = file;
    }

    // #Sequence	Count	PValue	Obs/Exp Max	Max Obs/Exp Position
    public static class Value {
        private String sequence;
        private int count;
        private double pValue;
        private double obsExpMax;
        private String maxObsExpPosition;

        public Value() {
        }

        public Value(String sequence, int count, double pValue, double obsExpMax, String maxObsExpPosition){
            this.sequence = sequence;
            this.count = count;
            this.pValue = pValue;
            this.obsExpMax = obsExpMax;
            this.maxObsExpPosition = maxObsExpPosition;
        }

        @Override
        public String toString () {
            final StringBuilder sb = new StringBuilder("KmerContent{");
            sb.append("sequence='").append(sequence).append('\'');
            sb.append(", count=").append(count);
            sb.append(", pValue=").append(pValue);
            sb.append(", obsExpMax=").append(obsExpMax);
            sb.append(", maxObsExpPosition='").append(maxObsExpPosition).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String getSequence () {
            return sequence;
        }

        public Value setSequence (String sequence){
            this.sequence = sequence;
            return this;
        }

        public int getCount () {
            return count;
        }

        public Value setCount ( int count){
            this.count = count;
            return this;
        }

        public double getpValue () {
            return pValue;
        }

        public Value setpValue ( double pValue){
            this.pValue = pValue;
            return this;
        }

        public double getObsExpMax () {
            return obsExpMax;
        }

        public Value setObsExpMax ( double obsExpMax){
            this.obsExpMax = obsExpMax;
            return this;
        }

        public String getMaxObsExpPosition () {
            return maxObsExpPosition;
        }

        public Value setMaxObsExpPosition (String maxObsExpPosition){
            this.maxObsExpPosition = maxObsExpPosition;
            return this;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KmerContent{");
        sb.append("values=").append(values);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public List<Value> getValues() {
        return values;
    }

    public KmerContent setValues(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getFile() {
        return file;
    }

    public KmerContent setFile(String file) {
        this.file = file;
        return this;
    }
}
