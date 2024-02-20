package org.opencb.biodata.tools.commons;

import org.junit.jupiter.api.BeforeEach;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.BamUtils;
import org.opencb.biodata.tools.feature.WigUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jtarraga on 02/12/16.
 */
public class ChunkFrequencyManagerTest {
    Path bamPath;
    Path coverageWigPath;
    Path indexPath;

    int chunkSize = 1000;
    int windowSize = 100;

    @BeforeEach
    public void init() throws Exception {
        bamPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        coverageWigPath = Paths.get("/tmp/" + bamPath.toFile().getName() + ".bam.coverage.wig");
        indexPath = Paths.get("/tmp/" + bamPath.toFile().getName() + WigUtils.WIG_DB);
    }


    //@Test
    public void createCoverageFromWigFile() {
        try {
            coverageWigPath.toFile().delete();
            indexPath.toFile().delete();

            // create coverage wig file
            if (!coverageWigPath.toFile().exists()) {
                BamUtils.createCoverageWigFile(bamPath, coverageWigPath, windowSize);
            }

            // index
            Path indexPath = WigUtils.index(coverageWigPath);
            System.out.println("Database in " + indexPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void query() {
        try {
            if (!indexPath.toFile().exists()) {
                createCoverageFromWigFile();
            }

            // now we can query
            ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(indexPath);
//            Region region = new Region("chrM", 1, 5000);
            Region region = new Region("chr1", 3100000, 3200000);
            ChunkFrequencyManager.ChunkFrequency res = chunkFrequencyManager.query(region, coverageWigPath, 1000);
            for (int val: res.getValues()) {
                System.out.println(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}