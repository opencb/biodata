package org.opencb.biodata.models.pharma;

import java.util.HashMap;
import java.util.Map;

public class PharmaDrugLabelAnnotation {
    private String name;
    private String source;
    private String biomarkerFlag;
    private String testingLevel;
    private String prescribingInfo;
    private String dosingInfo;
    private String alternateDrug;
    private String cancerGenome;

    private Map<String, Object> attributes;

    public PharmaDrugLabelAnnotation() {
        this.attributes = new HashMap<>();
    }

    public PharmaDrugLabelAnnotation(String name, String source, String biomarkerFlag, String testingLevel, String prescribingInfo,
                                     String dosingInfo, String alternateDrug, String cancerGenome, Map<String, Object> attributes) {
        this.name = name;
        this.source = source;
        this.biomarkerFlag = biomarkerFlag;
        this.testingLevel = testingLevel;
        this.prescribingInfo = prescribingInfo;
        this.dosingInfo = dosingInfo;
        this.alternateDrug = alternateDrug;
        this.cancerGenome = cancerGenome;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaDrugLabelAnnotation{");
        sb.append("name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", biomarkerFlag='").append(biomarkerFlag).append('\'');
        sb.append(", testingLevel='").append(testingLevel).append('\'');
        sb.append(", prescribingInfo='").append(prescribingInfo).append('\'');
        sb.append(", dosingInfo='").append(dosingInfo).append('\'');
        sb.append(", alternateDrug='").append(alternateDrug).append('\'');
        sb.append(", cancerGenome='").append(cancerGenome).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public PharmaDrugLabelAnnotation setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public PharmaDrugLabelAnnotation setSource(String source) {
        this.source = source;
        return this;
    }

    public String getBiomarkerFlag() {
        return biomarkerFlag;
    }

    public PharmaDrugLabelAnnotation setBiomarkerFlag(String biomarkerFlag) {
        this.biomarkerFlag = biomarkerFlag;
        return this;
    }

    public String getTestingLevel() {
        return testingLevel;
    }

    public PharmaDrugLabelAnnotation setTestingLevel(String testingLevel) {
        this.testingLevel = testingLevel;
        return this;
    }

    public String getPrescribingInfo() {
        return prescribingInfo;
    }

    public PharmaDrugLabelAnnotation setPrescribingInfo(String prescribingInfo) {
        this.prescribingInfo = prescribingInfo;
        return this;
    }

    public String getDosingInfo() {
        return dosingInfo;
    }

    public PharmaDrugLabelAnnotation setDosingInfo(String dosingInfo) {
        this.dosingInfo = dosingInfo;
        return this;
    }

    public String getAlternateDrug() {
        return alternateDrug;
    }

    public PharmaDrugLabelAnnotation setAlternateDrug(String alternateDrug) {
        this.alternateDrug = alternateDrug;
        return this;
    }

    public String getCancerGenome() {
        return cancerGenome;
    }

    public PharmaDrugLabelAnnotation setCancerGenome(String cancerGenome) {
        this.cancerGenome = cancerGenome;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaDrugLabelAnnotation setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
