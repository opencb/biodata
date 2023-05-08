package org.opencb.biodata.models.pharma;

public class PharmaLabelAnnotation {
    private String name;
    private String source;
    private String biomarkerFlag;
    private String testingLevel;
    private String prescribingInfo;
    private String dosingInfo;
    private String alternateDrug;
    private String cancerGenome;

    public PharmaLabelAnnotation() {
    }

    public PharmaLabelAnnotation(String name, String source, String biomarkerFlag, String testingLevel, String prescribingInfo,
                                 String dosingInfo, String alternateDrug, String cancerGenome) {
        this.name = name;
        this.source = source;
        this.biomarkerFlag = biomarkerFlag;
        this.testingLevel = testingLevel;
        this.prescribingInfo = prescribingInfo;
        this.dosingInfo = dosingInfo;
        this.alternateDrug = alternateDrug;
        this.cancerGenome = cancerGenome;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaLabelAnnotation{");
        sb.append("name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", biomarkerFlag='").append(biomarkerFlag).append('\'');
        sb.append(", testingLevel='").append(testingLevel).append('\'');
        sb.append(", prescribingInfo='").append(prescribingInfo).append('\'');
        sb.append(", dosingInfo='").append(dosingInfo).append('\'');
        sb.append(", alternateDrug='").append(alternateDrug).append('\'');
        sb.append(", cancerGenome='").append(cancerGenome).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public PharmaLabelAnnotation setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public PharmaLabelAnnotation setSource(String source) {
        this.source = source;
        return this;
    }

    public String getBiomarkerFlag() {
        return biomarkerFlag;
    }

    public PharmaLabelAnnotation setBiomarkerFlag(String biomarkerFlag) {
        this.biomarkerFlag = biomarkerFlag;
        return this;
    }

    public String getTestingLevel() {
        return testingLevel;
    }

    public PharmaLabelAnnotation setTestingLevel(String testingLevel) {
        this.testingLevel = testingLevel;
        return this;
    }

    public String getPrescribingInfo() {
        return prescribingInfo;
    }

    public PharmaLabelAnnotation setPrescribingInfo(String prescribingInfo) {
        this.prescribingInfo = prescribingInfo;
        return this;
    }

    public String getDosingInfo() {
        return dosingInfo;
    }

    public PharmaLabelAnnotation setDosingInfo(String dosingInfo) {
        this.dosingInfo = dosingInfo;
        return this;
    }

    public String getAlternateDrug() {
        return alternateDrug;
    }

    public PharmaLabelAnnotation setAlternateDrug(String alternateDrug) {
        this.alternateDrug = alternateDrug;
        return this;
    }

    public String getCancerGenome() {
        return cancerGenome;
    }

    public PharmaLabelAnnotation setCancerGenome(String cancerGenome) {
        this.cancerGenome = cancerGenome;
        return this;
    }
}
