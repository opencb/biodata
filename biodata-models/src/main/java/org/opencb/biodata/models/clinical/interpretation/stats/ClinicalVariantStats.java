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

public class ClinicalVariantStats {

    private Map<String, Long> status;
    private Map<String, Long> confidences;

    public ClinicalVariantStats() {
        this.status = new HashMap<>();
        this.confidences = new HashMap<>();
    }

    public ClinicalVariantStats(Map<String, Long> status, Map<String, Long> confidences) {
        this.status = status;
        this.confidences = confidences;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantStats{");
        sb.append("status=").append(status);
        sb.append(", confidences=").append(confidences);
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Long> getStatus() {
        return status;
    }

    public ClinicalVariantStats setStatus(Map<String, Long> status) {
        this.status = status;
        return this;
    }

    public Map<String, Long> getConfidences() {
        return confidences;
    }

    public ClinicalVariantStats setConfidences(Map<String, Long> confidences) {
        this.confidences = confidences;
        return this;
    }
}
