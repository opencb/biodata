package org.opencb.biodata.models.variant.CADD;

/**
 * @author antonior on 5/22/14.
 * @author Luis Miguel Cruz.
 * @since October 08, 2014 
 */
public class CaddValues {
    /***
     * Cadd score
     */
    private float cscore;

    /***
     * Cadd score PHRED scale
     */
    private float phred;

    /***
     * GenomicFeature
     */
    private String GenomicFeature;


    public CaddValues(float cscore, float phred, String genomicFeature) {
        this.cscore = cscore;
        this.phred = phred;
        this.GenomicFeature = genomicFeature;
    }

    public float getCscore() {
        return cscore;
    }

    public void setCscore(float cscore) {
        this.cscore = cscore;
    }

    public float getPhred() {
        return phred;
    }

    public void setPhred(float phred) {
        this.phred = phred;
    }


    public String getGenomicFeature() {
        return GenomicFeature;
    }

    public void setGenomicFeature(String genomicFeature) {
        GenomicFeature = genomicFeature;
    }
}