package org.opencb.biodata.tools.variant.converters.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.opencb.biodata.models.variant.VariantTestUtils.generateVariantWithFormat;

/**
 * Created on 17/02/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfSliceToVariantListConverterTest {


    private List<Variant> variants;

    @BeforeEach
    public void setUp() throws Exception {
        variants = Arrays.asList(
                generateVariantWithFormat("1:980:A:.", "PASS", 102f,
                        toMap("K4", "V1", "K2", "V2", "END", "1000"), "GT:X", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1000:A:C", "PASS", 12f,
                        toMap("K3", "V1", "K4", "V2"), "GT:X", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1002:A:C", "PASS:LowGQX", 102f,
                        toMap("K5", "V1", "K2", "V2"), "GT:X", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1003:A:C", "PASS", 0f,
                        toMap("K3", "V1", "K2", "V2"), "GT:T", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1004:A:C", "LowGQX", null,
                        toMap("K2", "V1", "K3", "V2"), "GT:X", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1005:A:C", "PASS", 102f,
                        toMap("K3", "V1", "K2", "V2"), "GT:X", "S1", "0/1", "1"),
                generateVariantWithFormat("1:1006:A:.", "PASS:LowGQX", 102f,
                        toMap("K1", "V1", "K5", "V2", "END", "1100"), "GT:T", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1106:T:C,TT", "PASS:LowGQX", 102f,
                        toMap("K2", "V1", "K3", "V2"), "GT:T", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1106:T:TT,C", "PASS:LowGQX", 102f,
                        toMap("K2", "V1", "K3", "V2"), "GT:T", "S1", "0/0", "1"),
                generateVariantWithFormat("1:1200:A:AATAG,AAGAAAGAG", "PASS", 102f,
                        toMap(), "GT:X", "S1", "0/0", "1"),
                generateVariantWithFormat("1:11111:A:", "PASS", 102f,
                        toMap(), "GT:X", "S1", "0/0", "1"),
                generateVariantWithFormat("1:11211::A,C", "PASS", 102f,
                        toMap(), "GT:X", "S1", "0/0", "1")
        );
        variants.get(5).getStudy("").getFile("").getData().put(StudyEntry.QUAL, ".");
//        variants.get(0).setEnd(1000);
//        variants.get(6).setEnd(1100);


        variants = new VariantNormalizer(true).apply(variants);
    }

    @Test
    public void buildFields() {
        VcfSliceProtos.Fields fields = VariantToVcfSliceConverter.buildDefaultFields(variants);

        assertEquals(Arrays.asList("PASS", "PASS:LowGQX", "LowGQX"), fields.getFiltersList());
        assertEquals(Arrays.asList("GT:X", "GT:T"), fields.getFormatsList());
        assertEquals(Arrays.asList("K2", "K3", "K4", "K5", "K1"), fields.getInfoKeysList());
        assertEquals(Arrays.asList(0, 1), fields.getDefaultInfoKeysList());
        assertEquals(Arrays.asList("0/0", "0/1"), fields.getGtsList());
    }

    @Test
    public void buildFieldsSkipAll() {
        VcfSliceProtos.Fields fields = VariantToVcfSliceConverter.buildDefaultFields(variants, Collections.emptySet(), Collections.emptySet());

        assertEquals(Collections.emptyList(), fields.getFiltersList());
        assertEquals(Collections.emptyList(), fields.getFormatsList());
        assertEquals(Collections.emptyList(), fields.getInfoKeysList());
        assertEquals(Collections.emptyList(), fields.getDefaultInfoKeysList());
        assertEquals(Arrays.asList("0/0", "0/1"), fields.getGtsList());
    }

    @Test
    public void buildFieldsSkipSome() {
        VcfSliceProtos.Fields fields = VariantToVcfSliceConverter.buildDefaultFields(variants,
                new HashSet<>(Arrays.asList(StudyEntry.FILTER, "K2", "K4", "K1")),
                new HashSet<>(Arrays.asList("GT", "T")));

        assertEquals(Arrays.asList("PASS", "PASS:LowGQX", "LowGQX"), fields.getFiltersList());
        assertEquals(Arrays.asList("GT", "GT:T"), fields.getFormatsList());
        assertEquals(Arrays.asList("K2", "K4",  "K1"), fields.getInfoKeysList());
        assertEquals(Arrays.asList(0), fields.getDefaultInfoKeysList());
        assertEquals(Arrays.asList("0/0", "0/1"), fields.getGtsList());
    }

    @Test
    public void buildFieldsSkipSomeNoGT() {
        VcfSliceProtos.Fields fields = VariantToVcfSliceConverter.buildDefaultFields(variants,
                new HashSet<>(Arrays.asList(StudyEntry.FILTER, "K2", "K4", "K1")),
                new HashSet<>(Arrays.asList("T")));

        assertEquals(Arrays.asList("PASS", "PASS:LowGQX", "LowGQX"), fields.getFiltersList());
        assertEquals(Arrays.asList("", "T"), fields.getFormatsList());
        assertEquals(Arrays.asList("K2", "K4",  "K1"), fields.getInfoKeysList());
        assertEquals(Arrays.asList(0), fields.getDefaultInfoKeysList());
        assertEquals(Arrays.asList("0/0", "0/1"), fields.getGtsList());
    }

    @Test
    public void testConvertVariants() throws InvalidProtocolBufferException {
//        VcfSliceToVariantListConverter converters = new VcfSliceToVariantListConverter();
        VariantToVcfSliceConverter converter = new VariantToVcfSliceConverter();
        VcfSliceProtos.VcfSlice slice = converter.convert(variants, 1000);

        slice = VcfSliceProtos.VcfSlice.parseFrom(slice.toByteArray());

        LinkedHashMap<String, Integer> samplesPosition = variants.get(0).getStudies().get(0).getSamplesPosition();
        VcfSliceToVariantListConverter vcfSliceToVariantListConverter = new VcfSliceToVariantListConverter(samplesPosition, "", "");
        List<Variant> convert = vcfSliceToVariantListConverter.convert(slice);


        assertEquals(VariantType.NO_VARIATION, convert.get(6).getType());
        assertEquals(1000, convert.get(0).getEnd().intValue());
        assertEquals(1000, convert.get(1).getEnd().intValue());
        assertEquals(1100, convert.get(6).getEnd().intValue());
        assertEquals("0", convert.get(3).getStudy("").getFile("").getData().get(StudyEntry.QUAL));
        assertNull(convert.get(4).getStudy("").getFile("").getData().get(StudyEntry.QUAL));
        assertNull(convert.get(5).getStudy("").getFile("").getData().get(StudyEntry.QUAL));


        for (int i = 0; i < convert.size(); i++) {
            // Set qual to NULL if required.
            if (".".equals(variants.get(i).getStudy("").getFile("").getData().get(StudyEntry.QUAL))) {
                variants.get(i).getStudy("").getFile("").getData().remove(StudyEntry.QUAL);
            }

            System.out.println("Expected  : " + variants.get(i).toJson());
            System.out.println("Converted : " + convert.get(i).toJson());
            System.out.println("Proto     : " + slice.getRecords(i).toString());
            assertEquals(variants.get(i), convert.get(i));
            System.out.println("------------------------------");
        }
    }

    public static Map<String, String> toMap(String... strings) {
        assertEquals(0, strings.length % 2);
        Map<String, String> map = new HashMap<>(strings.length / 2);
        for (int i = 0; i < strings.length; i = i + 2) {
            map.put(strings[i], strings[i + 1]);
        }
        return map;
    }

}