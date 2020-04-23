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

import java.util.List;

/**
 * Created by fjlopez on 16/07/15.
 */
public class GeneDrugInteraction {

    private String geneName;
    private String source;
    private String interactionType;
    private String drugName;
    private String chemblId;
    private List<String> publications;
    @Deprecated
    private String studyType;
    @Deprecated
    private String type;

    public GeneDrugInteraction() {};

    public GeneDrugInteraction(String geneName, String source, String interactionType, String drugName, String chemblId,
                               List<String> publications) {
        this.geneName = geneName;
        this.source = source;
        this.interactionType = interactionType;
        this.drugName = drugName;
        this.chemblId = chemblId;
        this.publications = publications;
    }

    @Deprecated
    public GeneDrugInteraction(String geneName, String drugName, String source, String studyType, String type) {
        this.geneName = geneName;
        this.drugName = drugName;
        this.source = source;
        this.studyType = studyType;
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneDrugInteraction{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", interactionType='").append(interactionType).append('\'');
        sb.append(", drugName='").append(drugName).append('\'');
        sb.append(", chemblId='").append(chemblId).append('\'');
        sb.append(", publications=").append(publications);
        sb.append('}');
        return sb.toString();
    }

    public String getGeneName() {
        return geneName;
    }

    public GeneDrugInteraction setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getSource() {
        return source;
    }

    public GeneDrugInteraction setSource(String source) {
        this.source = source;
        return this;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public GeneDrugInteraction setInteractionType(String interactionType) {
        this.interactionType = interactionType;
        return this;
    }

    public String getDrugName() {
        return drugName;
    }

    public GeneDrugInteraction setDrugName(String drugName) {
        this.drugName = drugName;
        return this;
    }

    public String getChemblId() {
        return chemblId;
    }

    public GeneDrugInteraction setChemblId(String chemblId) {
        this.chemblId = chemblId;
        return this;
    }

    public List<String> getPublications() {
        return publications;
    }

    public GeneDrugInteraction setPublications(List<String> publications) {
        this.publications = publications;
        return this;
    }

    public String getStudyType() {
        return studyType;
    }

    public GeneDrugInteraction setStudyType(String studyType) {
        this.studyType = studyType;
        return this;
    }

    public String getType() {
        return type;
    }

    public GeneDrugInteraction setType(String type) {
        this.type = type;
        return this;
    }
}
