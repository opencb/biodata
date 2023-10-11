package org.opencb.biodata.models.pharma.guideline;

public class SummaryMarkdown {
    private float id;
    private String html;
    private float version;

    public float getId() {
        return id;
    }

    public SummaryMarkdown setId(float id) {
        this.id = id;
        return this;
    }

    public String getHtml() {
        return html;
    }

    public SummaryMarkdown setHtml(String html) {
        this.html = html;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public SummaryMarkdown setVersion(float version) {
        this.version = version;
        return this;
    }
}
