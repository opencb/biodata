package org.opencb.biodata.models.clinical;

public class ClinicalAcmg {

    private String classification;
    private String strength;
    private String comment;
    private String author;
    private String date;

    public ClinicalAcmg() {
    }

    public ClinicalAcmg(String classification, String strength, String comment, String author, String date) {
        this.classification = classification;
        this.strength = strength;
        this.comment = comment;
        this.author = author;
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalAcmg{");
        sb.append("classification='").append(classification).append('\'');
        sb.append(", strength='").append(strength).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getClassification() {
        return classification;
    }

    public ClinicalAcmg setClassification(String classification) {
        this.classification = classification;
        return this;
    }

    public String getStrength() {
        return strength;
    }

    public ClinicalAcmg setStrength(String strength) {
        this.strength = strength;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ClinicalAcmg setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public ClinicalAcmg setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ClinicalAcmg setDate(String date) {
        this.date = date;
        return this;
    }
}
