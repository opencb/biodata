package org.opencb.biodata.models.clinical;

public class MutationalSignature {

    private Signature signature;
    private Fitting fitting;

    public MutationalSignature() {
    }

    public MutationalSignature(Signature signature, Fitting fitting) {
        this.signature = signature;
        this.fitting = fitting;
    }

    public Signature getSignature() {
        return signature;
    }

    public MutationalSignature setSignature(Signature signature) {
        this.signature = signature;
        return this;
    }

    public Fitting getFitting() {
        return fitting;
    }

    public MutationalSignature setFitting(Fitting fitting) {
        this.fitting = fitting;
        return this;
    }

    public static class Signature {
        private String type; // SNV, INDEL
        private Count[] counts;

        public Signature() {
        }

        public Signature(String type, Count[] counts) {
            this.type = type;
            this.counts = counts;
        }

        public String getType() {
            return type;
        }

        public Signature setType(String type) {
            this.type = type;
            return this;
        }

        public Count[] getCounts() {
            return counts;
        }

        public Signature setCounts(Count[] counts) {
            this.counts = counts;
            return this;
        }

        public static class Count {
            private String context;
            private long total;

            public Count() {
            }

            public Count(String context, long total) {
                this.context = context;
                this.total = total;
            }

            public String getContext() {
                return context;
            }

            public Count setContext(String context) {
                this.context = context;
                return this;
            }

            public long getTotal() {
                return total;
            }

            public Count setTotal(long total) {
                this.total = total;
                return this;
            }
        }
    }

    public static class Fitting {
        private String method;
        private String signatureSource;
        private String signatureVersion;
        private Score[] scores;
        private double coeff;
        private String image;

        public Fitting() {
        }

        public Fitting(String method, String signatureSource, String signatureVersion, Score[] scores, double coeff, String image) {
            this.method = method;
            this.signatureSource = signatureSource;
            this.signatureVersion = signatureVersion;
            this.scores = scores;
            this.coeff = coeff;
            this.image = image;
        }

        public String getMethod() {
            return method;
        }

        public Fitting setMethod(String method) {
            this.method = method;
            return this;
        }

        public String getSignatureSource() {
            return signatureSource;
        }

        public Fitting setSignatureSource(String signatureSource) {
            this.signatureSource = signatureSource;
            return this;
        }

        public String getSignatureVersion() {
            return signatureVersion;
        }

        public Fitting setSignatureVersion(String signatureVersion) {
            this.signatureVersion = signatureVersion;
            return this;
        }

        public Score[] getScores() {
            return scores;
        }

        public Fitting setScores(Score[] scores) {
            this.scores = scores;
            return this;
        }

        public double getCoeff() {
            return coeff;
        }

        public Fitting setCoeff(double coeff) {
            this.coeff = coeff;
            return this;
        }

        public String getImage() {
            return image;
        }

        public Fitting setImage(String image) {
            this.image = image;
            return this;
        }

        public static class Score {
            private String signatureId;
            private double value;

            public Score() {
            }

            public Score(String signatureId, double value) {
                this.signatureId = signatureId;
                this.value = value;
            }

            public String getSignatureId() {
                return signatureId;
            }

            public Score setSignatureId(String signatureId) {
                this.signatureId = signatureId;
                return this;
            }

            public double getValue() {
                return value;
            }

            public Score setValue(double value) {
                this.value = value;
                return this;
            }
        }
    }
}
