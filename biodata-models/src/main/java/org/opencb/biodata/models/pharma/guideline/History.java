package org.opencb.biodata.models.pharma.guideline;

public class History {
    private float id;
    private String date;
    private String description;
    private String type;
    private float version;

    public float getId() {
        return id;
    }

    public History setId(float id) {
        this.id = id;
        return this;
    }

    public String getDate() {
        return date;
    }

    public History setDate(String date) {
        this.date = date;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public History setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public History setType(String type) {
        this.type = type;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public History setVersion(float version) {
        this.version = version;
        return this;
    }
}
