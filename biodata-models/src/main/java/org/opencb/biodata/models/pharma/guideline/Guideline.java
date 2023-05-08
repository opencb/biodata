package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class Guideline {
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

    public Guideline setObjCls(String objCls) {
        this.objCls = objCls;
        return this;
    }

    public String getId() {
        return id;
    }

    public Guideline setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Guideline setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isAlternateDrugAvailable() {
        return alternateDrugAvailable;
    }

    public Guideline setAlternateDrugAvailable(boolean alternateDrugAvailable) {
        this.alternateDrugAvailable = alternateDrugAvailable;
        return this;
    }

    public boolean isCancerGenome() {
        return cancerGenome;
    }

    public Guideline setCancerGenome(boolean cancerGenome) {
        this.cancerGenome = cancerGenome;
        return this;
    }

    public List<CrossReference> getCrossReferences() {
        return crossReferences;
    }

    public Guideline setCrossReferences(List<CrossReference> crossReferences) {
        this.crossReferences = crossReferences;
        return this;
    }

    public String getDescriptiveVideoId() {
        return descriptiveVideoId;
    }

    public Guideline setDescriptiveVideoId(String descriptiveVideoId) {
        this.descriptiveVideoId = descriptiveVideoId;
        return this;
    }

    public boolean isDosingInformation() {
        return dosingInformation;
    }

    public Guideline setDosingInformation(boolean dosingInformation) {
        this.dosingInformation = dosingInformation;
        return this;
    }

    public List<GuidelineGene> getGuidelineGenes() {
        return guidelineGenes;
    }

    public Guideline setGuidelineGenes(List<GuidelineGene> guidelineGenes) {
        this.guidelineGenes = guidelineGenes;
        return this;
    }

    public boolean isHasTestingInfo() {
        return hasTestingInfo;
    }

    public Guideline setHasTestingInfo(boolean hasTestingInfo) {
        this.hasTestingInfo = hasTestingInfo;
        return this;
    }

    public List<History> getHistory() {
        return history;
    }

    public Guideline setHistory(List<History> history) {
        this.history = history;
        return this;
    }

    public List<Literature> getLiterature() {
        return literature;
    }

    public Guideline setLiterature(List<Literature> literature) {
        this.literature = literature;
        return this;
    }

    public boolean isPediatric() {
        return pediatric;
    }

    public Guideline setPediatric(boolean pediatric) {
        this.pediatric = pediatric;
        return this;
    }

    public PediatricMarkdown getPediatricMarkdown() {
        return pediatricMarkdown;
    }

    public Guideline setPediatricMarkdown(PediatricMarkdown pediatricMarkdown) {
        this.pediatricMarkdown = pediatricMarkdown;
        return this;
    }

    public boolean isRecommendation() {
        return recommendation;
    }

    public Guideline setRecommendation(boolean recommendation) {
        this.recommendation = recommendation;
        return this;
    }

    public List<BasicObject> getRelatedAlleles() {
        return relatedAlleles;
    }

    public Guideline setRelatedAlleles(List<BasicObject> relatedAlleles) {
        this.relatedAlleles = relatedAlleles;
        return this;
    }

    public List<BasicObject> getRelatedChemicals() {
        return relatedChemicals;
    }

    public Guideline setRelatedChemicals(List<BasicObject> relatedChemicals) {
        this.relatedChemicals = relatedChemicals;
        return this;
    }

    public List<BasicObject> getRelatedGenes() {
        return relatedGenes;
    }

    public Guideline setRelatedGenes(List<BasicObject> relatedGenes) {
        this.relatedGenes = relatedGenes;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Guideline setSource(String source) {
        this.source = source;
        return this;
    }

    public SummaryMarkdown getSummaryMarkdown() {
        return summaryMarkdown;
    }

    public Guideline setSummaryMarkdown(SummaryMarkdown summaryMarkdown) {
        this.summaryMarkdown = summaryMarkdown;
        return this;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public Guideline setTerms(List<Term> terms) {
        this.terms = terms;
        return this;
    }

    public TextMarkdown getTextMarkdown() {
        return textMarkdown;
    }

    public Guideline setTextMarkdown(TextMarkdown textMarkdown) {
        this.textMarkdown = textMarkdown;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Guideline setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public Guideline setVersion(float version) {
        this.version = version;
        return this;
    }
}