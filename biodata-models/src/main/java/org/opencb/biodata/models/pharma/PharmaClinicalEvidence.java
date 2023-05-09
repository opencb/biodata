package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.List;

public class PharmaClinicalEvidence {
    private String type;
    private String url;
    private String pubmed;
    private String summary;
    private String score;

    private List<PharmaVariantAssociation> variantAssociations;
    @Deprecated
    private List<PharmaVariantAssociation> variantAnnotations;
    private List<PharmaGuidelineAnnotation> guidelineAnnotations;
    private List<PharmaDrugLabelAnnotation> drugLabelAnnotations;
    @Deprecated
    private List<PharmaVariantAssociation> phenotypeAnnotations;
    @Deprecated
    private List<PharmaVariantAssociation> functionalAnnotations;

    public PharmaClinicalEvidence() {
        this.variantAssociations = new ArrayList<>();
        this.variantAnnotations = new ArrayList<>();
        this.guidelineAnnotations = new ArrayList<>();
        this.drugLabelAnnotations = new ArrayList<>();
        this.phenotypeAnnotations = new ArrayList<>();
        this.functionalAnnotations = new ArrayList<>();
    }

    public PharmaClinicalEvidence(String type, String url, String pubmed, String summary, String score,
                                  List<PharmaVariantAssociation> variantAnnotations,
                                  List<PharmaGuidelineAnnotation> guidelineAnnotations,
                                  List<PharmaDrugLabelAnnotation> drugLabelAnnotations,
                                  List<PharmaVariantAssociation> phenotypeAnnotations,
                                  List<PharmaVariantAssociation> functionalAnnotations) {
        this.type = type;
        this.url = url;
        this.pubmed = pubmed;
        this.summary = summary;
        this.score = score;
        this.variantAnnotations = variantAnnotations;
        this.guidelineAnnotations = guidelineAnnotations;
        this.drugLabelAnnotations = drugLabelAnnotations;
        this.phenotypeAnnotations = phenotypeAnnotations;
        this.functionalAnnotations = functionalAnnotations;
    }

    public PharmaClinicalEvidence(String type, String url, String pubmed, String summary, String score,
                                  List<PharmaVariantAssociation> variantAssociations,
                                  List<PharmaGuidelineAnnotation> guidelineAnnotations,
                                  List<PharmaDrugLabelAnnotation> drugLabelAnnotations) {
        this.type = type;
        this.url = url;
        this.pubmed = pubmed;
        this.summary = summary;
        this.score = score;
        this.variantAssociations = variantAssociations;
        this.guidelineAnnotations = guidelineAnnotations;
        this.drugLabelAnnotations = drugLabelAnnotations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalEvidence{");
        sb.append("type='").append(type).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", pubmed='").append(pubmed).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", score='").append(score).append('\'');
        sb.append(", variantAssociations=").append(variantAssociations);
        sb.append(", variantAnnotations=").append(variantAnnotations);
        sb.append(", guidelineAnnotations=").append(guidelineAnnotations);
        sb.append(", drugLabelAnnotations=").append(drugLabelAnnotations);
        sb.append(", phenotypeAnnotations=").append(phenotypeAnnotations);
        sb.append(", functionalAnnotations=").append(functionalAnnotations);
        sb.append('}');
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public PharmaClinicalEvidence setType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PharmaClinicalEvidence setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getPubmed() {
        return pubmed;
    }

    public PharmaClinicalEvidence setPubmed(String pubmed) {
        this.pubmed = pubmed;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public PharmaClinicalEvidence setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getScore() {
        return score;
    }

    public PharmaClinicalEvidence setScore(String score) {
        this.score = score;
        return this;
    }

    public List<PharmaVariantAssociation> getVariantAssociations() {
        return variantAssociations;
    }

    public PharmaClinicalEvidence setVariantAssociations(List<PharmaVariantAssociation> variantAssociations) {
        this.variantAssociations = variantAssociations;
        return this;
    }

    @Deprecated
    public List<PharmaVariantAssociation> getVariantAnnotations() {
        return variantAnnotations;
    }

    @Deprecated
    public PharmaClinicalEvidence setVariantAnnotations(List<PharmaVariantAssociation> variantAnnotations) {
        this.variantAnnotations = variantAnnotations;
        return this;
    }

    public List<PharmaGuidelineAnnotation> getGuidelineAnnotations() {
        return guidelineAnnotations;
    }

    public PharmaClinicalEvidence setGuidelineAnnotations(List<PharmaGuidelineAnnotation> guidelineAnnotations) {
        this.guidelineAnnotations = guidelineAnnotations;
        return this;
    }

    public List<PharmaDrugLabelAnnotation> getDrugLabelAnnotations() {
        return drugLabelAnnotations;
    }

    public PharmaClinicalEvidence setDrugLabelAnnotations(List<PharmaDrugLabelAnnotation> drugLabelAnnotations) {
        this.drugLabelAnnotations = drugLabelAnnotations;
        return this;
    }

    @Deprecated
    public List<PharmaVariantAssociation> getPhenotypeAnnotations() {
        return phenotypeAnnotations;
    }

    @Deprecated
    public PharmaClinicalEvidence setPhenotypeAnnotations(List<PharmaVariantAssociation> phenotypeAnnotations) {
        this.phenotypeAnnotations = phenotypeAnnotations;
        return this;
    }

    @Deprecated
    public List<PharmaVariantAssociation> getFunctionalAnnotations() {
        return functionalAnnotations;
    }

    @Deprecated
    public PharmaClinicalEvidence setFunctionalAnnotations(List<PharmaVariantAssociation> functionalAnnotations) {
        this.functionalAnnotations = functionalAnnotations;
        return this;
    }
}
