package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class Score {

    private Double score;
    private String source;
    private String description;

    public Score(Double score, String source) {
        this.score = score;
        this.source = source;
    }

    public Score(Double score, String source, String description) {
        this.score = score;
        this.source = source;
        this.description = description;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
