package org.opencb.biodata.models.clinical;

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

public class ClinicalAudit {
    @DataField(id = "author", indexed = true,
            description = FieldConstants.AUDIT_AUTHOR_DESCRIPTION)
    private String author;

    @DataField(id = "action", indexed = true,
            description = FieldConstants.AUDIT_ACTION_DESCRIPTION)
    private Action action;

    @DataField(id = "message", indexed = true,
            description = FieldConstants.AUDIT_MESSAGE_DESCRIPTION)
    private String message;

    @DataField(id = "date", indexed = true,
            description = FieldConstants.AUDIT_DATE_DESCRIPTION)
    private String date;


    public ClinicalAudit() {
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

    public enum Action {
        CREATE_CLINICAL_ANALYSIS,
        CREATE_INTERPRETATION,
        UPDATE_CLINICAL_ANALYSIS,
        DELETE_CLINICAL_ANALYSIS,
        UPDATE_INTERPRETATION,
        REVERT_INTERPRETATION,
        CLEAR_INTERPRETATION,
        MERGE_INTERPRETATION,
        SWAP_INTERPRETATION,
        DELETE_INTERPRETATION
    }
}
