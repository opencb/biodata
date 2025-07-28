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

import org.opencb.biodata.models.variant.avro.*;

import java.util.ArrayList;
import java.util.List;


public class GeneAnnotation {

    private List<Expression> expression;
    private List<GeneTraitAssociation> diseases;
    private List<GeneDrugInteraction> drugs;
    private List<Constraint> constraints;
    private List<MirnaTarget> mirnaTargets;
    private List<GeneCancerAssociation> cancerAssociations;
    private List<CancerHotspot> cancerHotspots;
    private List<ImprintedGene> imprinted;
    private List<GeneFusion> fusions;

    public GeneAnnotation() {
        this.expression = new ArrayList<>();
        this.diseases = new ArrayList<>();
        this.drugs = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.mirnaTargets = new ArrayList<>();
        this.cancerAssociations = new ArrayList<>();
        this.cancerHotspots = new ArrayList<>();
        this.imprinted = new ArrayList<>();
        this.fusions = new ArrayList<>();
    }

    @Deprecated
    public GeneAnnotation(List<Expression> expression, List<GeneTraitAssociation> diseases,
                          List<GeneDrugInteraction> drugs, List<Constraint> constraints, List<MirnaTarget> mirnaTargets) {
        this(expression, diseases, drugs, constraints, mirnaTargets, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
    }

    @Deprecated
    public GeneAnnotation(List<Expression> expression, List<GeneTraitAssociation> diseases, List<GeneDrugInteraction> drugs,
                          List<Constraint> constraints, List<MirnaTarget> mirnaTargets, List<GeneCancerAssociation> cancerAssociations) {
        this(expression, diseases, drugs, constraints, mirnaTargets, cancerAssociations, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
    }

    @Deprecated
    public GeneAnnotation(List<Expression> expression, List<GeneTraitAssociation> diseases, List<GeneDrugInteraction> drugs,
                          List<Constraint> constraints, List<MirnaTarget> mirnaTargets, List<GeneCancerAssociation> cancerAssociations,
                          List<CancerHotspot> cancerHotspots) {
        this(expression, diseases, drugs, constraints, mirnaTargets, cancerAssociations, cancerHotspots, new ArrayList<>(),
                new ArrayList<>());
    }

    public GeneAnnotation(List<Expression> expression, List<GeneTraitAssociation> diseases, List<GeneDrugInteraction> drugs,
                          List<Constraint> constraints, List<MirnaTarget> mirnaTargets, List<GeneCancerAssociation> cancerAssociations,
                          List<CancerHotspot> cancerHotspots, List<ImprintedGene> imprinted, List<GeneFusion> fusions) {
        this.expression = expression;
        this.diseases = diseases;
        this.drugs = drugs;
        this.constraints = constraints;
        this.mirnaTargets = mirnaTargets;
        this.cancerAssociations = cancerAssociations;
        this.cancerHotspots = cancerHotspots;
        this.imprinted = imprinted;
        this.fusions = fusions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneAnnotation{");
        sb.append("expression=").append(expression);
        sb.append(", diseases=").append(diseases);
        sb.append(", drugs=").append(drugs);
        sb.append(", constraints=").append(constraints);
        sb.append(", mirnaTargets=").append(mirnaTargets);
        sb.append(", cancerAssociations=").append(cancerAssociations);
        sb.append(", cancerHotspots=").append(cancerHotspots);
        sb.append(", imprinted=").append(imprinted);
        sb.append(", fusions=").append(fusions);
        sb.append('}');
        return sb.toString();
    }

    public List<Expression> getExpression() {
        return expression;
    }

    public GeneAnnotation setExpression(List<Expression> expression) {
        this.expression = expression;
        return this;
    }

    public List<GeneTraitAssociation> getDiseases() {
        return diseases;
    }

    public GeneAnnotation setDiseases(List<GeneTraitAssociation> diseases) {
        this.diseases = diseases;
        return this;
    }

    public List<GeneDrugInteraction> getDrugs() {
        return drugs;
    }

    public GeneAnnotation setDrugs(List<GeneDrugInteraction> drugs) {
        this.drugs = drugs;
        return this;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public GeneAnnotation setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
        return this;
    }

    public List<MirnaTarget> getMirnaTargets() {
        return mirnaTargets;
    }

    public GeneAnnotation setMirnaTargets(List<MirnaTarget> mirnaTargets) {
        this.mirnaTargets = mirnaTargets;
        return this;
    }

    public List<GeneCancerAssociation> getCancerAssociations() {
        return cancerAssociations;
    }

    public GeneAnnotation setCancerAssociations(List<GeneCancerAssociation> cancerAssociations) {
        this.cancerAssociations = cancerAssociations;
        return this;
    }

    public List<CancerHotspot> getCancerHotspots() {
        return cancerHotspots;
    }

    public GeneAnnotation setCancerHotspots(List<CancerHotspot> cancerHotspots) {
        this.cancerHotspots = cancerHotspots;
        return this;
    }

    public List<ImprintedGene> getImprinted() {
        return imprinted;
    }

    public GeneAnnotation setImprinted(List<ImprintedGene> imprinted) {
        this.imprinted = imprinted;
        return this;
    }

    public List<GeneFusion> getFusions() {
        return fusions;
    }

    public GeneAnnotation setFusions(List<GeneFusion> fusions) {
        this.fusions = fusions;
        return this;
    }
}
