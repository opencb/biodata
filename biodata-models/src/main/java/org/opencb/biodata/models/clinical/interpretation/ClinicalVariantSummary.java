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

package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.clinical.ClinicalProperty;

import java.util.HashMap;
import java.util.Map;

public class ClinicalVariantSummary {

    private int numCases;
    private int numPrimaryInterpretations;
    private int numSecondaryInterpretations;
    private Map<String, Integer> evidencePhenotypeCounts;
    private Map<String, Integer> evidenceTierCounts;
    private Map<String, Integer> evidenceAcmgCounts;
    private Map<ClinicalProperty.ClinicalSignificance, Integer> evidenceClinicalSignificanceCounts;
    private Map<VariantClassification.DrugResponse, Integer> drugResponseCounts;
    private Map<VariantClassification.TraitAssociation, Integer> evidenceTraitAssociationCounts;
    private Map<VariantClassification.FunctionalEffect, Integer> evidenceFunctionalEffectCounts;
    private Map<VariantClassification.Tumorigenesis, Integer> evidenceTumorigenesisCounts;

    public ClinicalVariantSummary() {
        this.evidencePhenotypeCounts = new HashMap<>();
        this.evidenceTierCounts = new HashMap<>();
        this.evidenceAcmgCounts = new HashMap<>();
        this.evidenceClinicalSignificanceCounts = new HashMap<>();
        this.drugResponseCounts = new HashMap<>();
        this.evidenceTraitAssociationCounts = new HashMap<>();
        this.evidenceFunctionalEffectCounts = new HashMap<>();
        this.evidenceTumorigenesisCounts = new HashMap<>();
    }

