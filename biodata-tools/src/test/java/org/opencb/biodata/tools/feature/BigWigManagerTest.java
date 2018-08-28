package org.opencb.biodata.tools.feature;

import org.broad.igv.bbfile.*;
import org.junit.Test;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void groupBy() throws Exception {
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());

        String chrom = "chr21";
        int start = 9480000;
        int end =   9500000;
        int windowSize = 10000;

        BigWigManager bigWigManager = new BigWigManager(bwPath);
        Region region = new Region(chrom, start, end);
        float[] coverage = bigWigManager.groupBy(region, windowSize);

        for (int i = 0; i < coverage.length ; i++) {
            System.out.println(i + ": " + coverage[i]);
        }
//        assertEquals(region.getEnd() - region.getStart() + 1, coverage.length);
    }

    @Deprecated
    public void index() throws Exception {
        // this reads a file from src/test/resources folder
        Path bamPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());
        Path dbPath = Paths.get(bamPath + ".db");

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

    @Test
    public void zoom() throws Exception {
//        Path bwPath = Paths.get("~/data150/coverage.bw");
//        Path bwPath = Paths.get("~/data150/bw/HG00096.chrom20.small.bam.bw");
//        Path bwPath = Paths.get("~/data150/bw/HG00096.mapped.illumina.exome.bam.1.sort.bam.coverage.bs100.bw");
        Path bwPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());

        BBFileReader bbFileReader = new BBFileReader(bwPath.toString());
        System.out.println("zoom level count = " + bbFileReader.getZoomLevelCount());

        BBZoomLevels zoomLevels = bbFileReader.getZoomLevels();
        System.out.println("zoom headers:");
        zoomLevels.printZoomHeaders();

        for (int zoomLevel = 1; zoomLevel <= bbFileReader.getZoomLevelCount(); zoomLevel++) {
            System.out.println("Zoom level " + zoomLevel);
            System.out.println(bbFileReader.getZoomLevels().getZoomLevelHeader(zoomLevel).getReductionLevel());
        }

//        long chromosomeNameCount = bbFileReader.getChromosomeNameCount();
//        for (int chrom = 0; chrom < chromosomeNameCount; chrom++) {
//            System.out.println(chrom + " > " + bbFileReader.getChromosomeName(chrom));
//        }

        System.out.println("------------------------------------------");
        String info = "";
        for (int zoomLevel = 1; zoomLevel <= bbFileReader.getZoomLevelCount(); zoomLevel++) {
            System.out.println("Zoom level " + zoomLevel);
            ZoomLevelIterator zoomLevelIterator = bbFileReader.getZoomLevelIterator(zoomLevel,
                    "1", 0, "1", 900000000, false);
            long count = 0;
            while (zoomLevelIterator.hasNext()) {
                ++count;
                ZoomDataRecord next = zoomLevelIterator.next();
                info = "Chunk " + count + " > " + next.getChromName() + ":"
                        + next.getChromStart() + "-" + next.getChromEnd()
                        + ", mean value = " + next.getMeanVal()
                        + ", min. value = " + next.getMinVal()
                        + ", max. value = " + next.getMaxVal();


                if (count < 3) {
                    System.out.println("\t" + info);
                } else if (count == 3) {
                    System.out.println("\t...");
                }
//                if (++count > 100) {
//                    break;
//                }
            }
            System.out.println("\t" + info);
            System.out.println("\t\tNum. chunks = " + count + "\n");
        }

//        for (int zoomLevel = 1; zoomLevel <= bbFileReader.getZoomLevelCount(); zoomLevel++) {
//            System.out.println("Zoom level " + zoomLevel);
//            RPChromosomeRegion zoomLevelBounds = bbFileReader.getZoomLevelBounds(zoomLevel);
//            System.out.println("\t" + zoomLevelBounds.getStartBase() + ", " + zoomLevelBounds.getEndBase());
//        }
//        System.out.println("zoom levels = " + zoomLevels);

    }

}