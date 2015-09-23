package org.opencb.biodata.models.variation;

import org.opencb.biodata.models.protein.ProteinFeature;
import org.opencb.biodata.models.variant.annotation.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjlopez on 18/09/15.
 */
public class ProteinVariantAnnotation {

    private String accession;
    private int position;
    private String reference;
    private String alternate;
    private String variantId;
    private String functionalDescription;
    private List<Score> substitutionScores = null;
    private List<String> keywords;
    private List<ProteinFeature> features;

    public ProteinVariantAnnotation() {
    }

    public ProteinVariantAnnotation(int aaPosition, String aaReference, String aaAlternate, List<Score> substitutionScores) {
        this(null, aaPosition, aaReference, aaAlternate, null, null, substitutionScores, null, null);

    }

    public ProteinVariantAnnotation(String accession, int position, String reference, String alternate, String variantId,
                                    String functionalDescription, List<Score> substitutionScores, List<String> keywords, List<ProteinFeature> features) {
        this.accession = accession;
        this.position = position;
        this.reference = reference;
        this.alternate = alternate;
        this.variantId = variantId;
        this.functionalDescription = functionalDescription;
        this.substitutionScores = substitutionScores;
        this.keywords = keywords;
        this.features = features;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getFunctionalDescription() {
        return functionalDescription;
    }

    public void setFunctionalDescription(String functionalDescription) {
        this.functionalDescription = functionalDescription;
    }

    public List<Score> getSubstitutionScores() {
        return substitutionScores;
    }

    public void setSubstitutionScores(List<Score> substitutionScores) {
        this.substitutionScores = substitutionScores;
    }

    public void addSubstitutionScore(Score score) {
        if (this.substitutionScores == null) {
            substitutionScores = new ArrayList<>();
        }
        substitutionScores.add(score);
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void addUniprotKeyword(String keyword) {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        keywords.add(keyword);
    }

    public List<ProteinFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<ProteinFeature> features) {
        this.features = features;
    }

    public void addProteinFeature(ProteinFeature proteinFeature) {
        if(features ==null) {
            features = new ArrayList<>();
        }
        features.add(proteinFeature);
    }
}
