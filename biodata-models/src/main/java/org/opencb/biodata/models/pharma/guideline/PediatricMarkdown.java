package org.opencb.biodata.models.pharma.guideline;

public class PediatricMarkdown {
    private float id;
    private String html;
    private float version;

    public float getId() {
        return id;
    }

    public PediatricMarkdown setId(float id) {
        this.id = id;
        return this;
    }

    public String getHtml() {
        return html;
    }

    public PediatricMarkdown setHtml(String html) {
        this.html = html;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public PediatricMarkdown setVersion(float version) {
        this.version = version;
        return this;
    }
}
