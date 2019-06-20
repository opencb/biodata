package org.opencb.biodata.tools.clinical;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.commons.Disorder;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.commons.datastore.core.ObjectMap;

import java.io.IOException;
import java.util.*;

import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.ClinicalSignificance;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.calculateAcmgClassification;
import static org.opencb.biodata.models.clinical.interpretation.VariantClassification.computeClinicalSignificance;

public class ReportedVariantCreator {

    private ObjectMap dependencies;
    private ObjectMap config;

    private String assembly;
    private Disorder disorder;

    private List<DiseasePanel> diseasePanels;
    private Map<String, Set<DiseasePanel>> variantToPanel;
    private Map<String, Set<DiseasePanel>> geneToPanel;

    private Set<String> biotypeNameSet;
    private Set<String> soNameSet;

    private ClinicalProperty.ModeOfInheritance modeOfInheritance;
    private ClinicalProperty.Penetrance penetrance;

    private RoleInCancerManager roleInCancerManager;
    private ActionableVariantManager actionableVariantManager;

    public ReportedVariantCreator(ObjectMap dependencies, ObjectMap config) {
        this.dependencies = dependencies;
        this.config = config;

        // Assembly
        if (config.containsKey(ClinicalUtils.ASSEMBLY)) {
            assembly = config.getString(ClinicalUtils.ASSEMBLY);
        }

        // Disorder
        if (config.containsKey(ClinicalUtils.DISORDER)) {
            disorder = (Disorder) config.get(ClinicalUtils.DISORDER);
        }

        // Panel management
        if (CollectionUtils.isNotEmpty(config.getAsList(ClinicalUtils.PANELS))) {
            diseasePanels = (List<DiseasePanel>) config.get(ClinicalUtils.PANELS);
            variantToPanel = ClinicalUtils.getVariantToPanelMap(diseasePanels);
            geneToPanel = ClinicalUtils.getGeneToPanelMap(diseasePanels);
        }

        // Sequence ontology term and biotype management
        if (CollectionUtils.isNotEmpty(config.getAsList(ClinicalUtils.SEQUENCE_ONTOLOGY_TERMS))) {
            soNameSet = new HashSet<>((List<String>) config.get(ClinicalUtils.SEQUENCE_ONTOLOGY_TERMS));
        }
        if (CollectionUtils.isNotEmpty(config.getAsList(ClinicalUtils.BIOTYPES))) {
            biotypeNameSet = new HashSet<>((List<String>) config.get(ClinicalUtils.BIOTYPES));
        }

        // Mode of inheritance
        if (config.containsKey(ClinicalUtils.MODE_OF_INHERITANCE)) {
            modeOfInheritance = (ClinicalProperty.ModeOfInheritance) config.get(ClinicalUtils.MODE_OF_INHERITANCE);
        }
        if (config.containsKey(ClinicalUtils.PENETRANCE)) {
            penetrance = (ClinicalProperty.Penetrance) config.get(ClinicalUtils.PENETRANCE);
        }


        // Role in cancer and actionable variant managers
        if (dependencies.containsKey(ClinicalUtils.ROLE_IN_CANCER_MANAGER)) {
            roleInCancerManager = (RoleInCancerManager) dependencies.get(ClinicalUtils.ROLE_IN_CANCER_MANAGER);
        }
        if (CollectionUtils.isNotEmpty(dependencies.getAsList(ClinicalUtils.ACTIONABLE_VARIANT_MANAGER))) {
            actionableVariantManager = (ActionableVariantManager) dependencies.get(ClinicalUtils.ACTIONABLE_VARIANT_MANAGER);
        }
    }

