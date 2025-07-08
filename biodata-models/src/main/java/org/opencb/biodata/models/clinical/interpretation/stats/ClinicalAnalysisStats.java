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

public class ClinicalAnalysisStats {

    private Map<String, Long> disorders;
    private Map<String, Long> probandPhenotypes;
    private Map<String, Long> probandDisorders;

    public ClinicalAnalysisStats() {
        this.disorders = new LinkedHashMap<>();
        this.probandPhenotypes = new LinkedHashMap<>();
        this.probandDisorders = new LinkedHashMap<>();
    }

    public ClinicalAnalysisStats(Map<String, Long> disorders, Map<String, Long> probandPhenotypes, Map<String, Long> probandDisorders) {
        this.disorders = disorders;
        this.probandPhenotypes = probandPhenotypes;
        this.probandDisorders = probandDisorders;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalAnalysisStats{");
        sb.append("disorders=").append(disorders);
        sb.append(", probandPhenotypes=").append(probandPhenotypes);
        sb.append(", probandDisorders=").append(probandDisorders);
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Long> getDisorders() {
        return disorders;
    }

    public ClinicalAnalysisStats setDisorders(Map<String, Long> disorders) {
        this.disorders = disorders;
        return this;
    }

    public Map<String, Long> getProbandPhenotypes() {
        return probandPhenotypes;
    }

    public ClinicalAnalysisStats setProbandPhenotypes(Map<String, Long> probandPhenotypes) {
        this.probandPhenotypes = probandPhenotypes;
        return this;
    }

    public Map<String, Long> getProbandDisorders() {
        return probandDisorders;
    }

    public ClinicalAnalysisStats setProbandDisorders(Map<String, Long> probandDisorders) {
        this.probandDisorders = probandDisorders;
        return this;
    }
}
