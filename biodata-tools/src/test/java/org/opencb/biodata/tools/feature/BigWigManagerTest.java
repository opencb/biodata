package org.opencb.biodata.tools.feature;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceRecord;
import org.broad.igv.bbfile.BigWigIterator;
import org.junit.Test;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.BamUtils;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManagerTest {

    public void query(Path inputPath, String chrom, int start, int end, boolean display) throws Exception {
        BigWigManager bigWigManager = new BigWigManager(inputPath);
        Region region = new Region(chrom, start, end);
        float[] coverage = bigWigManager.query(region);

        if (display) {
            for (float v : coverage) {
                System.out.println((start++) + " :" + v);
            }
        }

        assertEquals(region.getEnd() - region.getStart() + 1, coverage.length);
    }

    @Test
    public void query1() throws Exception {
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());
        query(bwPath, "chr21", 9411190, 9411290, true);
    }

    //@Test
    public void query2() throws Exception {
        Path bwPath = Paths.get("/tmp/test/HG00096.chrom20.small.bam.sort.bam.coverage.bw");
        query(bwPath, "20", 60000, 60200, true);
    }

    @Test
    public void query3() throws Exception {
        Path bwPath = Paths.get("/home/jtarraga/test/HG00096.mapped.illumina.exome.bam.1.sort.bam.coverage.bw");
        query(bwPath, "18", 10000000, 100002000, false);
    }


    @Test
    public void groupBy() throws Exception {
//        Path bwPath = Paths.get("/home/jtarraga/test/HG00096.mapped.illumina.exome.bam.1.sort.bam.coverage.bw");
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());

        String chrom = "20";
        int start = 100;
        int end =   10000000;
        int chunkSize = 1000;

        BigWigManager bigWigManager = new BigWigManager(bwPath);
        Region region = new Region(chrom, start, end);
        float[] coverage = bigWigManager.groupBy(region, chunkSize);

        for (int i = 0; i < coverage.length ; i++) {
            System.out.println(i + ": " + coverage[i]);
        }
//        assertEquals(region.getEnd() - region.getStart() + 1, coverage.length);
    }

    @Test
    public void index() throws Exception {
        // this reads a file from src/test/resources folder
        Path bamPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());
        Path dbPath = Paths.get(bamPath + ".db");

//        Path bamPath = Paths.get("/home/jtarraga/data150/bam/NA12877_chr1.bam");
//        Path bwPath = Paths.get("/home/jtarraga/data150/bam/wgEncodeBroadHistoneH1hescH4k20me1StdSig.bigWig");
//        Path dbPath = Paths.get(bwPath + ".db");

        dbPath.toFile().delete();


        // now, we can index
        BigWigManager bigWigManager = new BigWigManager(bwPath);
        dbPath = bigWigManager.index();

//        Region region = new Region("chr21", 10000000 - 1000, 10001000 - 1000);
//        float[] values = bigWigManager.query(region);
//        int total = 0;
//        for (float v : values) {
//            System.out.println(v);
//            total += v;
//        }
//        System.out.println("**** mean = " + (total / 1000));
//

        // initialize chunkFrequencyManager and DB to query
        int chunkSize = 1000;
        ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(dbPath, chunkSize);
        Region region = new Region("chr21", 10000000 - 1000, 10001000 - 1000);
        ChunkFrequencyManager.ChunkFrequency res = chunkFrequencyManager.query(region, bwPath, 1000);
        for (short i : res.getValues()) {
            System.out.println("---> " + i);
        }

        //        // this reads a file from src/test/resources folder
//        Path inputPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());
//
//        BigWigManager bigWigManager = new BigWigManager(inputPath);
//        List<Float> chr21 = bigWigManager.query(new Region("chr21", 9411190, 9411291));
//        bigWigManager.close();
//
//        assertEquals(20, chr21.size());
    }

}