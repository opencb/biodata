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

}
