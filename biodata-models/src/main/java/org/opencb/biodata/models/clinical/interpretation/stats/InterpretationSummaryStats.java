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

import java.util.HashMap;
import java.util.Map;

public class InterpretationSummaryStats {

    private Map<String, Integer> evidenceTranscriptCounts;
    private Map<String, Integer> evidenceModeOfInheritanceCounts;
    private Map<String, Integer> evidencePanelCounts;
    private Map<String, Integer> evidencePhenotypeCounts;
    private Map<String, Integer> evidenceReviewTierCounts;
    private Map<String, Integer> evidenceReviewAcmgCounts;
    private Map<String, Integer> evidenceReviewClinicalSignificanceCounts;
    private Map<String, Integer> evidenceDrugResponseCounts;
    private Map<String, Integer> evidenceTraitAssociationCounts;
    private Map<String, Integer> evidenceFunctionalEffectCounts;
    private Map<String, Integer> evidenceTumorigenesisCounts;

    public InterpretationSummaryStats() {
        this.evidenceTranscriptCounts = new HashMap<>();
        this.evidenceModeOfInheritanceCounts = new HashMap<>();
        this.evidencePanelCounts = new HashMap<>();
        this.evidencePhenotypeCounts = new HashMap<>();
        this.evidenceReviewTierCounts = new HashMap<>();
        this.evidenceReviewAcmgCounts = new HashMap<>();
        this.evidenceReviewClinicalSignificanceCounts = new HashMap<>();
        this.evidenceDrugResponseCounts = new HashMap<>();
        this.evidenceTraitAssociationCounts = new HashMap<>();
        this.evidenceFunctionalEffectCounts = new HashMap<>();
        this.evidenceTumorigenesisCounts = new HashMap<>();
    }

