package org.opencb.biodata.tools.variant.metadata;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.tools.variant.VariantVcfHtsjdkReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

public class VariantMetadataUtilsTest {

    @Test
    public void mapSamples() throws IOException {
        VariantFileMetadata variantFileMetadata;
        try (InputStream is = getClass().getResourceAsStream("/sampleMappingName.vcf")) {
            variantFileMetadata = VariantMetadataUtils
                    .readVariantFileMetadata(is, new VariantFileMetadata("1", ""));
            assertEquals(Arrays.asList("sample_tumor", "sample_normal", "sample_other"), variantFileMetadata.getSampleIds());
        }
        try (InputStream is = getClass().getResourceAsStream("/sampleMappingName.vcf")) {
            for (Variant variant : new VariantVcfHtsjdkReader(is, variantFileMetadata.toVariantStudyMetadata("s"))) {
                assertEquals("TUMOR", variant.getStudies().get(0).getSamples().get(0).getData().get(1));
                assertEquals("NORMAL", variant.getStudies().get(0).getSamples().get(1).getData().get(1));
                assertEquals("OTHER", variant.getStudies().get(0).getSamples().get(2).getData().get(1));
                assertEquals(Arrays.asList("sample_tumor", "sample_normal", "sample_other"), variant.getStudies().get(0).getOrderedSamplesName());
            }
        }
    }
}