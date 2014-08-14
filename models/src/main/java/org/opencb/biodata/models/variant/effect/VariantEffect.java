package org.opencb.biodata.models.variant.effect;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * 
 * @todo IND - individual name
 * @todo ZYG - zygosity of individual genotype at this locus
 */
public class VariantEffect {

    /**
     * Chromosome where the variant occurred
     */
    private String chromosome;
    
    /**
     * Genomic position
     */
    private int position;
    
    /**
     * Reference allele
     */
    private String referenceAllele;
    
    /**
     * Alternate allele
     */
    private String alternateAllele;

    /**
     * Ensembl stable ID of affected gene
     */
    private String geneId;
    
    /**
     * The gene symbol
     */
    private String geneName;
    
    /**
     * The gene symbol source
     */
    private String geneNameSource;
    
    /**
     * Ensembl stable ID of feature
     */
    private String featureId;
    
    /**
     * Type of feature, currently one of Transcript, RegulatoryFeature, MotifFeature
     */
    private String featureType;
    
    /**
     * Biotype of transcript or gene
     */
    private String featureBiotype;
    
    /**
     * The DNA strand (1 or -1) on which the transcript/feature lies
     */
    private String featureStrand;
    
    /**
     * Relative position of base pair in cDNA sequence
     */
    private int cDnaPosition;
    
    /**
     * The CCDS identifier for this transcript, where applicable
     */
    private String ccdsId;
    
    /**
     * Relative position of base pair in coding sequence
     */
    private int cdsPosition;
         
    /**
     * Ensembl protein identifier of the affected transcript
     */
    private String proteinId;
       
    /**
     * Relative position of amino acid in protein
     */
    private int proteinPosition;
    
    /**
     * Source and identifier of any overlapping protein domains
     */
    private String[] proteinDomains;
    
    /**
     * Only given if the variation affects the protein-coding sequence
     */
    private String aminoacidChange;
    
    /**
     * The alternative codons with the variant base in upper case
     */
    private String codonChange;

    /**
     * Known identifier of existing variation
     */
    private String variationId;
    
    /**
     * IDs of overlapping structural variants
     */
    private String[] structuralVariantsId;
            
    /**
     * Consequence type of this variation (SO code)
     */
    private int[] consequenceTypes;
    
    /**
     * Flag indicating if the transcript is denoted as the canonical transcript for this gene
     */
    private boolean canonical;
    
    /**
     * HGVS coding sequence name
     */
    private String hgvsc;
    
    /**
     * HGVS protein sequence name
     */
    private String hgvsp;
    
    /**
     * Intron number, out of total number
     */
    private String intronNumber;
    
    /**
     * Exon number, out of total number
     */
    private String exonNumber;
    
    /**
     * Shortest distance from variant to transcript
     */
    private int variantToTranscriptDistance;
    
    /**
     * Clinical significance of variant from dbSNP
     */
    private String clinicalSignificance;
            
    /**
     * Pubmed ID(s) of publications that cite existing variant
     */
    private String[] pubmed;

    VariantEffect() { 
        this(null, -1, null, null);
    }
    
