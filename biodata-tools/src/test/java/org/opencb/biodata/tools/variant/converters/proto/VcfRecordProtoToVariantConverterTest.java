package org.opencb.biodata.tools.variant.converters.proto;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.*;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfRecordProtoToVariantConverterTest {

    private VcfRecordProtoToVariantConverter converter;
    private VcfSliceProtos.Fields fields;
    private String fileId = "file1";
    private String studyId = "study1";

    @Before
    public void setUp() throws Exception {

        LinkedHashMap<String, Integer> samplePositions = new LinkedHashMap<>();
        samplePositions.put("sample1", 0);
        samplePositions.put("sample2", 1);
        samplePositions.put("sample3", 2);
        samplePositions.put("sample4", 3);

        fields = VcfSliceProtos.Fields.newBuilder()
                .addFormats("GT:DP")
                .addFormats("GT")
                .addFilters("PASS")
                .addFilters("nopass")
                .addInfoKeys("Key")
                .addInfoKeys("Key1")
                .addInfoKeys("Key2")
                .addDefaultInfoKeys(0)
                .addGts("0|0")
                .addGts("0|1")
                .addGts("1|0")
                .addGts("1|1")
                .build();

        converter = new VcfRecordProtoToVariantConverter(fields, samplePositions, fileId, studyId);

    }

    @Test
    public void testConvert() throws Exception {

        VariantToProtoVcfRecord toProto = new VariantToProtoVcfRecord();
        toProto.updateMeta(fields);

        Variant variant = new Variant("1", 5000, "A", "C");
        StudyEntry studyEntry = new StudyEntry();
        studyEntry.setSampleDataKeys(Collections.singletonList("GT"));
        studyEntry.setStudyId(studyId);
        studyEntry.setSamples(Arrays.asList(
                new SampleEntry(null, null, Arrays.asList("0|0")),
                new SampleEntry(null, null, Arrays.asList("0|1")),
                new SampleEntry(null, null, Arrays.asList("1|0")),
                new SampleEntry(null, null, Arrays.asList("1|1"))));
        Map<String, String> fileData = new HashMap<>();
        fileData.put(StudyEntry.FILTER, "nopass");
        fileData.put(StudyEntry.QUAL, "50");
        fileData.put("Key1", "V1");
        fileData.put("Key2", "V2");

        studyEntry.setFiles(Collections.singletonList(new FileEntry(fileId, "5:A:C:0", fileData)));
        variant.setStudies(Collections.singletonList(studyEntry));

        VcfSliceProtos.VcfRecord vcfRecord = toProto.convert(variant);
        assertNotEquals(0, vcfRecord.getFormatIndex());
        assertNotEquals("", vcfRecord.getFilterIndex());
        assertEquals(2, vcfRecord.getInfoKeyIndexCount());
        Variant convertedVariant = converter.convert(vcfRecord, "1", 0);

        assertEquals(variant, convertedVariant);
    }

    @Test
    public void testConvertNoSamples() throws Exception {
        Variant variant = Variant.newBuilder("1", 5000, 5000, "A", "C").setStudyId(studyId)
                .setFilter("nopass")
                .setQuality("50")
                .setSampleDataKeys(Collections.emptyList())
                .setSamples(Collections.emptyList())
                .setFileId(fileId)
                .setCall("5:A:C:0")
                .addFileData("Key1", "V1")
                .addFileData("Key2", "V2")
                .build();
        Variant variantWithFormat = Variant.newBuilder("1", 5000, 5000, "A", "C").setStudyId(studyId)
                .setFilter("nopass")
                .setQuality("50")
                .setSampleDataKeys("GT", "DP")
                .setSamples(Collections.emptyList())
                .setFileId(fileId)
                .setCall("5:A:C:0")
                .addFileData("Key1", "V1")
                .addFileData("Key2", "V2")
                .build();

        VcfSliceProtos.Fields fields = VariantToVcfSliceConverter.buildDefaultFields(Arrays.asList(variant, variantWithFormat), null, null);
        assertEquals("", fields.getFormats(0));
        assertEquals("GT:DP", fields.getFormats(1));

        VariantToProtoVcfRecord toProto = new VariantToProtoVcfRecord();
        toProto.updateMeta(fields);


        VcfSliceProtos.VcfRecord vcfRecord = toProto.convert(variant);
        assertEquals(0, vcfRecord.getFormatIndex());
        assertNotEquals("", vcfRecord.getFilterIndex());
        assertEquals(0, vcfRecord.getInfoKeyIndexCount());
        VcfRecordProtoToVariantConverter converter = new VcfRecordProtoToVariantConverter(fields, Collections.emptyMap(), fileId, studyId);
        Variant convertedVariant = converter.convert(vcfRecord, "1", 0);

//        System.out.println("variant          = " + variant.toJson());
//        System.out.println("convertedVariant = " + convertedVariant.toJson());
        assertEquals(variant, convertedVariant);
    }

    @Test
    public void testConvertDefaultValues() throws Exception {

        VariantToProtoVcfRecord toProto = new VariantToProtoVcfRecord();
        toProto.updateMeta(fields);

        Variant variant = new Variant("1", 5, "A", "C");
        StudyEntry studyEntry = new StudyEntry();
        studyEntry.setSampleDataKeys(Arrays.asList("GT", "DP"));
        studyEntry.setStudyId(studyId);
        studyEntry.setSamples(Arrays.asList(
                new SampleEntry(null, null, Arrays.asList("0|0", "10")),
                new SampleEntry(null, null, Arrays.asList("0|1", "20")),
                new SampleEntry(null, null, Arrays.asList("1|0", "30")),
                new SampleEntry(null, null, Arrays.asList("1|1", "40"))));
        Map<String, String> fileData = new HashMap<>();
        fileData.put(StudyEntry.FILTER, "PASS");
        fileData.put(StudyEntry.QUAL, "57");
        fileData.put("Key", "Value");

        studyEntry.setFiles(Collections.singletonList(new FileEntry(fileId, "5:A:C:0", fileData)));
        variant.setStudies(Collections.singletonList(studyEntry));

        VcfSliceProtos.VcfRecord vcfRecord = toProto.convert(variant, 100);
        assertEquals(0, vcfRecord.getFormatIndex());
        assertEquals(0, vcfRecord.getFilterIndex());
        assertEquals(0, vcfRecord.getInfoKeyIndexCount());
        assertEquals(5, vcfRecord.getRelativeStart());
        Variant convertedVariant = converter.convert(vcfRecord, "1", 0);

        assertEquals(variant, convertedVariant);
    }

}