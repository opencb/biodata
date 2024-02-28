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

    private int numCases;
    private int numPrimaryInterpretations;
    private int numSecondaryInterpretations;
    private InterpretationSummaryStats primaryInterpretationSummary;
    private InterpretationSummaryStats secondaryInterpretationsSummary;

    public ClinicalVariantSummaryStats() {
        primaryInterpretationSummary = new InterpretationSummaryStats();
        secondaryInterpretationsSummary = new InterpretationSummaryStats();
    }

    public ClinicalVariantSummaryStats(int numCases, int numPrimaryInterpretations, int numSecondaryInterpretations,
                                       InterpretationSummaryStats primaryInterpretationSummary, InterpretationSummaryStats secondaryInterpretationsSummary) {
        this.numCases = numCases;
        this.numPrimaryInterpretations = numPrimaryInterpretations;
        this.numSecondaryInterpretations = numSecondaryInterpretations;
        this.primaryInterpretationSummary = primaryInterpretationSummary;
        this.secondaryInterpretationsSummary = secondaryInterpretationsSummary;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantSummary{");
        sb.append("numCases=").append(numCases);
        sb.append(", numPrimaryInterpretations=").append(numPrimaryInterpretations);
        sb.append(", numSecondaryInterpretations=").append(numSecondaryInterpretations);
        sb.append(", primaryInterpretationSummary=").append(primaryInterpretationSummary);
        sb.append(", secondaryInterpretationsSummary=").append(secondaryInterpretationsSummary);
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

    public InterpretationSummaryStats getPrimaryInterpretationSummary() {
        return primaryInterpretationSummary;
    }

    public ClinicalVariantSummaryStats setPrimaryInterpretationSummary(InterpretationSummaryStats primaryInterpretationSummary) {
        this.primaryInterpretationSummary = primaryInterpretationSummary;
        return this;
    }

    public InterpretationSummaryStats getSecondaryInterpretationsSummary() {
        return secondaryInterpretationsSummary;
    }

    public ClinicalVariantSummaryStats setSecondaryInterpretationsSummary(InterpretationSummaryStats secondaryInterpretationsSummary) {
        this.secondaryInterpretationsSummary = secondaryInterpretationsSummary;
        return this;
    }
}
