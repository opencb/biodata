package org.opencb.biodata.tools.commons;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jtarraga on 02/12/16.
 */
public class ChunkFrequencyManagerTest {

//    String filename = "NA12877_chr1";
    String filename = "NA12877_chrM";
    Path bamPath = Paths.get("/home/jtarraga/data150/bam/" + filename + ".bam");
    Path coveragePath = Paths.get("/home/jtarraga/data150/bam/" + filename + ".bam.coverage.wig");
    Path databasePath = Paths.get("/home/jtarraga/data150/bam/" + filename + ".bam.db");

    int chunkSize = 1000;
    int windowSize = 100;

    @Test
    public void createCoverageFromWigFile() {
        try {
//            coveragePath.toFile().delete();
//            databasePath.toFile().delete();
//
//            ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(databasePath, chunkSize);
//
//            SAMFileHeader samHeader = null;
//            samHeader = BamUtils.getFileHeader(bamPath);
//            List<String> chromosomeNames = new ArrayList<>();
//            List<Integer> chromosomeLengths = new ArrayList<>();
//            samHeader.getSequenceDictionary().getSequences().forEach(
//                    seq -> {
//                        chromosomeNames.add(seq.getSequenceName());
//                        chromosomeLengths.add(seq.getSequenceLength());
//                    });
//            chunkFrequencyManager.init(chromosomeNames, chromosomeLengths);
//            BamUtils.createCoverageWigFile(bamPath, coveragePath, windowSize);
//            WigUtils.index(coveragePath, bamPath, chunkFrequencyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryCoverage() {
//        if (!databasePath.toFile().exists()) {
//            createCoverageFromWigFile();
//        }
//
//        ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(databasePath, chunkSize);
//        Region region = new Region("chrM", 1, 5000);
//        //Region region = new Region("chr1", 2000000, 2100000);
//        ChunkFrequencyManager.ChunkFrequency res = chunkFrequencyManager.query(region, bamPath, 1000);
//        for (int val: res.getValues()) {
//            System.out.println(val);
//        }
    }
}