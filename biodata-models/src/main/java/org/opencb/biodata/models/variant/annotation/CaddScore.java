package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class CaddScore {

    private String transcriptId;
    private float cScore;
    private float rawScore;

    public CaddScore(String transcriptId, float cScore, float rawScore) {
        this.transcriptId = transcriptId;
        this.cScore = cScore;
        this.rawScore = rawScore;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public float getcScore() {
        return cScore;
    }

    public void setcScore(float cScore) {
        this.cScore = cScore;
    }

    public float getRawScore() {
        return rawScore;
    }

    public void setRawScore(float rawScore) {
        this.rawScore = rawScore;
    }

}