    public ClinicalVariantSummary(int numCases, int numPrimaryInterpretations, int numSecondaryInterpretations,
                                  Map<String, Integer> evidencePhenotypeCounts,
                                  Map<String, Integer> evidenceTierCounts,
                                  Map<String, Integer> evidenceAcmgCounts,
                                  Map<ClinicalProperty.ClinicalSignificance, Integer> evidenceClinicalSignificanceCounts,
                                  Map<VariantClassification.DrugResponse, Integer> drugResponseCounts,
                                  Map<VariantClassification.TraitAssociation, Integer> evidenceTraitAssociationCounts,
                                  Map<VariantClassification.FunctionalEffect, Integer> evidenceFunctionalEffectCounts,
                                  Map<VariantClassification.Tumorigenesis, Integer> evidenceTumorigenesisCounts) {
        this.numCases = numCases;
        this.numPrimaryInterpretations = numPrimaryInterpretations;
        this.numSecondaryInterpretations = numSecondaryInterpretations;
        this.evidencePhenotypeCounts = evidencePhenotypeCounts;
        this.evidenceTierCounts = evidenceTierCounts;
        this.evidenceAcmgCounts = evidenceAcmgCounts;
        this.evidenceClinicalSignificanceCounts = evidenceClinicalSignificanceCounts;
        this.drugResponseCounts = drugResponseCounts;
        this.evidenceTraitAssociationCounts = evidenceTraitAssociationCounts;
        this.evidenceFunctionalEffectCounts = evidenceFunctionalEffectCounts;
        this.evidenceTumorigenesisCounts = evidenceTumorigenesisCounts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantSummary{");
        sb.append("numCases=").append(numCases);
        sb.append(", numPrimaryInterpretations=").append(numPrimaryInterpretations);
        sb.append(", numSecondaryInterpretations=").append(numSecondaryInterpretations);
        sb.append(", evidencePhenotypeCounts=").append(evidencePhenotypeCounts);
        sb.append(", evidenceTierCounts=").append(evidenceTierCounts);
        sb.append(", evidenceAcmgCounts=").append(evidenceAcmgCounts);
        sb.append(", evidenceClinicalSignificanceCounts=").append(evidenceClinicalSignificanceCounts);
        sb.append(", drugResponseCounts=").append(drugResponseCounts);
        sb.append(", evidenceTraitAssociationCounts=").append(evidenceTraitAssociationCounts);
        sb.append(", evidenceFunctionalEffectCounts=").append(evidenceFunctionalEffectCounts);
        sb.append(", evidenceTumorigenesisCounts=").append(evidenceTumorigenesisCounts);
        sb.append('}');
        return sb.toString();
    }

    public int getNumCases() {
        return numCases;
    }

    public ClinicalVariantSummary setNumCases(int numCases) {
        this.numCases = numCases;
        return this;
    }

    public int getNumPrimaryInterpretations() {
        return numPrimaryInterpretations;
    }

    public ClinicalVariantSummary setNumPrimaryInterpretations(int numPrimaryInterpretations) {
        this.numPrimaryInterpretations = numPrimaryInterpretations;
        return this;
    }

    public int getNumSecondaryInterpretations() {
        return numSecondaryInterpretations;
    }

    public ClinicalVariantSummary setNumSecondaryInterpretations(int numSecondaryInterpretations) {
        this.numSecondaryInterpretations = numSecondaryInterpretations;
        return this;
    }

    public Map<String, Integer> getEvidencePhenotypeCounts() {
        return evidencePhenotypeCounts;
    }

    public ClinicalVariantSummary setEvidencePhenotypeCounts(Map<String, Integer> evidencePhenotypeCounts) {
        this.evidencePhenotypeCounts = evidencePhenotypeCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceTierCounts() {
        return evidenceTierCounts;
    }

    public ClinicalVariantSummary setEvidenceTierCounts(Map<String, Integer> evidenceTierCounts) {
        this.evidenceTierCounts = evidenceTierCounts;
        return this;
    }

    public Map<String, Integer> getEvidenceAcmgCounts() {
        return evidenceAcmgCounts;
    }

    public ClinicalVariantSummary setEvidenceAcmgCounts(Map<String, Integer> evidenceAcmgCounts) {
        this.evidenceAcmgCounts = evidenceAcmgCounts;
        return this;
    }

    public Map<ClinicalProperty.ClinicalSignificance, Integer> getEvidenceClinicalSignificanceCounts() {
        return evidenceClinicalSignificanceCounts;
    }

    public ClinicalVariantSummary setEvidenceClinicalSignificanceCounts(Map<ClinicalProperty.ClinicalSignificance, Integer> evidenceClinicalSignificanceCounts) {
        this.evidenceClinicalSignificanceCounts = evidenceClinicalSignificanceCounts;
        return this;
    }

    public Map<VariantClassification.DrugResponse, Integer> getDrugResponseCounts() {
        return drugResponseCounts;
    }

    public ClinicalVariantSummary setDrugResponseCounts(Map<VariantClassification.DrugResponse, Integer> drugResponseCounts) {
        this.drugResponseCounts = drugResponseCounts;
        return this;
    }

    public Map<VariantClassification.TraitAssociation, Integer> getEvidenceTraitAssociationCounts() {
        return evidenceTraitAssociationCounts;
    }

    public ClinicalVariantSummary setEvidenceTraitAssociationCounts(Map<VariantClassification.TraitAssociation, Integer> evidenceTraitAssociationCounts) {
        this.evidenceTraitAssociationCounts = evidenceTraitAssociationCounts;
        return this;
    }

    public Map<VariantClassification.FunctionalEffect, Integer> getEvidenceFunctionalEffectCounts() {
        return evidenceFunctionalEffectCounts;
    }

    public ClinicalVariantSummary setEvidenceFunctionalEffectCounts(Map<VariantClassification.FunctionalEffect, Integer> evidenceFunctionalEffectCounts) {
        this.evidenceFunctionalEffectCounts = evidenceFunctionalEffectCounts;
        return this;
    }

    public Map<VariantClassification.Tumorigenesis, Integer> getEvidenceTumorigenesisCounts() {
        return evidenceTumorigenesisCounts;
    }

    public ClinicalVariantSummary setEvidenceTumorigenesisCounts(Map<VariantClassification.Tumorigenesis, Integer> evidenceTumorigenesisCounts) {
        this.evidenceTumorigenesisCounts = evidenceTumorigenesisCounts;
        return this;
    }
}
