package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PharmaVariantAssociation {

    private String variantId;
    private List<String> haplotypes;
    private String geneName;
    private List<String> drugs;
    private String pubmed;
    private String phenotypeType;
    private String significance;
    private String assayType;
    private String population;
    private String alleles;
    private String description;
    private String discussion;
    private List<PharmaStudyParameters> studyParameters;

    private Map<String, Object> attributes;

    public PharmaVariantAssociation() {
        this.haplotypes = new ArrayList<>();
        this.drugs = new ArrayList<>();
        this.studyParameters = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaVariantAssociation{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", haplotypes=").append(haplotypes);
        sb.append(", geneName='").append(geneName).append('\'');
        sb.append(", drugs=").append(drugs);
        sb.append(", pubmed='").append(pubmed).append('\'');
        sb.append(", phenotypeType='").append(phenotypeType).append('\'');
        sb.append(", significance='").append(significance).append('\'');
        sb.append(", assayType='").append(assayType).append('\'');
        sb.append(", population='").append(population).append('\'');
        sb.append(", alleles='").append(alleles).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", discussion='").append(discussion).append('\'');
        sb.append(", studyParameters=").append(studyParameters);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getVariantId() {
        return variantId;
    }

    public PharmaVariantAssociation setVariantId(String variantId) {
        this.variantId = variantId;
        return this;
    }

    public List<String> getHaplotypes() {
        return haplotypes;
    }

    public PharmaVariantAssociation setHaplotypes(List<String> haplotypes) {
        this.haplotypes = haplotypes;
        return this;
    }

    public String getGeneName() {
        return geneName;
    }

    public PharmaVariantAssociation setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public List<String> getDrugs() {
        return drugs;
    }

    public PharmaVariantAssociation setDrugs(List<String> drugs) {
        this.drugs = drugs;
        return this;
    }

    public String getPubmed() {
        return pubmed;
    }

    public PharmaVariantAssociation setPubmed(String pubmed) {
        this.pubmed = pubmed;
        return this;
    }

    public String getPhenotypeType() {
        return phenotypeType;
    }

    public PharmaVariantAssociation setPhenotypeType(String phenotypeType) {
        this.phenotypeType = phenotypeType;
        return this;
    }

    public String getSignificance() {
        return significance;
    }

    public PharmaVariantAssociation setSignificance(String significance) {
        this.significance = significance;
        return this;
    }

    public String getAssayType() {
        return assayType;
    }

    public PharmaVariantAssociation setAssayType(String assayType) {
        this.assayType = assayType;
        return this;
    }

    public String getPopulation() {
        return population;
    }

    public PharmaVariantAssociation setPopulation(String population) {
        this.population = population;
        return this;
    }

    public String getAlleles() {
        return alleles;
    }

    public PharmaVariantAssociation setAlleles(String alleles) {
        this.alleles = alleles;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PharmaVariantAssociation setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDiscussion() {
        return discussion;
    }

    public PharmaVariantAssociation setDiscussion(String discussion) {
        this.discussion = discussion;
        return this;
    }

    public List<PharmaStudyParameters> getStudyParameters() {
        return studyParameters;
    }

    public PharmaVariantAssociation setStudyParameters(List<PharmaStudyParameters> studyParameters) {
        this.studyParameters = studyParameters;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaVariantAssociation setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
