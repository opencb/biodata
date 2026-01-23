package org.opencb.biodata.models.core.civic;

import java.util.ArrayList;
import java.util.List;

public class CivicClinicalEvidence {

    // From ClinicalEvidenceSummaries.tsv
    private String disease;
    private String doid;
    private List<String> phenotypes;
    private List<String> therapies;
    private String therapyInteractionType;
    private String evidenceType;
    private String evidenceDirection;
    private String evidenceLevel;
    private String significance;
    private String evidenceStatement;
    private String citationId;
    private String sourceType;
    private String ascoAbstractId;
    private String citation;
    private List<String> nctIds;
    private String rating;
    private String evidenceStatus;
    private String evidenceId;
    private String variantOrigin;
    private String lastReviewDate;
    private String evidenceCivicUrl;
    private Boolean isFlagged;

    public CivicClinicalEvidence() {
        this.phenotypes = new ArrayList<>();
        this.therapies = new ArrayList<>();
        this.nctIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CivicClinicalEvidence{");
        sb.append(", disease='").append(disease).append('\'');
        sb.append(", doid='").append(doid).append('\'');
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", therapies=").append(therapies);
        sb.append(", therapyInteractionType='").append(therapyInteractionType).append('\'');
        sb.append(", evidenceType='").append(evidenceType).append('\'');
        sb.append(", evidenceDirection='").append(evidenceDirection).append('\'');
        sb.append(", evidenceLevel='").append(evidenceLevel).append('\'');
        sb.append(", significance='").append(significance).append('\'');
        sb.append(", evidenceStatement='").append(evidenceStatement).append('\'');
        sb.append(", citationId='").append(citationId).append('\'');
        sb.append(", sourceType='").append(sourceType).append('\'');
        sb.append(", ascoAbstractId='").append(ascoAbstractId).append('\'');
        sb.append(", citation='").append(citation).append('\'');
        sb.append(", nctIds=").append(nctIds);
        sb.append(", rating='").append(rating).append('\'');
        sb.append(", evidenceStatus='").append(evidenceStatus).append('\'');
        sb.append(", evidenceId='").append(evidenceId).append('\'');
        sb.append(", variantOrigin='").append(variantOrigin).append('\'');
        sb.append(", lastReviewDate='").append(lastReviewDate).append('\'');
        sb.append(", evidenceCivicUrl='").append(evidenceCivicUrl).append('\'');
        sb.append(", isFlagged=").append(isFlagged);
        sb.append('}');
        return sb.toString();
    }

    public String getDisease() {
        return disease;
    }

    public CivicClinicalEvidence setDisease(String disease) {
        this.disease = disease;
        return this;
    }

    public String getDoid() {
        return doid;
    }

    public CivicClinicalEvidence setDoid(String doid) {
        this.doid = doid;
        return this;
    }

    public List<String> getPhenotypes() {
        return phenotypes;
    }

    public CivicClinicalEvidence setPhenotypes(List<String> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public List<String> getTherapies() {
        return therapies;
    }

    public CivicClinicalEvidence setTherapies(List<String> therapies) {
        this.therapies = therapies;
        return this;
    }

    public String getTherapyInteractionType() {
        return therapyInteractionType;
    }

    public CivicClinicalEvidence setTherapyInteractionType(String therapyInteractionType) {
        this.therapyInteractionType = therapyInteractionType;
        return this;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    public CivicClinicalEvidence setEvidenceType(String evidenceType) {
        this.evidenceType = evidenceType;
        return this;
    }

    public String getEvidenceDirection() {
        return evidenceDirection;
    }

    public CivicClinicalEvidence setEvidenceDirection(String evidenceDirection) {
        this.evidenceDirection = evidenceDirection;
        return this;
    }

    public String getEvidenceLevel() {
        return evidenceLevel;
    }

    public CivicClinicalEvidence setEvidenceLevel(String evidenceLevel) {
        this.evidenceLevel = evidenceLevel;
        return this;
    }

    public String getSignificance() {
        return significance;
    }

    public CivicClinicalEvidence setSignificance(String significance) {
        this.significance = significance;
        return this;
    }

    public String getEvidenceStatement() {
        return evidenceStatement;
    }

    public CivicClinicalEvidence setEvidenceStatement(String evidenceStatement) {
        this.evidenceStatement = evidenceStatement;
        return this;
    }

    public String getCitationId() {
        return citationId;
    }

    public CivicClinicalEvidence setCitationId(String citationId) {
        this.citationId = citationId;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public CivicClinicalEvidence setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public String getAscoAbstractId() {
        return ascoAbstractId;
    }

    public CivicClinicalEvidence setAscoAbstractId(String ascoAbstractId) {
        this.ascoAbstractId = ascoAbstractId;
        return this;
    }

    public String getCitation() {
        return citation;
    }

    public CivicClinicalEvidence setCitation(String citation) {
        this.citation = citation;
        return this;
    }

    public List<String> getNctIds() {
        return nctIds;
    }

    public CivicClinicalEvidence setNctIds(List<String> nctIds) {
        this.nctIds = nctIds;
        return this;
    }

    public String getRating() {
        return rating;
    }

    public CivicClinicalEvidence setRating(String rating) {
        this.rating = rating;
        return this;
    }

    public String getEvidenceStatus() {
        return evidenceStatus;
    }

    public CivicClinicalEvidence setEvidenceStatus(String evidenceStatus) {
        this.evidenceStatus = evidenceStatus;
        return this;
    }

    public String getEvidenceId() {
        return evidenceId;
    }

    public CivicClinicalEvidence setEvidenceId(String evidenceId) {
        this.evidenceId = evidenceId;
        return this;
    }

    public String getVariantOrigin() {
        return variantOrigin;
    }

    public CivicClinicalEvidence setVariantOrigin(String variantOrigin) {
        this.variantOrigin = variantOrigin;
        return this;
    }

    public String getLastReviewDate() {
        return lastReviewDate;
    }

    public CivicClinicalEvidence setLastReviewDate(String lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
        return this;
    }

    public String getEvidenceCivicUrl() {
        return evidenceCivicUrl;
    }

    public CivicClinicalEvidence setEvidenceCivicUrl(String evidenceCivicUrl) {
        this.evidenceCivicUrl = evidenceCivicUrl;
        return this;
    }

    public Boolean getFlagged() {
        return isFlagged;
    }

    public CivicClinicalEvidence setFlagged(Boolean flagged) {
        isFlagged = flagged;
        return this;
    }
}