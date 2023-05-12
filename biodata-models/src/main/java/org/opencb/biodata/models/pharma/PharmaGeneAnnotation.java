package org.opencb.biodata.models.pharma;

import org.opencb.biodata.models.core.Xref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PharmaGeneAnnotation {
    private String id;
    private String name;
    private List<Xref> xrefs;
    private boolean hasVariantAnnotation;
    private List<String> evidences;
    private String confidence;
    private List<String> pubmed;
    private List<PharmaGuidelineAnnotation> guidelineAnnotations;
    private Map<String, Object> attributes;

    public PharmaGeneAnnotation() {
        this.xrefs = new ArrayList<>();
        this.evidences = new ArrayList<>();
        this.pubmed = new ArrayList<>();
        this.guidelineAnnotations = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    public PharmaGeneAnnotation(String id, String name, List<Xref> xrefs, boolean hasVariantAnnotation, List<String> evidences,
                                String confidence, List<String> pubmed, List<PharmaGuidelineAnnotation> guidelineAnnotations,
                                Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.xrefs = xrefs;
        this.hasVariantAnnotation = hasVariantAnnotation;
        this.evidences = evidences;
        this.confidence = confidence;
        this.pubmed = pubmed;
        this.guidelineAnnotations = guidelineAnnotations;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaGeneAnnotation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", xrefs=").append(xrefs);
        sb.append(", hasVariantAnnotation=").append(hasVariantAnnotation);
        sb.append(", evidences=").append(evidences);
        sb.append(", confidence='").append(confidence).append('\'');
        sb.append(", pubmed=").append(pubmed);
        sb.append(", guidelineAnnotations=").append(guidelineAnnotations);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PharmaGeneAnnotation setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PharmaGeneAnnotation setName(String name) {
        this.name = name;
        return this;
    }

    public List<Xref> getXrefs() {
        return xrefs;
    }

    public PharmaGeneAnnotation setXrefs(List<Xref> xrefs) {
        this.xrefs = xrefs;
        return this;
    }

    public boolean isHasVariantAnnotation() {
        return hasVariantAnnotation;
    }

    public PharmaGeneAnnotation setHasVariantAnnotation(boolean hasVariantAnnotation) {
        this.hasVariantAnnotation = hasVariantAnnotation;
        return this;
    }

    public List<String> getEvidences() {
        return evidences;
    }

    public PharmaGeneAnnotation setEvidences(List<String> evidences) {
        this.evidences = evidences;
        return this;
    }

    public String getConfidence() {
        return confidence;
    }

    public PharmaGeneAnnotation setConfidence(String confidence) {
        this.confidence = confidence;
        return this;
    }

    public List<String> getPubmed() {
        return pubmed;
    }

    public PharmaGeneAnnotation setPubmed(List<String> pubmed) {
        this.pubmed = pubmed;
        return this;
    }

    public List<PharmaGuidelineAnnotation> getGuidelineAnnotations() {
        return guidelineAnnotations;
    }

    public PharmaGeneAnnotation setGuidelineAnnotations(List<PharmaGuidelineAnnotation> guidelineAnnotations) {
        this.guidelineAnnotations = guidelineAnnotations;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaGeneAnnotation setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
