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
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.biodata.models.variant.avro.Xref;
import org.opencb.commons.utils.ListUtils;

import java.util.*;

public class DefaultReportedVariantCreator extends ReportedVariantCreator {

    private List<DiseasePanel> diseasePanels;
    private Set<String> findings;
    private Phenotype phenotype;
    private ModeOfInheritance modeOfInheritance;
    private Penetrance penetrance;

    public DefaultReportedVariantCreator(List<DiseasePanel> diseasePanels) {
        this(diseasePanels, null, null, null, null);
    }

    public DefaultReportedVariantCreator(List<DiseasePanel> diseasePanels, Set<String> findings) {
        this(diseasePanels, findings, null, null, null);
    }

    public DefaultReportedVariantCreator(List<DiseasePanel> diseasePanels, Set<String> findings, Phenotype phenotype,
                                         ModeOfInheritance modeOfInheritance, Penetrance penetrance) {
        this.diseasePanels = diseasePanels;
        this.findings = findings;
        this.phenotype = phenotype;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) {
        Map<String, List<String>> idToPanelIdMap = getIdToPanelIdMap(diseasePanels);

        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            String tier = "";
            String panelId = null;

            // Get SO names and genomic feature
            GenomicFeature genomicFeature = null;
            List<ConsequenceType> cts = null;
            List<String> soNames = new ArrayList<>();
            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                cts = variant.getAnnotation().getConsequenceTypes();
                for (ConsequenceType ct : cts) {
                    if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                        for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                            if (StringUtils.isNotEmpty(soTerm.getName())) {
                                soNames.add(soTerm.getName());
                            }
                        }
                    }
                }

                ConsequenceType ct = cts.get(0); // we take the first
                genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(), ct.getGeneName(), null, null);
            }

            if (MapUtils.isNotEmpty(idToPanelIdMap) && idToPanelIdMap.containsKey(variant.getId())) {
                // Tier 1
                tier = "Tier1";
                panelId = idToPanelIdMap.get(variant.getId()).get(0);
            } else {
                if (CollectionUtils.isNotEmpty(cts)) {
                    // Create the reported event for each consequence type
                    for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                        if (idToPanelIdMap.containsKey(ct.getEnsemblGeneId())) {
                            // Tier 2
                            tier = "Tier2";
                            panelId = idToPanelIdMap.get(ct.getEnsemblGeneId()).get(0);
                            break;
                        }
                    }
                }
            }

            // Tier 3
            if (StringUtils.isEmpty(tier) && CollectionUtils.isNotEmpty(findings)) {
                // First, check variant ID
                if (findings.contains(variant.getId())) {
                    tier = "Tier3";
                } else {
                    if (CollectionUtils.isNotEmpty(variant.getNames())) {
                        // Second, check variant names
                        for (String name : variant.getNames()) {
                            if (findings.contains(name)) {
                                tier = "Tier3";
                                break;
                            }
                        }
                    } else {
                        // Third, check xrefs for that variant
                        if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getXrefs())) {
                            for (Xref xref : variant.getAnnotation().getXrefs()) {
                                if (StringUtils.isNotEmpty(xref.getId()) && findings.contains(xref.getId())) {
                                    tier = "Tier3";
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            ReportedVariant reportedVariant = new ReportedVariant(variant.getImpl(), 0, new ArrayList<>(),
                    Collections.emptyList(), Collections.emptyMap());

            ReportedEvent reportedEvent = createReportedEvent(phenotype, soNames, genomicFeature, panelId, modeOfInheritance, penetrance,
                    variant);
            if (StringUtils.isNotEmpty(tier)) {
                reportedEvent.setTier(tier);
            }

            // Add reported event to the reported variant
            reportedVariant.getReportedEvents().add(reportedEvent);

            // Add variant to the list
            reportedVariants.add(reportedVariant);
        }
        return reportedVariants;
    }
}
