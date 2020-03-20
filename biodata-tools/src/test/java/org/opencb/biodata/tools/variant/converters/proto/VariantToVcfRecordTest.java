package org.opencb.biodata.tools.variant.converters.proto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.opencb.biodata.tools.variant.converters.proto.VariantToProtoVcfRecord.getSliceOffset;
import static org.opencb.biodata.tools.variant.converters.proto.VariantToProtoVcfRecord.getSlicePosition;

import htsjdk.variant.vcf.VCFConstants;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.*;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord;

public class VariantToVcfRecordTest {

    private List<String> ids = Arrays.asList("id123", "id432");
    private String formatGt = "GT:AB:EF:CD";
    private String format = "AB:EF:CD";
    private String qual = "321.12";
    private String filter = "PASS;low30";

    private Variant v_gt;
    private Variant v;

    @Before
    public void setUp() throws Exception {
        v_gt = Variant.newBuilder("4:1234565-1234568:X:A")
                .setNames(ids)
                .setStudyId("s")
                .setFileId("file_123")
                .setQuality(qual)
                .setFilter(filter)
                .addFileData("X", "x")
                .addFileData("A", "ab")
                .addFileData(StudyEntry.SRC, ":src-stuff")
                .setSampleDataKeys(formatGt.split(VCFConstants.FORMAT_FIELD_SEPARATOR))
                .addSample("Sample_0A", "0/0", "ab1", "ef1", "cd1")
                .addSample("Sample_0B", "0/1", "ab2", "ef2", "cd2").build();
        v = Variant.newBuilder("4:1234565-1234568:X:A")
                .setNames(ids)
                .setStudyId("s")
                .setFileId("file_123")
                .setQuality(qual)
                .setFilter(filter)
                .addFileData("X", "x")
                .addFileData("A", "ab")
                .addFileData(StudyEntry.SRC, ":src-stuff")
                .setSampleDataKeys(format.split(VCFConstants.FORMAT_FIELD_SEPARATOR))
                .addSample("Sample_0A", "ab1", "ef1", "cd1")
                .addSample("Sample_0B", "ab2", "ef2", "cd2").build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConvertVariant() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("X", "AB", "A", StudyEntry.VCF_ID))
                .addFormats("GT")
                .addFormats(format)
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .addAllDefaultInfoKeys(Arrays.asList(0, 1)).build();
        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields);
        VcfRecord rec = con.convert(v, 100);

        assertArrayEquals(rec.getIdNonDefaultList().toArray(), ids.toArray());
        assertEquals(v.getReference(), rec.getReference());
        assertEquals(v.getAlternate(), rec.getAlternate());
        assertEquals(65, rec.getRelativeStart());
        assertEquals(65 + 3, rec.getRelativeEnd());
        assertEquals(1, rec.getFormatIndex());
        assertEquals(2, rec.getSamplesList().size());
        assertEquals(Arrays.asList("ab1", "ef1", "cd1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(0, rec.getSamples(0).getGtIndex());
        assertEquals(Arrays.asList("ab2", "ef2", "cd2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(0, rec.getSamples(1).getGtIndex());
        assertEquals(Float.parseFloat(qual) + 1, rec.getQuality(), 0);
        assertEquals(Arrays.asList(0, 2, 3), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());

        // change default FILTER
        con.updateMeta(VcfSliceProtos.Fields.newBuilder(fields).setFilters(0, filter).setFilters(2, "PASS").build());
        rec = con.convert(v, 100);
        assertEquals(0, rec.getFilterIndex());
    }

    @Test
    public void testConvertVariantGT() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("X", "AB", "A", StudyEntry.VCF_ID))
                .addFormats(formatGt)
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .addAllDefaultInfoKeys(Arrays.asList(0, 1))
                .addGts("0/0")
                .addGts("0/1")
                .build();

//        VcfSliceProtos.Fields actualFields = VariantToVcfSliceConverter.buildDefaultFields(Collections.singletonList(v_gt));
//        assertEquals("\n" + expectedFields.toString() + "\n" + fields.toString(), expectedFields, fields);

        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields);
        VcfRecord rec = con.convert(v_gt, 100);

        assertEquals(0, rec.getFormatIndex());
        assertEquals(2, rec.getSamplesList().size());
        assertEquals(Arrays.asList("ab1", "ef1", "cd1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(0, rec.getSamples(0).getGtIndex());
        assertEquals(Arrays.asList("ab2", "ef2", "cd2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(1, rec.getSamples(1).getGtIndex());
        assertEquals(Float.parseFloat(qual) + 1, rec.getQuality(), 0);
        assertEquals(Arrays.asList(0, 2, 3), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());

        // change default FILTER
        con.updateMeta(VcfSliceProtos.Fields.newBuilder(fields).setFilters(0, filter).setFilters(2, "PASS").build());
        rec = con.convert(v_gt, 100);
        assertEquals(0, rec.getFilterIndex());
    }

    @Test
    public void testConvertVariantSkipAllFields() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder().build();

        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields, Collections.emptySet(), Collections.emptySet());

        VcfRecord rec = con.convert(v, 100);

        assertEquals(0, rec.getFormatIndex());
        assertEquals(0, rec.getSamplesList().size());
        assertEquals(0, rec.getQuality(), 0);
        assertEquals(Collections.emptyList(), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(0, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());
    }

    @Test
    public void testConvertVariantSkipAllFieldsGt() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder().build();

        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields, Collections.emptySet(), Collections.emptySet());

        VcfRecord rec = con.convert(v_gt, 100);
        assertEquals(0, rec.getFormatIndex());
        assertEquals(0, rec.getSamplesList().size());
        assertEquals(0, rec.getQuality(), 0);
        assertEquals(Collections.emptyList(), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(0, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());
    }

    @Test
    public void testConvertVariantSkipSomeFields() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("AB", "A"))
                .addAllDefaultInfoKeys(Collections.singletonList(0))
                .addFormats("EF")
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .build();
        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields,
                new HashSet<>(Arrays.asList(StudyEntry.FILTER, "A")),
                new HashSet<>(Arrays.asList("EF", "GT")));
