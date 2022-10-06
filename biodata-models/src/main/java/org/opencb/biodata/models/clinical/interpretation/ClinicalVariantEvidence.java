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

import org.apache.commons.collections4.CollectionUtils;
import org.opencb.biodata.models.clinical.Phenotype;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /***************************************************************************
     * IMPORTANT
     * roleInCancer is deprecated but it is kept by backward compatibility,
     * and it is set in the function setRolesInCancer:
     *
     *     public ClinicalVariantEvidence setRolesInCancer(List<RoleInCancer> rolesInCancer) {
     *         this.rolesInCancer = rolesInCancer;
     *         if (CollectionUtils.isNotEmpty(rolesInCancer)) {
     *             this.setRoleInCancer(rolesInCancer.get(0));
     *         }
     *         return this;
     *     }
     *  FIXME (29/09/22) This has been added to keep backward compatibility. To be removed in 1 year.
     ********************************************************************************/
    @Deprecated
    private RoleInCancer roleInCancer;
    private List<RoleInCancer> rolesInCancer;
    @Deprecated
    private boolean actionable;

    private ClinicalEvidenceReview review;

    private Map<String, Object> attributes;

    public ClinicalVariantEvidence() {
        phenotypes = Collections.emptyList();
        compoundHeterozygousVariantIds = Collections.emptyList();
        fullyExplainPhenotypes = false;
        actionable = false;
        review = new ClinicalEvidenceReview();
        attributes = new HashMap<>();
    }


    @Deprecated
    public ClinicalVariantEvidence(String interpretationMethodName, List<Phenotype> phenotypes, GenomicFeature genomicFeature,
                                   List<ModeOfInheritance> modeOfInheritances, String panelId, VariantClassification classification,
                                   Penetrance penetrance, double score, boolean fullyExplainPhenotypes,
                                   List<String> compoundHeterozygousVariantIds, RoleInCancer roleInCancer, boolean actionable,
                                   ClinicalEvidenceReview review) {
        this(interpretationMethodName, phenotypes, genomicFeature, modeOfInheritances, panelId, classification, penetrance, score,
                fullyExplainPhenotypes, compoundHeterozygousVariantIds, roleInCancer, null, actionable, review, new HashMap<>());
    }

    @Deprecated
    public ClinicalVariantEvidence(String interpretationMethodName, List<Phenotype> phenotypes, GenomicFeature genomicFeature,
                                   List<ModeOfInheritance> modeOfInheritances, String panelId, VariantClassification classification,
                                   Penetrance penetrance, double score, boolean fullyExplainPhenotypes,
                                   List<String> compoundHeterozygousVariantIds, RoleInCancer roleInCancer, List<RoleInCancer> rolesInCancer,
                                   boolean actionable, ClinicalEvidenceReview review) {
        this(interpretationMethodName, phenotypes, genomicFeature, modeOfInheritances, panelId, classification, penetrance, score,
                fullyExplainPhenotypes, compoundHeterozygousVariantIds, roleInCancer, rolesInCancer, actionable, review, new HashMap<>());
    }

    public ClinicalVariantEvidence(String interpretationMethodName, List<Phenotype> phenotypes, GenomicFeature genomicFeature,
                                   List<ModeOfInheritance> modeOfInheritances, String panelId, VariantClassification classification,
                                   Penetrance penetrance, double score, boolean fullyExplainPhenotypes,
                                   List<String> compoundHeterozygousVariantIds, RoleInCancer roleInCancer, List<RoleInCancer> rolesInCancer,
                                   boolean actionable, ClinicalEvidenceReview review, Map<String, Object> attributes) {
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
        this.rolesInCancer = rolesInCancer;
        this.actionable = actionable;
        this.review = review;
        this.attributes = attributes;
    }

    @Deprecated
    public ClinicalVariantEvidence(String interpretationMethodName, List<Phenotype> phenotypes, GenomicFeature genomicFeature,
                                   List<ModeOfInheritance> modeOfInheritances, String panelId, VariantClassification classification,
                                   Penetrance penetrance, double score, boolean fullyExplainPhenotypes,
                                   List<String> compoundHeterozygousVariantIds, RoleInCancer roleInCancer,
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
        this.review = review;
        this.attributes = new HashMap<>();
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
        sb.append(", rolesInCancer=").append(rolesInCancer);
        sb.append(", actionable=").append(actionable);
        sb.append(", review=").append(review);
        sb.append(", attributes=").append(attributes);
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

    public GenomicFeature getGenomicFeature() {
        return genomicFeature;
    }

    public ClinicalVariantEvidence setGenomicFeature(GenomicFeature genomicFeature) {
        this.genomicFeature = genomicFeature;
        return this;
    }

    public List<ModeOfInheritance> getModeOfInheritances() {
        return modeOfInheritances;
    }

    public ClinicalVariantEvidence setModeOfInheritances(List<ModeOfInheritance> modeOfInheritances) {
        this.modeOfInheritances = modeOfInheritances;
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

    @Deprecated
    public RoleInCancer getRoleInCancer() {
        return roleInCancer;
    }

    @Deprecated
    public ClinicalVariantEvidence setRoleInCancer(RoleInCancer roleInCancer) {
        this.roleInCancer = roleInCancer;
        return this;
    }

    public List<RoleInCancer> getRolesInCancer() {
        return rolesInCancer;
    }

    public ClinicalVariantEvidence setRolesInCancer(List<RoleInCancer> rolesInCancer) {
        this.rolesInCancer = rolesInCancer;
        // FIXME (29/09/22) This has been added to keep backward compatibility. To be removed in 1 year.
        if (CollectionUtils.isNotEmpty(rolesInCancer)) {
            this.setRoleInCancer(rolesInCancer.get(0));
        }
        return this;
    }

    public boolean isActionable() {
        return actionable;
    }

    public ClinicalVariantEvidence setActionable(boolean actionable) {
        this.actionable = actionable;
        return this;
    }

    public ClinicalEvidenceReview getReview() {
        return review;
    }

    public ClinicalVariantEvidence setReview(ClinicalEvidenceReview review) {
        this.review = review;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ClinicalVariantEvidence setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
