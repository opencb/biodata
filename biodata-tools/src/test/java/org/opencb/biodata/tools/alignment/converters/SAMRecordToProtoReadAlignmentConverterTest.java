package org.opencb.biodata.tools.alignment.converters;

import ga4gh.Reads;
import htsjdk.samtools.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToProtoReadAlignmentConverterTest {

    @Test
    public void testConverter() throws URISyntaxException, IOException {
        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        SamReader reader = srf.open(SamInputResource.of(inputPath.toFile()));

        SAMRecordToProtoReadAlignmentConverter converter = new SAMRecordToProtoReadAlignmentConverter(false);

        for (SAMRecord original : reader) {
            // Convert to proto
            Reads.ReadAlignment proto = converter.to(original);
            SAMRecord converted = converter.from(proto);
            assertEquals(original.getSAMString(), converted.getSAMString());
        }
    }

}