    public List<ReportedVariant> createReportedVariants(List<Variant> variants) {
        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            ReportedVariant reportedVariant = createReportedVariant(variant);
            if (reportedVariant != null) {
                reportedVariants.add(reportedVariant);
            }
        }
        return reportedVariants;
    }

    public ReportedVariant createReportedVariant(Variant variant) {
        List<ReportedEvent> reportedEvents = new ArrayList<>();

        if (CollectionUtils.isEmpty(diseasePanels)) {
            // No panels
            reportedEvents.addAll(createReportedEvents(variant));
        } else {
            // Panels are present
            List<DiseasePanel> panels = new ArrayList<>();
            if (variantToPanel.containsKey(variant.toStringSimple())) {
                panels.addAll(variantToPanel.get(variant.toStringSimple()));
            } else {
                Set<String> geneIds = getGeneIds(variant);
                if (CollectionUtils.isNotEmpty(geneIds)) {
                    Set<DiseasePanel> panelSet = new HashSet<>();
                    for (String geneId : geneIds) {
                        if (geneToPanel.containsKey(geneId)) {
                            panelSet.addAll(geneToPanel.get(geneId));
                        }
                    }
                    panels.addAll(panelSet);
                }
            }
            reportedEvents.addAll(createReportedEvents(variant, panels));
        }

        // It creates reported variant if there are reported events for that variant
        ReportedVariant reportedVariant = null;
        if (CollectionUtils.isNotEmpty(reportedEvents)) {
            reportedVariant = new ReportedVariant(variant.getImpl()).setEvidences(reportedEvents);
        }

        return reportedVariant;
    }

    //------------------------------------------------------------------------
    // P R I V A T E      M E T H O D S
    //------------------------------------------------------------------------

    private List<ReportedEvent> createReportedEvents(Variant variant, List<DiseasePanel> panels) {
        List<ReportedEvent> reportedEvents = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(panels)) {
            for (DiseasePanel panel : panels) {
                reportedEvents.addAll(createReportedEvents(variant, panel));
            }
        } else {
            reportedEvents.addAll(createReportedEvents(variant));
        }
        return reportedEvents;
    }

    private List<ReportedEvent> createReportedEvents(Variant variant) {
        return createReportedEvents(variant, (DiseasePanel) null);
    }

    private List<ReportedEvent> createReportedEvents(Variant variant, DiseasePanel panel) {
        List<ReportedEvent> reportedEvents = new ArrayList<>();

        if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
            // Actionable management
            boolean actionable = false;
            if (actionableVariantManager != null && assembly != null && disorder != null) {
                try {
                    Map<String, List<String>> actionableVariants = actionableVariantManager.getActionableVariants(assembly);
                    if (actionableVariants.containsKey(variant.toStringSimple())) {
                        Set<String> phenotypes = new HashSet<>(actionableVariants.get(variant.toStringSimple()));
                        if (CollectionUtils.isNotEmpty(phenotypes) && CollectionUtils.isNotEmpty(disorder.getEvidences())) {
                            for (Phenotype phenotype : disorder.getEvidences()) {
                                if (phenotypes.contains(phenotype.getId()) || phenotypes.contains(phenotype.getName())) {
                                    actionable = true;
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Consequence types
            for (ConsequenceType consequenceType : variant.getAnnotation().getConsequenceTypes()) {
                List<SequenceOntologyTerm> soTerms = getSequenceOntologyTerms(consequenceType);
                if (isBiotypeValid(consequenceType.getBiotype()) && CollectionUtils.isNotEmpty(soTerms)) {
                    ReportedEvent reportedEvent = new ReportedEvent();

                    // Set panel ID, genomic feature and sequence ontology terms
                    if (panel != null) {
                        reportedEvent.setPanelId(panel.getId());
                    }
                    reportedEvent.setGenomicFeature(getGenomicFeature(consequenceType));
                    reportedEvent.setConsequenceTypes(soTerms);

                    // Set mode of inheritance and penetrance
                    if (modeOfInheritance != null) {
                        reportedEvent.setModeOfInheritance(modeOfInheritance);
                    }
                    if (penetrance != null) {
                        reportedEvent.setPenetrance(penetrance);
                    }

                    // Set role in cancer
                    if (roleInCancerManager != null) {
                        try {
                            Map<String, ClinicalProperty.RoleInCancer> roleInCancer = roleInCancerManager.getRoleInCancer();
                            if (roleInCancer.containsKey(consequenceType.getEnsemblGeneId())) {
                                reportedEvent.setRoleInCancer(roleInCancer.get(consequenceType.getEnsemblGeneId()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Set actionable
                    reportedEvent.setActionable(actionable);

                    // Set variant classification
                    List<String> acmg = calculateAcmgClassification(consequenceType, variant.getAnnotation(), modeOfInheritance);
                    ClinicalSignificance clinicalSignificance = computeClinicalSignificance(acmg);
                    VariantClassification variantClassification = new VariantClassification()
                            .setAcmg(acmg)
                            .setClinicalSignificance(clinicalSignificance);
                    reportedEvent.setClassification(variantClassification);

                    // And finally, add reported event to the list
                    reportedEvents.add(reportedEvent);
                }
            }
        } else if (panel != null) {
            // Only panel (no transcripts found)
            ReportedEvent reportedEvent = new ReportedEvent().setPanelId(panel.getId());
            reportedEvents.add(reportedEvent);
        }

        return reportedEvents;
    }

    private Set<String> getGeneIds(Variant variant) {
        Set<String> geneIds = new HashSet<>();
        if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
            for (ConsequenceType consequenceType : variant.getAnnotation().getConsequenceTypes()) {
                if (StringUtils.isNotEmpty(consequenceType.getEnsemblGeneId())) {
                    geneIds.add(consequenceType.getEnsemblGeneId());
                }
            }
        }
        return geneIds;
    }

    private GenomicFeature getGenomicFeature(ConsequenceType consequenceType) {
        return new GenomicFeature(consequenceType.getEnsemblGeneId(), "GENE", consequenceType.getEnsemblTranscriptId(),
                    consequenceType.getGeneName(), null);
    }

    private boolean isBiotypeValid(String biotype) {
        if (CollectionUtils.isEmpty(biotypeNameSet) || biotypeNameSet.contains(biotype)) {
            return true;
        }
        return false;
    }

    private List<SequenceOntologyTerm> getSequenceOntologyTerms(ConsequenceType consequenceType) {
        List<SequenceOntologyTerm> soList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(consequenceType.getSequenceOntologyTerms())) {
            for (SequenceOntologyTerm sequenceOntologyTerm : consequenceType.getSequenceOntologyTerms()) {
                if (CollectionUtils.isEmpty(soNameSet) || soNameSet.contains(sequenceOntologyTerm.getName())
                        || soNameSet.contains(sequenceOntologyTerm.getAccession())) {
                    soList.add(sequenceOntologyTerm);
                }
            }
        }
        return soList;
    }
}
