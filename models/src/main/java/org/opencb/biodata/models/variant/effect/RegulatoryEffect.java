package org.opencb.biodata.models.variant.effect;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * 
 * MOTIF_SCORE_CHANGE - The difference in motif score of the reference and variant sequences for the TFBP
 */
public class RegulatoryEffect {
    
    /**
     * Source and identifier of a transcription factor binding profile aligned at this position
     */
    private String motifName;
    
    /**
     * Relative position of the variation in the aligned TFBP
     */
    private int motifPosition;
    
    /**
     * If the variant falls in a high information position of a transcription factor binding profile (TFBP)
     */
    private boolean highInformationPosition;
    
    /**
     * List of cell types and classifications for regulatory feature
     */
    private String cellType;
    
}
