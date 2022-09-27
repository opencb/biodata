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

package org.opencb.biodata.tools.clinical;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.opencb.biodata.models.clinical.ClinicalDiscussion;
import org.opencb.biodata.models.clinical.ClinicalProperty;
import org.opencb.biodata.models.clinical.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.Disorder;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariant;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariantEvidence;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.*;

public class DefaultClinicalVariantCreator extends ClinicalVariantCreator {

    private boolean includeUntieredVariants;

    public DefaultClinicalVariantCreator(Map<String, ClinicalProperty.RoleInCancer> roleInCancer, Disorder disorder,
                                         List<ModeOfInheritance> modeOfInheritances, Penetrance penetrance,
                                         List<DiseasePanel> diseasePanels, List<String> biotypes, List<String> soNames,
                                         boolean includeUntieredVariants) {
        super(diseasePanels, disorder, modeOfInheritances, penetrance, roleInCancer, null, biotypes, soNames);

        this.includeUntieredVariants = includeUntieredVariants;
    }

    @Override
    public List<ClinicalVariant> create(List<Variant> variants) {
        // Sanity check
        if (variants == null || variants.isEmpty()) {
            return Collections.emptyList();
        }

        List<ClinicalVariant> clinicalVariants = new ArrayList<>();

        // Disease panels are optional in custom interpretation analysis
        Map<String, Set<DiseasePanel>> geneToPanelMap = null;
        Map<String, Set<DiseasePanel>> variantToPanelMap = null;
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            geneToPanelMap = getGeneToPanelMap(diseasePanels);
            variantToPanelMap = getVariantToPanelMap(diseasePanels);
        }

        for (Variant variant : variants) {
            List<ClinicalVariantEvidence> clinicalVariantEvidences = new ArrayList<>();

            if (MapUtils.isNotEmpty(variantToPanelMap) && variantToPanelMap.containsKey(variant.getId())
                    && CollectionUtils.isNotEmpty(variantToPanelMap.get(variant.getId()))) {
                // Tier 1, variant in panel

                Set<DiseasePanel> panels = variantToPanelMap.get(variant.getId());
                List<String> panelIds = panels.stream().map(DiseasePanel::getId).collect(Collectors.toList());

                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        if (CollectionUtils.isEmpty(biotypeSet) || biotypeSet.contains(ct.getBiotype())) {
                            clinicalVariantEvidences.addAll(createClinicalVariantEvidences(TIER_1, panelIds, ct, variant, soNameSet));
                        }
                    }
                } else {
                    // We create the clinical variant evidences anyway!
                    clinicalVariantEvidences.addAll(createClinicalVariantEvidences(TIER_1, panelIds, null, variant, soNameSet));
                }
            } else {
                // Sanity check
                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    String tier;
                    if (MapUtils.isNotEmpty(geneToPanelMap)) {
                        // Gene panels are present
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            if (CollectionUtils.isEmpty(biotypeSet) || biotypeSet.contains(ct.getBiotype())) {
                                if (geneToPanelMap.containsKey(ct.getEnsemblGeneId())
                                        && CollectionUtils.isNotEmpty(geneToPanelMap.get(ct.getEnsemblGeneId()))) {
                                    // Gene in panel
                                    Set<DiseasePanel> panels = geneToPanelMap.get(ct.getEnsemblGeneId());
                                    List<String> panelIds = panels.stream().map(DiseasePanel::getId).collect(Collectors.toList());
                                    tier = getTier(ct, soNameSet);
                                    if (includeUntieredVariants || !UNTIERED.equals(tier)) {
                                        clinicalVariantEvidences.addAll(createClinicalVariantEvidences(tier, panelIds, ct, variant, soNameSet));
                                    }
                                }
                            }
                        }
                    } else {
                        // No gene panels provided
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            if (CollectionUtils.isEmpty(biotypeSet) || biotypeSet.contains(ct.getBiotype())) {
                                tier = getTier(ct, soNameSet);
                                if (includeUntieredVariants || !UNTIERED.equals(tier)) {
                                    clinicalVariantEvidences.addAll(createClinicalVariantEvidences(tier, null, ct, variant,
                                            soNameSet));
                                }
                            }
                        }
                    }
                }
            }

            // Create a clinical variant only if we have evidences
            if (CollectionUtils.isNotEmpty(clinicalVariantEvidences)) {
                ClinicalVariant clinicalVariant = new ClinicalVariant(variant.getImpl(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyMap(), new ClinicalDiscussion(), null, ClinicalVariant.Status.NOT_REVIEWED,
                        Collections.emptyList(), Collections.emptyMap());
                clinicalVariant.setEvidences(clinicalVariantEvidences);

                // Add variant to the list
                clinicalVariants.add(clinicalVariant);
            }
        }
        return clinicalVariants;
    }

    private String getTier(ConsequenceType ct, Set<String> includeSoTerms) {
        if (CollectionUtils.isNotEmpty(biotypeSet) && CollectionUtils.isNotEmpty(soNameSet)) {
            if (biotypeSet.contains(ct.getBiotype()) && containSOName(ct, soNameSet, includeSoTerms)) {
                return TIER_2;
            }
        } else if (CollectionUtils.isNotEmpty(biotypeSet) && biotypeSet.contains(ct.getBiotype())) {
            return TIER_2;
        } else if (CollectionUtils.isNotEmpty(soNameSet) && containSOName(ct, soNameSet, includeSoTerms)) {
            return TIER_2;
        }
        return UNTIERED;
    }
}
