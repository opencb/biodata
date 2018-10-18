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

import java.util.List;
import java.util.Map;

public class ReportedVariant extends Variant {

    private double deNovoQualityScore;
    private List<ReportedEvent> reportedEvents;
    private List<Comment> comments;

    // TODO maybe this parameter should be in Variant
    private Map<String, Object> attributes;

    public ReportedVariant() {
    }

    public ReportedVariant(VariantAvro avro) {
        super(avro);
    }

    public ReportedVariant(VariantAvro avro, double deNovoQualityScore, List<ReportedEvent> reportedEvents, List<Comment> comments,
                           Map<String, Object> attributes) {
        super(avro);

        this.deNovoQualityScore = deNovoQualityScore;
        this.reportedEvents = reportedEvents;
        this.comments = comments;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReportedVariant{");
        sb.append("deNovoQualityScore=").append(deNovoQualityScore);
        sb.append(", reportedEvents=").append(reportedEvents);
        sb.append(", comments=").append(comments);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public double getDeNovoQualityScore() {
        return deNovoQualityScore;
    }

    public ReportedVariant setDeNovoQualityScore(double deNovoQualityScore) {
        this.deNovoQualityScore = deNovoQualityScore;
        return this;
    }

    public List<ReportedEvent> getReportedEvents() {
        return reportedEvents;
    }

    public ReportedVariant setReportedEvents(List<ReportedEvent> reportedEvents) {
        this.reportedEvents = reportedEvents;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public ReportedVariant setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ReportedVariant setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
