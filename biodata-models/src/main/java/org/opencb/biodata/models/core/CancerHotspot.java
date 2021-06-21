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
    private int aminoacidPosition;
    private String aminoacidReference;
    private int numMutations;
    private String cancerType;
    private Map<String, Integer> cancerTypeCount;
    private Map<String, Integer> organCount;
    private List<String> analysis;
    private double log10Pvalue;
    private double mutability;
    private double muProtein;
    private double qvalue;
    private double qvaluePancan;
    private double qvalueCancerType;
    private List<CancerHotspotVariant> variants;

    public CancerHotspot() {
    }

    public CancerHotspot(String geneName, int aminoacidPosition, String aminoacidReference, int numMutations, String cancerType,
                         Map<String, Integer> cancerTypeCount, Map<String, Integer> organCount, List<String> analysis, double log10Pvalue,
                         double mutability, double muProtein, double qvalue, double qvaluePancan, double qvalueCancerType,
                         List<CancerHotspotVariant> variants) {
        this.geneName = geneName;
        this.aminoacidPosition = aminoacidPosition;
        this.aminoacidReference = aminoacidReference;
        this.numMutations = numMutations;
        this.cancerType = cancerType;
        this.cancerTypeCount = cancerTypeCount;
        this.organCount = organCount;
        this.analysis = analysis;
        this.log10Pvalue = log10Pvalue;
        this.mutability = mutability;
        this.muProtein = muProtein;
        this.qvalue = qvalue;
        this.qvaluePancan = qvaluePancan;
        this.qvalueCancerType = qvalueCancerType;
        this.variants = variants;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CancerHotspot{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", aminoacidPosition=").append(aminoacidPosition);
        sb.append(", aminoacidReference='").append(aminoacidReference).append('\'');
        sb.append(", numMutations=").append(numMutations);
        sb.append(", cancerType='").append(cancerType).append('\'');
        sb.append(", cancerTypeCount=").append(cancerTypeCount);
        sb.append(", organCount=").append(organCount);
        sb.append(", analysis=").append(analysis);
        sb.append(", log10Pvalue=").append(log10Pvalue);
        sb.append(", mutability=").append(mutability);
        sb.append(", muProtein=").append(muProtein);
        sb.append(", qvalue=").append(qvalue);
        sb.append(", qvaluePancan=").append(qvaluePancan);
        sb.append(", qvalueCancerType=").append(qvalueCancerType);
        sb.append(", variants=").append(variants);
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

    public double getLog10Pvalue() {
        return log10Pvalue;
    }

    public CancerHotspot setLog10Pvalue(double log10Pvalue) {
        this.log10Pvalue = log10Pvalue;
        return this;
    }

    public double getMutability() {
        return mutability;
    }

    public CancerHotspot setMutability(double mutability) {
        this.mutability = mutability;
        return this;
    }

    public double getMuProtein() {
        return muProtein;
    }

    public CancerHotspot setMuProtein(double muProtein) {
        this.muProtein = muProtein;
        return this;
    }

    public double getQvalue() {
        return qvalue;
    }

    public CancerHotspot setQvalue(double qvalue) {
        this.qvalue = qvalue;
        return this;
    }

    public double getQvaluePancan() {
        return qvaluePancan;
    }

    public CancerHotspot setQvaluePancan(double qvaluePancan) {
        this.qvaluePancan = qvaluePancan;
        return this;
    }

    public double getQvalueCancerType() {
        return qvalueCancerType;
    }

    public CancerHotspot setQvalueCancerType(double qvalueCancerType) {
        this.qvalueCancerType = qvalueCancerType;
        return this;
    }

    public List<CancerHotspotVariant> getVariants() {
        return variants;
    }

    public CancerHotspot setVariants(List<CancerHotspotVariant> variants) {
        this.variants = variants;
        return this;
    }
}
