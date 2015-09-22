package org.opencb.biodata.models.variation;

import org.opencb.biodata.models.protein.ProteinFeature;
import org.opencb.biodata.models.variant.annotation.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjlopez on 18/09/15.
 */
public class ProteinVariantAnnotation {

    private String uniprotProteinAccession;
    private int position;
    private String reference;
    private String alternate;
    private String uniprotVariantId;
    private String functionalDescription;
    private List<Score> proteinSubstitutionScores = null;
    private List<String> uniprotKeywords;
    private List<ProteinFeature> proteinFeatureList;

    public ProteinVariantAnnotation() {
    }

    public ProteinVariantAnnotation(int aaPosition, String aaReference, String aaAlternate,
                                    List<Score> proteinSubstitutionScores) {
        this(null, aaPosition, aaReference, aaAlternate, null, null, proteinSubstitutionScores, null, null);

    }

    public ProteinVariantAnnotation(String uniprotProteinAccession, int position, String reference, String alternate, String uniprotVariantId, String functionalDescription, List<Score> proteinSubstitutionScores, List<String> uniprotKeywords, List<ProteinFeature> proteinFeatureList) {
        this.uniprotProteinAccession = uniprotProteinAccession;
        this.position = position;
        this.reference = reference;
        this.alternate = alternate;
        this.uniprotVariantId = uniprotVariantId;
        this.functionalDescription = functionalDescription;
        this.proteinSubstitutionScores = proteinSubstitutionScores;
        this.uniprotKeywords = uniprotKeywords;
        this.proteinFeatureList = proteinFeatureList;
    }

    public String getUniprotProteinAccession() {
        return uniprotProteinAccession;
    }

    public void setUniprotProteinAccession(String uniprotProteinAccession) {
        this.uniprotProteinAccession = uniprotProteinAccession;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public String getUniprotVariantId() {
        return uniprotVariantId;
    }

    public void setUniprotVariantId(String uniprotVariantId) {
        this.uniprotVariantId = uniprotVariantId;
    }

    public String getFunctionalDescription() {
        return functionalDescription;
    }

    public void setFunctionalDescription(String functionalDescription) {
        this.functionalDescription = functionalDescription;
    }

    public List<Score> getProteinSubstitutionScores() {
        return proteinSubstitutionScores;
    }

    public void setProteinSubstitutionScores(List<Score> proteinSubstitutionScores) {
        this.proteinSubstitutionScores = proteinSubstitutionScores;
    }

    public void addProteinSubstitutionScore(Score score) {
        if (this.proteinSubstitutionScores == null) {
            proteinSubstitutionScores = new ArrayList<>();
        }
        proteinSubstitutionScores.add(score);
    }

    public List<String> getUniprotKeywords() {
        return uniprotKeywords;
    }

    public void setUniprotKeywords(List<String> uniprotKeywords) {
        this.uniprotKeywords = uniprotKeywords;
    }

    public void addUniprotKeyword(String keyword) {
        if(uniprotKeywords==null) {
            uniprotKeywords = new ArrayList<>();
        }
        uniprotKeywords.add(keyword);
    }

    public List<ProteinFeature> getProteinFeatureList() {
        return proteinFeatureList;
    }

    public void setProteinFeatureList(List<ProteinFeature> proteinFeatureList) {
        this.proteinFeatureList = proteinFeatureList;
    }

    public void addProteinFeature(ProteinFeature proteinFeature) {
        if(proteinFeatureList==null) {
            proteinFeatureList = new ArrayList<>();
        }
        proteinFeatureList.add(proteinFeature);
    }
}
