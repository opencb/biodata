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

public class ClinicalVariantSummaryStats {

    private int numCases;
    private Map<String, Integer> variantStatusCounts;
    private Map<String, Integer> variantConfidenceCounts;
    private int numPrimaryInterpretations;
    private int numSecondaryInterpretations;
    private InterpretationSummaryStats interpretationSummaryStats;
    private Map<String, Integer> clinicalAnalysisDisorderCounts;

    public ClinicalVariantSummaryStats() {
        this.numCases = 0;
        this.variantStatusCounts = new HashMap<>();
        this.variantConfidenceCounts = new HashMap<>();
        this.numPrimaryInterpretations = 0;
        this.numSecondaryInterpretations = 0;
        this.interpretationSummaryStats = new InterpretationSummaryStats();
        this.clinicalAnalysisDisorderCounts = new HashMap<>();
    }

    public ClinicalVariantSummaryStats(int numCases, Map<String, Integer> variantStatusCounts, Map<String, Integer> variantConfidenceCounts,
                                       int numPrimaryInterpretations, int numSecondaryInterpretations,
                                       InterpretationSummaryStats interpretationSummaryStats,
                                       Map<String, Integer> clinicalAnalysisDisorderCounts) {
        this.numCases = numCases;
        this.variantStatusCounts = variantStatusCounts;
        this.variantConfidenceCounts = variantConfidenceCounts;
        this.numPrimaryInterpretations = numPrimaryInterpretations;
        this.numSecondaryInterpretations = numSecondaryInterpretations;
        this.interpretationSummaryStats = interpretationSummaryStats;
        this.clinicalAnalysisDisorderCounts = clinicalAnalysisDisorderCounts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantSummaryStats{");
        sb.append("numCases=").append(numCases);
        sb.append(", variantStatusCounts=").append(variantStatusCounts);
        sb.append(", variantConfidenceCounts=").append(variantConfidenceCounts);
        sb.append(", numPrimaryInterpretations=").append(numPrimaryInterpretations);
        sb.append(", numSecondaryInterpretations=").append(numSecondaryInterpretations);
        sb.append(", interpretationSummaryStats=").append(interpretationSummaryStats);
        sb.append(", clinicalAnalysisDisorderCounts=").append(clinicalAnalysisDisorderCounts);
        sb.append('}');
        return sb.toString();
    }

    public int getNumCases() {
        return numCases;
    }

    public ClinicalVariantSummaryStats setNumCases(int numCases) {
        this.numCases = numCases;
        return this;
    }

    public Map<String, Integer> getVariantStatusCounts() {
        return variantStatusCounts;
    }

    public ClinicalVariantSummaryStats setVariantStatusCounts(Map<String, Integer> variantStatusCounts) {
        this.variantStatusCounts = variantStatusCounts;
        return this;
    }

    public Map<String, Integer> getVariantConfidenceCounts() {
        return variantConfidenceCounts;
    }

    public ClinicalVariantSummaryStats setVariantConfidenceCounts(Map<String, Integer> variantConfidenceCounts) {
        this.variantConfidenceCounts = variantConfidenceCounts;
        return this;
    }

    public int getNumPrimaryInterpretations() {
        return numPrimaryInterpretations;
    }

    public ClinicalVariantSummaryStats setNumPrimaryInterpretations(int numPrimaryInterpretations) {
        this.numPrimaryInterpretations = numPrimaryInterpretations;
        return this;
    }

    public int getNumSecondaryInterpretations() {
        return numSecondaryInterpretations;
    }

    public ClinicalVariantSummaryStats setNumSecondaryInterpretations(int numSecondaryInterpretations) {
        this.numSecondaryInterpretations = numSecondaryInterpretations;
        return this;
    }

    public InterpretationSummaryStats getInterpretationSummaryStats() {
        return interpretationSummaryStats;
    }

    public ClinicalVariantSummaryStats setInterpretationSummaryStats(InterpretationSummaryStats interpretationSummaryStats) {
        this.interpretationSummaryStats = interpretationSummaryStats;
        return this;
    }

    public Map<String, Integer> getClinicalAnalysisDisorderCounts() {
        return clinicalAnalysisDisorderCounts;
    }

    public ClinicalVariantSummaryStats setClinicalAnalysisDisorderCounts(Map<String, Integer> clinicalAnalysisDisorderCounts) {
        this.clinicalAnalysisDisorderCounts = clinicalAnalysisDisorderCounts;
        return this;
    }
}
