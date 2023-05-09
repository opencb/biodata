package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PharmaClinicalAnnotation {
    private String variantId;
    private String location;
    private String chromosome;
    private int position;
    private String gene;
    private List<String> phenotypes;
    private String phenotypeCategory;
    private String levelOfEvidence;
    private String levelOverride;
    private String levelModifiers;
    private String score;
    private String url;
    private String specialtyPopulation;
    private List<PharmaClinicalEvidence> evidences;
    private List<PharmaClinicalAllele> alleles;
    private Map<String, Object> attributes;

    public PharmaClinicalAnnotation() {
        phenotypes = new ArrayList<>();
        evidences = new ArrayList<>();
        alleles = new ArrayList<>();
        attributes = new HashMap<>();
    }

    public PharmaClinicalAnnotation(String variantId, String location, String chromosome, int position, String gene,
                                    List<String> phenotypes, String phenotypeCategory, String levelOfEvidence,
                                    String levelOverride, String levelModifiers, String score, String url,
                                    String specialtyPopulation, List<PharmaClinicalEvidence> evidences,
                                    List<PharmaClinicalAllele> alleles, Map<String, Object> attributes) {
        this.variantId = variantId;
        this.location = location;
        this.chromosome = chromosome;
        this.position = position;
        this.gene = gene;
        this.phenotypes = phenotypes;
        this.phenotypeCategory = phenotypeCategory;
        this.levelOfEvidence = levelOfEvidence;
        this.levelOverride = levelOverride;
        this.levelModifiers = levelModifiers;
        this.score = score;
        this.url = url;
        this.specialtyPopulation = specialtyPopulation;
        this.evidences = evidences;
        this.alleles = alleles;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaClinicalAnnotation{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", phenotypeCategory='").append(phenotypeCategory).append('\'');
        sb.append(", levelOfEvidence='").append(levelOfEvidence).append('\'');
        sb.append(", levelOverride='").append(levelOverride).append('\'');
        sb.append(", levelModifiers='").append(levelModifiers).append('\'');
        sb.append(", score='").append(score).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", specialtyPopulation='").append(specialtyPopulation).append('\'');
        sb.append(", evidences=").append(evidences);
        sb.append(", alleles=").append(alleles);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getVariantId() {
        return variantId;
    }

    public PharmaClinicalAnnotation setVariantId(String variantId) {
        this.variantId = variantId;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public PharmaClinicalAnnotation setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public PharmaClinicalAnnotation setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public PharmaClinicalAnnotation setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getGene() {
        return gene;
    }

    public PharmaClinicalAnnotation setGene(String gene) {
        this.gene = gene;
        return this;
    }

    public List<String> getPhenotypes() {
        return phenotypes;
    }

    public PharmaClinicalAnnotation setPhenotypes(List<String> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public String getPhenotypeCategory() {
        return phenotypeCategory;
    }

    public PharmaClinicalAnnotation setPhenotypeCategory(String phenotypeCategory) {
        this.phenotypeCategory = phenotypeCategory;
        return this;
    }

    public String getLevelOfEvidence() {
        return levelOfEvidence;
    }

    public PharmaClinicalAnnotation setLevelOfEvidence(String levelOfEvidence) {
        this.levelOfEvidence = levelOfEvidence;
        return this;
    }

    public String getLevelOverride() {
        return levelOverride;
    }

    public PharmaClinicalAnnotation setLevelOverride(String levelOverride) {
        this.levelOverride = levelOverride;
        return this;
    }

    public String getLevelModifiers() {
        return levelModifiers;
    }

    public PharmaClinicalAnnotation setLevelModifiers(String levelModifiers) {
        this.levelModifiers = levelModifiers;
        return this;
    }

    public String getScore() {
        return score;
    }

    public PharmaClinicalAnnotation setScore(String score) {
        this.score = score;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PharmaClinicalAnnotation setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSpecialtyPopulation() {
        return specialtyPopulation;
    }

    public PharmaClinicalAnnotation setSpecialtyPopulation(String specialtyPopulation) {
        this.specialtyPopulation = specialtyPopulation;
        return this;
    }

    public List<PharmaClinicalEvidence> getEvidences() {
        return evidences;
    }

    public PharmaClinicalAnnotation setEvidences(List<PharmaClinicalEvidence> evidences) {
        this.evidences = evidences;
        return this;
    }

    public List<PharmaClinicalAllele> getAlleles() {
        return alleles;
    }

    public PharmaClinicalAnnotation setAlleles(List<PharmaClinicalAllele> alleles) {
        this.alleles = alleles;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaClinicalAnnotation setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
