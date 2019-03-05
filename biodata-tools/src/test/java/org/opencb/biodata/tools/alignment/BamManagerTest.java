package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.*;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.exceptions.AlignmentCoverageException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 26/10/16.
 */
public class BamManagerTest {
    Path inputPath;
    Path bamPath;
    Path bwPath;

    @Before
    public void init() throws URISyntaxException, IOException {
        inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        bamPath = Paths.get("/tmp/" + inputPath.toFile().getName());
        bwPath = Paths.get("/tmp/" + inputPath.toFile().getName() + ".bw");
    }

    //@Test
    public void testIndex() throws IOException {
        try {
            Files.copy(inputPath, bamPath);
        } catch (FileAlreadyExistsException e) {
            System.out.println("BAM file " + bamPath + " already copied");
        }
        System.out.println("bamPath = " + bamPath);
        BamManager bamManager = new BamManager(bamPath);
        bamManager.createIndex();
    }

    //@Test
    public void testIndexBigWigCoverage() throws Exception {
        try {
            Files.copy(inputPath, bamPath);
        } catch (FileAlreadyExistsException e) {
            System.out.println("BAM file " + bamPath + " already copied");
        }

        System.out.println("bamPath = " + bamPath);
        System.out.println("bwPath = " + bwPath);
        BamManager bamManager = new BamManager(bamPath);
        bamManager.createIndex();
        bamManager.calculateBigWigCoverage(bwPath, 50);
    }

    @Test
    public void testQuery() throws Exception {
        BamManager bamManager = new BamManager(inputPath);
        AlignmentOptions options = new AlignmentOptions().setLimit(5);
        Region region = new Region("20", 60000, 65000);
        List<SAMRecord> query = bamManager.query(region, options);
        assertEquals(5, query.size());

        options.setLimit(3);
        query = bamManager.query(region, options);
        for (SAMRecord sam: query) {
            System.out.println(sam.toString());
        }
        assertEquals(3, query.size());
    }

    //@Test
    public void testQueryBigWigCoverage() throws Exception {
        if (!bwPath.toFile().exists()) {
            testIndexBigWigCoverage();
        }

        BamManager bamManager = new BamManager(bamPath);

        Region region = new Region("20", 62000, 62200);
        RegionCoverage coverage = bamManager.coverage(region, 20);
//        System.out.println(coverage.toString());
        System.out.println(coverage.toJSON());
        System.out.println("mean coverage = " + coverage.meanCoverage());
    }

