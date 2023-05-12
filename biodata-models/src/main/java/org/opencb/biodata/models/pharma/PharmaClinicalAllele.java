package org.opencb.biodata.models.pharma;

import java.util.Map;

public class PharmaClinicalAllele {
    private String allele;
    private String annotation;
    private String description;

    private Map<String, Object> attributes;

    public PharmaClinicalAllele() {
    }

    public PharmaClinicalAllele(String allele, String annotation, String description, Map<String, Object> attributes) {
        this.allele = allele;
        this.annotation = annotation;
        this.description = description;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalAllele{");
        sb.append("allele='").append(allele).append('\'');
        sb.append(", annotation='").append(annotation).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getAllele() {
        return allele;
    }

    public PharmaClinicalAllele setAllele(String allele) {
        this.allele = allele;
        return this;
    }

    public String getAnnotation() {
        return annotation;
    }

    public PharmaClinicalAllele setAnnotation(String annotation) {
        this.annotation = annotation;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PharmaClinicalAllele setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaClinicalAllele setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
