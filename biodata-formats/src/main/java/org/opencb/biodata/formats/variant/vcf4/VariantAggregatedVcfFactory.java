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

package org.opencb.biodata.formats.variant.vcf4;

import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
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
    protected void parseSplitSampleData(StudyEntry variant, VariantSource source, String[] fields,
                                        String reference, String[] alternateAlleles)
            throws NonStandardCompliantSampleField {
        // Nothing to do
        variant.setSamplesPosition(Collections.emptyMap());
    }

    @Override
    protected void setOtherFields(Variant variant, VariantSource source, List<String> ids, float quality, String filter,
                                  String info, String format, String[] alternateAlleles, String line) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setIds(ids);
        StudyEntry sourceEntry = variant.getSourceEntry(source.getFileId(), source.getStudyId());
        if (quality > -1) {
            sourceEntry.addAttribute(source.getFileId(), StudyEntry.QUAL, String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            sourceEntry.addAttribute(source.getFileId(), StudyEntry.FILTER, filter);
        }
        Map<String, String> infoMap = getInfoMap(info);
        sourceEntry.setFormatAsString(format);
        sourceEntry.addAttribute(source.getFileId(), StudyEntry.SRC, line);
        sourceEntry.addAttributes(source.getFileId(), infoMap);
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

    protected void addInfo(Variant variant, StudyEntry file, int numAllele, Map<String, String> info) {
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
                    for (String sampleName : file.getSamplesName()) {
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
                    for (String sampleName : file.getSamplesName()) {
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


    protected void addAttributes(Variant variant, StudyEntry sourceEntry, int numAllele, String[] alternateAlleles,
                                 Map<String, String> infoMap) {

    }


    public static Genotype parseGenotype(String gt, int numAllele, String reference, String[] alternateAlleles) {
        Genotype g;
        Matcher m;

        List<String> alternates = Arrays.asList(alternateAlleles);
//        String alternates = variant.getAlternate();
        m = singleNuc.matcher(gt);

        if (m.matches()) { // A,C,T,G
            g = new Genotype(gt, reference, alternates);
            return g;
        }
        m = singleRef.matcher(gt);
        if (m.matches()) { // R
            g = new Genotype("0", reference, alternates);
            return g;
        }

        m = refAlt.matcher(gt);
        if (m.matches()) { // AA,AC,TT,GT,...
            String ref = m.group(1);
            String alt = m.group(2);

            int allele1 = (alternates.indexOf(ref) + 1);
            int allele2 = (alternates.indexOf(alt) + 1);

            int val1 = mapToMultiallelicIndex(allele1, numAllele);
            int val2 = mapToMultiallelicIndex(allele2, numAllele);

            return new Genotype(val1 + "/" + val2, reference, alternates);

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
            g = new Genotype(reference + "/" + reference, reference, alternates);
            return g;
        }

        m = altNum.matcher(gt);
        if (m.matches()) { // A1,A2,A3
            int val = Integer.parseInt(m.group(1));
            val = mapToMultiallelicIndex(val, numAllele);
            return new Genotype(Integer.toString(val), reference, alternates);
        }

        m = altNumaltNum.matcher(gt);
        if (m.matches()) { // A1A2,A1A3...
            int val1 = Integer.parseInt(m.group(1));
            int val2 = Integer.parseInt(m.group(2));
            val1 = mapToMultiallelicIndex(val1, numAllele);
            val2 = mapToMultiallelicIndex(val2, numAllele);
            return new Genotype(val1 + "/" + val2, reference, alternates);
        }

        m = altNumRef.matcher(gt);
        if (m.matches()) { // A1R, A2R
            int val1 = Integer.parseInt(m.group(1));
            val1 = mapToMultiallelicIndex(val1, numAllele);
            return new Genotype(val1 + "/" + 0, reference, alternates);
        }

        return null;
    }

}

