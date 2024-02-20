package org.opencb.biodata.tools.variant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.Variant;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by priesgo on 04/10/17.
 */
public class LeftAlignmentTest  extends VariantNormalizerGenericTest{

    Path trickyReference;
    protected Path referenceGenomeUncompressed;
    protected Path referenceGenomeCompressed;


    @BeforeEach
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
    public void testNormalizedSamplesDataSameNoLeftAlignment() throws Exception {
        // C -> A  === C -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "C", "A", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData1NoLeftAlignment() throws Exception {
        // AC -> AA  === C -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "AC", "AA", 101, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData2NoLeftAlignment() throws Exception {
        // CA -> AA  === C -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "CA", "AA", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftDeletionNoLeftAlignment() throws Exception {
        // AC -> C  === A -> .
        // no left alignment as position 100 at chromosome 1 is N
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "AC", "C", 100, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataRightDeletionNoLeftAlignment() throws Exception {
        // CA -> C  === A -> .
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "CA", "C", 101, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataAmbiguousDeletionNoLeftAlignment() throws Exception {
        // AAA -> A  === AA -> .
        // no left alignment as position 100 at chromosome 1 is N
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "AAA", "A", 100, 101, "AA", "");
    }

    @Test
    public void testNormalizeSamplesDataIndelNoLeftAlignment() throws Exception {
        // ATC -> ACCC  === T -> CC
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "ATC", "ACCC", 101, 101, "T", "CC");
    }

