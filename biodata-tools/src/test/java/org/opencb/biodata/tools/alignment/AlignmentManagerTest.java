package org.opencb.biodata.tools.alignment;

import org.ga4gh.models.ReadAlignment;
import org.junit.Test;

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
        AlignmentOptions options = new AlignmentOptions().setMaxNumberRecords(5);
        List<ReadAlignment> query = alignmentManager.query("20", 60000, 65000, options);
        assertEquals(5, query.size());

        options.setMaxNumberRecords(3);
        query = alignmentManager.query("20", 60000, 65000, options);
        assertEquals(3, query.size());
    }

}
