package org.opencb.biodata.models.pharma;

import java.util.List;
import java.util.Map;

public class PharmaGeneAnnotation {
    private String id;
    private String name;
    private String symbol;
    private String ncbiGeneId;
    private String hgncId;
    private String ensebmlId;
    private boolean isVIP;
    private boolean hasVariantAnnotation;
    private List<PharmaGuidelineAnnotation> guidelineAnnotations;
    private Map<String, Object> attributes;

    public PharmaGeneAnnotation() {
    }

    public PharmaGeneAnnotation(String id, String name, String symbol, String ncbiGeneId, String hgncId, String ensebmlId, boolean isVIP,
                                boolean hasVariantAnnotation, List<PharmaGuidelineAnnotation> guidelineAnnotations,
                                Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.ncbiGeneId = ncbiGeneId;
        this.hgncId = hgncId;
        this.ensebmlId = ensebmlId;
        this.isVIP = isVIP;
        this.hasVariantAnnotation = hasVariantAnnotation;
        this.guidelineAnnotations = guidelineAnnotations;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaGeneAnnotation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", ncbiGeneId='").append(ncbiGeneId).append('\'');
        sb.append(", hgncId='").append(hgncId).append('\'');
        sb.append(", ensebmlId='").append(ensebmlId).append('\'');
        sb.append(", isVIP=").append(isVIP);
        sb.append(", hasVariantAnnotation=").append(hasVariantAnnotation);
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

    public String getSymbol() {
        return symbol;
    }

    public PharmaGeneAnnotation setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getNcbiGeneId() {
        return ncbiGeneId;
    }

    public PharmaGeneAnnotation setNcbiGeneId(String ncbiGeneId) {
        this.ncbiGeneId = ncbiGeneId;
        return this;
    }

    public String getHgncId() {
        return hgncId;
    }

    public PharmaGeneAnnotation setHgncId(String hgncId) {
        this.hgncId = hgncId;
        return this;
    }

    public String getEnsebmlId() {
        return ensebmlId;
    }

    public PharmaGeneAnnotation setEnsebmlId(String ensebmlId) {
        this.ensebmlId = ensebmlId;
        return this;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public PharmaGeneAnnotation setVIP(boolean VIP) {
        isVIP = VIP;
        return this;
    }

    public boolean isHasVariantAnnotation() {
        return hasVariantAnnotation;
    }

    public PharmaGeneAnnotation setHasVariantAnnotation(boolean hasVariantAnnotation) {
        this.hasVariantAnnotation = hasVariantAnnotation;
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
