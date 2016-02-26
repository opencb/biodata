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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.exceptions.NotAVariantException;

import static org.opencb.biodata.models.variant.VariantNormalizer.*;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantVcfFactory implements VariantFactory {

    @Deprecated
    public static final String ORI = "ori";

    public static final String QUAL = "QUAL";
    public static final String FILTER = "FILTER";
    public static final String SRC = "src";

    private final VariantNormalizer variantNormalizer = new VariantNormalizer();

    /**
     * Creates a list of Variant objects using the fields in a record of a VCF
     * file. A new Variant object is created per allele, so several of them can
     * be created from a single line.
     * 
     * Start/end coordinates assignment tries to work as similarly as possible 
     * as Ensembl does, except for insertions, where start is greater than end: 
     * http://www.ensembl.org/info/docs/tools/vep/vep_formats.html#vcf
     *
     * @param source Origin of the variants information
     * @param line Contents of the line in the file
     * @return The list of Variant objects that can be created using the fields
     * from a VCF record
     */
    @Override
    public List<Variant> create(VariantSource source, String line) throws IllegalArgumentException, NotAVariantException {
        String[] fields = line.split("\t");
        if (fields.length < 8) {
            throw new IllegalArgumentException("Not enough fields provided (min 8)");
        }
        if(fields[4].equals(".")) {
            throw new NotAVariantException("Alternative allele is a '.'. This is not an actual variant but a reference position.");
        }

        List<Variant> variants = new LinkedList<>();

        String chromosome = fields[0];
        int position = Integer.parseInt(fields[1]);
        String id = fields[2].equals(".") ? null : fields[2];
        List<String> ids = id == null? Collections.emptyList() : Arrays.asList(id.split(";"));
        String reference = fields[3].equals(".") ? "" : fields[3];
        String alternate = fields[4];
//        String alternate = fields[4].equals(".") ? "" : fields[4];
        String[] alternateAlleles = alternate.split(",");
        float quality = fields[5].equals(".") ? -1 : Float.parseFloat(fields[5]);
        String filter = fields[6].equals(".") ? "" : fields[6];
        String info = fields[7].equals(".") ? "" : fields[7];
        String format = (fields.length <= 8 || fields[8].equals(".")) ? "" : fields[8];

        List<VariantKeyFields> generatedKeyFields = variantNormalizer.normalize(position, reference, Arrays.asList(alternateAlleles));

        for (int i = 0; i < generatedKeyFields.size(); i++) {

            // Since the reference and alternate alleles won't necessarily match
            // the ones read from the VCF file but they are still needed for
            // instantiating the variants, they must be updated
            alternateAlleles[i] = generatedKeyFields.get(i).getAlternate();
        }

        // Now create all the Variant objects read from the VCF record
        for (int i = 0; i < alternateAlleles.length; i++) {
            VariantKeyFields keyFields = generatedKeyFields.get(i);
            Variant variant = new Variant(chromosome, keyFields.getStart(), keyFields.getEnd(), keyFields.getReference(), keyFields.getAlternate());
//            String[] secondaryAlternates = getSecondaryAlternates(variant, keyFields.getNumAllele(), alternateAlleles);
            List<AlternateCoordinate> secondaryAlternatesMap = variantNormalizer.getSecondaryAlternatesMap(keyFields, generatedKeyFields);
            StudyEntry entry = new StudyEntry(source.getStudyId(), secondaryAlternatesMap, Arrays.asList(format.split(":")));
            entry.setFileId(source.getFileId());
            variant.addStudyEntry(entry);

            try {
                parseSplitSampleData(entry, source, fields, reference, alternateAlleles, keyFields);
                // Fill the rest of fields (after samples because INFO depends on them)
                setOtherFields(variant, source, ids, quality, filter, info, format, keyFields.getNumAllele(), alternateAlleles, line);
                if (alternateAlleles.length > 1
                        || keyFields.getStart() != variant.getStart()
                        || keyFields.getEnd() != variant.getEnd()
                        || !Objects.equals(keyFields.getAlternate(), variant.getAlternate())
                        || !Objects.equals(keyFields.getReference(), variant.getReference())) {
                    entry.getFile(source.getFileId()).setCall(fields[1] + ":" + fields[3] + ":" + fields[4] + ":" + keyFields.getNumAllele());
                }
                variants.add(variant);
            } catch (NonStandardCompliantSampleField ex) {
                Logger.getLogger(VariantFactory.class.getName()).log(Level.SEVERE,
                        String.format("Variant %s:%d:%s>%s will not be saved\n%s",
                                chromosome, position, reference, alternateAlleles[i], ex.getMessage()));
            }
        }

        return variants;
    }

    protected void parseSplitSampleData(StudyEntry entry, VariantSource source, String[] fields,
                                        String reference, String[] alternateAlleles, VariantKeyFields variantKeyFields) throws NonStandardCompliantSampleField {
//        List<String> formatFields = variant.getSourceEntry(source.getFileId(), source.getStudyId()).getFormat();

        if (fields.length < 9) {
            entry.setSamplesData(Collections.emptyList());
            entry.setSamplesPosition(Collections.emptyMap());
            return;
        }
        List<String> formatFields = Arrays.asList(fields[8].split(":"));
        entry.setSamplesPosition(source.getSamplesPosition());

        List<List<String>> samplesData = Arrays.asList(new List[fields.length - 9]);
        for (int i = 9; i < fields.length; i++) {
            List<String> data = Arrays.asList(fields[i].split(":"));
            if (data.size() < formatFields.size()) {
                List<String> correctSizeData = new ArrayList<>(formatFields.size());
                correctSizeData.addAll(data);
                while (correctSizeData.size() < formatFields.size()) {
                    correctSizeData.add(".");
                }
                data = correctSizeData;
            }
            samplesData.set(i - 9, data);
        }

        samplesData = variantNormalizer.normalizeSamplesData(variantKeyFields, samplesData, formatFields, reference, Arrays.asList(alternateAlleles), null);

        // Add samples data to the variant entry in the source file
        entry.setSamplesData(samplesData);
    }

    /**
     * Checks whether a sample should be included in a variant's list of
     * samples. If current allele index is not found in the genotype and not all
     * alleles are references/missing, then the sample must not be included.
     *
     * @param genotype The genotype
     * @param alleleIdx The index of the allele
     * @return If the sample should be associated to the variant
     */
    private boolean shouldAddSampleToVariant(String genotype, int alleleIdx) {
        if (genotype.contains(String.valueOf(alleleIdx))) {
            return true;
        }
        
        if (!genotype.contains("0") && !genotype.contains(".")) {
            return false;
        }
        
        String[] alleles = genotype.split("[/|]");
        for (String allele : alleles) {
            if (!allele.equals("0") && !allele.equals(".")) {
                return false;
            }
        }
        
        return true;
    }

    protected void setOtherFields(Variant variant, VariantSource source, List<String> ids, float quality, String filter,
            String info, String format, int numAllele, String[] alternateAlleles, String line) {
        // Fields not affected by the structure of REF and ALT fields
        if (!ids.isEmpty()) {
            variant.setIds(ids);
        }
        if (quality > -1) {
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute(QUAL, String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute(FILTER, filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, source.getFileId(), source.getStudyId(), info, numAllele);
        }
        variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute(SRC, line);
    }

    protected void parseInfo(Variant variant, String fileId, String studyId, String info, int numAllele) {
        StudyEntry file = variant.getSourceEntry(fileId, studyId);
        
        for (String var : info.split(";")) {
            String[] splits = var.split("=");
            if (splits.length == 2) {
                switch (splits[0]) {
                    case "ACC":
                        // Managing accession ID for the allele
                        String[] ids = splits[1].split(",");
                        file.addAttribute(splits[0], ids[numAllele]);
                        break;

                // next is commented to store the AC, AF and AN as-is, to be able to compute stats from the DB using the attributes, and "ori" tag
//                    case "AC":
//                        // TODO For now, only one alternate is supported
//                        String[] counts = splits[1].split(",");
//                        file.addAttribute(splits[0], counts[numAllele]);
//                        break;
//                    case "AF":
//                         // TODO For now, only one alternate is supported
//                        String[] frequencies = splits[1].split(",");
//                        file.addAttribute(splits[0], frequencies[numAllele]);
//                        break;
//                    case "AN":
//                        // TODO For now, only two alleles (reference and one alternate) are supported, but this should be changed
//                        file.addAttribute(splits[0], "2");
//                        break;
                    case "NS":
                        // Count the number of samples that are associated with the allele
                        file.addAttribute(splits[0], String.valueOf(file.getSamplesData().size()));
                        break;
                    case "DP":
                        int dp = 0;
                        for (String sampleName : file.getSamplesName()) {
                            String sampleDp = file.getSampleData(sampleName, "DP");
                            if (StringUtils.isNumeric(sampleDp)) {
                                dp += Integer.parseInt(sampleDp);
                            }
                        }
                        file.addAttribute(splits[0], String.valueOf(dp));
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
                        file.addAttribute(splits[0], splits[1]);
                        break;
                }
            } else {
                variant.getSourceEntry(fileId, studyId).addAttribute(splits[0], "");
            }
        }
    }

    /**
     * In multiallelic variants, we have a list of alternates, where numAllele is the one whose variant we are parsing now.
     * If we are parsing the first variant (numAllele == 0) A1 refers to first alternative, (i.e. alternateAlleles[0]), A2 to 
     * second alternative (alternateAlleles[1]), and so on.
     * However, if numAllele == 1, A1 refers to second alternate (alternateAlleles[1]), A2 to first (alternateAlleles[0]) and higher alleles remain unchanged.
     * Moreover, if NumAllele == 2, A1 is third alternate, A2 is first alternate and A3 is second alternate.
     * It's also assumed that A0 would be the reference, so it remains unchanged too.
     *
     * This pattern of the first allele moving along (and swapping) is what describes this function. 
     * Also, look VariantVcfFactory.getSecondaryAlternates().
     * @param parsedAllele the value of parsed alleles. e.g. 1 if genotype was "A1" (first allele).
     * @param numAllele current variant of the alternates.
     * @return the correct allele index depending on numAllele.
     */
    public static int mapToMultiallelicIndex (int parsedAllele, int numAllele) {
        int correctedAllele = parsedAllele;
        if (parsedAllele > 0) {
            if (parsedAllele == numAllele + 1) {
                correctedAllele = 1;
            } else if (parsedAllele < numAllele + 1) {
                correctedAllele = parsedAllele + 1;
            }
        }
        return correctedAllele;
    }
}
