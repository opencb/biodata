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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TieringReportedVariantCreator extends ReportedVariantCreator {

    private List<DiseasePanel> diseasePanels;
    private Phenotype phenotype;
    private ModeOfInheritance modeOfInheritance;
    private Penetrance penetrance;

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
        Map<String, List<String>> geneToPanelIdMap = getGeneToPanelIdMap(diseasePanels);
        if (MapUtils.isEmpty(geneToPanelIdMap)) {
            throw new InterpretationAnalysisException("Tiering analysis: no genes found in gene panels: "
                    + StringUtils.join(diseasePanels.stream().map(DiseasePanel::getId).collect(Collectors.toList()), ","));
        }

        // Create the list of reported variants, with a reported event for each 1) transcript, 2) panel and 3) consequence type (SO name)
        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            ReportedVariant reportedVariant = new ReportedVariant(variant.getImpl(), 0, new ArrayList<>(),
                    Collections.emptyList(), Collections.emptyMap());

            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                // 1) create the reported event for each transcript
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                    if (geneToPanelIdMap.containsKey(ct.getEnsemblGeneId())) {
                        // Tier 1: variant found in panel
                        // 2) create the reported event for each panel
                        for (String panelId : geneToPanelIdMap.get(ct.getEnsemblGeneId())) {
                            addReportedEvents(ct, panelId, "Tier 1", variant, reportedVariant);
                        }
                    } else {
                        // Tier 2: variant not found in panel
                        addReportedEvents(ct, null, "Tier 2", variant, reportedVariant);
                    }
                }
            }
            // Add reported variant to the list
            reportedVariants.add(reportedVariant);
        }

        return reportedVariants;
    }

    private void addReportedEvents(ConsequenceType ct, String panelId, String tier, Variant variant,
                                                  ReportedVariant reportedVariant) {
        List<String> soNames;
        ReportedEvent reportedEvent;

        GenomicFeature genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(),
                ct.getGeneName(), null, null);

        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
            // 3) create the reported event for consequence type (SO term)
            for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                soNames = null;
                if (StringUtils.isNotEmpty(soTerm.getName())) {
                    soNames = Collections.singletonList(soTerm.getName());
                }
                reportedEvent = createReportedEvent(phenotype, soNames, genomicFeature, panelId, modeOfInheritance,
                        penetrance, variant);
                reportedEvent.setTier(tier);

                // Add reported event to the reported variant
                reportedVariant.getReportedEvents().add(reportedEvent);
            }
        } else {
            // TODO: what to do?
            // No sequence ontoloy terms
            reportedEvent = createReportedEvent(phenotype, null, genomicFeature, panelId, modeOfInheritance,
                    penetrance, variant);
            reportedEvent.setTier(tier);

            // Add reported event to the reported variant
            reportedVariant.getReportedEvents().add(reportedEvent);
        }
    }
}
