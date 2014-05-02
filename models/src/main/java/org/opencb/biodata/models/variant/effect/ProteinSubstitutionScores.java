package org.opencb.biodata.models.variant.effect;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class ProteinSubstitutionScores {
    
    public enum PolyphenEffect { DAMAGING, POSSIBLY_DAMAGING, BENING, UNKNOWN};
    public enum SiftEffect { TOLERATED, DELETERIOUS };
    
    private double polyphenScore;
    
    private double siftScore;
    
    private PolyphenEffect polyphenEffect;
    
    private SiftEffect siftEffect;

    public ProteinSubstitutionScores() {
    }

    public ProteinSubstitutionScores(double polyphenScore, double siftScore, PolyphenEffect polyphenEffect, SiftEffect siftEffect) {
        this.polyphenScore = polyphenScore;
        this.siftScore = siftScore;
        this.polyphenEffect = polyphenEffect;
        this.siftEffect = siftEffect;
    }

    public double getPolyphenScore() {
        return polyphenScore;
    }

    public void setPolyphenScore(double polyphenScore) {
        this.polyphenScore = polyphenScore;
    }

    public double getSiftScore() {
        return siftScore;
    }

    public void setSiftScore(double siftScore) {
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
