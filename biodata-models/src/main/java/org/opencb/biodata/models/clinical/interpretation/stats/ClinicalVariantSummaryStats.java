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

    private long numCases;
    private Map<String, Long> variantStatusCounts;
    private Map<String, Long> variantConfidenceCounts;
    private long numPrimaryInterpretations;
    private long numSecondaryInterpretations;
    private InterpretationSummaryStats interpretationSummaryStats;
    private Map<String, Long> clinicalAnalysisDisorderCounts;

    public ClinicalVariantSummaryStats() {
        this.numCases = 0;
        this.variantStatusCounts = new HashMap<>();
        this.variantConfidenceCounts = new HashMap<>();
        this.numPrimaryInterpretations = 0;
        this.numSecondaryInterpretations = 0;
        this.interpretationSummaryStats = new InterpretationSummaryStats();
        this.clinicalAnalysisDisorderCounts = new HashMap<>();
    }

    public ClinicalVariantSummaryStats(int numCases, Map<String, Long> variantStatusCounts, Map<String, Long> variantConfidenceCounts,
                                       int numPrimaryInterpretations, int numSecondaryInterpretations,
                                       InterpretationSummaryStats interpretationSummaryStats,
                                       Map<String, Long> clinicalAnalysisDisorderCounts) {
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

    public long getNumCases() {
        return numCases;
    }

    public ClinicalVariantSummaryStats setNumCases(long numCases) {
        this.numCases = numCases;
        return this;
    }

    public Map<String, Long> getVariantStatusCounts() {
        return variantStatusCounts;
    }

    public ClinicalVariantSummaryStats setVariantStatusCounts(Map<String, Long> variantStatusCounts) {
        this.variantStatusCounts = variantStatusCounts;
        return this;
    }

    public Map<String, Long> getVariantConfidenceCounts() {
        return variantConfidenceCounts;
    }

    public ClinicalVariantSummaryStats setVariantConfidenceCounts(Map<String, Long> variantConfidenceCounts) {
        this.variantConfidenceCounts = variantConfidenceCounts;
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

    public InterpretationSummaryStats getInterpretationSummaryStats() {
        return interpretationSummaryStats;
    }

    public ClinicalVariantSummaryStats setInterpretationSummaryStats(InterpretationSummaryStats interpretationSummaryStats) {
        this.interpretationSummaryStats = interpretationSummaryStats;
        return this;
    }

    public Map<String, Long> getClinicalAnalysisDisorderCounts() {
        return clinicalAnalysisDisorderCounts;
    }

    public ClinicalVariantSummaryStats setClinicalAnalysisDisorderCounts(Map<String, Long> clinicalAnalysisDisorderCounts) {
        this.clinicalAnalysisDisorderCounts = clinicalAnalysisDisorderCounts;
        return this;
    }
}
