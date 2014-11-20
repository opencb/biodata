package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class PopulationFrequency {

    private String study;
    private String populationName;
    private String subPopulationName;
    private float frequency;

    public PopulationFrequency(String study, String populationName, String subPopulationName, float frequency) {
        this.study = study;
        this.populationName = populationName;
        this.subPopulationName = subPopulationName;
        this.frequency = frequency;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getPopulationName() {
        return populationName;
    }

    public void setPopulationName(String populationName) {
        this.populationName = populationName;
    }

    public String getSubPopulationName() {
        return subPopulationName;
    }

    public void setSubPopulationName(String subPopulationName) {
        this.subPopulationName = subPopulationName;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

}
