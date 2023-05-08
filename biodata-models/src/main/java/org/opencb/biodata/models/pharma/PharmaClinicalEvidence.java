package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.List;

public class PharmaClinicalEvidence {
    private String type;
    private String url;
    private String pmid;
    private String summary;
    private String score;

    private List<PharmaBasicAnnotation> variantAnnotations;
    private List<PharmaGuidelineAnnotation> guidelineAnnotations;
    private List<PharmaDrugLabelAnnotation> drugLabelAnnotations;
    private List<PharmaBasicAnnotation> phenotypeAnnotations;
    private List<PharmaFunctionalAnnotation> functionalAnnotations;

    public PharmaClinicalEvidence() {
        this.variantAnnotations = new ArrayList<>();
        this.guidelineAnnotations = new ArrayList<>();
        this.drugLabelAnnotations = new ArrayList<>();
        this.phenotypeAnnotations = new ArrayList<>();
        this.functionalAnnotations = new ArrayList<>();
    }

    public PharmaClinicalEvidence(String type, String url, String pmid, String summary, String score,
                                  List<PharmaBasicAnnotation> variantAnnotations,
                                  List<PharmaGuidelineAnnotation> guidelineAnnotations,
                                  List<PharmaDrugLabelAnnotation> drugLabelAnnotations,
                                  List<PharmaBasicAnnotation> phenotypeAnnotations,
                                  List<PharmaFunctionalAnnotation> functionalAnnotations) {
        this.type = type;
        this.url = url;
        this.pmid = pmid;
        this.summary = summary;
        this.score = score;
        this.variantAnnotations = variantAnnotations;
        this.guidelineAnnotations = guidelineAnnotations;
        this.drugLabelAnnotations = drugLabelAnnotations;
        this.phenotypeAnnotations = phenotypeAnnotations;
        this.functionalAnnotations = functionalAnnotations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalEvidence{");
        sb.append("type='").append(type).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", pmid='").append(pmid).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", score='").append(score).append('\'');
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

    public String getPmid() {
        return pmid;
    }

    public PharmaClinicalEvidence setPmid(String pmid) {
        this.pmid = pmid;
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

    public List<PharmaBasicAnnotation> getVariantAnnotations() {
        return variantAnnotations;
    }

    public PharmaClinicalEvidence setVariantAnnotations(List<PharmaBasicAnnotation> variantAnnotations) {
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

    public List<PharmaBasicAnnotation> getPhenotypeAnnotations() {
        return phenotypeAnnotations;
    }

    public PharmaClinicalEvidence setPhenotypeAnnotations(List<PharmaBasicAnnotation> phenotypeAnnotations) {
        this.phenotypeAnnotations = phenotypeAnnotations;
        return this;
    }

    public List<PharmaFunctionalAnnotation> getFunctionalAnnotations() {
        return functionalAnnotations;
    }

    public PharmaClinicalEvidence setFunctionalAnnotations(List<PharmaFunctionalAnnotation> functionalAnnotations) {
        this.functionalAnnotations = functionalAnnotations;
        return this;
    }
}
