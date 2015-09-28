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

    private final static Pattern singleNuc = Pattern.compile("^[ACTG]$");
    private final static Pattern singleRef = Pattern.compile("^R$");
    private final static Pattern refAlt = Pattern.compile("^([ACTG])([ACTG])$");
    private final static Pattern refRef = Pattern.compile("^R{2}$");
    private final static Pattern altNum = Pattern.compile("^A(\\d+)$");
    private final static Pattern altNumaltNum = Pattern.compile("^A(\\d+)A(\\d+)$");
    private final static Pattern altNumRef = Pattern.compile("^A(\\d+)R$");
    protected final static String COMMA = ",";
    protected final static String DOT = "\\.";   // a literal dot. extracted to avoid confusion and avoid using the wrong "." with split()


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
    }

    public static Map<String, String> getInfoMap(String info) {
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
                // next is commented to store the AC, AF and AN as-is, to be able to compute stats from the DB using the attributes, and "ori" tag
//                case "AC":
//                    String[] counts = infoValue.split(COMMA);
//                    file.addAttribute(infoTag, counts[numAllele]);
//                    break;
//                case "AF":
//                    String[] frequencies = infoValue.split(COMMA);
//                    file.addAttribute(infoTag, frequencies[numAllele]);
//                    break;
//                    case "AN":
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


    protected void addAttributes(Variant variant, VariantSourceEntry sourceEntry, int numAllele, String[] alternateAlleles,
                                 Map<String, String> infoMap) {

    }


    public static Genotype parseGenotype(String gt, Variant variant, int numAllele, String[] alternateAlleles) {
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

}

