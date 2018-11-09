package org.opencb.biodata.models.commons;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Phenotype extends OntologyTerm {

    private String ageOfOnset;
    private Status status;

    private Map<String, String> attributes;

    public enum Status {
        OBSERVED,
        NOT_OBSERVED,
        UNKNOWN
    }

    public Phenotype() {
    }

    public Phenotype(String id, String name, String source) {
        this(id, name, source, "", Status.UNKNOWN, Collections.emptyMap());
    }

    public Phenotype(String id, String name, String source, Status status) {
        this(id, name, source, "", status, Collections.emptyMap());
    }

    public Phenotype(String id, String name, String source, String ageOfOnset, Status status, Map<String, String> attributes) {
        super(id, name, source);
        this.ageOfOnset = ageOfOnset;
        this.status = status;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Phenotype{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", status=").append(status);
        sb.append(", ageOfOnset='").append(ageOfOnset).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Phenotype setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Phenotype setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Phenotype setSource(String source) {
        this.source = source;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Phenotype setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getAgeOfOnset() {
        return ageOfOnset;
    }

    public Phenotype setAgeOfOnset(String ageOfOnset) {
        this.ageOfOnset = ageOfOnset;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Phenotype setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }
}
