package org.opencb.biodata.models.clinical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

import java.util.Map;

public class ClinicalAnalyst {

    @DataField(id = "id", indexed = true,
            description = FieldConstants.GENERIC_ID_DESCRIPTION)
    private String id;

    @DataField(id = "name", indexed = true,
            description = FieldConstants.GENERIC_NAME_DESCRIPTION)
    private String name;

    @DataField(id = "email", indexed = true,
            description = FieldConstants.CLINICAL_ANALYST_EMAIL_DESCRIPTION)
    private String email;

    @DataField(id = "role", indexed = true,
            description = FieldConstants.CLINICAL_ANALYST_ROLE_DESCRIPTION)
    private String role;

    @DataField(id = "attributes", indexed = true,
            description = FieldConstants.CLINICAL_ANALYST_ATTRIBUTES_DESCRIPTION)
    private Map<String, Object> attributes;

    @Deprecated
    @DataField(id = "assignedBy")
    private String assignedBy;

    @Deprecated
    @DataField(id = "date")
    private String date;

    public ClinicalAnalyst() {
    }

    public ClinicalAnalyst(String id, String name, String email, String role, Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalAnalyst{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", role='").append(role).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ClinicalAnalyst setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClinicalAnalyst setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ClinicalAnalyst setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRole() {
        return role;
    }

    public ClinicalAnalyst setRole(String role) {
        this.role = role;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ClinicalAnalyst setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    @JsonIgnore
    @Deprecated
    public String getAssignedBy() {
        return assignedBy;
    }

    @Deprecated
    public ClinicalAnalyst setAssignedBy(String assignedBy) {
        return this;
    }

    @JsonIgnore
    @Deprecated
    public String getDate() {
        return date;
    }

    @Deprecated
    public ClinicalAnalyst setDate(String date) {
        return this;
    }
}
