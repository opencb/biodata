package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class Score {

    private float score;
    private String src;
    private String version;

    public Score(float score, String src, String version) {
        this.score = score;
        this.src = src;
        this.version = version;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
