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

import java.util.List;
import java.util.Map;

public class Interpretation {

    private String id;
    private String description;
    private String clinicalAnalysisId;

    /**
     * Interpretation algorithm tool used to generate this interpretation.
     */
    private Software software;
    private Analyst analyst;
    private List<Software> dependencies;
    private Map<String, Object> filters;

    private List<DiseasePanel> panels;
    private List<ClinicalVariant> primaryFindings;
    private List<ClinicalVariant> secondaryFindings;
    private List<ReportedLowCoverage> lowCoverageRegions;

    private List<Comment> comments;

    private String status;
    private String creationDate;
    private int version;

    /**
     * Users can add custom information in this field.
     * OpenCGA uses this field to store the Clinical Analysis object in key 'OPENCGA_CLINICAL_ANALYSIS'
     */
    private Map<String, Object> attributes;

    public Interpretation() {
    }

    public Interpretation(String id, String description, String clinicalAnalysisId, Software software, Analyst analyst,
                          List<Software> dependencies, Map<String, Object> filters, List<DiseasePanel> panels,
                          List<ClinicalVariant> primaryFindings, List<ClinicalVariant> secondaryFindings,
                          List<ReportedLowCoverage> lowCoverageRegions, List<Comment> comments, String status,
                          String creationDate, int version, Map<String, Object> attributes) {
        this.id = id;
        this.description = description;
        this.clinicalAnalysisId = clinicalAnalysisId;
        this.software = software;
        this.analyst = analyst;
        this.dependencies = dependencies;
        this.filters = filters;
        this.panels = panels;
        this.primaryFindings = primaryFindings;
        this.secondaryFindings = secondaryFindings;
        this.lowCoverageRegions = lowCoverageRegions;
        this.comments = comments;
        this.status = status;
        this.creationDate = creationDate;
        this.version = version;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Interpretation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", clinicalAnalysisId='").append(clinicalAnalysisId).append('\'');
        sb.append(", software=").append(software);
        sb.append(", analyst=").append(analyst);
        sb.append(", dependencies=").append(dependencies);
        sb.append(", filters=").append(filters);
        sb.append(", panels=").append(panels);
        sb.append(", primaryFindings=").append(primaryFindings);
        sb.append(", secondaryFindings=").append(secondaryFindings);
        sb.append(", lowCoverageRegions=").append(lowCoverageRegions);
        sb.append(", comments=").append(comments);
        sb.append(", status=").append(status);
        sb.append(", creationDate='").append(creationDate).append('\'');
        sb.append(", version=").append(version);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Interpretation setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Interpretation setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getClinicalAnalysisId() {
        return clinicalAnalysisId;
    }

    public Interpretation setClinicalAnalysisId(String clinicalAnalysisId) {
        this.clinicalAnalysisId = clinicalAnalysisId;
        return this;
    }

    public Software getSoftware() {
        return software;
    }

    public Interpretation setSoftware(Software software) {
        this.software = software;
        return this;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public Interpretation setAnalyst(Analyst analyst) {
        this.analyst = analyst;
        return this;
    }

    public List<Software> getDependencies() {
        return dependencies;
    }

    public Interpretation setDependencies(List<Software> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public Interpretation setFilters(Map<String, Object> filters) {
        this.filters = filters;
        return this;
    }

    public List<DiseasePanel> getPanels() {
        return panels;
    }

    public Interpretation setPanels(List<DiseasePanel> panels) {
        this.panels = panels;
        return this;
    }

    public List<ClinicalVariant> getPrimaryFindings() {
        return primaryFindings;
    }

    public Interpretation setPrimaryFindings(List<ClinicalVariant> primaryFindings) {
        this.primaryFindings = primaryFindings;
        return this;
    }

    public List<ClinicalVariant> getSecondaryFindings() {
        return secondaryFindings;
    }

    public Interpretation setSecondaryFindings(List<ClinicalVariant> secondaryFindings) {
        this.secondaryFindings = secondaryFindings;
        return this;
    }

    public List<ReportedLowCoverage> getLowCoverageRegions() {
        return lowCoverageRegions;
    }

    public Interpretation setLowCoverageRegions(List<ReportedLowCoverage> lowCoverageRegions) {
        this.lowCoverageRegions = lowCoverageRegions;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Interpretation setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Interpretation setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Interpretation setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public Interpretation setVersion(int version) {
        this.version = version;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Interpretation setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
