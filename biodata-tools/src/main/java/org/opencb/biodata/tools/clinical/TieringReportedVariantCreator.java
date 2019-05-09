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
import org.opencb.biodata.models.commons.Disorder;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance.UNKNOWN;
import static org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.RoleInCancer;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.extendedLof;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.proteinCoding;

public class TieringReportedVariantCreator extends ReportedVariantCreator {

    public static final Set<String> TIER_1_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList("SO:0001893", "transcript_ablation",
            "SO:0001574", "splice_acceptor_variant", "SO:0001575", "splice_donor_variant", "SO:0001587", "stop_gained",
            "SO:0001589", "frameshift_variant", "SO:0001578", "stop_lost", "SO:0001582", "initiator_codon_variant"));

    private static final Set<String> TIER_2_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList("SO:0001889", "transcript_amplification",
            "SO:0001821", "inframe_insertion", "SO:0001822", "inframe_deletion", "SO:0001583", "missense_variant",
            "SO:0001630", "splice_region_variant", "SO:0001626", "incomplete_terminal_codon_variant"));

    public TieringReportedVariantCreator(List<DiseasePanel> diseasePanels, Map<String, RoleInCancer> roleInCancer,
                                         Map<String, List<String>> actionableVariants, Disorder disorder,
                                         ModeOfInheritance modeOfInheritance, Penetrance penetrance, String assembly) {
        super(diseasePanels, disorder, modeOfInheritance, penetrance, roleInCancer, actionableVariants, assembly);
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) throws InterpretationAnalysisException {
        Map<String, List<ModeOfInheritance>> moiMap = new HashMap<>();
        for (Variant variant : variants) {
            moiMap.put(variant.getId(), modeOfInheritance != null ? Collections.singletonList(modeOfInheritance) : Collections.emptyList());
        }
        return create(variants, moiMap);
    }

    public List<ReportedVariant> create(List<Variant> variants, Map<String, List<ModeOfInheritance>> variantMoIMap)
            throws InterpretationAnalysisException {
        // Sanity check
        if (variants == null || variants.isEmpty()) {
            return Collections.emptyList();
        }

        // Panels are mandatory in Tiering analysis
        if (CollectionUtils.isEmpty(diseasePanels)) {
            throw new InterpretationAnalysisException("Missing gene panels for Tiering analysis");
        }
        Map<String, Set<DiseasePanel>> geneToPanelMap = getGeneToPanelMap(diseasePanels);

        if (MapUtils.isEmpty(geneToPanelMap)) {
            throw new InterpretationAnalysisException("Tiering analysis: no genes found in gene panels: "
                    + StringUtils.join(diseasePanels.stream().map(DiseasePanel::getId).collect(Collectors.toList()), ","));
        }

        //   Gene        Panel       Moi
        Map<String, Map<String, ModeOfInheritance>> geneToPanelMoiMap = getGeneToPanelMoiMap(diseasePanels);

        // Create the list of reported variants, with a reported event for each 1) transcript, 2) panel and 3) consequence type (SO name)
        // Tiers classification:
        //     - Tier 1: gene panel + mode of inheritance + TIER_1_CONSEQUENCE_TYPES
        //     - Tier 2: gene panel + mode of inheritance + TIER_2_CONSEQUENCE_TYPES
        //     - Tier 3: gene panel + mode of inheritance + other consequence types
        //               gene panel + mode of inheritance
        //               not in panel
        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {

            List<ReportedEvent> reportedEvents = new ArrayList<>();
            List<ModeOfInheritance> modeOfInheritances = variantMoIMap.get(variant.getId());

            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {

                // 1) create the reported event for each transcript
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {

                    // Only protein coding
                    if (ct.getBiotype() == null || !proteinCoding.contains(ct.getBiotype())) {
                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", discarded, biotype: "
                                + ct.getBiotype());
                        continue;
                    }

                    GenomicFeature genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), "GENE", ct.getEnsemblTranscriptId(),
                            ct.getGeneName(), null);

                    if (geneToPanelMap.containsKey(ct.getEnsemblGeneId())) {
                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", gene in panel");

                        // 2) create the reported event for each panel
                        Set<DiseasePanel> genePanels = geneToPanelMap.get(ct.getEnsemblGeneId());
                        for (DiseasePanel genePanel : genePanels) {
                            // In addition to the panel, the mode of inheritance must match too!
                            if (geneToPanelMoiMap.containsKey(ct.getEnsemblGeneId())) {
                                for (ModeOfInheritance moi : modeOfInheritances) {
                                    if (geneToPanelMoiMap.get(ct.getEnsemblGeneId()).get(genePanel.getId()) == moi) {
                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", moi match");

                                        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {

                                            // 3) create the reported event for consequence type (SO term)
                                            for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {

                                                // Only LOF extended SO terms are reported
                                                if ((soTerm.getName() != null && !extendedLof.contains(soTerm.getName()))
                                                        || (soTerm.getAccession() != null
                                                        && !extendedLof.contains(soTerm.getAccession()))) {
                                                    logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                            + ", discarded, LOF: " + soTerm.getName());
                                                    continue;
                                                }

                                                if (StringUtils.isNotEmpty(soTerm.getAccession())) {
                                                    if (TIER_1_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                        // Tier 1
                                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                                + ", reported, TIER 1, " + soTerm.getName());
                                                        reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                                                genomicFeature, genePanel.getId(), moi, penetrance, TIER_1, variant));
                                                    } else if (TIER_2_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                        // Tier 2
                                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                                + ", reported, TIER 2, " + soTerm.getName());
                                                        reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                                                genomicFeature, genePanel.getId(), moi, penetrance, TIER_2, variant));
                                                    } else {
                                                        // Tier 3
                                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                                + ", reported, TIER 3, " + soTerm.getName());
                                                        reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                                                genomicFeature, genePanel.getId(), moi, penetrance, TIER_3, variant));
                                                    }
                                                } else {
                                                    // Tier 3
                                                    logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                            + ", reported, TIER 3, empty SO");
                                                    reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                                            genomicFeature, genePanel.getId(), moi, penetrance, TIER_3, variant));
                                                }
                                            }
                                        } else {
                                            // Tier 3
                                            logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", reported, "
                                                    + "TIER 3, empty SO list");
                                            reportedEvents.add(createReportedEvent(disorder, null, genomicFeature,
                                                    genePanel.getId(), moi, penetrance, TIER_3, variant));
                                        }
                                    } else {
                                        if (geneToPanelMoiMap.get(ct.getEnsemblGeneId()).get(genePanel.getId()) == UNKNOWN) {
                                            // Tier 3
                                            logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", reported,"
                                                    + " TIER 3, UNKNOWN moi");
                                            if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                                                for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                                                    // Only LOF extended SO terms are reported
                                                    if ((soTerm.getName() != null && !extendedLof.contains(soTerm.getName()))
                                                            || (soTerm.getAccession() != null
                                                            && !extendedLof.contains(soTerm.getAccession()))) {
                                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                                + ", discarded, LOF: " + soTerm.getName());
                                                        continue;
                                                    }
                                                    logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                            + ", reported, TIER 3");
                                                    reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                                            genomicFeature, genePanel.getId(), moi, penetrance, TIER_3, variant));
                                                }
                                            } else {
                                                logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", discarded,"
                                                        + " moi mismatch " + moi.name() + " vs panel gene moi "
                                                        + geneToPanelMoiMap.get(ct.getEnsemblGeneId()).get(genePanel.getId()).name());
                                            }
                                        }
                                    }
                                }
                            } else {
                                logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", moi missing (UNTIERED)");
                                for (ModeOfInheritance moi : modeOfInheritances) {
                                    if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                                        for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                                            // Only LOF extended SO terms are reported
                                            if ((soTerm.getName() != null && !extendedLof.contains(soTerm.getName()))
                                                    || (soTerm.getAccession() != null && !extendedLof.contains(soTerm.getAccession()))) {
                                                logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                        + ", discarded, LOF: " + soTerm.getName());
                                                continue;
                                            }
                                            logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                    + ", reported, UNTIERED, LOF: " + soTerm.getName());
                                            reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                                    genomicFeature, null, moi, penetrance, "", variant));
                                        }
                                    } else {
                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                + ", reported, UNTIERED, missing LOF");
                                        reportedEvents.add(createReportedEvent(disorder, null,
                                                genomicFeature, null, moi, penetrance, "", variant));
                                    }
                                }
                            }
                        }
                    } else {
                        // Tier 3
                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", not in panel");
                        for (ModeOfInheritance moi : modeOfInheritances) {
                            if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
                                for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                                    // Only LOF extended SO terms are reported
                                    if ((soTerm.getName() != null && !extendedLof.contains(soTerm.getName()))
                                            || (soTerm.getAccession() != null && !extendedLof.contains(soTerm.getAccession()))) {
                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", discarded, LOF: "
                                                + soTerm.getName());
                                        continue;
                                    }

                                    logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", reported, TIER 3, LOF: "
                                            + soTerm.getName());
                                    reportedEvents.add(createReportedEvent(disorder, Collections.singletonList(soTerm),
                                            genomicFeature, null, moi, penetrance, TIER_3, variant));
                                }
                            } else {
                                logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                        + ", reported, TIER 3, missing LOF");
                                reportedEvents.add(createReportedEvent(disorder, null,
                                        genomicFeature, null, moi, penetrance, TIER_3, variant));
                            }
                        }
                    }
                }
            }

            // If we have reported events, then we have to create the reported variant
            if (CollectionUtils.isNotEmpty(reportedEvents)) {
                logger.debug(variant.toStringSimple() + ": reported, num. events: " + reportedEvents.size());
                ReportedVariant reportedVariant = new ReportedVariant(variant.getImpl(), 0, new ArrayList<>(),
                        Collections.emptyList(), ReportedVariant.Status.NOT_REVIEWED, Collections.emptyMap());
                reportedVariant.setEvidences(reportedEvents);

                // Add variant to the list
                reportedVariants.add(reportedVariant);
            }
        }

        return reportedVariants;
    }


}
