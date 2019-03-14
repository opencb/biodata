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
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.commons.Disorder;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.EvidenceEntry;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;
import static org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.RoleInCancer;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.ClinicalSignificance;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.calculateAcmgClassification;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.extendedLof;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.proteinCoding;

public abstract class ReportedVariantCreator {

    protected Set<String> biotypeSet;
    protected Set<String> soNameSet;

    public final String TIER_1 = "Tier1";
    public final String TIER_2 = "Tier2";
    public final String TIER_3 = "Tier3";

    // logger
    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    protected List<DiseasePanel> diseasePanels;
    protected Disorder disorder;
    protected ModeOfInheritance modeOfInheritance;
    protected Penetrance penetrance;

    protected Map<String, RoleInCancer> roleInCancer;
    protected Map<String, List<String>> actionableVariants;

    public ReportedVariantCreator(List<DiseasePanel> diseasePanels, Disorder disorder, ModeOfInheritance modeOfInheritance,
                                  Penetrance penetrance, Map<String, RoleInCancer> roleInCancer,
                                  Map<String, List<String>> actionableVariants) {
        this(diseasePanels, disorder, modeOfInheritance, penetrance, roleInCancer, actionableVariants,
                new ArrayList<>(proteinCoding), new ArrayList<>(extendedLof));
    }

