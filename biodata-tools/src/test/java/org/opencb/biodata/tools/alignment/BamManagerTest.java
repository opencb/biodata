package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.SAMRecord;
import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 26/10/16.
 */
public class BamManagerTest {

    @Test
    public void testQuery() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        BamManager BamManager = new BamManager(inputPath);
        AlignmentOptions options = new AlignmentOptions().setLimit(5);
        Region region = new Region("20", 60000, 65000);
        List<SAMRecord> query = BamManager.query(region, options);
        assertEquals(5, query.size());

        options.setLimit(3);
        query = BamManager.query(region, options);
        assertEquals(3, query.size());
    }

    @Test
    public void testCoverage() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        System.out.println("inputPath = " + inputPath);
        BamManager BamManager = new BamManager(inputPath);

        AlignmentOptions options = new AlignmentOptions();
        options.setContained(false);
        Region region = new Region("20", 62000, 62200);
        RegionCoverage coverage = BamManager.coverage(region, options, null);
//        System.out.println(coverage.toString());
        System.out.println(coverage.toJSON());
        System.out.println("mean coverage = " + coverage.meanCoverage());
    }

}
