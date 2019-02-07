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
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.ModeOfInheritance;
import org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;
import org.opencb.biodata.models.clinical.interpretation.*;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;

import java.util.*;

public class DefaultReportedVariantCreator extends ReportedVariantCreator {

    private Phenotype phenotype;
    private ModeOfInheritance modeOfInheritance;
    private Penetrance penetrance;

    public DefaultReportedVariantCreator() {
        this(null, null, null);
    }


    public DefaultReportedVariantCreator(Phenotype phenotype, ModeOfInheritance modeOfInheritance, Penetrance penetrance) {
        this.phenotype = phenotype;
        this.modeOfInheritance = modeOfInheritance;
        this.penetrance = penetrance;
    }

    @Override
    public List<ReportedVariant> create(List<Variant> variants) {
        List<ReportedVariant> reportedVariants = new ArrayList<>();
        for (Variant variant : variants) {
            List<ReportedEvent> reportedEvents = new ArrayList<>();

            // SO names and genomic feature
            List<String> soNames;
            GenomicFeature genomicFeature;

            // Sanity check
            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {

                for (ConsequenceType ct : variant.getAnnotation().getConsequenceTypes()) {
                    soNames = getSoNames(ct);
                    genomicFeature = new GenomicFeature(ct.getEnsemblGeneId(), ct.getEnsemblTranscriptId(), ct.getGeneName(),
                            null, null);

                    boolean lof = isLOF(soNames);
                    if (lof || includeNoTier) {
                        ReportedEvent reportedEvent = createReportedEvent(phenotype, soNames, genomicFeature, null, modeOfInheritance,
                                penetrance, variant);
                        if (lof) {
                            setTier(reportedEvent);
                        }
                        reportedEvents.add(reportedEvent);
                    }
                }
            }

            // Create a reported variant only if we have reported events
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

    private void setTier(ReportedEvent reportedEvent) {
        // Sanity check
        if (reportedEvent != null && reportedEvent.getClassification() != null
                && CollectionUtils.isNotEmpty(reportedEvent.getClassification().getAcmg())) {
            for (String acmg : reportedEvent.getClassification().getAcmg()) {
                if (acmg.startsWith("PVS") || acmg.startsWith("PS")) {
                    // PVS = Very strong evidence of pathogenicity
                    // PS = Strong evidence of pathogenicity
                    reportedEvent.setTier(TIER_2);
                    return;
                } else if (acmg.startsWith("PM") || acmg.startsWith("PP")) {
                    // PM = Moderate evidence of pathogenicity
                    // PP = Supporting evidence of pathogenicity
                    reportedEvent.setTier(TIER_3);
                    return;
                }
            }
        }
    }

    private boolean isLOF(List<String> soNames) {
        // Sanity check
        if (CollectionUtils.isNotEmpty(soNames)) {
            for (String soName : soNames) {
                if (LOF_EXTENDED_SET.contains(soName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> getSoNames(ConsequenceType ct) {
        List<String> soNames = new ArrayList<>();
        // Sanity check
        if (CollectionUtils.isNotEmpty(ct.getSequenceOntologyTerms())) {
            for (SequenceOntologyTerm soTerm : ct.getSequenceOntologyTerms()) {
                if (StringUtils.isNotEmpty(soTerm.getName())) {
                    soNames.add(soTerm.getName());
                }
            }
        }
        return soNames;
    }
}
