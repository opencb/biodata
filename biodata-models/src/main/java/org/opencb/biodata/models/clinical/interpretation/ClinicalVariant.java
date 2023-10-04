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
import org.opencb.biodata.models.clinical.ClinicalDiscussion;
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
    private ClinicalDiscussion discussion;
    private ClinicalVariantConfidence confidence;
    private List<String> tags;

    private Status status;

    // TODO maybe this parameter should be in Variant
    private Map<String, Object> attributes;

    public enum Status {
        NOT_REVIEWED,
        REVIEW_REQUESTED,
        REVIEWED,
        DISCARDED,
        REPORTED,
        ARTIFACT
    }

    public ClinicalVariant() {
        this.status = Status.NOT_REVIEWED;
    }

    public ClinicalVariant(VariantAvro avro) {
        this(avro, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new ClinicalDiscussion(), null,
                Status.NOT_REVIEWED, new ArrayList<>(), new HashMap<>());
    }

    @Deprecated
    public ClinicalVariant(VariantAvro avro, List<ClinicalVariantEvidence> evidences, List<ClinicalComment> comments,
                           Map<String, Object> filters, ClinicalDiscussion discussion, Status status, List<String> tags,
                           Map<String, Object> attributes) {
        super(avro);

        this.evidences = evidences;
        this.comments = comments;
        this.filters = filters;
        this.discussion = discussion;
        this.status = status;
        this.tags = tags;
        this.attributes = attributes;
    }

    public ClinicalVariant(VariantAvro avro, List<ClinicalVariantEvidence> evidences, List<ClinicalComment> comments,
                           Map<String, Object> filters, ClinicalDiscussion discussion,
                           ClinicalVariantConfidence confidence, Status status, List<String> tags,
                           Map<String, Object> attributes) {
        super(avro);

        this.evidences = evidences;
        this.comments = comments;
        this.filters = filters;
        this.discussion = discussion;
        this.status = status;
        this.tags = tags;
        this.confidence = confidence;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return super.toString();
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

    public ClinicalDiscussion getDiscussion() {
        return discussion;
    }

    public ClinicalVariant setDiscussion(ClinicalDiscussion discussion) {
        this.discussion = discussion;
        return this;
    }

    public ClinicalVariantConfidence getConfidence() {
        return confidence;
    }

    public ClinicalVariant setConfidence(ClinicalVariantConfidence confidence) {
        this.confidence = confidence;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public ClinicalVariant setStatus(Status status) {
        this.status = status;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public ClinicalVariant setTags(List<String> tags) {
        this.tags = tags;
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
