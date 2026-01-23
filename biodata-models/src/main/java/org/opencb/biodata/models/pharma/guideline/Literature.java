package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class Literature {
    private float id;
    private String title;
    private String _sameAs;
    private List<CrossReference> crossReferences;
    private String objCls;
    private String pubDate;
    private List<Term> terms;
    private String type;

    public float getId() {
        return id;
    }

    public Literature setId(float id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Literature setTitle(String title) {
        this.title = title;
        return this;
    }

    public String get_sameAs() {
        return _sameAs;
    }

    public Literature set_sameAs(String _sameAs) {
        this._sameAs = _sameAs;
        return this;
    }

    public List<CrossReference> getCrossReferences() {
        return crossReferences;
    }

    public Literature setCrossReferences(List<CrossReference> crossReferences) {
        this.crossReferences = crossReferences;
        return this;
    }

    public String getObjCls() {
        return objCls;
    }

    public Literature setObjCls(String objCls) {
        this.objCls = objCls;
        return this;
    }

    public String getPubDate() {
        return pubDate;
    }

    public Literature setPubDate(String pubDate) {
        this.pubDate = pubDate;
        return this;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public Literature setTerms(List<Term> terms) {
        this.terms = terms;
        return this;
    }

    public String getType() {
        return type;
    }

    public Literature setType(String type) {
        this.type = type;
        return this;
    }
}


