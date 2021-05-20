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

public class GenomePlot {

    private String id;
    private String description;
    private GenomePlotConfig config;
    private String file;

    public GenomePlot() {
    }

    public GenomePlot(String id, String description, GenomePlotConfig config, String file) {
        this.id = id;
        this.description = description;
        this.config = config;
        this.file = file;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenomePlot{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", config=").append(config);
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public GenomePlot setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GenomePlot setDescription(String description) {
        this.description = description;
        return this;
    }

    public GenomePlotConfig getConfig() {
        return config;
    }

    public GenomePlot setConfig(GenomePlotConfig config) {
        this.config = config;
        return this;
    }

    public String getFile() {
        return file;
    }

    public GenomePlot setFile(String file) {
        this.file = file;
        return this;
    }
}
