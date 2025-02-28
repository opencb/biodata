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

import java.util.LinkedHashMap;
import java.util.Map;

public class ClinicalVariantEvidenceStats {

    private Map<String, Long> transcripts;
    private Map<String, Long> genes;
    private Map<String, Long> soTerms;
    private Map<String, Long> mois;
    private Map<String, Long> panels;
    private Map<String, Long> reviewTiers;
    private Map<String, Long> reviewAcmgs;
    private Map<String, Long> reviewClinicalSignificances;
    private Map<String, Long> acmgs;

    public ClinicalVariantEvidenceStats() {
        this.transcripts = new LinkedHashMap<>();
        this.genes = new LinkedHashMap<>();
        this.soTerms = new LinkedHashMap<>();
        this.mois = new LinkedHashMap<>();
        this.panels = new LinkedHashMap<>();
        this.reviewTiers = new LinkedHashMap<>();
        this.reviewAcmgs = new LinkedHashMap<>();
        this.reviewClinicalSignificances = new LinkedHashMap<>();
        this.acmgs = new LinkedHashMap<>();
    }

    public ClinicalVariantEvidenceStats(Map<String, Long> transcripts, Map<String, Long> genes, Map<String, Long> soTerms,
                                        Map<String, Long> mois, Map<String, Long> panels, Map<String, Long> reviewTiers,
                                        Map<String, Long> reviewAcmgs, Map<String, Long> reviewClinicalSignificances,
                                        Map<String, Long> acmgs) {
        this.transcripts = transcripts;
        this.genes = genes;
        this.soTerms = soTerms;
        this.mois = mois;
        this.panels = panels;
        this.reviewTiers = reviewTiers;
        this.reviewAcmgs = reviewAcmgs;
        this.reviewClinicalSignificances = reviewClinicalSignificances;
        this.acmgs = acmgs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantEvidenceStats{");
        sb.append("transcripts=").append(transcripts);
        sb.append(", genes=").append(genes);
        sb.append(", soTerms=").append(soTerms);
        sb.append(", mois=").append(mois);
        sb.append(", panels=").append(panels);
        sb.append(", reviewTiers=").append(reviewTiers);
        sb.append(", reviewAcmgs=").append(reviewAcmgs);
        sb.append(", reviewClinicalSignificances=").append(reviewClinicalSignificances);
        sb.append(", acmgs=").append(acmgs);
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Long> getTranscripts() {
        return transcripts;
    }

    public ClinicalVariantEvidenceStats setTranscripts(Map<String, Long> transcripts) {
        this.transcripts = transcripts;
        return this;
    }

    public Map<String, Long> getGenes() {
        return genes;
    }

    public ClinicalVariantEvidenceStats setGenes(Map<String, Long> genes) {
        this.genes = genes;
        return this;
    }

    public Map<String, Long> getSoTerms() {
        return soTerms;
    }

    public ClinicalVariantEvidenceStats setSoTerms(Map<String, Long> soTerms) {
        this.soTerms = soTerms;
        return this;
    }

    public Map<String, Long> getMois() {
        return mois;
    }

    public ClinicalVariantEvidenceStats setMois(Map<String, Long> mois) {
        this.mois = mois;
        return this;
    }

    public Map<String, Long> getPanels() {
        return panels;
    }

    public ClinicalVariantEvidenceStats setPanels(Map<String, Long> panels) {
        this.panels = panels;
        return this;
    }

    public Map<String, Long> getReviewTiers() {
        return reviewTiers;
    }

    public ClinicalVariantEvidenceStats setReviewTiers(Map<String, Long> reviewTiers) {
        this.reviewTiers = reviewTiers;
        return this;
    }

    public Map<String, Long> getReviewAcmgs() {
        return reviewAcmgs;
    }

    public ClinicalVariantEvidenceStats setReviewAcmgs(Map<String, Long> reviewAcmgs) {
        this.reviewAcmgs = reviewAcmgs;
        return this;
    }

    public Map<String, Long> getReviewClinicalSignificances() {
        return reviewClinicalSignificances;
    }

    public ClinicalVariantEvidenceStats setReviewClinicalSignificances(Map<String, Long> reviewClinicalSignificances) {
        this.reviewClinicalSignificances = reviewClinicalSignificances;
        return this;
    }

    public Map<String, Long> getAcmgs() {
        return acmgs;
    }

    public ClinicalVariantEvidenceStats setAcmgs(Map<String, Long> acmgs) {
        this.acmgs = acmgs;
        return this;
    }
}
