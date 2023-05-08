package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class Term {
    private float id;
    private String description;
    private List<String> parents;
    private String resource;
    private String _url;
    private String term;
    private String termId;
    private float version;

    public float getId() {
        return id;
    }

    public Term setId(float id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Term setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getParents() {
        return parents;
    }

    public Term setParents(List<String> parents) {
        this.parents = parents;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public Term setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String get_url() {
        return _url;
    }

    public Term set_url(String _url) {
        this._url = _url;
        return this;
    }

    public String getTerm() {
        return term;
    }

    public Term setTerm(String term) {
        this.term = term;
        return this;
    }

    public String getTermId() {
        return termId;
    }

    public Term setTermId(String termId) {
        this.termId = termId;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public Term setVersion(float version) {
        this.version = version;
        return this;
    }
}
