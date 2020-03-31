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

import java.util.ArrayList;
import java.util.List;

public class OntologyAnnotation {

    private String oboTermId;
    private String oboTermName;
    private List<String> evidenceCodes;
    private List<String> publications;
    private String qualifier;

    public OntologyAnnotation() {

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OntologyAnnotation{");
        sb.append("oboTermId='").append(oboTermId).append('\'');
        sb.append(", oboTermName='").append(oboTermName).append('\'');
        sb.append(", evidenceCodes=").append(evidenceCodes);
        sb.append(", publications=").append(publications);
        sb.append(", qualifier=").append(qualifier);
        sb.append('}');
        return sb.toString();
    }

    public String getOboTermId() {
        return oboTermId;
    }

    public OntologyAnnotation setOboTermId(String oboTermId) {
        this.oboTermId = oboTermId;
        return this;
    }

    public String getOboTermName() {
        return oboTermName;
    }

    public OntologyAnnotation setOboTermName(String oboTermName) {
        this.oboTermName = oboTermName;
        return this;
    }

    public List<String> getEvidenceCodes() {
        return evidenceCodes;
    }

    public OntologyAnnotation setEvidenceCodes(List<String> evidenceCodes) {
        this.evidenceCodes = evidenceCodes;
        return this;
    }

    public List<String> getPublications() {
        return publications;
    }

    public OntologyAnnotation setPublications(List<String> publications) {
        this.publications = publications;
        return this;
    }

    public String getQualifier() {
        return qualifier;
    }

    public OntologyAnnotation setQualifier(String qualifier) {
        this.qualifier = qualifier;
        return this;
    }

}