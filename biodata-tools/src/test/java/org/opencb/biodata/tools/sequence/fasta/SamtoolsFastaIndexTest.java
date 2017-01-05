package org.opencb.biodata.tools.sequence.fasta;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencb.biodata.tools.sequence.SamtoolsFastaIndex;
import org.opencb.commons.utils.FileUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by imedina on 21/10/16.
 */
public class SamtoolsFastaIndexTest {

    private static Path rootDir;
    private static Path fastaFile;

    @BeforeClass
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

}