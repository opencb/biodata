package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class Literature {
    private float id;
    private String title;
    private String _sameAs;
    private List<CrossReference> crossReferences;
    private String objCls;
    private List<Term> terms;

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

    public List<Term> getTerms() {
        return terms;
    }

    public Literature setTerms(List<Term> terms) {
        this.terms = terms;
        return this;
    }
}


