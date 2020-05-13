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

import org.opencb.biodata.models.clinical.Phenotype;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;

import java.util.Collections;
import java.util.List;

import static org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.*;

public class ClinicalVariantEvidence {

    private String interpretationMethodName;

    private List<Phenotype> phenotypes;
    private List<SequenceOntologyTerm> consequenceTypes;
    private GenomicFeature genomicFeature;
    private ModeOfInheritance modeOfInheritance;
    private String panelId;
    private VariantClassification classification;
    private Penetrance penetrance;
    private double score;
    private boolean fullyExplainPhenotypes;
    private List<String> compoundHeterozygousVariantIds;
    private RoleInCancer roleInCancer;
    private boolean actionable;
    private String justification;

    public ClinicalVariantEvidence() {
        phenotypes = Collections.emptyList();
        consequenceTypes = Collections.emptyList();
        compoundHeterozygousVariantIds = Collections.emptyList();
        fullyExplainPhenotypes = false;
        actionable = false;
    }

    public ClinicalVariantEvidence(String interpretationMethodName, List<Phenotype> phenotypes, List<SequenceOntologyTerm> consequenceTypes,
                                   GenomicFeature genomicFeature, ModeOfInheritance modeOfInheritance, String panelId,
                                   VariantClassification classification, Penetrance penetrance, double score,
                                   boolean fullyExplainPhenotypes, List<String> compoundHeterozygousVariantIds, RoleInCancer roleInCancer,
                                   boolean actionable, String justification) {
        this.interpretationMethodName = interpretationMethodName;
        this.phenotypes = phenotypes;
        this.consequenceTypes = consequenceTypes;
        this.genomicFeature = genomicFeature;
        this.modeOfInheritance = modeOfInheritance;
        this.panelId = panelId;
        this.classification = classification;
        this.penetrance = penetrance;
        this.score = score;
        this.fullyExplainPhenotypes = fullyExplainPhenotypes;
        this.compoundHeterozygousVariantIds = compoundHeterozygousVariantIds;
        this.roleInCancer = roleInCancer;
        this.actionable = actionable;
        this.justification = justification;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantEvidence{");
        sb.append("interpretationMethodName='").append(interpretationMethodName).append('\'');
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", consequenceTypes=").append(consequenceTypes);
        sb.append(", genomicFeature=").append(genomicFeature);
        sb.append(", modeOfInheritance=").append(modeOfInheritance);
        sb.append(", panelId='").append(panelId).append('\'');
        sb.append(", classification=").append(classification);
        sb.append(", penetrance=").append(penetrance);
        sb.append(", score=").append(score);
        sb.append(", fullyExplainPhenotypes=").append(fullyExplainPhenotypes);
        sb.append(", compoundHeterozygousVariantIds=").append(compoundHeterozygousVariantIds);
        sb.append(", roleInCancer=").append(roleInCancer);
        sb.append(", actionable=").append(actionable);
        sb.append(", justification='").append(justification).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getInterpretationMethodName() {
        return interpretationMethodName;
    }

    public ClinicalVariantEvidence setInterpretationMethodName(String interpretationMethodName) {
        this.interpretationMethodName = interpretationMethodName;
        return this;
    }

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public ClinicalVariantEvidence setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public List<SequenceOntologyTerm> getConsequenceTypes() {
        return consequenceTypes;
    }

    public ClinicalVariantEvidence setConsequenceTypes(List<SequenceOntologyTerm> consequenceTypes) {
        this.consequenceTypes = consequenceTypes;
        return this;
    }

    public GenomicFeature getGenomicFeature() {
        return genomicFeature;
    }

    public ClinicalVariantEvidence setGenomicFeature(GenomicFeature genomicFeature) {
        this.genomicFeature = genomicFeature;
        return this;
    }

    public ModeOfInheritance getModeOfInheritance() {
        return modeOfInheritance;
    }

    public ClinicalVariantEvidence setModeOfInheritance(ModeOfInheritance modeOfInheritance) {
        this.modeOfInheritance = modeOfInheritance;
        return this;
    }

    public String getPanelId() {
        return panelId;
    }

    public ClinicalVariantEvidence setPanelId(String panelId) {
        this.panelId = panelId;
        return this;
    }

    public VariantClassification getClassification() {
        return classification;
    }

    public ClinicalVariantEvidence setClassification(VariantClassification classification) {
        this.classification = classification;
        return this;
    }

    public Penetrance getPenetrance() {
        return penetrance;
    }

    public ClinicalVariantEvidence setPenetrance(Penetrance penetrance) {
        this.penetrance = penetrance;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ClinicalVariantEvidence setScore(double score) {
        this.score = score;
        return this;
    }

    public boolean isFullyExplainPhenotypes() {
        return fullyExplainPhenotypes;
    }

    public ClinicalVariantEvidence setFullyExplainPhenotypes(boolean fullyExplainPhenotypes) {
        this.fullyExplainPhenotypes = fullyExplainPhenotypes;
        return this;
    }

    public List<String> getCompoundHeterozygousVariantIds() {
        return compoundHeterozygousVariantIds;
    }

    public ClinicalVariantEvidence setCompoundHeterozygousVariantIds(List<String> compoundHeterozygousVariantIds) {
        this.compoundHeterozygousVariantIds = compoundHeterozygousVariantIds;
        return this;
    }

    public RoleInCancer getRoleInCancer() {
        return roleInCancer;
    }

    public ClinicalVariantEvidence setRoleInCancer(RoleInCancer roleInCancer) {
        this.roleInCancer = roleInCancer;
        return this;
    }

    public boolean isActionable() {
        return actionable;
    }

    public ClinicalVariantEvidence setActionable(boolean actionable) {
        this.actionable = actionable;
        return this;
    }

    public String getJustification() {
        return justification;
    }

    public ClinicalVariantEvidence setJustification(String justification) {
        this.justification = justification;
        return this;
    }
}
