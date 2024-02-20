package org.opencb.biodata.tools.sequence.fasta;

import htsjdk.samtools.SAMException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.tools.sequence.SamtoolsFastaIndex;
import org.opencb.biodata.tools.sequence.SequenceAdaptor;

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

    @BeforeAll
    public static void setUp() throws Exception {
        rootDir = Paths.get("target/test-data", "junit-" + RandomStringUtils.randomAlphabetic(5));
        Files.createDirectories(rootDir);
        fastaFile = rootDir.resolve("homo_sapiens_grch37_small.fa.gz");
        Files.copy(SamtoolsFastaIndexTest.class.getResourceAsStream("/homo_sapiens_grch37_small.fa.gz"), fastaFile);
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
        SAMException thrown = Assertions.assertThrows(SAMException.class, () -> {
            referenceGenomeReader.query("1234", 1, 1999);
        });

        Assertions.assertEquals("Unable to find entry for contig: 1234", thrown.getMessage());
    }

    @Test
    public void testGenomicSequenceQueryStartEndOutOfRightBound() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());

        // Both start & end out of the right bound
        SAMException thrown = Assertions.assertThrows(SAMException.class, () -> {
            referenceGenomeReader.query("1", 600000, 700000);
        });

        Assertions.assertEquals("Query asks for data past end of contig", thrown.getMessage());
    }

    @Test
    public void testGenomicSequenceQueryEndOutOfRightBound() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());
        // start within the bounds, end out of the right bound.

        SAMException thrown = Assertions.assertThrows(SAMException.class, () -> {
            referenceGenomeReader.query("1", 50000, 700000);
        });

        Assertions.assertEquals("Query asks for data past end of contig", thrown.getMessage());
    }

    @Test
    public void testGenomicSequenceQueryStartOutOfLeftBound() throws Exception {
        Path referenceGenome = Paths.get(
                getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI()
        );

        SequenceAdaptor referenceGenomeReader = new SamtoolsFastaIndex(referenceGenome.toString());
        // start within the bounds, end out of the right bound. Should return last 10 nts.

        SAMException thrown = Assertions.assertThrows(SAMException.class, () -> {
            referenceGenomeReader.query("1", -1, 700000);
        });

        Assertions.assertEquals("Query asks for data past end of contig", thrown.getMessage());

    }

}