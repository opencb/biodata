package org.opencb.biodata.tools.alignment.iterators;

import htsjdk.samtools.SAMRecord;
import org.junit.Test;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.AlignmentManager;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.filtering.AlignmentFilters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by pfurio on 25/10/16.
 */
public class ProtoIteratorTest {

    @Test
    public void testIterator() throws URISyntaxException, IOException {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        AlignmentManager alignmentManager = new AlignmentManager(inputPath);

        AlignmentFilters alignmentFilters = new AlignmentFilters()
                .addMappingQualityFilter(50)
                .addFilter(samRecord -> samRecord.getInferredInsertSize() > 200 && samRecord.getInferredInsertSize() < 300);
        Region region = new Region("20", 60000, 65000);
        AlignmentIterator<SAMRecord> iterator = alignmentManager.iterator(region, new AlignmentOptions(), alignmentFilters);
        while (iterator.hasNext()) {
            SAMRecord next = iterator.next();
            System.out.println(next.getSAMString());
        }
    }

}