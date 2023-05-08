package org.opencb.biodata.models.pharma;

import java.util.ArrayList;
import java.util.List;

public class PharmaBasicAnnotation {
    protected String variantId;
    protected String gene;
    protected List<String> drugs;
    protected String pmid;
    protected String phenotypeCategory;
    protected String significance;
    protected String discussion;
    protected String sentence;
    protected String alleles;
    protected String specialtyPopulation;
    protected List<PharmaStudyParameters> studyParameters;

    public PharmaBasicAnnotation() {
        this.drugs = new ArrayList<>();
        this.studyParameters = new ArrayList<>();
    }

    public PharmaBasicAnnotation(String variantId, String gene, List<String> drugs, String pmid, String phenotypeCategory,
                                 String significance, String discussion, String sentence, String alleles, String specialtyPopulation,
                                 List<PharmaStudyParameters> studyParameters) {
        this.variantId = variantId;
        this.gene = gene;
        this.drugs = drugs;
        this.pmid = pmid;
        this.phenotypeCategory = phenotypeCategory;
        this.significance = significance;
        this.discussion = discussion;
        this.sentence = sentence;
        this.alleles = alleles;
        this.specialtyPopulation = specialtyPopulation;
        this.studyParameters = studyParameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaBasicAnnotation{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", drugs=").append(drugs);
        sb.append(", pmid='").append(pmid).append('\'');
        sb.append(", phenotypeCategory='").append(phenotypeCategory).append('\'');
        sb.append(", significance='").append(significance).append('\'');
        sb.append(", discussion='").append(discussion).append('\'');
        sb.append(", sentence='").append(sentence).append('\'');
        sb.append(", alleles='").append(alleles).append('\'');
        sb.append(", specialtyPopulation='").append(specialtyPopulation).append('\'');
        sb.append(", studyParameters=").append(studyParameters);
        sb.append('}');
        return sb.toString();
    }

    public String getVariantId() {
        return variantId;
    }

    public PharmaBasicAnnotation setVariantId(String variantId) {
        this.variantId = variantId;
        return this;
    }

    public String getGene() {
        return gene;
    }

    public PharmaBasicAnnotation setGene(String gene) {
        this.gene = gene;
        return this;
    }

    public List<String> getDrugs() {
        return drugs;
    }

    public PharmaBasicAnnotation setDrugs(List<String> drugs) {
        this.drugs = drugs;
        return this;
    }

    public String getPmid() {
        return pmid;
    }

    public PharmaBasicAnnotation setPmid(String pmid) {
        this.pmid = pmid;
        return this;
    }

    public String getPhenotypeCategory() {
        return phenotypeCategory;
    }

    public PharmaBasicAnnotation setPhenotypeCategory(String phenotypeCategory) {
        this.phenotypeCategory = phenotypeCategory;
        return this;
    }

    public String getSignificance() {
        return significance;
    }

    public PharmaBasicAnnotation setSignificance(String significance) {
        this.significance = significance;
        return this;
    }

    public String getDiscussion() {
        return discussion;
    }

    public PharmaBasicAnnotation setDiscussion(String discussion) {
        this.discussion = discussion;
        return this;
    }

    public String getSentence() {
        return sentence;
    }

    public PharmaBasicAnnotation setSentence(String sentence) {
        this.sentence = sentence;
        return this;
    }

    public String getAlleles() {
        return alleles;
    }

    public PharmaBasicAnnotation setAlleles(String alleles) {
        this.alleles = alleles;
        return this;
    }

    public String getSpecialtyPopulation() {
        return specialtyPopulation;
    }

    public PharmaBasicAnnotation setSpecialtyPopulation(String specialtyPopulation) {
        this.specialtyPopulation = specialtyPopulation;
        return this;
    }

    public List<PharmaStudyParameters> getStudyParameters() {
        return studyParameters;
    }

    public PharmaBasicAnnotation setStudyParameters(List<PharmaStudyParameters> studyParameters) {
        this.studyParameters = studyParameters;
        return this;
    }
}
