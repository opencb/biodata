package org.opencb.biodata.tools.variant;

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

    private static final Set<Character> ACCEPTED_BASES =
            new HashSet(Arrays.asList('a', 'c', 'g', 't', 'A', 'C', 'G', 'T'));
    private final String[] acceptedExtensions = {".fa", ".fn", ".fasta"}; //, ".gz"};
    private SamtoolsFastaIndex referenceGenomeReader;
    private String referenceGenome;
    private int windowSize;

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
     * Calculates the allele length considering "-" for empty alleles
     * @param allele
     * @return
     */
    static int getAlleleLength(String allele) {
        return allele != null && !allele.equals("-")? allele.length() : 0;
    }

    /**
     * Only accepts as a valid base A, C, G and T
     * @param base
     * @return
     */
    static boolean isValidBase(char base) {
        return ACCEPTED_BASES.contains(base);
    }

    /**
     * Checks if both reference and alternate bases are correct
     * @param referenceBase
     * @param alternateBase
     * @return
     */
    static boolean areValidBases(char referenceBase, char alternateBase) {
        return isValidBase(referenceBase) && isValidBase(alternateBase);
    }

    /**
     * Checks if all bases in the reference are valid bases.
     * @param reference the reference bases
     * @return
     */
    static boolean isAlternateCorrect(String reference) {

        boolean isCorrect = true;
        if (!StringUtils.isEmpty(reference)) {
            for (char base : reference.toCharArray()) {
                isCorrect = isCorrect && isValidBase(base);
                if (!isCorrect) {
                    return isCorrect;
                }
            }
        }
        return isCorrect;
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

        public LeftAlignmentWindow(
                int windowStart, int windowEnd, String chromosome, SamtoolsFastaIndex referenceGenomeReader
        ) {
            this.windowStart = windowStart;
            if (windowStart < 1) {
                this.windowStart = 1;
            }
            this.windowEnd = windowEnd;
            this.chromosome = chromosome;
            this.referenceGenomeReader = referenceGenomeReader;
            this.sequence = this.referenceGenomeReader.query(this.chromosome, this.windowStart, this.windowEnd);
        }

        public int getWindowStart() {
            return windowStart;
        }

        public int getWindowEnd() {
            return windowEnd;
        }

        public String getSequence() {
            return sequence;
        }

        public boolean isChromosomeExhausted() {
            return this.windowStart == 1 && this.windowEnd == 1;
        }

        public LeftAlignmentWindow slideWindow(int windowSize) {
            windowStart = windowStart - windowSize;
            if (windowStart < 1) {
                // the window cannot go below position 1 as genomic coordinates in this context are 1-based
                windowStart = 1;
            }
            windowEnd = windowEnd - windowSize;
            sequence = referenceGenomeReader.query(chromosome, windowStart, windowEnd);
            return this;
        }
    }

    /**
     * Checks that a given reference matches the reference genome
     * @param reference
     * @param lastReferenceBaseRelativePosition
     * @param sequence
     * @return
     */
    private static boolean checkReferenceMatchGenome(
            String reference, int lastReferenceBaseRelativePosition, String sequence) {

        return reference.equals(sequence.substring(
                lastReferenceBaseRelativePosition - getAlleleLength(reference) + 1,
                lastReferenceBaseRelativePosition + 1
        ));
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
     */
    public void leftAlign(VariantNormalizer.VariantKeyFields variant, String chromosome) {

        String reference = variant.getReference();
        String alternate = variant.getAlternate();
        int referenceLength = this.getAlleleLength(reference);
        int alternateLength = this.getAlleleLength(alternate);
        int lastReferenceBasePosition = variant.getStart() + referenceLength - 1;
        int lastAlternateBasePosition = variant.getStart() + alternateLength - 1;
        boolean hasInsertion = referenceLength - alternateLength < 0;
        boolean hasDeletion = referenceLength - alternateLength > 0;
        boolean hasIndel = hasInsertion || hasDeletion;
        // only left aligns indels
        if (hasIndel && isAlternateCorrect(reference) && isAlternateCorrect(alternate)) {
            // TODO: check if left alignment is required to avoid reading from the reference genome always
            // TODO: check if variant is at the beginning of the chromosome
            // gets an analysis window from the reference genome
            LeftAlignmentWindow alignmentWindow = new LeftAlignmentWindow(
                    Math.min(lastAlternateBasePosition, lastReferenceBasePosition)
                            - this.windowSize,
                    Math.max(lastReferenceBasePosition, lastAlternateBasePosition),
                    chromosome,
                    referenceGenomeReader
            );
            String sequence = alignmentWindow.getSequence();
            int lastReferenceBaseRelativePosition = lastReferenceBasePosition - alignmentWindow.getWindowStart();
            char referenceBase = sequence.charAt(lastReferenceBaseRelativePosition);
            boolean referenceCoherent = checkReferenceMatchGenome(
                    reference, lastReferenceBaseRelativePosition, sequence
            );
            int skipped_positions = 0;
            boolean applyLeftAlignment = false;

            if (hasInsertion && referenceCoherent) {
                // points to the bases to check for left alignment
                // beware that relative positions are 0-based
                int lastAlternateBaseRelativePosition = alternateLength - 1;
                char alternateBase = alternate.charAt(lastAlternateBaseRelativePosition);
                // only left aligns if last bases of reference and alternate equal and they contain new bases
                while (referenceBase == alternateBase &&
                        areValidBases(referenceBase, alternateBase) &&
                        !alignmentWindow.isChromosomeExhausted()) {

                    // slides the window 1bp to the left
                    skipped_positions ++;
                    lastReferenceBaseRelativePosition--;
                    lastAlternateBaseRelativePosition--;

                    // checks if window is exhausted and reloads
                    if (lastReferenceBaseRelativePosition < 0) {
                        alignmentWindow = alignmentWindow.slideWindow(this.windowSize);
                        sequence = alignmentWindow.getSequence();
                        lastReferenceBaseRelativePosition = this.windowSize - 1;  // 0-based
                    }

                    // checks if alternate sequence is exhausted and reloads
                    if (lastAlternateBaseRelativePosition < 0) {
                        // reloads alternate sequence from the end
                        lastAlternateBaseRelativePosition = alternateLength - 1;  // 0-based
                    }
                    referenceBase = sequence.charAt(lastReferenceBaseRelativePosition);
                    alternateBase = alternate.charAt(lastAlternateBaseRelativePosition);
                }
                applyLeftAlignment = skipped_positions > 0 &&
                        areValidBases(referenceBase, alternateBase) &&
                        !alignmentWindow.isChromosomeExhausted();
                // sets the new coordinates
                if (applyLeftAlignment) {
                    int positionInAlternate = alternateLength - (skipped_positions % alternateLength);
                    if (positionInAlternate > 0) {
                        variant.setAlternate(
                                alternate.substring(positionInAlternate) +
                                        alternate.substring(0, positionInAlternate)
                        );
                    }
                }

            } else if (hasDeletion && referenceCoherent) {
                // points to the bases to check for left alignment
                // beware that relative positions are 0-based
                int lastAlternateBaseRelativePosition = lastAlternateBasePosition - alignmentWindow.getWindowStart();
                char alternateBase = sequence.charAt(lastAlternateBaseRelativePosition);
                // only left aligns if last bases of reference and alternate equal and they contain new bases
                while (referenceBase == alternateBase &&
                        areValidBases(referenceBase, alternateBase) &&
                        !alignmentWindow.isChromosomeExhausted()) {

                    // slides the window 1bp to the left
                    skipped_positions ++;
                    lastAlternateBaseRelativePosition--;
                    lastReferenceBaseRelativePosition--;
                    if (lastAlternateBaseRelativePosition < 0) {
                        // window is exhausted
                        alignmentWindow = alignmentWindow.slideWindow(this.windowSize);
                        sequence = alignmentWindow.getSequence();
                        lastAlternateBaseRelativePosition = this.windowSize;
                        lastReferenceBaseRelativePosition = this.windowSize + referenceLength;
                    }
                    referenceBase = sequence.charAt(lastReferenceBaseRelativePosition);
                    alternateBase = sequence.charAt(lastAlternateBaseRelativePosition);
                }

                applyLeftAlignment = skipped_positions > 0 &&
                        areValidBases(referenceBase, alternateBase) &&
                        !alignmentWindow.isChromosomeExhausted();
                // sets the new alternate
                if (applyLeftAlignment) {
                    // TODO: can't we just set alternate to ""???
                    variant.setAlternate(
                            alternateLength == 0 ?
                                    "" :
                                    sequence.substring(
                                            lastAlternateBaseRelativePosition - alternateLength + 1,
                                            lastAlternateBaseRelativePosition + 1
                                    )
                    );
                }
            }

            if (applyLeftAlignment) {
                variant.setReference(referenceLength == 0 ?
                        "" :
                        sequence.substring(
                                lastReferenceBaseRelativePosition - referenceLength + 1,
                                lastReferenceBaseRelativePosition + 1
                        )
                );
                variant.setStart(variant.getStart() - skipped_positions);
                variant.setEnd(variant.getStart() + referenceLength - 1);
            }
        }
    }
}
