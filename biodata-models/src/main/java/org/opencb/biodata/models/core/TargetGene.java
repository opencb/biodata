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


package org.opencb.biodata.models.core;

public class TargetGene {
    /**
     * Experiment information, e.g. Immunohistochemistry//Luciferase reporter assay//qRT-PCR//Western blot
     */
    private String experiment;
    /**
     * support type, e.g. Functional MTI
     */
    private String evidence; // support type
    /**
     * Pubmed ID
     */
    private String pubmed;

    public TargetGene() {

    }


    public TargetGene(String experiment, String evidence, String pubmed) {
        this.experiment = experiment;
        this.evidence = evidence;
        this.pubmed = pubmed;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TargetGene{");
        sb.append("experiment='").append(experiment).append('\'');
        sb.append(", evidence='").append(evidence).append('\'');
        sb.append(", pubmed='").append(pubmed).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getExperiment() {
        return experiment;
    }

    public TargetGene setExperiment(String experiment) {
        this.experiment = experiment;
        return this;
    }

    public String getEvidence() {
        return evidence;
    }

    public TargetGene setEvidence(String evidence) {
        this.evidence = evidence;
        return this;
    }

    public String getPubmed() {
        return pubmed;
    }

    public TargetGene setPubmed(String pubmed) {
        this.pubmed = pubmed;
        return this;
    }
}
