/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.variant.stats.VariantAggregatedStatsCalculator;

import java.util.*;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedEVSStatsCalculator extends VariantAggregatedStatsCalculator {



    public VariantAggregatedEVSStatsCalculator() {
        super();
    }

    /**
     * @param tagMap Extends the VariantAggregatedVcfFactory(Properties properties) with one extra tag: GROUPS_ORDER. Example:
     *
     * EUR.AF=EUR_AF
     * EUR.AC=AC_EUR
     * EUR.AN=EUR_AN
     * EUR.GTC=EUR_GTC
     * ALL.AF=AF
     * ALL.AC=TAC
     * ALL.AN=AN
     * ALL.GTC=GTC
     * GROUPS_ORDER=EUR,ALL
     *
     * The special tag 'GROUPS_ORDER' can be used to specify the order of the comma separated values for populations in tags such as MAF.
     *
     */
    public VariantAggregatedEVSStatsCalculator(Properties tagMap) {
        super(tagMap);
    }

    public VariantAggregatedEVSStatsCalculator(Set<String> cohorts) {
        super(cohorts);
    }

    @Override
    protected void parseStats(Variant variant, VariantSourceEntry file, int numAllele, String[] alternateAlleles, Map<String, String> info) {
        VariantStats stats = new VariantStats(variant);
        if (info.containsKey("MAF")) {
            String splitsMAF[] = info.get("MAF").split(",");
            if (splitsMAF.length == 3) {
                float maf = Float.parseFloat(splitsMAF[2]) / 100;
                stats.setMaf(maf);
            }
        }

        if (info.containsKey("GTS") && info.containsKey("GTC")) {
            String splitsGTC[] = info.get("GTC").split(",");
            addGenotypeWithGTS(variant, file, splitsGTC, alternateAlleles, numAllele, stats);
        }
        file.setStats(stats);
    }

    @Override
    protected void parseMappedStats(Variant variant, VariantSourceEntry sourceEntry,
                                    int numAllele, String[] alternateAlleles, Map<String, String> info) {
        if (tagMap != null) {
            for (String key : info.keySet()) {
                String opencgaTag = reverseTagMap.get(key);
                String[] values = info.get(key).split(COMMA);
                if (opencgaTag != null) {
                    String[] opencgaTagSplit = opencgaTag.split(DOT); // a literal point
                    if (opencgaTagSplit.length == 2) {
                        String cohort = opencgaTagSplit[0];
                        VariantStats cohortStats = sourceEntry.getCohortStats(cohort);
                        if (cohortStats == null) {
                            cohortStats = new VariantStats(variant);
                            sourceEntry.setCohortStats(cohort, cohortStats);
                        }
                        switch (opencgaTagSplit[1]) {
                            case "AC":
                                cohortStats.setAltAlleleCount(Integer.parseInt(values[numAllele]));
                                cohortStats.setRefAlleleCount(Integer.parseInt(values[values.length - 1]));    // ref allele count is the last one
                                break;
                            case "AF":
                                cohortStats.setAltAlleleFreq(Float.parseFloat(values[numAllele]));
                                cohortStats.setRefAlleleFreq(Float.parseFloat(values[values.length - 1]));
                                break;
                            case "AN":
                                // TODO implement this. also, take into account that needed fields may not be processed yet
                                break;
                            case "GTC":
                                addGenotypeWithGTS(variant, sourceEntry, values, alternateAlleles, numAllele, cohortStats);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (key.equals("MAF")) {
                    String groups_order = tagMap.getProperty("GROUPS_ORDER");
                    if (groups_order != null) {
                        String[] populations = groups_order.split(COMMA);
                        if (populations.length == values.length) {
                            for (int i = 0; i < values.length; i++) {   // each value has the maf of each population
                                float maf = Float.parseFloat(values[i]) / 100;  // from [0, 100] (%) to [0, 1]
                                VariantStats cohortStats = sourceEntry.getCohortStats(populations[i]);
                                if (cohortStats == null) {
                                    cohortStats = new VariantStats(variant);
                                    sourceEntry.setCohortStats(populations[i], cohortStats);
                                }
                                cohortStats.setMaf(maf);
                            }
                        }
                    }
                }
            }
            // TODO reprocess stats to complete inferable values. A StatsHolder may be needed to keep values not storables in VariantStats
        }
    }

}

