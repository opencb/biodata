package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

public class ClinicalVariantConfidence {

    @DataField(id = "value", description = FieldConstants.CLINICAL_CONFIDENCE_VALUE_DESCRIPTION)
    private Confidence value;

    @DataField(id = "author", description = FieldConstants.CLINICAL_CONFIDENCE_AUTHOR_DESCRIPTION)
    private String author;

    @DataField(id = "date", description = FieldConstants.CLINICAL_CONFIDENCE_DATE_DESCRIPTION)
    private String date;

    public enum Confidence {
        HIGH,
        MEDIUM,
        LOW
    }

    public ClinicalVariantConfidence() {
    }

    public ClinicalVariantConfidence(Confidence value, String author, String date) {
        this.value = value;
        this.author = author;
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantConfidence{");
        sb.append("value=").append(value);
        sb.append(", author='").append(author).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Confidence getValue() {
        return value;
    }

    public ClinicalVariantConfidence setValue(Confidence value) {
        this.value = value;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public ClinicalVariantConfidence setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ClinicalVariantConfidence setDate(String date) {
        this.date = date;
        return this;
    }
}
