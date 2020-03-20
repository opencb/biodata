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

package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedEVSStatsCalculator extends VariantAggregatedStatsCalculator {

    public static final String GROUPS_ORDER = "GROUPS_ORDER";

    public VariantAggregatedEVSStatsCalculator() {
        super();
    }

    /**
     * @param tagMap Extends the VariantAggregatedVcfFactory(Properties properties) with one extra tag: {@link #GROUPS_ORDER}.
     * Example:
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
     *  The special tag 'GROUPS_ORDER' can be used to specify the order of the comma separated values for populations 
     *  in tags such as MAF.
     */
    public VariantAggregatedEVSStatsCalculator(Properties tagMap) {
        super(tagMap);
    }

    @Override
    protected void parseStats(Variant variant, StudyEntry study, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        FileEntry fileEntry = study.getFiles().get(0);
        // EVS params are not rearranged when normalizing. Use original call
        if (fileEntry.getCall() != null && !fileEntry.getCall().isEmpty()) {
            String[] ori = fileEntry.getCall().split(":");
            numAllele = Integer.parseInt(ori[3]);
            alternateAlleles = ori[2].split(",");
            reference = ori[1];
        }
        VariantStats stats = new VariantStats();
        if (info.containsKey("MAF")) {
            String splitsMAF[] = info.get("MAF").split(",");
            if (splitsMAF.length == 3) {
                float maf = Float.parseFloat(splitsMAF[2]) / 100;
                stats.setMaf(maf);
            }
        }

        if (info.containsKey("GTS") && info.containsKey("GTC")) {
            String splitsGTC[] = info.get("GTC").split(",");
            addGenotypeWithGTS(study.getFile(0).getData(), splitsGTC, reference, alternateAlleles, numAllele, stats);
        }
        calculateFilterQualStats(fileEntry.getData(), stats);

        study.setStats(StudyEntry.DEFAULT_COHORT, stats);
    }

    @Override
    protected void parseMappedStats(Variant variant, StudyEntry studyEntry,
                                    int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        FileEntry fileEntry = studyEntry.getFiles().get(0);
        if (fileEntry.getCall() != null && !fileEntry.getCall().isEmpty()) {
            String[] ori = fileEntry.getCall().split(":");
            numAllele = Integer.parseInt(ori[3]);
            alternateAlleles = ori[2].split(",");
            reference = ori[1];
        }

        if (tagMap != null) {
            for (String key : info.keySet()) {
                String opencgaTag = reverseTagMap.get(key);
                if (opencgaTag != null) {
                    String[] values = info.get(key).split(COMMA);
                    String[] opencgaTagSplit = opencgaTag.split(DOT); // a literal point
                    if (opencgaTagSplit.length == 2) {
                        String cohort = opencgaTagSplit[0];
                        VariantStats cohortStats = studyEntry.getStats(cohort);
                        if (cohortStats == null) {
                            cohortStats = new VariantStats();
                            studyEntry.setStats(cohort, cohortStats);
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
                                addGenotypeWithGTS(studyEntry.getFile(0).getData(), values, reference, alternateAlleles, numAllele, cohortStats);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (key.equals("MAF")) {
                    String[] values = info.get(key).split(COMMA);
                    String groups_order = tagMap.getProperty(VariantAggregatedEVSStatsCalculator.GROUPS_ORDER);
                    if (groups_order != null) {
                        String[] populations = groups_order.split(COMMA);
                        if (populations.length == values.length) {
                            for (int i = 0; i < values.length; i++) {   // each value has the maf of each population
                                float maf = Float.parseFloat(values[i]) / 100;  // from [0, 100] (%) to [0, 1]
                                VariantStats cohortStats = studyEntry.getStats(populations[i]);
                                if (cohortStats == null) {
                                    cohortStats = new VariantStats();
                                    studyEntry.setStats(populations[i], cohortStats);
                                }
                                cohortStats.setMaf(maf);
                            }
                        }
                    }
                }
            }
            // TODO reprocess stats to complete inferable values. A StatsHolder may be needed to keep values not storables in VariantStats
        }
        for (VariantStats stats : studyEntry.getStats().values()) {
            calculateFilterQualStats(info, stats);
        }
    }

}

