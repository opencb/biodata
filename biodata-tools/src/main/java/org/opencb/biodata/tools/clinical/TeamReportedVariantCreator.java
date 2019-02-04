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
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.GenomicFeature;
import org.opencb.biodata.models.clinical.interpretation.ReportedEvent;
import org.opencb.biodata.models.clinical.interpretation.ReportedVariant;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.biodata.models.variant.avro.Xref;

import java.util.*;
import java.util.stream.Collectors;

public class TeamReportedVariantCreator extends ReportedVariantCreator {

    private List<DiseasePanel> diseasePanels;
    private Set<String> findings;
    private Phenotype phenotype;
    private ModeOfInheritance modeOfInheritance;
    private Penetrance penetrance;

    public TeamReportedVariantCreator(List<DiseasePanel> diseasePanels) {
        this(diseasePanels, null, null, null, null);
    }

    public TeamReportedVariantCreator(List<DiseasePanel> diseasePanels, Set<String> findings) {
        this(diseasePanels, findings, null, null, null);
    }

    public TeamReportedVariantCreator(List<DiseasePanel> diseasePanels, Set<String> findings, Phenotype phenotype,
                                      ModeOfInheritance modeOfInheritance, Penetrance penetrance) {
        this.diseasePanels = diseasePanels;
        this.findings = findings;
        this.phenotype = phenotype;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) {
        Map<String, List<DiseasePanel.GenePanel>> geneToPanelMap = getGeneToPanelMap(diseasePanels);
        Map<String, List<DiseasePanel.VariantPanel>> variantToPanelMap = getVariantToPanelMap(diseasePanels);

        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            // SO names, panels IDs and genomic feature
            List<String> soNames;
            GenomicFeature genomicFeature;

            if (MapUtils.isNotEmpty(variantToPanelMap) && variantToPanelMap.containsKey(variant.getId())
                    && CollectionUtils.isNotEmpty(variantToPanelMap.get(variant.getId()))) {
                // Tier 1, variant in panel
                List<DiseasePanel.VariantPanel> panels = variantToPanelMap.get(variant.getId());
                List<String> panelIds = panels.stream().map(DiseasePanel.VariantPanel::getId).collect(Collectors.toList());

                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {

                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        soNames = getSoNames(ct);
                        genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(), ct.getGeneName(),
                                null, null);

                        reportedEvents.addAll(createReportedEvents(panelIds, TIER_1, soNames, genomicFeature, variant));
                    }
                } else {
                    // We create the reported events, anyway
                    reportedEvents.addAll(createReportedEvents(panelIds, TIER_1, null, null, variant));
                }
            } else {
                // Check Tier 2 and Tier 3
                boolean isTier2 = false;
                if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {

                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        if (MapUtils.isNotEmpty(geneToPanelMap) && geneToPanelMap.containsKey(ct.getEnsemblGeneId())
                                && CollectionUtils.isNotEmpty(geneToPanelMap.get(ct.getEnsemblGeneId()))) {
                            // Tier 2, gene in panel
                            List<DiseasePanel.GenePanel> panels = geneToPanelMap.get(ct.getEnsemblGeneId());
                            List<String> panelIds = panels.stream().map(DiseasePanel.GenePanel::getId).collect(Collectors.toList());

                            soNames = getSoNames(ct);
                            genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(), ct.getGeneName(),
                                    null, null);

                            reportedEvents.addAll(createReportedEvents(panelIds, TIER_2, soNames, genomicFeature, variant));
                            isTier2 = true;
                        }
                    }
                }

                if (!isTier2 && CollectionUtils.isNotEmpty(findings)) {
                    // Check for findings, i.e.: tier 3
                    boolean isTier3 = false;
                    if (findings.contains(variant.getId())) {
                    } else {
                        if (CollectionUtils.isNotEmpty(variant.getNames())) {
                            // Second, check variant names
                            for (String name : variant.getNames()) {
                                if (findings.contains(name)) {
                                    isTier3 = true;
                                    break;
                                }
                            }
                        } else {
                            // Third, check xrefs for that variant
                            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getXrefs())) {
                                for (Xref xref : variant.getAnnotation().getXrefs()) {
                                    if (StringUtils.isNotEmpty(xref.getId()) && findings.contains(xref.getId())) {
                                        isTier3 = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (isTier3) {
                        // TODO: should we set consequence types and genomic feature for these findings?
                        ReportedEvent reportedEvent = createReportedEvent(phenotype, null, null, null, modeOfInheritance, penetrance,
                                variant);
                        reportedEvent.setTier(TIER_3);


                        reportedEvents.add(reportedEvent);
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

    private List<ReportedEvent> createReportedEvents(List<String> panelIds, String tier, List<String> soAccessions,
                                                     GenomicFeature genomicFeature, Variant variant) {
        List<ReportedEvent> reportedEvents = new ArrayList<>();
        for (String panelId : panelIds) {
            ReportedEvent reportedEvent = createReportedEvent(phenotype, soAccessions, genomicFeature, panelId, modeOfInheritance,
                    penetrance, variant);
            if (reportedEvent != null) {
                reportedEvent.setTier(tier);
                reportedEvents.add(reportedEvent);
            }
        }
        return reportedEvents;
    }

    private List<String> getSoNames(ConsequenceType ct) {
        List<String> soNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
            for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                if (StringUtils.isNotEmpty(soTerm.getName())) {
                    soNames.add(soTerm.getName());
                }
            }
        }
        return soNames;
    }
}
