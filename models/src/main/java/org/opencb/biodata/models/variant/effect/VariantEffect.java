package org.opencb.biodata.models.variant.effect;

import java.util.HashSet;


/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 9/10/13
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantEffect {

    private String chromosome;
    private int position;
    private String referenceAllele;
    private String alternativeAllele;
    private String featureId;
    private String featureName;
    private String featureType;
    private String featureBiotype;
    private String featureChromosome;
    private int featureStart;
    private int featureEnd;
    private String featureStrand;
    private String snpId;
    private String ancestral;
    private String alternative;
    private String geneId;
    private String transcriptId;
    private String geneName;
    private String consequenceType;
    private String consequenceTypeObo;
    private String consequenceTypeDesc;
    private String consequenceTypeType;
    private int aaPosition;
    private String aminoacidChange;
    private String codonChange;

    private double polyphenScore;
    private double siftScore;
    private int polyphenEffect;
    private int siftEffect;


    public VariantEffect() {
    }

    public VariantEffect(String chromosome,
                         int position,
                         String referenceAllele,
                         String alternativeAllele,
                         String featureId,
                         String featureName,
                         String featureType,
                         String featureBiotype,
                         String featureChromosome,
                         int featureStart,
                         int featureEnd,
                         String featureStrand,
                         String snpId,
                         String ancestral,
                         String alternative,
                         String geneId,
                         String transcriptId,
                         String geneName,
                         String consequenceType,
                         String consequenceTypeObo,
                         String consequenceTypeDesc,
                         String consequenceTypeType,
                         int aaPosition,
                         String aminoacidChange,
                         String codonChange
    ) {
        this.chromosome = chromosome;
        this.position = position;
        this.referenceAllele = referenceAllele;
        this.alternativeAllele = alternativeAllele;
        this.featureId = featureId;
        this.featureName = featureName;
        this.featureType = featureType;
        this.featureBiotype = featureBiotype;
        this.featureChromosome = featureChromosome;
        this.featureStart = featureStart;
        this.featureEnd = featureEnd;
        this.featureStrand = featureStrand;
        this.snpId = snpId;
        this.ancestral = ancestral;
        this.alternative = alternative;
        this.geneId = geneId;
        this.transcriptId = transcriptId;
        this.geneName = geneName;
        this.consequenceType = consequenceType;
        this.consequenceTypeObo = consequenceTypeObo;
        this.consequenceTypeDesc = consequenceTypeDesc;
        this.consequenceTypeType = consequenceTypeType;
        this.aaPosition = aaPosition;
        this.aminoacidChange = aminoacidChange;
        this.codonChange = codonChange;
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

    public String getAlternativeAllele() {
        return alternativeAllele;
    }

    public void setAlternativeAllele(String alternativeAllele) {
        this.alternativeAllele = alternativeAllele;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
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

    public String getFeatureChromosome() {
        return featureChromosome;
    }

    public void setFeatureChromosome(String featureChromosome) {
        this.featureChromosome = featureChromosome;
    }

    public int getFeatureStart() {
        return featureStart;
    }

    public void setFeatureStart(int featureStart) {
        this.featureStart = featureStart;
    }

    public int getFeatureEnd() {
        return featureEnd;
    }

    public void setFeatureEnd(int featureEnd) {
        this.featureEnd = featureEnd;
    }

    public String getFeatureStrand() {
        return featureStrand;
    }

    public void setFeatureStrand(String featureStrand) {
        this.featureStrand = featureStrand;
    }

    public String getSnpId() {
        return snpId;
    }

    public void setSnpId(String snpId) {
        this.snpId = snpId;
    }

    public String getAncestral() {
        return ancestral;
    }

    public void setAncestral(String ancestral) {
        this.ancestral = ancestral;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getConsequenceType() {
        return consequenceType;
    }

    public void setConsequenceType(String consequenceType) {
        this.consequenceType = consequenceType;
    }

    public String getConsequenceTypeObo() {
        return consequenceTypeObo;
    }

    public void setConsequenceTypeObo(String consequenceTypeObo) {
        this.consequenceTypeObo = consequenceTypeObo;
    }

    public String getConsequenceTypeDesc() {
        return consequenceTypeDesc;
    }

    public void setConsequenceTypeDesc(String consequenceTypeDesc) {
        this.consequenceTypeDesc = consequenceTypeDesc;
    }

    public String getConsequenceTypeType() {
        return consequenceTypeType;
    }

    public void setConsequenceTypeType(String consequenceTypeType) {
        this.consequenceTypeType = consequenceTypeType;
    }

    public int getAaPosition() {
        return aaPosition;
    }

    public void setAaPosition(int aaPosition) {
        this.aaPosition = aaPosition;
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

    @Override
    public String toString() {
        return "VariantEffect{" +
                "chromosome='" + chromosome + '\'' +
                ", position=" + position +
                ", referenceAllele='" + referenceAllele + '\'' +
                ", alternativeAllele='" + alternativeAllele + '\'' +
                ", featureId='" + featureId + '\'' +
                ", featureName='" + featureName + '\'' +
                ", featureType='" + featureType + '\'' +
                ", featureBiotype='" + featureBiotype + '\'' +
                ", featureChromosome='" + featureChromosome + '\'' +
                ", featureStart=" + featureStart +
                ", featureEnd=" + featureEnd +
                ", featureStrand='" + featureStrand + '\'' +
                ", snpId='" + snpId + '\'' +
                ", ancestral='" + ancestral + '\'' +
                ", alternative='" + alternative + '\'' +
                ", geneId='" + geneId + '\'' +
                ", transcriptId='" + transcriptId + '\'' +
                ", geneName='" + geneName + '\'' +
                ", consequenceType='" + consequenceType + '\'' +
                ", consequenceTypeObo='" + consequenceTypeObo + '\'' +
                ", consequenceTypeDesc='" + consequenceTypeDesc + '\'' +
                ", consequenceTypeType='" + consequenceTypeType + '\'' +
                ", aaPosition=" + aaPosition +
                ", aminoacidChange='" + aminoacidChange + '\'' +
                ", codonChange=" + codonChange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariantEffect)) return false;

        VariantEffect that = (VariantEffect) o;

        if (aaPosition != that.aaPosition) return false;
        if (featureEnd != that.featureEnd) return false;
        if (featureStart != that.featureStart) return false;
        if (position != that.position) return false;
        if (alternative != null ? !alternative.equals(that.alternative) : that.alternative != null) return false;
        if (alternativeAllele != null ? !alternativeAllele.equals(that.alternativeAllele) : that.alternativeAllele != null)
            return false;
        if (aminoacidChange != null ? !aminoacidChange.equals(that.aminoacidChange) : that.aminoacidChange != null)
            return false;
        if (ancestral != null ? !ancestral.equals(that.ancestral) : that.ancestral != null) return false;
        if (chromosome != null ? !chromosome.equals(that.chromosome) : that.chromosome != null) return false;
        if (codonChange != null ? !codonChange.equals(that.codonChange) : that.codonChange != null) return false;
        if (consequenceType != null ? !consequenceType.equals(that.consequenceType) : that.consequenceType != null)
            return false;
        if (consequenceTypeDesc != null ? !consequenceTypeDesc.equals(that.consequenceTypeDesc) : that.consequenceTypeDesc != null)
            return false;
        if (consequenceTypeObo != null ? !consequenceTypeObo.equals(that.consequenceTypeObo) : that.consequenceTypeObo != null)
            return false;
        if (consequenceTypeType != null ? !consequenceTypeType.equals(that.consequenceTypeType) : that.consequenceTypeType != null)
            return false;
        if (featureBiotype != null ? !featureBiotype.equals(that.featureBiotype) : that.featureBiotype != null)
            return false;
        if (featureChromosome != null ? !featureChromosome.equals(that.featureChromosome) : that.featureChromosome != null)
            return false;
        if (featureId != null ? !featureId.equals(that.featureId) : that.featureId != null) return false;
        if (featureName != null ? !featureName.equals(that.featureName) : that.featureName != null) return false;
        if (featureStrand != null ? !featureStrand.equals(that.featureStrand) : that.featureStrand != null)
            return false;
        if (featureType != null ? !featureType.equals(that.featureType) : that.featureType != null) return false;
        if (geneId != null ? !geneId.equals(that.geneId) : that.geneId != null) return false;
        if (geneName != null ? !geneName.equals(that.geneName) : that.geneName != null) return false;
        if (referenceAllele != null ? !referenceAllele.equals(that.referenceAllele) : that.referenceAllele != null)
            return false;
        if (snpId != null ? !snpId.equals(that.snpId) : that.snpId != null) return false;
        if (transcriptId != null ? !transcriptId.equals(that.transcriptId) : that.transcriptId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chromosome != null ? chromosome.hashCode() : 0;
        result = 31 * result + position;
        result = 31 * result + (referenceAllele != null ? referenceAllele.hashCode() : 0);
        result = 31 * result + (alternativeAllele != null ? alternativeAllele.hashCode() : 0);
        result = 31 * result + (featureId != null ? featureId.hashCode() : 0);
        result = 31 * result + (featureName != null ? featureName.hashCode() : 0);
        result = 31 * result + (featureType != null ? featureType.hashCode() : 0);
        result = 31 * result + (featureBiotype != null ? featureBiotype.hashCode() : 0);
        result = 31 * result + (featureChromosome != null ? featureChromosome.hashCode() : 0);
        result = 31 * result + featureStart;
        result = 31 * result + featureEnd;
        result = 31 * result + (featureStrand != null ? featureStrand.hashCode() : 0);
        result = 31 * result + (snpId != null ? snpId.hashCode() : 0);
        result = 31 * result + (ancestral != null ? ancestral.hashCode() : 0);
        result = 31 * result + (alternative != null ? alternative.hashCode() : 0);
        result = 31 * result + (geneId != null ? geneId.hashCode() : 0);
        result = 31 * result + (transcriptId != null ? transcriptId.hashCode() : 0);
        result = 31 * result + (geneName != null ? geneName.hashCode() : 0);
        result = 31 * result + (consequenceType != null ? consequenceType.hashCode() : 0);
        result = 31 * result + (consequenceTypeObo != null ? consequenceTypeObo.hashCode() : 0);
        result = 31 * result + (consequenceTypeDesc != null ? consequenceTypeDesc.hashCode() : 0);
        result = 31 * result + (consequenceTypeType != null ? consequenceTypeType.hashCode() : 0);
        result = 31 * result + aaPosition;
        result = 31 * result + (aminoacidChange != null ? aminoacidChange.hashCode() : 0);
        result = 31 * result + (codonChange != null ? codonChange.hashCode() : 0);
        return result;
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

    public int getPolyphenEffect() {
        return polyphenEffect;
    }

    public void setPolyphenEffect(int polyphenEffect) {
        this.polyphenEffect = polyphenEffect;
    }

    public int getSiftEffect() {
        return siftEffect;
    }

    public void setSiftEffect(int siftEffect) {
        this.siftEffect = siftEffect;
    }
}
