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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.models.variant.exceptions.NotAVariantException;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantVcfFactory implements VariantFactory {

    public static final String ORI = "ori";

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
        String id = fields[2].equals(".") ? "" : fields[2];
        Set<String> ids = new HashSet<>(Arrays.asList(id.split(";")));
        String reference = fields[3].equals(".") ? "" : fields[3];
        String alternate = fields[4];
//        String alternate = fields[4].equals(".") ? "" : fields[4];
        String[] alternateAlleles = alternate.split(",");
        float quality = fields[5].equals(".") ? -1 : Float.parseFloat(fields[5]);
        String filter = fields[6].equals(".") ? "" : fields[6];
        String info = fields[7].equals(".") ? "" : fields[7];
        String format = (fields.length <= 8 || fields[8].equals(".")) ? "" : fields[8];

        List<VariantKeyFields> generatedKeyFields = new ArrayList<>();

        for (int i = 0; i < alternateAlleles.length; i++) { // This index is necessary for getting the samples where the mutated allele is present
            String alt = alternateAlleles[i];
            VariantKeyFields keyFields;
            int referenceLen = reference.length();
            int alternateLen = alt.length();

            if (referenceLen == alternateLen) {
                keyFields = createVariantsFromSameLengthRefAlt(position, reference, alt);
            } else if (referenceLen == 0) {
                keyFields = createVariantsFromInsertionEmptyRef(position, alt);
            } else if (alternateLen == 0) {
                keyFields = createVariantsFromDeletionEmptyAlt(position, reference);
            } else {
                keyFields = createVariantsFromIndelNoEmptyRefAlt(position, reference, alt);
            }

            keyFields.setNumAllele(i);

            // Since the reference and alternate alleles won't necessarily match
            // the ones read from the VCF file but they are still needed for
            // instantiating the variants, they must be updated
            alternateAlleles[i] = keyFields.alternate;
            generatedKeyFields.add(keyFields);
        }

        // Now create all the Variant objects read from the VCF record
        for (int i = 0; i < alternateAlleles.length; i++) {
            VariantKeyFields keyFields = generatedKeyFields.get(i);
            Variant variant = new Variant(chromosome, keyFields.start, keyFields.end, keyFields.reference, keyFields.alternate);
            String[] secondaryAlternates = getSecondaryAlternates(variant, keyFields.getNumAllele(), alternateAlleles);
            VariantSourceEntry file = new VariantSourceEntry(source.getFileId(), source.getStudyId(), secondaryAlternates, format);
            variant.addSourceEntry(file);

            try {
                parseSplitSampleData(variant, source, fields, alternateAlleles, secondaryAlternates, i + 1);
                // Fill the rest of fields (after samples because INFO depends on them)
                setOtherFields(variant, source, ids, quality, filter, info, format, keyFields.getNumAllele(), alternateAlleles, line);
                file.addAttribute(ORI, fields[1] + ":" + fields[3] + ":" + fields[4] + ":" + i);
                variants.add(variant);
            } catch (NonStandardCompliantSampleField ex) {
                Logger.getLogger(VariantFactory.class.getName()).log(Level.SEVERE,
                        String.format("Variant %s:%d:%s>%s will not be saved\n%s",
                                chromosome, position, reference, alternateAlleles[i], ex.getMessage()));
            }
        }

        return variants;
    }

    /**
     * Calculates the start, end, reference and alternate of a SNV/MNV where the 
     * reference and the alternate are not empty. 
     * 
     * This task comprises 2 steps: removing the trailing bases that are 
     * identical in both alleles, then the leading identical bases.
     * 
     * @param position Input starting position
     * @param reference Input reference allele
     * @param alt Input alternate allele
     * @return The new start, end, reference and alternate alleles
     */
    protected VariantKeyFields createVariantsFromSameLengthRefAlt(int position, String reference, String alt) {
        int indexOfDifference;
        // Remove the trailing bases
        String refReversed = StringUtils.reverse(reference);
        String altReversed = StringUtils.reverse(alt);
        indexOfDifference = StringUtils.indexOfDifference(refReversed, altReversed);
        
        reference = StringUtils.reverse(refReversed.substring(indexOfDifference));
        alt = StringUtils.reverse(altReversed.substring(indexOfDifference));
        
        // Remove the leading bases
        indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return null;
        } else {
            int start = position + indexOfDifference;
            int end = position + reference.length() - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            return new VariantKeyFields(start, end, ref, inAlt);
        }
    }

    protected VariantKeyFields createVariantsFromInsertionEmptyRef(int position, String alt) {
        return new VariantKeyFields(position, position + alt.length() - 1, "", alt);
    }

    protected VariantKeyFields createVariantsFromDeletionEmptyAlt(int position, String reference) {
        return new VariantKeyFields(position, position + reference.length() - 1, reference, "");
    }

    /**
     * Calculates the start, end, reference and alternate of an indel where the 
     * reference and the alternate are not empty. 
     * 
     * This task comprises 2 steps: removing the trailing bases that are 
     * identical in both alleles, then the leading identical bases.
     * 
     * @param position Input starting position
     * @param reference Input reference allele
     * @param alt Input alternate allele
     * @return The new start, end, reference and alternate alleles
     */
    protected VariantKeyFields createVariantsFromIndelNoEmptyRefAlt(int position, String reference, String alt) {
        int indexOfDifference;
        // Remove the trailing bases
        String refReversed = StringUtils.reverse(reference);
        String altReversed = StringUtils.reverse(alt);
        indexOfDifference = StringUtils.indexOfDifference(refReversed, altReversed);
        
        reference = StringUtils.reverse(refReversed.substring(indexOfDifference));
        alt = StringUtils.reverse(altReversed.substring(indexOfDifference));
        
        // Remove the leading bases
        indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return null;
        } else if (indexOfDifference == 0) {
            if (reference.length() > alt.length()) { // Deletion
                return new VariantKeyFields(position, position + reference.length() - 1, reference, alt);
            } else { // Insertion
                return new VariantKeyFields(position, position + alt.length() - 1, reference, alt);
            }
        } else {
            if (reference.length() > alt.length()) { // Deletion
                int start = position + indexOfDifference;
                int end = position + reference.length() - 1;
                String ref = reference.substring(indexOfDifference);
                String inAlt = alt.substring(indexOfDifference);
                return new VariantKeyFields(start, end, ref, inAlt);
            } else { // Insertion
                int start = position + indexOfDifference;
                int end = position + alt.length() - 1;
                String ref = reference.substring(indexOfDifference);
                String inAlt = alt.substring(indexOfDifference);
                return new VariantKeyFields(start, end, ref, inAlt);
            }
        }
    }

    protected String[] getSecondaryAlternates(Variant variant, int numAllele, String[] alternateAlleles) {
        String[] secondaryAlternates = new String[alternateAlleles.length-1];
        for (int i = 0, j = 0; i < alternateAlleles.length; i++) {
            if (i != numAllele) {
                secondaryAlternates[j++] = alternateAlleles[i];
            }
        }
        return secondaryAlternates;
    }

    protected void parseSplitSampleData(Variant variant, VariantSource source, String[] fields, 
            String[] alternateAlleles, String[] secondaryAlternates, int alleleIdx) throws NonStandardCompliantSampleField {
        String[] formatFields = variant.getSourceEntry(source.getFileId(), source.getStudyId()).getFormat().split(":");
        List<String> samples = source.getSamples();

        for (int i = 9; i < fields.length; i++) {
            Map<String, String> map = new HashMap<>(5);

            // Fill map of a sample
            String[] sampleFields = fields[i].split(":");
            Genotype genotype = null;

            // Samples may remove the trailing fields (only GT is mandatory),
            // so the loop iterates to sampleFields.length, not formatFields.length
            for (int j = 0; j < sampleFields.length; j++) {
                String formatField = formatFields[j];
                String sampleField = sampleFields[j];

                if (formatField.equalsIgnoreCase("GT")) {
                    // Save alleles just in case they are necessary for GL/PL/GP transformation
                    genotype = new Genotype(sampleField, variant.getReference(), variant.getAlternate());

                    StringBuilder genotypeStr = new StringBuilder();
                    for (int allele : genotype.getAllelesIdx()) {
                        if (allele == 0) { // Reference
                            genotypeStr.append("0");
                        } else if (allele == alleleIdx) { // Current alternate
                            genotypeStr.append("1");
                        } else if (allele < 0) { // Missing
                            genotypeStr.append(".");
                        } else {
                            // Replace numerical indexes when they refer to another alternate allele
                            genotypeStr.append(String.valueOf(ArrayUtils.indexOf(secondaryAlternates, alternateAlleles[allele-1]) + 2));
                        }
                        genotypeStr.append(genotype.isPhased() ? "|" : "/");
                    }
                    sampleField = genotypeStr.substring(0, genotypeStr.length()-1);
                        
                } else if (formatField.equalsIgnoreCase("GL")
                        || formatField.equalsIgnoreCase("PL")
                        || formatField.equalsIgnoreCase("GP")) {
                    // All-alleles present and not haploid
                    if (!sampleField.equals(".") && genotype != null
                            && (genotype.getCode() == AllelesCode.ALLELES_OK
                            || genotype.getCode() == AllelesCode.MULTIPLE_ALTERNATES)) {
                        String[] likelihoods = sampleField.split(",");

                        // If only 3 likelihoods are represented, no transformation is needed
                        if (likelihoods.length > 3) {
                            // Get alleles index to work with: if both are the same alternate,
                            // the combinations must be run with the reference allele.
                            // Otherwise all GL reported would be alt/alt.
                            int allele1 = genotype.getAllele(0);
                            int allele2 = genotype.getAllele(1);
                            if (genotype.getAllele(0) == genotype.getAllele(1) && genotype.getAllele(0) > 0) {
                                allele1 = 0;
                            }

                            // If the number of values is not enough for this GT
                            int maxAllele = allele1 >= allele2 ? allele1 : allele2;
                            int numValues = (int) (((float) maxAllele * (maxAllele + 1)) / 2) + maxAllele;
                            if (likelihoods.length < numValues) {
                                throw new NonStandardCompliantSampleField(formatField, sampleField, String.format("It must contain %d values", numValues));
                            }

                            // Genotype likelihood must be distributed following similar criteria as genotypes
                            String[] alleleLikelihoods = new String[3];
                            alleleLikelihoods[0] = likelihoods[(int) (((float) allele1 * (allele1 + 1)) / 2) + allele1];
                            alleleLikelihoods[1] = likelihoods[(int) (((float) allele2 * (allele2 + 1)) / 2) + allele1];
                            alleleLikelihoods[2] = likelihoods[(int) (((float) allele2 * (allele2 + 1)) / 2) + allele2];
                            sampleField = StringUtils.join(alleleLikelihoods, ",");
                        }
                    }
                }

                map.put(formatField, sampleField);
            }

            // Add sample to the variant entry in the source file
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(samples.get(i - 9), map);
        }
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

    protected void setOtherFields(Variant variant, VariantSource source, Set<String> ids, float quality, String filter,
            String info, String format, int numAllele, String[] alternateAlleles, String line) {
        // Fields not affected by the structure of REF and ALT fields
        if (!ids.isEmpty()) {
            variant.setIds(ids);
        }
        if (quality > -1) {
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, source.getFileId(), source.getStudyId(), info, numAllele);
        }
        variant.getSourceEntry(source.getFileId(), source.getStudyId()).addAttribute("src", line);
    }

    protected void parseInfo(Variant variant, String fileId, String studyId, String info, int numAllele) {
        VariantSourceEntry file = variant.getSourceEntry(fileId, studyId);
        
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
                        for (String sampleName : file.getSampleNames()) {
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
                        file.addAttribute(splits[0], splits[1]);
                        break;
                }
            } else {
                variant.getSourceEntry(fileId, studyId).addAttribute(splits[0], "");
            }
        }
    }

    protected class VariantKeyFields {

        int start, end, numAllele;
        String reference, alternate;

        public VariantKeyFields(int start, int end, String reference, String alternate) {
            this.start = start;
            this.end = end;
            this.reference = reference;
            this.alternate = alternate;
        }

        public void setNumAllele(int numAllele) {
            this.numAllele = numAllele;
        }

        public int getNumAllele() {
            return numAllele;
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
