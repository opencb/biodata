package org.opencb.biodata.models.pharma;

public class PharmaClinicalAllele {
    private String id;
    private String allele;
    private String annotation;
    private String function;

    public PharmaClinicalAllele() {
    }

    public PharmaClinicalAllele(String id, String allele, String annotation, String function) {
        this.id = id;
        this.allele = allele;
        this.annotation = annotation;
        this.function = function;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalAllele{");
        sb.append("id='").append(id).append('\'');
        sb.append(", allele='").append(allele).append('\'');
        sb.append(", annotation='").append(annotation).append('\'');
        sb.append(", function='").append(function).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PharmaClinicalAllele setId(String id) {
        this.id = id;
        return this;
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

    public String getFunction() {
        return function;
    }

    public PharmaClinicalAllele setFunction(String function) {
        this.function = function;
        return this;
    }
}
