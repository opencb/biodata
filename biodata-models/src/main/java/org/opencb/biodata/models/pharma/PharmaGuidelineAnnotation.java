package org.opencb.biodata.models.pharma;

import org.opencb.biodata.models.pharma.guideline.Guideline;

import java.util.List;

public class PharmaGuidelineAnnotation {
    private List<Object> citations;
    private Guideline guideline;

    public List<Object> getCitations() {
        return citations;
    }

    public PharmaGuidelineAnnotation setCitations(List<Object> citations) {
        this.citations = citations;
        return this;
    }

    public Guideline getGuideline() {
        return guideline;
    }

    public PharmaGuidelineAnnotation setGuideline(Guideline guideline) {
        this.guideline = guideline;
        return this;
    }
}
