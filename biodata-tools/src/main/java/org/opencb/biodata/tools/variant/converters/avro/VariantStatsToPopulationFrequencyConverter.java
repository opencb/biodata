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

package org.opencb.biodata.tools.variant.converters.avro;

import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;

import java.util.Map;

/**
 * Created on 31/05/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsToPopulationFrequencyConverter {

    public PopulationFrequency convert(String study, VariantStats stats, String reference, String alternate) {
        return convert(study, stats.getCohortId(), stats, reference, alternate);
    }

    private enum GtType {
        HOM_ALT,
        HOM_REF,
        HET,
        OTHER
    }

    public PopulationFrequency convert(String study, String population, VariantStats stats, String reference, String alternate) {
        Float refHomGenotypeFreq = 0F;
        Float hetGenotypeFreq = 0F;
        Float altHomGenotypeFreq = 0F;

        Integer refHomGenotypeCount = 0;
        Integer hetGenotypeCount = 0;
        Integer altHomGenotypeCount = 0;

        if (stats.getGenotypeFreq() != null && !stats.getGenotypeFreq().isEmpty()) {
            for (Map.Entry<String, Float> entry : stats.getGenotypeFreq().entrySet()) {
                switch (getGtType(entry.getKey())) {
                    case HOM_REF:
                        refHomGenotypeFreq += entry.getValue();
                        break;
                    case HET:
                        hetGenotypeFreq += entry.getValue();
                        break;
                    case HOM_ALT:
                        altHomGenotypeFreq += entry.getValue();
                        break;
                }
            }
        } else {
            refHomGenotypeFreq = null;
            hetGenotypeFreq = null;
            altHomGenotypeFreq = null;
        }

        if (stats.getGenotypeCount() != null && !stats.getGenotypeCount().isEmpty()) {
            for (Map.Entry<String, Integer> entry : stats.getGenotypeCount().entrySet()) {
                switch (getGtType(entry.getKey())) {
                    case HOM_REF:
                        refHomGenotypeCount += entry.getValue();
                        break;
                    case HET:
                        hetGenotypeCount += entry.getValue();
                        break;
                    case HOM_ALT:
                        altHomGenotypeCount += entry.getValue();
                        break;
                }
            }
        } else {
            refHomGenotypeCount = null;
            hetGenotypeCount = null;
            altHomGenotypeCount = null;
        }

        return new PopulationFrequency(
                study,
                population,
                reference,
                alternate,
                stats.getRefAlleleFreq(),
                stats.getAltAlleleFreq(),
                stats.getRefAlleleCount(), stats.getAltAlleleCount(),
                refHomGenotypeFreq, hetGenotypeFreq, altHomGenotypeFreq,
                refHomGenotypeCount, hetGenotypeCount, altHomGenotypeCount);
    }

    private GtType getGtType(String gtStr) {
        switch (gtStr) {
            case Genotype.HOM_REF:
                return GtType.HOM_REF;
            case Genotype.HET_REF:
                return GtType.HET;
            case Genotype.HOM_VAR:
                return GtType.HOM_ALT;
        }
        gtStr = gtStr.replace("*", "2");
        Genotype gt = new Genotype(gtStr);
        boolean anyRef = false;
        boolean anyAlt = false;
        boolean anyOther = false;
        for (int i : gt.getAllelesIdx()) {
            if (i == 0) {
                anyRef = true;
            } else if (i == 1) {
                anyAlt = true;
            } else {
                // Missing or secondary alternates
                anyOther = true;
            }
        }
        if (anyOther) {
            return GtType.OTHER;
        } else if (anyRef && !anyAlt) {
            return GtType.HOM_REF;
        } else if (anyRef) {
            return GtType.HET;
        } else {
            return GtType.HOM_ALT;
        }
    }
}
