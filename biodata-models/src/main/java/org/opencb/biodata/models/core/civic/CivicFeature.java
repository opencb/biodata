package org.opencb.biodata.models.core.civic;

import java.util.ArrayList;
import java.util.List;

public class CivicFeature {

    // From FeatureSummaries.tsv
    private String featureId;
    private String featureCivicUrl;
    private String featureType;
    private String name;
    private List<String> featureAliases;
    private String description;
    private String lastReviewDate;
    private Boolean isFlagged;
    private String entrezId;
    private String ncitId;
    private String fivePrimePartnerStatus;
    private String threePrimePartnerStatus;
    private String fivePrimeGeneId;
    private String fivePrimeGeneName;
    private String fivePrimeGeneEntrezId;
    private String threePrimeGeneId;
    private String threePrimeGeneName;
    private String threePrimeGeneEntrezId;

    // Additional transcript/exon information from VariantSummaries.tsv
    private String fivePrimePartner;
    private String threePrimePartner;
    private String fivePrimeTranscript;
    private String fivePrimeEndExon;
    private String fivePrimeExonOffset;
    private String fivePrimeExonOffsetDirection;
    private String threePrimeTranscript;
    private String threePrimeStartExon;
    private String threePrimeExonOffset;
    private String threePrimeExonOffsetDirection;

