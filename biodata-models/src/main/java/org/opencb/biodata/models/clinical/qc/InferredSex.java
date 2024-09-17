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

package org.opencb.biodata.models.clinical.qc;

import org.opencb.biodata.models.clinical.interpretation.Software;
import org.opencb.biodata.models.common.Image;
import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;
import org.opencb.commons.datastore.core.ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class InferredSex {

    @DataField(id = "method", description = FieldConstants.INFERRED_SEX_METHOD_DESCRIPTION)
    private String method;

    @DataField(id = "sampleId", description = FieldConstants.INFERRED_SEX_SAMPLE_DESCRIPTION)
    private String sampleId;

    @DataField(id = "software", description = FieldConstants.INFERRED_SEX_SOFTWARE_DESCRIPTION)
    private Software software;

    @DataField(id = "inferredKaryotypicSex", description = FieldConstants.INFERRED_SEX_INFERRED_KARYOTYPIC_SEX_DESCRIPTION)
    private String inferredKaryotypicSex;

    @DataField(id = "values", description = FieldConstants.INFERRED_SEX_VALUES_DESCRIPTION)
    private ObjectMap values;

    @DataField(id = "images", description = FieldConstants.INFERRED_SEX_IMAGES_DESCRIPTION)
    private List<Image> images;

    @DataField(id = "attributes", description = FieldConstants.INFERRED_SEX_ATTRIBUTES_DESCRIPTION)
    private ObjectMap attributes;

    public InferredSex() {
        this("","", new Software(), "", new ObjectMap(), new ArrayList<>(), new ObjectMap());
    }

    public InferredSex(String method, String sampleId, Software software, String inferredKaryotypicSex, ObjectMap values,
                       List<Image> images, ObjectMap attributes) {
        this.method = method;
        this.sampleId = sampleId;
        this.software = software;
        this.inferredKaryotypicSex = inferredKaryotypicSex;
        this.values = values;
        this.images = images;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InferredSex{");
        sb.append("method='").append(method).append('\'');
        sb.append(", sampleId='").append(sampleId).append('\'');
        sb.append(", software=").append(software);
        sb.append(", inferredKaryotypicSex='").append(inferredKaryotypicSex).append('\'');
        sb.append(", values=").append(values);
        sb.append(", images=").append(images);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public InferredSex setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getSampleId() {
        return sampleId;
    }

    public InferredSex setSampleId(String sampleId) {
        this.sampleId = sampleId;
        return this;
    }

    public Software getSoftware() {
        return software;
    }

    public InferredSex setSoftware(Software software) {
        this.software = software;
        return this;
    }

    public String getInferredKaryotypicSex() {
        return inferredKaryotypicSex;
    }

    public InferredSex setInferredKaryotypicSex(String inferredKaryotypicSex) {
        this.inferredKaryotypicSex = inferredKaryotypicSex;
        return this;
    }

    public ObjectMap getValues() {
        return values;
    }

    public InferredSex setValues(ObjectMap values) {
        this.values = values;
        return this;
    }

    public List<Image> getImages() {
        return images;
    }

    public InferredSex setImages(List<Image> images) {
        this.images = images;
        return this;
    }

    public ObjectMap getAttributes() {
        return attributes;
    }

    public InferredSex setAttributes(ObjectMap attributes) {
        this.attributes = attributes;
        return this;
    }
}