    public InterpretationSummaryStats(Map<String, Integer> evidenceTranscriptCounts, Map<String, Integer> evidenceModeOfInheritanceCounts,
                                      Map<String, Integer> evidencePanelCounts, Map<String, Integer> evidencePhenotypeCounts,
                                      Map<String, Integer> evidenceReviewTierCounts, Map<String, Integer> evidenceReviewAcmgCounts,
                                      Map<String, Integer> evidenceReviewClinicalSignificanceCounts,
                                      Map<String, Integer> evidenceDrugResponseCounts, Map<String, Integer> evidenceTraitAssociationCounts,
                                      Map<String, Integer> evidenceFunctionalEffectCounts,
                                      Map<String, Integer> evidenceTumorigenesisCounts) {
        this.evidenceTranscriptCounts = evidenceTranscriptCounts;
        this.evidenceModeOfInheritanceCounts = evidenceModeOfInheritanceCounts;
        this.evidencePanelCounts = evidencePanelCounts;
        this.evidencePhenotypeCounts = evidencePhenotypeCounts;
        this.evidenceReviewTierCounts = evidenceReviewTierCounts;
        this.evidenceReviewAcmgCounts = evidenceReviewAcmgCounts;
        this.evidenceReviewClinicalSignificanceCounts = evidenceReviewClinicalSignificanceCounts;
        this.evidenceDrugResponseCounts = evidenceDrugResponseCounts;
        this.evidenceTraitAssociationCounts = evidenceTraitAssociationCounts;
        this.evidenceFunctionalEffectCounts = evidenceFunctionalEffectCounts;
        this.evidenceTumorigenesisCounts = evidenceTumorigenesisCounts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InterpretationSummaryStats{");
        sb.append("evidenceTranscriptCounts=").append(evidenceTranscriptCounts);
        sb.append(", evidenceModeOfInheritanceCounts=").append(evidenceModeOfInheritanceCounts);
        sb.append(", evidencePanelCounts=").append(evidencePanelCounts);
        sb.append(", evidencePhenotypeCounts=").append(evidencePhenotypeCounts);
        sb.append(", evidenceReviewTierCounts=").append(evidenceReviewTierCounts);
        sb.append(", evidenceReviewAcmgCounts=").append(evidenceReviewAcmgCounts);
        sb.append(", evidenceReviewClinicalSignificanceCounts=").append(evidenceReviewClinicalSignificanceCounts);
        sb.append(", evidenceDrugResponseCounts=").append(evidenceDrugResponseCounts);
        sb.append(", evidenceTraitAssociationCounts=").append(evidenceTraitAssociationCounts);
        sb.append(", evidenceFunctionalEffectCounts=").append(evidenceFunctionalEffectCounts);
        sb.append(", evidenceTumorigenesisCounts=").append(evidenceTumorigenesisCounts);
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Integer> getEvidenceTranscriptCounts() {
        return evidenceTranscriptCounts;
    }

    public InterpretationSummaryStats setEvidenceTranscriptCounts(Map<String, Integer> evidenceTranscriptCounts) {
        this.evidenceTranscriptCounts = evidenceTranscriptCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceModeOfInheritanceCounts() {
        return evidenceModeOfInheritanceCounts;
    }

    public InterpretationSummaryStats setEvidenceModeOfInheritanceCounts(Map<String, Integer> evidenceModeOfInheritanceCounts) {
        this.evidenceModeOfInheritanceCounts = evidenceModeOfInheritanceCounts;
        return this;
    }

    public Map<String, Integer> getEvidencePanelCounts() {
        return evidencePanelCounts;
    }

    public InterpretationSummaryStats setEvidencePanelCounts(Map<String, Integer> evidencePanelCounts) {
        this.evidencePanelCounts = evidencePanelCounts;
        return this;
    }

    public Map<String, Integer> getEvidencePhenotypeCounts() {
        return evidencePhenotypeCounts;
    }

    public InterpretationSummaryStats setEvidencePhenotypeCounts(Map<String, Integer> evidencePhenotypeCounts) {
        this.evidencePhenotypeCounts = evidencePhenotypeCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceReviewTierCounts() {
        return evidenceReviewTierCounts;
    }

    public InterpretationSummaryStats setEvidenceReviewTierCounts(Map<String, Integer> evidenceReviewTierCounts) {
        this.evidenceReviewTierCounts = evidenceReviewTierCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceReviewAcmgCounts() {
        return evidenceReviewAcmgCounts;
    }

    public InterpretationSummaryStats setEvidenceReviewAcmgCounts(Map<String, Integer> evidenceReviewAcmgCounts) {
        this.evidenceReviewAcmgCounts = evidenceReviewAcmgCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceReviewClinicalSignificanceCounts() {
        return evidenceReviewClinicalSignificanceCounts;
    }

    public InterpretationSummaryStats setEvidenceReviewClinicalSignificanceCounts(Map<String, Integer> evidenceReviewClinicalSignificanceCounts) {
        this.evidenceReviewClinicalSignificanceCounts = evidenceReviewClinicalSignificanceCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceDrugResponseCounts() {
        return evidenceDrugResponseCounts;
    }

    public InterpretationSummaryStats setEvidenceDrugResponseCounts(Map<String, Integer> evidenceDrugResponseCounts) {
        this.evidenceDrugResponseCounts = evidenceDrugResponseCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceTraitAssociationCounts() {
        return evidenceTraitAssociationCounts;
    }

    public InterpretationSummaryStats setEvidenceTraitAssociationCounts(Map<String, Integer> evidenceTraitAssociationCounts) {
        this.evidenceTraitAssociationCounts = evidenceTraitAssociationCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceFunctionalEffectCounts() {
        return evidenceFunctionalEffectCounts;
    }

    public InterpretationSummaryStats setEvidenceFunctionalEffectCounts(Map<String, Integer> evidenceFunctionalEffectCounts) {
        this.evidenceFunctionalEffectCounts = evidenceFunctionalEffectCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceTumorigenesisCounts() {
        return evidenceTumorigenesisCounts;
    }

    public InterpretationSummaryStats setEvidenceTumorigenesisCounts(Map<String, Integer> evidenceTumorigenesisCounts) {
        this.evidenceTumorigenesisCounts = evidenceTumorigenesisCounts;
        return this;
    }
}
