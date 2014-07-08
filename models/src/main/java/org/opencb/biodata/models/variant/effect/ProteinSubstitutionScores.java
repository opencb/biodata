package org.opencb.biodata.models.variant.effect;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class ProteinSubstitutionScores {
    
    public enum PolyphenEffect { PROBABLY_DAMAGING, POSSIBLY_DAMAGING, BENIGN, UNKNOWN};
    public enum SiftEffect { TOLERATED, DELETERIOUS };
    
    private float polyphenScore;
    
    private float siftScore;
    
    private PolyphenEffect polyphenEffect;
    
    private SiftEffect siftEffect;

    ProteinSubstitutionScores() {
        this(-1, -1, null, null);
    }

    public ProteinSubstitutionScores(float polyphenScore, float siftScore, PolyphenEffect polyphenEffect, SiftEffect siftEffect) {
        this.polyphenScore = polyphenScore;
        this.siftScore = siftScore;
        this.polyphenEffect = polyphenEffect;
        this.siftEffect = siftEffect;
    }

    public float getPolyphenScore() {
        return polyphenScore;
    }

    public void setPolyphenScore(float polyphenScore) {
        this.polyphenScore = polyphenScore;
    }

    public float getSiftScore() {
        return siftScore;
    }

    public void setSiftScore(float siftScore) {
        this.siftScore = siftScore;
    }

    public PolyphenEffect getPolyphenEffect() {
        return polyphenEffect;
    }

    public void setPolyphenEffect(PolyphenEffect polyphenEffect) {
        this.polyphenEffect = polyphenEffect;
    }

    public SiftEffect getSiftEffect() {
        return siftEffect;
    }

    public void setSiftEffect(SiftEffect siftEffect) {
        this.siftEffect = siftEffect;
    }

    
}
