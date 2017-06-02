package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.*;
import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 26/10/16.
 */
public class BamManagerTest {

    @Test
    public void testQuery() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        BamManager bamManager = new BamManager(inputPath);
        AlignmentOptions options = new AlignmentOptions().setLimit(5);
        Region region = new Region("20", 60000, 65000);
        List<SAMRecord> query = bamManager.query(region, options);
        assertEquals(5, query.size());

        options.setLimit(3);
        query = bamManager.query(region, options);
        assertEquals(3, query.size());
    }

    @Test
    public void testCoverage() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        System.out.println("inputPath = " + inputPath);
        BamManager bamManager = new BamManager(inputPath);

        AlignmentOptions options = new AlignmentOptions();
        options.setContained(false);
        Region region = new Region("20", 62000, 62200);
        RegionCoverage coverage = bamManager.coverage(region, null, options);
//        System.out.println(coverage.toString());
        System.out.println(coverage.toJSON());
        System.out.println("mean coverage = " + coverage.meanCoverage());
    }

    //@Test
    public void testFullCoverage() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
//        Path inputPath = Paths.get("/home/jtarraga/data150/bam/NA12877_chrM.bam");
//        Path inputPath = Paths.get("/home/jtarraga/swdev-data/downloads/datasets/bam/platinum_genomes/NA12877_S1.bam");
//        Path inputPath = Paths.get("/home/jtarraga/data150/bam/NA12877_chr1.bam");
        System.out.println("inputPath = " + inputPath);
        BamManager bamManager = new BamManager(inputPath);

//        FileOutputStream fos = new FileOutputStream("/home/jtarraga/data150/bam/NA12877_chr1.500k-chunk.coverage.out");
//        FileOutputStream fos = new FileOutputStream("/home/jtarraga/data150/bam/NA12877_chr1.1k-chunk.coverage.out");
//        FileOutputStream fos = new FileOutputStream("/home/jtarraga/data150/bam/NA12877_chrM.single-chunk.coverage.out");
//        FileOutputStream fos = new FileOutputStream("/home/jtarraga/data150/bam/NA12877_chrM.500k-chunk.coverage.out");
//        FileOutputStream fos = new FileOutputStream("/home/jtarraga/data150/bam/NA12877_chr1.coverage.out");
        PrintWriter writer = new PrintWriter(new File(inputPath + ".coverage"));

//        short[] values;
        int chunkSize = 100000;

        SAMFileReader sfr = new SAMFileReader(inputPath.toFile());
        SAMFileHeader h = sfr.getFileHeader();
        SAMSequenceDictionary dict = h.getSequenceDictionary();
        long totalStartTime = System.currentTimeMillis();
        for (SAMSequenceRecord ssr : dict.getSequences()) {
            String ref_name = ssr.getSequenceName();
            int ref_len = ssr.getSequenceLength();

//            if (!"chrM".equals(ref_name)) continue;
//            if (!"chr1".equals(ref_name)) continue;
            if (!"20".equals(ref_name)) continue;

            if (chunkSize == -1) {
                chunkSize = ref_len;
            }

            int pos = 1;
            // start time
            long startTime3 = System.currentTimeMillis();
            while (pos <= ref_len) {
                Region region = new Region(ref_name, pos, Math.min(pos + chunkSize - 1, ref_len));
//                System.out.println(region);
//
//                // start time
//                long startTime1 = System.currentTimeMillis();
                RegionCoverage coverage = bamManager.coverage(region, null, null);
                // stop time
//                long stopTime1 = System.currentTimeMillis();
//                System.out.println("\tComputing coverage: CHR " + ref_name + " in " + (stopTime1 - startTime1)/1000.0 + " seconds.");
//
//                // start time
//                long startTime2 = System.currentTimeMillis();
                BamUtils.printWigFormatCoverage(coverage, 1, coverage.getStart() == 1, writer);
                // stop time
//                long stopTime2 = System.currentTimeMillis();
//                System.out.println("\tWriting coverage: CHR " + ref_name + " in " + (stopTime2 - startTime2)/1000.0 + " seconds.");

                pos += chunkSize;
            }
            long stopTime3 = System.currentTimeMillis();
            System.out.println("\t\tTotal time: CHR " + ref_name + " in " + (stopTime3 - startTime3)/1000.0 + " seconds.");
        }
        long totalStopTime = System.currentTimeMillis();
        System.out.println("\nTotal time: " + (totalStopTime - totalStartTime)/1000.0 + " seconds.");

        writer.close();
    }
}
