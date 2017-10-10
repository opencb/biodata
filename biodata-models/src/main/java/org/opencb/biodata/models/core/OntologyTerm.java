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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologyTerm that = (OntologyTerm) o;

        if (!id.equals(that.id)) return false;
        if (term != null ? !term.equals(that.term) : that.term != null) return false;
        if (definition != null ? !definition.equals(that.definition) : that.definition != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (subClassOf != null ? !subClassOf.equals(that.subClassOf) : that.subClassOf != null) return false;
        if (synonyms != null ? !synonyms.equals(that.synonyms) : that.synonyms != null) return false;
        if (xrefs != null ? !xrefs.equals(that.xrefs) : that.xrefs != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        return alternativeIds != null ? alternativeIds.equals(that.alternativeIds) : that.alternativeIds == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (term != null ? term.hashCode() : 0);
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (subClassOf != null ? subClassOf.hashCode() : 0);
        result = 31 * result + (synonyms != null ? synonyms.hashCode() : 0);
        result = 31 * result + (xrefs != null ? xrefs.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (alternativeIds != null ? alternativeIds.hashCode() : 0);
        return result;
    }
}
