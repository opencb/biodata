package org.opencb.biodata.tools.alignment;

import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.models.core.Region;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentManagerTest {

    @Test
    public void testQuery() throws Exception {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        AlignmentManager alignmentManager = new AlignmentManager(inputPath);
        AlignmentOptions options = new AlignmentOptions().setLimit(5);
        Region region = new Region("20", 60000, 65000);
        List<ReadAlignment> query = alignmentManager.query(region, options);
        assertEquals(5, query.size());

        options.setLimit(3);
        query = alignmentManager.query(region, options);
        assertEquals(3, query.size());
    }

}
