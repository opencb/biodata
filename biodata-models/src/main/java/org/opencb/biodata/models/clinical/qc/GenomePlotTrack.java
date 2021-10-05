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

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class GenomePlotTrack {

    private String type;
    private String description;
    private Map<String, String> query;

    public GenomePlotTrack() {
    }

    public GenomePlotTrack(String type, String description, Map<String, String> query) {
        this.type = type;
        this.description = description;
        this.query = query;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenomePlotTrack{");
        sb.append("type='").append(type).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", query=").append(query);
        sb.append('}');
        return sb.toString();
    }

    public String getDescription() {
        return description;
    }

    public GenomePlotTrack setDescription(String description) {
        this.description = description;
        return this;
    }


    public String getType() {
        return type;
    }

    public GenomePlotTrack setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public GenomePlotTrack setQuery(Map<String, String> query) {
        this.query = query;
        return this;
    }
}