    @Test
    public void testNormalizeSamplesDataRightInsertionNoLeftAlignment() throws Exception {
        // C -> AC  === . -> A
        // no left alignment as position 100 at chromosome 1 is N
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "C", "AC", 100, 99, "", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftInsertionNoLeftAlignment() throws Exception {
        // C -> CA  === . -> A
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("1", 100, "C", "CA", 101, 100, "", "A");
    }

    @Test
    public void testNormalizedSamplesData1bpDeletionLeftAlignment() throws Exception {

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
    public void testNormalizedSamplesData2bpDeletionLeftAlignment() throws Exception {

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
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
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
    public void testNormalizedSamplesData1bpInsertionLeftAlignment() throws Exception {

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
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
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
    public void testNormalizedSamplesData2bpInsertionLeftAlignment() throws Exception {

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
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("10", 10483, "A", "AT", 10484, 10483, "", "T");
        testSampleNormalization("10", 12397, "A", "ACA", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12396, "C", "CAC", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12395, "A", "ACA", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12394, "C", "CAC", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12393, "A", "ACA", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12392, "C", "CAC", 12391, 12390, "", "AC");
        testSampleNormalization("10", 12391, "A", "ACA", 12391, 12390, "", "AC");
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
    public void testNormalizedSamplesData6bpDeletionLeftAlignment() throws Exception {

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
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        this.normalizer.setAcceptAmbiguousBasesInReference(false);
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
    public void testNormalizedSamplesData6bpInsertionLeftAlignment() throws Exception {

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
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        this.normalizer.setAcceptAmbiguousBasesInReference(false);
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
        testSampleNormalization("10", 10321, "", "AACCCT", 10321, 10320, "", "AACCCT", false);
        // left trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10322, "", "ACCCTA", 10322, 10321, "", "ACCCTA", false);
        // right trimming A + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10323, "", "CCCTAA", 10323, 10322, "", "CCCTAA", false);
        // left trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10324, "", "CCTAAC", 10324, 10323, "", "CCTAAC", false);
        // right trimming C + failed left alignment as N is reached + window exhausted
        testSampleNormalization("10", 10325, "", "CTAACC", 10325, 10324, "", "CTAACC", false);
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
    public void testWrongReferenceGenome() throws Exception {

        // Initializing the normalizer with an unexisting reference genome should raise an exception
        try {
            this.normalizer.enableLeftAlign("idontexist.fasta");
            assertTrue(false);
        }
        catch (IOException ex) {
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
        } catch (IOException ex) {
            assertTrue(true);
        } catch (URISyntaxException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testReferenceBasesMismatch() throws Exception {

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
        testSampleNormalization("10", 10484, "ATT", "T", 10484, 10485, "AT", "");
        testSampleNormalization("10", 10485, "AT", "TTTT", 10485, 10485, "A", "TTT");
    }

    @Test
    public void testCompressedReferenceGenome() throws Exception {

        // Initializing the normalizer with a compressed reference genome should not raise an exception
        this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        this.normalizer.enableLeftAlign(this.referenceGenomeUncompressed.toString());

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
        /*this.normalizer.enableLeftAlign(this.referenceGenomeCompressed.toString());
        testSampleNormalization("10", 10486, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10485, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10484, "TT", "T", 10484, "T", "");
        testSampleNormalization("10", 10483, "AT", "A", 10484, "T", "");
        // FIXME: using the context-free representation breaks normalization
        testSampleNormalization("10", 10484, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10485, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10486, "T", "", 10484, 10484, "T", "", false);
        testSampleNormalization("10", 10487, "T", "", 10484, 10484, "T", "", false);*/
    }

    @Test
    public void testTrickyInsertion() throws Exception {

        /*

         */
        this.normalizer.enableLeftAlign(trickyReference.toString());
        //testSampleNormalization("10", 10483, "A", "AT", 10484, "", "T");
        testSampleNormalization("1", 10, "C", "CCTAACC", 5, 4, "", "CTAACC");
    }

    @Test
    public void testInsertionRightTrimmingAndLeftAlignment() throws Exception {
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

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("2", 8, "C", "CCTCC", 8, 7, "", "CCTC");

        // enables left alignment
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        // different representations of 5:->CTCC
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

    @Test
    public void testDeletionRightTrimmingAndLeftAlignment() throws Exception {
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

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("2", 8, "CCTCC", "C", 8, 11, "CCTC", "");


        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        // different representations of 5:CTCC>-
        testSampleNormalization("2", 8, "CCTCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("2", 8, "CCTC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 4, "GCTCC", "G", 5, 8, "CTCC", "");
        testSampleNormalization("2", 5, "CTCC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 11, "CCCTC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("2", 11, "CCCT", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 12, "CCTCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("2", 12, "CCTC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 13, "CTCCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("2", 13, "CTCC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 14, "TCCC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 14, "TCCCT", "T", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 14, "TCCC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 15, "CCCT", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 15, "CCCTC", "C", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 15, "CCCT", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 16, "CCTC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 16, "CCTCC", "C", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 16, "CCTC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("2", 17, "CTCC", "", 5, 8, "CTCC", "", false);

        /*
        Deletions having reference bases not matching the reference genome
          */
        testSampleNormalization("2", 8, "CCTCG", "C", 9, 12, "CTCG", "", false);
        testSampleNormalization("2", 8, "CCTGC", "C", 8, 11, "CCTG", "", false);
        testSampleNormalization("2", 8, "CCGCC", "C", 8, 11, "CCGC", "", false);
        testSampleNormalization("2", 8, "CGTCC", "C", 8, 11, "CGTC", "", false);
        testSampleNormalization("2", 8, "CCTCC", "A", 8, 12, "CCTCC", "A", false);
        testSampleNormalization("2", 8, "CCTCC", "G", 8, 12, "CCTCC", "G", false);

        /*
        Deletions not requiring left alignment
         */
        testSampleNormalization("2", 8, "CCTC", "C", 8, 10, "CCT", "", false);
        testSampleNormalization("2", 8, "CCT", "C", 9, 10, "CT", "", false);
        testSampleNormalization("2", 12, "CCTC", "C", 12, 14, "CCT", "", false);
        testSampleNormalization("2", 12, "CCT", "C", 13, 14, "CT", "", false);
        testSampleNormalization("2", 16, "CCTC", "C", 16, 18, "CCT", "", false);
        testSampleNormalization("2", 16, "CCT", "C", 17, 18, "CT", "", false);
        testSampleNormalization("2", 8, "TC", "T", 9, 9, "C", "", false);
        testSampleNormalization("2", 8, "CTCC", "C", 8, 10, "CTC", "", false);
    }

    @Test
    public void testAmbiguousBases() throws Exception {

        /*
        Tests two cases:
        * Left alignment when Ns are found in the reference genome
        * Left alignment when the alternate sequence contains ambiguous bases

        Context:
        > 3 (ambiguous bases)
        NNNNCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCC
        1234567890123456789012345678901234567890
         */
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        // Ambiguous bases found in the reference
        testSampleNormalization("3", 8, "C", "CCTCC", 5, 4, "", "CTCC");
        this.normalizer.setAcceptAmbiguousBasesInReference(false);
        testSampleNormalization("3", 8, "C", "CCTCC", 8, 7, "", "CCTC");
        this.normalizer.setAcceptAmbiguousBasesInReference(true);
        testSampleNormalization("3", 8, "C", "CCTCC", 5, 4, "", "CTCC");


        // Ambiguous codes in the alternate
        // N and W are ambiguous IUPAC codes
        testSampleNormalization("3", 8, "C", "CNTCC", 8, 7, "", "CNTC");
        testSampleNormalization("3", 8, "C", "CWTCC", 8, 7, "", "CWTC");
        // Z is not an ambiguous IUPAC codes
        testSampleNormalization("3", 8, "C", "CZTCC", 8, 7, "", "CZTC");
        this.normalizer.setAcceptAmbiguousBasesInAlternate(true);
        testSampleNormalization("3", 8, "C", "CNTCC", 6, 5, "", "TCCN");
        testSampleNormalization("3", 8, "C", "CWTCC", 6, 5, "", "TCCW");
        testSampleNormalization("3", 8, "C", "CZTCC", 8, 7, "", "CZTC");
        this.normalizer.setAcceptAmbiguousBasesInAlternate(false);
        testSampleNormalization("3", 8, "C", "CNTCC", 8, 7, "", "CNTC");
        testSampleNormalization("3", 8, "C", "CWTCC", 8, 7, "", "CWTC");
        testSampleNormalization("3", 8, "C", "CZTCC", 8, 7, "", "CZTC");
    }

    @Test
    public void testExhaustedChromosome() throws Exception {

        /*
        Tests:
        * Repetitive region spanning to the chromosome start

        Context:
        > 4 (exhausted chromosome)
        CTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCC
        1234567890123456789012345678901234567890
         */
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        // Insertion requiring left alignment exhausting the chromosome
        testSampleNormalization("4", 8, "C", "CCTCC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 9, "C", "CTCCC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 10, "T", "TCCCT", 1, 0, "", "CTCC");
        testSampleNormalization("4", 11, "C", "CCCTC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 12, "C", "CCTCC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 9, "", "CTCC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 10, "", "TCCC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 11, "", "CCCT", 1, 0, "", "CTCC");
        testSampleNormalization("4", 12, "", "CCTC", 1, 0, "", "CTCC");
        testSampleNormalization("4", 13, "", "CTCC", 1, 0, "", "CTCC");
        // Deletion requiring left alignment exhausting the chromosome
        testSampleNormalization("4", 8, "CCTCC", "C", 1, 4, "CTCC", "");
        testSampleNormalization("4", 9, "CTCCC", "C", 1, 4, "CTCC", "");
        testSampleNormalization("4", 10, "TCCCT", "T", 1, 4, "CTCC", "");
        testSampleNormalization("4", 11, "CCCTC", "C", 1, 4, "CTCC", "");
        testSampleNormalization("4", 12, "CCTCC", "C", 1, 4, "CTCC", "");
        testSampleNormalization("4", 9, "CTCC", "", 1, 4, "CTCC", "", false);
        testSampleNormalization("4", 10, "TCCC", "", 1, 4, "CTCC", "", false);
        testSampleNormalization("4", 11, "CCCT", "", 1, 4, "CTCC", "", false);
        testSampleNormalization("4", 12, "CCTC", "", 1, 4, "CTCC", "", false);
        testSampleNormalization("4", 13, "CTCC", "", 1, 4, "CTCC", "", false);
    }

    @Test
    public void testDeletionAndSlidingWindow() throws Exception {
        /*
        Insertions of a whole repetitive block, CTCC for a region longer than the sliding window of 100 bp

        Context:
        > 5 (sliding window)
        GGGGCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCC ...
        1234567890123456789012345678901234567890

        Output:
        5:->CTCC
         */

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("5", 248, "CCTCC", "C", 248, 251, "CCTC", "");


        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        // different representations of 5:CTCC>-
        testSampleNormalization("5", 88, "CCTCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("5", 108, "CCTCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("5", 248, "CCTCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("5", 249, "CTCC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("5", 249, "CTCCC", "C", 5, 8, "CTCC", "");
        testSampleNormalization("5", 250, "TCCC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("5", 250, "TCCCT", "T", 5, 8, "CTCC", "");
        testSampleNormalization("5", 251, "CCCT", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("5", 251, "CCCTC", "C", 5, 8, "CTCC", "", false);
        testSampleNormalization("5", 252, "CCTC", "", 5, 8, "CTCC", "", false);
        testSampleNormalization("5", 252, "CCTCC", "C", 5, 8, "CTCC", "", false);
        testSampleNormalization("5", 253, "CTCCC", "C", 5, 8, "CTCC", "", false);

        /*
        Deletions not requiring left alignment
         */
        testSampleNormalization("5", 248, "CCTC", "C", 248, 250, "CCT", "", false);
        testSampleNormalization("5", 248, "CCT", "C", 249, 250, "CT", "", false);
        testSampleNormalization("5", 252, "CCTC", "C", 252, 254, "CCT", "", false);
        testSampleNormalization("5", 252, "CCT", "C", 253, 254, "CT", "", false);
        testSampleNormalization("5", 256, "CCTC", "C", 256, 258, "CCT", "", false);
        testSampleNormalization("5", 256, "CCT", "C", 257, 258, "CT", "", false);
        testSampleNormalization("5", 248, "TC", "T", 249, 249, "C", "", false);
        testSampleNormalization("5", 248, "CTCC", "C", 248, 250, "CTC", "", false);
    }

    @Test
    public void testInsertionAndSlidingWindow() throws Exception {
        /*
        Insertions of a whole repetitive block, CTCC
        Some requiring right trimming + left alignment, all requiring left alignment

        Context:
        > 5 (sliding window)
        GGGGCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCCCTCC ...
        1234567890123456789012345678901234567890

        Output:
        5:->CTCC
         */

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("5", 248, "C", "CCTCC", 248, 247, "", "CCTC");

        // enables left alignment
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);

        // different representations of 5:->CTCC
        testSampleNormalization("5", 248, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 248, "", "CCTC", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 244, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 245, "", "CTCC", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 251, "C", "CCCTC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 251, "", "CCCT", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 252, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 252, "", "CCTC", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 253, "C", "CTCCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 253, "", "CTCC", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 254, "", "TCCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 254, "T", "TCCCT", 5, 4, "", "CTCC");
        testSampleNormalization("5", 254, "", "TCCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 255, "", "CCCT", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 255, "C", "CCCTC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 255, "", "CCCT", 5, 4, "", "CTCC");
        testSampleNormalization("5", 256, "", "CCTC", 5, 4, "", "CTCC", false);
        testSampleNormalization("5", 256, "C", "CCTCC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 256, "", "CCTC", 5, 4, "", "CTCC");
        testSampleNormalization("5", 257, "", "CTCC", 5, 4, "", "CTCC", false);
        /*
        Insertion of a partial repetitive block
        Partial left alignment is required.
          */
        testSampleNormalization("5", 248, "C", "CCTCG", 249, 248, "", "CTCG", false);
        testSampleNormalization("5", 248, "C", "CCTGC", 248, 247, "", "CCTG", false);
        testSampleNormalization("5", 248, "C", "CCGCC", 247, 246, "", "CCCG", false);
        testSampleNormalization("5", 248, "C", "CGTCC", 246, 245, "", "TCCG", false);
        testSampleNormalization("5", 248, "C", "ACTCC", 245, 244, "", "CTCA", false);
        testSampleNormalization("5", 248, "C", "GCTCC", 245, 244, "", "CTCG", false);
        /*
        Insertions not requiring left alignment
         */
        testSampleNormalization("5", 248, "C", "CGGGTTTT", 249, 248, "", "GGGTTTT", false);
        testSampleNormalization("5", 248, "C", "CTTTT", 249, 248, "", "TTTT", false);
        testSampleNormalization("5", 248, "C", "TTTTT", 248, 248, "C", "TTTTT", false);
        testSampleNormalization("5", 248, "C", "CACACA", 249, 248, "", "ACACA", false);
        testSampleNormalization("5", 248, "CT", "TTTTT", 248, 248, "C", "TTTT", false);
        testSampleNormalization("5", 248, "CTT", "TTTTT", 248, 248, "C", "TTT", false);
        testSampleNormalization("5", 248, "CTTT", "TTTTT", 248, 248, "C", "TT", false);
        testSampleNormalization("5", 248, "CTTTT", "TTTTT", 248, 248, "C", "T", false);
        testSampleNormalization("5", 248, "CGT", "TTTTT", 248, 249, "CG", "TTTT", false);
    }

    @Test
    public void testUnexistingChromosomeOrCoordinates() throws Exception {
        /*
        Indels in unexisting contigs or in coordinates out of bounds
         */

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("imnotachromosome", 248, "C", "CCTCC", 248, 247, "", "CCTC");
        testSampleNormalization("imnotachromosome", 248, "CCTCC", "C", 248, 251, "CCTC", "");
        testSampleNormalization("5", 2480, "C", "CCTCC", 2480, 2479, "", "CCTC");
        testSampleNormalization("5", 2480, "CCTCC", "C", 2480, 2483, "CCTC", "");

        // enables left alignment
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);
        testSampleNormalization("imnotachromosome", 248, "C", "CCTCC", 248, 247, "", "CCTC");
        testSampleNormalization("imnotachromosome", 248, "CCTCC", "C", 248, 251, "CCTC", "");
        testSampleNormalization("5", 2480, "C", "CCTCC", 2480, 2479, "", "CCTC");
        testSampleNormalization("5", 2480, "CCTCC", "C", 2480, 2483, "CCTC", "");
    }

    @Test
    public void testMNVs() throws Exception {
        /*
        Indels in unexisting contigs or in coordinates out of bounds
         */

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("5", 10, "CT", "AG", 10, 11, "CT", "AG");

        // enables left alignment
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);
        testSampleNormalization("5", 10, "CT", "AG", 10, 11, "CT", "AG");
    }

    @Test
    public void testNonBlockedSubstitutions() throws Exception {
        /*
        Indels in unexisting contigs or in coordinates out of bounds
         */

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        testSampleNormalization("5", 8, "C", "GCTCC", 8, 7, "", "GCTC");

        // enables left alignment
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);
        testSampleNormalization("5", 8, "C", "GCTCC", 4, 3, "", "GCTC");
    }

    @Test
    public void testLostGenotypes() throws Exception {
        /*
        https://jira.extge.co.uk/browse/INTERP-2248
         */

        // tests left alignment disabled
        this.normalizer.disableLeftAlign();
        this.normalizer.setDecomposeMNVs(false);
        Variant variant = Variant.newBuilder("2:110855123:GCAGGGGCCG:TCAGGGGCCA,TCAGGGGCCG")
                .setStudyId("s").setFileId("f")
                .setSampleDataKeys("GT", "AD")
                .addSample("S1", "0/0", "1,2,8")
                .addSample("S2", "1/2", "3,4,8")
                .addSample("S3", "2/2", "5,6,8")
                .build();
//        Variant variant = new Variant("2", 110855123, "GCAGGGGCCG", "TCAGGGGCCA,TCAGGGGCCG");
//        new FileEntry();
        //new StudyEntry()
        //variant.setStudies();
        List<Variant> normalizedVariants = this.normalizer.normalize(Collections.singletonList(variant), false);

        for (Variant normalizedVariant : normalizedVariants) {
            System.out.println("normalizedVariant.toJson() = " + normalizedVariant.toJson());
        }
        assertTrue(normalizedVariants.size() == 2);

        // enables left alignment
        this.normalizer.enableLeftAlign(trickyReference.toString());
        this.normalizer.setGenerateReferenceBlocks(false);
        testSampleNormalization("5", 8, "C", "GCTCC", 4, 3, "", "GCTC");
    }

}
