package org.opencb.biodata.tools.variant.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import htsjdk.variant.vcf.VCFConstants;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.protobuf.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord;

public class VariantToVcfRecordTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConvertVariantInt() {

        List<String> sampleList = Arrays.asList("Sample_03", "Sample_01");

        // Variant
        String chr = "4";
        int start = 1234565;
        int end = start + 3;
        List<String> ids = Arrays.asList("id123", "id432");
        String ref = "X";
        String alt = "A";
        Variant v = createVariant(chr, start, end, ids, ref, alt);


        String fileName = "file_123";
        String format = "AB:EF:CD";
        String qual = "321.12";
        String filter = "PASS;low30";
        StudyEntry study = new StudyEntry();
        study.setFileId(fileName);
        study.setFormat(Arrays.asList(format.split(VCFConstants.FORMAT_FIELD_SEPARATOR)));
        study.setAttributes(
                buildMap(
                        "X:x", "A:ab",
                        VariantVcfFactory.SRC + ":src-stuff",
                        VariantVcfFactory.QUAL + ":" + qual,
                        VariantVcfFactory.FILTER + ":" + filter));
//        study.setSamplesData(new HashMap<String, Map<String,String>>());
//        study.getSamplesDataAsMap().put(sampleList.get(0), buildMap("EF:ef","AB:sample_03"));
//        study.getSamplesDataAsMap().put(sampleList.get(1), buildMap("EF:ef","AB:sample_01","CD:cd"));
        study.setSamplesData(new ArrayList<>());
        study.getSamplesData().add(Arrays.asList("ab1", "ef1", "cd1"));
        study.getSamplesData().add(Arrays.asList("ab2", "ef2", "cd2"));

//        Map<String, VariantSourceEntry> studyMap = new HashMap<>();
//        studyMap.put("1", study );
        v.setStudies(Collections.singletonList(study));

        // META
        VcfMeta meta = new VcfMeta(new VariantSource(fileName, fileName, "2", "study"));
        meta.setFormatDefault(Arrays.asList(format.split(":")));
        meta.setInfoDefault(Arrays.asList("X", "AB"));

        // Converter
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        VcfSliceProtos.Fields fields = VcfSliceProtos.Fields.newBuilder()
                .addAllInfoKeys(Arrays.asList("X", "AB", "A"))
                .addFormats("AB:EF:CD")
                .addFilters("PASS")
                .addFilters("low30")
                .addFilters("PASS;low30")
                .addAllDefaultInfoKeys(Arrays.asList(0, 1)).build();
        con.updateMeta(fields);
        VcfRecord rec = con.convert(v, 100);

        assertArrayEquals(rec.getIdNonDefaultList().toArray(), ids.toArray());
        assertEquals(ref, rec.getReference());
        assertEquals(alt, rec.getAlternate());
        assertEquals(65, rec.getRelativeStart());
        assertEquals(65 + 3, rec.getRelativeEnd());
        assertEquals(sampleList.size(), rec.getSamplesList().size());
        assertEquals(Arrays.asList("ab2", "ef2", "cd2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(Arrays.asList("ab1", "ef1", "cd1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(Float.parseFloat(qual) + 1, rec.getQuality(), 0);
        assertEquals(Arrays.asList(0, 2), new ArrayList<>(rec.getInfoKeyIndexList()));
        assertEquals(2, rec.getFilterIndex());

        // change default FILTER
        meta.setFilterDefault(filter);
        con.updateMeta(VcfSliceProtos.Fields.newBuilder(fields).setFilters(0, filter).setFilters(2, "PASS").build());
        rec = con.convert(v, 100);
        assertEquals(0, rec.getFilterIndex());
    }

    private Map<String, String> buildMap(String... entries) {
        Map<String, String> m = new HashMap<>();
        Arrays.asList(entries).forEach(x -> m.put(x.split(":")[0], x.split(":")[1]));
        return m;
    }

    private Variant createVariant(String chr, int start, int end,
                                  List<String> ids, String ref, String alt) {
        Variant v = new Variant(chr, start, end, ref, alt);
        v.setIds(ids);
        return v;
    }

    @Test
    public void testGetSlicePosition() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSlicePosition(100, 0));
        assertEquals("Issues with ignoring chunks <= 0", 101, con.getSlicePosition(101, -1));
        assertEquals("Issues with slice conversion", 100, con.getSlicePosition(100, 10));
        assertEquals("Issues with slice conversion", 100, con.getSlicePosition(109, 10));
        assertEquals("Issues with slice conversion", 100, con.getSlicePosition(100, 100));
        assertEquals("Issues with slice conversion", 0, con.getSlicePosition(99, 100));
        assertEquals("Issues with slice conversion", 0, con.getSlicePosition(100, 1000));
        assertEquals("Issues with slice conversion", 1200, con.getSlicePosition(1234, 100));
    }

    @Test
    public void testGetSliceOffset() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSliceOffset(100, 0));
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSliceOffset(100, -1));
        assertEquals("Issues with slice conversion", 0, con.getSliceOffset(100, 10));
        assertEquals("Issues with slice conversion", 0, con.getSliceOffset(100, 100));
        assertEquals("Issues with slice conversion", 1, con.getSliceOffset(101, 100));
        assertEquals("Issues with slice conversion", 34, con.getSliceOffset(1234, 100));
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
//		assertEquals("Issues with Format",formatList, converter.getDefaultFormatKeys());

    }

    @Test
    public void testDecodeSample() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();
        List<List<String>> data = new LinkedList<>();

        data.add(Arrays.asList("a"));
        Map<String, Integer> formatPositions = new HashMap<>();
        formatPositions.put("A", 0);
        formatPositions.put("B", 1);
        assertEquals(
                new ArrayList<>(con.encodeSamples(formatPositions, data).get(0).getSampleValuesList()),
                Arrays.asList("a"));

        data.set(0, Arrays.asList("a", "b"));
        assertEquals(
                new ArrayList<>(con.encodeSamples(formatPositions, data).get(0).getSampleValuesList()),
                Arrays.asList("a", "b"));

        data.set(0, Arrays.asList("a", "b", "c"));
        assertEquals(
                new ArrayList<>(con.encodeSamples(formatPositions, data).get(0).getSampleValuesList()),
                Arrays.asList("a", "b", "c"));

    }

    @Test
    public void testGetSamples() {
        VariantToProtoVcfRecord con = new VariantToProtoVcfRecord();

        List<String> samplesList = Arrays.asList("S1", "S2", "S5", "S3");
        VcfMeta meta = new VcfMeta(new VariantSource("", "", "", ""));
        meta.getVariantSource().setSamples(samplesList);
//        con.updateVcfMeta(meta, slice);
//
//        List<String> samples = con.getSamples();
//        assertEquals(samplesList, samples);
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
        assertEquals(expected, VcfRecordToVariantConverter.getQuality(quality));
    }
}
