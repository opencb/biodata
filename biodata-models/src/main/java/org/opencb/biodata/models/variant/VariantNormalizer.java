package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.ArrayUtils;
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
import org.opencb.biodata.models.variant.avro.StructuralVariation;
import org.opencb.commons.run.ParallelTaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
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
    private Map<Integer, int[]> genotypeReorderMapCache = new ConcurrentHashMap<>();

    private static final Set<String> VALID_NTS = new HashSet<>(Arrays.asList("A", "C", "G", "T", "N"));
    private static final String CNVSTRINGPATTERN = "<CN[0-9]+>";
    private static final Pattern CNVPATTERN = Pattern.compile(CNVSTRINGPATTERN);
    private static final String COPY_NUMBER_TAG = "CN";
    private static final String CIPOS_STRING = "CIPOS";
    private static final String CIEND_STRING = "CIEND";

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
            Integer end = variant.getEnd();
            String chromosome = variant.getChromosome();

            if (variant.getStudies() == null || variant.getStudies().isEmpty()) {
                List<VariantKeyFields> keyFieldsList;
                if (VariantType.CNV.equals(variant.getType())) {
                    keyFieldsList = normalizeCNV(start, end, reference, alternate, null);
                } else {
                    keyFieldsList = normalize(chromosome, start, reference, alternate);
                }
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

                    // FIXME: assumes there wont be multinucleotide positions with CNVs and short variants mixed
                    List<VariantKeyFields> keyFieldsList;
                    String copyNumberString = null;
                    if (VariantType.CNV.equals(variant.getType())) {
                        String sampleName = variant.getStudies().get(0).getSamplesName().iterator().next();
                        copyNumberString = variant.getStudies().get(0).getSampleData(sampleName).get(COPY_NUMBER_TAG);
                        keyFieldsList = normalizeCNV(start, end, reference, alternates, copyNumberString);
                    } else {
                        keyFieldsList = normalize(chromosome, start, reference, alternates);

                    }
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
                            // Variant is being reused, must ensure the SV field si appropriately created
                            if (VariantType.CNV.equals(variant.getType())) {
                                int[] impreciseStart = getImpreciseStart(variant);
                                int[] impreciseEnd = getImpreciseEnd(variant);
                               variant.setSv(new StructuralVariation(impreciseStart[0], impreciseStart[1],
                                       impreciseEnd[0], impreciseEnd[1],
                                       copyNumberString != null ? Integer.valueOf(copyNumberString)      // Assuming if copy number
                                               : getCopyNumberFromAlternate(keyFields.getAlternate()))); // is not provided in the
                                                                                                         // info field, it shall be
                                                                                                         // indicated as part of the
                                                                                                         // alternate allele string
                            }
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

    private Integer getCopyNumberFromAlternate(String alternate) {
        String copyNumberString = alternate.split("<CN")[1].split(">")[0];
        if (StringUtils.isNumeric(copyNumberString)) {
            return Integer.valueOf(copyNumberString);
        } else {
            return null;
        }
    }

    private int[] getImpreciseStart(Variant variant) {
        if (variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(CIPOS_STRING)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(CIPOS_STRING).split(",");
            return new int[]{variant.getStart() + Integer.parseInt(parts[0]),
                    variant.getStart() + Integer.parseInt(parts[1])};
        } else {
            return new int[]{variant.getStart(), variant.getStart()};
        }
    }

    private int[] getImpreciseEnd(Variant variant) {
        if (variant.getStudies().get(0).getFiles().get(0).getAttributes().containsKey(CIEND_STRING)) {
            String[] parts = variant.getStudies().get(0).getFiles().get(0).getAttributes().get(CIEND_STRING).split(",");
            return new int[]{variant.getEnd() + Integer.parseInt(parts[0]),
                    variant.getEnd() + Integer.parseInt(parts[1])};
        } else {
            return new int[]{variant.getEnd(), variant.getEnd()};
        }
    }

    public List<VariantKeyFields> normalizeCNV(Integer start, Integer end, String reference, String alternate,
                                               String copyNumber) {
        return normalizeCNV(start, end, reference, Collections.singletonList(alternate), copyNumber);
    }

    public List<VariantKeyFields> normalizeCNV(Integer start, Integer end, String reference, List<String> alternates,
                                               String copyNumber) {
        List<VariantKeyFields> list = new ArrayList<>(alternates.size());

        String newReference = reference;
        // Reference for CNVs must contain just one nucleotide - set to N if it's something different
        if (reference.length() != 1 || !VALID_NTS.contains(reference)) {
            newReference = "N";
        }

        int numAllelesIdx = 0; // This index is necessary for getting the samples where the mutated allele is present
        for (Iterator<String> iterator = alternates.iterator(); iterator.hasNext(); numAllelesIdx++) {
            String newAlternate = iterator.next();

            // Alternate must be of the form <CNxxx>, being xxx the number of copies
            if (!CNVPATTERN.matcher(newAlternate).matches()) {
                if (copyNumber != null && !copyNumber.isEmpty()) {
                    newAlternate = "<CN" + copyNumber + ">";
                } else {
                    newAlternate = "<CNV>";
                }
            }
            list.add(new VariantKeyFields(start, end, newReference, newAlternate));
        }

        return list;
    }


    public List<VariantKeyFields> normalize(String chromosome, int position, String reference, String alternate) {
        return normalize(chromosome, position, reference, Collections.singletonList(alternate));
    }

    public List<VariantKeyFields> normalize(String chromosome, int position, String reference, List<String> alternates) {
        List<VariantKeyFields> list = new ArrayList<>(alternates.size());
        int numAllelesIdx = 0; // This index is necessary for getting the samples where the mutated allele is present
        for (Iterator<String> iterator = alternates.iterator(); iterator.hasNext(); numAllelesIdx++) {
            String currentAlternate = iterator.next();
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

                // To deal with cases such as A>GT
                boolean isMnv = (keyFields.getReference().length() > 1 && keyFields.getAlternate().length() >= 1)
                        || (keyFields.getAlternate().length() > 1 && keyFields.getReference().length() >= 1);
                if (decomposeMNVs && isMnv) {
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
            boolean isFinished = false;
            while (current.isReferenceBlock() && !isFinished) {
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
                                }
                                break;
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
                } else {
                    isFinished = true;
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
        return !variant.getType().equals(VariantType.NO_VARIATION) && !variant.getType().equals(VariantType.SYMBOLIC);
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
        int[] secondaryAlternatesIdxMap = new int[1 + alternateAlleles.size()];  //reference + alternates
        int secondaryReferencesIdx = 2;
        int alleleIdx = 1;
        secondaryAlternatesMap[0] = "0";     // Set the reference id
        secondaryAlternatesIdxMap[0] = 0;
        for (String alternateAllele : alternateAlleles) {
            if (variantKeyFields.getNumAllele() == alleleIdx - 1) {
                secondaryAlternatesMap[alleleIdx] = "1";    //The first alternate
                secondaryAlternatesIdxMap[alleleIdx] = 1;
            } else {    //Secondary alternates will start at position 2, and increase sequentially
                secondaryAlternatesMap[alleleIdx] = Integer.toString(secondaryReferencesIdx);
                secondaryAlternatesIdxMap[alleleIdx] = secondaryReferencesIdx;
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
                        // All-alleles present
                        if (!sampleField.equals(".") && genotype != null
                                && (genotype.getCode() == AllelesCode.ALLELES_OK
                                || genotype.getCode() == AllelesCode.MULTIPLE_ALTERNATES)) {
                            String[] likelihoods = sampleField.split(",");

                            int ploidy = genotype.getPloidy();

                            if (ploidy == 1) {
                                if (likelihoods.length > 2) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(likelihoods[0]);
                                    for (int i = 1; i < secondaryAlternatesIdxMap.length; i++) {
                                        sb.append(",");
                                        sb.append(likelihoods[secondaryAlternatesIdxMap[i]]);
                                    }
                                    sampleField = sb.toString();
                                }
                            } else if (ploidy == 2) {
                                // If only 3 likelihoods are represented, no transformation is needed
                                if (likelihoods.length > 3) {
                                    int[] gtOrderMap = getGenotypesReorderingMap(variantKeyFields.getNumAllele(), secondaryAlternatesIdxMap);
                                    if (likelihoods.length != gtOrderMap.length) {
                                        throw new NonStandardCompliantSampleField(formatField, sampleField,
                                                "It must contain " + gtOrderMap.length + " values");
                                    }

                                    StringBuilder sb = new StringBuilder(likelihoods[0]);
                                    for (int i = 1; i < likelihoods.length; i++) {
                                        sb.append(",").append(likelihoods[gtOrderMap[i]]);
                                    }
                                    sampleField = sb.toString();
                                }
                            } else {
                                logger.warn("Do not normalize field " + formatField + " with ploidy = " + ploidy);
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

    /**
     * Gets an array for reordening the positions of a format field with Number=G and ploidy=2.
     *
     * In those fields where there is a value per genotype, if we change the order of the alleles,
     * the order of the genotypes will also change.
     *
     * The genotypes ordering is defined in the Vcf spec : https://samtools.github.io/hts-specs/VCFv4.3.pdf as "Genotype Ordering"
     * Given a number of alleles N and a ploidy of P, the order algorithm is:
     *   for (a_p = 0; a_p < N: a_p++)
     *      for (a_p-1 = 0; a_p-1 <= a_p: a_p-1++)
     *          ...
     *              for (a_1 = 0; a_1 <= a_2: a_1++)
     *                  print(a_1, a_2, ..., a_p)
     *
     * i.e:
     *  N=2,P=2:  00,01,11,02,12,22
     *  N=3,P=2:  00,01,11,02,12,22,03,13,23,33
     *
     *  With P=2, given a genotype a/b, where a<b, its position is b(b+a)/2+a
     *
     *  For each genotype, map the alleles using the alleleMap, and calculate
     *  the position of the new mapped genotype in the original order.
     *
     * int posInReorderedList;
     * int posInOriginalList = map[posInReorderedList];
     *
     * @param numAllele Num allele that defines the alleleMap.
     * @param alleleMap Allele map
     * @return          Map between the position in the new reordered list and the original one.
     * TODO: Allow ploidy>2
     */
    private int[] getGenotypesReorderingMap(int numAllele, int[] alleleMap) {
        int numAlleles = alleleMap.length;
        // int ploidy = 2;
        int key = numAllele * 100 + numAlleles;
        int[] map = genotypeReorderMapCache.get(key);
        if (map != null) {
            return map;
        } else {
            ArrayList<Integer> mapList = new ArrayList<>();
            for (int originalA2 = 0; originalA2 < alleleMap.length; originalA2++) {
                int newA2 = ArrayUtils.indexOf(alleleMap, originalA2);
                for (int originalA1 = 0; originalA1 <= originalA2; originalA1++) {
                    int newA1 = ArrayUtils.indexOf(alleleMap, originalA1);
                    if (newA1 <= newA2) {
                        mapList.add((newA2 * (newA2 + 1) / 2 + newA1));
                    } else {
                        mapList.add((newA1 * (newA1 + 1) / 2 + newA2));
                    }
                }
            }

            map = new int[mapList.size()];
            for (int i = 0; i < mapList.size(); i++) {
                map[i] = mapList.get(i);
            }

            genotypeReorderMapCache.put(key, map);
            return map;
        }
    }


    private Variant newVariant(Variant variant, VariantKeyFields keyFields) {
        Variant normalizedVariant = new Variant(variant.getChromosome(), keyFields.getStart(), keyFields.getEnd(), keyFields.getReference(), keyFields.getAlternate());
        normalizedVariant.setIds(variant.getIds());
        normalizedVariant.setStrand(variant.getStrand());
        normalizedVariant.setAnnotation(variant.getAnnotation());
        if (variant.getStudies() != null && !variant.getStudies().isEmpty()) {
            if (variant.getStudies().get(0).getAllAttributes().containsKey(CIPOS_STRING)) {
                int[] impreciseStart = getImpreciseStart(variant);
                normalizedVariant.getSv().setCiStartLeft(impreciseStart[0]);
                normalizedVariant.getSv().setCiStartLeft(impreciseStart[1]);
            }
            if (variant.getStudies().get(0).getAllAttributes().containsKey(CIEND_STRING)) {
                int[] impreciseEnd = getImpreciseEnd(variant);
                normalizedVariant.getSv().setCiEndLeft(impreciseEnd[0]);
                normalizedVariant.getSv().setCiEndLeft(impreciseEnd[1]);
            }

        }

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
