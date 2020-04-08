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

    public PopulationFrequency convert(String study, String population, VariantStats stats, String reference, String alternate) {
        Float refHomGenotypeFreq = 0F;
        Float hetGenotypeFreq = 0F;
        Float altHomGenotypeFreq = 0F;

        if (stats.getGenotypeFreq() != null && !stats.getGenotypeFreq().isEmpty()) {
            for (Map.Entry<Genotype, Float> entry : stats.getGenotypeFreq().entrySet()) {
                Genotype gt = entry.getKey();
                boolean anyRef = false;
                boolean anyAlt = false;
                for (int i : gt.getAllelesIdx()) {
                    if (i == 0) {
                        anyRef = true;
                    } else {
                        anyAlt = true;
                    }
                }
                if (anyRef && !anyAlt) {
                    refHomGenotypeFreq += entry.getValue();
                } else if (anyRef && anyAlt) {
                    hetGenotypeFreq += entry.getValue();
                } else {
                    altHomGenotypeFreq += entry.getValue();
                }
            }
        } else {
            refHomGenotypeFreq = null;
            hetGenotypeFreq = null;
            altHomGenotypeFreq = null;
        }

        return new PopulationFrequency(
                study,
                population,
                reference,
                alternate,
                stats.getRefAlleleFreq(),
                stats.getAltAlleleFreq(),
                refHomGenotypeFreq, hetGenotypeFreq, altHomGenotypeFreq);
    }

}
