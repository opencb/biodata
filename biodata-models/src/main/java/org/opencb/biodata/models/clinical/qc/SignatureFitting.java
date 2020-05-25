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

import java.util.Arrays;

public class SignatureFitting {

    private String method;
    private String signatureSource;
    private String signatureVersion;
    private Score[] scores;
    private double coeff;
    private String image;

    public SignatureFitting() {
    }

    public SignatureFitting(String method, String signatureSource, String signatureVersion, Score[] scores,
                            double coeff, String image) {
        this.method = method;
        this.signatureSource = signatureSource;
        this.signatureVersion = signatureVersion;
        this.scores = scores;
        this.coeff = coeff;
        this.image = image;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SignatureFitting{");
        sb.append("method='").append(method).append('\'');
        sb.append(", signatureSource='").append(signatureSource).append('\'');
        sb.append(", signatureVersion='").append(signatureVersion).append('\'');
        sb.append(", scores=").append(Arrays.toString(scores));
        sb.append(", coeff=").append(coeff);
        sb.append(", image='").append(image).append('\'');
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

    public Score[] getScores() {
        return scores;
    }

    public SignatureFitting setScores(Score[] scores) {
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

    public String getImage() {
        return image;
    }

    public SignatureFitting setImage(String image) {
        this.image = image;
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
