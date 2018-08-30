package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Condition;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;

/**
 * Created on 21/06/18.
 *
 * Code extracted from {@link VariantStatsCalculator}
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsPedigree {

    public static void calculate(StudyEntry study, Collection<String> sampleNames, Pedigree pedigree) {
        VariantStats variantStats = new VariantStats();
        int controlsDominant = 0;
        int casesDominant = 0;
        int controlsRecessive = 0;
        int casesRecessive = 0;
        double numSamples = sampleNames.size();
        double mendelianErrors = 0;


        Integer gtIdx = study.getFormatPositions().get("GT");
        LinkedHashMap<String, Integer> samplesPosition = study.getSamplesPosition();

        Map<String, Genotype> gts = new TreeMap<>(String::compareTo);
        for (String sampleName : sampleNames) {
            Integer sampleIdx = samplesPosition.get(sampleName);
            if (sampleIdx == null) {
                continue;
            }
            String genotype = study.getSamplesData().get(sampleIdx).get(gtIdx);
            Genotype g = gts.computeIfAbsent(genotype, key -> new Genotype(genotype));

            // Include statistics that depend on pedigree information
            if (g.getCode() == AllelesCode.ALLELES_OK) {
                Individual ind = pedigree.getIndividual(sampleName);
//                if (MendelChecker.isMendelianError(ind, g, variant.getChromosome(), file.getSamplesDataAsMap())) {
//                   mendelianErrors++
//                }
                if (g.getCode() == AllelesCode.ALLELES_OK) {
                    // Check inheritance models
                    if (ind.getCondition() == Condition.UNAFFECTED) {
                        if (g.isAlleleRef(0) && g.isAlleleRef(1)) { // 0|0
                            controlsDominant++;
                            controlsRecessive++;

                        } else if ((g.isAlleleRef(0) && !g.isAlleleRef(1)) || (!g.isAlleleRef(0) || g.isAlleleRef(1))) { // 0|1 or 1|0
                            controlsRecessive++;

                        }
                    } else if (ind.getCondition() == Condition.AFFECTED) {
                        if (!g.isAlleleRef(0) && !g.isAlleleRef(1) && g.getAllele(0) == g.getAllele(1)) {// 1|1, 2|2, and so on
                            casesRecessive++;
                            casesDominant++;
                        } else if (!g.isAlleleRef(0) || !g.isAlleleRef(1)) { // 0|1, 1|0, 1|2, 2|1, 1|3, and so on
                            casesDominant++;

                        }
                    }

                }

            }

        }  // Finish all samples loop


        // Once all samples have been traversed, calculate % that follow inheritance model
        double controlsDominantPercent = controlsDominant * 100 / (numSamples - variantStats.getMissingGenotypeCount());
        double casesDominantPercent = casesDominant * 100 / (numSamples - variantStats.getMissingGenotypeCount());
        double controlsRecessivePercent = controlsRecessive * 100 / (numSamples - variantStats.getMissingGenotypeCount());
        double casesRecessivePercent = casesRecessive * 100 / (numSamples - variantStats.getMissingGenotypeCount());


        // TODO: Store somewhere
    }


}
