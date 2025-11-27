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
import org.opencb.biodata.models.clinical.ClinicalProperty;
import org.opencb.biodata.models.clinical.interpretation.stats.ClinicalVariantSummaryStats;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;

import java.util.*;

public class ClinicalVariant extends Variant {

    private List<ClinicalVariantEvidence> evidences;
    private List<ClinicalComment> comments;
    @Deprecated
    private Map<String, Object> filters;
    private ClinicalVariantFilter filter;
    private List<ClinicalProperty.ModeOfInheritance> modesOfInheritance; // all compatible MoIs
    private String recommendation;
    private List<MiniPubmed> references;
    private ClinicalDiscussion discussion;
    private ClinicalVariantConfidence confidence;
    private List<String> tags;
    private List<String> images;

    private List<ClinicalVariantSummaryStats> stats;

    private Status status;
    private int version;

    // TODO maybe this parameter should be in Variant
    private Map<String, Object> attributes;

    public enum Status {
        NOT_REVIEWED,
        UNDER_CONSIDERATION,
        CANDIDATE,
        REVIEWED,
        VALIDATION_REQUESTED,
        VALIDATED,
        DISCARDED,
        REPORTED,
        ARTIFACT
    }

    public ClinicalVariant() {
        this.status = Status.NOT_REVIEWED;
    }

    public ClinicalVariant(VariantAvro avro) {
        this(avro, new ArrayList<>(), new ArrayList<>(), new ClinicalVariantFilter(), Collections.emptyList(), "", Collections.emptyList(),
                new ClinicalDiscussion(), new ClinicalVariantConfidence(), Collections.emptyList(), Status.NOT_REVIEWED,
                Collections.emptyList(), Collections.emptyList(), new HashMap<>());
    }

    @Deprecated
    public ClinicalVariant(VariantAvro avro, List<ClinicalVariantEvidence> evidences, List<ClinicalComment> comments,
                           ClinicalVariantFilter filter, List<ClinicalProperty.ModeOfInheritance> modesOfInheritance, String recommendation,
                           List<MiniPubmed> references,
                           ClinicalDiscussion discussion, ClinicalVariantConfidence confidence, List<ClinicalVariantSummaryStats> stats,
                           Status status, List<String> tags, List<String> images, Map<String, Object> attributes) {
        this(avro, evidences, comments, filter, modesOfInheritance, recommendation, references, discussion, confidence, stats,
                status, tags, images, 1, attributes);
    }

    public ClinicalVariant(VariantAvro avro, List<ClinicalVariantEvidence> evidences, List<ClinicalComment> comments,
                           ClinicalVariantFilter filter, List<ClinicalProperty.ModeOfInheritance> modesOfInheritance, String recommendation,
                           List<MiniPubmed> references,
                           ClinicalDiscussion discussion, ClinicalVariantConfidence confidence, List<ClinicalVariantSummaryStats> stats,
                           Status status, List<String> tags, List<String> images, int version, Map<String, Object> attributes) {
        super(avro);

        this.evidences = evidences;
        this.comments = comments;
        this.filter = filter;
        this.modesOfInheritance = modesOfInheritance;
        this.recommendation = recommendation;
        this.references = references;
        this.discussion = discussion;
        this.stats = stats;
        this.status = status;
        this.tags = tags;
        this.images = images;
        this.confidence = confidence;
        this.version = version;
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

    @Deprecated
    public Map<String, Object> getFilters() {
        if (this.filter != null) {
            return this.filter.getQuery();
        }
        return null;
    }

    @Deprecated
    public ClinicalVariant setFilters(Map<String, Object> filters) {
        if (this.filter != null) {
            this.filter.setQuery(filters);
        } else {
            this.filter = new ClinicalVariantFilter(filters, "", "");
        }
        return this;
    }

    public ClinicalVariantFilter getFilter() {
        return filter;
    }

    public ClinicalVariant setFilter(ClinicalVariantFilter filter) {
        this.filter = filter;
        return this;
    }

    public List<ClinicalProperty.ModeOfInheritance> getModesOfInheritance() {
        return modesOfInheritance;
    }

    public ClinicalVariant setModesOfInheritance(List<ClinicalProperty.ModeOfInheritance> modesOfInheritance) {
        this.modesOfInheritance = modesOfInheritance;
        return this;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public ClinicalVariant setRecommendation(String recommendation) {
        this.recommendation = recommendation;
        return this;
    }

    public List<MiniPubmed> getReferences() {
        return references;
    }

    public ClinicalVariant setReferences(List<MiniPubmed> references) {
        this.references = references;
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

    public List<ClinicalVariantSummaryStats> getStats() {
        return stats;
    }

    public ClinicalVariant setStats(List<ClinicalVariantSummaryStats> stats) {
        this.stats = stats;
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

    public List<String> getImages() {
        return images;
    }

    public ClinicalVariant setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public ClinicalVariant setVersion(int version) {
        this.version = version;
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
