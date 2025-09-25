package org.opencb.biodata.models.core.civic;

import java.util.ArrayList;
import java.util.List;

public class CivicVariant {

    // From VariantSummaries.tsv
    private String variantId;
    private String variantCivicUrl;
    private String variant;
    private List<String> variantAliases;
    private Boolean isFlagged;
    private List<String> variantGroups;
    private List<String> variantTypes;
    private String lastReviewDate;
    private String gene;
    private String entrezId;
    private String chromosome;
    private String start;
    private String stop;
    private String referenceBases;
    private String variantBases;
    private String representativeTranscript;
    private String ensemblVersion;
    private String referenceBuild;
    private List<String> hgvsDescriptions;
    private String alleleRegistryId;
    private List<String> clinvarIds;
    private String ncitId;
    private String viccCompliantName;

    // Associated data
    private CivicFeature feature;
    private List<CivicMolecularProfile> molecularProfiles;

    public CivicVariant() {
        this.variantAliases = new ArrayList<>();
        this.variantGroups = new ArrayList<>();
        this.variantTypes = new ArrayList<>();
        this.hgvsDescriptions = new ArrayList<>();
        this.clinvarIds = new ArrayList<>();

        this.feature = new CivicFeature();
        this.molecularProfiles = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CivicVariant{");
        sb.append("variantId='").append(variantId).append('\'');
        sb.append(", variantCivicUrl='").append(variantCivicUrl).append('\'');
        sb.append(", variant='").append(variant).append('\'');
        sb.append(", variantAliases=").append(variantAliases);
        sb.append(", isFlagged=").append(isFlagged);
        sb.append(", variantGroups=").append(variantGroups);
        sb.append(", variantTypes=").append(variantTypes);
        sb.append(", lastReviewDate='").append(lastReviewDate).append('\'');
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", entrezId='").append(entrezId).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start='").append(start).append('\'');
        sb.append(", stop='").append(stop).append('\'');
        sb.append(", referenceBases='").append(referenceBases).append('\'');
        sb.append(", variantBases='").append(variantBases).append('\'');
        sb.append(", representativeTranscript='").append(representativeTranscript).append('\'');
        sb.append(", ensemblVersion='").append(ensemblVersion).append('\'');
        sb.append(", referenceBuild='").append(referenceBuild).append('\'');
        sb.append(", hgvsDescriptions=").append(hgvsDescriptions);
        sb.append(", alleleRegistryId='").append(alleleRegistryId).append('\'');
        sb.append(", clinvarIds=").append(clinvarIds);
        sb.append(", ncitId='").append(ncitId).append('\'');
        sb.append(", viccCompliantName='").append(viccCompliantName).append('\'');
        sb.append(", feature=").append(feature);
        sb.append(", molecularProfiles=").append(molecularProfiles);
        sb.append('}');
        return sb.toString();
    }

    public String getVariantId() {
        return variantId;
    }

    public CivicVariant setVariantId(String variantId) {
        this.variantId = variantId;
        return this;
    }

    public String getVariantCivicUrl() {
        return variantCivicUrl;
    }

    public CivicVariant setVariantCivicUrl(String variantCivicUrl) {
        this.variantCivicUrl = variantCivicUrl;
        return this;
    }

    public String getVariant() {
        return variant;
    }

    public CivicVariant setVariant(String variant) {
        this.variant = variant;
        return this;
    }

    public List<String> getVariantAliases() {
        return variantAliases;
    }

    public CivicVariant setVariantAliases(List<String> variantAliases) {
        this.variantAliases = variantAliases;
        return this;
    }

    public Boolean getFlagged() {
        return isFlagged;
    }

    public CivicVariant setFlagged(Boolean flagged) {
        isFlagged = flagged;
        return this;
    }

    public List<String> getVariantGroups() {
        return variantGroups;
    }

    public CivicVariant setVariantGroups(List<String> variantGroups) {
        this.variantGroups = variantGroups;
        return this;
    }

    public List<String> getVariantTypes() {
        return variantTypes;
    }

    public CivicVariant setVariantTypes(List<String> variantTypes) {
        this.variantTypes = variantTypes;
        return this;
    }

    public String getLastReviewDate() {
        return lastReviewDate;
    }

    public CivicVariant setLastReviewDate(String lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
        return this;
    }

    public String getGene() {
        return gene;
    }

    public CivicVariant setGene(String gene) {
        this.gene = gene;
        return this;
    }

    public String getEntrezId() {
        return entrezId;
    }

    public CivicVariant setEntrezId(String entrezId) {
        this.entrezId = entrezId;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public CivicVariant setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public String getStart() {
        return start;
    }

    public CivicVariant setStart(String start) {
        this.start = start;
        return this;
    }

    public String getStop() {
        return stop;
    }

    public CivicVariant setStop(String stop) {
        this.stop = stop;
        return this;
    }

    public String getReferenceBases() {
        return referenceBases;
    }

    public CivicVariant setReferenceBases(String referenceBases) {
        this.referenceBases = referenceBases;
        return this;
    }

    public String getVariantBases() {
        return variantBases;
    }

    public CivicVariant setVariantBases(String variantBases) {
        this.variantBases = variantBases;
        return this;
    }

    public String getRepresentativeTranscript() {
        return representativeTranscript;
    }

    public CivicVariant setRepresentativeTranscript(String representativeTranscript) {
        this.representativeTranscript = representativeTranscript;
        return this;
    }

    public String getEnsemblVersion() {
        return ensemblVersion;
    }

    public CivicVariant setEnsemblVersion(String ensemblVersion) {
        this.ensemblVersion = ensemblVersion;
        return this;
    }

    public String getReferenceBuild() {
        return referenceBuild;
    }

    public CivicVariant setReferenceBuild(String referenceBuild) {
        this.referenceBuild = referenceBuild;
        return this;
    }

    public List<String> getHgvsDescriptions() {
        return hgvsDescriptions;
    }

    public CivicVariant setHgvsDescriptions(List<String> hgvsDescriptions) {
        this.hgvsDescriptions = hgvsDescriptions;
        return this;
    }

    public String getAlleleRegistryId() {
        return alleleRegistryId;
    }

    public CivicVariant setAlleleRegistryId(String alleleRegistryId) {
        this.alleleRegistryId = alleleRegistryId;
        return this;
    }

    public List<String> getClinvarIds() {
        return clinvarIds;
    }

    public CivicVariant setClinvarIds(List<String> clinvarIds) {
        this.clinvarIds = clinvarIds;
        return this;
    }

    public String getNcitId() {
        return ncitId;
    }

    public CivicVariant setNcitId(String ncitId) {
        this.ncitId = ncitId;
        return this;
    }

    public String getViccCompliantName() {
        return viccCompliantName;
    }

    public CivicVariant setViccCompliantName(String viccCompliantName) {
        this.viccCompliantName = viccCompliantName;
        return this;
    }

    public CivicFeature getFeature() {
        return feature;
    }

    public CivicVariant setFeature(CivicFeature feature) {
        this.feature = feature;
        return this;
    }

    public List<CivicMolecularProfile> getMolecularProfiles() {
        return molecularProfiles;
    }

    public CivicVariant setMolecularProfiles(List<CivicMolecularProfile> molecularProfiles) {
        this.molecularProfiles = molecularProfiles;
        return this;
    }
}