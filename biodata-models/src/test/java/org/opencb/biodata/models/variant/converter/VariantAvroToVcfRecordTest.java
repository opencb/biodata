package org.opencb.biodata.models.variant.converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.avro.Variant;
import org.opencb.biodata.models.variant.avro.VariantSourceEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfMeta.Builder;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord;

import java.util.*;

import static org.junit.Assert.*;

public class VariantAvroToVcfRecordTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConvertVariantInt() {

        List<String> sampleList = Arrays.asList("Sample_03","Sample_01");

        // Variant
        String chr = "4";
        int start = 1234565;
        int end = start+3;
        List<String> ids = Arrays.asList("id123","id432");
        String ref = "X";
        String alt = "A";
        Variant v = createVariant(chr, start, end, ids,ref,alt);


        String file_name = "file_123";String format = "AB:EF:CD";String qual = "321";String filter = "PASS;low30";
        VariantSourceEntry study = new VariantSourceEntry();
        study.setFileId(file_name);
        study.setFormat(format);
        study.setAttributes(
                buildMap(
                        "X:x","A:ab",
                        VariantAvroToVcfRecord.ATTRIBUTE_SRC+":src-stuff",
                        VariantAvroToVcfRecord.ATTRIBUTE_ORI+":ori-stuff",
                        VariantAvroToVcfRecord.ATTRIBUTE_QUAL+":"+qual,
                        VariantAvroToVcfRecord.ATTRIBUTE_FILTER+":"+filter));
//        study.setSamplesData(new HashMap<String, Map<String,String>>());
//        study.getSamplesData().put(sampleList.get(0), buildMap("EF:ef","AB:sample_03"));
//        study.getSamplesData().put(sampleList.get(1), buildMap("EF:ef","AB:sample_01","CD:cd"));
        study.setSamplesData(new ArrayList<>());
        study.getSamplesData().add(Arrays.asList("ab1", "ef1", "cd1"));
        study.getSamplesData().add(Arrays.asList("ab2", "ef2", "cd2"));

//        Map<String, VariantSourceEntry> studyMap = new HashMap<>();
//        studyMap.put("1", study );
        v.setStudies(Arrays.asList(study));

        // META
        Builder mbuild = VcfMeta.newBuilder().addAllFormatDefault(Arrays.asList(format.split(":")))
                .addAllInfoDefault(Arrays.asList("X","AB")).addAllSamples(sampleList);
        mbuild.setStudyId("1").setFileId(file_name);

        // Converter
        VariantAvroToVcfRecord con = new VariantAvroToVcfRecord();
        con.updateVcfMeta(mbuild.build());
        VcfRecord rec = con.convert(v, 100);

        assertArrayEquals(rec.getIdNonDefaultList().toArray(), ids.toArray());
        assertEquals(ref, rec.getReference());
        assertEquals(alt, rec.getAlternate());
        assertEquals(65,rec.getRelativeStart());
        assertEquals(65+3,rec.getRelativeEnd());
        assertEquals(sampleList.size(), rec.getSamplesList().size());
        assertEquals(Arrays.asList("ab2", "ef2", "cd2"), new ArrayList<CharSequence>(rec.getSamples(1).getSampleValuesList()));
        assertEquals(Arrays.asList("ab1", "ef1", "cd1"), new ArrayList<CharSequence>(rec.getSamples(0).getSampleValuesList()));
        assertEquals(qual, rec.getQuality());
        assertEquals(Arrays.asList("A","X"), new ArrayList<String>(rec.getInfoKeyList()));
        assertEquals(filter, rec.getFilterNonDefault());

        // change default FILTER
        mbuild.setFilterDefault(filter);
        rec = con.convert(v, 100);
        assertEquals("", rec.getFilterNonDefault());



    }

    private Map<String, String> buildMap(String ... entries) {
        Map<String, String> m = new HashMap<>();
        Arrays.asList(entries).forEach(x -> m.put(x.split(":")[0], x.split(":")[1]));
        return m;
    }

    private Variant createVariant(String chr, int start, int end,
                                  List<String> ids, String ref, String alt) {
        Variant v = new Variant();
        v.setReference(chr);
        v.setStart(start);
        v.setEnd(end);
        v.setIds(ids);
        v.setReference(ref);
        v.setAlternate(alt);
        return v;
    }

    @Test
    public void testGetSlicePosition() {
        VariantAvroToVcfRecord con = new VariantAvroToVcfRecord();
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSlicePosition(100, 0));
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSlicePosition(100, -1));
        assertEquals("Issues with slice conversion", 10, con.getSlicePosition(100, 10));
        assertEquals("Issues with slice conversion", 1, con.getSlicePosition(100, 100));
        assertEquals("Issues with slice conversion", 0, con.getSlicePosition(100, 1000));
        assertEquals("Issues with slice conversion", 12, con.getSlicePosition(1234, 100));
    }

    @Test
    public void testGetSliceOffset() {
        VariantAvroToVcfRecord con = new VariantAvroToVcfRecord();
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSliceOffset(100, 0));
        assertEquals("Issues with ignoring chunks <= 0", 100, con.getSliceOffset(100, -1));
        assertEquals("Issues with slice conversion", 0, con.getSliceOffset(100, 10));
        assertEquals("Issues with slice conversion", 0, con.getSliceOffset(100, 100));
        assertEquals("Issues with slice conversion", 1, con.getSliceOffset(101, 100));
        assertEquals("Issues with slice conversion", 34, con.getSliceOffset(1234, 100));
    }

    @Test
    public void testIsDefaultFormat() {
        String format = "AB:CD:EF";
        VariantAvroToVcfRecord con = new VariantAvroToVcfRecord();

        List<String> flist = Arrays.asList("AB","CD","EF");
        List<String> wrongList = new ArrayList<String>(flist);
        Collections.reverse(wrongList);
        con.updateVcfMeta(VcfMeta.newBuilder().addAllFormatDefault(flist).build());
        List<String> decode = con.decodeFormat(format);
        assertTrue("Format is default ",con.isDefaultFormat(flist));
        assertFalse("Format is default ",con.isDefaultFormat(wrongList));
        assertEquals("Format decoded", flist,decode);
//		assertEquals("Issues with Format",flist, con.getDefaultFormatKeys());

    }

    @Test
    public void testDecodeSample() {
        VariantAvroToVcfRecord con = new VariantAvroToVcfRecord();
        Map<String, String> data = new HashMap<>();

        data.put("A", "a");
        assertEquals(
                new ArrayList<String>(con.decodeSample(Arrays.asList("A","B"), data).getSampleValuesList()),
                Arrays.asList("a",""));
        assertNotEquals(
                new ArrayList<String>(con.decodeSample(Arrays.asList("A","B"), data).getSampleValuesList()),
                Arrays.asList("a"));

        data.put("B","b");
        assertEquals(
                new ArrayList<String>(con.decodeSample(Arrays.asList("A","B"), data).getSampleValuesList()),
                Arrays.asList("a","b"));

        data.put("C","c");
        assertEquals(
                new ArrayList<String>(con.decodeSample(Arrays.asList("A","B"), data).getSampleValuesList()),
                Arrays.asList("a","b"));

    }

    @Test
    public void testGetSamples() {
        VariantAvroToVcfRecord con = new VariantAvroToVcfRecord();

        Iterable<String> slist = Arrays.asList("S1","S2","S5","S3");
        VcfMeta meta = VcfMeta.newBuilder().addAllSamples(slist ).build();
        con.updateVcfMeta(meta);

        List<String> samples = con.getSamples();
        assertEquals(slist, samples);
    }

}
