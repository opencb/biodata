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

import org.opencb.biodata.models.protein.ProteinFeature;
import org.opencb.biodata.models.variant.annotation.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjlopez on 18/09/15.
 */
public class ProteinVariantAnnotation {

    private String uniprotAccession;
    private String uniprotName;
    private int position;
    private String reference;
    private String alternate;
    private String uniprotVariantId;
    private String functionalDescription;
    private List<Score> substitutionScores = null;
    private List<String> keywords;
    private List<ProteinFeature> features;

    public ProteinVariantAnnotation() {
    }

    public ProteinVariantAnnotation(int aaPosition, String aaReference, String aaAlternate,
                                    List<Score> substitutionScores) {
        this(null, null, aaPosition, aaReference, aaAlternate, null, null, substitutionScores, null, null);

    }

    public ProteinVariantAnnotation(String uniprotAccession, String uniprotName, int position, String reference,
                                    String alternate, String uniprotVariantId, String functionalDescription,
                                    List<Score> substitutionScores, List<String> keywords,
                                    List<ProteinFeature> features) {
        this.uniprotAccession = uniprotAccession;
        this.uniprotName = uniprotName;
        this.position = position;
        this.reference = reference;
        this.alternate = alternate;
        this.uniprotVariantId = uniprotVariantId;
        this.functionalDescription = functionalDescription;
        this.substitutionScores = substitutionScores;
        this.keywords = keywords;
        this.features = features;
    }

    public String getUniprotAccession() {
        return uniprotAccession;
    }

    public void setUniprotAccession(String uniprotAccession) {
        this.uniprotAccession = uniprotAccession;
    }

    public String getUniprotName() {
        return uniprotName;
    }

    public void setUniprotName(String uniprotName) {
        this.uniprotName = uniprotName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public String getUniprotVariantId() {
        return uniprotVariantId;
    }

    public void setUniprotVariantId(String uniprotVariantId) {
        this.uniprotVariantId = uniprotVariantId;
    }

    public String getFunctionalDescription() {
        return functionalDescription;
    }

    public void setFunctionalDescription(String functionalDescription) {
        this.functionalDescription = functionalDescription;
    }

    public List<Score> getSubstitutionScores() {
        return substitutionScores;
    }

    public void setSubstitutionScores(List<Score> substitutionScores) {
        this.substitutionScores = substitutionScores;
    }

    public void addSubstitutionScore(Score score) {
        if (this.substitutionScores == null) {
            substitutionScores = new ArrayList<>();
        }
        substitutionScores.add(score);
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void addUniprotKeyword(String keyword) {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        keywords.add(keyword);
    }

    public List<ProteinFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<ProteinFeature> features) {
        this.features = features;
    }

    public void addProteinFeature(ProteinFeature proteinFeature) {
        if(features ==null) {
            features = new ArrayList<>();
        }
        features.add(proteinFeature);
    }
}
