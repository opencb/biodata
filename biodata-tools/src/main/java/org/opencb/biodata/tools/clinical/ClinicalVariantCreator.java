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
import org.opencb.biodata.models.clinical.*;
import org.opencb.biodata.models.clinical.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.clinical.interpretation.exceptions.InterpretationAnalysisException;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.biodata.models.clinical.ClinicalProperty.Penetrance;
import static org.opencb.biodata.models.clinical.ClinicalProperty.RoleInCancer;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.*;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.extendedLof;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.proteinCoding;

public abstract class ClinicalVariantCreator {

    protected Set<String> biotypeSet;
    protected Set<String> soNameSet;

    protected Map<String, Set<DiseasePanel>> geneToPanelMap;
    protected Map<String, Set<DiseasePanel>> variantToPanelMap;

    // logger
    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    protected List<DiseasePanel> diseasePanels;
    protected Disorder disorder;
    protected List<ModeOfInheritance> modeOfInheritances;
    protected Penetrance penetrance;

    protected Map<String, List<RoleInCancer>> rolesInCancerMap;
    protected Map<String, List<String>> actionableVariants;

    protected String assembly;

    public ClinicalVariantCreator(List<DiseasePanel> diseasePanels, Disorder disorder,
                                  List<ModeOfInheritance> modeOfInheritances, Penetrance penetrance,
                                  Map<String, List<RoleInCancer>> roleInCancer, Map<String, List<String>> actionableVariants,
                                  String assembly) {
        this(diseasePanels, disorder, modeOfInheritances, penetrance, roleInCancer, actionableVariants, assembly,
                new ArrayList<>(proteinCoding), new ArrayList<>(extendedLof));
    }

