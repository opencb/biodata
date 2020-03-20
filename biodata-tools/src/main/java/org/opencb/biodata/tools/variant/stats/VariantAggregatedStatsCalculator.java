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

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.formats.variant.vcf4.VariantAggregatedVcfFactory;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.formats.variant.vcf4.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedStatsCalculator {
    protected Properties tagMap;
    protected Map<String, String> reverseTagMap;

    protected final static String COMMA = ",";
    protected final static String DOT = "\\.";   // a literal dot. extracted to avoid confusion and avoid using the wrong "." with split()
    private final static Pattern numNum = Pattern.compile("^(\\d+)[|/](\\d+)$");
    protected final static String cohortSeparator = "_";
    protected final List<String> statsTags = new ArrayList<>(Arrays.asList("AC", "AN", "AF", "GTC", "GTS"));

    public VariantAggregatedStatsCalculator() {
        this(null);
    }
    /**
     * @param tagMap Properties that contains case-sensitive tag mapping for aggregation data.
     * A valid example structure of this file is:
     * EUR.AF=EUR_AF
     * EUR.AC=AC_EUR
     * EUR.AN=EUR_AN
     * EUR.GTC=EUR_GTC
     * ALL.AF=AF
     * ALL.AC=TAC
     * ALL.AN=AN
     * ALL.GTC=GTC
     *
     * where the right side of the '=' is how the values appear in the vcf, and left side is how it will loaded.
     * It must be a bijection, i.e. there must not be repeated entries in any side.
     * The part before the '.' can be any string naming the group. The part after the '.' must be one of AF, AC, AN or GTC.
     */
    public VariantAggregatedStatsCalculator(Properties tagMap) {
        this.tagMap = tagMap;
        if (tagMap != null) {
            this.reverseTagMap = new LinkedHashMap<>(tagMap.size());
            for (String tag : tagMap.stringPropertyNames()) {
                this.reverseTagMap.put(tagMap.getProperty(tag), tag);
            }
        } else {
            this.reverseTagMap = null;
        }
    }

    public void calculate(List<Variant> variants) {
        for (Variant variant : variants) {
            calculate(variant);
        }
    }

    public void calculate(Variant variant) {
        for (StudyEntry study : variant.getStudies()) {
            calculate(variant, study);
        }
    }

    /**
     * @param variant remains unchanged if the VariantSourceEntry is not inside
     * @param study stats are written here
     */
    public void calculate(Variant variant, StudyEntry study) {
//        Map<String, String> infoMap = VariantAggregatedVcfFactory.getInfoMap(info);
        if (study.getFiles().isEmpty()) {
            return;
        }
        FileEntry fileEntry = study.getFiles().get(0);
        Map<String, String> infoMap = fileEntry.getData();
        int numAllele = 0;
        String reference = variant.getReference();
        String[] alternateAlleles;

        if (study.getSecondaryAlternates().isEmpty()) {
            alternateAlleles = new String[]{variant.getAlternate()};
        } else {
            List<String> secondaryAlternates = study.getSecondaryAlternates().stream().map(AlternateCoordinate::getAlternate).collect(Collectors.toList());
            secondaryAlternates.add(0, variant.getAlternate());
            alternateAlleles = secondaryAlternates.toArray(new String[secondaryAlternates.size()]);
        }
        if (tagMap != null) {
            parseMappedStats(variant, study, numAllele, reference, alternateAlleles, infoMap);
        } else {
            parseStats(variant, study, numAllele, reference, alternateAlleles, infoMap);
        }
    }

    /**
     * Looks for tags contained in statsTags and calculates stats parsing them.
     * @param variant
     * @param file
     * @param numAllele
     * @param reference
     * @param alternateAlleles
     * @param info
     */
    protected void parseStats(Variant variant, StudyEntry file, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        VariantStats vs = new VariantStats(StudyEntry.DEFAULT_COHORT);
        Map<String, String> stats = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : info.entrySet()) {

            String infoTag = entry.getKey();
            String infoValue = entry.getValue();

            if (statsTags.contains(infoTag)) {
                stats.put(infoTag, infoValue);
            }
        }

        calculate(variant, file, numAllele, reference, alternateAlleles, stats, vs);

        file.addStats(vs);
    }

    /**
     * Looks in the info map for keys present as values in the tagMap passed in the constructor
     * VariantAggregatedStatsCalculator(Properties tagMap), and calculates stats for each cohort described in the tagMap.
     * @param variant
     * @param file
     * @param numAllele
     * @param reference
     * @param alternateAlleles
     * @param info
     */
    protected void parseMappedStats(Variant variant, StudyEntry file, int numAllele, String reference, String[] alternateAlleles, Map<String, String> info) {
        Map<String, Map<String, String>> cohortStats = new LinkedHashMap<>();   // cohortName -> (statsName -> statsValue): EUR->(AC->3,2)
        for (Map.Entry<String, String> entry : info.entrySet()) {
            if (reverseTagMap.containsKey(entry.getKey())) {
                String opencgaTag = reverseTagMap.get(entry.getKey());
                String[] tagSplit = opencgaTag.split(DOT);
                String cohortName = tagSplit[0];
                String statName = tagSplit[1];
                Map<String, String> parsedValues = cohortStats.computeIfAbsent(cohortName, k -> new LinkedHashMap<>());
                parsedValues.put(statName, entry.getValue());
            }
        }

        for (String cohortName : cohortStats.keySet()) {
            VariantStats vs = new VariantStats(cohortName);
            calculate(variant, file, numAllele, reference, alternateAlleles, cohortStats.get(cohortName), vs);
            file.addStats(vs);
        }
    }

    /**
     * sets (if the map of fileData contains AF, AC, AF and GTC) alleleCount, refAlleleCount, maf, mafAllele, alleleFreq and genotypeCounts,
     * @param variant
     * @param studyEntry
     * @param numAllele
     * @param reference
     * @param alternateAlleles
     * @param fileData
     * @param variantStats results are returned by reference here
     */
    protected void calculate(Variant variant, StudyEntry studyEntry, int numAllele, String reference, String[] alternateAlleles,
                             Map<String, String> fileData, VariantStats variantStats) {

        if (fileData.containsKey("AN") && fileData.containsKey("AC")) {
            int total = Integer.parseInt(fileData.get("AN"));
            variantStats.setAlleleCount(total);
            String[] alleleCountString = fileData.get("AC").split(COMMA);

            if (alleleCountString.length != alternateAlleles.length) {
                return;
            }

            int[] alleleCount = new int[alleleCountString.length];

            String mafAllele = variant.getReference();
            int referenceCount = total;

            for (int i = 0; i < alleleCountString.length; i++) {
                alleleCount[i] = Integer.parseInt(alleleCountString[i]);
                if (i == numAllele) {
                    variantStats.setAltAlleleCount(alleleCount[i]);
                }
                referenceCount -= alleleCount[i];
            }

            variantStats.setRefAlleleCount(referenceCount);
            float maf = (float) referenceCount / total;

            for (int i = 0; i < alleleCount.length; i++) {
                float auxMaf = (float) alleleCount[i] / total;
                if (auxMaf < maf) {
                    maf = auxMaf;
                    mafAllele = alternateAlleles[i];
                }
            }

            variantStats.setMaf(maf);
            variantStats.setMafAllele(mafAllele);
        }

        if (fileData.containsKey("AF")) {
            String[] afs = fileData.get("AF").split(COMMA);
            if (afs.length == alternateAlleles.length) {
                float value = parseFloat(afs[numAllele], -1);
                variantStats.setAltAlleleFreq(value);
                variantStats.setRefAlleleFreq(1 - variantStats.getAltAlleleFreq());
                if (variantStats.getMaf() == -1) {  // in case that we receive AFs but no ACs
                    if (variantStats.getRefAlleleFreq() < 0) {
                        variantStats.setRefAlleleFreq(1 - variantStats.getAltAlleleFreq());
                    }

                    float sumFreq = 0;
                    for (String af : afs) {
                        sumFreq += parseFloat(af, -1);
                    }
                    float maf = 1 - sumFreq;
                    String mafAllele = variant.getReference();

                    for (int i = 0; i < afs.length; i++) {
                        float auxMaf = parseFloat(afs[i], -1);
                        if (auxMaf < maf) {
                            maf = auxMaf;
                            mafAllele = alternateAlleles[i];
                        }
                    }
                    variantStats.setMaf(maf);
                    variantStats.setMafAllele(mafAllele);
                }
            }
        }

        if (fileData.containsKey("MAF")) {
            String[] mafs = fileData.get("MAF").split(COMMA);
            if (mafs.length == alternateAlleles.length) {
                float maf = parseFloat(mafs[numAllele], -1);
                variantStats.setMaf(maf);
                if (fileData.containsKey("MA")) { // Get the minor allele
                    String ma = fileData.get("MA");
                    if (ma.equals("-")) {
                        ma = "";
                    }
                    variantStats.setMafAllele(ma);
                    if (variantStats.getAltAlleleFreq() < 0 || variantStats.getRefAlleleFreq() < 0) {
                        if (ma.equals(variant.getReference())) {
                            variantStats.setRefAlleleFreq(maf);
                            variantStats.setAltAlleleFreq(1 - maf);
                        } else if (ma.equals(variant.getAlternate())) {
                            variantStats.setRefAlleleFreq(1 - maf);
                            variantStats.setAltAlleleFreq(maf);
                        } // It may happen that the MA is none of the variant alleles. Just skip
                    }
                }
            }
        }
        if (fileData.containsKey("GTC")) {
            String[] gtcs = fileData.get("GTC").split(COMMA);
            if (fileData.containsKey("GTS")) {    // GTS contains the format like: GTS=GG,GT,TT or GTS=A1A1,A1R,RR
                addGenotypeWithGTS(fileData, gtcs, reference, alternateAlleles, numAllele, variantStats);
            } else {
                // Het count is a non standard field that can not be rearranged when decomposing multi-allelic variants.
                // Get the original variant call to parse this field
                FileEntry fileEntry = studyEntry.getFiles().get(0);
                int numAlleleOri;
                String[] alternateAllelesOri;
                if (fileEntry.getCall() != null && !fileEntry.getCall().isEmpty()) {
                    String[] ori = fileEntry.getCall().split(":");
                    numAlleleOri = Integer.parseInt(ori[3]);
                    alternateAllelesOri = ori[2].split(",");
                } else {
                    numAlleleOri = numAllele;
                    alternateAllelesOri = alternateAlleles;
                }

                for (int i = 0; i < gtcs.length; i++) {
                    String[] gtcSplit = gtcs[i].split(":");
                    Integer alleles[] = new Integer[2];
                    Integer gtc = 0;
                    String gt = null;
                    boolean parseable = true;
                    if (gtcSplit.length == 1) { // GTC=0,5,8
                        getGenotype(i, alleles);
                        gtc = Integer.parseInt(gtcs[i]);
                        gt = VariantVcfFactory.mapToMultiallelicIndex(alleles[0], numAlleleOri) + "/" + VariantVcfFactory.mapToMultiallelicIndex(alleles[1], numAlleleOri);
                    } else {    // GTC=0/0:0,0/1:5,1/1:8
                        Matcher matcher = numNum.matcher(gtcSplit[0]);
                        if (matcher.matches()) {    // number/number:number
                            alleles[0] = Integer.parseInt(matcher.group(1));
                            alleles[1] = Integer.parseInt(matcher.group(2));
                            gtc = Integer.parseInt(gtcSplit[1]);
                            gt = VariantVcfFactory.mapToMultiallelicIndex(alleles[0], numAlleleOri) + "/" + VariantVcfFactory.mapToMultiallelicIndex(alleles[1], numAlleleOri);
                        } else {
                            if (gtcSplit[0].equals("./.")) {    // ./.:number
                                alleles[0] = -1;
                                alleles[1] = -1;
                                gtc = Integer.parseInt(gtcSplit[1]);
                                gt = "./.";
                            } else {
                                parseable = false;
                            }
                        }
                    }
                    if (parseable) {
                        Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAlleleOri]);
                        variantStats.addGenotype(genotype, gtc);
                    }
                }

                VariantStatsCalculator.calculateGenotypeFrequencies(variantStats);
            }
        }

        calculateFilterQualStats(fileData, variantStats);
    }

    protected void calculateFilterQualStats(Map<String, String> fileData, VariantStats variantStats) {
        String filter = fileData.get(StudyEntry.FILTER);
        if (StringUtils.isNotEmpty(filter)) {
            VariantStatsCalculator.addFileFilter(filter, variantStats.getFilterCount());
            VariantStatsCalculator.calculateFilterFreq(variantStats, 1);
        }

        String qual = fileData.get(StudyEntry.QUAL);
        if (StringUtils.isNotEmpty(qual) && !qual.equals(".")) {
            variantStats.setQualityAvg(Float.valueOf(qual));
        }
    }

    protected float parseFloat(String s, float missingValue) {
        if (s.equals(".")) {
            return missingValue;
        } else {
            return Float.parseFloat(s);
        }
    }

    public static void addGenotypeWithGTS(Map<String, String> fileData, String[] splitsGTC,
                                          String reference, String[] alternateAlleles, int numAllele, VariantStats cohortStats) {
        if (fileData.containsKey("GTS")) {
            String splitsGTS[] = fileData.get("GTS").split(COMMA);
            if (splitsGTC.length == splitsGTS.length) {
                for (int i = 0; i < splitsGTC.length; i++) {
                    String gt = splitsGTS[i];
                    int gtCount = Integer.parseInt(splitsGTC[i]);

                    Genotype g = VariantAggregatedVcfFactory.parseGenotype(gt, numAllele, reference, alternateAlleles);
                    if (g != null) {
                        cohortStats.addGenotype(g, gtCount);
                    }
                }
            }
        }
    }

    public static Set<String> getCohorts(Properties tagMap) {
        Set<String> cohorts = null;
        if (tagMap != null) {
            cohorts = new LinkedHashSet<>();
            for (String key : tagMap.stringPropertyNames()) {
                int index = key.indexOf(".");
                if (index >= 0) {
                    cohorts.add(key.substring(0, index));
                }
            }
        }
        
        return cohorts;
    }

    /**
     * returns in alleles[] the genotype specified in index in the sequence:
     * 0/0, 0/1, 1/1, 0/2, 1/2, 2/2, 0/3...
     * @param index in this sequence, starting in 0
     * @param alleles returned genotype.
     */
    public static void getGenotype(int index, Integer alleles[]) {
//        index++;
//        double value = (-3 + Math.sqrt(1 + 8 * index)) / 2;    // slower than the iterating version, right?
//        alleles[1] = new Double(Math.ceil(value)).intValue();
//        alleles[0] = alleles[1] - ((alleles[1] + 1) * (alleles[1] +2) / 2 - index);

        int cursor = 0;
        final int MAX_ALLOWED_ALLELES = 100;   // should we allow more than 100 alleles?
        for (int i = 0; i < MAX_ALLOWED_ALLELES; i++) {
            for (int j = 0; j <= i; j++) {
                if (cursor == index) {
                    alleles[0] = j;
                    alleles[1] = i;
                    return;
                }
                cursor++;
            }
        }
    }
}
