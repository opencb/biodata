package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantVcfFactory implements VariantFactory {

    /**
     * Creates a list of Variant objects using the fields in a record of a VCF
     * file. A new Variant object is created per allele, so several of them can
     * be created from a single line.
     *
     * @param source
     * @param line   Contents of the line in the file
     * @return The list of Variant objects that can be created using the fields from a VCF record
     */
    public List<Variant> create(VariantSource source, String line) throws IllegalArgumentException {

        String[] fields = line.split("\t");
        if (fields.length < 8) {
            throw new IllegalArgumentException("Not enough fields provided (min 8)");
        }

        List<Variant> variants = new LinkedList<>();

        String chromosome = fields[0];
        int position = Integer.parseInt(fields[1]);
        String id = fields[2].equals(".") ? "" : fields[2];
        String reference = fields[3].equals(".") ? "" : fields[3];
        String alternate = fields[4].equals(".") ? "" : fields[4];
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
            variant.addFile(new ArchivedVariantFile(source.getFileName(), source.getFileId(), source.getStudyId()));
            setOtherFields(variant, source, id, quality, filter, info, format, keyFields.getNumAllele(), alternateAlleles);

            try {
                // Copy only the samples that correspond to each specific mutation
                parseSplitSampleData(variant, source.getFileId(), fields, source.getSamples(), alternateAlleles, i + 1);
                variants.add(variant);
            } catch (NonStandardCompliantSampleField ex) {
                Logger.getLogger(VariantFactory.class.getName()).log(Level.SEVERE,
                        String.format("Variant %s:%d:%s>%s will not be saved\n%s",
                                chromosome, position, reference, alternateAlleles[i], ex.getMessage()));
            }
        }

        return variants;
    }

    protected void parseSplitSampleData(Variant variant, String fileId, String[] fields, List<String> sampleNames,
                                        String[] alternateAlleles, int alleleIdx) throws NonStandardCompliantSampleField {
        String[] formatFields = variant.getFile(fileId).getFormat().split(":");

        for (int i = 9; i < fields.length; i++) {
            Map<String, String> map = new HashMap<>(5);

            // Fill map of a sample
            boolean shouldAddSample = true;
            String[] sampleFields = fields[i].split(":");
            Genotype genotype = null;

            for (int j = 0; j < formatFields.length; j++) {
                String formatField = formatFields[j];
                String sampleField = sampleFields[j];

                if (formatField.equalsIgnoreCase("GT")) {
                    shouldAddSample = shouldAddSampleToVariant(sampleField, alleleIdx);

                    if (shouldAddSample) {
                        // Save alleles just in case they are necessary for GL/PL/GP transformation
                        genotype = new Genotype(sampleField, variant.getReference(), variant.getAlternate());

                        // Replace numerical indexes with the bases
                        // TODO Could this be done with Java 8 streams? :)
//                        sampleField = sampleField.replace("0", variant.getReference());
//                        for (int k = 0; k < alternateAlleles.length; k++) {
//                            sampleField = sampleField.replace(String.valueOf(k+1), alternateAlleles[k]);
//                        }
                        // Replace numerical indexes when they refer to another alternate allele
                        for (int k = 0; k < alternateAlleles.length; k++) {
                            if (k + 1 != alleleIdx) {
                                sampleField = sampleField.replace(String.valueOf(k + 1), alternateAlleles[k]);
                            } else {
                                sampleField = sampleField.replace(String.valueOf(k + 1), "1");
                            }
                        }
                    } else {
                        break; // Do not waste time processing the rest of fields
                    }
                } else if (formatField.equalsIgnoreCase("GL") ||
                        formatField.equalsIgnoreCase("PL") ||
                        formatField.equalsIgnoreCase("GP")) {
                    // All-alleles present and not haploid
                    if (!sampleField.equals(".") && genotype != null &&
                            (genotype.getCode() == AllelesCode.ALLELES_OK ||
                                    genotype.getCode() == AllelesCode.MULTIPLE_ALTERNATES)) {
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

                map.put(formatField.toUpperCase(), sampleField);
            }

            // If the genotype of the sample did not match the alleles of this variant, do not add it to the list
            if (shouldAddSample) {
                variant.getFile(fileId).addSampleData(sampleNames.get(i - 9), map);
            }
        }
    }

    /**
     * Checks whether a sample should be included in a variant's list of samples.
     * If current allele index is not found in the genotype and not all alleles
     * are references/missing, then the sample must not be included.
     *
     * @param genotype  The genotype
     * @param alleleIdx The index of the allele
     * @return If the sample should be associated to the variant
     */
    private boolean shouldAddSampleToVariant(String genotype, int alleleIdx) {
        if (!genotype.contains(String.valueOf(alleleIdx))) {
            if (!genotype.contains("0")) {
                return false;
            } else {
                String[] alleles = genotype.split("[/|]");
                for (String allele : alleles) {
                    if (!allele.equals("0") && !allele.equals(".")) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    protected void parseInfo(Variant variant, String fileId, String info) {
        for (String var : info.split(";")) {
            String[] splits = var.split("=");
            if (splits.length == 2) {
                variant.getFile(fileId).addAttribute(splits[0], splits[1]);
            } else {
                variant.getFile(fileId).addAttribute(splits[0], "");
            }
        }
    }

    protected VariantKeyFields createVariantsFromSameLengthRefAlt(int position, String reference, String alt) {
        int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return null;
        } else if (indexOfDifference == 0) {
            return new VariantKeyFields(position, position + alt.length(), reference, alt);
        } else {
            int start = position + indexOfDifference;
            int end = position + Math.max(reference.length(), alt.length()) - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            return new VariantKeyFields(start, end, ref, inAlt);
        }
    }

    protected VariantKeyFields createVariantsFromInsertionEmptyRef(int position, String alt) {
        return new VariantKeyFields(position - 1, position + alt.length(), "", alt);
    }

    protected VariantKeyFields createVariantsFromDeletionEmptyAlt(int position, String reference) {
        return new VariantKeyFields(position, position + reference.length() - 1, reference, "");
    }

    protected VariantKeyFields createVariantsFromIndelNoEmptyRefAlt(int position, String reference, String alt) {
        int indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            return null;
        } else if (indexOfDifference == 0) {
            if (reference.length() > alt.length()) {
                return new VariantKeyFields(position, position + reference.length() - 1, reference, alt);
            } else {
                return new VariantKeyFields(position - 1, position + alt.length(), reference, alt);
            }
        } else {
            int start = position + indexOfDifference;
            int end = position + Math.max(reference.length(), alt.length()) - 1;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            return new VariantKeyFields(start, end, ref, inAlt);
        }
    }

    protected void setOtherFields(Variant variant, VariantSource source, String id, float quality, String filter, String info, String format, int numAllele, String[] alternateAlleles) {
        // Fields not affected by the structure of REF and ALT fields
        variant.setId(id);
        if (quality > -1) {
            variant.getFile(source.getFileId()).addAttribute("QUAL", String.valueOf(quality));
        }
        if (!filter.isEmpty()) {
            variant.getFile(source.getFileId()).addAttribute("FILTER", filter);
        }
        if (!info.isEmpty()) {
            parseInfo(variant, source.getFileId(), info);
        }
        variant.getFile(source.getFileId()).setFormat(format);
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
}