    public VariantEffect(String chromosome, int position, String referenceAllele, String alternateAllele) {
        this.chromosome = chromosome;
        this.position = position;
        this.referenceAllele = referenceAllele;
        this.alternateAllele = alternateAllele;
        this.cDnaPosition = -1;
        this.cdsPosition = -1;
        this.proteinPosition = -1;
        this.variantToTranscriptDistance = -1;
        
        this.proteinDomains = new String[0];
        this.structuralVariantsId = new String[0];
        this.consequenceTypes = new int[0];
        this.pubmed = new String[0];
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public String getAlternateAllele() {
        return alternateAllele;
    }

    public void setAlternateAllele(String alternateAllele) {
        this.alternateAllele = alternateAllele;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getGeneNameSource() {
        return geneNameSource;
    }

    public void setGeneNameSource(String geneNameSource) {
        this.geneNameSource = geneNameSource;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFeatureBiotype() {
        return featureBiotype;
    }

    public void setFeatureBiotype(String featureBiotype) {
        this.featureBiotype = featureBiotype;
    }

    public String getFeatureStrand() {
        return featureStrand;
    }

    public void setFeatureStrand(String featureStrand) {
        this.featureStrand = featureStrand;
    }

    public int getcDnaPosition() {
        return cDnaPosition;
    }

    public void setcDnaPosition(int cDnaPosition) {
        this.cDnaPosition = cDnaPosition;
    }

    public String getCcdsId() {
        return ccdsId;
    }

    public void setCcdsId(String ccdsId) {
        this.ccdsId = ccdsId;
    }

    public int getCdsPosition() {
        return cdsPosition;
    }

    public void setCdsPosition(int cdsPosition) {
        this.cdsPosition = cdsPosition;
    }

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    public int getProteinPosition() {
        return proteinPosition;
    }

    public void setProteinPosition(int proteinPosition) {
        this.proteinPosition = proteinPosition;
    }

    public String[] getProteinDomains() {
        return proteinDomains;
    }

    public void setProteinDomains(String[] proteinDomains) {
        this.proteinDomains = proteinDomains;
    }

    public String getAminoacidChange() {
        return aminoacidChange;
    }

    public void setAminoacidChange(String aminoacidChange) {
        this.aminoacidChange = aminoacidChange;
    }

    public String getCodonChange() {
        return codonChange;
    }

    public void setCodonChange(String codonChange) {
        this.codonChange = codonChange;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public String[] getStructuralVariantsId() {
        return structuralVariantsId;
    }

    public void setStructuralVariantsId(String[] structuralVariantsId) {
        this.structuralVariantsId = structuralVariantsId;
    }

    public int[] getConsequenceTypes() {
        return consequenceTypes;
    }

    public void setConsequenceTypes(int[] consequenceTypes) {
        this.consequenceTypes = consequenceTypes;
    }

    public boolean isCanonical() {
        return canonical;
    }

    public void setCanonical(boolean canonical) {
        this.canonical = canonical;
    }

    public String getHgvsc() {
        return hgvsc;
    }

    public void setHgvsc(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    public String getHgvsp() {
        return hgvsp;
    }

    public void setHgvsp(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    public String getIntronNumber() {
        return intronNumber;
    }

    public void setIntronNumber(String intronNumber) {
        this.intronNumber = intronNumber;
    }

    public String getExonNumber() {
        return exonNumber;
    }

    public void setExonNumber(String exonNumber) {
        this.exonNumber = exonNumber;
    }

    public int getVariantToTranscriptDistance() {
        return variantToTranscriptDistance;
    }

    public void setVariantToTranscriptDistance(int variantToTranscriptDistance) {
        this.variantToTranscriptDistance = variantToTranscriptDistance;
    }

    public String getClinicalSignificance() {
        return clinicalSignificance;
    }

    public void setClinicalSignificance(String clinicalSignificance) {
        this.clinicalSignificance = clinicalSignificance;
    }

    public String[] getPubmed() {
        return pubmed;
    }

    public void setPubmed(String[] pubmed) {
        this.pubmed = pubmed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariantEffect other = (VariantEffect) obj;
        if (!Objects.equals(this.alternateAllele, other.alternateAllele)) {
            return false;
        }
        if (!Objects.equals(this.geneId, other.geneId)) {
            return false;
        }
        if (!Objects.equals(this.geneName, other.geneName)) {
            return false;
        }
        if (!Objects.equals(this.geneNameSource, other.geneNameSource)) {
            return false;
        }
        if (!Objects.equals(this.featureId, other.featureId)) {
            return false;
        }
        if (!Objects.equals(this.featureType, other.featureType)) {
            return false;
        }
        if (!Objects.equals(this.featureBiotype, other.featureBiotype)) {
            return false;
        }
        if (!Objects.equals(this.featureStrand, other.featureStrand)) {
            return false;
        }
        if (this.cDnaPosition != other.cDnaPosition) {
            return false;
        }
        if (!Objects.equals(this.ccdsId, other.ccdsId)) {
            return false;
        }
        if (this.cdsPosition != other.cdsPosition) {
            return false;
        }
        if (!Objects.equals(this.proteinId, other.proteinId)) {
            return false;
        }
        if (this.proteinPosition != other.proteinPosition) {
            return false;
        }
        if (!Objects.equals(this.aminoacidChange, other.aminoacidChange)) {
            return false;
        }
        if (!Objects.equals(this.codonChange, other.codonChange)) {
            return false;
        }
        if (!Objects.equals(this.variationId, other.variationId)) {
            return false;
        }
        if (!Arrays.equals(this.consequenceTypes, other.consequenceTypes)) {
            return false;
        }
        if (!Objects.equals(this.intronNumber, other.intronNumber)) {
            return false;
        }
        if (!Objects.equals(this.exonNumber, other.exonNumber)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.alternateAllele);
        hash = 97 * hash + Objects.hashCode(this.geneId);
        hash = 97 * hash + Objects.hashCode(this.geneName);
        hash = 97 * hash + Objects.hashCode(this.geneNameSource);
        hash = 97 * hash + Objects.hashCode(this.featureId);
        hash = 97 * hash + Objects.hashCode(this.featureType);
        hash = 97 * hash + Objects.hashCode(this.featureBiotype);
        hash = 97 * hash + Objects.hashCode(this.featureStrand);
        hash = 97 * hash + this.cDnaPosition;
        hash = 97 * hash + Objects.hashCode(this.ccdsId);
        hash = 97 * hash + this.cdsPosition;
        hash = 97 * hash + Objects.hashCode(this.proteinId);
        hash = 97 * hash + this.proteinPosition;
        hash = 97 * hash + Objects.hashCode(this.aminoacidChange);
        hash = 97 * hash + Objects.hashCode(this.codonChange);
        hash = 97 * hash + Objects.hashCode(this.variationId);
        hash = 97 * hash + Arrays.hashCode(this.consequenceTypes);
        hash = 97 * hash + Objects.hashCode(this.intronNumber);
        hash = 97 * hash + Objects.hashCode(this.exonNumber);
        return hash;
    }
    
    
}
