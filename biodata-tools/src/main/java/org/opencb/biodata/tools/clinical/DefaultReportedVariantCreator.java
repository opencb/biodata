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
import org.apache.commons.lang.StringUtils;
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

    private boolean includeUntieredVariants;
    private boolean includeActionableVariants;

    public DefaultReportedVariantCreator(Map<String, ClinicalProperty.RoleInCancer> roleInCancer,
                                         Map<String, List<String>> actionableVariants, Disorder disorder,
                                         ModeOfInheritance modeOfInheritance, Penetrance penetrance, List<DiseasePanel> diseasePanels,
                                         boolean includeUntieredVariants, boolean includeActionableVariants) {
        super(diseasePanels, disorder, modeOfInheritance, penetrance, roleInCancer, actionableVariants);

        this.includeUntieredVariants = includeUntieredVariants;
        this.includeActionableVariants = includeActionableVariants;
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

        boolean hasTier;
        for (Variant variant : variants) {
            hasTier = false;
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            // SO names and genomic feature
            List<String> soNames;
            GenomicFeature genomicFeature;

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

            if (!hasTier) {

                boolean isActionable = false;
                if (MapUtils.isNotEmpty(actionableVariants)) {
                    isActionable = actionableVariants.containsKey(variant.getAnnotation().getId());
                }

                // Sanity check
                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        soNames = getSoNames(ct);
                        genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(), ct.getGeneName(),
                                null, null);

                        boolean lof = isLoF(soNames);
                        if (lof || isActionable) {
                            List<String> acmg = calculateAcmgClassification(variant, modeOfInheritance);
                            if (lof && CollectionUtils.isNotEmpty(acmg)) {
                                String tier = calculateTierFromACGM(acmg);
                                if (StringUtils.isNotEmpty(tier) || includeUntieredVariants) {
                                    // Report
                                    ReportedEvent reportedEvent = createReportedEvent(disorder, soNames, genomicFeature, null, modeOfInheritance,
                                            penetrance, tier, variant);
                                    reportedEvents.add(reportedEvent);
                                }
                            } else if (isActionable) {
                                // Report
                                ReportedEvent reportedEvent = createReportedEvent(disorder, soNames, genomicFeature, null, modeOfInheritance,
                                        penetrance, null, variant);
                                reportedEvents.add(reportedEvent);
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

    private String calculateTierFromACGM(List<String> acmgValues) {
        // Sanity check
        if (CollectionUtils.isNotEmpty(acmgValues)) {
            boolean isTier2 = false;
            boolean isTier3 = false;
            for (String acmg : acmgValues) {
                if (acmg.startsWith("PVS") || acmg.startsWith("PS")) {
                    // PVS = Very strong evidence of pathogenicity
                    // PS = Strong evidence of pathogenicity
                    isTier2 = true;
                } else if (acmg.startsWith("PM") || acmg.startsWith("PP")) {
                    // PM = Moderate evidence of pathogenicity
                    // PP = Supporting evidence of pathogenicity
                    isTier3 = true;
                }
            }
            // Tier2 > Tier3
            if (isTier2) {
                return TIER_2;
            } else if (isTier3) {
                return TIER_3;
            }
        }
        return null;
    }

    private boolean isLoF(List<String> soNames) {
        // Sanity check
        if (CollectionUtils.isNotEmpty(soNames)) {
            for (String soName : soNames) {
                if (LOF_EXTENDED_SET.contains(soName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
