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

import org.opencb.biodata.models.commons.OntologyTermAnnotation;

import java.util.List;
import java.util.Map;


public class TranscriptOntologyTermAnnotation extends OntologyTermAnnotation {

    private List<String> evidenceCodes;
    private List<String> publications;
    private String qualifier;

    public TranscriptOntologyTermAnnotation() {
    }

    public TranscriptOntologyTermAnnotation(String id, String name, String source, Map<String, String> attributes,
                                            List<String> evidenceCodes, List<String> publications, String qualifier) {
        super(id, name, source, attributes);

        this.evidenceCodes = evidenceCodes;
        this.publications = publications;
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranscriptOntologyTermAnnotation{");
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

    public List<String> getEvidenceCodes() {
        return evidenceCodes;
    }

    public TranscriptOntologyTermAnnotation setEvidenceCodes(List<String> evidenceCodes) {
        this.evidenceCodes = evidenceCodes;
        return this;
    }

    public List<String> getPublications() {
        return publications;
    }

    public TranscriptOntologyTermAnnotation setPublications(List<String> publications) {
        this.publications = publications;
        return this;
    }

    public String getQualifier() {
        return qualifier;
    }

    public TranscriptOntologyTermAnnotation setQualifier(String qualifier) {
        this.qualifier = qualifier;
        return this;
    }
}
