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

import java.util.Map;

/**
 * Created by imedina on 03/07/16.
 */
public class OntologyTermAnnotation {

    protected String id;
    protected String name;
    protected String description;
    protected String source;
    protected String url;

    protected Map<String, String> attributes;

    public OntologyTermAnnotation() {
    }

    public OntologyTermAnnotation(String id, String name, String description, String source, String url, Map<String, String> attributes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.source = source;
        this.url = url;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OntologyTermAnnotation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public OntologyTermAnnotation setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OntologyTermAnnotation setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OntologyTermAnnotation setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSource() {
        return source;
    }

    public OntologyTermAnnotation setSource(String source) {
        this.source = source;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public OntologyTermAnnotation setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public OntologyTermAnnotation setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }
}
