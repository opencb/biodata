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

import java.util.*;

public abstract class ReportedVariantCreator {

    public abstract List<ReportedVariant> create(List<Variant> variants) throws InterpretationAnalysisException;

    protected Map<String, List<String>> getGeneToPanelIdMap(List<DiseasePanel> diseasePanels) {
        return getIdToPanelIdMap(diseasePanels, true);
    }

    protected Map<String, List<String>> getIdToPanelIdMap(List<DiseasePanel> diseasePanels) {
        return getIdToPanelIdMap(diseasePanels, false);
    }

    protected Map<String, List<String>> getIdToPanelIdMap(List<DiseasePanel> diseasePanels, boolean excludeVariants) {
        Map<String, List<String>> idToPanelIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(diseasePanels)) {
            for (DiseasePanel panel: diseasePanels) {
                // Put gene IDs
                if (CollectionUtils.isNotEmpty(panel.getGenes())) {
                    for (DiseasePanel.GenePanel panelGene : panel.getGenes()) {
                        if (panelGene.getId() != null) {
                            if (!idToPanelIdMap.containsKey(panelGene.getId())) {
                                idToPanelIdMap.put(panelGene.getId(), new ArrayList<>());
                            }
                            idToPanelIdMap.get(panelGene.getId()).add(panel.getId());
                        }
                    }
                }

                if (!excludeVariants) {
                    // Put variant IDs
                    if (CollectionUtils.isNotEmpty(panel.getVariants())) {
                        for (DiseasePanel.VariantPanel panelVariant : panel.getVariants()) {
                            if (panelVariant.getId() != null) {
                                if (!idToPanelIdMap.containsKey(panelVariant.getId())) {
                                    idToPanelIdMap.put(panelVariant.getId(), new ArrayList<>());
                                }
                                idToPanelIdMap.get(panelVariant.getId()).add(panel.getId());
                            }
                        }
                    }
                }
            }
        }
        return idToPanelIdMap;
    }

    protected ReportedEvent createReportedEvent(Phenotype phenotype, List<String> soNames, GenomicFeature genomicFeature, String panelId,
                                                ClinicalProperty.ModeOfInheritance moi, ClinicalProperty.Penetrance penetrance,
                                                Variant variant) {
        ReportedEvent reportedEvent = new ReportedEvent()
                .setId("OPENCB-" + UUID.randomUUID());
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

        List<String> acmg = VariantClassification.calculateAcmgClassification(variant, reportedEvent);
        VariantClassification variantClassification = new VariantClassification().setAcmg(acmg);
        reportedEvent.setClassification(variantClassification);

        return reportedEvent;
    }
}
