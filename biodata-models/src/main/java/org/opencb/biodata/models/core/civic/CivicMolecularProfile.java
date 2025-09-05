package org.opencb.biodata.models.core.civic;

import java.util.ArrayList;
import java.util.List;

public class CivicMolecularProfile {

    // From MolecularProfileSummaries.tsv
    private String name;
    private String molecularProfileId;
    private String summary;
    private String evidenceScore;
    private List<String> aliases;
    private String lastReviewDate;
    private Boolean isFlagged;

    // Associated data
    private List<CivicAssertion> assertions;
    private List<CivicClinicalEvidence> evidences;

    public CivicMolecularProfile() {
        this.aliases = new ArrayList<>();

        this.assertions = new ArrayList<>();
        this.evidences = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CivicMolecularProfile{");
        sb.append("name='").append(name).append('\'');
        sb.append(", molecularProfileId='").append(molecularProfileId).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", evidenceScore='").append(evidenceScore).append('\'');
        sb.append(", aliases=").append(aliases);
        sb.append(", lastReviewDate='").append(lastReviewDate).append('\'');
        sb.append(", isFlagged=").append(isFlagged);
        sb.append(", assertions=").append(assertions);
        sb.append(", evidences=").append(evidences);
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public CivicMolecularProfile setName(String name) {
        this.name = name;
        return this;
    }

    public String getMolecularProfileId() {
        return molecularProfileId;
    }

    public CivicMolecularProfile setMolecularProfileId(String molecularProfileId) {
        this.molecularProfileId = molecularProfileId;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public CivicMolecularProfile setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getEvidenceScore() {
        return evidenceScore;
    }

    public CivicMolecularProfile setEvidenceScore(String evidenceScore) {
        this.evidenceScore = evidenceScore;
        return this;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public CivicMolecularProfile setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public String getLastReviewDate() {
        return lastReviewDate;
    }

    public CivicMolecularProfile setLastReviewDate(String lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
        return this;
    }

    public Boolean getFlagged() {
        return isFlagged;
    }

    public CivicMolecularProfile setFlagged(Boolean flagged) {
        isFlagged = flagged;
        return this;
    }

    public List<CivicAssertion> getAssertions() {
        return assertions;
    }

    public CivicMolecularProfile setAssertions(List<CivicAssertion> assertions) {
        this.assertions = assertions;
        return this;
    }

    public List<CivicClinicalEvidence> getEvidences() {
        return evidences;
    }

    public CivicMolecularProfile setEvidences(List<CivicClinicalEvidence> evidences) {
        this.evidences = evidences;
        return this;
    }
}