package org.opencb.biodata.tools.variant.normalizer.extensions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class VafVariantNormalizerExtensionTest {

    private VariantFileHeader cavemanHeader;
    private VariantFileHeader pindelHeader;
    private VariantFileHeader generalHeader;
    private List<String> singleSample;
    private List<String> multiSample;

    @BeforeEach
    public void setUp() throws Exception {
        singleSample = Collections.singletonList("SAMPLE_1");
        multiSample = Arrays.asList("SAMPLE_1", "SAMPLE_2", "SAMPLE_3");
        cavemanHeader = new VariantFileHeader("", new ArrayList<>(Arrays.asList(
                new VariantFileHeaderComplexLine("INFO", "ASMD", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("INFO", "CLPM", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "PM", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "FAZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "FCZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "FGZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "FTZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "RAZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "RCZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "RGZ", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "RTZ", "", "", "", Collections.emptyMap())
        )), Collections.emptyList());
        pindelHeader = new VariantFileHeader("", new ArrayList<>(Arrays.asList(
                new VariantFileHeaderComplexLine("INFO", "PC", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("INFO", "VT", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "PU", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "NU", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "PR", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "NR", "", "", "", Collections.emptyMap())
        )), Collections.emptyList());
        generalHeader = new VariantFileHeader("", new ArrayList<>(Arrays.asList(
                new VariantFileHeaderComplexLine("INFO", "DP", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "DP", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "AD", "", "", "", Collections.emptyMap())
        )), Collections.emptyList());
    }

    @Test
    public void testCanUse() {
        VariantFileHeader header = new VariantFileHeader("", new ArrayList<>(Arrays.asList(
                new VariantFileHeaderComplexLine("INFO", "DP", "", "", "", Collections.emptyMap()),
                new VariantFileHeaderComplexLine("FORMAT", "DP", "", "", "", Collections.emptyMap())
        )), Collections.emptyList());

        VafVariantNormalizerExtension extension = buildExtension(header, singleSample);
        assertFalse(extension.canUseExtension());
    }

    @Test
    public void testGeneralNothingToDo() throws Exception {
        Variant variant = Variant.newBuilder("1:10:A:C")
                .setFileId("file")
                .setSampleDataKeys("GT", "DP")
                .addSample("SAMPLE_1", "1/1", "10")
                .build();

        normalize(generalHeader, singleSample, variant);
        assertEquals(Arrays.asList("GT", "DP", "EXT_VAF"), variant.getStudies().get(0).getSampleDataKeys());
        assertEquals(".", variant.getStudies().get(0).getSampleData("SAMPLE_1", "EXT_VAF"));
    }

    @Test
    public void testGeneral() throws Exception {
        Variant variant = Variant.newBuilder("1:10:A:C")
                .setFileId("file")
                .setSampleDataKeys("GT", "AD")
                .addSample("SAMPLE_1", "0/1", "5,5")
                .addFileData("DP", "10")
                .build();

        normalize(generalHeader, singleSample, variant);
        assertEquals("GT:AD:EXT_VAF", variant.getStudies().get(0).getSampleDataKeysAsString());
        assertEquals("0.5", variant.getStudies().get(0).getSampleData("SAMPLE_1", "EXT_VAF"));
    }

    @Test
    public void testGeneralMultiSample() throws Exception {
        Variant variant = Variant.newBuilder("1:10:A:C")
                .setFileId("file")
                .addFileData("DP", "30")
                .setSampleDataKeys("GT", "AD", "DP")
                .addSample("SAMPLE_1", "0/1", "5,5", "10")
                .addSample("SAMPLE_2", "0/0", null, null)
                .addSample("SAMPLE_3", "1/1", "2,18", "20")
                .build();

        normalize(generalHeader, multiSample, variant);
        assertEquals("GT:AD:DP:EXT_VAF", variant.getStudies().get(0).getSampleDataKeysAsString());
        assertEquals("0.5", variant.getStudies().get(0).getSampleData("SAMPLE_1", "EXT_VAF"));
        assertEquals(".", variant.getStudies().get(0).getSampleData("SAMPLE_2", "EXT_VAF"));
        assertEquals("0.9", variant.getStudies().get(0).getSampleData("SAMPLE_3", "EXT_VAF"));
    }

    private VafVariantNormalizerExtension buildExtension(VariantFileHeader header, List<String> samples) {
        VariantFileMetadata fileMetadata = new VariantFileMetadata("file", "path", samples, null, header);
        VafVariantNormalizerExtension extension = new VafVariantNormalizerExtension();
        extension.init(fileMetadata);
        return extension;
    }

    private void normalize(VariantFileHeader header, List<String> samples, Variant variant)
            throws Exception {
        VafVariantNormalizerExtension extension = buildExtension(header, samples);
        assertTrue(extension.canUseExtension());
        extension.pre();
        extension.apply(Collections.singletonList(variant));
    }

}