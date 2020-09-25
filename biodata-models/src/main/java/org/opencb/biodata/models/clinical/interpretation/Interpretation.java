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

import org.opencb.biodata.models.clinical.ClinicalAnalyst;
import org.opencb.biodata.models.clinical.ClinicalComment;

import java.util.List;
import java.util.Map;

public class Interpretation {

    private String id;
    private String uuid;
    private String description;
    private String clinicalAnalysisId;

    /**
     * Interpretation algorithm tool used to generate this interpretation.
     */
    private ClinicalAnalyst analyst;
    private List<InterpretationMethod> methods;

    private List<ClinicalVariant> primaryFindings;
    private List<ClinicalVariant> secondaryFindings;

    private List<ClinicalComment> comments;

    private String status;
    private String creationDate;
    private String modificationDate;
    private int version;

    /**
     * Users can add custom information in this field.
     * OpenCGA uses this field to store the Clinical Analysis object in key 'OPENCGA_CLINICAL_ANALYSIS'
     */
    private Map<String, Object> attributes;

    public Interpretation() {
    }

    public Interpretation(String id, String uuid, String description, String clinicalAnalysisId, ClinicalAnalyst analyst,
                          List<InterpretationMethod> methods, List<ClinicalVariant> primaryFindings,
                          List<ClinicalVariant> secondaryFindings, List<ClinicalComment> comments, String status, String creationDate,
                          String modificationDate, int version, Map<String, Object> attributes) {
        this.id = id;
        this.uuid = uuid;
        this.description = description;
        this.clinicalAnalysisId = clinicalAnalysisId;
        this.analyst = analyst;
        this.methods = methods;
        this.primaryFindings = primaryFindings;
        this.secondaryFindings = secondaryFindings;
        this.comments = comments;
        this.status = status;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.version = version;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Interpretation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", uuid='").append(uuid).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", clinicalAnalysisId='").append(clinicalAnalysisId).append('\'');
        sb.append(", analyst=").append(analyst);
        sb.append(", methods=").append(methods);
        sb.append(", primaryFindings=").append(primaryFindings);
        sb.append(", secondaryFindings=").append(secondaryFindings);
        sb.append(", comments=").append(comments);
        sb.append(", status='").append(status).append('\'');
        sb.append(", creationDate='").append(creationDate).append('\'');
        sb.append(", modificationDate='").append(modificationDate).append('\'');
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

    public String getUuid() {
        return uuid;
    }

    public Interpretation setUuid(String uuid) {
        this.uuid = uuid;
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

    public ClinicalAnalyst getAnalyst() {
        return analyst;
    }

    public Interpretation setAnalyst(ClinicalAnalyst analyst) {
        this.analyst = analyst;
        return this;
    }

    public List<InterpretationMethod> getMethods() {
        return methods;
    }

    public Interpretation setMethods(List<InterpretationMethod> methods) {
        this.methods = methods;
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

    public List<ClinicalComment> getComments() {
        return comments;
    }

    public Interpretation setComments(List<ClinicalComment> comments) {
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

    public String getModificationDate() {
        return modificationDate;
    }

    public Interpretation setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
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