    public ClinicalVariantCreator(List<DiseasePanel> diseasePanels, Disorder disorder,
                                  List<ModeOfInheritance> modeOfInheritances, Penetrance penetrance,
                                  Map<String, List<RoleInCancer>> rolesInCancerMap, Map<String, List<String>> actionableVariants,
                                  String assembly, List<String> biotypes, List<String> soNames) {

        this.diseasePanels = diseasePanels;
        this.disorder = disorder;
        this.modeOfInheritances = modeOfInheritances;
        this.penetrance = penetrance;
        this.rolesInCancerMap = rolesInCancerMap;
        this.actionableVariants = actionableVariants;
        this.assembly = assembly;

        this.biotypeSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(biotypes)) {
            biotypeSet.addAll(biotypes);
        }
        this.soNameSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(soNames)) {
            soNameSet.addAll(soNames);
        }

        this.geneToPanelMap = null;
        this.variantToPanelMap = null;

    }

    public abstract List<ClinicalVariant> create(List<Variant> variants) throws InterpretationAnalysisException;

    public List<ClinicalVariant> create(List<Variant> variants, ModeOfInheritance moi) throws InterpretationAnalysisException {
        return create(variants, Collections.singletonList(moi));
    }

    public List<ClinicalVariant> create(List<Variant> variants, List<ModeOfInheritance> mois) throws InterpretationAnalysisException {
        this.modeOfInheritances = mois;
        return create(variants);
    }

    public List<ClinicalVariant> createSecondaryFindings(List<Variant> variants) {
        List<ClinicalVariant> clinicalVariants = new ArrayList<>();
        for (Variant variant : variants) {
            List<ClinicalVariantEvidence> clinicalVariantEvidences = new ArrayList<>();

            // Tier 3, actionable variants
            if (MapUtils.isNotEmpty(actionableVariants)) {
                if (variant.getAnnotation() != null && actionableVariants.containsKey(variant.toString())) {
                    if (CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                        for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                            clinicalVariantEvidences.addAll(createClinicalVariantEvidences("", null, ct, variant));
                        }
                    } else {
                        // We create the evidences anyway!
                        clinicalVariantEvidences.addAll(createClinicalVariantEvidences("", null, null, variant));
                    }
                }
            }

            // If we have clinical variant evidences, then we have to create the clinical variant
            if (CollectionUtils.isNotEmpty(clinicalVariantEvidences)) {
                ClinicalVariant clinicalVariant = new ClinicalVariant(variant.getImpl(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyMap(), new ClinicalDiscussion(), ClinicalVariant.Status.NOT_REVIEWED,
                        Collections.emptyList(), Collections.emptyMap());
                clinicalVariant.setEvidences(clinicalVariantEvidences);

                // Add variant to the list
                clinicalVariants.add(clinicalVariant);
            }
        }
        return clinicalVariants;
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
                        if (StringUtils.isNotEmpty(panelGene.getId()) && panelGene.getModeOfInheritance() != null) {
                            if (!idToPanelMoiMap.containsKey(panelGene.getId())) {
                                idToPanelMoiMap.put(panelGene.getId(), new HashMap());
                            }
                            idToPanelMoiMap.get(panelGene.getId()).put(panel.getId(), panelGene.getModeOfInheritance());
                        }
                    }
                }
            }
        }
        return idToPanelMoiMap;
    }

    protected ClinicalVariantEvidence createClinicalVariantEvidence(GenomicFeature genomicFeature, String panelId,
                                                                    ModeOfInheritance moi, Penetrance penetrance,
                                                                    String tier, Variant variant) {
        return  createClinicalVariantEvidence(genomicFeature, panelId, Collections.singletonList(moi), penetrance,
                tier, variant);
    }

    protected ClinicalVariantEvidence createClinicalVariantEvidence(GenomicFeature genomicFeature, String panelId,
                                                                    List<ModeOfInheritance> mois,
                                                                    Penetrance penetrance, String tier, Variant variant) {
        ClinicalVariantEvidence clinicalVariantEvidence = new ClinicalVariantEvidence();

//        // Consequence types
//        if (CollectionUtils.isNotEmpty(consequenceTypes)) {
//            // Set consequence type
//            clinicalVariantEvidence.setConsequenceTypes(consequenceTypes);
//        }

        // Genomic feature
        if (genomicFeature != null) {
            clinicalVariantEvidence.setGenomicFeature(genomicFeature);
        }

        // Panel ID
        if (panelId != null) {
            clinicalVariantEvidence.setPanelId(panelId);
        }

        // Mode of inheritance
        if (mois != null) {
            clinicalVariantEvidence.setModeOfInheritances(mois);
        }

        // Penetrance
        if (penetrance != null) {
            clinicalVariantEvidence.setPenetrance(penetrance);
        }

        // Variant classification:
        clinicalVariantEvidence.setClassification(new VariantClassification());

        // Variant classification: ACMG
        List<ClinicalAcmg> acmgs = calculateAcmgClassification(variant, mois);
        clinicalVariantEvidence.getClassification().setAcmg(acmgs);

        // Variant classification: clinical significance
        if (MapUtils.isNotEmpty(variantToPanelMap) && variantToPanelMap.containsKey(variant.getId())
                && CollectionUtils.isNotEmpty(variantToPanelMap.get(variant.getId()))) {
            clinicalVariantEvidence.getClassification().setClinicalSignificance(ClinicalProperty.ClinicalSignificance.PATHOGENIC);
        } else {
            clinicalVariantEvidence.getClassification().setClinicalSignificance(computeClinicalSignificance(acmgs));
        }

        // Role in cancer
        if (variant.getAnnotation() != null) {
            if (MapUtils.isNotEmpty(rolesInCancerMap) && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                    if (StringUtils.isNotEmpty(ct.getGeneName()) && rolesInCancerMap.containsKey(ct.getGeneName())) {
                        clinicalVariantEvidence.setRolesInCancer(rolesInCancerMap.get(ct.getGeneName()));
                        break;
                    }
                }
            }
        }

        // Actionable management
        if (MapUtils.isNotEmpty(actionableVariants) && actionableVariants.containsKey(variant.getId())) {
            clinicalVariantEvidence.setActionable(true);
            // Set tier 3 only if it is null or untiered
            if (tier == null || UNTIERED.equals(tier)) {
                clinicalVariantEvidence.getClassification().setTier(TIER_3);
            } else {
                clinicalVariantEvidence.getClassification().setTier(tier);
            }
            // Add 'actionable' phenotypes
            if (CollectionUtils.isNotEmpty(actionableVariants.get(variant.getId()))) {
                List<Phenotype> evidences = new ArrayList<>();
                for (String phenotypeId : actionableVariants.get(variant.getId())) {
                    evidences.add(new Phenotype(phenotypeId, phenotypeId, ""));
                }
                if (CollectionUtils.isNotEmpty(evidences)) {
                    clinicalVariantEvidence.setPhenotypes(evidences);
                }
            }
        }

        return clinicalVariantEvidence;
    }


    protected List<SequenceOntologyTerm> getSOTerms(ConsequenceType ct, Set<String> includeSoTerms) {
        List<SequenceOntologyTerm> soTerms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
            for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                if (CollectionUtils.isEmpty(includeSoTerms) || includeSoTerms.contains(soTerm.getName())) {
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


    protected List<ClinicalVariantEvidence> createClinicalVariantEvidences(String tier, List<String> panelIds, ConsequenceType ct,
                                                                           Variant variant) {
        return createClinicalVariantEvidences(tier, panelIds, ct, variant, extendedLof);
    }

    protected List<ClinicalVariantEvidence> createClinicalVariantEvidences(String tier, List<String> panelIds, ConsequenceType ct,
                                                                           Variant variant, Set<String> includeSoTerms) {
        List<ClinicalVariantEvidence> clinicalVariantEvidences = new ArrayList<>();

        // Sanity check
        List<SequenceOntologyTerm> soTerms = null;
        GenomicFeature genomicFeature = null;
        if (ct != null) {
            soTerms = getSOTerms(ct, includeSoTerms);

            genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), "GENE", ct.getEnsemblTranscriptId(), ct.getGeneName(), soTerms,
                    null);
        }

        if (CollectionUtils.isNotEmpty(panelIds)) {
            for (String panelId : panelIds) {
                ClinicalVariantEvidence clinicalVariantEvidence = createClinicalVariantEvidence(genomicFeature, panelId, modeOfInheritances,
                        penetrance, tier, variant);
                if (clinicalVariantEvidence != null) {
                    clinicalVariantEvidences.add(clinicalVariantEvidence);
                }
            }
        } else {
            // We report events without panels, e.g., actionable variants (tier 3)
            if (CollectionUtils.isNotEmpty(soTerms)) {
                ClinicalVariantEvidence clinicalVariantEvidence = createClinicalVariantEvidence(genomicFeature, null, modeOfInheritances,
                        penetrance, tier, variant);
                if (clinicalVariantEvidence != null) {
                    clinicalVariantEvidences.add(clinicalVariantEvidence);
                }
            }
        }
        return clinicalVariantEvidences;
    }

    public List<ClinicalVariant> groupCHVariants(Map<String, List<ClinicalVariant>> clinicalVariantMap) {
        List<ClinicalVariant> clinicalVariants = new ArrayList<>();

        for (Map.Entry<String, List<ClinicalVariant>> entry : clinicalVariantMap.entrySet()) {
            Set<String> variantIds = entry.getValue().stream().map(Variant::toStringSimple).collect(Collectors.toSet());
            for (ClinicalVariant clinicalVariant : entry.getValue()) {
                Set<String> tmpVariantIds = new HashSet<>(variantIds);
                tmpVariantIds.remove(clinicalVariant.toStringSimple());

                for (ClinicalVariantEvidence clinicalVariantEvidence : clinicalVariant.getEvidences()) {
                    clinicalVariantEvidence.setCompoundHeterozygousVariantIds(new ArrayList<>(tmpVariantIds));
                }

                clinicalVariants.add(clinicalVariant);
            }
        }

        return clinicalVariants;
    }

    public List<ClinicalVariant> mergeClinicalVariants(List<ClinicalVariant> clinicalVariants) {
        Map<String, ClinicalVariant> clinicalVariantMap = new HashMap<>();
        for (ClinicalVariant clinicalVariant : clinicalVariants) {
            if (clinicalVariantMap.containsKey(clinicalVariant.getId())) {
                clinicalVariantMap.get(clinicalVariant.getId()).getEvidences().addAll(clinicalVariant.getEvidences());
            } else {
                clinicalVariantMap.put(clinicalVariant.getId(), clinicalVariant);
            }
        }

        return new ArrayList<>(clinicalVariantMap.values());
    }
}
