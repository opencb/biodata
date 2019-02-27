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
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.RoleInCancer;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.GenomicFeature;
import org.opencb.biodata.models.clinical.interpretation.ReportedEvent;
import org.opencb.biodata.models.clinical.interpretation.ReportedVariant;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.commons.Disorder;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;

import java.util.*;
import java.util.stream.Collectors;

public class TeamReportedVariantCreator extends ReportedVariantCreator {

    public TeamReportedVariantCreator(List<DiseasePanel> diseasePanels, Map<String, RoleInCancer> roleInCancer,
                                      Map<String, List<String>> actionableVariants, Disorder disorder, ModeOfInheritance modeOfInheritance,
                                      Penetrance penetrance) {
        super(diseasePanels, disorder, modeOfInheritance, penetrance, roleInCancer, actionableVariants);
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) throws InterpretationAnalysisException {
        // Panels are mandatory in Tiering analysis
        if (CollectionUtils.isEmpty(diseasePanels)) {
            throw new InterpretationAnalysisException("Missing gene panels for TEAM analysis");
        }

        Map<String, List<DiseasePanel.GenePanel>> geneToPanelMap = getGeneToPanelMap(diseasePanels);
        Map<String, List<DiseasePanel.VariantPanel>> variantToPanelMap = getVariantToPanelMap(diseasePanels);

        boolean hasTier;

        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            hasTier = false;
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            if (MapUtils.isNotEmpty(variantToPanelMap) && variantToPanelMap.containsKey(variant.getId())
                    && CollectionUtils.isNotEmpty(variantToPanelMap.get(variant.getId()))) {
                // Tier 1, variant in panel
                hasTier = true;

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
                // Tier 2
                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        if (MapUtils.isNotEmpty(geneToPanelMap) && geneToPanelMap.containsKey(ct.getEnsemblGeneId())
                                && CollectionUtils.isNotEmpty(geneToPanelMap.get(ct.getEnsemblGeneId()))) {
                            // Tier 2, gene in panel
                            hasTier = true;
                            List<DiseasePanel.GenePanel> panels = geneToPanelMap.get(ct.getEnsemblGeneId());
                            List<String> panelIds = panels.stream().map(DiseasePanel.GenePanel::getId).collect(Collectors.toList());

                            reportedEvents.addAll(createReportedEvents(TIER_2, panelIds, ct, variant));
                        }
                    }
                }
            }

            // Tier 3, actionable variants
            if (!hasTier && MapUtils.isNotEmpty(actionableVariants)) {
                if (variant.getAnnotation() != null && actionableVariants.containsKey(variant.getId())) {
                    if (CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            reportedEvents.addAll(createReportedEvents(null, null, ct, variant));
                        }
                    } else {
                        // We create the reported events anyway!
                        reportedEvents.addAll(createReportedEvents(null, null, null, variant));
                    }
                }
            }

            // If we have reported events, then we have to create the reported variant
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
}
