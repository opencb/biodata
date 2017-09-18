package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
public class VariantBuilderTest {

    @Test
    public void parseVariantTest() {
        Map<String, Variant> map = new LinkedHashMap<>();
        map.put("1:1000:A:C", new Variant("1", 1000, 1000, "A", "C"));
        map.put("1:1000:A:C", new Variant("1", 1000, 1000, "A", "C"));
        map.put("chr1:1000:A:C", new Variant("1", 1000, 1000, "A", "C"));
        map.put("1:1000-2000:<DEL>", new Variant("1", 1000, 2000, "", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation(1000, 1000, 2000, 2000, null, null, null, null)));
        map.put("1:1000-2000:<CNV>", new Variant("1", 1000, 2000, "", "<CNV>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 2000, 2000, null, null, null, null)));
        map.put("1:1000-2000:<CN0>", new Variant("1", 1000, 2000, "", "<CN0>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 2000, 2000, 0, null, null, StructuralVariantType.COPY_NUMBER_LOSS)));
        map.put("1:1000-2000:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 2000, 2000, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN)));
        map.put("1:999<1000<1001-2000:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(999, 1001, 2000, 2000, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN)));
        map.put("1:1000-1999<2000<2001:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(1000, 1000, 1999, 2001, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN)));
        map.put("1:999<1000<1001-1999<2000<2001:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(999, 1001, 1999, 2001, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN)));
        map.put("1:1000:A:.", new Variant("1", 1000, 1000, "A", "").setType(VariantType.NO_VARIATION));
        map.put("1:1000-1005:A:.", new Variant("1", 1000, 1005, "A", "").setLength(6).setType(VariantType.NO_VARIATION));

        for (Map.Entry<String, Variant> entry : map.entrySet()) {
//            System.out.println("expected : " + entry.getValue().toJson());
//            System.out.println("actual   : " + new Variant(entry.getKey()).toJson());
            System.out.println("Original : " + entry.getKey() + " \t-->\t " + entry.getValue());
            assertEquals("Parsing \"" + entry.getKey() + "\"", entry.getValue(), new Variant(entry.getKey()));
        }
    }

    @Test
    public void buildSVDeletion() {
        Variant v = new VariantBuilder("1:1000:A:<DEL>")
                .setStudyId("1")
                .addAttribute("END", 1100)
                .build();

        assertEquals("A", v.getReference());
        assertEquals(VariantType.DELETION, v.getType());
        assertEquals(101, v.getLength().intValue());
        assertEquals(101, v.getLengthReference().intValue());
        assertEquals(0, v.getLengthAlternate().intValue());

    }

    @Test
    public void buildSVInsertion() {
        int length = 500;
        String alt = RandomStringUtils.random(length, 'A', 'C', 'G', 'T');
        Variant v = new VariantBuilder("1:1000:A:<INS>")
                .setStudyId("1")
                .addAttribute("SVINSSEQ", alt)
                .build();

        assertEquals(VariantType.INSERTION, v.getType());
        assertEquals("A", v.getReference());
        assertEquals(alt, v.getAlternate());
        assertEquals(length, v.getLength().intValue());
        assertEquals(length, v.getLengthAlternate().intValue());
        assertEquals(1, v.getLengthReference().intValue());

    }

    @Test
    public void buildSVInsertion2() {
        String leftSeq = RandomStringUtils.random(20, 'A', 'C', 'G', 'T');
        String rightSeq = RandomStringUtils.random(20, 'A', 'C', 'G', 'T');
        Variant v = new VariantBuilder("1:1000:A:<INS>")
                .setStudyId("1")
                .addAttribute("LEFT_SVINSSEQ", leftSeq)
                .addAttribute("RIGHT_SVINSSEQ", rightSeq)
                .build();

        assertEquals(VariantType.INSERTION, v.getType());
        assertEquals("A", v.getReference());
        assertEquals("<INS>", v.getAlternate());
        assertEquals(Variant.UNKNOWN_LENGTH, v.getLength().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v.getLengthAlternate().intValue());
        assertEquals(1, v.getLengthReference().intValue());
        assertEquals(leftSeq, v.getSv().getLeftSvInsSeq());
        assertEquals(rightSeq, v.getSv().getRightSvInsSeq());

    }

    @Test
    public void buildCNV() {
        Variant v = new VariantBuilder("1:1000:A:<CNV>")
                .setStudyId("1")
                .setFormat("GT", "CN")
                .addSample("S1", "0/1", "5")
                .addAttribute("END", 1100)
                .addAttribute("CIPOS", "-10,20")
                .addAttribute("CIEND", "-5,7")
                .build();

        assertEquals(VariantType.CNV, v.getType());
        assertEquals("A", v.getReference());
        assertEquals("<CNV>", v.getAlternate());
        assertEquals(1100, v.getEnd().intValue());
        assertEquals(101, v.getLength().intValue());
        assertEquals(StructuralVariantType.COPY_NUMBER_GAIN, v.getSv().getType());
        assertEquals(5, v.getSv().getCopyNumber().intValue());
        assertEquals(1000-10, v.getSv().getCiStartLeft().intValue());
        assertEquals(1000+20, v.getSv().getCiStartRight().intValue());
        assertEquals(1100-5, v.getSv().getCiEndLeft().intValue());
        assertEquals(1100+7, v.getSv().getCiEndRight().intValue());

    }

}