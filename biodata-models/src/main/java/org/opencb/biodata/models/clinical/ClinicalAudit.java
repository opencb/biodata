package org.opencb.biodata.models.clinical;

public class ClinicalAudit {
    private String author;
    private Action action; {

    }
    private String message;
    private String date;


    public enum Action {
        CREATE_CLINICAL_ANALYSIS,
        CREATE_INTERPRETATION,
        UPDATE_CLINICAL_ANALYSIS,
        UPDTATE_INTERPRETATION
    }

    public ClinicalAudit(String author, Action action, String message, String date) {
        this.author = author;
        this.action = action;
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalAudit{");
        sb.append("author='").append(author).append('\'');
        sb.append(", action=").append(action);
        sb.append(", message='").append(message).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAuthor() {
        return author;
    }

    public ClinicalAudit setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Action getAction() {
        return action;
    }

    public ClinicalAudit setAction(Action action) {
        this.action = action;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ClinicalAudit setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ClinicalAudit setDate(String date) {
        this.date = date;
        return this;
    }
}
