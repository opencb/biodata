package org.opencb.biodata.tools.alignment.iterators;

import org.ga4gh.models.ReadAlignment;
import org.junit.Test;
import org.opencb.biodata.tools.alignment.AlignmentManager;
import org.opencb.biodata.tools.alignment.AlignmentOptions;
import org.opencb.biodata.tools.alignment.filtering.AlignmentFilter;

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

        AlignmentFilter alignmentFilter = new AlignmentFilter()
                .addMappingQualityFilter(50)
                .addFilter(samRecord -> samRecord.getInferredInsertSize() > 200 && samRecord.getInferredInsertSize() < 300);
        AlignmentIterator<ReadAlignment> iterator = alignmentManager.iterator("20", 60000, 65000, new AlignmentOptions(), alignmentFilter);
        while (iterator.hasNext()) {
            ReadAlignment next = iterator.next();
            System.out.println(next.toString());
        }
    }

}