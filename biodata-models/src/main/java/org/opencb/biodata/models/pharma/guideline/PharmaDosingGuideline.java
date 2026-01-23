package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class PharmaDosingGuideline {
    private String objCls;
    private String id;
    private String name;
    private boolean alternateDrugAvailable;
    private boolean cancerGenome;
    private List<CrossReference> crossReferences;
    private String descriptiveVideoId;
    private boolean dosingInformation;
    private List<GuidelineGene> guidelineGenes;
    private boolean hasTestingInfo;
    private List<History> history;
    private List<Literature> literature;
    private boolean otherPrescribingGuidance;
    private boolean pediatric;
    private PediatricMarkdown pediatricMarkdown;
    private boolean recommendation;
    private List<BasicObject> relatedAlleles;
    private List<BasicObject> relatedChemicals;
    private List<BasicObject> relatedGenes;
    private String source;
    private SummaryMarkdown summaryMarkdown;
    private List<Term> terms;
    private TextMarkdown textMarkdown;
    private String userId;
    private float version;

    public String getObjCls() {
        return objCls;
    }

    public PharmaDosingGuideline setObjCls(String objCls) {
        this.objCls = objCls;
        return this;
    }

    public String getId() {
        return id;
    }

    public PharmaDosingGuideline setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PharmaDosingGuideline setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isAlternateDrugAvailable() {
        return alternateDrugAvailable;
    }

    public PharmaDosingGuideline setAlternateDrugAvailable(boolean alternateDrugAvailable) {
        this.alternateDrugAvailable = alternateDrugAvailable;
        return this;
    }

    public boolean isCancerGenome() {
        return cancerGenome;
    }

    public PharmaDosingGuideline setCancerGenome(boolean cancerGenome) {
        this.cancerGenome = cancerGenome;
        return this;
    }

    public List<CrossReference> getCrossReferences() {
        return crossReferences;
    }

    public PharmaDosingGuideline setCrossReferences(List<CrossReference> crossReferences) {
        this.crossReferences = crossReferences;
        return this;
    }

    public String getDescriptiveVideoId() {
        return descriptiveVideoId;
    }

    public PharmaDosingGuideline setDescriptiveVideoId(String descriptiveVideoId) {
        this.descriptiveVideoId = descriptiveVideoId;
        return this;
    }

    public boolean isDosingInformation() {
        return dosingInformation;
    }

    public PharmaDosingGuideline setDosingInformation(boolean dosingInformation) {
        this.dosingInformation = dosingInformation;
        return this;
    }

    public List<GuidelineGene> getGuidelineGenes() {
        return guidelineGenes;
    }

    public PharmaDosingGuideline setGuidelineGenes(List<GuidelineGene> guidelineGenes) {
        this.guidelineGenes = guidelineGenes;
        return this;
    }

    public boolean isHasTestingInfo() {
        return hasTestingInfo;
    }

    public PharmaDosingGuideline setHasTestingInfo(boolean hasTestingInfo) {
        this.hasTestingInfo = hasTestingInfo;
        return this;
    }

    public List<History> getHistory() {
        return history;
    }

    public PharmaDosingGuideline setHistory(List<History> history) {
        this.history = history;
        return this;
    }

    public List<Literature> getLiterature() {
        return literature;
    }

    public PharmaDosingGuideline setLiterature(List<Literature> literature) {
        this.literature = literature;
        return this;
    }

    public boolean isOtherPrescribingGuidance() {
        return otherPrescribingGuidance;
    }

    public PharmaDosingGuideline setOtherPrescribingGuidance(boolean otherPrescribingGuidance) {
        this.otherPrescribingGuidance = otherPrescribingGuidance;
        return this;
    }

    public boolean isPediatric() {
        return pediatric;
    }

    public PharmaDosingGuideline setPediatric(boolean pediatric) {
        this.pediatric = pediatric;
        return this;
    }

    public PediatricMarkdown getPediatricMarkdown() {
        return pediatricMarkdown;
    }

    public PharmaDosingGuideline setPediatricMarkdown(PediatricMarkdown pediatricMarkdown) {
        this.pediatricMarkdown = pediatricMarkdown;
        return this;
    }

    public boolean isRecommendation() {
        return recommendation;
    }

    public PharmaDosingGuideline setRecommendation(boolean recommendation) {
        this.recommendation = recommendation;
        return this;
    }

    public List<BasicObject> getRelatedAlleles() {
        return relatedAlleles;
    }

    public PharmaDosingGuideline setRelatedAlleles(List<BasicObject> relatedAlleles) {
        this.relatedAlleles = relatedAlleles;
        return this;
    }

    public List<BasicObject> getRelatedChemicals() {
        return relatedChemicals;
    }

    public PharmaDosingGuideline setRelatedChemicals(List<BasicObject> relatedChemicals) {
        this.relatedChemicals = relatedChemicals;
        return this;
    }

    public List<BasicObject> getRelatedGenes() {
        return relatedGenes;
    }

    public PharmaDosingGuideline setRelatedGenes(List<BasicObject> relatedGenes) {
        this.relatedGenes = relatedGenes;
        return this;
    }

    public String getSource() {
        return source;
    }

    public PharmaDosingGuideline setSource(String source) {
        this.source = source;
        return this;
    }

    public SummaryMarkdown getSummaryMarkdown() {
        return summaryMarkdown;
    }

    public PharmaDosingGuideline setSummaryMarkdown(SummaryMarkdown summaryMarkdown) {
        this.summaryMarkdown = summaryMarkdown;
        return this;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public PharmaDosingGuideline setTerms(List<Term> terms) {
        this.terms = terms;
        return this;
    }

    public TextMarkdown getTextMarkdown() {
        return textMarkdown;
    }

    public PharmaDosingGuideline setTextMarkdown(TextMarkdown textMarkdown) {
        this.textMarkdown = textMarkdown;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public PharmaDosingGuideline setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public PharmaDosingGuideline setVersion(float version) {
        this.version = version;
        return this;
    }
}