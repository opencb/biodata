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

package org.opencb.biodata.models.clinical.qc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatednessReport {

    // Method., e.g.: Plink/IBD
    private String method;

    // Minor allele frequency to filter variants, e.g.: 1kg_phase3:CEU>0.35, cohort:ALL>0.05")
    private String maf;

    // Relatedness scores for pair of samples
    private List<RelatednessScore> scores;
    private List<String> files;

    public RelatednessReport() {
        this("PLINK/IBD", "cohort:ALL>0.05", new ArrayList<>(), new ArrayList<>());
    }

    public RelatednessReport(String method, String maf, List<RelatednessScore> scores, List<String> files) {
        this.method = method;
        this.maf = maf;
        this.scores = scores;
        this.files = files;
    }

    public static class RelatednessScore {
        // Pair of samples
        private String sampleId1;
        private String sampleId2;

        // Reported relation according to pedigree
        private String inferredRelationship;

        private Map<String, Object> values;

        public RelatednessScore() {
             this("", "", "", new LinkedHashMap<>());
        }

        public RelatednessScore(String sampleId1, String sampleId2, String inferredRelationship, Map<String, Object> values) {
            this.sampleId1 = sampleId1;
            this.sampleId2 = sampleId2;
            this.inferredRelationship = inferredRelationship;
            this.values = values;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RelatednessScore{");
            sb.append("sampleId1='").append(sampleId1).append('\'');
            sb.append(", sampleId2='").append(sampleId2).append('\'');
            sb.append(", inferredRelationship='").append(inferredRelationship).append('\'');
            sb.append(", values=").append(values);
            sb.append('}');
            return sb.toString();
        }

        public String getSampleId1() {
            return sampleId1;
        }

        public RelatednessScore setSampleId1(String sampleId1) {
            this.sampleId1 = sampleId1;
            return this;
        }

        public String getSampleId2() {
            return sampleId2;
        }

        public RelatednessScore setSampleId2(String sampleId2) {
            this.sampleId2 = sampleId2;
            return this;
        }

        public String getInferredRelationship() {
            return inferredRelationship;
        }

        public RelatednessScore setInferredRelationship(String inferredRelationship) {
            this.inferredRelationship = inferredRelationship;
            return this;
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public RelatednessScore setValues(Map<String, Object> values) {
            this.values = values;
            return this;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RelatednessReport{");
        sb.append("method='").append(method).append('\'');
        sb.append(", maf='").append(maf).append('\'');
        sb.append(", scores=").append(scores);
        sb.append(", files=").append(files);
        sb.append('}');
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public RelatednessReport setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getMaf() {
        return maf;
    }

    public RelatednessReport setMaf(String maf) {
        this.maf = maf;
        return this;
    }

    public List<RelatednessScore> getScores() {
        return scores;
    }

    public RelatednessReport setScores(List<RelatednessScore> scores) {
        this.scores = scores;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public RelatednessReport setFiles(List<String> files) {
        this.files = files;
        return this;
    }
}
