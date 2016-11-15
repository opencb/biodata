package org.opencb.biodata.tools.alignment.filters;

import htsjdk.samtools.SAMRecord;
import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.BamManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by joaquin on 11/14/16.
 */
public class SamRecordFiltersTest  {

    @Test
    public void testOR() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        BamManager BamManager = new BamManager(inputPath);

        AlignmentOptions options = new AlignmentOptions().setContained(false);
        SamRecordFilters alignmentFilters = new SamRecordFilters();
        Region region = new Region("20", 60000, 65000);

        SamRecordFilters tmpFilters = new SamRecordFilters();
        tmpFilters.addMappingQualityFilter(60);
        tmpFilters.addMappingQualityFilter(35);

        alignmentFilters.addFilterList(tmpFilters.getFilters(), true);

        List<ReadAlignment> results = BamManager.query(region, options, alignmentFilters);
        System.out.println("Number of results: " + results.size());
        assertEquals(108, results.size());
   }

    @Test
    public void testAND() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        BamManager BamManager = new BamManager(inputPath);

        AlignmentOptions options = new AlignmentOptions().setContained(false);
        SamRecordFilters alignmentFilters = new SamRecordFilters();
        Region region = new Region("20", 60000, 65000);

        SamRecordFilters tmpFilters = new SamRecordFilters();
        tmpFilters.addMappingQualityFilter(60);
        tmpFilters.addMappingQualityFilter(35);

        alignmentFilters.addFilterList(tmpFilters.getFilters(), false);

        List<ReadAlignment> results = BamManager.query(region, options, alignmentFilters);
        System.out.println("Number of results: " + results.size());
        assertEquals(96, results.size());
    }
}