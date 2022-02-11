package org.opencb.biodata.models.clinical;

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

import java.util.List;

public class ClinicalComment {

    @DataField(id = "author", indexed = true,
            description = FieldConstants.CLINICAL_COMMENT_AUTHOR_DESCRIPTION)
    private String author;

    @DataField(id = "message", indexed = true,
            description = FieldConstants.CLINICAL_COMMENT_MESSAGE_DESCRIPTION)
    private String message;

    @DataField(id = "tags", indexed = true,
            description = FieldConstants.CLINICAL_COMMENT_TAGS_DESCRIPTION)
    private List<String> tags;

    @DataField(id = "date", indexed = true,
            description = FieldConstants.CLINICAL_COMMENT_DATE_DESCRIPTION)
    private String date;

    public ClinicalComment() {
    }

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
