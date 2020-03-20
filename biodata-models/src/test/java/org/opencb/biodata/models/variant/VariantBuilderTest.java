package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.protobuf.VariantProto;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.opencb.biodata.models.variant.VariantBuilder.getProtoVariantType;

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
        map.put("1:1000-2000:<DEL>", new Variant("1", 1000, 2000, "", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation()));
        map.put("1:1000-1010:A:<DEL>", new Variant("1", 1000, 1010, "A", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation()).setLength(11));
        map.put("1:1000-1010:<DEL>", new Variant("1", 1000, 1010, "", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation()).setLength(11));
        map.put("1:1000-1010:<DEL:ME:ALU>", new Variant("1", 1000, 1010, "", "<DEL:ME:ALU>").setType(VariantType.DELETION).setSv(new StructuralVariation()).setLength(11));
//        map.put("1:1000:A:<DEL>", new Variant("1", 1000, 1000, "A", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation(1000, 1000, 1000, 1000, null, null, null, null)).setLength(Variant.UNKNOWN_LENGTH));
//        map.put("1:1000:<DEL>", new Variant("1", 1000, 999, "", "<DEL>").setType(VariantType.DELETION).setSv(new StructuralVariation(1000, 1000, 999, 999, null, null, null, null)).setLength(Variant.UNKNOWN_LENGTH));
        map.put("1:1000-2000:<CNV>", new Variant("1", 1000, 2000, "", "<CNV>").setType(VariantType.CNV).setSv(new StructuralVariation()));
        map.put("1:1000-2000:<CN0>", new Variant("1", 1000, 2000, "", "<CN0>").setType(VariantType.CNV).setSv(new StructuralVariation(null, null, null, null, 0, null, null, StructuralVariantType.COPY_NUMBER_LOSS, null)));
        map.put("1:1000-2000:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(null, null, null, null, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN, null)));
        map.put("1:1000-2000::<DUP:TANDEM>", new Variant("1", 1000, 2000, "", "<DUP>").setType(VariantType.DUPLICATION).setSv(new StructuralVariation(null, null, null, null, null, null, null, StructuralVariantType.TANDEM_DUPLICATION, null)));
        map.put("1:1000-2000:<DUP:TANDEM>", new Variant("1", 1000, 2000, "", "<DUP>").setType(VariantType.DUPLICATION).setSv(new StructuralVariation(null, null, null, null, null, null, null, StructuralVariantType.TANDEM_DUPLICATION, null)));
        map.put("1:999<1000<1001-2000:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(999, 1001, null, null, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN, null)));
        map.put("1:1000-1999<2000<2001:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(null, null, 1999, 2001, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN, null)));
        map.put("1:999<1000<1001-1999<2000<2001:<CN5>", new Variant("1", 1000, 2000, "", "<CN5>").setType(VariantType.CNV).setSv(new StructuralVariation(999, 1001, 1999, 2001, 5, null, null, StructuralVariantType.COPY_NUMBER_GAIN, null)));
        map.put("1:1000:A:.", new Variant("1", 1000, 1000, "A", "").setType(VariantType.NO_VARIATION));
        map.put("1:1000-1005:A:.", new Variant("1", 1000, 1005, "A", "").setLength(6).setType(VariantType.NO_VARIATION));
        map.put("1:1000-1005:A:<*>", new Variant("1", 1000, 1005, "A", "<*>").setLength(6).setType(VariantType.NO_VARIATION));
        map.put("1:1000-1005:A:<NON_REF>", new Variant("1", 1000, 1005, "A", "<NON_REF>").setLength(6).setType(VariantType.NO_VARIATION));
        map.put("1:1000:ACACAC...GTGTGTGT", new Variant("1", 1000, 999, "", "<INS>").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.INSERTION).setSv(new StructuralVariation(null, null, null, null, null, "ACACAC", "GTGTGTGT", null, null)));
        map.put("1:1000:...GTGTGTGT", new Variant("1", 1000, 999, "", "<INS>").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.INSERTION).setSv(new StructuralVariation(null, null, null, null, null, "", "GTGTGTGT", null, null)));
        map.put("1:1000:ACACAC...", new Variant("1", 1000, 999, "", "<INS>").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.INSERTION).setSv(new StructuralVariation(null, null, null, null, null, "ACACAC", "", null, null)));
        map.put("1:799984<800001<800022:-:TGTGGTGTGTGTGGTGTG...ACCACACCCACACAACACACA", new Variant("1", 800001, 800000, "", "<INS>").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.INSERTION).setSv(new StructuralVariation(799984, 800022, null, null, null, "TGTGGTGTGTGTGGTGTG", "ACCACACCCACACAACACACA", null, null)));

        // Breakends
        map.put("1:1000:A:A.", new Variant("1", 1000, 999, "A", "A.").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.BREAKEND).setSv(new StructuralVariation(null, null, null, null, null, null, null, null, null)));
        map.put("1:800001:A:A[2:321681[", new Variant("1", 800001, 800000, "A", "A[2:321681[").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.BREAKEND).setSv(new StructuralVariation(null, null, null, null, null, null, null, null, new Breakend(new BreakendMate("2", 321681, null, null), BreakendOrientation.SE, null))));
        map.put("1:799984<800001<800022:A:A[2:321681[", new Variant("1", 800001, 800000, "A", "A[2:321681[").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.BREAKEND).setSv(new StructuralVariation(799984, 800022, null, null, null, null, null, null, new Breakend(new BreakendMate("2", 321681, null, null), BreakendOrientation.SE, null))));
        map.put("1:800001:G:GTATTG[2:321681[", new Variant("1", 800001, 800000, "G", "GTATTG[2:321681[").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.BREAKEND).setSv(new StructuralVariation(null, null, null, null, null, null, null, null, new Breakend(new BreakendMate("2", 321681, null, null), BreakendOrientation.SE, "TATTG"))));
        map.put("1:800001:G:[2:321681[GTATTG", new Variant("1", 800001, 800000, "G", "[2:321681[GTATTG").setLength(Variant.UNKNOWN_LENGTH).setType(VariantType.BREAKEND).setSv(new StructuralVariation(null, null, null, null, null, null, null, null, new Breakend(new BreakendMate("2", 321681, null, null), BreakendOrientation.EE, "GTATT"))));

        for (Map.Entry<String, Variant> entry : map.entrySet()) {
            String expected = entry.getKey().replace(":-:", ":").replace("::", ":").replace("chr", "");
            String actual = entry.getValue().toString().replace(":-:", ":");

            System.out.println("Original : " + entry.getKey() + " \t-->\t " + entry.getValue());
            try {
                assertEquals("Parsing \"" + entry.getKey() + "\"", entry.getValue(), new Variant(entry.getKey()));
            } catch (AssertionError e) {
                System.out.println("expected : " + entry.getValue().toJson());
                System.out.println("actual   : " + new Variant(entry.getKey()).toJson());
                throw e;
            }
            assertEquals(expected, actual);
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
        String alt = RandomStringUtils.random(length - 1, 'A', 'C', 'G', 'T');
        Variant v = new VariantBuilder("1:1000:A:<INS>")
                .setStudyId("1")
                .addAttribute("SVINSSEQ", alt)
                .build();

        assertEquals(VariantType.INSERTION, v.getType());
        assertEquals("A", v.getReference());
        assertEquals("A" + alt, v.getAlternate());
        assertEquals(length, v.getLength().intValue());
        assertEquals(length, v.getLengthAlternate().intValue());
        assertEquals(1, v.getLengthReference().intValue());
        assertEquals("1000:A:<INS>:0", v.getStudies().get(0).getFiles().get(0).getCall());

    }

    @Test
    public void buildSVInsertion2() {
        int length = 500;
        String alt = "A" + RandomStringUtils.random(length - 1, 'A', 'C', 'G', 'T');
        Variant v = new VariantBuilder("1:1000:A:" + alt)
                .setStudyId("1")
                .build();

        assertEquals(VariantType.INSERTION, v.getType());
        assertEquals("A", v.getReference());
        assertEquals(alt, v.getAlternate());
        assertEquals(length, v.getLength().intValue());
        assertEquals(length, v.getLengthAlternate().intValue());
        assertEquals(1, v.getLengthReference().intValue());

    }

    @Test
    public void buildSVInsertion3() {
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
    public void buildSVInsertion4() {
        String leftSeq = RandomStringUtils.random(20, 'A', 'C', 'G', 'T');
        String rightSeq = RandomStringUtils.random(20, 'A', 'C', 'G', 'T');
        Variant v = new VariantBuilder("1:1000:A:<INS>")
                .setLength(1000)
                .setStudyId("1")
                .addAttribute("LEFT_SVINSSEQ", leftSeq)
                .addAttribute("RIGHT_SVINSSEQ", rightSeq)
                .build();

        assertEquals(VariantType.INSERTION, v.getType());
        assertEquals("A", v.getReference());
        assertEquals("<INS>", v.getAlternate());
        assertEquals(1000, v.getLength().intValue());
        assertEquals(1000, v.getLengthAlternate().intValue());
        assertEquals(1, v.getLengthReference().intValue());
        assertEquals(leftSeq, v.getSv().getLeftSvInsSeq());
        assertEquals(rightSeq, v.getSv().getRightSvInsSeq());

    }

    @Test
    public void buildCNV() {
        Variant v = new VariantBuilder("1:1000:A:<CNV>")
                .setStudyId("1")
                .setSampleDataKeys("GT", "CN")
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

    @Test
    public void buildIndelVariantNoEnd() {
        String ref = "CAAAAAAA";
        Variant variant = new Variant("1", 100, ref, "C");
        assertEquals(ref, variant.getReference());
        assertEquals("C", variant.getAlternate());
        assertEquals(VariantType.INDEL, variant.getType());
        assertEquals(100, variant.getStart().intValue());
        assertEquals(100 + ref.length() - 1, variant.getEnd().intValue());
    }

    @Test
    public void buildIndelVariantNoEnd_large() {
        String ref = RandomStringUtils.random(200, 'A', 'C', 'G', 'T');
        Variant variant = new Variant("1", 100, ref, "-");
        assertEquals(ref, variant.getReference());
        assertEquals("", variant.getAlternate());
        assertEquals(VariantType.DELETION, variant.getType());
        assertEquals(100, variant.getStart().intValue());
        assertEquals(100 + ref.length() - 1, variant.getEnd().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildIncompleteSV() throws Exception {
        new VariantBuilder("1:1000:<DEL>").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildIncompleteSV_2() throws Exception {
        new VariantBuilder("1:1000:A:<DEL>").build();
    }


    @Test
    public void buildBND1() {
        Variant v1 = new Variant("1", 16877367, "A", "[chr4:17481913[T");
        assertEquals(VariantType.BREAKEND, v1.getType());
        assertEquals("A", v1.getReference());
        assertEquals("[chr4:17481913[T", v1.getAlternate());
        assertEquals(16877367, v1.getStart().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v1.getLength().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v1.getLengthReference().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v1.getLengthAlternate().intValue());
        assertEquals("4", v1.getSv().getBreakend().getMate().getChromosome());
        assertEquals(17481913, v1.getSv().getBreakend().getMate().getPosition().intValue());
        assertEquals(BreakendOrientation.EE, v1.getSv().getBreakend().getOrientation());
    }

    @Test
    public void buildBND2() {
        Variant v1 = new Variant("19", 172450, "", "A]2:10000]");
        assertEquals(VariantType.BREAKEND, v1.getType());
        assertEquals("", v1.getReference());
        assertEquals("A]2:10000]", v1.getAlternate());
        assertEquals(172450, v1.getStart().intValue());
        assertEquals(172449, v1.getEnd().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v1.getLength().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v1.getLengthReference().intValue());
        assertEquals(Variant.UNKNOWN_LENGTH, v1.getLengthAlternate().intValue());
        assertEquals("2", v1.getSv().getBreakend().getMate().getChromosome());
        assertEquals(10000, v1.getSv().getBreakend().getMate().getPosition().intValue());
        assertEquals(BreakendOrientation.SS, v1.getSv().getBreakend().getOrientation());
    }

    @Test
    public void testGetProtoVariantType() throws Exception {
        for (VariantType type : VariantType.values()) {
            getProtoVariantType(type);
        }
        for (VariantProto.VariantType type : VariantProto.VariantType.values()) {
            // Proto generates an extra enum value "UNRECOGNIZED"
            if (type != VariantProto.VariantType.UNRECOGNIZED) {
                assertNotNull(VariantType.valueOf(type.name()));
            }
        }
        assertEquals(VariantType.values().length, VariantProto.VariantType.values().length - 1);
    }
}