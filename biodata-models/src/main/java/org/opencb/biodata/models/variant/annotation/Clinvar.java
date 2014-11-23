package org.opencb.biodata.models.variant.annotation;

import java.util.List;

/**
 * Created by antonior on 21/11/14.
 */
public class Clinvar {

    private String acc;
    private String clinicalSignificance;
    private List <String> traits;
    private List<String> geneName;
    private String reviewStatus;

    public Clinvar(String acc, String clinicalSignificance, List<String> traits, List<String> geneName, String reviewStatus) {
        this.acc = acc;
        this.clinicalSignificance = clinicalSignificance;
        this.traits = traits;
        this.geneName = geneName;
        this.reviewStatus = reviewStatus;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getClinicalSignificance() {
        return clinicalSignificance;
    }

    public void setClinicalSignificance(String clinicalSignificance) {
        this.clinicalSignificance = clinicalSignificance;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }

    public List<String> getGeneName() {
        return geneName;
    }

    public void setGeneName(List<String> geneName) {
        this.geneName = geneName;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
