package org.opencb.biodata.tools.variant.converters.avro;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;

import java.util.Map;

/**
 * Created on 31/05/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsToPopulationFrequencyConverter {

    public PopulationFrequency convert(String study, String population, VariantStats stats, String reference, String alternate) {
        Float refHomGenotypeFreq = 0F;
        Float hetGenotypeFreq = 0F;
        Float altHomGenotypeFreq = 0F;

        if (stats.getGenotypesFreq() != null && !stats.getGenotypesFreq().isEmpty()) {
            for (Map.Entry<Genotype, Float> entry : stats.getGenotypesFreq().entrySet()) {
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
