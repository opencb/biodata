package org.opencb.biodata.tools.commons;

import org.junit.Test;
import org.opencb.biodata.tools.feature.WigUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jtarraga on 02/12/16.
 */
public class ChunkFrequencyManagerTest {

    String filename = "NA12877_chr1";
//    String filename = "NA12877_chrM";
    Path bamPath = Paths.get("/home/jtarraga/data150/bam/" + filename + ".bam");
    Path coverageWigPath = Paths.get("/home/jtarraga/data150/bam/" + filename + ".bam.coverage.wig");
    Path indexPath = Paths.get("/home/jtarraga/data150/bam/" + WigUtils.WIG_DB);

    int chunkSize = 1000;
    int windowSize = 100;

    @Test
    public void createCoverageFromWigFile() {
//        try {
//            //coverageWigPath.toFile().delete();
//            indexPath.toFile().delete();
//
//            // create coverage wig file
//            if (!coverageWigPath.toFile().exists()) {
//                BamUtils.createCoverageWigFile(bamPath, coverageWigPath, windowSize);
//            }
//
//            // index
//            Path indexPath = WigUtils.index(coverageWigPath);
//            System.out.println("Database in " + indexPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void query() {
//        try {
//            if (!indexPath.toFile().exists()) {
//                createCoverageFromWigFile();
//            }
//
//            // now we can query
//            ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(indexPath);
////            Region region = new Region("chrM", 1, 5000);
//            Region region = new Region("chr1", 3100000, 3200000);
//            ChunkFrequencyManager.ChunkFrequency res = chunkFrequencyManager.query(region, coverageWigPath, 1000);
//            for (int val: res.getValues()) {
//                System.out.println(val);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}