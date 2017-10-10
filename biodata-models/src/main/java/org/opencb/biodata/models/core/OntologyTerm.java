package org.opencb.biodata.models.core;

import java.util.List;

/**
 * Created by fjlopez on 05/10/17.
 */
public class OntologyTerm {
    private String id;
    private String term;
    private String definition;
    private String source;
    private List<String> subClassOf;
    private List<String> synonyms;
    private List<String> xrefs;
    private String namespace;
    private List<String> alternativeIds;

    public OntologyTerm() {
    }

    public String getId() {
        return id;
    }

    public OntologyTerm setId(String id) {
        this.id = id;
        return this;
    }

    public String getTerm() {
        return term;
    }

    public OntologyTerm setTerm(String term) {
        this.term = term;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public OntologyTerm setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getSource() {
        return source;
    }

    public OntologyTerm setSource(String source) {
        this.source = source;
        return this;
    }

    public List<String> getSubClassOf() {
        return subClassOf;
    }

    public OntologyTerm setSubClassOf(List<String> subClassOf) {
        this.subClassOf = subClassOf;
        return this;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public OntologyTerm setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
        return this;
    }

    public List<String> getXrefs() {
        return xrefs;
    }

    public OntologyTerm setXrefs(List<String> xrefs) {
        this.xrefs = xrefs;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public OntologyTerm setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public List<String> getAlternativeIds() {
        return alternativeIds;
    }

    public OntologyTerm setAlternativeIds(List<String> alternativeIds) {
        this.alternativeIds = alternativeIds;
        return this;
    }
}
