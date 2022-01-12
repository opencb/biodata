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

import static org.opencb.biodata.models.clinical.ClinicalProperty.*;

public class ClinicalVariantEvidence {

    private String interpretationMethodName;

    private List<Phenotype> phenotypes;
    private GenomicFeature genomicFeature;
    private List<ModeOfInheritance> modeOfInheritances;
    private String panelId;
    private VariantClassification classification;
    private Penetrance penetrance;
    private double score;
    private boolean fullyExplainPhenotypes;
    private List<String> compoundHeterozygousVariantIds;
    private RoleInCancer roleInCancer;
    private boolean actionable;

    private ClinicalEvidenceReview review;

    public ClinicalVariantEvidence() {
        phenotypes = Collections.emptyList();
        compoundHeterozygousVariantIds = Collections.emptyList();
        fullyExplainPhenotypes = false;
        actionable = false;
        review = new ClinicalEvidenceReview();
    }

    public ClinicalVariantEvidence(String interpretationMethodName, List<Phenotype> phenotypes, GenomicFeature genomicFeature,
                                   List<ModeOfInheritance> modeOfInheritances, String panelId, VariantClassification classification,
                                   Penetrance penetrance, double score, boolean fullyExplainPhenotypes,
                                   List<String> compoundHeterozygousVariantIds, RoleInCancer roleInCancer, boolean actionable,
                                   ClinicalEvidenceReview review) {
        this.interpretationMethodName = interpretationMethodName;
        this.phenotypes = phenotypes;
        this.genomicFeature = genomicFeature;
        this.modeOfInheritances = modeOfInheritances;
        this.panelId = panelId;
        this.classification = classification;
        this.penetrance = penetrance;
        this.score = score;
        this.fullyExplainPhenotypes = fullyExplainPhenotypes;
        this.compoundHeterozygousVariantIds = compoundHeterozygousVariantIds;
        this.roleInCancer = roleInCancer;
        this.actionable = actionable;
        this.review = review;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantEvidence{");
        sb.append("interpretationMethodName='").append(interpretationMethodName).append('\'');
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", genomicFeature=").append(genomicFeature);
        sb.append(", modeOfInheritances=").append(modeOfInheritances);
        sb.append(", panelId='").append(panelId).append('\'');
        sb.append(", classification=").append(classification);
        sb.append(", penetrance=").append(penetrance);
        sb.append(", score=").append(score);
        sb.append(", fullyExplainPhenotypes=").append(fullyExplainPhenotypes);
        sb.append(", compoundHeterozygousVariantIds=").append(compoundHeterozygousVariantIds);
        sb.append(", roleInCancer=").append(roleInCancer);
        sb.append(", actionable=").append(actionable);
        sb.append(", review=").append(review);
        sb.append('}');
        return sb.toString();
    }

    public String getInterpretationMethodName() {
        return interpretationMethodName;
    }

    public void setInterpretationMethodName(String interpretationMethodName) {
        this.interpretationMethodName = interpretationMethodName;
    }

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public void setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
    }

    public GenomicFeature getGenomicFeature() {
        return genomicFeature;
    }

    public void setGenomicFeature(GenomicFeature genomicFeature) {
        this.genomicFeature = genomicFeature;
    }

    public List<ModeOfInheritance> getModeOfInheritances() {
        return modeOfInheritances;
    }

    public void setModeOfInheritances(List<ModeOfInheritance> modeOfInheritances) {
        this.modeOfInheritances = modeOfInheritances;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public VariantClassification getClassification() {
        return classification;
    }

    public void setClassification(VariantClassification classification) {
        this.classification = classification;
    }

    public Penetrance getPenetrance() {
        return penetrance;
    }

    public void setPenetrance(Penetrance penetrance) {
        this.penetrance = penetrance;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isFullyExplainPhenotypes() {
        return fullyExplainPhenotypes;
    }

    public void setFullyExplainPhenotypes(boolean fullyExplainPhenotypes) {
        this.fullyExplainPhenotypes = fullyExplainPhenotypes;
    }

    public List<String> getCompoundHeterozygousVariantIds() {
        return compoundHeterozygousVariantIds;
    }

    public void setCompoundHeterozygousVariantIds(List<String> compoundHeterozygousVariantIds) {
        this.compoundHeterozygousVariantIds = compoundHeterozygousVariantIds;
    }

    public RoleInCancer getRoleInCancer() {
        return roleInCancer;
    }

    public void setRoleInCancer(RoleInCancer roleInCancer) {
        this.roleInCancer = roleInCancer;
    }

    public boolean isActionable() {
        return actionable;
    }

    public void setActionable(boolean actionable) {
        this.actionable = actionable;
    }

    public ClinicalEvidenceReview getReview() {
        return review;
    }

    public void setReview(ClinicalEvidenceReview review) {
        this.review = review;
    }
}
