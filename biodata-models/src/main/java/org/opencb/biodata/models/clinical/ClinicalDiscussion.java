package org.opencb.biodata.models.clinical;

public class ClinicalDiscussion {

    private String author;
    private String date;
    private String message;

    public ClinicalDiscussion() {
    }

    public ClinicalDiscussion(String author, String date, String message) {
        this.author = author;
        this.date = date;
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalDiscussion{");
        sb.append("author='").append(author).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAuthor() {
        return author;
    }

    public ClinicalDiscussion setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ClinicalDiscussion setDate(String date) {
        this.date = date;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ClinicalDiscussion setMessage(String message) {
        this.message = message;
        return this;
    }
}
