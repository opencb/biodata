package org.opencb.biodata.models.pharma;

import org.opencb.biodata.models.pharma.guideline.PharmaDosingGuideline;

import java.util.List;

public class PharmaGuidelineAnnotation {
    private List<Object> citations;
    private PharmaDosingGuideline guideline;

    public List<Object> getCitations() {
        return citations;
    }

    public PharmaGuidelineAnnotation setCitations(List<Object> citations) {
        this.citations = citations;
        return this;
    }

    public PharmaDosingGuideline getGuideline() {
        return guideline;
    }

    public PharmaGuidelineAnnotation setGuideline(PharmaDosingGuideline pharmaDosingGuideline) {
        this.guideline = pharmaDosingGuideline;
        return this;
    }
}
