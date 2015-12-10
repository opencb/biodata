package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.commons.run.ParallelTaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 06/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantNormalizer implements ParallelTaskRunner.Task<Variant, Variant> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    private boolean reuseVariants = true;

    public VariantNormalizer() {}

    public VariantNormalizer(boolean reuseVariants) {
        this.reuseVariants = reuseVariants;
    }

    @Override
    public List<Variant> apply(List<Variant> batch) {
        try {
            return normalize(batch, reuseVariants);
        } catch (NonStandardCompliantSampleField e) {
            throw new RuntimeException(e);
        }
    }

    public List<Variant> normalize(List<Variant> batch, boolean reuse) throws NonStandardCompliantSampleField {
        List<Variant> normalizedVariants = new ArrayList<>(batch.size());

        for (Variant variant : batch) {
            if (!isNormalizable(variant)) {
                normalizedVariants.add(variant);
                continue;
            }

            String reference = variant.getReference();  //Save original values, as they can be changed
            String alternate = variant.getAlternate();
            Integer start = variant.getStart();

            if (variant.getStudies() == null || variant.getStudies().isEmpty()) {
                VariantKeyFields keyFields = normalize(start, reference, alternate);
                Variant normalizedVariant = newVariant(variant, keyFields);
                normalizedVariants.add(normalizedVariant);
            } else {
                for (StudyEntry entry : variant.getStudies()) {
                    List<String> alternates = new ArrayList<>(1 + entry.getSecondaryAlternates().size());
                    alternates.add(alternate);
                    alternates.addAll(entry.getSecondaryAlternates());

                    List<VariantKeyFields> keyFieldsList = normalize(start, reference, alternates);
                    for (VariantKeyFields keyFields : keyFieldsList) {
                        String call = start + ":" + reference + ":" + alternates.stream().collect(Collectors.joining(",")) + ":" + keyFields.getNumAllele();

                        final Variant normalizedVariant;
                        final StudyEntry normalizedEntry;
                        final List<List<String>> samplesData;
                        if (reuse && keyFieldsList.size() == 1) {   //Only reuse for non multiallelic variants
                            //Reuse variant. Set new fields.
                            normalizedVariant = variant;
                            variant.setStart(keyFields.getStart());
                            variant.setEnd(keyFields.getEnd());
                            variant.setReference(keyFields.getReference());
                            variant.setAlternate(keyFields.getAlternate());
                            normalizedEntry = entry;
                            entry.getFiles().forEach(fileEntry -> fileEntry.setCall(call)); //TODO: Check file attributes
                            samplesData = entry.getSamplesData();
                        } else {
                            normalizedVariant = newVariant(variant, keyFields);

                            normalizedEntry = new StudyEntry();
                            normalizedEntry.setStudyId(entry.getStudyId());
                            normalizedEntry.setSamplesPosition(entry.getSamplesPosition());
                            normalizedEntry.setFormat(entry.getFormat());

                            List<FileEntry> files = new ArrayList<>(entry.getFiles().size());
                            for (FileEntry file : entry.getFiles()) {
                                files.add(new FileEntry(file.getFileId(), call, file.getAttributes())); //TODO: Check file attributes
                            }
                            normalizedEntry.setFiles(files);
                            normalizedVariant.addStudyEntry(normalizedEntry);
                            samplesData = newSamplesData(entry.getSamplesData().size(), entry.getFormat().size());
                        }

                        //Set normalized secondary alternates
                        normalizedEntry.setSecondaryAlternates(getSecondaryAlternates(keyFields.getAlternate(), alternates));
                        //Set normalized samples data
                        try {
                            List<List<String>> normalizedSamplesData = normalizeSamplesData(keyFields,
                                    entry.getSamplesData(), entry.getFormat(), reference, alternates, samplesData);
                            normalizedEntry.setSamplesData(normalizedSamplesData);
                            normalizedVariants.add(normalizedVariant);
                        } catch (Exception e) {
                            logger.warn("Error parsing variant " + call + ", numAllele " + keyFields.getNumAllele(), e);
                            throw e;
                        }
                    }
                }
            }
        }

        return normalizedVariants;
    }

    public VariantKeyFields normalize(int position, String reference, String alternate) {
        return normalize(position, reference, Collections.singletonList(alternate)).get(0);
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
     * Non normalizable variants
     * TODO: Add {@link VariantType#SYMBOLIC} variants?
     */
    private boolean isNormalizable(Variant variant) {
        return !variant.getType().equals(VariantType.NO_VARIATION);
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
                                                   String reference, List<String> alternateAlleles) throws NonStandardCompliantSampleField {
        return normalizeSamplesData(variantKeyFields, samplesData, format, reference, alternateAlleles, null);
    }

    public List<List<String>> normalizeSamplesData(VariantKeyFields variantKeyFields, final List<List<String>> samplesData, List<String> format,
                                                   String reference, List<String> alternateAlleles, List<List<String>> reuseSampleData)
            throws NonStandardCompliantSampleField {

        List<List<String>> newSampleData;
        if (reuseSampleData == null) {
            newSampleData = newSamplesData(samplesData.size(), format.size());
        } else {
            newSampleData = reuseSampleData;
        }

        Genotype genotype = null;
        String[] secondaryAlternatesMap = new String[1 + alternateAlleles.size()];  //reference + alternates
        int secondaryReferencesIdx = 2;
        int alleleIdx = 1;
        secondaryAlternatesMap[0] = "0";     // Set the reference id
        for (String alternateAllele : alternateAlleles) {
            if (variantKeyFields.getNumAllele() == alleleIdx - 1) {
                secondaryAlternatesMap[alleleIdx] = "1";    //The first alternate
            } else {    //Secondary alternates will start at position 2, and increase sequentially
                secondaryAlternatesMap[alleleIdx] = Integer.toString(secondaryReferencesIdx);
                secondaryReferencesIdx++;
            }
            alleleIdx++;
        }


        for (int sampleIdx = 0; sampleIdx < samplesData.size(); sampleIdx++) {
            List<String> sampleData = samplesData.get(sampleIdx);

            // TODO we could check that format and sampleData sizes are equals
//            if (sampleData.size() == 1 && sampleData.get(0).equals(".")) {
//                newSampleData.get(sampleIdx).set(0, "./.");
//                System.out.println("Format data equals '.'");
//                continue;
//            }

            for (int formatFieldIdx = 0; formatFieldIdx < format.size(); formatFieldIdx++) {
                String formatField = format.get(formatFieldIdx);
                String sampleField = sampleData.get(formatFieldIdx);

                if (formatField.equalsIgnoreCase("GT")) {
                    // Save alleles just in case they are necessary for GL/PL/GP transformation
                    // Use the original alternates to create the genotype.
                    genotype = new Genotype(sampleField, reference, alternateAlleles);

                    StringBuilder genotypeStr = new StringBuilder();

                    int[] allelesIdx = genotype.getAllelesIdx();
                    for (int i = 0; i < allelesIdx.length; i++) {
                        int allele = allelesIdx[i];
                        if (allele < 0) { // Missing
                            genotypeStr.append(".");
                        } else {
                            // Replace numerical indexes when they refer to another alternate allele
                            genotypeStr.append(secondaryAlternatesMap[allele]);
                        }
                        if (i < allelesIdx.length - 1) {
                            genotypeStr.append(genotype.isPhased() ? "|" : "/");
                        }
                    }
                    sampleField = genotypeStr.toString();

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
                            sampleField = String.join(",", alleleLikelihoods);
                        }
                    }
                }
                newSampleData.get(sampleIdx).set(formatFieldIdx, sampleField);
            }
        }
        return newSampleData;
    }


    private Variant newVariant(Variant variant, VariantKeyFields keyFields) {
        Variant normalizedVariant = new Variant(variant.getChromosome(), keyFields.getStart(), keyFields.getEnd(), keyFields.getReference(), keyFields.getAlternate());
        normalizedVariant.setIds(variant.getIds());
        normalizedVariant.setStrand(variant.getStrand());
        normalizedVariant.setAnnotation(variant.getAnnotation());
        return normalizedVariant;
    }

    private List<String> getSecondaryAlternates(String alternate, List<String> alternates) {
        List<String> secondaryAlternates;
        if (alternates.size() == 1) {
            secondaryAlternates = Collections.emptyList();
        } else {
            secondaryAlternates = new ArrayList<>(alternates.size() - 1);
            for (String secondaryAlternate : alternates) {
                if (!secondaryAlternate.equals(alternate)) {
                    secondaryAlternates.add(secondaryAlternate);
                }
            }
        }
        return secondaryAlternates;
    }

    private List<List<String>> newSamplesData(int samplesSize, int formatSize) {
        List<List<String>> newSampleData;
        newSampleData = new ArrayList<>(samplesSize);
        for (int i = 0; i < samplesSize; i++) {
            newSampleData.add(Arrays.asList(new String[formatSize]));
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VariantKeyFields)) return false;

            VariantKeyFields that = (VariantKeyFields) o;

            if (start != that.start) return false;
            if (end != that.end) return false;
            if (numAllele != that.numAllele) return false;
            if (reference != null ? !reference.equals(that.reference) : that.reference != null) return false;
            return !(alternate != null ? !alternate.equals(that.alternate) : that.alternate != null);

        }

        @Override
        public int hashCode() {
            int result = start;
            result = 31 * result + end;
            result = 31 * result + numAllele;
            result = 31 * result + (reference != null ? reference.hashCode() : 0);
            result = 31 * result + (alternate != null ? alternate.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "VariantKeyFields{" +
                    "start=" + start +
                    ", end=" + end +
                    ", numAllele=" + numAllele +
                    ", reference='" + reference + '\'' +
                    ", alternate='" + alternate + '\'' +
                    '}';
        }


    }

}
