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
    private Map<String, Integer> evidenceGeneNameCounts;
    private Map<String, Integer> evidenceModeOfInheritanceCounts;
    private Map<String, Integer> evidencePanelCounts;
    private Map<String, Integer> evidenceReviewTierCounts;
    private Map<String, Integer> evidenceReviewAcmgCounts;
    private Map<String, Integer> evidenceReviewClinicalSignificanceCounts;
    private Map<String, Integer> evidenceClassificationAcmgCounts;

    public InterpretationSummaryStats() {
        this.evidenceTranscriptCounts = new HashMap<>();
        this.evidenceGeneNameCounts = new HashMap<>();
        this.evidenceModeOfInheritanceCounts = new HashMap<>();
        this.evidencePanelCounts = new HashMap<>();
        this.evidenceReviewTierCounts = new HashMap<>();
        this.evidenceReviewAcmgCounts = new HashMap<>();
        this.evidenceReviewClinicalSignificanceCounts = new HashMap<>();
        this.evidenceClassificationAcmgCounts = new HashMap<>();
    }

    public InterpretationSummaryStats(Map<String, Integer> evidenceTranscriptCounts, Map<String, Integer> evidenceGeneNameCounts,
                                      Map<String, Integer> evidenceModeOfInheritanceCounts, Map<String, Integer> evidencePanelCounts,
                                      Map<String, Integer> evidenceReviewTierCounts, Map<String, Integer> evidenceReviewAcmgCounts,
                                      Map<String, Integer> evidenceReviewClinicalSignificanceCounts,
                                      Map<String, Integer> evidenceClassificationAcmgCounts) {
        this.evidenceTranscriptCounts = evidenceTranscriptCounts;
        this.evidenceGeneNameCounts = evidenceGeneNameCounts;
        this.evidenceModeOfInheritanceCounts = evidenceModeOfInheritanceCounts;
        this.evidencePanelCounts = evidencePanelCounts;
        this.evidenceReviewTierCounts = evidenceReviewTierCounts;
        this.evidenceReviewAcmgCounts = evidenceReviewAcmgCounts;
        this.evidenceReviewClinicalSignificanceCounts = evidenceReviewClinicalSignificanceCounts;
        this.evidenceClassificationAcmgCounts = evidenceClassificationAcmgCounts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InterpretationSummaryStats{");
        sb.append("evidenceTranscriptCounts=").append(evidenceTranscriptCounts);
        sb.append(", evidenceGeneNameCounts=").append(evidenceGeneNameCounts);
        sb.append(", evidenceModeOfInheritanceCounts=").append(evidenceModeOfInheritanceCounts);
        sb.append(", evidencePanelCounts=").append(evidencePanelCounts);
        sb.append(", evidenceReviewTierCounts=").append(evidenceReviewTierCounts);
        sb.append(", evidenceReviewAcmgCounts=").append(evidenceReviewAcmgCounts);
        sb.append(", evidenceReviewClinicalSignificanceCounts=").append(evidenceReviewClinicalSignificanceCounts);
        sb.append(", evidenceClassificationAcmgCounts=").append(evidenceClassificationAcmgCounts);
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

    public Map<String, Integer> getEvidenceGeneNameCounts() {
        return evidenceGeneNameCounts;
    }

    public InterpretationSummaryStats setEvidenceGeneNameCounts(Map<String, Integer> evidenceGeneNameCounts) {
        this.evidenceGeneNameCounts = evidenceGeneNameCounts;
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

    public Map<String, Integer> getEvidenceClassificationAcmgCounts() {
        return evidenceClassificationAcmgCounts;
    }

    public InterpretationSummaryStats setEvidenceClassificationAcmgCounts(Map<String, Integer> evidenceClassificationAcmgCounts) {
        this.evidenceClassificationAcmgCounts = evidenceClassificationAcmgCounts;
        return this;
    }
}
