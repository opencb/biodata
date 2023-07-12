package org.opencb.biodata.models.pharma.guideline;

public class TextMarkdown {
    private float id;
    private String html;
    private float version;

    public float getId() {
        return id;
    }

    public TextMarkdown setId(float id) {
        this.id = id;
        return this;
    }

    public String getHtml() {
        return html;
    }

    public TextMarkdown setHtml(String html) {
        this.html = html;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public TextMarkdown setVersion(float version) {
        this.version = version;
        return this;
    }
}
