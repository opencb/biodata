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

package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.clinical.ClinicalComment;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClinicalVariant extends Variant {

    private List<ClinicalVariantEvidence> evidences;
    private List<ClinicalComment> comments;
    private Map<String, Object> filters;
    private String discussion;

    private Status status;

    // TODO maybe this parameter should be in Variant
    private Map<String, Object> attributes;

    public enum Status {
        NOT_REVIEWED,
        REVIEW_REQUESTED,
        REVIEWED,
        DISCARDED,
        REPORTED
    }

    public ClinicalVariant() {
        this.status = Status.NOT_REVIEWED;
    }

    public ClinicalVariant(VariantAvro avro) {
        this(avro, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), "", Status.NOT_REVIEWED, new HashMap<>());
    }

    public ClinicalVariant(VariantAvro avro, List<ClinicalVariantEvidence> evidences, List<ClinicalComment> comments,
                           Map<String, Object> filters, String discussion, Status status, Map<String, Object> attributes) {
        super(avro);

        this.evidences = evidences;
        this.comments = comments;
        this.filters = filters;
        this.discussion = discussion;
        this.status = status;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariant{");
        sb.append("evidences=").append(evidences);
        sb.append(", comments=").append(comments);
        sb.append(", filters=").append(filters);
        sb.append(", discussion='").append(discussion).append('\'');
        sb.append(", status=").append(status);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public List<ClinicalVariantEvidence> getEvidences() {
        return evidences;
    }

    public ClinicalVariant setEvidences(List<ClinicalVariantEvidence> evidences) {
        this.evidences = evidences;
        return this;
    }

    public List<ClinicalComment> getComments() {
        return comments;
    }

    public ClinicalVariant setComments(List<ClinicalComment> comments) {
        this.comments = comments;
        return this;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public ClinicalVariant setFilters(Map<String, Object> filters) {
        this.filters = filters;
        return this;
    }

    public String getDiscussion() {
        return discussion;
    }

    public ClinicalVariant setDiscussion(String discussion) {
        this.discussion = discussion;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public ClinicalVariant setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ClinicalVariant setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
