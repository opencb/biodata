package org.opencb.biodata.models.pharma;

import java.util.List;

public class PharmaFunctionalAnnotation extends PharmaBasicAnnotation {
    private String assayType;

    public PharmaFunctionalAnnotation() {
        super();
    }

    public PharmaFunctionalAnnotation(String variantId, String gene, List<String> drugs, String pmid, String phenotypeCategory,
                                      String significance, String discussion, String sentence, String alleles, String specialtyPopulation,
                                      List<PharmaStudyParameters> studyParameters, String assayType) {
        super(variantId, gene, drugs, pmid, phenotypeCategory, significance, discussion, sentence, alleles, specialtyPopulation,
                studyParameters);
        this.assayType = assayType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaFunctionalAnnotation{");
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
        sb.append(", assayType='").append(assayType).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAssayType() {
        return assayType;
    }

    public PharmaFunctionalAnnotation setAssayType(String assayType) {
        this.assayType = assayType;
        return this;
    }
}
