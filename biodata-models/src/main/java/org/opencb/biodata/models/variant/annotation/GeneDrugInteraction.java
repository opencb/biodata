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

package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 16/07/15.
 */
public class GeneDrugInteraction {

    private String geneName;
    private String drugName;
    private String source;
    private String studyType;
    private String type;

    public GeneDrugInteraction() {};

    public GeneDrugInteraction(String geneName, String drugName, String source, String studyType, String type) {
        this.geneName = geneName;
        this.drugName = drugName;
        this.source = source;
        this.studyType = studyType;
        this.type = type;
    }

    public String getGeneName() { return geneName; }

    public void setGeneName(String geneName) { this.geneName = geneName; }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStudyType() {
        return studyType;
    }

    public void setStudyType(String studyType) {
        this.studyType = studyType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
