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

package org.opencb.biodata.models.variant;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactory extends VariantVcfFactory {

    private final Pattern singleNuc = Pattern.compile("^[ACTG]$");
    private final Pattern singleRef = Pattern.compile("^R$");
    private final Pattern refAlt = Pattern.compile("^([ACTG])([ACTG])$");
    private final Pattern refRef = Pattern.compile("^R{2}$");
    private final Pattern altNum = Pattern.compile("^A(\\d+)$");
    private final Pattern altNumaltNum = Pattern.compile("^A(\\d+)A(\\d+)$");
    private final Pattern altNumRef = Pattern.compile("^A(\\d+)R$");
    protected final String COMMA = ",";
    protected final String DOT = "\\.";   // a literal dot. extracted to avoid confusion and avoid using the wrong "." with split()

    private final Pattern numNum = Pattern.compile("^(\\d+)[|/](\\d+)$");

    protected Properties tagMap;
    protected Map<String, String> reverseTagMap;

    public VariantAggregatedVcfFactory() {
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
    public VariantAggregatedVcfFactory(Properties tagMap) {
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
    @Override
    protected void parseSplitSampleData(Variant variant, VariantSource source, String[] fields,
                                        String[] alternateAlleles, String[] secondaryAlternates, int alleleIdx)
            throws NonStandardCompliantSampleField {
        // Nothing to do
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
        Map<String, String> infoMap = getInfoMap(info);
        addInfo(variant, sourceEntry, numAllele, infoMap);
        sourceEntry.setFormat(format);
        sourceEntry.addAttribute("src", line);

        VariantSourceEntry file = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        if (tagMap == null) {
            parseStats(variant, file, numAllele, alternateAlleles, infoMap);
        } else {
            parseCohortStats(variant, file, numAllele, alternateAlleles, infoMap);
        }
    }

    protected Map<String, String> getInfoMap(String info) {
        String[] splittedInfo = info.split(";");
        Map<String, String> map = new HashMap<>(splittedInfo.length);
        for (String attribute : splittedInfo) {
            String[] assignment = attribute.split("=");
            if (assignment.length == 2) {
                map.put(assignment[0], assignment[1]);
            } else {
                map.put(assignment[0], "");
            }
        }
        return map;
    }

    protected void addInfo(Variant variant, VariantSourceEntry file, int numAllele, Map<String, String> info) {
        for (Map.Entry<String, String> infoElement : info.entrySet()) {

            String infoTag = infoElement.getKey();
            String infoValue = infoElement.getValue();

            switch (infoTag) {
                case "ACC":
                    // Managing accession ID for the allele
                    String[] ids = infoValue.split(COMMA);
                    file.addAttribute(infoTag, ids[numAllele]);
                    break;
                case "AC":
                    // TODO For now, only one alternate is supported
                    String[] counts = infoValue.split(COMMA);
                    file.addAttribute(infoTag, counts[numAllele]);
                    break;
                case "AF":
                    // TODO For now, only one alternate is supported
                    String[] frequencies = infoValue.split(COMMA);
                    file.addAttribute(infoTag, frequencies[numAllele]);
                    break;
//                    case "AN":
//                        // TODO For now, only two alleles (reference and one alternate) are supported, but this should be changed
//                        file.addAttribute(infoTag, "2");
//                        break;
                case "NS":
                    // Count the number of samples that are associated with the allele
                    file.addAttribute(infoTag, String.valueOf(file.getSamplesData().size()));
                    break;
                case "DP":
                    int dp = 0;
                    for (String sampleName : file.getSampleNames()) {
                        String sampleDp = file.getSampleData(sampleName, "DP");
                        if (StringUtils.isNumeric(sampleDp)) {
                            dp += Integer.parseInt(sampleDp);
                        }
                    }
                    file.addAttribute(infoTag, String.valueOf(dp));
                    break;
                case "MQ":
                case "MQ0":
                    int mq = 0;
                    int mq0 = 0;
                    for (String sampleName : file.getSampleNames()) {
                        if (StringUtils.isNumeric(file.getSampleData(sampleName, "GQ"))) {
                            int gq = Integer.parseInt(file.getSampleData(sampleName, "GQ"));
                            mq += gq * gq;
                            if (gq == 0) {
                                mq0++;
                            }
                        }
                    }
                    file.addAttribute("MQ", String.valueOf(mq));
                    file.addAttribute("MQ0", String.valueOf(mq0));
                    break;
                default:
                    file.addAttribute(infoTag, infoValue);
                    break;
            }
        }
    }

    protected void parseStats(Variant variant, VariantSourceEntry file, int numAllele, String[] alternateAlleles, Map<String, String> info) {
        VariantStats vs = new VariantStats(variant);
        Map<String, String> stats = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : info.entrySet()) {

            String infoTag = entry.getKey();
            String infoValue = entry.getValue();

            if ((infoTag.equals("AC") || infoTag.equals("AN") || infoTag.equals("AF")
                    || infoTag.equals("GTC") || infoTag.equals("GTS"))) {
                stats.put(infoTag, infoValue);
            }
        }

        addStats(variant, file, numAllele, alternateAlleles, stats, vs);

        file.setStats(vs);
    }

    protected void parseCohortStats (Variant variant, VariantSourceEntry file, int numAllele, String[] alternateAlleles, Map<String, String> info) {
        Map<String, Map<String, String>> cohortStats = new LinkedHashMap<>();   // cohortName -> (statsName -> statsValue): EUR->(AC->3,2)
        for (Map.Entry<String, String> entry : info.entrySet()) {
            if (reverseTagMap.containsKey(entry.getKey())) {
                String opencgaTag = reverseTagMap.get(entry.getKey());
                String[] tagSplit = opencgaTag.split(DOT);
                String cohortName = tagSplit[0];
                String statName = tagSplit[1];
                Map<String, String> parsedValues = cohortStats.get(cohortName);
                if (parsedValues == null) {
                    parsedValues = new LinkedHashMap<>();
                    cohortStats.put(cohortName, parsedValues);
                }
                parsedValues.put(statName, entry.getValue());
            }
        }

        for (String cohortName : cohortStats.keySet()) {
            VariantStats vs = new VariantStats(variant);
            addStats(variant, file, numAllele, alternateAlleles, cohortStats.get(cohortName), vs);
            file.setCohortStats(cohortName, vs);
        }

    }

    protected void addAttributes(Variant variant, VariantSourceEntry sourceEntry, int numAllele, String[] alternateAlleles,
                                 Map<String, String> infoMap) {

    }

    /**
     * sets (if the map of attributes contains AF, AC, AF and GTC) alleleCount, refAlleleCount, maf, mafAllele, alleleFreq and genotypeCounts,
     * @param variant
     * @param sourceEntry
     * @param numAllele
     * @param alternateAlleles
     * @param attributes
     * @param variantStats
     */
    protected void addStats(Variant variant, VariantSourceEntry sourceEntry, int numAllele, String[] alternateAlleles,
                            Map<String, String> attributes, VariantStats variantStats) {

        if (attributes.containsKey("AN") && attributes.containsKey("AC")) {
            int total = Integer.parseInt(attributes.get("AN"));
            String[] alleleCountString = attributes.get("AC").split(COMMA);

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

        if (attributes.containsKey("AF")) {
            String[] afs = attributes.get("AF").split(COMMA);
            if (afs.length == alternateAlleles.length) {
                variantStats.setAltAlleleFreq(Float.parseFloat(afs[numAllele]));
                if (variantStats.getMaf() == -1) {  // in case that we receive AFs but no ACs
                    float sumFreq = 0;
                    for (String af : afs) {
                        sumFreq += Float.parseFloat(af);
                    }
                    float maf = 1 - sumFreq;
                    String mafAllele = variantStats.getRefAllele();

                    for (int i = 0; i < afs.length; i++) {
                        float auxMaf = Float.parseFloat(afs[i]);
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
        if (attributes.containsKey("GTC")) {
            String[] gtcs = attributes.get("GTC").split(COMMA);
            if (sourceEntry.hasAttribute("GTS")) {    // GTS contains the format like: GTS=GG,GT,TT or GTS=A1A1,A1R,RR
                addGenotypeWithGTS(variant, sourceEntry, gtcs, alternateAlleles, numAllele, variantStats);
            } else {
                for (int i = 0; i < gtcs.length; i++) {
                    String[] gtcSplit = gtcs[i].split(":");
                    Integer alleles[] = new Integer[2];
                    Integer gtc = 0;
                    String gt = null;
                    boolean parseable = true;
                    if (gtcSplit.length == 1) { // GTC=0,5,8
                        getGenotype(i, alleles);
                        gtc = Integer.parseInt(gtcs[i]);
                        gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
                    } else {    // GTC=0/0:0,0/1:5,1/1:8
                        Matcher matcher = numNum.matcher(gtcSplit[0]);
                        if (matcher.matches()) {    // number/number:number
                            alleles[0] = Integer.parseInt(matcher.group(1));
                            alleles[1] = Integer.parseInt(matcher.group(2));
                            gtc = Integer.parseInt(gtcSplit[1]);
                            gt = mapToMultiallelicIndex(alleles[0], numAllele) + "/" + mapToMultiallelicIndex(alleles[1], numAllele);
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
                        Genotype genotype = new Genotype(gt, variant.getReference(), alternateAlleles[numAllele]);
                        variantStats.addGenotype(genotype, gtc);
                    }
                }
            }
        }

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

    protected Genotype parseGenotype(String gt, Variant variant, int numAllele, String[] alternateAlleles) {
        Genotype g;
        Matcher m;

        m = singleNuc.matcher(gt);

        if (m.matches()) { // A,C,T,G
            g = new Genotype(gt + "/" + gt, variant.getReference(), variant.getAlternate());
            return g;
        }
        m = singleRef.matcher(gt);
        if (m.matches()) { // R
            g = new Genotype(variant.getReference() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        m = refAlt.matcher(gt);
        if (m.matches()) { // AA,AC,TT,GT,...
            String ref = m.group(1);
            String alt = m.group(2);

            int allele1 = (Arrays.asList(alternateAlleles).indexOf(ref) + 1);
            int allele2 = (Arrays.asList(alternateAlleles).indexOf(alt) + 1);

            int val1 = mapToMultiallelicIndex(allele1, numAllele);
            int val2 = mapToMultiallelicIndex(allele2, numAllele);

            return new Genotype(val1 + "/" + val2, variant.getReference(), variant.getAlternate());

//            if ((allele1 == 0 || allele1 == (numAllele + 1)) && (allele2 == 0 || allele2 == (numAllele + 1))) {
//
//                allele1 = allele1 > 1 ? 1 : allele1;
//                allele2 = allele2 > 1 ? 1 : allele2;
//                g = new Genotype(allele1 + "/" + allele2, variant.getReference(), variant.getAlternate());
//
//                return g;
//            } else {
//                return new Genotype("./.", variant.getReference(), variant.getAlternate());
//            }
        }

        m = refRef.matcher(gt);
        if (m.matches()) { // RR
            g = new Genotype(variant.getReference() + "/" + variant.getReference(), variant.getReference(), variant.getAlternate());
            return g;
        }

        m = altNum.matcher(gt);
        if (m.matches()) { // A1,A2,A3
            int val = Integer.parseInt(m.group(1));
            val = mapToMultiallelicIndex(val, numAllele);
            return new Genotype(val + "/" + val, variant.getReference(), variant.getAlternate());
        }

        m = altNumaltNum.matcher(gt);
        if (m.matches()) { // A1A2,A1A3...
            int val1 = Integer.parseInt(m.group(1));
            int val2 = Integer.parseInt(m.group(2));
            val1 = mapToMultiallelicIndex(val1, numAllele);
            val2 = mapToMultiallelicIndex(val2, numAllele);
            return new Genotype(val1 + "/" + val2, variant.getReference(), variant.getAlternate());
        }

        m = altNumRef.matcher(gt);
        if (m.matches()) { // A1R, A2R
            int val1 = Integer.parseInt(m.group(1));
            val1 = mapToMultiallelicIndex(val1, numAllele);
            return new Genotype(val1 + "/" + 0, variant.getReference(), variant.getAlternate());
        }

        return null;
    }

    protected void addGenotypeWithGTS(Variant variant, VariantSourceEntry sourceEntry, String[] splitsGTC, String[] alternateAlleles
            , int numAllele, VariantStats cohortStats) {
        if (sourceEntry.hasAttribute("GTS")) {
            String splitsGTS[] = sourceEntry.getAttribute("GTS").split(COMMA);
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

