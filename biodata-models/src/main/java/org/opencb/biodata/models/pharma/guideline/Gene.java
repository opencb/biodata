package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class Gene {
    private String objCls;
    private String id;
    private String symbol;
    private String alleleFile;
    private String name;
    private AltName altNames;
    private boolean amp;
    private String buildVersion;
    private String cbStart;
    private String cbStop;
    private BasicObject chr;
    private int chrStartPosB37;
    private int chrStartPosB38;
    private int chrStopPosB37;
    private int chrStopPosB38;
    private boolean cpicGene;
    private List<CrossReference> crossReferences;
    private boolean hasNonStandardHaplotypes;
    private boolean hideHaplotypes;
    private boolean pharmVarGene;
    private String strand;
    private List<Term> terms;
    private float version;

    public String getObjCls() {
        return objCls;
    }

    public Gene setObjCls(String objCls) {
        this.objCls = objCls;
        return this;
    }

    public String getId() {
        return id;
    }

    public Gene setId(String id) {
        this.id = id;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Gene setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getAlleleFile() {
        return alleleFile;
    }

    public Gene setAlleleFile(String alleleFile) {
        this.alleleFile = alleleFile;
        return this;
    }

    public String getName() {
        return name;
    }

    public Gene setName(String name) {
        this.name = name;
        return this;
    }

    public AltName getAltNames() {
        return altNames;
    }

    public Gene setAltNames(AltName altNames) {
        this.altNames = altNames;
        return this;
    }

    public boolean isAmp() {
        return amp;
    }

    public Gene setAmp(boolean amp) {
        this.amp = amp;
        return this;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public Gene setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
        return this;
    }

    public String getCbStart() {
        return cbStart;
    }

    public Gene setCbStart(String cbStart) {
        this.cbStart = cbStart;
        return this;
    }

    public String getCbStop() {
        return cbStop;
    }

    public Gene setCbStop(String cbStop) {
        this.cbStop = cbStop;
        return this;
    }

    public BasicObject getChr() {
        return chr;
    }

    public Gene setChr(BasicObject chr) {
        this.chr = chr;
        return this;
    }

    public int getChrStartPosB37() {
        return chrStartPosB37;
    }

    public Gene setChrStartPosB37(int chrStartPosB37) {
        this.chrStartPosB37 = chrStartPosB37;
        return this;
    }

    public int getChrStartPosB38() {
        return chrStartPosB38;
    }

    public Gene setChrStartPosB38(int chrStartPosB38) {
        this.chrStartPosB38 = chrStartPosB38;
        return this;
    }

    public int getChrStopPosB37() {
        return chrStopPosB37;
    }

    public Gene setChrStopPosB37(int chrStopPosB37) {
        this.chrStopPosB37 = chrStopPosB37;
        return this;
    }

    public int getChrStopPosB38() {
        return chrStopPosB38;
    }

    public Gene setChrStopPosB38(int chrStopPosB38) {
        this.chrStopPosB38 = chrStopPosB38;
        return this;
    }

    public boolean isCpicGene() {
        return cpicGene;
    }

    public Gene setCpicGene(boolean cpicGene) {
        this.cpicGene = cpicGene;
        return this;
    }

    public List<CrossReference> getCrossReferences() {
        return crossReferences;
    }

    public Gene setCrossReferences(List<CrossReference> crossReferences) {
        this.crossReferences = crossReferences;
        return this;
    }

    public boolean isHasNonStandardHaplotypes() {
        return hasNonStandardHaplotypes;
    }

    public Gene setHasNonStandardHaplotypes(boolean hasNonStandardHaplotypes) {
        this.hasNonStandardHaplotypes = hasNonStandardHaplotypes;
        return this;
    }

    public boolean isHideHaplotypes() {
        return hideHaplotypes;
    }

    public Gene setHideHaplotypes(boolean hideHaplotypes) {
        this.hideHaplotypes = hideHaplotypes;
        return this;
    }

    public boolean isPharmVarGene() {
        return pharmVarGene;
    }

    public Gene setPharmVarGene(boolean pharmVarGene) {
        this.pharmVarGene = pharmVarGene;
        return this;
    }

    public String getStrand() {
        return strand;
    }

    public Gene setStrand(String strand) {
        this.strand = strand;
        return this;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public Gene setTerms(List<Term> terms) {
        this.terms = terms;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public Gene setVersion(float version) {
        this.version = version;
        return this;
    }
}
