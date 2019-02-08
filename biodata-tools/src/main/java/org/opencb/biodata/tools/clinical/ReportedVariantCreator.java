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
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.EvidenceEntry;

import java.util.*;

import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.ClinicalSignificance;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.calculateAcmgClassification;

public abstract class ReportedVariantCreator {

    protected boolean includeNoTier = false;

    public final String TIER_1 = "Tier1";
    public final String TIER_2 = "Tier2";
    public final String TIER_3 = "Tier3";

    public static final Set<String> LOF_SET = new HashSet<>(Arrays.asList("transcript_ablation", "splice_acceptor_variant",
            "splice_donor_variant", "stop_gained", "frameshift_variant", "stop_lost", "start_lost", "transcript_amplification",
            "inframe_insertion", "inframe_deletion"));

    public static final Set<String> LOF_EXTENDED_SET = new HashSet<>(Arrays.asList("transcript_ablation", "splice_acceptor_variant",
            "splice_donor_variant", "stop_gained", "frameshift_variant", "stop_lost", "start_lost", "initiator_codon_variant",
            "transcript_amplification", "inframe_insertion", "inframe_deletion", "missense_variant", "splice_region_variant",
            "incomplete_terminal_codon_variant"));

    public abstract List<ReportedVariant> create(List<Variant> variants) throws InterpretationAnalysisException;

    protected Map<String, List<DiseasePanel.GenePanel>> getGeneToPanelMap(List<DiseasePanel> diseasePanels) {
        Map<String, List<DiseasePanel.GenePanel>> idToPanelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel : diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.GenePanel panelGene : panel.getGenes()) {
                        if (panelGene.getId() != null) {
                            if (!idToPanelMap.containsKey(panelGene.getId())) {
                                idToPanelMap.put(panelGene.getId(), new ArrayList<>());
                            }
                            idToPanelMap.get(panelGene.getId()).add(panelGene);
                        }
                    }
                }
            }
        }
        return idToPanelMap;
    }

    protected Map<String, List<DiseasePanel.VariantPanel>> getVariantToPanelMap(List<DiseasePanel> diseasePanels) {
        Map<String, List<DiseasePanel.VariantPanel>> idToPanelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel : diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.VariantPanel variantPanel : panel.getVariants()) {
                        if (variantPanel.getId() != null) {
                            if (!idToPanelMap.containsKey(variantPanel.getId())) {
                                idToPanelMap.put(variantPanel.getId(), new ArrayList<>());
                            }
                            idToPanelMap.get(variantPanel.getId()).add(variantPanel);
                        }
                    }
                }
            }
        }
        return idToPanelMap;
    }

    protected ReportedEvent createReportedEvent(Phenotype phenotype, List<String> soNames, GenomicFeature genomicFeature, String panelId,
                                                ClinicalProperty.ModeOfInheritance moi, ClinicalProperty.Penetrance penetrance,
                                                Variant variant) {
        ReportedEvent reportedEvent = new ReportedEvent().setId("OPENCB-" + UUID.randomUUID());

        if (phenotype != null) {
            reportedEvent.setPhenotypes(Collections.singletonList(phenotype));
        }
        if (CollectionUtils.isNotEmpty(soNames)) {
            // Set consequence type
            reportedEvent.setConsequenceTypeIds(soNames);
        }
        if (genomicFeature != null) {
            reportedEvent.setGenomicFeature(genomicFeature);
        }
        if (panelId != null) {
            reportedEvent.setPanelId(panelId);
        }
        if (moi != null) {
            reportedEvent.setModeOfInheritance(moi);
        }
        if (penetrance != null) {
            reportedEvent.setPenetrance(penetrance);
        }

        updateVariantClassification(reportedEvent, variant);

        return reportedEvent;
    }

    public void updateVariantClassification(ReportedEvent reportedEvent, Variant variant) {
        List<String> acmg = calculateAcmgClassification(variant, reportedEvent);
        VariantClassification variantClassification = new VariantClassification().setAcmg(acmg);

        // Clinical significance management, this is stored in the trait association
        if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getTraitAssociation())) {
            List<EvidenceEntry> traitAssociations = variant.getAnnotation().getTraitAssociation();
            String ensemblTranscriptId = reportedEvent.getGenomicFeature().getEnsemblTranscriptId();

            // Iterate across the list of evidences
            for (EvidenceEntry evidenceEntry : traitAssociations) {

                // We are interested in the clinical significance from the clinvar annotation
                if (evidenceEntry.getSource() != null && "clinvar".equals(evidenceEntry.getSource().getName())) {

                    if (evidenceEntry.getVariantClassification() != null
                            && evidenceEntry.getVariantClassification().getClinicalSignificance() != null
                            && evidenceEntry.getVariantClassification().getClinicalSignificance().name() != null) {
                        String clinicalSignificance = evidenceEntry.getVariantClassification().getClinicalSignificance().name();
                        List<org.opencb.biodata.models.variant.avro.GenomicFeature> genomicFeatures = evidenceEntry.getGenomicFeatures();

                        // And check if we are in the proper reported event, i.e., the transcript matches
                        if (CollectionUtils.isNotEmpty(genomicFeatures)) {
                            for (org.opencb.biodata.models.variant.avro.GenomicFeature genomicFeature : genomicFeatures) {
                                if (genomicFeature.getFeatureType() != null && "transcript".equals(genomicFeature.getFeatureType().name())
                                        && ensemblTranscriptId.equals(genomicFeature.getEnsemblId())) {

                                    try {
                                        variantClassification.setClinicalSignificance(ClinicalSignificance.valueOf(clinicalSignificance));
                                        break;
                                    } catch (IllegalArgumentException e) {
                                        // Do nothing
                                    }
                                }
                            }
                        }
                    }
                }
                if (variantClassification.getClinicalSignificance() != null) {
                    break;
                }
            }
        }

        reportedEvent.setClassification(variantClassification);
    }

    public boolean isIncludeNoTier() {
        return includeNoTier;
    }

    public ReportedVariantCreator setIncludeNoTier(boolean includeNoTier) {
        this.includeNoTier = includeNoTier;
        return this;
    }
}
