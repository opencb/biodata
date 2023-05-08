package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PharmaVariantAnnotation {
//Variant Annotation ID   Variant/Haplotypes      Gene    Drug(s) PMID    Phenotype Category      Significance    Notes   Sentence        Alleles Specialty Population

    private String variantId;
    private String gene;
    private List<String> drugs;
    private String alleles;
    private String specialtyPopulation;
    private String phenotypeCategory;
    private String significance;
    private String sentence;
    private String pmid;
    private String discussion;
    private List<Map<String, Object>> studyParameters;

    public PharmaVariantAnnotation() {
    }

    public PharmaVariantAnnotation(String variantId, String gene, List<String> drugs, String alleles, String specialtyPopulation,
                                   String phenotypeCategory, String significance, String sentence, String pmid, String discussion) {
        this.variantId = variantId;
        this.gene = gene;
        this.drugs = drugs;
        this.alleles = alleles;
        this.specialtyPopulation = specialtyPopulation;
        this.phenotypeCategory = phenotypeCategory;
        this.significance = significance;
        this.sentence = sentence;
        this.pmid = pmid;
        this.discussion = discussion;
        this.studyParameters = new ArrayList<>();
    }

    public PharmaVariantAnnotation(String variantId, String gene, List<String> drugs, String alleles, String specialtyPopulation,
                                   String phenotypeCategory, String significance, String sentence, String pmid, String discussion,
                                   List<Map<String, Object>> studyParameters) {
        this.variantId = variantId;
        this.gene = gene;
        this.drugs = drugs;
        this.alleles = alleles;
        this.specialtyPopulation = specialtyPopulation;
        this.phenotypeCategory = phenotypeCategory;
        this.significance = significance;
        this.sentence = sentence;
        this.pmid = pmid;
        this.discussion = discussion;
        this.studyParameters = studyParameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaVariantAnnotation{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", drugs=").append(drugs);
        sb.append(", alleles='").append(alleles).append('\'');
        sb.append(", specialtyPopulation='").append(specialtyPopulation).append('\'');
        sb.append(", phenotypeCategory='").append(phenotypeCategory).append('\'');
        sb.append(", significance='").append(significance).append('\'');
        sb.append(", sentence='").append(sentence).append('\'');
        sb.append(", pmid='").append(pmid).append('\'');
        sb.append(", discussion='").append(discussion).append('\'');
        sb.append(", studyParameters=").append(studyParameters);
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

    public String getGene() {
        return gene;
    }

    public PharmaVariantAnnotation setGene(String gene) {
        this.gene = gene;
        return this;
    }

    public List<String> getDrugs() {
        return drugs;
    }

    public PharmaVariantAnnotation setDrugs(List<String> drugs) {
        this.drugs = drugs;
        return this;
    }

    public String getAlleles() {
        return alleles;
    }

    public PharmaVariantAnnotation setAlleles(String alleles) {
        this.alleles = alleles;
        return this;
    }

    public String getSpecialtyPopulation() {
        return specialtyPopulation;
    }

    public PharmaVariantAnnotation setSpecialtyPopulation(String specialtyPopulation) {
        this.specialtyPopulation = specialtyPopulation;
        return this;
    }

    public String getPhenotypeCategory() {
        return phenotypeCategory;
    }

    public PharmaVariantAnnotation setPhenotypeCategory(String phenotypeCategory) {
        this.phenotypeCategory = phenotypeCategory;
        return this;
    }

    public String getSignificance() {
        return significance;
    }

    public PharmaVariantAnnotation setSignificance(String significance) {
        this.significance = significance;
        return this;
    }

    public String getSentence() {
        return sentence;
    }

    public PharmaVariantAnnotation setSentence(String sentence) {
        this.sentence = sentence;
        return this;
    }

    public String getPmid() {
        return pmid;
    }

    public PharmaVariantAnnotation setPmid(String pmid) {
        this.pmid = pmid;
        return this;
    }

    public String getDiscussion() {
        return discussion;
    }

    public PharmaVariantAnnotation setDiscussion(String discussion) {
        this.discussion = discussion;
        return this;
    }

    public List<Map<String, Object>> getStudyParameters() {
        return studyParameters;
    }

    public PharmaVariantAnnotation setStudyParameters(List<Map<String, Object>> studyParameters) {
        this.studyParameters = studyParameters;
        return this;
    }
}
