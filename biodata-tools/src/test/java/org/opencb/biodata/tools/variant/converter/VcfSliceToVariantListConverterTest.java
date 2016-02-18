package org.opencb.biodata.tools.variant.converter;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;
import org.opencb.biodata.tools.variant.merge.VariantMergerTest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 17/02/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfSliceToVariantListConverterTest {


    private List<Variant> variants;

    @Before
    public void setUp() throws Exception {
        variants = Arrays.asList(
                VariantMergerTest.generateVariantWithFormat("1:100:A:C", "PASS", 102f,
                        toMap("K4", "V1", "K2", "V2"), "GT:X", "S1", "0/0", "1"),
                VariantMergerTest.generateVariantWithFormat("1:101:A:C", "PASS", 12f,
                        toMap("K3", "V1", "K4", "V2"), "GT:X", "S1", "0/0", "1"),
                VariantMergerTest.generateVariantWithFormat("1:102:A:C", "PASS:LowGQX", 102f,
                        toMap("K5", "V1", "K2", "V2"), "GT:X", "S1", "0/0", "1"),
                VariantMergerTest.generateVariantWithFormat("1:103:A:C", "PASS", 0f,
                        toMap("K3", "V1", "K2", "V2"), "GT:T", "S1", "0/0", "1"),
                VariantMergerTest.generateVariantWithFormat("1:104:A:C", "LowGQX", null,
                        toMap("K2", "V1", "K3", "V2"), "GT:X", "S1", "0/0", "1"),
                VariantMergerTest.generateVariantWithFormat("1:105:A:C", "PASS", 102f,
                        toMap("K3", "V1", "K2", "V2"), "GT:X", "S1", "0/0", "1"),
                VariantMergerTest.generateVariantWithFormat("1:106:A:", "PASS:LowGQX", 102f,
                        toMap("K1", "V1", "K5", "V2", "END", "110"), "GT:T", "S1", "0/0", "1")
        );
        variants.get(6).setType(VariantType.NO_VARIATION);
        variants.get(6).setEnd(110);
        variants.get(5).getStudy("").getFile("").getAttributes().put(VariantVcfFactory.QUAL, ".");
    }

    @Test
    public void buildFields() {

        VcfSliceProtos.Fields fields = VariantToVcfSliceConverter.buildDefaultFields(variants, null).build();

        assertEquals(Arrays.asList("PASS", "PASS:LowGQX", "LowGQX"), fields.getFiltersList());
        assertEquals(Arrays.asList("GT:X", "GT:T"), fields.getFormatsList());
        assertEquals(Arrays.asList("K2", "K3", "K4", "K5", "K1"), fields.getInfoKeysList());
        assertEquals(Arrays.asList(0, 1), fields.getDefaultInfoKeysList());

    }

    @Test
    public void testConvertVariants() {
//        VcfSliceToVariantListConverter converter = new VcfSliceToVariantListConverter();
        VariantToVcfSliceConverter converter = new VariantToVcfSliceConverter();
        VcfSliceProtos.VcfSlice slice = converter.convert(variants);

        LinkedHashMap<String, Integer> samplesPosition = variants.get(0).getStudies().get(0).getSamplesPosition();
        VcfSliceToVariantListConverter vcfSliceToVariantListConverter = new VcfSliceToVariantListConverter(samplesPosition, "", "");
        List<Variant> convert = vcfSliceToVariantListConverter.convert(slice);
        for (int i = 0; i < convert.size(); i++) {
            System.out.println("Expected  : " + variants.get(i).toJson());
            System.out.println("Converted : " + convert.get(i).toJson());
            System.out.println("------------------------------");
        }

        assertEquals(VariantType.NO_VARIATION, convert.get(6).getType());
        assertEquals(110, convert.get(6).getEnd().intValue());
        assertEquals("0", convert.get(3).getStudy("").getFile("").getAttributes().get(VariantVcfFactory.QUAL));
        assertEquals(null, convert.get(4).getStudy("").getFile("").getAttributes().get(VariantVcfFactory.QUAL));
        assertEquals(null, convert.get(5).getStudy("").getFile("").getAttributes().get(VariantVcfFactory.QUAL));

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