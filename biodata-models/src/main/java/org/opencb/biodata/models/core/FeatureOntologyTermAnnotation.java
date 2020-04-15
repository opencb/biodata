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

    private List<AnnotationEvidence> evidence;

    public FeatureOntologyTermAnnotation() {
    }

    public FeatureOntologyTermAnnotation(String id, String name, String source, Map<String, String> attributes,
                                         List<AnnotationEvidence> evidence) {
        super(id, name, source, attributes);

        this.evidence = evidence;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FeatureOntologyTermAnnotation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append(", evidence=").append(evidence);
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

    public List<AnnotationEvidence> getEvidence() {
        return evidence;
    }

    public FeatureOntologyTermAnnotation setEvidence(List<AnnotationEvidence> evidence) {
        this.evidence = evidence;
        return this;
    }
}
