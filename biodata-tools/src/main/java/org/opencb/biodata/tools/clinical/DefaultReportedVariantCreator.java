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
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.commons.utils.ListUtils;

import java.util.*;

public class DefaultReportedVariantCreator extends ReportedVariantCreator {

    private List<DiseasePanel> diseasePanels;
    private Phenotype phenotype;
    private ModeOfInheritance modeOfInheritance;
    private Penetrance penetrance;

    public DefaultReportedVariantCreator(List<DiseasePanel> diseasePanels, ModeOfInheritance modeOfInheritance, Penetrance penetrance) {
        this.diseasePanels = diseasePanels;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    public DefaultReportedVariantCreator(List<DiseasePanel> diseasePanels, Phenotype phenotype, ModeOfInheritance modeOfInheritance,
                                         Penetrance penetrance) {
        this.diseasePanels = diseasePanels;
        this.phenotype = phenotype;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) {
        Map<String, List<String>> geneToPanelIdMap = getGeneToPanelIdMap(diseasePanels);

        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant: variants) {
            ReportedVariant reportedVariant = new ReportedVariant(variant.getImpl(), 0, new ArrayList<>(),
                    Collections.emptyList(), Collections.emptyMap());

            if (variant.getAnnotation() != null && ListUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                // Create the reported event for each consequence type
                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                    if (geneToPanelIdMap.containsKey(ct.getEnsemblGeneId())) {
                        // Create the reported event for each gene panel
                        for (String panelId: geneToPanelIdMap.get(ct.getEnsemblGeneId())) {
                            ReportedEvent reportedEvent = newReportedEvent(reportedVariant.getReportedEvents().size(),
                            phenotype, ct, panelId, modeOfInheritance, penetrance, variant);

                            // Add reported event to the reported variant
                            reportedVariant.getReportedEvents().add(reportedEvent);
                        }
                    } else {
                        ReportedEvent reportedEvent = newReportedEvent(reportedVariant.getReportedEvents().size(),
                                phenotype, ct, null, modeOfInheritance, penetrance, variant);

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
