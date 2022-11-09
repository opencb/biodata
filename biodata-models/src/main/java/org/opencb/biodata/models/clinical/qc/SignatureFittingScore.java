package org.opencb.biodata.models.clinical.qc;

public class SignatureFittingScore {

    private String signatureId;
    private double value;

    public SignatureFittingScore() {
    }

    public SignatureFittingScore(String signatureId, double value) {
        this.signatureId = signatureId;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SignatureFittingScore{");
        sb.append("signatureId='").append(signatureId).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    public String getSignatureId() {
        return signatureId;
    }

    public SignatureFittingScore setSignatureId(String signatureId) {
        this.signatureId = signatureId;
        return this;
    }

    public double getValue() {
        return value;
    }

    public SignatureFittingScore setValue(double value) {
        this.value = value;
        return this;
    }
}
