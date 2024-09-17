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

public class Relatedness {

    @DataField(id = "method", description = FieldConstants.RELATEDNESS_METHOD_DESCRIPTION)
    private String method;

    @DataField(id = "software", description = FieldConstants.RELATEDNESS_SOFTWARE_DESCRIPTION)
    private Software software;

    @DataField(id = "scores", uncommentedClasses = {"RelatednessScore"}, description = FieldConstants.RELATEDNESS_SCORES_DESCRIPTION)
    private List<RelatednessScore> scores;

    @DataField(id = "images", description = FieldConstants.RELATEDNESS_IMAGES_DESCRIPTION)
    private List<Image> images;

    @DataField(id = "attributes", description = FieldConstants.RELATEDNESS_ATTRIBUTES_DESCRIPTION)
    private ObjectMap attributes;

    public Relatedness() {
        this("", new Software(), new ArrayList<>(), new ArrayList<>(), new ObjectMap());
    }

    public Relatedness(String method, Software software, List<RelatednessScore> scores, List<Image> images, ObjectMap attributes) {
        this.method = method;
        this.software = software;
        this.scores = scores;
        this.images = images;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Relatedness{");
        sb.append("method='").append(method).append('\'');
        sb.append(", software=").append(software);
        sb.append(", scores=").append(scores);
        sb.append(", images=").append(images);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public Relatedness setMethod(String method) {
        this.method = method;
        return this;
    }

    public Software getSoftware() {
        return software;
    }

    public Relatedness setSoftware(Software software) {
        this.software = software;
        return this;
    }

    public List<RelatednessScore> getScores() {
        return scores;
    }

    public Relatedness setScores(List<RelatednessScore> scores) {
        this.scores = scores;
        return this;
    }

    public List<Image> getImages() {
        return images;
    }

    public Relatedness setImages(List<Image> images) {
        this.images = images;
        return this;
    }

    public ObjectMap getAttributes() {
        return attributes;
    }

    public Relatedness setAttributes(ObjectMap attributes) {
        this.attributes = attributes;
        return this;
    }
}
