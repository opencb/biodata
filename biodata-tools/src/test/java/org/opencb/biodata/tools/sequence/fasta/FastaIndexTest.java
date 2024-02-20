package org.opencb.biodata.tools.sequence.fasta;

import htsjdk.samtools.SAMException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.tools.sequence.FastaIndex;
import org.opencb.biodata.tools.sequence.SamtoolsFastaIndex;
import org.opencb.biodata.tools.sequence.SequenceAdaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by imedina on 21/10/16.
 */
public class FastaIndexTest {


    @Test
    public void testIndex() throws Exception {
        Path rootDir = Paths.get("target/test-data", "junit-" + RandomStringUtils.randomAlphabetic(5));
        Files.createDirectories(rootDir);
        Path fastaFile = rootDir.resolve("homo_sapiens_grch38_small.fa");
        Files.copy(FastaIndexTest.class.getResourceAsStream("/homo_sapiens_grch38_small.fa"), fastaFile);
        runQueries(fastaFile);
    }

    @Test
    public void testIndexBlockCompressed() throws Exception {
        Path rootDir = Paths.get("target/test-data", "junit-" + RandomStringUtils.randomAlphabetic(5));
        Files.createDirectories(rootDir);
        Path destination = rootDir.resolve("homo_sapiens_grch38_small.fa.gz");
        Path source = Paths.get(getClass().getResource("/homo_sapiens_grch38_small.fa.gz").toURI());
        FileUtils.copyFile(source.toFile(), destination.toFile());
        runQueries(destination);
    }

    private void runQueries(Path fastaFile) throws IOException {
        FastaIndex samtoolsFastaIndex = new FastaIndex(fastaFile);

        File file = new File(fastaFile.toAbsolutePath() + ".fai");
        if (!file.exists()) {
            fail(".fai file does not exist!");
        }

        samtoolsFastaIndex = new FastaIndex(fastaFile);
        long l = System.currentTimeMillis();
        System.out.println(samtoolsFastaIndex.query("10", 10001, 10011));
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


        assertEquals("CTAACCCTAAC", samtoolsFastaIndex.query("10", 10001, 10011));
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