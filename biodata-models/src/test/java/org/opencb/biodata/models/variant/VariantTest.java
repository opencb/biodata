package org.opencb.biodata.models.variant;

import org.junit.Test;
import org.opencb.biodata.models.variant.avro.StructuralVariantType;
import org.opencb.biodata.models.variant.avro.StructuralVariation;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjlopez on 02/03/17.
 */
public class VariantTest {

    @Test
    public void parseVariantTest() {
        Map<String, Variant> map = new LinkedHashMap<>();
        map.put("1:1000:A:C", new Variant("1", 1000, 1000, "A", "C"));
        map.put("chr1:1000:A:C", new Variant("1", 1000, 1000, "A", "C"));
        map.put("1:1000-2000:<DEL>", new Variant("1", 1000, 2000, "", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation(1000, 1000, 2000, 2000, null, null, null, null)));
        map.put("1:1000-2000:<CNV>", new Variant("1", 1000, 2000, "", "<CNV>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 2000, 2000, null, null, null, null)));
        map.put("1:1000-2000:<CN0>", new Variant("1", 1000, 2000, "", "<CN0>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 2000, 2000, 0, null, null, StructuralVariantType.COPY_NUMBER_LOSS)));
        map.put("1:1000-2000:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 2000, 2000, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN)));

        for (Map.Entry<String, Variant> entry : map.entrySet()) {
            System.out.println("expected : " + entry.getValue().toJson());
            System.out.println("actual   : " + new Variant(entry.getKey()).toJson());
            assertEquals("Parsing \"" + entry.getKey() + "\"", entry.getValue(), new Variant(entry.getKey()));
        }
    }

}