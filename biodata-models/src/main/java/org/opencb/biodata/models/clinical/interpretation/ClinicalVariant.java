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

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClinicalVariant extends Variant {

    private double deNovoQualityScore;
    private List<ClinicalVariantEvidence> evidences;
    private List<Comment> comments;

    private Status status;

    // TODO maybe this parameter should be in Variant
    private Map<String, Object> attributes;

    public enum Status {
        NOT_REVIEWED,
        UNDER_REVIEW,
        REVIEWED,
        REJECTED,
        TO_BE_REPORTED
    }

    public ClinicalVariant() {
        this.status = Status.NOT_REVIEWED;
    }

    public ClinicalVariant(VariantAvro avro) {
        this(avro, 0.0, new ArrayList<>(), new ArrayList<>(), Status.NOT_REVIEWED, new HashMap<>());
    }

    public ClinicalVariant(VariantAvro avro, double deNovoQualityScore, List<ClinicalVariantEvidence> evidences, List<Comment> comments,
                           Status status, Map<String, Object> attributes) {
        super(avro);

        this.deNovoQualityScore = deNovoQualityScore;
        this.evidences = evidences;
        this.comments = comments;
        this.status = status;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariant{");
        sb.append("variant=").append(super.toString());
        sb.append(", deNovoQualityScore=").append(deNovoQualityScore);
        sb.append(", evidences=").append(evidences);
        sb.append(", comments=").append(comments);
        sb.append(", status=").append(status);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public double getDeNovoQualityScore() {
        return deNovoQualityScore;
    }

    public ClinicalVariant setDeNovoQualityScore(double deNovoQualityScore) {
        this.deNovoQualityScore = deNovoQualityScore;
        return this;
    }

    public List<ClinicalVariantEvidence> getEvidences() {
        return evidences;
    }

    public ClinicalVariant setEvidences(List<ClinicalVariantEvidence> evidences) {
        this.evidences = evidences;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public ClinicalVariant setComments(List<Comment> comments) {
        this.comments = comments;
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
