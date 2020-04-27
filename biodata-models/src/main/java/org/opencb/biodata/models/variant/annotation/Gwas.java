/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.variant.annotation;

import java.util.List;

/**
 * Created by antonior on 21/11/14.
 */
@Deprecated
public class Gwas {
    private  String snpIdCurrent;
    private List<String> traits;
    private Double riskAlleleFrequency;
    private  String reportedGenes;

    Gwas() { }

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

    public Double getRiskAlleleFrequency() {
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