//                Collections.singleton("EF"));

        VcfRecord rec = con.convert(v, 100);

        assertEquals(0, rec.getFormatIndex());
        assertEquals(2, rec.getSamplesList().size());
        assertEquals(Collections.singletonList("ef2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(0, rec.getSamples(0).getGtIndex());
        assertEquals(Collections.singletonList("ef1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(0, rec.getSamples(1).getGtIndex());

        assertEquals(0, rec.getQuality(), 0);
        assertEquals(Arrays.asList(1), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());
    }

    @Test
    public void testConvertVariantSkipSomeFieldsGt() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("AB", "A"))
                .addAllDefaultInfoKeys(Collections.singletonList(0))
                .addFormats("GT:EF")
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .addGts("0/0")
                .addGts("0/1")
                .build();
        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields,
                new HashSet<>(Arrays.asList(StudyEntry.FILTER, "A")),
                new HashSet<>(Arrays.asList("GT", "EF")));

        VcfRecord rec = con.convert(v_gt, 100);

        assertEquals(0, rec.getFormatIndex());
        assertEquals(2, rec.getSamplesList().size());
        assertEquals(Collections.singletonList("ef2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(0, rec.getSamples(0).getGtIndex());
        assertEquals(Collections.singletonList("ef1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(1, rec.getSamples(1).getGtIndex());

        assertEquals(0, rec.getQuality(), 0);
        assertEquals(Arrays.asList(1), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());
    }

    @Test
    public void testConvertVariantSkipSomeFieldsSkipGt() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("AB", "A"))
                .addAllDefaultInfoKeys(Collections.singletonList(0))
                .addFormats("EF")
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .build();
        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields,
                new HashSet<>(Arrays.asList(StudyEntry.FILTER, "A")), Collections.singleton("EF"));

        VcfRecord rec = con.convert(v_gt, 100);

        assertEquals(0, rec.getFormatIndex());
        assertEquals(2, rec.getSamplesList().size());
        assertEquals(Collections.singletonList("ef2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(0, rec.getSamples(0).getGtIndex());
        assertEquals(Collections.singletonList("ef1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(0, rec.getSamples(1).getGtIndex());

        assertEquals(0, rec.getQuality(), 0);
        assertEquals(Arrays.asList(1), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());
    }

    @Test
    public void testConvertVariantIncludeMissingFields() {
        // META
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("AB", "A"))
                .addAllDefaultInfoKeys(Collections.singletonList(0))
                .addFormats("GT:EF")
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .addGts("0/0")
                .addGts("0/1")
                .build();
        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord(fields,
                new HashSet<>(Arrays.asList(StudyEntry.FILTER, "A", "NON_EXISTING_1", "NON_EXISTING_2")),
                new HashSet<>(Arrays.asList("GT", "EF", "NON_EXISTING_3", "NON_EXISTING_4")));

        VcfRecord rec = con.convert(v_gt, 100);

        assertEquals(0, rec.getFormatIndex());
        assertEquals(2, rec.getSamplesList().size());
        assertEquals(Collections.singletonList("ef2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(0, rec.getSamples(0).getGtIndex());
        assertEquals(Collections.singletonList("ef1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(1, rec.getSamples(1).getGtIndex());

        assertEquals(0, rec.getQuality(), 0);
        assertEquals(Arrays.asList(1), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        System.out.println(new VcfRecordProtoToVariantConverter(fields, v.getStudy("s").getSamplesPosition(), "f", "s").convert(rec).toJson());
    }

    @Test
    public void testGetSlicePosition() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        assertEquals("Issues with ignoring chunks <= 0", 100, getSlicePosition(100, 0));
        assertEquals("Issues with ignoring chunks <= 0", 101, getSlicePosition(101, -1));
        assertEquals("Issues with slice conversion", 100, getSlicePosition(100, 10));
        assertEquals("Issues with slice conversion", 100, getSlicePosition(109, 10));
        assertEquals("Issues with slice conversion", 100, getSlicePosition(100, 100));
        assertEquals("Issues with slice conversion", 0, getSlicePosition(99, 100));
        assertEquals("Issues with slice conversion", 0, getSlicePosition(100, 1000));
        assertEquals("Issues with slice conversion", 1200, getSlicePosition(1234, 100));
    }

    @Test
    public void testGetSliceOffset() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        assertEquals("Issues with ignoring chunks <= 0", 100, getSliceOffset(100, 0));
        assertEquals("Issues with ignoring chunks <= 0", 100, getSliceOffset(100, -1));
        assertEquals("Issues with slice conversion", 0, getSliceOffset(100, 10));
        assertEquals("Issues with slice conversion", 0, getSliceOffset(100, 100));
        assertEquals("Issues with slice conversion", 1, getSliceOffset(101, 100));
        assertEquals("Issues with slice conversion", 34, getSliceOffset(1234, 100));
    }

    @Test
    public void testIsDefaultFormat() {
        VariantToProtoVcfRecord converter = new VariantToProtoVcfRecord();

        List<String> formatList = Arrays.asList("AB", "CD", "EF");
        List<String> wrongList = new ArrayList<>(formatList);
        Collections.reverse(wrongList);

        converter.updateMeta(VcfSliceProtos.Fields.newBuilder().addFormats(String.join(":", formatList)).build());
        assertTrue("Format is default ", converter.isDefaultFormat(formatList));
        assertFalse("Format is default ", converter.isDefaultFormat(wrongList));
//		assertEquals("Issues with Format",formatList, converters.getDefaultFormatKeys());

    }

    @Test
    public void testDecodeSample() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        List<SampleEntry> samples = new LinkedList<>();

        samples.add(new SampleEntry(null, null, Arrays.asList("a")));
        List<String> formatPositions = Arrays.asList("A", "B");
        assertEquals(
                new ArrayList<>(con.encodeSamples(formatPositions, null, samples).get(0).getSampleValuesList()),
                Arrays.asList("a"));

        samples.set(0, new SampleEntry(null, null, Arrays.asList("a", "b")));
        assertEquals(
                new ArrayList<>(con.encodeSamples(formatPositions, null, samples).get(0).getSampleValuesList()),
                Arrays.asList("a", "b"));

        samples.set(0, new SampleEntry(null, null, Arrays.asList("a", "b", "c")));
        assertEquals(
                new ArrayList<>(con.encodeSamples(formatPositions, null, samples).get(0).getSampleValuesList()),
                Arrays.asList("a", "b", "c"));

    }

    @Test
    public void testEncodeQuality() throws Exception {
        testEncodeDecodeQuality("10", 11f, "10.0");
        testEncodeDecodeQuality("10", 11f, "10.000");
        testEncodeDecodeQuality("10", 11f, "10");
        testEncodeDecodeQuality("10.01", 11.01f, "10.01");
        testEncodeDecodeQuality(null, 0f, ".");
        testEncodeDecodeQuality(null, 0f, null);
    }

    private void testEncodeDecodeQuality(String expected, float expectedFloat, String value) {
        float quality = VariantToProtoVcfRecord.encodeQuality(value);
        assertEquals(expectedFloat, quality, 0.0001);
        assertEquals(expected, VcfRecordProtoToVariantConverter.getQuality(quality));
    }
}
