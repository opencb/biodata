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

package org.opencb.biodata.models.clinical;

import org.opencb.biodata.models.core.OntologyTermAnnotation;

import java.util.List;
import java.util.Map;

public class Disorder extends OntologyTermAnnotation {

    private String description;
    private List<Phenotype> evidences;

    public Disorder() {
    }

    @Deprecated
    public Disorder(String id, String name, String source, String description, List<Phenotype> evidences, Map<String, String> attributes) {
        super(id, name, source, attributes);
        this.description = description;
        this.evidences = evidences;
    }

    public Disorder(String id, String name, String source, Map<String, String> attributes, String description, List<Phenotype> evidences) {
        super(id, name, source, attributes);
        this.description = description;
        this.evidences = evidences;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Disorder{");
        sb.append("description='").append(description).append('\'');
        sb.append(", evidences=").append(evidences);
        sb.append(", id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Disorder setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public Disorder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Disorder setSource(String source) {
        this.source = source;
        return this;
    }

    @Override
    public Disorder setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Disorder setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<Phenotype> getEvidences() {
        return evidences;
    }

    public Disorder setEvidences(List<Phenotype> evidences) {
        this.evidences = evidences;
        return this;
    }
}
