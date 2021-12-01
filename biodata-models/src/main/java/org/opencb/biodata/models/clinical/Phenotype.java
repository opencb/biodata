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

import java.util.Collections;
import java.util.Map;

public class Phenotype extends OntologyTermAnnotation {

    private String ageOfOnset;
    private Status status;

    public enum Status {
        OBSERVED,
        NOT_OBSERVED,
        UNKNOWN
    }

    public Phenotype() {
    }

    public Phenotype(String id, String name, String source) {
        this(id, name, "", source, "", Collections.emptyMap(), "", Status.UNKNOWN);
    }

    public Phenotype(String id, String name, String source, Status status) {
        this(id, name, "", source, "", Collections.emptyMap(), "", status);
    }

    public Phenotype(String id, String name, String source, String ageOfOnset, Status status, Map<String, String> attributes) {
        this(id, name, "", source, "", attributes, ageOfOnset, Status.UNKNOWN);
    }

    public Phenotype(String id, String name, String description, String source, String url, Map<String, String> attributes,
                     String ageOfOnset, Status status) {
        super(id, name, description, source, url, attributes);
        this.ageOfOnset = ageOfOnset;
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Phenotype{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", status=").append(status);
        sb.append(", ageOfOnset='").append(ageOfOnset).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Phenotype setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Phenotype setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Phenotype setSource(String source) {
        this.source = source;
        return this;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public Phenotype setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Phenotype setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getAgeOfOnset() {
        return ageOfOnset;
    }

    public Phenotype setAgeOfOnset(String ageOfOnset) {
        this.ageOfOnset = ageOfOnset;
        return this;
    }

}
