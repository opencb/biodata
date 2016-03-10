package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.template.SequencePair;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
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
    private boolean normalizeAlleles = false;
    private boolean decomposeMNVs = false;
    private boolean generateReferenceBlocks = false;

    public VariantNormalizer() {}

    public VariantNormalizer(boolean reuseVariants) {
        this.reuseVariants = reuseVariants;
    }

    public VariantNormalizer(boolean reuseVariants, boolean normalizeAlleles) {
        this.reuseVariants = reuseVariants;
        this.normalizeAlleles = normalizeAlleles;
    }

    public VariantNormalizer(boolean reuseVariants, boolean normalizeAlleles, boolean decomposeMNVs) {
        this.reuseVariants = reuseVariants;
        this.normalizeAlleles = normalizeAlleles;
        this.decomposeMNVs = decomposeMNVs;
    }

    public boolean isReuseVariants() {
        return reuseVariants;
    }

    public VariantNormalizer setReuseVariants(boolean reuseVariants) {
        this.reuseVariants = reuseVariants;
        return this;
    }

    public boolean isNormalizeAlleles() {
        return normalizeAlleles;
    }

    public boolean isDecomposeMNVs() {
        return decomposeMNVs;
    }

    public VariantNormalizer setDecomposeMNVs(boolean decomposeMNVs) {
        this.decomposeMNVs = decomposeMNVs;
        return this;
    }

    public VariantNormalizer setNormalizeAlleles(boolean normalizeAlleles) {
        this.normalizeAlleles = normalizeAlleles;
        return this;
    }

    public boolean isGenerateReferenceBlocks() {
        return generateReferenceBlocks;
    }

    public VariantNormalizer setGenerateReferenceBlocks(boolean generateReferenceBlocks) {
        this.generateReferenceBlocks = generateReferenceBlocks;
        return this;
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
            String chromosome = variant.getChromosome();

            if (variant.getStudies() == null || variant.getStudies().isEmpty()) {
                List<VariantKeyFields> keyFieldsList = normalize(chromosome, start, reference, alternate);
                for (VariantKeyFields keyFields : keyFieldsList) {
                    String call = start + ":" + reference + ":" + alternate + ":" + keyFields.getNumAllele();
                    Variant normalizedVariant = newVariant(variant, keyFields);
                    if (keyFields.getPhaseSet() != null) {
                        StudyEntry studyEntry = new StudyEntry();
                        studyEntry.setSamplesData(
                                Collections.singletonList(Collections.singletonList(keyFields.getPhaseSet())));
                        studyEntry.setFormat(Collections.singletonList("PS"));
                        // Use mnv string as file Id so that it can be later identified. It is also used
                        // as the genotype call since we don't have an actual call and to avoid confusion
                        studyEntry.setFiles(Collections.singletonList(new FileEntry(keyFields.getPhaseSet(), call, null)));
                        normalizedVariant.setStudies(Collections.singletonList(studyEntry));
                    }
                    normalizedVariants.add(normalizedVariant);
                }
            } else {
                for (StudyEntry entry : variant.getStudies()) {
                    List<String> alternates = new ArrayList<>(1 + entry.getSecondaryAlternates().size());
                    alternates.add(alternate);
                    alternates.addAll(entry.getSecondaryAlternatesAlleles());

                    List<VariantKeyFields> keyFieldsList = normalize(chromosome, start, reference, alternates);
                    boolean sameVariant = keyFieldsList.size() == 1
                            && keyFieldsList.get(0).getStart() == start
                            && keyFieldsList.get(0).getReference().equals(reference)
                            && keyFieldsList.get(0).getAlternate().equals(alternate);
                    for (VariantKeyFields keyFields : keyFieldsList) {
                        String call = start + ":" + reference + ":" + String.join(",", alternates) + ":" + keyFields.getNumAllele();

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
                            entry.getFiles().forEach(fileEntry -> fileEntry.setCall(sameVariant ? "" : call)); //TODO: Check file attributes
                            samplesData = entry.getSamplesData();
                        } else {
                            normalizedVariant = newVariant(variant, keyFields);

                            normalizedEntry = new StudyEntry();
                            normalizedEntry.setStudyId(entry.getStudyId());
                            normalizedEntry.setSamplesPosition(entry.getSamplesPosition());
                            normalizedEntry.setFormat(entry.getFormat());

                            List<FileEntry> files = new ArrayList<>(entry.getFiles().size());
                            for (FileEntry file : entry.getFiles()) {
                                HashMap<String, String> attributes = new HashMap<>(file.getAttributes()); //TODO: Check file attributes
                                files.add(new FileEntry(file.getFileId(), sameVariant ? "" : call, attributes));
                            }
                            normalizedEntry.setFiles(files);
                            normalizedVariant.addStudyEntry(normalizedEntry);
                            samplesData = newSamplesData(entry.getSamplesData().size(), entry.getFormat().size());
                        }

                        if (keyFields.isReferenceBlock()) {
                            normalizedVariant.setType(VariantType.NO_VARIATION);
                            normalizedEntry.getFiles().forEach(fileEntry -> fileEntry.getAttributes().put("END", Integer.toString(keyFields.getEnd())));
                        }

                        //Set normalized secondary alternates
                        normalizedEntry.setSecondaryAlternates(getSecondaryAlternatesMap(chromosome, keyFields, keyFieldsList));

                        //Set normalized samples data
                        try {
                            List<String> format = entry.getFormat();
                            if (keyFields.getPhaseSet() != null) {
                                if (!normalizedEntry.getFormatPositions().containsKey("PS")) {
                                    normalizedEntry.addFormat("PS");
                                    format = new ArrayList<>(normalizedEntry.getFormat());
                                }
                                // If no files are provided one must be created to ensure genotype calls are the same
                                // for all mnv-phased variants
                                if (normalizedEntry.getFiles().size() == 0) {
                                    // Use mnv string as file Id so that it can be later identified.
                                    normalizedEntry.setFiles(Collections.singletonList(new FileEntry(keyFields.getPhaseSet(), call, null)));
                                }
                            }
                            List<List<String>> normalizedSamplesData = normalizeSamplesData(keyFields,
                                    entry.getSamplesData(), format, reference, alternates, samplesData);
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

    public List<VariantKeyFields> normalize(String chromosome, int position, String reference, String alternate) {
        return normalize(chromosome, position, reference, Collections.singletonList(alternate));
    }

    public List<VariantKeyFields> normalize(String chromosome, int position, String reference, List<String> alternates) {
        List<VariantKeyFields> list = new ArrayList<>(alternates.size());
        int numAllelesIdx = 0; // This index is necessary for getting the samples where the mutated allele is present
        for (Iterator<String> iterator = alternates.iterator(); iterator.hasNext(); numAllelesIdx++) {
            String currentAlternate = iterator.next();
            List<VariantKeyFields> keyFieldsList;
            int referenceLen = reference.length();
            int alternateLen = currentAlternate.length();

            VariantKeyFields keyFields;
            if (referenceLen == 0) {
                keyFields = createVariantsFromInsertionEmptyRef(position, currentAlternate);
            } else if (alternateLen == 0) {
                keyFields = createVariantsFromDeletionEmptyAlt(position, reference);
            } else {
                keyFields = createVariantsFromNoEmptyRefAlt(position, reference, currentAlternate);
            }


            if (keyFields != null) {
                if (decomposeMNVs
                        && ((keyFields.getReference().length() > 1 && keyFields.getAlternate().length() > 1)
                        || ((
                        (keyFields.getReference().length() > 1 && keyFields.getAlternate().length() == 1) // To deal with cases such as A>GT
                                || (keyFields.getAlternate().length() > 1 && keyFields.getReference().length() == 1)
                        // After left and right trimming, first character of reference and alternate must be different.
                ) /*&& keyFieldsList.getReference().charAt(0) != keyFieldsList.getAlternate().charAt(0)*/))) {
                    for (VariantKeyFields keyFields1 : decomposeMNVSingleVariants(keyFields)) {
                        keyFields1.numAllele = numAllelesIdx;
                        keyFields1.phaseSet = chromosome + ":" + position + ":" + reference + ":" + currentAlternate;
                        list.add(keyFields1);
                    }
                } else {
                    keyFields.numAllele = numAllelesIdx;
                    list.add(keyFields);
                }
            }
        }

        if (generateReferenceBlocks) {
            list = generateReferenceBlocks(list, position, reference);
        }

        list.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));
        return list;
    }

    private List<VariantKeyFields> generateReferenceBlocks(List<VariantKeyFields> list, int position, String reference) {

        // Skip for simple SNV
        if (list.size() == 1 && list.get(0).getStart() == position && list.get(0).getReference().equals(reference)) {
            return list;
        }

        // Create a reference block for all the positions.
        list.add(new VariantKeyFields(position, position + reference.length() - 1, 0, reference.substring(0, 1), "", true));

        // Have to remove overlapped reference blocks.
        for (int i = 0; i < list.size(); i++) {
            //Don't use iterators to avoid concurrent modification exceptions
            VariantKeyFields current = list.get(i);
            if (current.isReferenceBlock()) {
                VariantKeyFields newSlice = null;
                for (VariantKeyFields aux : list) {
                    if (aux == current) {
                        // Skip self
                        continue;
                    } else {
                        if (aux.getStart() <= current.getStart() && aux.getReferenceEnd() >= current.getEnd()) {
                            /* 1)
                             *     |----|   <- current
                             *  |--------|  <- aux
                             */
                            list.remove(i--);
                            break;
                        } else if (aux.getStart() <= current.getStart() && current.getStart() <= aux.getReferenceEnd()) {
                            /* 2)
                             *     |----|   <- current
                             *  |-----|     <- aux
                             */
                            if (aux.isReferenceBlock()) {
                                //Merge reference blocks
                                aux.setEnd(current.getEnd());
                                list.remove(i--);
                                break;
                            } else {
                                current.setStart(aux.getReferenceEnd() + 1);
                                // Have to find the correct current reference from the original reference
                                current.setReference(reference.substring(current.getStart() - position, current.getStart() - position + 1));
                            }
                        } else if (aux.getReferenceEnd() >= current.getEnd() && aux.getStart() <= current.getEnd()) {
                            /* 3)
                             *   |-----|    <- current
                             *     |-----|  <- aux
                             */
                            if (aux.isReferenceBlock()) {
                                //Merge reference blocks
                                aux.setStart(current.getStart());
                                aux.setReference(current.getReference());
                                list.remove(i--);
                                break;
                            } else {
                                current.setEnd(aux.getStart() - 1);
                            }
                        } else if (aux.getStart() <= current.getEnd() && aux.getReferenceEnd() > current.getStart()) {
                            /* 4) As first case, but upside down
                             *  |-------|  <- current
                             *    |--|     <- aux
                             */
                            if (!aux.isReferenceBlock()) {
                                /* Split the current block into 2 blocks
                                 *  |-|  |--|  <- current + newSlip
                                 *    |--|     <- aux
                                 */
                                int blockStart = aux.getReferenceEnd() + 1;
                                int blockEnd = current.getEnd();
                                if (blockEnd >= blockStart) {
                                    // Have to find the correct current reference from the original reference
                                    String blockRef = reference.substring(blockStart - position, blockStart - position + 1);
                                    if (newSlice != null) {
                                        throw new IllegalStateException();
                                    }
                                    newSlice = new VariantKeyFields(blockStart,  blockEnd, 0, blockRef, "", true);
                                }

                                current.setEnd(aux.getStart() - 1);
                                if (current.getEnd() < current.getStart()) {
                                    list.remove(i--);
                                    break;
                                }
                            } // else, will be fixed later

                        }   /* else, nothing to do
                             * 5)
                             *  |--|       <- current
                             *       |--|  <- aux
                             */
                    }
                }
                if (newSlice != null) {
                    list.add(newSlice);
                }
            }
        }
        return list;
    }

    private List<VariantKeyFields> decomposeMNVSingleVariants(VariantKeyFields keyFields) {
        SequencePair<DNASequence, NucleotideCompound> sequenceAlignment = getPairwiseAlignment(keyFields.getReference(),
                keyFields.getAlternate());
        return decomposeAlignmentSingleVariants(sequenceAlignment, keyFields.getStart());
    }

    private List<VariantKeyFields> decomposeAlignmentSingleVariants(SequencePair<DNASequence, NucleotideCompound> sequenceAlignment,
                                                                    int genomicStart) {

        String reference = sequenceAlignment.getTarget().getSequenceAsString();
        String alternate = sequenceAlignment.getQuery().getSequenceAsString();
        List<VariantKeyFields> keyFieldsList = new ArrayList<>();
        VariantKeyFields keyFields = null;
        char previousReferenceChar = 0;
        char previousAlternateChar = 0;
        // Assume that as a result of the alignment "reference" and "alternate" Strings are of the same length
        for (int i = 0; i < reference.length(); i++) {
            char referenceChar = reference.charAt(i);
            char alternateChar = alternate.charAt(i);
            // Insertion
            if (referenceChar == '-') {
                // Assume there cannot be a '-' at the reference and alternate aligned sequences at the same position
                if (alternateChar == '-') {
                    logger.error("Unhandled case found after pairwise alignment of MNVs. Alignment result: "
                            + reference + "/" + alternate);
                }
                // Current character is a continuation of an insertion
                if (previousReferenceChar == '-') {
                    keyFields.setAlternate(keyFields.getAlternate() + alternateChar);
                // New insertion found, create new keyFields
                } else {
                    keyFields = new VariantKeyFields(genomicStart + i, genomicStart + i, "",
                            String.valueOf(alternateChar));
                    keyFieldsList.add(keyFields);
                }
            // Deletion
            } else if (alternateChar == '-') {
                // Current character is a continuation of a deletion
                if (previousAlternateChar == '-') {
                    keyFields.setReference(keyFields.getReference() + referenceChar);
                    keyFields.setEnd(keyFields.getEnd()+1);
                // New deletion found, create new keyFields
                } else {
                    keyFields = new VariantKeyFields(genomicStart + i, genomicStart + i, String.valueOf(referenceChar),
                            "");
                    keyFieldsList.add(keyFields);
                }
            // SNV
            } else if (referenceChar != alternateChar) {
                keyFields = new VariantKeyFields(genomicStart + i, genomicStart + i, String.valueOf(referenceChar),
                        String.valueOf(alternateChar));
                keyFieldsList.add(keyFields);
            }
            previousReferenceChar = referenceChar;
            previousAlternateChar = alternateChar;
        }

        return keyFieldsList;
    }

    private SequencePair<DNASequence, NucleotideCompound> getPairwiseAlignment(String seq1, String seq2) {
        DNASequence target = null;
        DNASequence query = null;
        try {
            target = new DNASequence(seq1, AmbiguityDNACompoundSet.getDNACompoundSet());
            query = new DNASequence(seq2, AmbiguityDNACompoundSet.getDNACompoundSet());
        } catch (Exception e) {
            logger.error("Error when creating DNASequence objects for " + seq1 + " and " + seq2 + " prior to pairwise " +
                    "sequence alignment", e);
        }
        SubstitutionMatrix<NucleotideCompound> substitutionMatrix = SubstitutionMatrixHelper.getNuc4_4();
        SimpleGapPenalty gapP = new SimpleGapPenalty();
        gapP.setOpenPenalty((short)5);
        gapP.setExtensionPenalty((short)2);
        SequencePair<DNASequence, NucleotideCompound> psa = Alignments.getPairwiseAlignment(query, target,
                Alignments.PairwiseSequenceAlignerType.GLOBAL, gapP, substitutionMatrix);

        return psa;
    }

    /**
     * Non normalizable variants
     * TODO: Add {@link VariantType#SYMBOLIC} variants?
     */
    private boolean isNormalizable(Variant variant) {
        return !variant.getType().equals(VariantType.NO_VARIATION);
    }

    protected VariantKeyFields createVariantsFromInsertionEmptyRef(int position, String alt) {
        return new VariantKeyFields(position, position + alt.length() - 1, "", alt);
    }

    protected VariantKeyFields createVariantsFromDeletionEmptyAlt(int position, String reference) {
        return new VariantKeyFields(position, position + reference.length() - 1, reference, "");
    }

    /**
     * Calculates the start, end, reference and alternate of an SNV/MNV/INDEL where the
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
    protected VariantKeyFields createVariantsFromNoEmptyRefAlt(int position, String reference, String alt) {
        int indexOfDifference;
        // Remove the trailing bases
        indexOfDifference = reverseIndexOfDifference(reference, alt);

//        VariantKeyFields startReferenceBlock = null;
        final VariantKeyFields keyFields;
//        VariantKeyFields endReferenceBlock = null;

//        if (generateReferenceBlocks) {
//            if (indexOfDifference > 0) {
//                //Generate a reference block from the trailing bases
//                String blockRef = reference.substring(reference.length() - indexOfDifference, reference.length() - indexOfDifference + 1);
//                int blockStart = position + reference.length() - indexOfDifference;
//                int blockEnd = position + reference.length() - 1;   // Base-1 ending
//                endReferenceBlock = new VariantKeyFields(blockStart, blockEnd, blockRef, "")
//                        .setReferenceBlock(true);
//            } else if (indexOfDifference < 0) {
//                //Reference and alternate are equals! Generate a single reference block
//                String blockRef = reference.substring(0, 1);
//                int blockStart = position;
//                int blockEnd = position + reference.length() - 1;   // Base-1 ending
//                VariantKeyFields referenceBlock = new VariantKeyFields(blockStart, blockEnd, blockRef, "")
//                        .setReferenceBlock(true);
//                return Collections.singletonList(referenceBlock);
//            }
//        }


        reference = reference.substring(0, reference.length() - indexOfDifference);
        alt = alt.substring(0, alt.length() - indexOfDifference);

        // Remove the leading bases
        indexOfDifference = StringUtils.indexOfDifference(reference, alt);
        if (indexOfDifference < 0) {
            //There reference and the alternate are the same
            return null;
        } else if (indexOfDifference == 0) {
            if (reference.length() > alt.length()) { // Deletion
                keyFields = new VariantKeyFields(position, position + reference.length() - 1, reference, alt);
            } else { // Insertion
                keyFields = new VariantKeyFields(position, position + alt.length() - 1, reference, alt);
            }
        } else {
//            if (generateReferenceBlocks) {
//                String blockRef = reference.substring(0, 1);
//                int blockStart = position;
//                int blockEnd = position + indexOfDifference - 1;   // Base-1 ending
//                startReferenceBlock = new VariantKeyFields(blockStart, blockEnd, blockRef, "")
//                        .setReferenceBlock(true);
//            }

            int start = position + indexOfDifference;
            String ref = reference.substring(indexOfDifference);
            String inAlt = alt.substring(indexOfDifference);
            int end = reference.length() > alt.length()
                    ? position + reference.length() - 1
                    : position + alt.length() - 1;
            keyFields = new VariantKeyFields(start, end, ref, inAlt);
        }

//        if (!generateReferenceBlocks) {
//            return Collections.singletonList(keyFields);
//        } else {
//            List<VariantKeyFields> list = new ArrayList<>(1 + (startReferenceBlock == null ? 0 : 1) + (endReferenceBlock == null ? 0 : 1));
//            if (startReferenceBlock != null) {
//                list.add(startReferenceBlock);
//            }
//            list.add(keyFields);
//            if (endReferenceBlock != null) {
//                list.add(endReferenceBlock);
//            }
//            return list;
//        }
        return keyFields;
    }

    /**
     * <p>Compares two CharSequences, and returns the index beginning from the behind,
     * at which the CharSequences begin to differ.</p>
     *
     * Based on {@link StringUtils#indexOfDifference}
     *
     * <p>For example,
     * {@code reverseIndexOfDifference("you are a machine", "i have one machine") -> 8}</p>
     *
     * <pre>
     * reverseIndexOfDifference(null, null) = -1
     * reverseIndexOfDifference("", "") = -1
     * reverseIndexOfDifference("", "abc") = 0
     * reverseIndexOfDifference("abc", "") = 0
     * reverseIndexOfDifference("abc", "abc") = -1
     * reverseIndexOfDifference("ab", "xyzab") = 2
     * reverseIndexOfDifference("abcde", "xyzab") = 2
     * reverseIndexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param cs1  the first CharSequence, may be null
     * @param cs2  the second CharSequence, may be null
     * @return the index from behind where cs1 and cs2 begin to differ; -1 if they are equal
     */
    public static int reverseIndexOfDifference(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        int cs1Length = cs1.length();
        int cs2Length = cs2.length();

        for (i = 0; i < cs1Length && i < cs2Length; ++i) {
            if (cs1.charAt(cs1Length - i - 1) != cs2.charAt(cs2Length - i - 1)) {
                break;
            }
        }
        if (i < cs2Length || i < cs1Length) {
            return i;
        }
        return StringUtils.INDEX_NOT_FOUND;
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

        // Normalizing an mnv and no sample data was provided in the original variant - need to create sample data to
        // indicate the phase set
        if (variantKeyFields.getPhaseSet() != null && samplesData.size() == 0) {
            if (format.equals(Collections.singletonList("PS"))) {
                newSampleData.add(Collections.singletonList(variantKeyFields.getPhaseSet()));
            } else {
                List<String> sampleData = new ArrayList<>(format.size());
                for (String f : format) {
                    if (f.equals("PS")) {
                        sampleData.add(variantKeyFields.getPhaseSet());
                    } else {
                        sampleData.add("");
                    }
                }
                newSampleData.add(sampleData);
            }
        } else {
            for (int sampleIdx = 0; sampleIdx < samplesData.size(); sampleIdx++) {
                List<String> sampleData = samplesData.get(sampleIdx);

                // TODO we could check that format and sampleData sizes are equals
                //            if (sampleData.size() == 1 && sampleData.get(0).equals(".")) {
                //                newSampleData.get(sampleIdx).set(0, "./.");
                //                System.out.println("Format data equals '.'");
                //                continue;
                //            }

                Genotype genotype = null;
                for (int formatFieldIdx = 0; formatFieldIdx < format.size(); formatFieldIdx++) {
                    String formatField = format.get(formatFieldIdx);
                    // It may happen that the Format contains other fields that were not in the original format,
                    // or that some sampleData array is smaller than the format list.
                    // If the variant was a splitted MNV, a new field 'PS' is added to the format (if missing), so it may
                    // not be in the original sampleData.
                    String sampleField = sampleData.size() > formatFieldIdx ? sampleData.get(formatFieldIdx) : "";

                    if (formatField.equalsIgnoreCase("GT")) {
                        // Save alleles just in case they are necessary for GL/PL/GP transformation
                        // Use the original alternates to create the genotype.
                        genotype = new Genotype(sampleField, reference, alternateAlleles);

                        StringBuilder genotypeStr = new StringBuilder();

                        int[] allelesIdx;
                        if (normalizeAlleles && !genotype.isPhased()) {
                            allelesIdx = genotype.getNormalizedAllelesIdx();
                        } else {
                            allelesIdx = genotype.getAllelesIdx();
                        }
                        for (int i = 0; i < allelesIdx.length; i++) {
                            int allele = allelesIdx[i];
                            if (allele < 0) { // Missing
                                genotypeStr.append(".");
                            } else {
                                if (variantKeyFields.isReferenceBlock()) {
                                    genotypeStr.append(0);
                                } else {
                                    // Replace numerical indexes when they refer to another alternate allele
                                    genotypeStr.append(secondaryAlternatesMap[allele]);
                                }
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
                    } else if (formatField.equals("PS")) {
                        if (variantKeyFields.getPhaseSet() != null) {
                            sampleField = variantKeyFields.getPhaseSet();
                        }
                    }
                    List<String> data = newSampleData.get(sampleIdx);
                    if (data.size() > formatFieldIdx) {
                        data.set(formatFieldIdx, sampleField);
                    } else {
                        try {
                            data.add(sampleField);
                        } catch (UnsupportedOperationException e ) {
                            data = new ArrayList<>(data);
                            data.add(sampleField);
                            newSampleData.set(sampleIdx, data);
                        }
                    }
                }
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

    public List<AlternateCoordinate> getSecondaryAlternatesMap(String chromosome, VariantKeyFields alternate, List<VariantKeyFields> alternates) {
        List<AlternateCoordinate> secondaryAlternates;
        if (alternates.size() == 1 || alternate.isReferenceBlock()) {
            // If there is only one alternate, there are no secondary alternates
            // Reference blocks do not have secondary alternates
            secondaryAlternates = Collections.emptyList();
        } else if (alternate.getPhaseSet() != null) {
            for (VariantKeyFields variantKeyFields : alternates) {
                if (variantKeyFields.getNumAllele() > 0) {
                    throw new IllegalStateException("Unable to resolve multiallelic with MNV variants -> "
                            + alternates.stream()
                            .map((v) -> chromosome + ":" + v.toString())
                            .collect(Collectors.joining(" , ")));
                }
            }
            secondaryAlternates = Collections.emptyList();
        } else {

            secondaryAlternates = new ArrayList<>(alternates.size() - 1);
            for (VariantKeyFields keyFields : alternates) {
                if (keyFields.isReferenceBlock()) {
                    continue;
                }
                if (!keyFields.equals(alternate)) {
                    secondaryAlternates.add(new AlternateCoordinate(
                            // Chromosome is always the same, do not set
                            null,
                            //Set position only if is different from the original one
                            alternate.getStart() == keyFields.getStart() ? null : keyFields.getStart(),
                            alternate.getEnd() == keyFields.getEnd() ? null : keyFields.getEnd(),
                            //Set reference only if is different from the original one
                            alternate.getReference().equals(keyFields.getReference()) ? null : keyFields.getReference(),
                            keyFields.getAlternate(),
                            Variant.inferType(keyFields.getReference(), keyFields.getAlternate(), keyFields.getEnd() - keyFields.getStart() + 1)
                    ));
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
        private String phaseSet;
        private String reference;
        private String alternate;

        boolean referenceBlock;

        public VariantKeyFields(int start, int end, String reference, String alternate) {
            this(start, end, 0, reference, alternate, false);
        }

        public VariantKeyFields(int start, int end, int numAllele, String reference, String alternate) {
            this(start, end, numAllele, reference, alternate, false);
        }

        public VariantKeyFields(int start, int end, int numAllele, String reference, String alternate, boolean referenceBlock) {
            this.start = start;
            this.end = end;
            this.numAllele = numAllele;
            this.reference = reference;
            this.alternate = alternate;
            this.referenceBlock = referenceBlock;
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

        public int getReferenceEnd() {
            return start + reference.length() - 1;
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

        public String getPhaseSet() { return phaseSet; }

        public VariantKeyFields setPhaseSet(String phaseSet) {
            this.phaseSet = phaseSet;
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

        public boolean isReferenceBlock() {
            return referenceBlock;
        }

        public VariantKeyFields setReferenceBlock(boolean referenceBlock) {
            this.referenceBlock = referenceBlock;
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
            return start + ":" + reference + ":" + alternate + ":" + numAllele
                    + (phaseSet == null ? "" : ("(ps:" + phaseSet + ")"))
                    + (referenceBlock ? ("(END=" + end + ")") : "");
        }


    }

}
