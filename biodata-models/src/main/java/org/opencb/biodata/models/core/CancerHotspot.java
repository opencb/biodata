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

package org.opencb.biodata.models.core;

import java.util.List;
import java.util.Map;

public class CancerHotspot {

    private String geneName;
    private String proteinId;
    private int aminoacidPosition;
    private String aminoacidReference;
    private int numMutations;
    private String cancerType;
    private Map<String, Double> scores;
    private Map<String, Integer> cancerTypeCount;
    private Map<String, Integer> organCount;
    private List<String> analysis;
    private List<CancerHotspotVariant> variants;
    private String source;

    public CancerHotspot() {
    }

    public CancerHotspot(String geneName, String proteinId, int aminoacidPosition, String aminoacidReference, int numMutations,
                         String cancerType, Map<String, Double> scores, Map<String, Integer> cancerTypeCount,
                         Map<String, Integer> organCount, List<String> analysis, List<CancerHotspotVariant> variants, String source) {
        this.geneName = geneName;
        this.proteinId = proteinId;
        this.aminoacidPosition = aminoacidPosition;
        this.aminoacidReference = aminoacidReference;
        this.numMutations = numMutations;
        this.cancerType = cancerType;
        this.scores = scores;
        this.cancerTypeCount = cancerTypeCount;
        this.organCount = organCount;
        this.analysis = analysis;
        this.variants = variants;
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CancerHotspot{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", proteinId='").append(proteinId).append('\'');
        sb.append(", aminoacidPosition=").append(aminoacidPosition);
        sb.append(", aminoacidReference='").append(aminoacidReference).append('\'');
        sb.append(", numMutations=").append(numMutations);
        sb.append(", cancerType='").append(cancerType).append('\'');
        sb.append(", scores=").append(scores);
        sb.append(", cancerTypeCount=").append(cancerTypeCount);
        sb.append(", organCount=").append(organCount);
        sb.append(", analysis=").append(analysis);
        sb.append(", variants=").append(variants);
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getGeneName() {
        return geneName;
    }

    public CancerHotspot setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getProteinId() {
        return proteinId;
    }

    public CancerHotspot setProteinId(String proteinId) {
        this.proteinId = proteinId;
        return this;
    }

    public int getAminoacidPosition() {
        return aminoacidPosition;
    }

    public CancerHotspot setAminoacidPosition(int aminoacidPosition) {
        this.aminoacidPosition = aminoacidPosition;
        return this;
    }

    public String getAminoacidReference() {
        return aminoacidReference;
    }

    public CancerHotspot setAminoacidReference(String aminoacidReference) {
        this.aminoacidReference = aminoacidReference;
        return this;
    }

    public int getNumMutations() {
        return numMutations;
    }

    public CancerHotspot setNumMutations(int numMutations) {
        this.numMutations = numMutations;
        return this;
    }

    public String getCancerType() {
        return cancerType;
    }

    public CancerHotspot setCancerType(String cancerType) {
        this.cancerType = cancerType;
        return this;
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public CancerHotspot setScores(Map<String, Double> scores) {
        this.scores = scores;
        return this;
    }

    public Map<String, Integer> getCancerTypeCount() {
        return cancerTypeCount;
    }

    public CancerHotspot setCancerTypeCount(Map<String, Integer> cancerTypeCount) {
        this.cancerTypeCount = cancerTypeCount;
        return this;
    }

    public Map<String, Integer> getOrganCount() {
        return organCount;
    }

    public CancerHotspot setOrganCount(Map<String, Integer> organCount) {
        this.organCount = organCount;
        return this;
    }

    public List<String> getAnalysis() {
        return analysis;
    }

    public CancerHotspot setAnalysis(List<String> analysis) {
        this.analysis = analysis;
        return this;
    }

    public List<CancerHotspotVariant> getVariants() {
        return variants;
    }

    public CancerHotspot setVariants(List<CancerHotspotVariant> variants) {
        this.variants = variants;
        return this;
    }

    public String getSource() {
        return source;
    }

    public CancerHotspot setSource(String source) {
        this.source = source;
        return this;
    }
}
