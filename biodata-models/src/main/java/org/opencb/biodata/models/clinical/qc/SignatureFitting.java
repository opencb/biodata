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
import org.opencb.commons.datastore.core.ObjectMap;

import java.util.List;

public class SignatureFitting {


    @DataField(id = "method", indexed = true,
            description = FieldConstants.SIGNATURE_FITTING_METHOD_DESCRIPTION)
    private String method;

    @DataField(id = "signatureSource", indexed = true,
            description = FieldConstants.SIGNATURE_FITTING_SOURCE_DESCRIPTION)
    private String signatureSource;

    @DataField(id = "signatureVersion", indexed = true,
            description = FieldConstants.SIGNATURE_FITTING_SIGNATURE_VERSION_DESCRIPTION)
    private String signatureVersion;

    @DataField(id = "scores", indexed = true, uncommentedClasses = {"Score"},
            description = FieldConstants.SIGNATURE_FITTING_SCORES_DESCRIPTION)
    private List<Score> scores;

    @Deprecated
    @DataField(id = "coeff", indexed = true,
            description = FieldConstants.SIGNATURE_FITTING_COEFF_DESCRIPTION)
    private double coeff;

    @Deprecated
    @DataField(id = "file", indexed = true,
            description = FieldConstants.SIGNATURE_FITTING_FILE_DESCRIPTION)
    private String file;

    @DataField(id = "files", indexed = true,
            description = FieldConstants.SIGNATURE_FITTING_FILE_DESCRIPTION)
    private List<String> files;

    @DataField(id = "params", indexed = false,
            description = FieldConstants.SIGNATURE_FITTING_PARAMS_DESCRIPTION)
    private ObjectMap params;

    public SignatureFitting() {
    }

    @Deprecated
    public SignatureFitting(String method, String signatureSource, String signatureVersion, List<Score> scores, double coeff, String file) {
        this.method = method;
        this.signatureSource = signatureSource;
        this.signatureVersion = signatureVersion;
        this.scores = scores;
        this.coeff = coeff;
        this.file = file;
    }

    public SignatureFitting(String method, String signatureSource, String signatureVersion, List<Score> scores, double coeff, String file,
                            List<String> files, ObjectMap params) {
        this.method = method;
        this.signatureSource = signatureSource;
        this.signatureVersion = signatureVersion;
        this.scores = scores;
        this.coeff = coeff;
        this.file = file;
        this.files = files;
        this.params = params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SignatureFitting{");
        sb.append("method='").append(method).append('\'');
        sb.append(", signatureSource='").append(signatureSource).append('\'');
        sb.append(", signatureVersion='").append(signatureVersion).append('\'');
        sb.append(", scores=").append(scores);
        sb.append(", coeff=").append(coeff);
        sb.append(", file='").append(file).append('\'');
        sb.append(", files=").append(files);
        sb.append(", params=").append(params);
        sb.append('}');
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public SignatureFitting setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getSignatureSource() {
        return signatureSource;
    }

    public SignatureFitting setSignatureSource(String signatureSource) {
        this.signatureSource = signatureSource;
        return this;
    }

    public String getSignatureVersion() {
        return signatureVersion;
    }

    public SignatureFitting setSignatureVersion(String signatureVersion) {
        this.signatureVersion = signatureVersion;
        return this;
    }

    public List<Score> getScores() {
        return scores;
    }

    public SignatureFitting setScores(List<Score> scores) {
        this.scores = scores;
        return this;
    }

    public double getCoeff() {
        return coeff;
    }

    public SignatureFitting setCoeff(double coeff) {
        this.coeff = coeff;
        return this;
    }

    public String getFile() {
        return file;
    }

    public SignatureFitting setFile(String file) {
        this.file = file;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public SignatureFitting setFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public ObjectMap getParams() {
        return params;
    }

    public SignatureFitting setParams(ObjectMap params) {
        this.params = params;
        return this;
    }

    public static class Score {

        private String signatureId;
        private double value;

        public Score() {
        }

        public Score(String signatureId, double value) {
            this.signatureId = signatureId;
            this.value = value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Score{");
            sb.append("signatureId='").append(signatureId).append('\'');
            sb.append(", value=").append(value);
            sb.append('}');
            return sb.toString();
        }

        public String getSignatureId() {
            return signatureId;
        }

        public Score setSignatureId(String signatureId) {
            this.signatureId = signatureId;
            return this;
        }

        public double getValue() {
            return value;
        }

        public Score setValue(double value) {
            this.value = value;
            return this;
        }
    }
}
