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

public class MutationalSignature {

    private Signature signature;
    private SignatureFitting fitting;

    public MutationalSignature() {
    }

    public MutationalSignature(Signature signature, SignatureFitting signatureFitting) {
        this.signature = signature;
        this.fitting = signatureFitting;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MutationalSignature{");
        sb.append("signature=").append(signature);
        sb.append(", fitting=").append(fitting);
        sb.append('}');
        return sb.toString();
    }

    public Signature getSignature() {
        return signature;
    }

    public MutationalSignature setSignature(Signature signature) {
        this.signature = signature;
        return this;
    }

    public SignatureFitting getFitting() {
        return fitting;
    }

    public MutationalSignature setFitting(SignatureFitting signatureFitting) {
        this.fitting = signatureFitting;
        return this;
    }
}
