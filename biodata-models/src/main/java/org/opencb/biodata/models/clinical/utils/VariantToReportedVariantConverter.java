package org.opencb.biodata.models.clinical.utils;

import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.commons.utils.ListUtils;

import java.util.*;

public class VariantToReportedVariantConverter {

    public List<ReportedVariant> convert(List<Variant> variants, List<DiseasePanel> diseasePanels, Phenotype phenotype,
                                         ModeOfInheritance moi, Penetrance penetrance) {

        Map<String, List<String>> genePanelMap = new HashMap<>();
        if (ListUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel: diseasePanels) {
                for (DiseasePanel.GenePanel genePanel : panel.getGenes()) {
                    if (genePanel.getId() != null) {
                        if (!genePanelMap.containsKey(genePanel.getId())) {
                            genePanelMap.put(genePanel.getId(), new ArrayList<>());
                        }
                        genePanelMap.get(genePanel.getId()).add(panel.getId());

                    }
                    genePanelMap.put(genePanel.getId(), null);
                }
            }
        }

        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant: variants) {
            ReportedVariant reportedVariant = new ReportedVariant(variant.getImpl(), 0, new ArrayList<>(),
                    Collections.emptyList(), Collections.emptyMap());

            if (variant.getAnnotation() != null && ListUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                // Create the reported event for each consequence type
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                    if (genePanelMap.containsKey(ct.getEnsemblGeneId())) {
                        // Create the reported event for each gene panel
                        for (String panelId: genePanelMap.get(ct.getEnsemblGeneId())) {
                            ReportedEvent reportedEvent = newReportedEvent(reportedVariant.getReportedEvents().size(),
                            phenotype, ct, panelId, moi, penetrance, variant);

                            // Add reported event to the reported variant
                            reportedVariant.getReportedEvents().add(reportedEvent);
                        }
                    } else {
                        ReportedEvent reportedEvent = newReportedEvent(reportedVariant.getReportedEvents().size(),
                                phenotype, ct, null, moi, penetrance, variant);

                        // Add reported event to the reported variant
                        reportedVariant.getReportedEvents().add(reportedEvent);
                    }
                }
            }
            reportedVariants.add(reportedVariant);
        }
        return reportedVariants;
    }

    private ReportedEvent newReportedEvent(int id, Phenotype phenotype, ConsequenceType ct, String panelId,
                                           ModeOfInheritance moi, Penetrance penetrance, Variant variant) {
        ReportedEvent reportedEvent = new ReportedEvent()
                .setId("OPENCB-" + id);
        if (phenotype != null) {
               reportedEvent.setPhenotypes(Collections.singletonList(phenotype));
        }
        if (ct != null) {
            reportedEvent.setConsequenceTypeIds(Collections.singletonList(ct.getBiotype()))
                    .setGenomicFeature(new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(),
                            ct.getGeneName(), null, null));
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

        List<String> acmg = VariantClassification.calculateAcmgClassification(variant, reportedEvent);
        VariantClassification variantClassification = new VariantClassification().setAcmg(acmg);
        reportedEvent.setClassification(variantClassification);

        return reportedEvent;
    }
}
