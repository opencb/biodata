package org.opencb.biodata.models.pharma;

import java.util.Map;

public class PharmaClinicalEvidence {
    private String type;
    private String url;
    private String pmid;
    private String summary;
    private String score;
    private Map<String, Object> studyParameters;

    public PharmaClinicalEvidence() {
    }

    public PharmaClinicalEvidence(String type, String url, String pmid, String summary, String score,
                                  Map<String, Object> studyParameters) {
        this.type = type;
        this.url = url;
        this.pmid = pmid;
        this.summary = summary;
        this.score = score;
        this.studyParameters = studyParameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalEvidence{");
        sb.append("type='").append(type).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", pmid='").append(pmid).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", score='").append(score).append('\'');
        sb.append(", studyParameters=").append(studyParameters);
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

    public Map<String, Object> getStudyParameters() {
        return studyParameters;
    }

    public PharmaClinicalEvidence setStudyParameters(Map<String, Object> studyParameters) {
        this.studyParameters = studyParameters;
        return this;
    }
}
