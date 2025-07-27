package org.opencb.biodata.models.clinical.genefusion;

import org.opencb.commons.datastore.core.ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class GeneFusion {

    private String id;
    private String pair;
    private String source;
    private String gene5PrimeJunction;
    private String gene3PrimeJunction;
    private GeneFusionBreakpoint headGene;
    private GeneFusionBreakpoint tailGene;
    private List<String> diseases;
    private List<String> publications;
    private List<String> validations;
    private ObjectMap attributes;

    public GeneFusion() {
        this.diseases = new ArrayList<>();
        this.publications = new ArrayList<>();
        this.attributes = new ObjectMap();
    }

    public GeneFusion(String id, String pair, String source, String gene5PrimeJunction, String gene3PrimeJunction,
                      GeneFusionBreakpoint headGene, GeneFusionBreakpoint tailGene, List<String> diseases, List<String> publications,
                      List<String> validations, ObjectMap attributes) {
        this.id = id;
        this.pair = pair;
        this.source = source;
        this.gene5PrimeJunction = gene5PrimeJunction;
        this.gene3PrimeJunction = gene3PrimeJunction;
        this.headGene = headGene;
        this.tailGene = tailGene;
        this.diseases = diseases;
        this.publications = publications;
        this.validations = validations;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneFusion{");
        sb.append("id='").append(id).append('\'');
        sb.append("pair='").append(pair).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", gene5PrimeJunction='").append(gene5PrimeJunction).append('\'');
        sb.append(", gene3PrimeJunction='").append(gene3PrimeJunction).append('\'');
        sb.append(", headGene=").append(headGene);
        sb.append(", tailGene=").append(tailGene);
        sb.append(", diseases=").append(diseases);
        sb.append(", publications=").append(publications);
        sb.append(", validations='").append(validations).append('\'');
        sb.append(", attributes=").append(attributes.toJson());
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public GeneFusion setId(String id) {
        this.id = id;
        return this;
    }

    public String getPair() {
        return pair;
    }

    public GeneFusion setPair(String pair) {
        this.pair = pair;
        return this;
    }

    public String getSource() {
        return source;
    }

    public GeneFusion setSource(String source) {
        this.source = source;
        return this;
    }

    public String getGene5PrimeJunction() {
        return gene5PrimeJunction;
    }

    public GeneFusion setGene5PrimeJunction(String gene5PrimeJunction) {
        this.gene5PrimeJunction = gene5PrimeJunction;
        return this;
    }

    public String getGene3PrimeJunction() {
        return gene3PrimeJunction;
    }

    public GeneFusion setGene3PrimeJunction(String gene3PrimeJunction) {
        this.gene3PrimeJunction = gene3PrimeJunction;
        return this;
    }

    public GeneFusionBreakpoint getHeadGene() {
        return headGene;
    }

    public GeneFusion setHeadGene(GeneFusionBreakpoint headGene) {
        this.headGene = headGene;
        return this;
    }

    public GeneFusionBreakpoint getTailGene() {
        return tailGene;
    }

    public GeneFusion setTailGene(GeneFusionBreakpoint tailGene) {
        this.tailGene = tailGene;
        return this;
    }

    public List<String> getDiseases() {
        return diseases;
    }

    public GeneFusion setDiseases(List<String> diseases) {
        this.diseases = diseases;
        return this;
    }

    public List<String> getPublications() {
        return publications;
    }

    public GeneFusion setPublications(List<String> publications) {
        this.publications = publications;
        return this;
    }

    public List<String> getValidations() {
        return validations;
    }

    public GeneFusion setValidations(List<String> validations) {
        this.validations = validations;
        return this;
    }

    public ObjectMap getAttributes() {
        return attributes;
    }

    public GeneFusion setAttributes(ObjectMap attributes) {
        this.attributes = attributes;
        return this;
    }
}
