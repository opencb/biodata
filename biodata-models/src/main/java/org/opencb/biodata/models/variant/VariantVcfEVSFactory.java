package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantVcfEVSFactory extends VariantAggregatedVcfFactory {


    public VariantVcfEVSFactory() {
        this(null);
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
    public VariantVcfEVSFactory(Properties tagMap) {
        super(tagMap);
    }
    

    @Override
    protected void setOtherFields(Variant variant, VariantSource source, Set<String> ids, float quality, String filter,
                                  String info, String format, int numAllele, String[] alternateAlleles, String line) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setIds(ids);
        VariantSourceEntry sourceEntry = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        if (quality > -1) {
            sourceEntry.addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            sourceEntry.addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, source.getFileId(), source.getStudyId(), info, numAllele);
        }
        sourceEntry.setFormat(format);
        sourceEntry.addAttribute("src", line);


        if (tagMap == null) {   // whether we can parse population stats or not
            parseEVSAttributes(variant, source, numAllele, alternateAlleles);
        } else {
            parseCohortEVSInfo(variant, sourceEntry, numAllele, alternateAlleles);
        }
    }

    private void parseEVSAttributes(Variant variant, VariantSource source, int numAllele, String[] alternateAlleles) {
        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        VariantStats stats = new VariantStats(variant);
        if (file.hasAttribute("MAF")) {
            String splitsMAF[] = file.getAttribute("MAF").split(",");
            if (splitsMAF.length == 3) {
                float maf = Float.parseFloat(splitsMAF[2]) / 100;
                stats.setMaf(maf);
            }
        }

        if (file.hasAttribute("GTS") && file.hasAttribute("GTC")) {
            String splitsGTC[] = file.getAttribute("GTC").split(",");
            addGenotype(variant, file, splitsGTC, alternateAlleles, numAllele, stats);
        }
        file.setStats(stats);
    }


    private void parseCohortEVSInfo(Variant variant, VariantSourceEntry sourceEntry, 
                                    int numAllele, String[] alternateAlleles) {
        if (tagMap != null) {
            for (String key : sourceEntry.getAttributes().keySet()) {
                String opencgaTag = reverseTagMap.get(key);
                String[] values = sourceEntry.getAttribute(key).split(",");
                if (opencgaTag != null) {
                    String[] opencgaTagSplit = opencgaTag.split("\\."); // a literal point
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
                                addGenotype(variant, sourceEntry, values, alternateAlleles, numAllele, cohortStats);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (key.equals("MAF")) {
                    String groups_order = tagMap.getProperty("GROUPS_ORDER");
                    if (groups_order != null) {
                        String[] populations = groups_order.split(",");
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

    private void addGenotype(Variant variant, VariantSourceEntry sourceEntry, String[] splitsGTC, String[] alternateAlleles
            , int numAllele, VariantStats cohortStats) {
        if (sourceEntry.hasAttribute("GTS")) {
            String splitsGTS[] = sourceEntry.getAttribute("GTS").split(",");
            if (splitsGTC.length == splitsGTS.length) {
                for (int i = 0; i < splitsGTC.length; i++) {
                    String gt = splitsGTS[i];
                    int gtCount = Integer.parseInt(splitsGTC[i]);
                    
                    Genotype g = parseGenotype(gt, variant, numAllele, alternateAlleles);
                    if (g != null) {
                        cohortStats.addGenotype(g, gtCount);
                    }
                }
            }
        }
    }

}