    public ReportedVariantCreator(List<DiseasePanel> diseasePanels, Disorder disorder, ModeOfInheritance modeOfInheritance,
                                  Penetrance penetrance, Map<String, RoleInCancer> roleInCancer,
                                  Map<String, List<String>> actionableVariants, List<String> biotypes, List<String> soNames) {

        this.diseasePanels = diseasePanels;
        this.disorder = disorder;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
        this.roleInCancer = roleInCancer;
        this.actionableVariants = actionableVariants;

        this.biotypeSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(biotypes)) {
            biotypeSet.addAll(biotypes);
        }
        this.soNameSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(soNames)) {
            soNameSet.addAll(soNames);
        }

    }

    public abstract List<ReportedVariant> create(List<Variant> variants) throws InterpretationAnalysisException;

    public List<ReportedVariant> create(List<Variant> variants, ModeOfInheritance moi) throws InterpretationAnalysisException {
        this.modeOfInheritance = moi;
        return create(variants);
    }

    public List<ReportedVariant> createSecondaryFindings(List<Variant> variants) {
        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            // Tier 3, actionable variants
            if (MapUtils.isNotEmpty(actionableVariants)) {
                if (variant.getAnnotation() != null && actionableVariants.containsKey(variant.toString())) {
                    if (CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            reportedEvents.addAll(createReportedEvents("", null, ct, variant));
                        }
                    } else {
                        // We create the reported events anyway!
                        reportedEvents.addAll(createReportedEvents("", null, null, variant));
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

    protected Map<String, Set<DiseasePanel>> getVariantToPanelMap(List<DiseasePanel> diseasePanels) {
        Map<String, Set<DiseasePanel>> idToPanelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel : diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.VariantPanel variantPanel : panel.getVariants()) {
                        if (variantPanel.getId() != null) {
                            if (!idToPanelMap.containsKey(variantPanel.getId())) {
                                idToPanelMap.put(variantPanel.getId(), new HashSet<>());
                            }
                            idToPanelMap.get(variantPanel.getId()).add(panel);
                        }
                    }
                }
            }
        }
        return idToPanelMap;
    }

    protected Map<String, Set<DiseasePanel>> getGeneToPanelMap(List<DiseasePanel> diseasePanels) {
        Map<String, Set<DiseasePanel>> idToPanelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel : diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.GenePanel genePanel : panel.getGenes()) {
                        if (genePanel.getId() != null) {
                            if (!idToPanelMap.containsKey(genePanel.getId())) {
                                idToPanelMap.put(genePanel.getId(), new HashSet<>());
                            }
                            idToPanelMap.get(genePanel.getId()).add(panel);
                        }
                    }
                }
            }
        }
        return idToPanelMap;
    }

    protected Map<String, Map<String, ClinicalProperty.ModeOfInheritance>> getGeneToPanelMoiMap(List<DiseasePanel> diseasePanels) {
        // Map<Ensembl gene ID, Map<Panel Id, MoI>>
        Map<String, Map<String, ClinicalProperty.ModeOfInheritance>> idToPanelMoiMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel : diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.GenePanel panelGene : panel.getGenes()) {
                        if (StringUtils.isNotEmpty(panelGene.getId()) && StringUtils.isNotEmpty(panelGene.getModeOfInheritance())) {
                            if (!idToPanelMoiMap.containsKey(panelGene.getId())) {
                                idToPanelMoiMap.put(panelGene.getId(), new HashMap());
                            }
                            idToPanelMoiMap.get(panelGene.getId()).put(panel.getId(), getMoiFromGenePanel(panelGene.getModeOfInheritance()));
                        }
                    }
                }
            }
        }
        return idToPanelMoiMap;
    }

    protected ReportedEvent createReportedEvent(Disorder disorder, List<SequenceOntologyTerm> consequenceTypes, GenomicFeature genomicFeature, String panelId,
                                                ModeOfInheritance moi, Penetrance penetrance, String tier, Variant variant) {
        ReportedEvent reportedEvent = new ReportedEvent().setId("OPENCB-" + UUID.randomUUID());

        // Disorder
        if (disorder != null) {
            reportedEvent.setDisorder(disorder);
        }

        // Consequence types
        if (CollectionUtils.isNotEmpty(consequenceTypes)) {
            // Set consequence type
            reportedEvent.setConsequenceTypes(consequenceTypes);
        }

        // Genomic feature
        if (genomicFeature != null) {
            reportedEvent.setGenomicFeature(genomicFeature);
        }

        // Panel ID
        if (panelId != null) {
            reportedEvent.setPanelId(panelId);
        }

        // Mode of inheritance
        if (moi != null) {
            reportedEvent.setModeOfInheritance(moi);
        }

        // Penetrance
        if (penetrance != null) {
            reportedEvent.setPenetrance(penetrance);
        }

        // Variant classification
        updateVariantClassification(reportedEvent, variant);

        // Role in cancer
        if (variant.getAnnotation() != null) {
            if (MapUtils.isNotEmpty(roleInCancer) && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                    if (StringUtils.isNotEmpty(ct.getGeneName()) && roleInCancer.containsKey(ct.getGeneName())) {
                        reportedEvent.setRoleInCancer(roleInCancer.get(ct.getGeneName()));
                        break;
                    }
                }
            }
        }

        // Actionable
        // DefaultReportedVariantCreator sets tier to null in order to avoid setting actionable info,
        // on the other hand TeamReportedVariantCreator sets tier to "" in order to set actionable info,
        // otherwise, set the provided tier
        if (tier != null) {
            if (StringUtils.isEmpty(tier)) {
                updateActionableInfo(reportedEvent, variant);
            } else {
                reportedEvent.setTier(tier);
            }
        }

        return reportedEvent;
    }

    public void updateVariantClassification(ReportedEvent reportedEvent, Variant variant) {
        VariantClassification variantClassification = new VariantClassification();

        // ACMG
        variantClassification.setAcmg(calculateAcmgClassification(variant, reportedEvent.getModeOfInheritance()));

        // Role in cancer and clinical significance
        if (variant.getAnnotation() != null) {

            // Clinical significance, this is stored in the trait association
            if (CollectionUtils.isNotEmpty(variant.getAnnotation().getTraitAssociation()) && reportedEvent.getGenomicFeature() != null) {
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
        }

        reportedEvent.setClassification(variantClassification);
    }

    protected void updateActionableInfo(ReportedEvent reportedEvent, Variant variant) {
        if (MapUtils.isNotEmpty(actionableVariants) && actionableVariants.containsKey(variant.getId())) {
            // Set actionable
            reportedEvent.setActionable(true);

            // Set Tier3
            reportedEvent.setTier(TIER_3);

            // Set phenotypes for that variant
            List<String> phenotypeIds = actionableVariants.get(variant.getId());
            if (CollectionUtils.isNotEmpty(phenotypeIds)) {
                Disorder disorder = new Disorder();
                List<Phenotype> evidences = new ArrayList<>();
                for (String phenotypeId : phenotypeIds) {
                    evidences.add(new Phenotype(phenotypeId, phenotypeId, ""));
                }
                disorder.setEvidences(evidences);

                reportedEvent.setDisorder(disorder);
            }
        }
    }


    protected List<SequenceOntologyTerm> getSOTerms(ConsequenceType ct, Set<String> includeSoTerms) {
        List<SequenceOntologyTerm> soTerms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
            for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                if (includeSoTerms.contains(soTerm.getName())) {
                    soTerms.add(soTerm);
                }
            }
        }
        return soTerms;
    }


    protected boolean containSOName(ConsequenceType ct, Set<String> soNameSet, Set<String> includeSoTerms) {
        List<SequenceOntologyTerm> sots = getSOTerms(ct, includeSoTerms);
        if (CollectionUtils.isNotEmpty(sots) && CollectionUtils.isNotEmpty(soNameSet)) {
            for (SequenceOntologyTerm sot : sots) {
                if (StringUtils.isNotEmpty(sot.getName()) && soNameSet.contains(sot.getName())) {
                    return true;
                }
            }

        }
        return false;
    }


    protected List<ReportedEvent> createReportedEvents(String tier, List<String> panelIds, ConsequenceType ct, Variant variant) {
        return createReportedEvents(tier, panelIds, ct, variant, extendedLof);
    }

    protected List<ReportedEvent> createReportedEvents(String tier, List<String> panelIds, ConsequenceType ct, Variant variant,
                                                       Set<String> includeSoTerms) {
        List<ReportedEvent> reportedEvents = new ArrayList<>();

        // Sanity check
        List<SequenceOntologyTerm> soTerms = null;
        GenomicFeature genomicFeature = null;
        if (ct != null) {
            soTerms = getSOTerms(ct, includeSoTerms);
            genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(), ct.getGeneName(), null, null);
        }

        if (CollectionUtils.isNotEmpty(panelIds)) {
            for (String panelId : panelIds) {
                ReportedEvent reportedEvent = createReportedEvent(disorder, soTerms, genomicFeature, panelId, modeOfInheritance,
                        penetrance, tier, variant);
                if (reportedEvent != null) {
                    reportedEvents.add(reportedEvent);
                }
            }
        } else {
            // We report events without panels, e.g., actionable variants (tier 3)
            ReportedEvent reportedEvent = createReportedEvent(disorder, soTerms, genomicFeature, null, modeOfInheritance,
                    penetrance, tier, variant);
            if (reportedEvent != null) {
                reportedEvents.add(reportedEvent);
            }
        }
        return reportedEvents;
    }

    private ClinicalProperty.ModeOfInheritance getMoiFromGenePanel(String inputMoi) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(inputMoi)) {
            return ModeOfInheritance.UNKNOWN;
        }

        String moi = inputMoi.toUpperCase();

        if (moi.startsWith("BIALLELIC")) {
            return ModeOfInheritance.BIALLELIC;
        }
        if (moi.startsWith("MONOALLELIC")) {
            if (moi.contains("NOT")) {
                return ModeOfInheritance.MONOALLELIC_NOT_IMPRINTED;
            } else if (moi.contains("MATERNALLY")) {
                return ModeOfInheritance.MONOALLELIC_MATERNALLY_IMPRINTED;
            } else if (moi.contains("PATERNALLY")) {
                return ModeOfInheritance.MONOALLELIC_PATERNALLY_IMPRINTED;
            } else {
                return ModeOfInheritance.MONOALLELIC;
            }
        }
        if (moi.startsWith("BOTH")) {
            if (moi.contains("SEVERE")) {
                return ModeOfInheritance.MONOALLELIC_AND_MORE_SEVERE_BIALLELIC;
            } else if (moi.contains("")) {
                return ModeOfInheritance.MONOALLELIC_AND_BIALLELIC;
            }
        }
        if (moi.startsWith("MITOCHONDRIAL")) {
            return ModeOfInheritance.MITOCHONDRIAL;
        }
        if (moi.startsWith("X-LINKED")) {
            if (moi.contains("BIALLELIC")) {
                return ModeOfInheritance.XLINKED_BIALLELIC;
            } else {
                return ModeOfInheritance.XLINKED_MONOALLELIC;
            }
        }
        return ModeOfInheritance.UNKNOWN;
    }

    public List<ReportedVariant> groupCHVariants(Map<String, List<ReportedVariant>> reportedVariantMap) {
        List<ReportedVariant> reportedVariants = new ArrayList<>();

        for (Map.Entry<String, List<ReportedVariant>> entry : reportedVariantMap.entrySet()) {
            Set<String> variantIds = entry.getValue().stream().map(Variant::toStringSimple).collect(Collectors.toSet());
            for (ReportedVariant reportedVariant : entry.getValue()) {
                Set<String> tmpVariantIds = new HashSet<>(variantIds);
                tmpVariantIds.remove(reportedVariant.toStringSimple());

                for (ReportedEvent reportedEvent : reportedVariant.getReportedEvents()) {
                    reportedEvent.setCompoundHeterozygousVariantIds(new ArrayList<>(tmpVariantIds));
                }

                reportedVariants.add(reportedVariant);
            }
        }

        return reportedVariants;
    }

    public List<ReportedVariant> mergeReportedVariants(List<ReportedVariant> reportedVariants) {
        Map<String, ReportedVariant> reportedVariantMap = new HashMap<>();
        for (ReportedVariant reportedVariant : reportedVariants) {
            if (reportedVariantMap.containsKey(reportedVariant.getId())) {
                reportedVariantMap.get(reportedVariant.getId()).getReportedEvents().addAll(reportedVariant.getReportedEvents());
            } else {
                reportedVariantMap.put(reportedVariant.getId(), reportedVariant);
            }
        }

        return new ArrayList<>(reportedVariantMap.values());
    }
}
