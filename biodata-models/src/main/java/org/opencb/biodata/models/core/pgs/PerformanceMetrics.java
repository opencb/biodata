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

package org.opencb.biodata.models.core.pgs;

public class PerformanceMetrics {
    private String id;
    // Hazard ratio (HR)
    private String hazardRatio;
    // Odds ratio (OR)
    private String oddsRatio;
    private String beta;
    // Area Under the Receiver-Operating Characteristic Curve (AUROC)
    private String auroc;
    // Concordance Statistic (C-index)
    private String cIndex;
    private String otherMetrics;

    public PerformanceMetrics() {
    }

    public PerformanceMetrics(String id, String hazardRatio, String oddsRatio, String beta, String auroc, String cIndex,
                              String otherMetrics) {
        this.id = id;
        this.hazardRatio = hazardRatio;
        this.oddsRatio = oddsRatio;
        this.beta = beta;
        this.auroc = auroc;
        this.cIndex = cIndex;
        this.otherMetrics = otherMetrics;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerformanceMetrics{");
        sb.append("id='").append(id).append('\'');
        sb.append(", hazardRatio=").append(hazardRatio);
        sb.append(", oddsRatio=").append(oddsRatio);
        sb.append(", beta=").append(beta);
        sb.append(", auroc=").append(auroc);
        sb.append(", cIndex=").append(cIndex);
        sb.append(", otherMetrics='").append(otherMetrics).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PerformanceMetrics setId(String id) {
        this.id = id;
        return this;
    }

    public String getHazardRatio() {
        return hazardRatio;
    }

    public PerformanceMetrics setHazardRatio(String hazardRatio) {
        this.hazardRatio = hazardRatio;
        return this;
    }

    public String getOddsRatio() {
        return oddsRatio;
    }

    public PerformanceMetrics setOddsRatio(String oddsRatio) {
        this.oddsRatio = oddsRatio;
        return this;
    }

    public String getBeta() {
        return beta;
    }

    public PerformanceMetrics setBeta(String beta) {
        this.beta = beta;
        return this;
    }

    public String getAuroc() {
        return auroc;
    }

    public PerformanceMetrics setAuroc(String auroc) {
        this.auroc = auroc;
        return this;
    }

    public String getcIndex() {
        return cIndex;
    }

    public PerformanceMetrics setcIndex(String cIndex) {
        this.cIndex = cIndex;
        return this;
    }

    public String getOtherMetrics() {
        return otherMetrics;
    }

    public PerformanceMetrics setOtherMetrics(String otherMetrics) {
        this.otherMetrics = otherMetrics;
        return this;
    }
}
