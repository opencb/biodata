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

package org.opencb.biodata.models.clinical.interpretation.stats;

public class ClinicalVariantSummaryStats {

    private String id;
    private String variantId;
    private long numClinicalAnalyses;
    private long numPrimaryInterpretations;
    private long numSecondaryInterpretations;

    private ClinicalAnalysisStats clinicalAnalysis;
    private InterpretationStats interpretation;
    private ClinicalVariantStats variant;
    private ClinicalVariantEvidenceStats evidence;

    public ClinicalVariantSummaryStats() {
        this.numClinicalAnalyses = 0L;
        this.numPrimaryInterpretations = 0L;
        this.numSecondaryInterpretations = 0L;
        this.clinicalAnalysis = new ClinicalAnalysisStats();
        this.interpretation = new InterpretationStats();
        this.variant = new ClinicalVariantStats();
        this.evidence = new ClinicalVariantEvidenceStats();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantSummaryStats{");
        sb.append("id='").append(id).append('\'');
        sb.append(", variantId='").append(variantId).append('\'');
        sb.append(", numClinicalAnalyses=").append(numClinicalAnalyses);
        sb.append(", numPrimaryInterpretations=").append(numPrimaryInterpretations);
        sb.append(", numSecondaryInterpretations=").append(numSecondaryInterpretations);
        sb.append(", clinicalAnalysis=").append(clinicalAnalysis);
        sb.append(", interpretation=").append(interpretation);
        sb.append(", variant=").append(variant);
        sb.append(", evidence=").append(evidence);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public ClinicalVariantSummaryStats setId(String id) {
        this.id = id;
        return this;
    }

    public String getVariantId() {
        return variantId;
    }

    public ClinicalVariantSummaryStats setVariantId(String variantId) {
        this.variantId = variantId;
        return this;
    }

    public long getNumClinicalAnalyses() {
        return numClinicalAnalyses;
    }

    public ClinicalVariantSummaryStats setNumClinicalAnalyses(long numClinicalAnalyses) {
        this.numClinicalAnalyses = numClinicalAnalyses;
        return this;
    }

    public long getNumPrimaryInterpretations() {
        return numPrimaryInterpretations;
    }

    public ClinicalVariantSummaryStats setNumPrimaryInterpretations(long numPrimaryInterpretations) {
        this.numPrimaryInterpretations = numPrimaryInterpretations;
        return this;
    }

    public long getNumSecondaryInterpretations() {
        return numSecondaryInterpretations;
    }

    public ClinicalVariantSummaryStats setNumSecondaryInterpretations(long numSecondaryInterpretations) {
        this.numSecondaryInterpretations = numSecondaryInterpretations;
        return this;
    }

    public ClinicalAnalysisStats getClinicalAnalysis() {
        return clinicalAnalysis;
    }

    public ClinicalVariantSummaryStats setClinicalAnalysis(ClinicalAnalysisStats clinicalAnalysis) {
        this.clinicalAnalysis = clinicalAnalysis;
        return this;
    }

    public InterpretationStats getInterpretation() {
        return interpretation;
    }

    public ClinicalVariantSummaryStats setInterpretation(InterpretationStats interpretation) {
        this.interpretation = interpretation;
        return this;
    }

    public ClinicalVariantStats getVariant() {
        return variant;
    }

    public ClinicalVariantSummaryStats setVariant(ClinicalVariantStats variant) {
        this.variant = variant;
        return this;
    }

    public ClinicalVariantEvidenceStats getEvidence() {
        return evidence;
    }

    public ClinicalVariantSummaryStats setEvidence(ClinicalVariantEvidenceStats evidence) {
        this.evidence = evidence;
        return this;
    }
}
