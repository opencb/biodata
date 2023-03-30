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
import org.opencb.biodata.models.clinical.ClinicalDiscussion;
import org.opencb.biodata.models.clinical.ClinicalProperty;
import org.opencb.biodata.models.clinical.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.Disorder;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariant;
import org.opencb.biodata.models.clinical.interpretation.ClinicalVariantEvidence;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.clinical.interpretation.GenomicFeature;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.ClinicalProperty.RoleInCancer;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.*;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.extendedLof;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.proteinCoding;

public class TieringClinicalVariantCreator extends ClinicalVariantCreator {

    public static final Set<String> TIER_1_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList(
            "SO:0001893", "transcript_ablation",
            "SO:0001574", "splice_acceptor_variant",
            "SO:0001575", "splice_donor_variant",
            "SO:0001587", "stop_gained",
            "SO:0001589", "frameshift_variant",
            "SO:0001578", "stop_lost",

            "SO:0002012", "start_lost", // to be deleted

            "SO:0001582", "initiator_codon_variant"));

    private static final Set<String> TIER_2_CONSEQUENCE_TYPES_SET = new HashSet<>(Arrays.asList(
            "SO:0001889", "transcript_amplification",
            "SO:0001821", "inframe_insertion",
            "SO:0001822", "inframe_deletion",
            "SO:0001583", "missense_variant",
            "SO:0001630", "splice_region_variant",
            "SO:0001626", "incomplete_terminal_codon_variant"));

    @Deprecated
    public TieringClinicalVariantCreator(List<DiseasePanel> diseasePanels, Map<String, RoleInCancer> roleInCancer, Disorder disorder,
                                         ModeOfInheritance modeOfInheritance, Penetrance penetrance, String assembly) {
        super(diseasePanels, disorder, Collections.singletonList(modeOfInheritance), penetrance, roleInCancer, assembly);
    }

    public TieringClinicalVariantCreator(List<DiseasePanel> diseasePanels, Disorder disorder, ModeOfInheritance modeOfInheritance,
                                         Penetrance penetrance, String assembly) {
        super(diseasePanels, disorder, Collections.singletonList(modeOfInheritance), penetrance, assembly);
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

                    // Only LOF extended SO terms are reported
                    boolean lof = false;
                    for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                        if ((soTerm.getName() != null && extendedLof.contains(soTerm.getName()))
                                || (soTerm.getAccession() != null && extendedLof.contains(soTerm.getAccession()))) {
                            lof = true;
                            break;
                        }
                    }
