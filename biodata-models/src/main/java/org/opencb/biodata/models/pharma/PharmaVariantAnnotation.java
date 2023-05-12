package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PharmaVariantAnnotation {
    private String variantId;
    private String location;
    private String chromosome;
    private int position;
    private String geneId;
    private String geneName;
    private List<String> phenotypes;
    private String phenotypeType;
    private String confidence;
    private String score;
    private String url;
    private String population;
    private List<PharmaClinicalEvidence> evidences;
    private List<PharmaClinicalAllele> alleles;

    private Map<String, Object> attributes;

    public PharmaVariantAnnotation() {
        phenotypes = new ArrayList<>();
        evidences = new ArrayList<>();
        alleles = new ArrayList<>();
        attributes = new HashMap<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaVariantAnnotation{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", geneId='").append(geneId).append('\'');
        sb.append(", geneName='").append(geneName).append('\'');
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", phenotypeType='").append(phenotypeType).append('\'');
        sb.append(", confidence='").append(confidence).append('\'');
        sb.append(", score='").append(score).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", population='").append(population).append('\'');
        sb.append(", evidences=").append(evidences);
        sb.append(", alleles=").append(alleles);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getVariantId() {
        return variantId;
    }

    public PharmaVariantAnnotation setVariantId(String variantId) {
        this.variantId = variantId;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public PharmaVariantAnnotation setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public PharmaVariantAnnotation setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public PharmaVariantAnnotation setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getGeneId() {
        return geneId;
    }

    public PharmaVariantAnnotation setGeneId(String geneId) {
        this.geneId = geneId;
        return this;
    }

    public String getGeneName() {
        return geneName;
    }

    public PharmaVariantAnnotation setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public List<String> getPhenotypes() {
        return phenotypes;
    }

    public PharmaVariantAnnotation setPhenotypes(List<String> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public String getPhenotypeType() {
        return phenotypeType;
    }

    public PharmaVariantAnnotation setPhenotypeType(String phenotypeType) {
        this.phenotypeType = phenotypeType;
        return this;
    }

    public String getConfidence() {
        return confidence;
    }

    public PharmaVariantAnnotation setConfidence(String confidence) {
        this.confidence = confidence;
        return this;
    }

    public String getScore() {
        return score;
    }

    public PharmaVariantAnnotation setScore(String score) {
        this.score = score;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PharmaVariantAnnotation setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getPopulation() {
        return population;
    }

    public PharmaVariantAnnotation setPopulation(String population) {
        this.population = population;
        return this;
    }

    public List<PharmaClinicalEvidence> getEvidences() {
        return evidences;
    }

    public PharmaVariantAnnotation setEvidences(List<PharmaClinicalEvidence> evidences) {
        this.evidences = evidences;
        return this;
    }

    public List<PharmaClinicalAllele> getAlleles() {
        return alleles;
    }

    public PharmaVariantAnnotation setAlleles(List<PharmaClinicalAllele> alleles) {
        this.alleles = alleles;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaVariantAnnotation setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
