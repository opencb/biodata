package org.opencb.biodata.models.variant.annotation;

/**
 * Created by antonior on 21/11/14.
 */
public class Cosmic {
    private String mutationID;
    private String primarySite;
    private String siteSubtype;
    private String primaryHistology;
    private String histologySubtype;
    private String sampleSource;
    private String tumourOrigin;
    private String geneName;
    private String mutationSomaticStatus;




    public Cosmic(String mutationID, String primarySite, String siteSubtype, String primaryHistology, String histologySubtype, String sampleSource, String tumourOrigin, String geneName, String mutationSomaticStatus) {
        this.mutationID = mutationID;
        this.primarySite = primarySite;
        this.siteSubtype = siteSubtype;
        this.primaryHistology = primaryHistology;
        this.histologySubtype = histologySubtype;
        this.sampleSource = sampleSource;
        this.tumourOrigin = tumourOrigin;
        this.geneName = geneName;
        this.mutationSomaticStatus = mutationSomaticStatus;
    }

    public String getMutationID() {
        return mutationID;
    }

    public void setMutationID(String mutationID) {
        this.mutationID = mutationID;
    }

    public String getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(String primarySite) {
        this.primarySite = primarySite;
    }

    public String getSiteSubtype() {
        return siteSubtype;
    }

    public void setSiteSubtype(String siteSubtype) {
        this.siteSubtype = siteSubtype;
    }

    public String getPrimaryHistology() {
        return primaryHistology;
    }

    public void setPrimaryHistology(String primaryHistology) {
        this.primaryHistology = primaryHistology;
    }

    public String getHistologySubtype() {
        return histologySubtype;
    }

    public void setHistologySubtype(String histologySubtype) {
        this.histologySubtype = histologySubtype;
    }

    public String getSampleSource() {
        return sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    public String getTumourOrigin() {
        return tumourOrigin;
    }

    public void setTumourOrigin(String tumourOrigin) {
        this.tumourOrigin = tumourOrigin;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getMutationSomaticStatus() {
        return mutationSomaticStatus;
    }

    public void setMutationSomaticStatus(String mutationSomaticStatus) {
        this.mutationSomaticStatus = mutationSomaticStatus;
    }

}
