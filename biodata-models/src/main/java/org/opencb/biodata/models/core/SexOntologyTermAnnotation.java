package org.opencb.biodata.models.core;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.pedigree.IndividualProperty;

import java.util.HashMap;
import java.util.Map;

import static org.opencb.biodata.models.pedigree.IndividualProperty.Sex.*;

public class SexOntologyTermAnnotation extends OntologyTermAnnotation {

    public SexOntologyTermAnnotation() {
        super();
    }

    public SexOntologyTermAnnotation(String id, String name, String description, String source, String url,
                                     Map<String, String> attributes) {
        super(id, name, description, source, url, attributes);
    }

    public static SexOntologyTermAnnotation initMale() {
        return new SexOntologyTermAnnotation(MALE.name(), "", "", "", "", new HashMap<>());
    }

    public static SexOntologyTermAnnotation initFemale() {
        return new SexOntologyTermAnnotation(FEMALE.name(), "", "", "", "", new HashMap<>());
    }

    public static SexOntologyTermAnnotation initUnknown() {
        return new SexOntologyTermAnnotation(UNKNOWN.name(), "", "", "", "", new HashMap<>());
    }

    public IndividualProperty.Sex getSex() {
        if (StringUtils.isEmpty(id)) {
            return UNKNOWN;
        } else {
            if (id.equalsIgnoreCase(MALE.name())) {
                return MALE;
            } else if (id.equalsIgnoreCase(FEMALE.name())) {
                return FEMALE;
            } else if (id.equalsIgnoreCase(UNDETERMINED.name())) {
                return UNDETERMINED;
            } else {
                return UNKNOWN;
            }
        }
    }

    public SexOntologyTermAnnotation setId(String id) {
        this.id = id;
        return this;
    }

    public SexOntologyTermAnnotation setName(String name) {
        this.name = name;
        return this;
    }

    public SexOntologyTermAnnotation setDescription(String description) {
        this.description = description;
        return this;
    }

    public SexOntologyTermAnnotation setSource(String source) {
        this.source = source;
        return this;
    }

    public SexOntologyTermAnnotation setUrl(String url) {
        this.url = url;
        return this;
    }

    public SexOntologyTermAnnotation setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }
}
