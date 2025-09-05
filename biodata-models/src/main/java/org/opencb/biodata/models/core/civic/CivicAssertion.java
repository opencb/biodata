package org.opencb.biodata.models.core.civic;

import java.util.ArrayList;
import java.util.List;

public class CivicAssertion {

    // From AssertionSummaries.tsv
    private String disease;
    private String doid;
    private List<String> phenotypes;
    private List<String> therapies;
    private String assertionType;
    private String assertionDirection;
    private String significance;
    private List<String> acmgCodes;
    private String ampCategory;
    private String nccnGuideline;
    private String nccnGuidelineVersion;
    private String regulatoryApproval;
    private String fdaCompanionTest;
    private String assertionSummary;
    private String assertionDescription;
    private String assertionId;
    private String lastReviewDate;
    private String assertionCivicUrl;
    private Boolean isFlagged;

    // Associated data
    private List<CivicClinicalEvidence> evidences;

    public CivicAssertion() {
        this.phenotypes = new ArrayList<>();
        this.therapies = new ArrayList<>();
        this.acmgCodes = new ArrayList<>();

        this.evidences = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CivicAssertion{");
        sb.append("disease='").append(disease).append('\'');
        sb.append(", doid='").append(doid).append('\'');
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", therapies=").append(therapies);
        sb.append(", assertionType='").append(assertionType).append('\'');
        sb.append(", assertionDirection='").append(assertionDirection).append('\'');
        sb.append(", significance='").append(significance).append('\'');
        sb.append(", acmgCodes=").append(acmgCodes);
        sb.append(", ampCategory='").append(ampCategory).append('\'');
        sb.append(", nccnGuideline='").append(nccnGuideline).append('\'');
        sb.append(", nccnGuidelineVersion='").append(nccnGuidelineVersion).append('\'');
        sb.append(", regulatoryApproval='").append(regulatoryApproval).append('\'');
        sb.append(", fdaCompanionTest='").append(fdaCompanionTest).append('\'');
        sb.append(", assertionSummary='").append(assertionSummary).append('\'');
        sb.append(", assertionDescription='").append(assertionDescription).append('\'');
        sb.append(", assertionId='").append(assertionId).append('\'');
        sb.append(", lastReviewDate='").append(lastReviewDate).append('\'');
        sb.append(", assertionCivicUrl='").append(assertionCivicUrl).append('\'');
        sb.append(", isFlagged=").append(isFlagged);
        sb.append(", evidences=").append(evidences);
        sb.append('}');
        return sb.toString();
    }

    public String getDisease() {
        return disease;
    }

    public CivicAssertion setDisease(String disease) {
        this.disease = disease;
        return this;
    }

    public String getDoid() {
        return doid;
    }

    public CivicAssertion setDoid(String doid) {
        this.doid = doid;
        return this;
    }

    public List<String> getPhenotypes() {
        return phenotypes;
    }

    public CivicAssertion setPhenotypes(List<String> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public List<String> getTherapies() {
        return therapies;
    }

    public CivicAssertion setTherapies(List<String> therapies) {
        this.therapies = therapies;
        return this;
    }

    public String getAssertionType() {
        return assertionType;
    }

    public CivicAssertion setAssertionType(String assertionType) {
        this.assertionType = assertionType;
        return this;
    }

    public String getAssertionDirection() {
        return assertionDirection;
    }

    public CivicAssertion setAssertionDirection(String assertionDirection) {
        this.assertionDirection = assertionDirection;
        return this;
    }

    public String getSignificance() {
        return significance;
    }

    public CivicAssertion setSignificance(String significance) {
        this.significance = significance;
        return this;
    }

    public List<String> getAcmgCodes() {
        return acmgCodes;
    }

    public CivicAssertion setAcmgCodes(List<String> acmgCodes) {
        this.acmgCodes = acmgCodes;
        return this;
    }

    public String getAmpCategory() {
        return ampCategory;
    }

    public CivicAssertion setAmpCategory(String ampCategory) {
        this.ampCategory = ampCategory;
        return this;
    }

    public String getNccnGuideline() {
        return nccnGuideline;
    }

    public CivicAssertion setNccnGuideline(String nccnGuideline) {
        this.nccnGuideline = nccnGuideline;
        return this;
    }

    public String getNccnGuidelineVersion() {
        return nccnGuidelineVersion;
    }

    public CivicAssertion setNccnGuidelineVersion(String nccnGuidelineVersion) {
        this.nccnGuidelineVersion = nccnGuidelineVersion;
        return this;
    }

    public String getRegulatoryApproval() {
        return regulatoryApproval;
    }

    public CivicAssertion setRegulatoryApproval(String regulatoryApproval) {
        this.regulatoryApproval = regulatoryApproval;
        return this;
    }

    public String getFdaCompanionTest() {
        return fdaCompanionTest;
    }

    public CivicAssertion setFdaCompanionTest(String fdaCompanionTest) {
        this.fdaCompanionTest = fdaCompanionTest;
        return this;
    }

    public String getAssertionSummary() {
        return assertionSummary;
    }

    public CivicAssertion setAssertionSummary(String assertionSummary) {
        this.assertionSummary = assertionSummary;
        return this;
    }

    public String getAssertionDescription() {
        return assertionDescription;
    }

    public CivicAssertion setAssertionDescription(String assertionDescription) {
        this.assertionDescription = assertionDescription;
        return this;
    }

    public String getAssertionId() {
        return assertionId;
    }

    public CivicAssertion setAssertionId(String assertionId) {
        this.assertionId = assertionId;
        return this;
    }

    public String getLastReviewDate() {
        return lastReviewDate;
    }

    public CivicAssertion setLastReviewDate(String lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
        return this;
    }

    public String getAssertionCivicUrl() {
        return assertionCivicUrl;
    }

    public CivicAssertion setAssertionCivicUrl(String assertionCivicUrl) {
        this.assertionCivicUrl = assertionCivicUrl;
        return this;
    }

    public Boolean getFlagged() {
        return isFlagged;
    }

    public CivicAssertion setFlagged(Boolean flagged) {
        isFlagged = flagged;
        return this;
    }

    public List<CivicClinicalEvidence> getEvidences() {
        return evidences;
    }

    public CivicAssertion setEvidences(List<CivicClinicalEvidence> evidences) {
        this.evidences = evidences;
        return this;
    }
}