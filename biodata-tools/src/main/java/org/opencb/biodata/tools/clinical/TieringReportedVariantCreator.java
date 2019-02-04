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
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.GenomicFeature;
import org.opencb.biodata.models.clinical.interpretation.ReportedEvent;
import org.opencb.biodata.models.clinical.interpretation.ReportedVariant;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;

import java.util.*;
import java.util.stream.Collectors;

public class TieringReportedVariantCreator extends ReportedVariantCreator {

    private List<DiseasePanel> diseasePanels;
    private Phenotype phenotype;
    private ModeOfInheritance modeOfInheritance;
    private Penetrance penetrance;

    public static final Set<String> TIER_1_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList("SO:0001893", "SO:0001574", "SO:0001575",
            "SO:0001587", "SO:0001589", "SO:0001578", "SO:0001582"));

    private static final Set<String> TIER_2_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList("SO:0001889", "SO:0001821", "SO:0001822",
            "SO:0001583", "SO:0001630", "SO:0001626"));

    public TieringReportedVariantCreator(List<DiseasePanel> diseasePanels, ModeOfInheritance modeOfInheritance, Penetrance penetrance) {
        this.diseasePanels = diseasePanels;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    public TieringReportedVariantCreator(List<DiseasePanel> diseasePanels, Phenotype phenotype, ModeOfInheritance modeOfInheritance,
                                         Penetrance penetrance) {
        this.diseasePanels = diseasePanels;
        this.phenotype = phenotype;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) throws InterpretationAnalysisException {
        // Panels are mandatory in Tiering analysis
        if (CollectionUtils.isEmpty(diseasePanels)) {
            throw new InterpretationAnalysisException("Missing gene panels for Tiering analysis");
        }
        Map<String, List<DiseasePanel.GenePanel>> geneToPanelMap = getGeneToPanelMap(diseasePanels);

        if (MapUtils.isEmpty(geneToPanelMap)) {
            throw new InterpretationAnalysisException("Tiering analysis: no genes found in gene panels: "
                    + StringUtils.join(diseasePanels.stream().map(DiseasePanel::getId).collect(Collectors.toList()), ","));
        }

        // Create the list of reported variants, with a reported event for each 1) transcript, 2) panel and 3) consequence type (SO name)
        // Tiers classification:
        //     - Tier 1: gene panel + mode of inheritance + TIER_1_CONSEQUENCE_TYPES
        //     - Tier 2: gene panel + mode of inheritance + TIER_2_CONSEQUENCE_TYPES
        //     - Tier 3: gene panel + mode of inheritance + other consequence types
        //               gene panel + mode of inheritance
        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {

                // 1) create the reported event for each transcript
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {

                    if (geneToPanelMap.containsKey(ct.getEnsemblGeneId())) {

                        // 2) create the reported event for each panel
                        List<DiseasePanel.GenePanel> genePanels = geneToPanelMap.get(ct.getEnsemblGeneId());
                        for (DiseasePanel.GenePanel genePanel : genePanels) {

                            // In addition to the panel, the mode of inheritance must match too!
                            if (StringUtils.isNotEmpty(genePanel.getModeOfInheritance()) && modeOfInheritance != null
                                    && ModeOfInheritance.valueOf(genePanel.getModeOfInheritance()) == modeOfInheritance) {

                                GenomicFeature genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(),
                                        ct.getGeneName(), null, null);

                                if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {

                                    // 3) create the reported event for consequence type (SO term)
                                    for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {

                                        if (StringUtils.isNotEmpty(soTerm.getAccession())) {
                                            if (TIER_1_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                // Tier 1
                                                reportedEvents.add(createReportedEvent(phenotype, getSoNameAsList(soTerm), genomicFeature,
                                                        genePanel.getId(), modeOfInheritance, penetrance, variant).setTier(TIER_1));
                                            } else if (TIER_2_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                // Tier 2
                                                reportedEvents.add(createReportedEvent(phenotype, getSoNameAsList(soTerm), genomicFeature,
                                                        genePanel.getId(), modeOfInheritance, penetrance, variant).setTier(TIER_2));
                                            } else {
                                                // Tier 3
                                                reportedEvents.add(createReportedEvent(phenotype, getSoNameAsList(soTerm), genomicFeature,
                                                        genePanel.getId(), modeOfInheritance, penetrance, variant).setTier(TIER_3));
                                            }
                                        } else {
                                            // Tier 3
                                            reportedEvents.add(createReportedEvent(phenotype, getSoNameAsList(soTerm), genomicFeature,
                                                    genePanel.getId(), modeOfInheritance, penetrance, variant).setTier(TIER_3));
                                        }
                                    }
                                } else {
                                    // Tier 3
                                    reportedEvents.add(createReportedEvent(phenotype, null, genomicFeature, genePanel.getId(),
                                            modeOfInheritance, penetrance, variant).setTier(TIER_3));
                                }
                            }
                        }
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

    private List<String> getSoNameAsList(SequenceOntologyTerm soTerm) {
        if (soTerm != null && StringUtils.isNotEmpty(soTerm.getName())) {
            return Collections.singletonList(soTerm.getName());
        }
        return null;
    }
}
