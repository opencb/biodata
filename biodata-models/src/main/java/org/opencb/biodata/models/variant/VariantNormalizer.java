package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.commons.run.ParallelTaskRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 06/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantNormalizer implements ParallelTaskRunner.Task<Variant, Variant> {


    @Override
    public List<Variant> apply(List<Variant> batch) {
        try {
            return normalize(batch);
        } catch (NonStandardCompliantSampleField e) {
            throw new RuntimeException(e);
        }
    }

    public List<Variant> normalize(List<Variant> batch) throws NonStandardCompliantSampleField {
        List<Variant> normalizedVariants = new ArrayList<>(batch.size());

        for (Variant variant : batch) {
            for (VariantSourceEntry entry : variant.getStudies()) {
                List<String> alternates = new ArrayList<>(1 + entry.getSecondaryAlternates().size());
                alternates.add(variant.getAlternate());
                alternates.addAll(entry.getSecondaryAlternates());

                List<VariantKeyFields> keyFieldsList = normalize(variant.getStart(), variant.getReference(), alternates);
                for (VariantKeyFields keyFields : keyFieldsList) {
                    Variant normalizedVariant = new Variant(variant.getChromosome(), keyFields.getStart(), keyFields.getEnd(), keyFields.getReference(), keyFields.getAlternate());
                    normalizedVariants.add(normalizedVariant);

                    VariantSourceEntry normalizedEntry = new VariantSourceEntry();
                    normalizedEntry.setStudyId(entry.getStudyId());
                    normalizedEntry.setSamplesPosition(entry.getSamplesPosition());
//                    normalizedEntry.setSecondaryAlternates(); //TODO: Set secondary alternates
                    normalizedEntry.setFormat(entry.getFormat());

                    List<FileEntry> files = new ArrayList<>(entry.getFiles().size());
                    String call = variant.getStart() + ":" + variant.getReference() + ":" + alternates.stream().collect(Collectors.joining(",")) + ":" + keyFields.getNumAllele();
                    for (FileEntry file : entry.getFiles()) {
                        files.add(new FileEntry(file.getFileId(), call, file.getAttributes())); //TODO: Check attributes
                    }
                    normalizedEntry.setFiles(files);

                    normalizedEntry.setSamplesData(normalizeSamplesData(keyFields, entry.getSamplesData(), entry.getFormat(), alternates));

                    normalizedVariant.addSourceEntry(normalizedEntry);
                    normalizedVariants.add(normalizedVariant);
                }
            }
        }

        return normalizedVariants;
    }

    public List<VariantKeyFields> normalize(int position, String reference, List<String> alternates) {
        List<VariantKeyFields> list = new ArrayList<>(alternates.size());
        int numAllelesIdx = 0; // This index is necessary for getting the samples where the mutated allele is present
        for (Iterator<String> iterator = alternates.iterator(); iterator.hasNext(); numAllelesIdx++) {
            String currentAlternate = iterator.next();
            VariantKeyFields keyFields;
            int referenceLen = reference.length();
            int alternateLen = currentAlternate.length();

            if (referenceLen == alternateLen) {
                keyFields = createVariantsFromSameLengthRefAlt(position, reference, currentAlternate);
            } else if (referenceLen == 0) {
                keyFields = createVariantsFromInsertionEmptyRef(position, currentAlternate);
            } else if (alternateLen == 0) {
                keyFields = createVariantsFromDeletionEmptyAlt(position, reference);
            } else {
                keyFields = createVariantsFromIndelNoEmptyRefAlt(position, reference, currentAlternate);
            }

            keyFields.numAllele = numAllelesIdx;
            list.add(keyFields);
        }
        return list;
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

    public List<List<String>> normalizeSamplesData(VariantKeyFields variantKeyFields, final List<List<String>> samplesData, List<String> format,
                                                   List<String> alternateAlleles) throws NonStandardCompliantSampleField {
        return normalizeSamplesData(variantKeyFields, samplesData, format, alternateAlleles, null);
    }

    public List<List<String>> normalizeSamplesData(VariantKeyFields variantKeyFields, final List<List<String>> samplesData, List<String> format,
                                                   List<String> alternateAlleles, List<List<String>> reuseSampleData)
            throws NonStandardCompliantSampleField {

        List<List<String>> newSampleData;
        if (reuseSampleData == null) {
            newSampleData = new ArrayList<>(samplesData.size());
            for (int i = 0; i < samplesData.size(); i++) {
                newSampleData.add(Arrays.asList(new String[format.size()]));
            }
        } else {
            newSampleData = reuseSampleData;
        }

        Genotype genotype = null;
        String[] secondaryAlternatesMap = new String[1 + alternateAlleles.size()];  //reference + alternates
        int secondaryReferencesIdx = 2;
        int alleleIdx = 1;
        secondaryAlternatesMap[0] = "0";     // Set the reference id
        for (String alternateAllele : alternateAlleles) {
            if (alternateAllele.equals(variantKeyFields.alternate)) {
                secondaryAlternatesMap[alleleIdx] = "1";    //The first alternate
            } else {    //Secondary alternates will start at position 2, and increase sequentially
                secondaryAlternatesMap[alleleIdx] = Integer.toString(secondaryReferencesIdx);
                secondaryReferencesIdx++;
            }
            alleleIdx++;
        }


        for (int sampleIdx = 0, samplesDataSize = samplesData.size(); sampleIdx < samplesDataSize; sampleIdx++) {
            List<String> sampleData = samplesData.get(sampleIdx);

            for (int formatFieldIdx = 0, formatSize = format.size(); formatFieldIdx < formatSize; formatFieldIdx++) {
                String formatField = format.get(formatFieldIdx);
                String sampleField = sampleData.get(formatFieldIdx);

                if (formatField.equalsIgnoreCase("GT")) {
                    // Save alleles just in case they are necessary for GL/PL/GP transformation
                    genotype = new Genotype(sampleField, variantKeyFields.getReference(), variantKeyFields.getAlternate());

                    StringBuilder genotypeStr = new StringBuilder();
                    for (int allele : genotype.getAllelesIdx()) {
                        if (allele < 0) { // Missing
                            genotypeStr.append(".");
                        } else {
                            // Replace numerical indexes when they refer to another alternate allele
                            genotypeStr.append(secondaryAlternatesMap[allele]);
                        }
                        genotypeStr.append(genotype.isPhased() ? "|" : "/");
                    }
                    sampleField = genotypeStr.substring(0, genotypeStr.length() - 1);

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
                newSampleData.get(sampleIdx).set(formatFieldIdx, sampleField);
            }
        }
        return newSampleData;
    }


    public static class VariantKeyFields {

        private int start;
        private int end;
        private int numAllele;
        private String reference;
        private String alternate;

        public VariantKeyFields(int start, int end, String reference, String alternate) {
            this.start = start;
            this.end = end;
            this.reference = reference;
            this.alternate = alternate;
        }

        public int getStart() {
            return start;
        }

        public VariantKeyFields setStart(int start) {
            this.start = start;
            return this;
        }

        public int getEnd() {
            return end;
        }

        public VariantKeyFields setEnd(int end) {
            this.end = end;
            return this;
        }

        public int getNumAllele() {
            return numAllele;
        }

        public VariantKeyFields setNumAllele(int numAllele) {
            this.numAllele = numAllele;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public VariantKeyFields setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public String getAlternate() {
            return alternate;
        }

        public VariantKeyFields setAlternate(String alternate) {
            this.alternate = alternate;
            return this;
        }

    }

}