//                    if (!lof) {
//                        continue;
//                    }

                    GenomicFeature genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), "GENE", ct.getEnsemblTranscriptId(),
                            ct.getGeneName(), ct.getSequenceOntologyTerms(), null);

                    for (DiseasePanel diseasePanel : diseasePanels) {
                        ModeOfInheritance panelMoi = getMoIFromPanel(diseasePanel, ct);
                        if (panelMoi != null && isGreen(diseasePanel, ct)) {
                            for (ModeOfInheritance moi1 : modeOfInheritances) {
                                boolean isDeNovo = false;
                                ModeOfInheritance moi = moi1;
                                if (moi1.name().endsWith("__DE_NOVO")) {
                                    isDeNovo = true;
                                    moi = ModeOfInheritance.valueOf(moi1.name().split("__DE_NOVO")[0]);
                                }
//                                    if (moi == ModeOfInheritance.UNKNOWN) {
//                                        processPanelRegion(genePanel, ct, variant, clinicalVariantEvidences);
//                                    } else
                                if (isCompatible(moi, panelMoi)) {
                                    logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", moi match");

                                    // 3) create the clinical variant evidence for consequence type (SO term)
                                    String tier = null;
                                    if (isDeNovo) {
                                        tier = TIER_1;
                                    } else {
                                        for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                                            if (TIER_1_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                // Tier 1
                                                tier = TIER_1;
                                                break;
                                            } else if (TIER_2_CONSEQUENCE_TYPES_SET.contains(soTerm.getAccession())) {
                                                tier = TIER_2;
                                            }
                                        }
                                    }

                                    if (tier != null) {
                                        clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature,
                                                diseasePanel.getId(), moi, penetrance, tier, variant));
                                    } else {
                                        System.out.println("Something wrong, no so-tier1 nor so-tier2 for " + variant.toStringSimple()
                                                + ": " + ct.getEnsemblTranscriptId());
//                                        System.exit(-1);
                                    }
                                }
                            }
                        } else {
                            // Tier 3
                            logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", not in panel");
                            for (ModeOfInheritance moi1 : modeOfInheritances) {
                                logger.debug(variant.toStringSimple() + ": " + ct.getEnsemblTranscriptId() + ", reported, TIER 3, LOF: ");
                                ModeOfInheritance moi = moi1;
                                if (moi1.name().endsWith("__DE_NOVO")) {
                                    moi = ModeOfInheritance.valueOf(moi1.name().split("__DE_NOVO")[0]);
                                }
                                if (lof && moi != ModeOfInheritance.UNKNOWN) {
                                    clinicalVariantEvidences.add(createClinicalVariantEvidence(genomicFeature, diseasePanel.getId(), moi,
                                            penetrance, TIER_3, variant));
                                }
                            }
                        }
                    }
                }
            }

            // If we have clinical variant evidence, then we have to create the clinical variant
            if (CollectionUtils.isNotEmpty(clinicalVariantEvidences)) {
                logger.debug(variant.toStringSimple() + ": reported, num. evidences: " + clinicalVariantEvidences.size());
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

    private ModeOfInheritance getMoIFromPanel(DiseasePanel diseasePanel, ConsequenceType ct) {
        for (DiseasePanel.GenePanel panelGene : diseasePanel.getGenes()) {
            if (StringUtils.isNotEmpty(ct.getGeneId())) {
                if (ct.getGeneId().equals(panelGene.getId()) || ct.getGeneId().equals(panelGene.getName())) {
                    return panelGene.getModeOfInheritance();
                }
            }
            if (StringUtils.isNotEmpty(ct.getGeneName())) {
                if (ct.getGeneName().equals(panelGene.getId()) || ct.getGeneName().equals(panelGene.getName())) {
                    return panelGene.getModeOfInheritance();
                }
            }
            if (StringUtils.isNotEmpty(ct.getEnsemblGeneId())) {
                if (ct.getEnsemblGeneId().equals(panelGene.getId()) || ct.getEnsemblGeneId().equals(panelGene.getName())) {
                    return panelGene.getModeOfInheritance();
                }
            }
        }
        return null;
    }

    private boolean inDiseasePanel(DiseasePanel diseasePanel, ConsequenceType ct) {
        for (DiseasePanel.GenePanel panelGene : diseasePanel.getGenes()) {
            if (StringUtils.isNotEmpty(ct.getGeneId())) {
                if (ct.getGeneId().equals(panelGene.getId()) || ct.getGeneId().equals(panelGene.getName())) {
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(ct.getGeneName())) {
                if (ct.getGeneName().equals(panelGene.getId()) || ct.getGeneName().equals(panelGene.getName())) {
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(ct.getEnsemblGeneId())) {
                if (ct.getEnsemblGeneId().equals(panelGene.getId()) || ct.getEnsemblGeneId().equals(panelGene.getName())) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isCompatible(ModeOfInheritance variantMoi, ModeOfInheritance panelMoi) {
        if (variantMoi == null || panelMoi == null) {
            return false;
        }
        switch (variantMoi) {
//            case AUTOSOMAL_DOMINANT_NOT_IMPRINTED: {
//                switch (panelMoi) {
//                    case AUTOSOMAL_DOMINANT:
//                    case X_LINKED_DOMINANT:
//                    case X_LINKED_RECESSIVE:
//                    case MITOCHONDRIAL:
//                    case UNKNOWN:
//                        return true;
//                    default:
//                        return false;
//                }
//            }

            case AUTOSOMAL_DOMINANT: {
                switch (panelMoi) {
                    case AUTOSOMAL_DOMINANT:
                    case AUTOSOMAL_DOMINANT_AND_RECESSIVE:
                    case AUTOSOMAL_DOMINANT_AND_MORE_SEVERE_RECESSIVE:
//                    case X_LINKED_RECESSIVE:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case AUTOSOMAL_DOMINANT_NOT_IMPRINTED: {
                switch (panelMoi) {
                    case AUTOSOMAL_DOMINANT_NOT_IMPRINTED:
                    case AUTOSOMAL_DOMINANT:
                    case AUTOSOMAL_DOMINANT_AND_RECESSIVE:
                    case AUTOSOMAL_DOMINANT_AND_MORE_SEVERE_RECESSIVE:
                    case X_LINKED_RECESSIVE:
                    case X_LINKED_DOMINANT:
                    case MITOCHONDRIAL:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case AUTOSOMAL_DOMINANT_PATERNALLY_IMPRINTED: {
                switch (panelMoi) {
                    case AUTOSOMAL_DOMINANT_PATERNALLY_IMPRINTED:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case AUTOSOMAL_DOMINANT_MATERNALLY_IMPRINTED:_: {
                switch (panelMoi) {
                    case AUTOSOMAL_DOMINANT_MATERNALLY_IMPRINTED:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case AUTOSOMAL_RECESSIVE: {
                switch (panelMoi) {
                    case AUTOSOMAL_RECESSIVE:
                    case AUTOSOMAL_DOMINANT_AND_RECESSIVE:
                    case AUTOSOMAL_DOMINANT_AND_MORE_SEVERE_RECESSIVE:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case X_LINKED_DOMINANT: {
                switch (panelMoi) {
                    case X_LINKED_DOMINANT:
//                    case X_LINKED_RECESSIVE:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case X_LINKED_RECESSIVE: {
                switch (panelMoi) {
                    case X_LINKED_RECESSIVE:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }
            case DE_NOVO: {
                switch (panelMoi) {
                    case AUTOSOMAL_DOMINANT:
//                    case AUTOSOMAL_RECESSIVE:
                    case X_LINKED_DOMINANT:
                    case X_LINKED_RECESSIVE:
                    case MITOCHONDRIAL:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }

            }
            case MITOCHONDRIAL: {
                switch (panelMoi) {
                    case MITOCHONDRIAL:
                    case UNKNOWN:
                        return true;
                    default:
                        return false;
                }
            }

            case UNKNOWN: {
                return true;
            }

            default: {
                System.out.println(variantMoi + " vs " + panelMoi);
                System.exit(-1);
            }

        }
        return false;
    }

    private boolean isGreen(DiseasePanel diseasePanel, ConsequenceType ct) {
        for (DiseasePanel.GenePanel panelGene : diseasePanel.getGenes()) {
            boolean matchName = false;
            if (StringUtils.isNotEmpty(ct.getGeneId())) {
                if (ct.getGeneId().equals(panelGene.getId()) || ct.getGeneId().equals(panelGene.getName())) {
                    matchName = true;
                }
            }
            if (StringUtils.isNotEmpty(ct.getGeneName())) {
                if (ct.getGeneName().equals(panelGene.getId()) || ct.getGeneName().equals(panelGene.getName())) {
                    matchName = true;
                }
            }
            if (StringUtils.isNotEmpty(ct.getEnsemblGeneId())) {
                if (ct.getEnsemblGeneId().equals(panelGene.getId()) || ct.getEnsemblGeneId().equals(panelGene.getName())) {
                    matchName = true;
                }
            }

            if (matchName) {
                for (String evidence : panelGene.getEvidences()) {
                    if (evidence.contains("Expert Review Green")) {
                        return true;
                    }
                }
                return (panelGene.getConfidence() == ClinicalProperty.Confidence.HIGH);
            }
        }
        return false;
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
