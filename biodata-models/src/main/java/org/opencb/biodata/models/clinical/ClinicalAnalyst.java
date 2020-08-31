package org.opencb.biodata.models.clinical;

import java.util.List;

public class ClinicalAnalyst {
    private String id;
    private String name;
    private String email;
    private String assignedBy;
    private String date;

    public ClinicalAnalyst(String id, String name, String email, String assignedBy, String date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.assignedBy = assignedBy;
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalAnalyst{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", assignedBy='").append(assignedBy).append('\'');
        sb.append(", date='").append(date).append('\'');
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

    public String getAssignedBy() {
        return assignedBy;
    }

    public ClinicalAnalyst setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ClinicalAnalyst setDate(String date) {
        this.date = date;
        return this;
    }
}
