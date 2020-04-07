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

public class OntologyTerm {

    private String id;
    private String name;
    private String source;
    private String description;
    private String namespace;
    private String comment;
    private List<String> synonyms;
    private List<String> xrefs;
    private List<String> parents;
    private List<String> children;

    public OntologyTerm() {
    }

    public OntologyTerm(String id, String name, String source, String description, String namespace, String comment,
                        List<String> synonyms, List<String> xrefs, List<String> parents, List<String> children) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.description = description;
        this.namespace = namespace;
        this.comment = comment;
        this.synonyms = synonyms;
        this.xrefs = xrefs;
        this.parents = parents;
        this.children = children;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OntologyTerm{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", namespace='").append(namespace).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", synonyms=").append(synonyms);
        sb.append(", xrefs=").append(xrefs);
        sb.append(", parents=").append(parents);
        sb.append(", children=").append(children);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public OntologyTerm setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OntologyTerm setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public OntologyTerm setSource(String source) {
        this.source = source;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OntologyTerm setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public OntologyTerm setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public OntologyTerm setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public OntologyTerm setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
        return this;
    }

    public List<String> getXrefs() {
        return xrefs;
    }

    public OntologyTerm setXrefs(List<String> xrefs) {
        this.xrefs = xrefs;
        return this;
    }

    public List<String> getParents() {
        return parents;
    }

    public OntologyTerm setParents(List<String> parents) {
        this.parents = parents;
        return this;
    }

    public List<String> getChildren() {
        return children;
    }

    public OntologyTerm setChildren(List<String> children) {
        this.children = children;
        return this;
    }
}