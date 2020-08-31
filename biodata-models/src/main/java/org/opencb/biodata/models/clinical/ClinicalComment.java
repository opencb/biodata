package org.opencb.biodata.models.clinical;

import java.util.ArrayList;
import java.util.List;

public class ClinicalComment {
    private String author;
    private String message;
    private List<String> tags;
    private String date;

    public ClinicalComment(String author, String message, List<String> tags, String date) {
        this.author = author;
        this.message = message;
        this.tags = tags;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public ClinicalComment setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ClinicalComment setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public ClinicalComment setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ClinicalComment setDate(String date) {
        this.date = date;
        return this;
    }
}
