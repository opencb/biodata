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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.opencb.biodata.models.clinical.ClinicalDiscussion;
import org.opencb.biodata.models.clinical.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.ClinicalProperty.RoleInCancer;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariant;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariantEvidence;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.clinical.Disorder;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.*;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.proteinCoding;

public class TeamClinicalVariantCreator extends ClinicalVariantCreator {

    public TeamClinicalVariantCreator(List<DiseasePanel> diseasePanels, Map<String, RoleInCancer> roleInCancer, Disorder disorder,
                                      List<ModeOfInheritance> modeOfInheritances, Penetrance penetrance) {
        super(diseasePanels, disorder, modeOfInheritances, penetrance, roleInCancer, null);
    }

    @Override
    public List<ClinicalVariant> create(List<Variant> variants) throws InterpretationAnalysisException {
        // Sanity check
        if (variants == null || variants.isEmpty()) {
            return Collections.emptyList();
        }

        // Panels are mandatory in Tiering analysis
        if (CollectionUtils.isEmpty(diseasePanels)) {
            throw new InterpretationAnalysisException("Missing gene panels for TEAM analysis");
        }

        geneToPanelMap = getGeneToPanelMap(diseasePanels);
        variantToPanelMap = getVariantToPanelMap(diseasePanels);

        boolean hasTier;

        List<ClinicalVariant> clinicalVariants = new ArrayList<>();
        for (Variant variant : variants) {
            hasTier = false;
            List<ClinicalVariantEvidence> clinicalVariantEvidences = new ArrayList<>();

            if (MapUtils.isNotEmpty(variantToPanelMap) && variantToPanelMap.containsKey(variant.getId())
                    && CollectionUtils.isNotEmpty(variantToPanelMap.get(variant.getId()))) {
                // Tier 1, variant in panel
                hasTier = true;

                Set<DiseasePanel> panels = variantToPanelMap.get(variant.getId());
                List<String> panelIds = panels.stream().map(DiseasePanel::getId).collect(Collectors.toList());

                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        if (ct.getBiotype() != null && proteinCoding.contains(ct.getBiotype())) {
                            clinicalVariantEvidences.addAll(createClinicalVariantEvidences(TIER_1, panelIds, ct, variant));
                        }
                    }
                } else {
                    // We create the clinical varaint evidences anyway!
                    clinicalVariantEvidences.addAll(createClinicalVariantEvidences(TIER_1, panelIds, null, variant));
                }
            } else {
                // Tier 2
                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        if (ct.getBiotype() != null && proteinCoding.contains(ct.getBiotype())) {
                            if (MapUtils.isNotEmpty(geneToPanelMap) && geneToPanelMap.containsKey(ct.getEnsemblGeneId())
                                    && CollectionUtils.isNotEmpty(geneToPanelMap.get(ct.getEnsemblGeneId()))) {
                                // Tier 2, gene in panel
                                hasTier = true;
                                Set<DiseasePanel> panels = geneToPanelMap.get(ct.getEnsemblGeneId());
                                List<String> panelIds = panels.stream().map(DiseasePanel::getId).collect(Collectors.toList());

                                clinicalVariantEvidences.addAll(createClinicalVariantEvidences(TIER_2, panelIds, ct, variant));
                            }
                        }
                    }
                }
            }

            // If we have clinical variant evidences, then we have to create the clinical variant
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
}
