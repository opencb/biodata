package org.opencb.biodata.models.pharma.guideline;

public class CrossReference {
    private float id;
    private String name;
    private String resource;
    private String resourceId;
    private String _url;
    private float version;

    public float getId() {
        return id;
    }

    public CrossReference setId(float id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CrossReference setName(String name) {
        this.name = name;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public CrossReference setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getResourceId() {
        return resourceId;
    }

    public CrossReference setResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public String get_url() {
        return _url;
    }

    public CrossReference set_url(String _url) {
        this._url = _url;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public CrossReference setVersion(float version) {
        this.version = version;
        return this;
    }
}