    public CivicFeature() {
        this.featureAliases = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CivicFeature{");
        sb.append("featureId='").append(featureId).append('\'');
        sb.append(", featureCivicUrl='").append(featureCivicUrl).append('\'');
        sb.append(", featureType='").append(featureType).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", featureAliases=").append(featureAliases);
        sb.append(", description='").append(description).append('\'');
        sb.append(", lastReviewDate='").append(lastReviewDate).append('\'');
        sb.append(", isFlagged=").append(isFlagged);
        sb.append(", entrezId='").append(entrezId).append('\'');
        sb.append(", ncitId='").append(ncitId).append('\'');
        sb.append(", fivePrimePartnerStatus='").append(fivePrimePartnerStatus).append('\'');
        sb.append(", threePrimePartnerStatus='").append(threePrimePartnerStatus).append('\'');
        sb.append(", fivePrimeGeneId='").append(fivePrimeGeneId).append('\'');
        sb.append(", fivePrimeGeneName='").append(fivePrimeGeneName).append('\'');
        sb.append(", fivePrimeGeneEntrezId='").append(fivePrimeGeneEntrezId).append('\'');
        sb.append(", threePrimeGeneId='").append(threePrimeGeneId).append('\'');
        sb.append(", threePrimeGeneName='").append(threePrimeGeneName).append('\'');
        sb.append(", threePrimeGeneEntrezId='").append(threePrimeGeneEntrezId).append('\'');
        sb.append(", fivePrimePartner='").append(fivePrimePartner).append('\'');
        sb.append(", threePrimePartner='").append(threePrimePartner).append('\'');
        sb.append(", fivePrimeTranscript='").append(fivePrimeTranscript).append('\'');
        sb.append(", fivePrimeEndExon='").append(fivePrimeEndExon).append('\'');
        sb.append(", fivePrimeExonOffset='").append(fivePrimeExonOffset).append('\'');
        sb.append(", fivePrimeExonOffsetDirection='").append(fivePrimeExonOffsetDirection).append('\'');
        sb.append(", threePrimeTranscript='").append(threePrimeTranscript).append('\'');
        sb.append(", threePrimeStartExon='").append(threePrimeStartExon).append('\'');
        sb.append(", threePrimeExonOffset='").append(threePrimeExonOffset).append('\'');
        sb.append(", threePrimeExonOffsetDirection='").append(threePrimeExonOffsetDirection).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getFeatureId() {
        return featureId;
    }

    public CivicFeature setFeatureId(String featureId) {
        this.featureId = featureId;
        return this;
    }

    public String getFeatureCivicUrl() {
        return featureCivicUrl;
    }

    public CivicFeature setFeatureCivicUrl(String featureCivicUrl) {
        this.featureCivicUrl = featureCivicUrl;
        return this;
    }

    public String getFeatureType() {
        return featureType;
    }

    public CivicFeature setFeatureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

    public String getName() {
        return name;
    }

    public CivicFeature setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getFeatureAliases() {
        return featureAliases;
    }

    public CivicFeature setFeatureAliases(List<String> featureAliases) {
        this.featureAliases = featureAliases;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CivicFeature setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLastReviewDate() {
        return lastReviewDate;
    }

    public CivicFeature setLastReviewDate(String lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
        return this;
    }

    public Boolean getFlagged() {
        return isFlagged;
    }

    public CivicFeature setFlagged(Boolean flagged) {
        isFlagged = flagged;
        return this;
    }

    public String getEntrezId() {
        return entrezId;
    }

    public CivicFeature setEntrezId(String entrezId) {
        this.entrezId = entrezId;
        return this;
    }

    public String getNcitId() {
        return ncitId;
    }

    public CivicFeature setNcitId(String ncitId) {
        this.ncitId = ncitId;
        return this;
    }

    public String getFivePrimePartnerStatus() {
        return fivePrimePartnerStatus;
    }

    public CivicFeature setFivePrimePartnerStatus(String fivePrimePartnerStatus) {
        this.fivePrimePartnerStatus = fivePrimePartnerStatus;
        return this;
    }

    public String getThreePrimePartnerStatus() {
        return threePrimePartnerStatus;
    }

    public CivicFeature setThreePrimePartnerStatus(String threePrimePartnerStatus) {
        this.threePrimePartnerStatus = threePrimePartnerStatus;
        return this;
    }

    public String getFivePrimeGeneId() {
        return fivePrimeGeneId;
    }

    public CivicFeature setFivePrimeGeneId(String fivePrimeGeneId) {
        this.fivePrimeGeneId = fivePrimeGeneId;
        return this;
    }

    public String getFivePrimeGeneName() {
        return fivePrimeGeneName;
    }

    public CivicFeature setFivePrimeGeneName(String fivePrimeGeneName) {
        this.fivePrimeGeneName = fivePrimeGeneName;
        return this;
    }

    public String getFivePrimeGeneEntrezId() {
        return fivePrimeGeneEntrezId;
    }

    public CivicFeature setFivePrimeGeneEntrezId(String fivePrimeGeneEntrezId) {
        this.fivePrimeGeneEntrezId = fivePrimeGeneEntrezId;
        return this;
    }

    public String getThreePrimeGeneId() {
        return threePrimeGeneId;
    }

    public CivicFeature setThreePrimeGeneId(String threePrimeGeneId) {
        this.threePrimeGeneId = threePrimeGeneId;
        return this;
    }

    public String getThreePrimeGeneName() {
        return threePrimeGeneName;
    }

    public CivicFeature setThreePrimeGeneName(String threePrimeGeneName) {
        this.threePrimeGeneName = threePrimeGeneName;
        return this;
    }

    public String getThreePrimeGeneEntrezId() {
        return threePrimeGeneEntrezId;
    }

    public CivicFeature setThreePrimeGeneEntrezId(String threePrimeGeneEntrezId) {
        this.threePrimeGeneEntrezId = threePrimeGeneEntrezId;
        return this;
    }

    public String getFivePrimePartner() {
        return fivePrimePartner;
    }

    public CivicFeature setFivePrimePartner(String fivePrimePartner) {
        this.fivePrimePartner = fivePrimePartner;
        return this;
    }

    public String getThreePrimePartner() {
        return threePrimePartner;
    }

    public CivicFeature setThreePrimePartner(String threePrimePartner) {
        this.threePrimePartner = threePrimePartner;
        return this;
    }

    public String getFivePrimeTranscript() {
        return fivePrimeTranscript;
    }

    public CivicFeature setFivePrimeTranscript(String fivePrimeTranscript) {
        this.fivePrimeTranscript = fivePrimeTranscript;
        return this;
    }

    public String getFivePrimeEndExon() {
        return fivePrimeEndExon;
    }

    public CivicFeature setFivePrimeEndExon(String fivePrimeEndExon) {
        this.fivePrimeEndExon = fivePrimeEndExon;
        return this;
    }

    public String getFivePrimeExonOffset() {
        return fivePrimeExonOffset;
    }

    public CivicFeature setFivePrimeExonOffset(String fivePrimeExonOffset) {
        this.fivePrimeExonOffset = fivePrimeExonOffset;
        return this;
    }

    public String getFivePrimeExonOffsetDirection() {
        return fivePrimeExonOffsetDirection;
    }

    public CivicFeature setFivePrimeExonOffsetDirection(String fivePrimeExonOffsetDirection) {
        this.fivePrimeExonOffsetDirection = fivePrimeExonOffsetDirection;
        return this;
    }

    public String getThreePrimeTranscript() {
        return threePrimeTranscript;
    }

    public CivicFeature setThreePrimeTranscript(String threePrimeTranscript) {
        this.threePrimeTranscript = threePrimeTranscript;
        return this;
    }

    public String getThreePrimeStartExon() {
        return threePrimeStartExon;
    }

    public CivicFeature setThreePrimeStartExon(String threePrimeStartExon) {
        this.threePrimeStartExon = threePrimeStartExon;
        return this;
    }

    public String getThreePrimeExonOffset() {
        return threePrimeExonOffset;
    }

    public CivicFeature setThreePrimeExonOffset(String threePrimeExonOffset) {
        this.threePrimeExonOffset = threePrimeExonOffset;
        return this;
    }

    public String getThreePrimeExonOffsetDirection() {
        return threePrimeExonOffsetDirection;
    }

    public CivicFeature setThreePrimeExonOffsetDirection(String threePrimeExonOffsetDirection) {
        this.threePrimeExonOffsetDirection = threePrimeExonOffsetDirection;
        return this;
    }
}