    @Test
    public void testQueryBAMCoverage() throws Exception {
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
        System.out.println("inputPath = " + inputPath);
        BamManager bamManager = new BamManager(inputPath);

        PrintWriter writer = new PrintWriter(new File(inputPath + ".coverage"));

//        short[] values;
        int chunkSize = 100000;

        SamReader sfr = SamReaderFactory.makeDefault().open(inputPath.toFile());
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

    @Test
    public void uncoveredRegions() throws IOException, AlignmentCoverageException {
        System.out.println("inputPath = " + inputPath);
        BamManager bamManager = new BamManager(inputPath);

        AlignmentOptions options = new AlignmentOptions();
        options.setContained(false);
        Region region = new Region("20", 62000, 62200);

        int maxCoverage = 3;

        List<RegionCoverage> uncoveredRegions = bamManager.getUncoveredRegions(region, maxCoverage);
        for (RegionCoverage uncoveredRegion : uncoveredRegions) {
            System.out.println(uncoveredRegion);
        }
    }

    public void testRanges() throws IOException {
        int refID = 1; // 1; // 22; //14;
        int beg = 114000; // 13000; // 10000; //24375199;
        int end = 200000; //13986; // 10100; //24378544;


        bamPath = Paths.get("/mnt/data/hgva/datasets/bams/NA12877_S1.bam");
        BamManager bamManager = new BamManager(bamPath);
        Region region = new Region("chr" + refID, beg, end);


        Region region1 = new Region(region.getChromosome(), region.getStart(), region.getEnd() + 32000);

        Set<String> breakSet = new HashSet<>(bamManager.getBreakpoints(region));
        Set<String> breakSet1 = new HashSet<>(bamManager.getBreakpoints(region1));

        System.out.println(breakSet.size() + " vs " + breakSet1.size());


        System.out.println("Commons:");
        Iterator<String> iterator = breakSet.iterator();
        while (iterator.hasNext()) {
            String breakpoint = iterator.next();
            if (breakSet1.contains(breakpoint)) {
                System.out.println("\t" + breakpoint);
            }
        }

        System.out.println("Only at region " + region);
        iterator = breakSet.iterator();
        while (iterator.hasNext()) {
            String breakpoint = iterator.next();
            if (!breakSet1.contains(breakpoint)) {
                System.out.println("\t" + breakpoint);
            }
        }

        System.out.println("Only at region " + region1);
        iterator = breakSet1.iterator();
        while (iterator.hasNext()) {
            String breakpoint = iterator.next();
            if (!breakSet.contains(breakpoint)) {
                System.out.println("\t" + breakpoint);
            }
        }

        //        Set<String> chunkSet = new HashSet<>();
//
//        List<Chunk> chunks = bamManager.getChunks(region);
//        for (Chunk chunk : chunks) {
//            chunkSet.add(chunk.toString());
//            System.out.println(chunk);
//        }

//        boolean keepGoing = true;
//        int blockSize = 65000;
//        while (keepGoing) {
//            region.setEnd(region.getEnd() + blockSize);
//            chunks = bamManager.getChunks(region);
//            for (Chunk chunk : chunks) {
//                if (!chunkSet.contains(chunk.toString())) {
//                    System.out.println("not found: " + chunk);
//                    keepGoing = false;
//                    break;
//                }
//            }
//        }

//        System.out.println();
//
//        List<String> breakpoints = bamManager.getBreakpoints(region);
//        for (String breakpoint : breakpoints) {
//            System.out.println(breakpoint);
//        }

    }

//    @Test
    public void testChunks() throws IOException {
        int refID = 1; // 1; // 22; //14;
        int beg = 114000; // 13000; // 10000; //24375199;
        int end = 200000; //13986; // 10100; //24378544;


        bamPath = Paths.get("/mnt/data/hgva/datasets/bams/NA12877_S1.bam");
        BamManager bamManager = new BamManager(bamPath);
        Region region = new Region("chr" + refID, beg, end);


        Region region1 = new Region(region.getChromosome(), region.getStart(), region.getEnd() + 32000);

        Set<Chunk> chunkSet = new HashSet<>(bamManager.getChunks(region));
        Set<Chunk> chunkSet1 = new HashSet<>(bamManager.getChunks(region1));

        System.out.println(chunkSet.size() + " vs " + chunkSet1.size());


        System.out.println("Commons:");
        Iterator<Chunk> iterator = chunkSet.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            if (chunkSet1.contains(chunk)) {
                System.out.println("\t" + chunk);
            }
        }

        System.out.println("Only at region " + region);
        iterator = chunkSet.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            if (!chunkSet1.contains(chunk)) {
                System.out.println("\t" + chunk);
            }
        }

        System.out.println("Only at region " + region1);
        iterator = chunkSet1.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            if (!chunkSet.contains(chunk)) {
                System.out.println("\t" + chunk);
            }
        }

        //        Set<String> chunkSet = new HashSet<>();
//
//        List<Chunk> chunks = bamManager.getChunks(region);
//        for (Chunk chunk : chunks) {
//            chunkSet.add(chunk.toString());
//            System.out.println(chunk);
//        }

//        boolean keepGoing = true;
//        int blockSize = 65000;
//        while (keepGoing) {
//            region.setEnd(region.getEnd() + blockSize);
//            chunks = bamManager.getChunks(region);
//            for (Chunk chunk : chunks) {
//                if (!chunkSet.contains(chunk.toString())) {
//                    System.out.println("not found: " + chunk);
//                    keepGoing = false;
//                    break;
//                }
//            }
//        }

//        System.out.println();
//
//        List<String> breakpoints = bamManager.getBreakpoints(region);
//        for (String breakpoint : breakpoints) {
//            System.out.println(breakpoint);
//        }

    }
}
