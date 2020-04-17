package org.opencb.biodata.tools.sequence.fasta;

import htsjdk.samtools.SAMException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.fail;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.tools.sequence.SamtoolsFastaIndex;
import org.opencb.biodata.tools.sequence.SequenceAdaptor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by imedina on 21/10/16.
 */
public class SamtoolsFastaIndexTest {

    private static Path rootDir;
    private static Path fastaFile;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws Exception {
        rootDir = Paths.get("target/test-data", "junit-" + RandomStringUtils.randomAlphabetic(5));
        Files.createDirectories(rootDir);
//        fastaFile = rootDir.resolve("homo_sapiens_grch38_small.fa.gz");
//        Files.copy(SamtoolsFastaIndexTest.class.getResourceAsStream("/homo_sapiens_grch38_small.fa.gz"), fastaFile);
        fastaFile = rootDir.resolve("homo_sapiens_grch38_small.fa");
        Files.copy(SamtoolsFastaIndexTest.class.getResourceAsStream("/homo_sapiens_grch38_small.fa"), fastaFile);
    }

    @Test
    public void testIndex() throws Exception {
        SamtoolsFastaIndex samtoolsFastaIndex = new SamtoolsFastaIndex();
        samtoolsFastaIndex.index(fastaFile, true);

        File file = new File(fastaFile.toAbsolutePath() + ".fai");
        if (!file.exists()) {
            fail(".fai file does not exist!");
        }

        samtoolsFastaIndex = new SamtoolsFastaIndex(fastaFile);
        long l = System.currentTimeMillis();
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        System.out.println(samtoolsFastaIndex.query("21", 10001, 10011));
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
    }

    @Test
    public void index() throws Exception {
        SamtoolsFastaIndex samtoolsFastaIndex = new SamtoolsFastaIndex();
        samtoolsFastaIndex.index(fastaFile);

//        File file = new File(fastaFile.toAbsolutePath() + ".fai");
//        if (!file.exists()) {
//            fail(".fai file does not exist!");
//        }

//        samtoolsFastaIndex = new SamtoolsFastaIndex(fastaFile.toString());
//        long l = System.currentTimeMillis();
//        System.out.println(samtoolsFastaIndex.query("21", 1000000, 1000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
//        System.out.println(samtoolsFastaIndex.query("21", 10000100, 10000200));
//        long l1 = System.currentTimeMillis();
//        System.out.println(l1 - l);

        assertEquals("", "", "");
    }


    @Test
    public void testGenomicSequenceChromosomeNotPresent() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());
        thrown.expect(SAMException.class);
        thrown.expectMessage("Unable to find entry for contig: 1234");
        referenceGenomeReader.query("1234", 1, 1999);

    }

    @Test
    public void testGenomicSequenceQueryStartEndOutOfRightBound() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());

        // Both start & end out of the right bound
        thrown.expect(SAMException.class);
        thrown.expectMessage("Query asks for data past end of contig");
        referenceGenomeReader.query("1", 600000, 700000);
    }

    @Test
    public void testGenomicSequenceQueryEndOutOfRightBound() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());
        // start within the bounds, end out of the right bound.
        thrown.expect(SAMException.class);
        thrown.expectMessage("Query asks for data past end of contig");
        referenceGenomeReader.query("1", 50000, 700000);

    }

    @Test
    public void testGenomicSequenceQueryStartOutOfLeftBound() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());
        // start within the bounds, end out of the right bound. Should return last 10 nts.
        thrown.expect(SAMException.class);
        thrown.expectMessage("Query asks for data past end of contig");
        referenceGenomeReader.query("1", -1, 700000);

    }

}