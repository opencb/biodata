package org.opencb.biodata.models.variant.annotation;

import java.util.List;

/**
 * Created by antonior on 21/11/14.
 */
public class Gwas {
    private  String snpIdCurrent;
    private List<String> traits;
    private Double riskAlleleFrequency;
    private  String reportedGenes;

    public Gwas(String snpIdCurrent, List<String> traits, Double riskAlleleFrequency, String reportedGenes) {
        this.snpIdCurrent = snpIdCurrent;
        this.traits = traits;
        this.riskAlleleFrequency = riskAlleleFrequency;
        this.reportedGenes = reportedGenes;
    }

    public String getSnpIdCurrent() {
        return snpIdCurrent;
    }

    public void setSnpIdCurrent(String snpIdCurrent) {
        this.snpIdCurrent = snpIdCurrent;
    }

    public List<String> getTraits() {
        return traits;
    }


    public void setTraits(List<String> traits) {
        this.traits = traits;
    }

    public double getRiskAlleleFrequency() {
        return riskAlleleFrequency;
    }

    public void setRiskAlleleFrequency(Double riskAlleleFrequency) {
        this.riskAlleleFrequency = riskAlleleFrequency;
    }

    public String getReportedGenes() {
        return reportedGenes;
    }

    public void setReportedGenes(String reportedGenes) {
        this.reportedGenes = reportedGenes;
    }
}
