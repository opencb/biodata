package org.opencb.biodata.tools.variant.converter;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfRecordToVariantConverterTest {

    private VcfRecordToVariantConverter converter;
    private VcfSliceProtos.VcfMeta meta;

    @Before
    public void setUp() throws Exception {
        meta = VcfSliceProtos.VcfMeta.newBuilder()
                .setFileId("chr1")
                .setStudyId("study1")
                .addFormatDefault("GT")
                .addFormatDefault("DP")
                .setFilterDefault("PASS")
                .setIdDefault(".")
                .addSamples("sample1")
                .addSamples("sample2")
                .addSamples("sample3")
                .addSamples("sample4")
                .addInfoDefault("Key")
                .build();
        converter = new VcfRecordToVariantConverter(meta);

    }

    @Test
    public void testConvert() throws Exception {

        VariantToProtoVcfRecord toProto = new VariantToProtoVcfRecord();
        toProto.updateVcfMeta(meta);

        Variant variant = new Variant("1", 5000, "A", "C");
        StudyEntry studyEntry = new StudyEntry();
        studyEntry.setFormat(Collections.singletonList("GT"));
        studyEntry.setStudyId("study1");
        studyEntry.setSamplesData(Arrays.asList(
                Arrays.asList("0|0"),
                Arrays.asList("0|1"),
                Arrays.asList("1|0"),
                Arrays.asList("1|1")));
        Map<String, String> attributes = new HashMap<>();
        attributes.put(VariantVcfFactory.FILTER, "nopass");
        attributes.put(VariantVcfFactory.QUAL, "50");
        attributes.put("Key1", "V1");
        attributes.put("Key2", "V2");

//        studyEntry.setFiles(Collections.singletonList(new FileEntry("chr1", "5:A:C:0", attributes)));
        studyEntry.setFiles(Collections.singletonList(new FileEntry("chr1", null, attributes)));
        variant.setStudies(Collections.singletonList(studyEntry));

        VcfSliceProtos.VcfRecord vcfRecord = toProto.convert(variant);
        assertNotEquals(0, vcfRecord.getSampleFormatNonDefaultCount());
        assertNotEquals("", vcfRecord.getFilterNonDefault());
        assertEquals(2, vcfRecord.getInfoKeyCount());
        Variant convertedVariant = converter.convert(vcfRecord, "1", 0);

        assertEquals(variant, convertedVariant);
    }

    @Test
    public void testConvertDefaultValues() throws Exception {

        VariantToProtoVcfRecord toProto = new VariantToProtoVcfRecord();
        toProto.updateVcfMeta(meta);

        Variant variant = new Variant("1", 5, "A", "C");
        StudyEntry studyEntry = new StudyEntry();
        studyEntry.setFormat(Arrays.asList("GT", "DP"));
        studyEntry.setStudyId("study1");
        studyEntry.setSamplesData(Arrays.asList(
                Arrays.asList("0|0", "10"),
                Arrays.asList("0|1", "20"),
                Arrays.asList("1|0", "30"),
                Arrays.asList("1|1", "40")));
        Map<String, String> attributes = new HashMap<>();
        attributes.put(VariantVcfFactory.FILTER, "PASS");
        attributes.put(VariantVcfFactory.QUAL, "57");
        attributes.put("Key", "Value");

//        studyEntry.setFiles(Collections.singletonList(new FileEntry("chr1", "5:A:C:0", attributes)));
        studyEntry.setFiles(Collections.singletonList(new FileEntry("chr1", null, attributes)));
        variant.setStudies(Collections.singletonList(studyEntry));

        VcfSliceProtos.VcfRecord vcfRecord = toProto.convert(variant);
        assertEquals(0, vcfRecord.getSampleFormatNonDefaultCount());
        assertEquals("", vcfRecord.getFilterNonDefault());
        assertEquals(0, vcfRecord.getInfoKeyCount());
        Variant convertedVariant = converter.convert(vcfRecord, "1", 0);

        assertEquals(variant, convertedVariant);
    }

}