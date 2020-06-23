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

import org.opencb.biodata.models.variant.metadata.SampleVariantStats;

import java.util.Map;

public class SampleQcVariantStats {

    private String id;
    private String description;
    private Map<String, String> query;
    private SampleVariantStats stats;

    @Deprecated
    private String sampleId;
    @Deprecated
    private Signature signature;
    @Deprecated
    private QcVariantStats qcVariantStats;

    public SampleQcVariantStats() {
    }

    @Deprecated
    public SampleQcVariantStats(String id, String sampleId, String description, Map<String, String> query, Signature signature,
                                QcVariantStats qcVariantStats) {
        this.id = id;
        this.sampleId = sampleId;
        this.description = description;
        this.query = query;
        this.signature = signature;
        this.qcVariantStats = qcVariantStats;
    }

    public SampleQcVariantStats(String id, String description, Map<String, String> query, SampleVariantStats stats) {
        this.id = id;
        this.description = description;
        this.query = query;
        this.stats = stats;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampleQcVariantStats{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", query=").append(query);
        sb.append(", stats=").append(stats);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public SampleQcVariantStats setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SampleQcVariantStats setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public SampleQcVariantStats setQuery(Map<String, String> query) {
        this.query = query;
        return this;
    }

    public SampleVariantStats getStats() {
        return stats;
    }

    public SampleQcVariantStats setStats(SampleVariantStats stats) {
        this.stats = stats;
        return this;
    }
}
