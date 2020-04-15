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
import java.util.Set;

public class AnnotationEvidence {

    private String code;
    private Set<String> publications;
    private String qualifier;

    public AnnotationEvidence() {
    }

    public AnnotationEvidence(String code, Set<String> publications, String qualifier) {
        this.code = code;
        this.publications = publications;
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnnotationEvidence{");
        sb.append("code='").append(code).append('\'');
        sb.append(", publications=").append(publications);
        sb.append(", qualifier='").append(qualifier).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getCode() {
        return code;
    }

    public AnnotationEvidence setCode(String code) {
        this.code = code;
        return this;
    }

    public Set<String> getPublications() {
        return publications;
    }

    public AnnotationEvidence setPublications(Set<String> publications) {
        this.publications = publications;
        return this;
    }

    public String getQualifier() {
        return qualifier;
    }

    public AnnotationEvidence setQualifier(String qualifier) {
        this.qualifier = qualifier;
        return this;
    }
}
