package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 20/11/14.
 */
public class ExpressionValue {

    private String tissueName;
    private String experiment;
    private Float value;

    public ExpressionValue(String tissueName, String experiment, Float value) {
        this.tissueName = tissueName;
        this.experiment = experiment;
        this.value = value;
    }

    public String getTissueName() {
        return tissueName;
    }

    public void setTissueName(String tissueName) {
        this.tissueName = tissueName;
    }

    public String getExperiment() {
        return experiment;
    }

    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
