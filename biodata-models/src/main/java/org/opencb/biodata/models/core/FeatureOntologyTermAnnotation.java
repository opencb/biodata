/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.core;

import java.util.List;
import java.util.Map;


public class FeatureOntologyTermAnnotation extends OntologyTermAnnotation {

    private List<String> evidenceCodes;
    private List<String> publications;
    private String qualifier;

    public FeatureOntologyTermAnnotation() {
    }

    public FeatureOntologyTermAnnotation(String id, String name, String source, Map<String, String> attributes,
                                         List<String> evidenceCodes, List<String> publications, String qualifier) {
        super(id, name, source, attributes);

        this.evidenceCodes = evidenceCodes;
        this.publications = publications;
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FeatureOntologyTermAnnotation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append(", evidenceCodes=").append(evidenceCodes);
        sb.append(", publications=").append(publications);
        sb.append(", qualifier='").append(qualifier).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public FeatureOntologyTermAnnotation setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FeatureOntologyTermAnnotation setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public FeatureOntologyTermAnnotation setSource(String source) {
        this.source = source;
        return this;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public FeatureOntologyTermAnnotation setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public List<String> getEvidenceCodes() {
        return evidenceCodes;
    }

    public FeatureOntologyTermAnnotation setEvidenceCodes(List<String> evidenceCodes) {
        this.evidenceCodes = evidenceCodes;
        return this;
    }

    public List<String> getPublications() {
        return publications;
    }

    public FeatureOntologyTermAnnotation setPublications(List<String> publications) {
        this.publications = publications;
        return this;
    }

    public String getQualifier() {
        return qualifier;
    }

    public FeatureOntologyTermAnnotation setQualifier(String qualifier) {
        this.qualifier = qualifier;
        return this;
    }
}
