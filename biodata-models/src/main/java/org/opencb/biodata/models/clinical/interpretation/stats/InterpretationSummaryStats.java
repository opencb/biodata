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

    private Map<String, Long> evidenceTranscriptCounts;
    private Map<String, Long> evidenceGeneNameCounts;
    private Map<String, Long> evidenceModeOfInheritanceCounts;
    private Map<String, Long> evidencePanelCounts;
    private Map<String, Long> evidenceReviewTierCounts;
    private Map<String, Long> evidenceReviewAcmgCounts;
    private Map<String, Long> evidenceReviewClinicalSignificanceCounts;
    private Map<String, Long> evidenceClassificationAcmgCounts;

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

    public InterpretationSummaryStats(Map<String, Long> evidenceTranscriptCounts, Map<String, Long> evidenceGeneNameCounts,
                                      Map<String, Long> evidenceModeOfInheritanceCounts, Map<String, Long> evidencePanelCounts,
                                      Map<String, Long> evidenceReviewTierCounts, Map<String, Long> evidenceReviewAcmgCounts,
                                      Map<String, Long> evidenceReviewClinicalSignificanceCounts,
                                      Map<String, Long> evidenceClassificationAcmgCounts) {
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

    public Map<String, Long> getEvidenceTranscriptCounts() {
        return evidenceTranscriptCounts;
    }

    public InterpretationSummaryStats setEvidenceTranscriptCounts(Map<String, Long> evidenceTranscriptCounts) {
        this.evidenceTranscriptCounts = evidenceTranscriptCounts;
        return this;
    }

    public Map<String, Long> getEvidenceGeneNameCounts() {
        return evidenceGeneNameCounts;
    }

    public InterpretationSummaryStats setEvidenceGeneNameCounts(Map<String, Long> evidenceGeneNameCounts) {
        this.evidenceGeneNameCounts = evidenceGeneNameCounts;
        return this;
    }

    public Map<String, Long> getEvidenceModeOfInheritanceCounts() {
        return evidenceModeOfInheritanceCounts;
    }

    public InterpretationSummaryStats setEvidenceModeOfInheritanceCounts(Map<String, Long> evidenceModeOfInheritanceCounts) {
        this.evidenceModeOfInheritanceCounts = evidenceModeOfInheritanceCounts;
        return this;
    }

    public Map<String, Long> getEvidencePanelCounts() {
        return evidencePanelCounts;
    }

    public InterpretationSummaryStats setEvidencePanelCounts(Map<String, Long> evidencePanelCounts) {
        this.evidencePanelCounts = evidencePanelCounts;
        return this;
    }

    public Map<String, Long> getEvidenceReviewTierCounts() {
        return evidenceReviewTierCounts;
    }

    public InterpretationSummaryStats setEvidenceReviewTierCounts(Map<String, Long> evidenceReviewTierCounts) {
        this.evidenceReviewTierCounts = evidenceReviewTierCounts;
        return this;
    }

    public Map<String, Long> getEvidenceReviewAcmgCounts() {
        return evidenceReviewAcmgCounts;
    }

    public InterpretationSummaryStats setEvidenceReviewAcmgCounts(Map<String, Long> evidenceReviewAcmgCounts) {
        this.evidenceReviewAcmgCounts = evidenceReviewAcmgCounts;
        return this;
    }

    public Map<String, Long> getEvidenceReviewClinicalSignificanceCounts() {
        return evidenceReviewClinicalSignificanceCounts;
    }

    public InterpretationSummaryStats setEvidenceReviewClinicalSignificanceCounts(Map<String, Long> evidenceReviewClinicalSignificanceCounts) {
        this.evidenceReviewClinicalSignificanceCounts = evidenceReviewClinicalSignificanceCounts;
        return this;
    }

    public Map<String, Long> getEvidenceClassificationAcmgCounts() {
        return evidenceClassificationAcmgCounts;
    }

    public InterpretationSummaryStats setEvidenceClassificationAcmgCounts(Map<String, Long> evidenceClassificationAcmgCounts) {
        this.evidenceClassificationAcmgCounts = evidenceClassificationAcmgCounts;
        return this;
    }
}
