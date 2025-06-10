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

import org.opencb.commons.datastore.core.ObjectMap;

import java.util.Map;


public class ImprintedGene {

    private String geneName;
    private String status;
    private String expressedAllele;
    private Map<String, Object> attributes;
    private String source;

    public ImprintedGene() {
        this.attributes = new ObjectMap();
    }

    public ImprintedGene(String geneName, String status, String expressedAllele, Map<String, Object> attributes, String source) {
        this.geneName = geneName;
        this.status = status;
        this.expressedAllele = expressedAllele;
        this.attributes = attributes;
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImprintedGene{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", expressedAllele='").append(expressedAllele).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getGeneName() {
        return geneName;
    }

    public ImprintedGene setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ImprintedGene setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getExpressedAllele() {
        return expressedAllele;
    }

    public ImprintedGene setExpressedAllele(String expressedAllele) {
        this.expressedAllele = expressedAllele;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ImprintedGene setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String getSource() {
        return source;
    }

    public ImprintedGene setSource(String source) {
        this.source = source;
        return this;
    }
}
