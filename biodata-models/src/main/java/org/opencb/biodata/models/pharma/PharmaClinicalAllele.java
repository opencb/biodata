package org.opencb.biodata.models.pharma;

public class PharmaClinicalAllele {
    private String allele;
    private String annotation;
    private String description;

    public PharmaClinicalAllele() {
    }

    public PharmaClinicalAllele(String allele, String annotation, String description) {
        this.allele = allele;
        this.annotation = annotation;
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalAllele{");
        sb.append("allele='").append(allele).append('\'');
        sb.append(", annotation='").append(annotation).append('\'');
        sb.append(", description='").append(description).append('\'');
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
}
