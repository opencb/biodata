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

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;

import java.util.ArrayList;
import java.util.List;

public class RelatednessReport {

    public static final String HAPLOID_CALL_MODE_HAPLOID_VALUE = "haploid";
    public static final String HAPLOID_CALL_MODE_MISSING_VALUE = "missing";
    public static final String HAPLOID_CALL_MODE_REF_VALUE = "reference";
    public static final String HAPLOID_CALL_MODE_DEFAUT_VALUE = HAPLOID_CALL_MODE_HAPLOID_VALUE;

    // Method., e.g.: Plink/IBD
    @DataField(id = "method",
            description = FieldConstants.RELATEDNESS_REPORT_METHOD_DESCRIPTION)
    private String method;

    // Minor allele frequency to filter variants, e.g.: 1000G:EUR>0.35, cohort:ALL>0.05")
    @DataField(id = "maf",
            description = FieldConstants.RELATEDNESS_REPORT_MAF_DESCRIPTION)
    private String maf;

    // Haploid call mode, i.e., the PLINK/IBD parameter: vcf-half-call
    @DataField(id = "haploidCallMode",
            description = FieldConstants.RELATEDNESS_REPORT_HAPLOID_CALL_MODE_DESCRIPTION)
    private String haploidCallMode;

    // Relatedness scores for pair of samples
    @DataField(id = "scores", uncommentedClasses = {"RelatednessScore"},
            description = FieldConstants.RELATEDNESS_REPORT_SCORES_DESCRIPTION)
    private List<RelatednessScore> scores;

    @DataField(id = "files",
            description = FieldConstants.RELATEDNESS_REPORT_FILES_DESCRIPTION)
    private List<String> files;

    public RelatednessReport() {
        this("PLINK/IBD", "cohort:ALL>0.05", HAPLOID_CALL_MODE_DEFAUT_VALUE, new ArrayList<>(), new ArrayList<>());
    }

    public RelatednessReport(String method, String maf, String haploidCallMode, List<RelatednessScore> scores, List<String> files) {
        this.method = method;
        this.maf = maf;
        this.haploidCallMode = haploidCallMode;
        this.scores = scores;
        this.files = files;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RelatednessReport{");
        sb.append("method='").append(method).append('\'');
        sb.append(", maf='").append(maf).append('\'');
        sb.append(", haploidCallMode='").append(haploidCallMode).append('\'');
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

    public String getHaploidCallMode() {
        return haploidCallMode;
    }

    public RelatednessReport setHaploidCallMode(String haploidCallMode) {
        this.haploidCallMode = haploidCallMode;
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
