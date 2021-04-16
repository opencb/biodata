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

import java.util.Map;

public class CircosPlot {

    private String id;
    private Map<String, String> query;

    private String b64Image;

    public CircosPlot() {
    }

    public CircosPlot(String id, Map<String, String> query, String b64Image) {
        this.id = id;
        this.query = query;
        this.b64Image = b64Image;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CircosPlot{");
        sb.append("id='").append(id).append('\'');
        sb.append(", query=").append(query);
        sb.append(", b64Image='").append(b64Image).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public CircosPlot setId(String id) {
        this.id = id;
        return this;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public CircosPlot setQuery(Map<String, String> query) {
        this.query = query;
        return this;
    }

    public String getB64Image() {
        return b64Image;
    }

    public CircosPlot setB64Image(String b64Image) {
        this.b64Image = b64Image;
        return this;
    }
}
