package org.opencb.biodata.tools.variant;

import htsjdk.samtools.SAMException;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.tools.sequence.SamtoolsFastaIndex;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by priesgo on 05/10/17.
 */
public class LeftAligner {

    private static final Set<Character> PRECISE_BASES =
            new HashSet<>(Arrays.asList('a', 'c', 'g', 't', 'A', 'C', 'G', 'T'));
    private static final Character N = 'N';
    private static final Set<Character> AMBIGUOUS_BASES =
            new HashSet<>(Arrays.asList('M', 'R', 'W', 'S', 'Y', 'K', 'V', 'H', 'D', 'B'));
    private final String[] acceptedExtensions = {".fa", ".fn", ".fasta"}; //, ".gz"};
    private SamtoolsFastaIndex referenceGenomeReader;
    private String referenceGenome;
    private int windowSize;
    private boolean acceptAmbiguousBasesInReference = true;
    private boolean acceptAmbiguousBasesInAlternate = false;

    public LeftAligner(String referenceGenome, int windowSize) throws FileNotFoundException {
        boolean validExtension = false;
        for (String acceptedExtension : acceptedExtensions) {
            if (referenceGenome.endsWith(acceptedExtension)) {
                validExtension = true;
                break;
            }
        }
        if (!validExtension) {
            throw new IllegalArgumentException(
                    String.format(
                            "A reference genome extension must be one of: %s",
                            Arrays.toString(acceptedExtensions)
                    )
            );
        }
        // it is checked by HTSJDK if there is a fai index exists
        this.referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome);
        this.referenceGenome = referenceGenome;
        this.windowSize = windowSize;
    }

    /**
     * Enables/disables the usage of Ns in the reference genome.
     * Default value: false
     * PRE: there are no ambiguous bases in the reference genome other than N
     *
     * @param acceptAmbiguousBasesInReference
     * @return
     */
    public LeftAligner setAcceptAmbiguousBasesInReference(boolean acceptAmbiguousBasesInReference) {

        this.acceptAmbiguousBasesInReference = acceptAmbiguousBasesInReference;
        return this;
    }

    /**
     * Enables/disables the usage of ambiguous bases in the alternate.
     * Default value: false
     * @param acceptAmbiguousBasesInAlternate
     * @return
     */
    public LeftAligner setAcceptAmbiguousBasesInAlternate(boolean acceptAmbiguousBasesInAlternate) {

        this.acceptAmbiguousBasesInAlternate = acceptAmbiguousBasesInAlternate;
        return this;
    }

    /**
     * Calculates the allele length considering "-" for empty alleles
     * @param allele
     * @return
     */
    static int getAlleleLength(String allele) {
        return allele != null? allele.length() : 0;
    }

    /**
     * Only accepts as a valid base A, C, G and T
     * or IUPAC ambiguous if enabled
     * @param base
     * @return
     */
    static boolean isValidBase(char base, boolean acceptAmbiguous) {
        boolean isValidBase = PRECISE_BASES.contains(base);
        if (!isValidBase && acceptAmbiguous) {
            isValidBase = N.equals(base) || AMBIGUOUS_BASES.contains(base);
        }
        return isValidBase;
    }

    /**
     * Checks if both reference and alternate bases are correct
     * @param referenceBase
     * @param alternateBase
     * @return
     */
    private boolean areValidBases(char referenceBase, char alternateBase) {
        return isValidBase(referenceBase, this.acceptAmbiguousBasesInReference)
                && isValidBase(alternateBase, this.acceptAmbiguousBasesInAlternate);
    }

    /**
     * Checks if all bases in the allele are valid bases.
     * @param allele the reference bases
     * @return
     */
    private boolean isAlleleCorrect(String allele, boolean acceptAmbiguousBases) {
        if (StringUtils.isNotEmpty(allele)) {
            for (char base : allele.toCharArray()) {
                if (!isValidBase(base, acceptAmbiguousBases)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * A class to hold the state of the window of the reference genome used for left alignment.
     */
    private class LeftAlignmentWindow {
        private int windowStart;
        private int windowEnd;
        private String chromosome;
        private SamtoolsFastaIndex referenceGenomeReader;
        private String sequence;
        // Absolute position
        private int position;

        LeftAlignmentWindow(int position, int offset, String chromosome, SamtoolsFastaIndex referenceGenomeReader) {
            this(position - windowSize, position + offset + 1, chromosome, referenceGenomeReader, position);
        }

        LeftAlignmentWindow(
                int windowStart, int windowEnd, String chromosome, SamtoolsFastaIndex referenceGenomeReader, int position
        ) {
            this.windowStart = windowStart;
            if (windowStart < 1) {
                this.windowStart = 1;
            }
            this.position = position;
            this.windowEnd = windowEnd;
            this.chromosome = chromosome;
            this.referenceGenomeReader = referenceGenomeReader;
            this.loadSequence();
        }

        String getSequence() {
            return sequence;
        }

        boolean isChromosomeExhausted() {
            return this.position == 0;
        }

        boolean isWindowExhausted() {
            return position == windowStart;
        }

        private void loadSequence() {
            this.sequence = this.referenceGenomeReader.query(this.chromosome, this.windowStart, this.windowEnd);
        }

        LeftAlignmentWindow slideWindow(int windowSize, int offset) {
            windowEnd = windowStart + offset + 1;
            windowStart = windowStart - windowSize;
            if (windowStart < 1) {
                // the window cannot go below position 1 as genomic coordinates in this context are 1-based
                windowStart = 1;
            }
            this.loadSequence();
            return this;
        }

        char getBase() {
            return isChromosomeExhausted() ? 'c' : getSequence().charAt(position - windowStart);
        }

        char slidePosition() {
            if (isWindowExhausted()) {
                slideWindow(windowSize, 0);
            }
            position--;
            return getBase();
        }

        String getSequence(int start, int end) {
            return sequence.substring(start - windowStart, end - windowStart + 1);
        }

    }

    /**
     * Checks that a given reference matches the reference genome
     * @param reference
     * @return
     */
    private static boolean checkReferenceMatchGenome(
            String reference, String expectedReference) {

        return reference.equals(expectedReference);
    }

    /**
     * Performs the left alignment of indels if:
     * * provided reference matches the reference genome
     * * bases in the genome are not ambiguous (i.e.: IUPAC codes are not accepted, only A, C, G and T)
     * * the chromosome is not exhausted
     * * there is a representation of the same variant in a lower position
     *
     * PRE: non-blocked suibstitutions have been discarded from left alignment previously
     *
     * @param variant
     * @param chromosome
     * @throws SAMException - when contig does not exist or query goes beyond contig boundaries
     */
    public void leftAlign(VariantNormalizer.VariantKeyFields variant, String chromosome) throws SAMException {

        String reference = variant.getReference();
        String alternate = variant.getAlternate();
        int referenceLength = getAlleleLength(reference);
        int alternateLength = getAlleleLength(alternate);
        boolean hasInsertion = referenceLength - alternateLength < 0;
        boolean hasDeletion = referenceLength - alternateLength > 0;
        boolean hasIndel = hasInsertion || hasDeletion;
        String allele = referenceLength == 0 ? alternate : reference;
        int alleleIndex = allele.length() - 1;

        // only left aligns indels
        if (hasIndel &&
                isAlleleCorrect(reference, this.acceptAmbiguousBasesInReference) &&
                isAlleleCorrect(alternate, this.acceptAmbiguousBasesInAlternate)) {

            LeftAlignmentWindow alignmentWindow = new LeftAlignmentWindow(variant.getStart() - 1, referenceLength, chromosome, referenceGenomeReader);
            char referenceBase = alignmentWindow.getBase();

            // if reference bases do not match the reference genome skips left alignment
            boolean referenceCoherent = checkReferenceMatchGenome(reference, alignmentWindow.getSequence(variant.getStart(), variant.getEnd()));
            if (referenceCoherent) {
                int skipped_positions = 0;
                boolean applyLeftAlignment;

                char alleleBase = allele.charAt(alleleIndex);
                while (referenceBase == alleleBase && areValidBases(referenceBase, alleleBase)) {
                    skipped_positions++;

                    referenceBase = alignmentWindow.slidePosition();
                    alleleIndex--;
                    if (alleleIndex < 0) {
                        alleleIndex = allele.length() - 1;
                    }
                    alleleBase = allele.charAt(alleleIndex);

                    // checks if chromosome is exhausted and ends
                    boolean reachedFirstPosition = alignmentWindow.isChromosomeExhausted();
                    if (reachedFirstPosition) {
                        break;
                    }
                }
                applyLeftAlignment = skipped_positions > 0 && areValidBases(referenceBase, alleleBase);

                if (applyLeftAlignment) {

                    if (alleleIndex != allele.length() - 1) {
                        allele = allele.substring(alleleIndex + 1) + allele.substring(0, alleleIndex + 1);
                    }
                    if (hasDeletion) {
                        variant.setReference(allele);
                    } else {
                        variant.setAlternate(allele);
                    }
                    variant.setStart(alignmentWindow.position + 1);
                    variant.setEnd(variant.getStart() + referenceLength - 1);
                }
            }
        }
    }
}
