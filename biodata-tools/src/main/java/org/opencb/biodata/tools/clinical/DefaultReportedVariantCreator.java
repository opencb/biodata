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
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.commons.Disorder;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.calculateAcmgClassification;

public class DefaultReportedVariantCreator extends ReportedVariantCreator {

    private Set<String> biotypeSet;
    private Set<String> soNameSet;

    private boolean includeUntieredVariants;

    public DefaultReportedVariantCreator(Map<String, ClinicalProperty.RoleInCancer> roleInCancer,
                                         Map<String, List<String>> actionableVariants, Disorder disorder,
                                         ModeOfInheritance modeOfInheritance, Penetrance penetrance, List<DiseasePanel> diseasePanels,
                                         List<String> biotypes, List<String> soNames,
                                         boolean includeUntieredVariants) {
        super(diseasePanels, disorder, modeOfInheritance, penetrance, roleInCancer, actionableVariants);

        this.biotypeSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(biotypes)) {
            biotypeSet.addAll(biotypes);
        }
        this.soNameSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(soNames)) {
            soNameSet.addAll(soNames);
        }

        this.includeUntieredVariants = includeUntieredVariants;
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) {
        List<ReportedVariant> reportedVariants = new ArrayList<>();

        // Disease panels are optional in custom interpretation analysis
        Map<String, List<DiseasePanel.GenePanel>> geneToPanelMap = null;
        Map<String, List<DiseasePanel.VariantPanel>> variantToPanelMap = null;
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            geneToPanelMap = getGeneToPanelMap(diseasePanels);
            variantToPanelMap = getVariantToPanelMap(diseasePanels);
        }

        boolean untiered;
        for (Variant variant : variants) {
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            if (MapUtils.isNotEmpty(variantToPanelMap) && variantToPanelMap.containsKey(variant.getId())
                    && CollectionUtils.isNotEmpty(variantToPanelMap.get(variant.getId()))) {
                // Tier 1, variant in panel

                List<DiseasePanel.VariantPanel> panels = variantToPanelMap.get(variant.getId());
                List<String> panelIds = panels.stream().map(DiseasePanel.VariantPanel::getId).collect(Collectors.toList());

                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        reportedEvents.addAll(createReportedEvents(TIER_1, panelIds, ct, variant));
                    }
                } else {
                    // We create the reported events anyway!
                    reportedEvents.addAll(createReportedEvents(TIER_1, panelIds, null, variant));
                }
            } else {
                // Sanity check
                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    boolean tier2;
                    if (MapUtils.isNotEmpty(geneToPanelMap)) {
                        // Gene panels are present
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            if (geneToPanelMap.containsKey(ct.getEnsemblGeneId())
                                    && CollectionUtils.isNotEmpty(geneToPanelMap.get(ct.getEnsemblGeneId()))) {
                                // Gene in panel
                                List<DiseasePanel.GenePanel> panels = geneToPanelMap.get(ct.getEnsemblGeneId());
                                List<String> panelIds = panels.stream().map(DiseasePanel.GenePanel::getId).collect(Collectors.toList());
                                tier2 = isTier2(ct);
                                if (tier2 || includeUntieredVariants) {
                                    reportedEvents.addAll(createReportedEvents(tier2 ? TIER_2 : null, panelIds, ct, variant));
                                }
                            }
                        }
                    } else {
                        // No gene panels provided
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            tier2 = isTier2(ct);
                            if (tier2 || includeUntieredVariants) {
                                reportedEvents.addAll(createReportedEvents(tier2 ? TIER_2 : null, null, ct, variant));
                            }

                        }
                    }
                }
            }

            // Create a reported variant only if we have reported events
            if (CollectionUtils.isNotEmpty(reportedEvents)) {
                ReportedVariant reportedVariant = new ReportedVariant(variant.getImpl(), 0, new ArrayList<>(),
                        Collections.emptyList(), Collections.emptyMap());
                reportedVariant.setReportedEvents(reportedEvents);

                // Add variant to the list
                reportedVariants.add(reportedVariant);
            }
        }
        return reportedVariants;
    }

    private boolean isTier2(ConsequenceType ct) {
        if (CollectionUtils.isNotEmpty(biotypeSet) && CollectionUtils.isNotEmpty(soNameSet)) {
            if (biotypeSet.contains(ct.getBiotype()) && containSoName(ct, soNameSet)) {
                return true;
            }
        } else if (CollectionUtils.isNotEmpty(biotypeSet) && biotypeSet.contains(ct.getBiotype())) {
            return true;
        } else if (CollectionUtils.isNotEmpty(soNameSet) && containSoName(ct, soNameSet)) {
            return true;
        }
        return false;
    }
}
