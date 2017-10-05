package org.opencb.biodata.tools.variant;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by priesgo on 04/10/17.
 */
public class LeftAlignmentTest  extends VariantNormalizerGenericTest{

    Path trickyReference;
    protected Path referenceGenomeUncompressed;
    protected Path referenceGenomeCompressed;


    @Before
    public void setUp() throws Exception {

        referenceGenomeUncompressed = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa").toURI()
        );
        referenceGenomeCompressed = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );
        trickyReference = Paths.get(getClass().getResource("/tricky.fasta").toURI());
        super.setUp();
    }

    @Test
    public void testNormalizedSamplesDataSameNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // C -> A  === C -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "C", "A", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData1NoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // AC -> AA  === C -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "AC", "AA", 101, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData2NoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // CA -> AA  === C -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "CA", "AA", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftDeletionNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // AC -> C  === A -> .
        // no left alignment as position 100 at chromosome 1 is N
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "AC", "C", 100, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataRightDeletionNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // CA -> C  === A -> .
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "CA", "C", 101, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataAmbiguousDeletionNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // AAA -> A  === AA -> .
        // no left alignment as position 100 at chromosome 1 is N
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "AAA", "A", 100, 101, "AA", "");
    }

    @Test
    public void testNormalizeSamplesDataIndelNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // ATC -> ACCC  === T -> CC
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "ATC", "ACCC", 101, 101, "T", "CC");
    }

    @Test
    public void testNormalizeSamplesDataRightInsertionNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // C -> AC  === . -> A
        // no left alignment as position 100 at chromosome 1 is N
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "C", "AC", 100, 99, "", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftInsertionNoLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {
        // C -> CA  === . -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("1", 100, "C", "CA", 101, 100, "", "A");
    }

    @Test
    public void testNormalizedSamplesData1bpDeletionLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        Context:
        CTGGACTCTGACCCTGATTGTTGAGGGCTGCAAAGAGGAAGA**ATTTT**ATTTACCGTCGCT

        Input:
        chr10:10486:TT>T
        chr10:10485:TT>T
        chr10:10484:TT>T
        chr10:10483:AT>A
        chr10:10484:T>-
        chr10:10485:T>-
        chr10:10486:T>-
        chr10:10487:T>-

        Output:
        chr10:10484:T>-
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("10", 10486, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10485, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10484, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10483, "AT", "A", 10484, "T", "");
        // FIXME: using the context-free representation breaks normalization
        testSampleNormalization("10", 10484, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10485, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10486, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10487, "T", "", 10484, 10484, "T", "", false);
    }

    @Test
    public void testNormalizedSamplesData2bpDeletionLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        Context:
        208: TCACTGCTCCATTGATTAAGCAAGTCTGG**GACACACA**TGTAGCTAAGCTGTGAGTTCTGT

        (206*60) + 29 = 12389

        Input:
        chr10:12395:ACA>A
        chr10:12394:CAC>C
        chr10:12393:ACA>A
        chr10:12392:CAC>C
        chr10:12391:ACA>A
        chr10:12390:GAC>G
        chr10:12391:AC>-
        chr10:12392:CA>-
        chr10:12393:AC>-
        chr10:12394:CA>-
        chr10:12395:AC>-
        chr10:12396:CA>-

        Output:
        chr10:12391:AC>-
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("10", 12395, "ACA", "A", 12391, 12392, "AC", "");
        testSampleNormalization("10", 12394, "CAC", "C", 12391, 12392, "AC", "");
        testSampleNormalization("10", 12393, "ACA", "A", 12391, 12392, "AC", "");
        testSampleNormalization("10", 12392, "CAC", "C", 12391, 12392, "AC", "");
        testSampleNormalization("10", 12391, "ACA", "A", 12391, 12392, "AC", "");
        testSampleNormalization("10", 12390, "GAC", "G", 12391, 12392, "AC", "");
        // FIXME: using the context-free representation breaks normalization
        testSampleNormalization("10", 12391, "AC", "", 12391, 12392, "AC", "", false);
        testSampleNormalization("10", 12392, "CA", "", 12391, 12392, "AC", "", false);
        testSampleNormalization("10", 12393, "AC", "", 12391, 12392, "AC", "", false);
        testSampleNormalization("10", 12394, "CA", "", 12391, 12392, "AC", "", false);
        testSampleNormalization("10", 12395, "AC", "", 12391, 12392, "AC", "", false);
        testSampleNormalization("10", 12396, "CA", "", 12391, 12392, "AC", "", false);
    }

    @Test
    public void testNormalizedSamplesData1bpInsertionLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        Context:
        CTGGACTCTGACCCTGATTGTTGAGGGCTGCAAAGAGGAAGA**ATTTT**ATTTACCGTCGCT

        Input:
        chr10:10483:A>AT
        chr10:10484:T>TT
        chr10:10485:T>TT
        chr10:10486:T>TT
        chr10:10487:T>TT
        chr10:10484:->T
        chr10:10485:->T
        chr10:10486:->T
        chr10:10487:->T
        chr10:10488:->T

        Output:
        chr10:10484:->T
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        //testSampleNormalization("10", 10483, "A", "AT", 10484, "", "T");
        testSampleNormalization("10", 10484, "T", "TT", 10484, 10483, "", "T");
        testSampleNormalization("10", 10485, "T", "TT", 10484, 10483, "", "T");
        testSampleNormalization("10", 10486, "T", "TT", 10484, 10483, "", "T");
        testSampleNormalization("10", 10487, "T", "TT", 10484, 10483, "", "T");
        // FIXME: using the context-free representation breaks normalization
        // FIXME: breaks the generation of reference blocks
        this.normalizer.setGenerateReferenceBlocks(false);
        testSampleNormalization("10", 10484, "", "T", 10484, 10483, "", "T", false);
        testSampleNormalization("10", 10485, "", "T", 10484, 10483, "", "T", false);
        testSampleNormalization("10", 10486, "", "T", 10484, 10483, "", "T", false);
        testSampleNormalization("10", 10487, "", "T", 10484, 10483, "", "T", false);
    }

    @Test
    public void testNormalizedSamplesData2bpInsertionLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        Context:
        TCACTGCTCCATTGATTAAGCAAGTCTGG**GACACACA**TGTAGCTAAGCTGTGAGTTCTGT

        Input:
        chr10:12397:A>ACA
        chr10:12396:C>CAC
        chr10:12395:A>ACA
        chr10:12394:C>CAC
        chr10:12393:A>ACA
        chr10:12392:C>CAC
        chr10:12391:A>ACA
        chr10:12390:G>GAC
        chr10:12391:->AC
        chr10:12398:->CA
        chr10:12397:->AC
        chr10:12396:->CA
        chr10:12395:->AC
        chr10:12394:->CA
        chr10:12393:->AC
        chr10:12392:->CA

        Output:
        chr10:12391:->AC
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        //testSampleNormalization("10", 10483, "A", "AT", 10484, "", "T");
        //testSampleNormalization("10", 12397, "A", "ACA", 12391, 12390, "", "AC");
        //testSampleNormalization("10", 12396, "C", "CAC", 12391, 12390, "", "AC");
        //testSampleNormalization("10", 12395, "A", "ACA", 12391, 12390, "", "AC");
        //testSampleNormalization("10", 12394, "C", "CAC", 12391, 12390, "", "AC");
        //testSampleNormalization("10", 12393, "A", "ACA", 12391, 12390, "", "AC");
        //testSampleNormalization("10", 12392, "C", "CAC", 12391, 12390, "", "AC");
        //testSampleNormalization("10", 12391, "A", "ACA", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12390, "G", "GAC", 12391, 12390, "", "AC");
        // FIXME: using the context-free representation breaks normalization
        // FIXME: breaks the generation of reference blocks
        this.normalizer.setGenerateReferenceBlocks(false);
        testSampleNormalization("10", 12391, "", "AC", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12392, "", "CA", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12393, "", "AC", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12394, "", "CA", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12395, "", "AC", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12396, "", "CA", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12397, "", "AC", 12391, 12390, "", "AC", false);
        testSampleNormalization("10", 12398, "", "CA", 12391, 12390, "", "AC", false);
    }

    @Test
    public void testNormalizedSamplesData6bpDeletionLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        ### Test case 5:
        Left alignment not applied as repetive sequence is preceded by a sequence of Ns
        Left alignment window of 100bp may be exhausted as repetitive region is 384bp long (6bp repeated 64 times)

        Context:
        168 NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN**CTAACCCTAACCCTAACCCT
        169   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        170   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        171   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        172   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        173   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        174   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT**TAACCC

        Start:  (166*60) + 40 = 10000
        End:    (172*60) + 54 = 10374

        Input:
        10000:CTAACCC>C
        10006:CTAACCC>C
        10012:CTAACCC>C
        10324:CTAACCC>C
        10384:CTAACCC>C
        10378:CTAACCC>C

        Output: the same as input after left and right trimming, any of these variants is normalized as the sequence of Ns is reached
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        // right trimming C + failed left alignment as N is reached
        testSampleNormalization("10", 10000, "CTAACCC", "C", 10000, 10005, "CTAACC", "");
        // right trimming C + failed left alignment as N is reached
        testSampleNormalization("10", 10006, "CTAACCC", "C", 10006, 10011, "CTAACC", "");
        // right trimming C + failed left alignment as N is reached
        testSampleNormalization("10", 10012, "CTAACCC", "C", 10012, 10017, "CTAACC", "");
        // right trimming C + failed left alignment as N is reached +  + window exhausted
        testSampleNormalization("10", 10318, "CTAACCC", "C", 10318, 10323, "CTAACC", "");
        // left trimming T + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10319, "TAACCCC", "T", 10320, 10325, "AACCCC", "");
        // left trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10320, "AACCCCT", "A", 10321, 10326, "ACCCCT", "");
        // right trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10321, "ACCCCTA", "A", 10321, 10326, "ACCCCT", "");
        // left trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10322, "CCCCTAA", "C", 10323, 10328, "CCCTAA", "");
        // right trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10323, "CCCTAAC", "C", 10323, 10328, "CCCTAA", "");
        // failed left alignment as N is reached
        testSampleNormalization("10", 10001, "TAACCC", "", 10001, 10006, "TAACCC", "", false);
        // failed left alignment as N is reached
        testSampleNormalization("10", 10007, "TAACCC", "", 10007, 10012, "TAACCC", "", false);
        // failed left alignment as N is reached
        testSampleNormalization("10", 10013, "TAACCC", "", 10013, 10018, "TAACCC", "", false);
        // failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10319, "TAACCC", "", 10319, 10324, "TAACCC", "", false);
        // left trimming T + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10320, "AACCCC", "", 10320, 10325, "AACCCC", "", false);
        // left trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10321, "ACCCCT", "", 10321, 10326, "ACCCCT", "", false);
        // right trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10322, "CCCCTA", "", 10322, 10327, "CCCCTA", "", false);
        // left trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10323, "CCCTAA", "", 10323, 10328, "CCCTAA", "", false);
        // right trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10324, "CCTAAC", "", 10324, 10329, "CCTAAC", "", false);
    }

    @Test
    public void testNormalizedSamplesData6bpInsertionLeftAlignment()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        ### Test case 6

        6 bp insertions
        Left alignment not applied as repetive sequence is preceded by a sequence of Ns
        Left alignment window of 100bp may be exhausted as repetitive region is 384bp long (6bp repeated 64 times)

        Context:
        168 NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN**CTAACCCTAACCCTAACCCT
        169   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        170   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        171   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        172   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        173   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT
        174   AACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCCT**TAACCC

        Start:  (166*60) + 40 = 10000
        End:    (172*60) + 54 = 10374

        Input:
        10000:C>CTAACCC
        10006:CTAACCC>CTAACCC
        10012:CTAACCC>CTAACCC
        10318:CTAACCC>CTAACCC
        10319:T>TAACCCC
        10320:A>AACCCCT
        10321:A>ACCCCTA
        10322:C>CCCCTAA
        10323:C>CCCTAAC
        10001:>TAACCC
        10007:>TAACCC
        10013:>TAACCC
        10319:>TAACCC
        10320:>AACCCC
        10321:>ACCCCT
        10322:>CCCCTA
        10323:>CCCTAA
        10324:>CCTAAC

        Output: the same as input after left and right trimming, any of these variants is normalized as the sequence of Ns is reached
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        // right trimming C + failed left alignment as N is reached
        testSampleNormalization("10", 10001, "C", "CTAACCC", 10001, 10000, "", "CTAACC");
        // right trimming C + failed left alignment as N is reached
        testSampleNormalization("10", 10007, "C", "CTAACCC", 10007, 10006, "", "CTAACC");
        // right trimming C + failed left alignment as N is reached
        testSampleNormalization("10", 10013, "C", "CTAACCC", 10013, 10012, "", "CTAACC");
        // right trimming C + failed left alignment as N is reached +  + window exhausted
        testSampleNormalization("10", 10319, "C", "CTAACCC", 10319, 10318, "", "CTAACC");
        // left trimming T + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10320, "T", "TAACCCC", 10321, 10320, "", "AACCCC");
        // left trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10321, "A", "AACCCCT", 10322, 10321, "", "ACCCCT");
        // right trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10322, "A", "ACCCCTA", 10322, 10321, "", "ACCCCT");
        // left trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10323, "C", "CCCCTAA", 10324, 10323, "", "CCCTAA");
        // right trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10324, "C", "CCCTAAC", 10324, 10323, "", "CCCTAA");
        this.normalizer.setGenerateReferenceBlocks(false);
        // failed left alignment as N is reached
        testSampleNormalization("10", 10002, "", "TAACCC", 10002, 10001, "", "TAACCC", false);
        // failed left alignment as N is reached
        testSampleNormalization("10", 10008, "", "TAACCC", 10008, 10007, "", "TAACCC", false);
        // failed left alignment as N is reached
        testSampleNormalization("10", 10014, "", "TAACCC", 10014, 10013, "", "TAACCC", false);
        // failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10320, "", "TAACCC", 10320, 10319, "", "TAACCC", false);
        // left trimming T + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10321, "", "AACCCC", 10321, 10320, "", "AACCCC", false);
        // left trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10322, "", "ACCCCT", 10322, 10321, "", "ACCCCT", false);
        // right trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10323, "", "CCCCTA", 10323, 10322, "", "CCCCTA", false);
        // left trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10324, "", "CCCTAA", 10324, 10323, "", "CCCTAA", false);
        // right trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10325, "", "CCTAAC", 10325, 10324, "", "CCTAAC", false);
    }

    @Test
    public void testRequireTestAlignment() {
        assertTrue(VariantNormalizer.requireLeftAlignment("", "A"));
        assertTrue(VariantNormalizer.requireLeftAlignment("A", ""));
        assertTrue(VariantNormalizer.requireLeftAlignment("ACCA", "A"));
        assertTrue(VariantNormalizer.requireLeftAlignment("A", "ACCA"));
        assertTrue(VariantNormalizer.requireLeftAlignment("ACTTTAC", "AC"));
        assertTrue(VariantNormalizer.requireLeftAlignment("AC", "ACTTTAC"));
        assertFalse(VariantNormalizer.requireLeftAlignment(
                "AT",
                "GCT",
                new VariantNormalizer.VariantKeyFields(12345, 123456, "A", "GC")
        ));
        assertTrue(VariantNormalizer.requireLeftAlignment("A", "CTTTA"));
        assertFalse(VariantNormalizer.requireLeftAlignment(
                "A",
                "ACTTT",
                new VariantNormalizer.VariantKeyFields(12345, 123456, "", "CTTT")
        ));
    }

    @Test
    public void testWrongReferenceGenome()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        // Initializing the normalizer with a compressed reference genome should raise an exception
        try {
            this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
            assertTrue(false);
        }
        catch (IllegalArgumentException ex) {
            assertTrue(true);
            assertTrue(ex.getMessage().startsWith("A reference genome extension must be one of"));
        }

        // Initializing the normalizer with an unexisting reference genome should raise an exception
        try {
            this.normalizer.enableLeftAlign("idontexist.fasta");
            assertTrue(false);
        }
        catch (FileNotFoundException ex) {
            assertTrue(true);
        }

        // Initializing the normalizer with a reference genome with wrong extension (txt) should raise an exception
        try {
            this.normalizer.enableLeftAlign(Paths.get(
                    getClass().getResource("/homo_sapiens_grch38_small.txt").toURI()).toString());
            assertTrue(false);
        }
        catch (IllegalArgumentException ex) {
            assertTrue(true);
            assertTrue(ex.getMessage().startsWith("A reference genome extension must be one of"));
        } catch (URISyntaxException e) {
            assertTrue(false);
        }

        // Initializing the normalizer with an unindexed reference genome should raise an exception
        try {
            this.normalizer.enableLeftAlign(Paths.get(
                    getClass().getResource("/homo_sapiens_grch38_small_unindexed.fa").toURI()).toString());
            assertTrue(false);
        }
        catch (FileNotFoundException ex) {
            assertTrue(true);
        } catch (URISyntaxException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testReferenceBasesMismatch()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        Context:
        CTGGACTCTGACCCTGATTGTTGAGGGCTGCAAAGAGGAAGA**ATTTT**ATTTACCGTCGCT

        Input:
        chr10:10486:TT>T
        chr10:10485:TT>T
        chr10:10484:TT>T
        chr10:10483:AT>A
        chr10:10484:T>-
        chr10:10485:T>-
        chr10:10486:T>-
        chr10:10487:T>-

        Output:
        chr10:10484:T>-
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());
        testSampleNormalization("10", 10484, "ATT", "T", 10484, 10485, "AT", "");
        testSampleNormalization("10", 10485, "AT", "TTTT", 10485, 10485, "A", "TTT");
    }

    @Test
    public void testCompressedReferenceGenome()
            throws NonStandardCompliantSampleField, FileNotFoundException {

        /*
        Context:
        CTGGACTCTGACCCTGATTGTTGAGGGCTGCAAAGAGGAAGA**ATTTT**ATTTACCGTCGCT

        Input:
        chr10:10486:TT>T
        chr10:10485:TT>T
        chr10:10484:TT>T
        chr10:10483:AT>A
        chr10:10484:T>-
        chr10:10485:T>-
        chr10:10486:T>-
        chr10:10487:T>-

        Output:
        chr10:10484:T>-
         */
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("10", 10486, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10485, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10484, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10483, "AT", "A", 10484, "T", "");
        // FIXME: using the context-free representation breaks normalization
        testSampleNormalization("10", 10484, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10485, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10486, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10487, "T", "", 10484, 10484, "T", "", false);
    }

    @Test
    public void testTrickyInsertion()
            throws NonStandardCompliantSampleField, FileNotFoundException, URISyntaxException {

        /*

         */
        this.normalizer.enableLeftAlign(trickyReference.toString());
        //testSampleNormalization("10", 10483, "A", "AT", 10484, "", "T");
        testSampleNormalization("1", 10, "C", "CCTAACC", 5, 4, "", "CTAACC");
    }

    @Test
    public void testRightTrimmingAndLeftAlignment()
            throws NonStandardCompliantSampleField, IOException, URISyntaxException {

        this.normalizer.disableLeftAlign();
        //testSampleNormalization("2", 8, "C", "CCTCC", 8, 7, "", "CCTC");
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        /*
        Insertions of a whole repetitive block, CTCC
        Some requiring right trimming + left alignment, all requiring left alignment

        Context:
        > 2 (repetitive block CTCC)
        GGGGCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCC
        1234567890123456789012345678901234567890

        Output:
        5:->CTCC
         */
        testSampleNormalization("2", 8, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 8, "", "CCTC", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 4, "G", "GCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 5, "", "CTCC", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 11, "C", "CCCTC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 11, "", "CCCT", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 12, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 12, "", "CCTC", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 13, "C", "CTCCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 13, "", "CTCC", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 14, "", "TCCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 14, "T", "TCCCT", 5, 4, "", "CTCC");
        testSampleNormalization("2", 14, "", "TCCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 15, "", "CCCT", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 15, "C", "CCCTC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 15, "", "CCCT", 5, 4, "", "CTCC");
        testSampleNormalization("2", 16, "", "CCTC", 5, 4, "", "CTCC", false);
        testSampleNormalization("2", 16, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 16, "", "CCTC", 5, 4, "", "CTCC");
        testSampleNormalization("2", 17, "", "CTCC", 5, 4, "", "CTCC", false);
        /*
        Insertion of a partial repetitive block
        Partial left alignment is required.
          */
        testSampleNormalization("2", 8, "C", "CCTCG", 9, 8, "", "CTCG", false);
        testSampleNormalization("2", 8, "C", "CCTGC", 8, 7, "", "CCTG", false);
        testSampleNormalization("2", 8, "C", "CCGCC", 7, 6, "", "CCCG", false);
        testSampleNormalization("2", 8, "C", "CGTCC", 6, 5, "", "TCCG", false);
        testSampleNormalization("2", 8, "C", "ACTCC", 5, 4, "", "CTCA", false);
        testSampleNormalization("2", 8, "C", "GCTCC", 4, 3, "", "GCTC", false);
        /*
        Insertions not requiring left alignment
         */
        testSampleNormalization("2", 8, "C", "CGGGTTTT", 9, 8, "", "GGGTTTT", false);
        testSampleNormalization("2", 8, "C", "CTTTT", 9, 8, "", "TTTT", false);
        testSampleNormalization("2", 8, "C", "TTTTT", 8, 8, "C", "TTTTT", false);
        testSampleNormalization("2", 8, "C", "CACACA", 9, 8, "", "ACACA", false);
        testSampleNormalization("2", 8, "CT", "TTTTT", 8, 8, "C", "TTTT", false);
        testSampleNormalization("2", 8, "CTT", "TTTTT", 8, 8, "C", "TTT", false);
        testSampleNormalization("2", 8, "CTTT", "TTTTT", 8, 8, "C", "TT", false);
        testSampleNormalization("2", 8, "CTTTT", "TTTTT", 8, 8, "C", "T", false);
        testSampleNormalization("2", 8, "CGT", "TTTTT", 8, 9, "CG", "TTTT", false);
    }
}
