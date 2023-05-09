package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PharmaVariantAssociation {

    private String variantId;
    private String gene;
    private List<String> drugs;
    private String pubmed;
    private String phenotypeCategory;
    private String significance;
    private String assayType;
    private String specialtyPopulation;
    private String alleles;
    private String sentence;
    private String discussion;
    private List<PharmaStudyParameters> studyParameters;

    private Map<String, Object> attributes;

    public PharmaVariantAssociation() {
        this.drugs = new ArrayList<>();
        this.studyParameters = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaBasicAnnotation{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", drugs=").append(drugs);
        sb.append(", pubmed='").append(pubmed).append('\'');
        sb.append(", phenotypeCategory='").append(phenotypeCategory).append('\'');
        sb.append(", significance='").append(significance).append('\'');
        sb.append(", assayType='").append(assayType).append('\'');
        sb.append(", specialtyPopulation='").append(specialtyPopulation).append('\'');
        sb.append(", alleles='").append(alleles).append('\'');
        sb.append(", sentence='").append(sentence).append('\'');
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

    public String getGene() {
        return gene;
    }

    public PharmaVariantAssociation setGene(String gene) {
        this.gene = gene;
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

    public String getPhenotypeCategory() {
        return phenotypeCategory;
    }

    public PharmaVariantAssociation setPhenotypeCategory(String phenotypeCategory) {
        this.phenotypeCategory = phenotypeCategory;
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

    public String getSpecialtyPopulation() {
        return specialtyPopulation;
    }

    public PharmaVariantAssociation setSpecialtyPopulation(String specialtyPopulation) {
        this.specialtyPopulation = specialtyPopulation;
        return this;
    }

    public String getAlleles() {
        return alleles;
    }

    public PharmaVariantAssociation setAlleles(String alleles) {
        this.alleles = alleles;
        return this;
    }

    public String getSentence() {
        return sentence;
    }

    public PharmaVariantAssociation setSentence(String sentence) {
        this.sentence = sentence;
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
