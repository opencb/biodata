package org.opencb.biodata.models.pharma;

import java.util.Map;

public class PharmaClinicalEvidence {
    private String type;
    private String url;
    private String pmid;
    private String summary;
    private String score;

    private PharmaVariantAnnotation annotation;

    public PharmaClinicalEvidence() {
    }

    public PharmaClinicalEvidence(String type, String url, String pmid, String summary, String score,
                                  PharmaVariantAnnotation annotation) {
        this.type = type;
        this.url = url;
        this.pmid = pmid;
        this.summary = summary;
        this.score = score;
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalEvidence{");
        sb.append("type='").append(type).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", pmid='").append(pmid).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", score='").append(score).append('\'');
        sb.append(", annotation=").append(annotation);
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

    public PharmaVariantAnnotation getAnnotation() {
        return annotation;
    }

    public PharmaClinicalEvidence setAnnotation(PharmaVariantAnnotation annotation) {
        this.annotation = annotation;
        return this;
    }
}
