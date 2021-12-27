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
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.clinical.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariant;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.GenomicFeature;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariantEvidence;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.clinical.Disorder;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.ClinicalProperty.ModeOfInheritance.UNKNOWN;
import static org.opencb.biodata.models.clinical.ClinicalProperty.RoleInCancer;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.*;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.extendedLof;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.proteinCoding;

public class TieringClinicalVariantCreator extends ClinicalVariantCreator {

    public static final Set<String> TIER_1_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList("SO:0001893", "transcript_ablation",
            "SO:0001574", "splice_acceptor_variant", "SO:0001575", "splice_donor_variant", "SO:0001587", "stop_gained",
            "SO:0001589", "frameshift_variant", "SO:0001578", "stop_lost", "SO:0001582", "initiator_codon_variant"));

    private static final Set<String> TIER_2_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList("SO:0001889", "transcript_amplification",
            "SO:0001821", "inframe_insertion", "SO:0001822", "inframe_deletion", "SO:0001583", "missense_variant",
            "SO:0001630", "splice_region_variant", "SO:0001626", "incomplete_terminal_codon_variant"));

    public TieringClinicalVariantCreator(List<DiseasePanel> diseasePanels, Map<String, RoleInCancer> roleInCancer,
                                         Map<String, List<String>> actionableVariants, Disorder disorder,
                                         ModeOfInheritance modeOfInheritance, Penetrance penetrance, String assembly) {
        super(diseasePanels, disorder, Collections.singletonList(modeOfInheritance), penetrance, roleInCancer,
                actionableVariants, assembly);
    }

    @Override
    public List<ClinicalVariant> create(List<Variant> variants) throws InterpretationAnalysisException {
        Map<String, List<ModeOfInheritance>> moiMap = new HashMap<>();
        for (Variant variant : variants) {
            moiMap.put(variant.getId(), modeOfInheritances != null ? modeOfInheritances : Collections.emptyList());
        }
        return create(variants, moiMap);
    }

    public List<ClinicalVariant> create(List<Variant> variants, Map<String, List<ModeOfInheritance>> variantMoIMap)
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

        // Create the list of clinical variants, with a evidence event for each 1) transcript, 2) panel and 3) consequence type (SO name)
        // Tiers classification:
        //     - Tier 1: gene panel + mode of inheritance + TIER_1_CONSEQUENCE_TYPES
        //     - Tier 2: gene panel + mode of inheritance + TIER_2_CONSEQUENCE_TYPES
        //     - Tier 3: gene panel + mode of inheritance + other consequence types
        //               gene panel + mode of inheritance
        //               not in panel
        List<ClinicalVariant> clinicalVariants = new ArrayList<>();
        for (Variant variant : variants) {

            List<ClinicalVariantEvidence> clinicalVariantEvidences = new ArrayList<>();
            List<ModeOfInheritance> modeOfInheritances = variantMoIMap.get(variant.getId());

            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {

                // 1) create the clinical variant evidence for each transcript
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {

                    // Only protein coding
                    if (StringUtils.isEmpty(ct.getBiotype()) || !proteinCoding.contains(ct.getBiotype())) {
                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", discarded, biotype: "
                                + ct.getBiotype());
                        continue;
                    }

                    GenomicFeature genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), "GENE", ct.getEnsemblTranscriptId(),
                            ct.getGeneName(), ct.getSequenceOntologyTerms(), null);

                    if (geneToPanelMap.containsKey(ct.getEnsemblGeneId())) {
                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", gene in panel");

                        // 2) create the clinical variant evidence for each panel
                        Set<DiseasePanel> genePanels = geneToPanelMap.get(ct.getEnsemblGeneId());
                        for (DiseasePanel genePanel : genePanels) {
                            // In addition to the panel, the mode of inheritance must match too!
                            if (geneToPanelMoiMap.containsKey(ct.getEnsemblGeneId())) {
                                for (ModeOfInheritance moi : modeOfInheritances) {
                                    if (moi == ModeOfInheritance.UNKNOWN) {
                                        processPanelRegion(genePanel, ct, variant, clinicalVariantEvidences);
                                    } else if (geneToPanelMoiMap.get(ct.getEnsemblGeneId()).get(genePanel.getId()) == moi) {
                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", moi match");

                                        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {

                                            // 3) create the clinical variant evidence for consequence type (SO term)
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
                                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                                genePanel.getId(), moi, penetrance, TIER_1, variant));
                                                    } else if (TIER_2_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                        // Tier 2
                                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                                + ", reported, TIER 2, " + soTerm.getName());
                                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                                genePanel.getId(), moi, penetrance, TIER_2, variant));
                                                    } else {
                                                        // Tier 3
                                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                                + ", reported, TIER 3, " + soTerm.getName());
                                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                                genePanel.getId(), moi, penetrance, TIER_3, variant));
                                                    }
                                                } else {
                                                    // Tier 3
                                                    logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                            + ", reported, TIER 3, empty SO");
                                                    clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                            genePanel.getId(), moi, penetrance, TIER_3, variant));
                                                }
                                            }
                                        } else {
                                            // Tier 3
                                            logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", reported, "
                                                    + "TIER 3, empty SO list");
                                            clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature, genePanel.getId(),
                                                    moi, penetrance, TIER_3, variant));
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
                                                    clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                            genePanel.getId(), moi, penetrance, TIER_3, variant));
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
                                            clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                    genePanel.getId(), moi, penetrance, "", variant));
                                        }
                                    } else {
                                        logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                                + ", reported, UNTIERED, missing LOF");
                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                genePanel.getId(), moi, penetrance, "", variant));
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
                                    clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature, null, moi, penetrance,
                                            TIER_3, variant));
                                }
                            } else {
                                logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId()
                                        + ", reported, TIER 3, missing LOF");
                                clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature, null, moi, penetrance,
                                        TIER_3, variant));
                            }
                        }
                    }
                }
            }

            // If we have clinical variant evidence, then we have to create the clinical variant
            if (CollectionUtils.isNotEmpty(clinicalVariantEvidences)) {
                logger.debug(variant.toStringSimple() + ": reported, num. evidences: " + clinicalVariantEvidences.size());
                ClinicalVariant clinicalVariant = new ClinicalVariant(variant.getImpl(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyMap(), "", ClinicalVariant.Status.NOT_REVIEWED, Collections.emptyMap());
                clinicalVariant.setEvidences(clinicalVariantEvidences);

                // Add variant to the list
                clinicalVariants.add(clinicalVariant);
            }
        }

        return clinicalVariants;
    }

    private void processPanelRegion(DiseasePanel genePanel, ConsequenceType ct, Variant variant,
                                    List<ClinicalVariantEvidence> clinicalVariantEvidences) {
        if (genePanel != null && CollectionUtils.isNotEmpty(genePanel.getRegions())) {
            for (DiseasePanel.RegionPanel panelRegion : genePanel.getRegions()) {
                if (CollectionUtils.isNotEmpty(genePanel.getRegions())) {
                    for (DiseasePanel.Coordinate coordinate : panelRegion.getCoordinates()) {
                        if (assembly.equals(coordinate.getAssembly())) {
                            if (StringUtils.isNotEmpty(coordinate.getLocation())) {
                                Region region = Region.parseRegion(coordinate.getLocation());
                                GenomicFeature genomicFeature = new GenomicFeature(region.toString(), "REGION",
                                        ct.getEnsemblTranscriptId(), ct.getGeneName(), Collections.emptyList(), panelRegion.getXrefs());

                                int overlapPercentage = getOverlapPercentage(region, variant);
                                if (overlapPercentage >= panelRegion.getRequiredOverlapPercentage()) {
                                    for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature, genePanel.getId(),
                                                ModeOfInheritance.UNKNOWN, penetrance, TIER_1, variant));
                                    }
                                } else {
                                    for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature, genePanel.getId(),
                                                ModeOfInheritance.UNKNOWN, penetrance, TIER_2, variant));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int getOverlapPercentage(Region region, Variant variant) {
        int start = Math.max(region.getStart(), variant.getStart());
        int end = Math.min(region.getEnd(), variant.getEnd());
        return 100 * (end - start + 1) / region.size();
    }
}
