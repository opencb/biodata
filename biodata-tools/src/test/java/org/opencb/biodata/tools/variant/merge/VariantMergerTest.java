package org.opencb.biodata.tools.variant.merge;

import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantNormalizer;
import org.opencb.biodata.models.variant.VariantTestUtils;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class VariantMergerTest {

    private static final VariantMerger VARIANT_MERGER = new VariantMerger();
    private Variant var;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        Variant tempate = VariantTestUtils.generateVariant("1",10,"A","T",VariantType.SNV,
                Arrays.asList("S01"), 
                Arrays.asList(Genotype.HET_REF));
        var = VARIANT_MERGER.createFromTemplate(tempate);
        VARIANT_MERGER.merge(var, tempate);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMergeSame_3SNP() {
        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1:10:A:T", "S02", "0/1"));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/1")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));

        String[] samples = new String[]{"S01","S02"};
        Map<String, Integer> collect = IntStream.range(0, 2).mapToObj(i -> i).collect(Collectors.toMap(i -> (String) samples[i], i-> i));
        assertEquals(collect,
                VARIANT_MERGER.getStudy(var).getSamplesPosition());

        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1:10:A:T", "S03", "0/0"));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/1"),lst("0/0")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));
    }

    @Test
    public void testMergeSame_2INDEL() {
        Variant var = VARIANT_MERGER.merge(VariantTestUtils.generateVariant("1:10:A:", "S01", "0/0"), VariantTestUtils.generateVariant("1:10:A:", "S02", "0/1"));
        StudyEntry se = var.getStudy(VariantTestUtils.STUDY_ID);

        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
        assertEquals("0/0", se.getSampleData("S01", "GT"));
        assertEquals("0/1", se.getSampleData("S02", "GT"));
        assertEquals(0, se.getSecondaryAlternates().size());
    }

    private List<List<String>> onlyField(List<List<String>> samplesData, int i) {
        return samplesData.stream().map(e -> Arrays.asList(e.get(i))).collect(Collectors.toList());
    }

    @Test
    public void testMergeDifferentSimple() {
        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1",10,"A","G",VariantType.SNV,
                Arrays.asList("S02"),
                Arrays.asList("0/0")));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/0")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));
    }

    @Test
    public void testMergeDifferentComplex() {
        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1", 10, "A", "G", VariantType.SNV,
                Arrays.asList("S02"),
                Arrays.asList("0/1")));
        StudyEntry se = var.getStudy(VariantTestUtils.STUDY_ID);
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Collections.singletonList(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV)), se.getSecondaryAlternates());
        assertEquals(Arrays.asList(lst("0/1"),lst("0/2")),
                onlyField(se.getSamplesData(), 0));
        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
    }

    @Test
    public void testEqualsVariantAltSecAlt() {
        Variant v = new Variant("1:1050:CTTTC:-");
        AlternateCoordinate a1 = new AlternateCoordinate(null, 1050, 1050, "C", "T", VariantType.SNV);
        AlternateCoordinate a2 = new AlternateCoordinate("1", 1054, 1054, "C", "T", VariantType.SNV);
        assertFalse(VARIANT_MERGER.equals(a1, a2));
    }


    @Test
    public void testValidateEmptyFields() {
        thrown.expect(IllegalStateException.class);
        Variant v = new Variant("1:1050:CTTTC:-");
        AlternateCoordinate a1 = new AlternateCoordinate(null, 1050, null, "C", "T", VariantType.SNV);
        VARIANT_MERGER.validateAlternate(a1);
    }

    @Test
    public void testMergeSecondaryAlternateToIdx() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:AT:T", "Sx", "1/2");
        AlternateCoordinate a1 = new AlternateCoordinate("1", 10, 10, "ATG", "G", VariantType.SNV);
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(a1);

        List<String> sampleList = new ArrayList<>();
        List<Variant> variantList = new ArrayList<>();
        List<AlternateCoordinate> secondaryAlternateList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String sample = "S"+i;

            Variant v = VariantTestUtils.generateVariant("1:10:ATG"+ StringUtils.repeat("T",i)+":T", sample, "1/2");
            AlternateCoordinate a = new AlternateCoordinate("1", 10, 10, "ATG", "G"+StringUtils.repeat('G',i), VariantType.SNV);
            v.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(a);
            sampleList.add(sample);
            variantList.add(v);
            secondaryAlternateList.add(a);
        }


        Variant merged = VARIANT_MERGER.merge(var1, variantList);
        StudyEntry studyEntry = merged.getStudies().get(0);

        for (int i = 0; i < 10; i++) {
            String gt = studyEntry.getSampleData(sampleList.get(i), "GT");
            Variant variant = variantList.get(i);
            AlternateCoordinate variantAlt = secondaryAlternateList.get(i);
            for (Genotype gto : Genotype.parse(gt)) {
                for (int gidx : gto.getAllelesIdx()) {
                    assertNotEquals(0, gidx);
                    assertNotEquals(1, gidx);
                    gidx -= 2;
                    AlternateCoordinate secAlt = studyEntry.getSecondaryAlternates().get(gidx);
                    assertTrue(VariantMerger.isSameVariant(variant, secAlt) || VariantMerger.isSameVariant(variantAlt, secAlt));
                }
            }
        }
    }

    @Test
    public void testMergeLotsOAlt() {
        Variant var = VARIANT_MERGER.merge(VariantTestUtils.generateVariant("1:100:CTTTC:-", "S01", "0/1"), VariantTestUtils.generateVariant("1:104:C:T", "S02", "0/1"));
        StudyEntry se = var.getStudy(VariantTestUtils.STUDY_ID);
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Collections.singletonList(new AlternateCoordinate("1", 104, 104, "C", "T", VariantType.SNV)), se.getSecondaryAlternates());
        assertEquals(Arrays.asList(lst("0/1"),lst("0/2")),
                onlyField(se.getSamplesData(), 0));
        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
        for (List<String> sampleData : se.getSamplesData()) {
            assertEquals(se.getFormat().size(), sampleData.size());
        }
    }

    @Test
    public void testMergeDifferentComplex2() {
        Variant var = VARIANT_MERGER.merge(VariantTestUtils.generateVariant("1:10:A:G", "S02", "0/1"), VariantTestUtils.generateVariant("1:10:A:T", "S01", "0/1"));
        StudyEntry se = var.getStudy(VariantTestUtils.STUDY_ID);
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Collections.singletonList(new AlternateCoordinate("1", 10, 10, "A", "T", VariantType.SNV)), se.getSecondaryAlternates());
        assertEquals(Arrays.asList(lst("0/1"),lst("0/2")),
                onlyField(se.getSamplesData(), 0));
        assertEquals(Arrays.asList("S02", "S01"), se.getOrderedSamplesName());
        for (List<String> sampleData : se.getSamplesData()) {
            assertEquals(se.getFormat().size(), sampleData.size());
        }
    }

    @Test
    public void testMergeIndelCase1() throws NonStandardCompliantSampleField {
        Variant v1 = new Variant("1:328:CTT:C");
        v1 = VariantTestUtils.generateVariant(v1, v1.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S1"), Collections.singletonList(Arrays.asList("1/2","PASS")), Collections.emptyMap());
        v1.getStudies().get(0).getSecondaryAlternates().add(new AlternateCoordinate(null,null,331,"CTT", "CTTTC", VariantType.INDEL));

        Variant v2 = new Variant("1:331:T:TCT");
        v2 = VariantTestUtils.generateVariant(v2, v2.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S1"), Collections.singletonList(Arrays.asList("0/1","PASS")), Collections.emptyMap());


        List<Variant> variants = new VariantNormalizer().normalize(Arrays.asList(v1, v2), false);
        variants.forEach(v -> System.out.println(v.toJson()));
        assertEquals(3, variants.size());
    }

    @Test
    public void testMergeIndelOverlapping() throws NonStandardCompliantSampleField {
        thrown.expect(IllegalStateException.class);
        Variant v1 = new Variant("1:10:TACACACACAC:TACACAC");
        v1 = VariantTestUtils.generateVariant(v1, v1.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S1"), Collections.singletonList(Arrays.asList("1/2","PASS")), Collections.emptyMap());
        v1.getStudies().get(0).getSecondaryAlternates().add(new AlternateCoordinate("1",10,21,"TACACACACAC", "T", VariantType.INDEL));

        Variant v2 = new Variant("1:11:A:.");
        v2 = VariantTestUtils.generateVariant(v2, v2.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S2"), Collections.singletonList(Arrays.asList("0/0","PASS")), Collections.emptyMap());

        System.out.println(v1.toJson());
        List<Variant> variants = new VariantNormalizer().normalize(Collections.singletonList(v1), false);
        for (Variant variant : variants) {
            List<AlternateCoordinate> alts = variant.getStudies().get(0).getSecondaryAlternates();
            for (AlternateCoordinate alt : alts) {
                alt.setChromosome(variant.getChromosome());
                if (alt.getStart() == null) {
                    alt.setStart(variant.getStart());
                }
            }
        }


        variants.stream().forEach(v -> System.out.println("v.toJson() = " + v.toJson()));

        // Fails down to normalization producing two variants with same GT (1/2 and 2/1)
        Variant mergeVar = VARIANT_MERGER.merge(v2, variants);
        System.out.println("mergeVar = " + mergeVar.toJson());
    }


    @Test
    public void testMergeReference() {
        Variant v1 = new Variant("1:10:ATGTA:-");
        v1 = VariantTestUtils.generateVariant(v1, v1.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S1"), Collections.singletonList(Arrays.asList("0/1", "PASS")), Collections.emptyMap());

        Variant v2 = new Variant("1:10:A:.");
        v2 = VariantTestUtils.generateVariant(v2, v2.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S2"), Collections.singletonList(Arrays.asList("0/0", "PASS")), Collections.emptyMap());

        Variant v3 = new Variant("1:12:T:.");
        v3 = VariantTestUtils.generateVariant(v3, v3.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S2"), Collections.singletonList(Arrays.asList("./.", "XXX")), Collections.emptyMap());

        Variant mergeVar = VARIANT_MERGER.merge(v1, v2);
        System.out.println("mergeVar2 = " + mergeVar.toJson());
        assertEquals(0, mergeVar.getStudies().get(0).getSecondaryAlternates().size());
        assertEquals("0/0", mergeVar.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_KEY));
        assertEquals("PASS", mergeVar.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_FILTER_KEY));
        Variant mergeVar2 = VARIANT_MERGER.merge(mergeVar, v3);
        System.out.println("mergeVar2 = " + mergeVar2.toJson());
        assertEquals("0/0,./.", mergeVar2.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_KEY));
        assertEquals("PASS", mergeVar.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_FILTER_KEY));
    }

    @Test
    public void testMergeIndel() {
        Variant v1 = new Variant("1:10:ATGTA:-");
        v1 = VariantTestUtils.generateVariant(v1, v1.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S1"), Collections.singletonList(Arrays.asList("0/1","PASS")), Collections.emptyMap());

        Variant v2 = new Variant("1:10:A:T");
        v2 = VariantTestUtils.generateVariant(v2, v2.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S2"), Collections.singletonList(Arrays.asList("1/1","PASS")), Collections.emptyMap());

        Variant v3 = new Variant("1:12:T:A");
        v3 = VariantTestUtils.generateVariant(v3, v3.getType(),
                Arrays.asList(VCFConstants.GENOTYPE_KEY, VCFConstants.GENOTYPE_FILTER_KEY),
                Arrays.asList("S2"), Collections.singletonList(Arrays.asList("0/1","XXX")), Collections.emptyMap());

        Variant mergeVar = VARIANT_MERGER.merge(v1, v2);
        System.out.println("mergeVar2 = " + mergeVar.toJson());
        assertEquals(1, mergeVar.getStudies().get(0).getSecondaryAlternates().size());
        assertEquals("2/2", mergeVar.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_KEY));
        assertEquals("PASS", mergeVar.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_FILTER_KEY));
        Variant mergeVar2 = VARIANT_MERGER.merge(mergeVar, v3);
        System.out.println("mergeVar2 = " + mergeVar2.toJson());
        assertEquals(2, mergeVar.getStudies().get(0).getSecondaryAlternates().size());
        assertEquals(new HashSet(Arrays.asList("2/2","0/3")), new HashSet(Arrays.asList(mergeVar2.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_KEY).split(","))));
        assertEquals("PASS", mergeVar.getStudies().get(0).getSampleData("S2", VCFConstants.GENOTYPE_FILTER_KEY));
    }

    @Test(expected = IllegalStateException.class)
    public void testMergeIndelDuplicates() {
        Variant v1 = VariantTestUtils.generateVariant("1:10:ATGTA:-", "S1", "0/1");
        Variant v2 = VariantTestUtils.generateVariant("1:10:A:T", "S2", "0/1");
        Variant v3 = VariantTestUtils.generateVariant("1:10:A:T", "S2", "0/1");
        Variant mergeVar = VARIANT_MERGER.merge(v1, v2);
        System.out.println("mergeVar2 = " + mergeVar);
        Variant mergeVar2 = VARIANT_MERGER.merge(mergeVar, v3);
        System.out.println("mergeVar2 = " + mergeVar2);
    }

    @Test()
    public void testMergeIndelOverlap() {
        Variant v1 = VariantTestUtils.generateVariant("1:10:ATGTA:-", "S1", "0/1");
        Variant v2 = VariantTestUtils.generateVariant("1:10:AT:-", "S2", "0/1");
        Variant v3 = VariantTestUtils.generateVariant("1:11:T:A", "S2", "0/1");
        Variant mergeVar = VARIANT_MERGER.merge(v1, v2);
        System.out.println("mergeVar2 = " + mergeVar);
        Variant mergeVar2 = VARIANT_MERGER.merge(mergeVar, v3);
        System.out.println("mergeVar2 = " + mergeVar2);
    }

    @Test
    public void testMergeSame_2SNV() {
        checkSameNoSecondaries("1:10:A:T");
        checkSameNoSecondaries("1:10:AT:TA");
        checkMergeVariantsNoSecondaries(
                VariantTestUtils.generateVariant("1:10:A:T", "S1", "0/0", "S2", "0/0"),
                VariantTestUtils.generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "1/1", "S06", "1/1"),
                "0/1", "1/1", "1/1", "1/1");
    }


    @Test
    public void testMergeSameWithSameAlternates_2SNV() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:T", "S1", "0/0", "S2", "0/0");
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "1/1", "S06", "1/1");
        AlternateCoordinate alternate = new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV);
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(alternate);
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(alternate);
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G"), "0/1", "1/1", "1/1", "1/1");
    }

    @Test
    public void testMergeSameWithDifferentAlternates_2SNV() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:T", "S1", "0/0", "S2", "0/0");
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "1/1", "S06", "1/1");
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV));
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "0/1", "1/1", "1/1", "1/1");
    }

    @Test
    public void testMergeSameWithSameUnorderedAlternates_2SNV() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:T", "S1", "1/2", "S2", "2/3");
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "0/2", "S06", "0/3");
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV));
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "0/1", "1/1", "0/3", "0/2");
    }

    @Test
    public void testMergeSame_2DEL() {
        checkSameNoSecondaries("1:10:A:");
        checkSameNoSecondaries("1:10:AT:T");
    }

    @Test
    public void testMergeSame_2IN() {
        checkSameNoSecondaries("1:10::AA");
        checkSameNoSecondaries("1:10:A:AAA");
    }

    @Test
    public void testMergeOverlap_2DEL() {
        checkOverlapNoSecondaries("1:10:A:", "1:10:AT:");
    }

    @Test
    public void testMergeOverlap_2DEL_2() {
        checkOverlapNoSecondaries("1:11:T:", "1:10:AT:");
    }

    @Test
    public void testMergeOverlap_2DEL_3() {
        checkOverlapNoSecondaries("1:10:AT:", "1:11:T:");
    }

    @Test
    public void testMergeOverlap_2IN() {
        checkOverlapNoSecondaries("1:10::AT", "1:10::GGG");
    }

//    @Test
//    public void testMergeOverlap_2IN_2() {
//        checkOverlapNoSecondaries("1:9::ATGG", "1:11::GGG");
//    }

    @Test
    public void testMergeOverlap_SNP_DEL() {
        checkOverlapNoSecondaries("1:10:A:T", "1:10:A:");
    }

    @Test
    public void testMergeOverlap_DEL_SNP() {
        checkOverlapNoSecondaries("1:10:A:", "1:10:A:T");
    }


    @Test
    public void testMergeWithSecondary_2SNP() {
        Variant variant = VariantTestUtils.generateVariant("1",10,"A","G",VariantType.SNV,
                Arrays.asList("S02"), 
                Arrays.asList("1/2"));
        variant.getStudies().get(0).setSecondaryAlternates(Arrays.asList(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV)));
        VARIANT_MERGER.merge(var, variant);
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
        assertEquals(2, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("2/3")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test
    public void testMergeWithSecondary_2SNP_2() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:T", "S01", "0/1");
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:G", "S02", "1/2");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
    }

    @Test
    public void testMergeWithSecondaryWithOtherFields_2SNP_2() {
        Variant var1 = VariantTestUtils.generateVariantWithFormat("1:10:A:T", "GT:FT", "S01", "0/1", "PASS");
        System.out.println("var1.toJson() = " + var1.toJson());
        Variant var2 = VariantTestUtils.generateVariantWithFormat("1:10:A:G", "GT:FT", "S02", "1/2", "noPASS");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
    }

    @Test
    public void testMergeWithSecondaryWithFileAttributes_2SNP_2() {
        Variant var1 = VariantTestUtils.generateVariantWithFormat("1:10:A:T", "GT", "S01", "0/1");
        System.out.println("var1.toJson() = " + var1.toJson());
        Variant var2 = VariantTestUtils.generateVariantWithFormat("1:10:A:G", "GT", "S02", "1/2");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
    }

    @Test
    public void testMergeWithSecondaryWithOtherFormats() {
        Variant var1 = VariantTestUtils.generateVariantWithFormat("1:10:A:T", "GT:DP", "S01", "0/1", "4");
        System.out.println("var1.toJson() = " + var1.toJson());
        Variant var2 = VariantTestUtils.generateVariantWithFormat("1:10:A:G", "GT:DP", "S02", "1/2", "5");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        Variant mergedVariant = checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
        StudyEntry studyEntry = mergedVariant.getStudies().get(0);
        assertEquals("GT:DP:FT", studyEntry.getFormatAsString());
        assertEquals("4", studyEntry.getSampleData("S01", "DP"));
        assertEquals("5", studyEntry.getSampleData("S02", "DP"));
    }

    @Test
    public void testMergeWithSecondaryWithOtherFormatsMissing() {
        Variant var1 = VariantTestUtils.generateVariantWithFormat("1:10:A:T", "PASS1", 100F, "GT:DP", "S01", "0/1", "4");
        Variant var2 = VariantTestUtils.generateVariantWithFormat("1:10:A:G", "PASS2", 100F, "GT:GQ", "S02", "1/2", "0.2");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        Variant mergedVariant = checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
        StudyEntry studyEntry = mergedVariant.getStudies().get(0);
        assertEquals("GT:DP:FT", studyEntry.getFormatAsString());
        assertEquals("4", studyEntry.getSampleData("S01", "DP"));
        assertEquals("PASS1", studyEntry.getSampleData("S01", "FT"));
        assertEquals("", studyEntry.getSampleData("S02", "DP"));
        assertEquals("PASS2", studyEntry.getSampleData("S02", "FT"));
    }
    @Test
    public void testMergeWithSecondaryWithOtherFormatsAndFT() {
        Variant var1 = VariantTestUtils.generateVariantWithFormat("1:10:A:T", "PASS1", 100F, "GT:DP:FT", "S01", "0/1", "4", "MyFilter1");
        Variant var2 = VariantTestUtils.generateVariantWithFormat("1:10:A:G", "PASS2", 100F, "GT:DP:FT", "S02", "1/2", "5", "MyFilter2");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        Variant mergedVariant = checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
        StudyEntry studyEntry = mergedVariant.getStudies().get(0);
        assertEquals("GT:DP:FT", studyEntry.getFormatAsString());
        assertEquals("4", studyEntry.getSampleData("S01", "DP"));
        assertEquals("MyFilter1", studyEntry.getSampleData("S01", "FT"));
        assertEquals("5", studyEntry.getSampleData("S02", "DP"));
        assertEquals("MyFilter2", studyEntry.getSampleData("S02", "FT"));
    }

    @Test
    public void testMergeWithSecondary_2SNP_3() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:T", "S01", "1/2");
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:G", "S02", "0/1");
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:C", "1:10:A:G"), "0/3");
    }

    @Test
    public void testMergeWithSameSecondary_2SNP() {
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:T", "S01", "0/1");
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:G", "S02", "1/2");
        var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "T", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G"), "2/1");
    }

    @Test
    public void testMergeWithSameSecondary_2SNP_2(){
        Variant var1 = VariantTestUtils.generateVariant("1:10:A:G", "S01", "1/2", "S02", "2/1");
        var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "T", VariantType.SNV));
        Variant var2 = VariantTestUtils.generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "0/0");
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:T"), "0/2", "2/2", "0/0");
    }

    @Test
    public void testMergeMonoAllelicSameAlt() {
        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1",10,"A","T",VariantType.SNV,
                Arrays.asList("S02"), 
                Arrays.asList("1")));
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
        assertEquals(0, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("1")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test
    public void testMergeMonoAllelicNocall() {
        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1",10,"A","G",VariantType.SNV,
                Arrays.asList("S02"), 
                Arrays.asList("./1")));
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("./2")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test
    public void testMergeSnpBlock() {
        Variant v = VariantTestUtils.generateVariant("1:10:A:", "S01", "0/0");
        v.setType(VariantType.NO_VARIATION);
        v.setEnd(100);

        checkMergeVariants(VariantTestUtils.generateVariant("1:10:A:T", "S02", "0/1"), v, Collections.emptyList(), "0/0");
    }

    @Test
    public void testMergeSameSample() { // TODO check if this can happen and result is correct !!!
        thrown.expect(IllegalStateException.class);
        VARIANT_MERGER.merge(var, VariantTestUtils.generateVariant("1", 9, "AAA", "-", VariantType.INDEL, Arrays.asList("S01"), Arrays.asList("0/1")));
//        StudyEntry se = VARIANT_MERGER.getStudy(var);
//        assertEquals(1, se.getSecondaryAlternates().size());
//        // TODO not sure 1/2 is correct if the same individual has a variant with 0/1 and another variant with 0/2 overlapping each other
//        assertEquals(Arrays.asList(lst("1/2")), se.getSamplesData());
    }

    private List<String> lst(String str) {
        return Collections.singletonList(str);
    }
    
    
    @Test
    public void sameStartPosition(){
        assertTrue(VariantTestUtils.generateVariant("1", 10, "A", "T", VariantType.SNV).onSameStartPosition(VariantTestUtils.generateVariant("1", 10, "A", "T", VariantType.SNV)));
        assertFalse(VariantTestUtils.generateVariant("1", 10, "A", "T", VariantType.SNV).onSameStartPosition(VariantTestUtils.generateVariant("1", 11, "A", "T", VariantType.SNV)));
    }

    public void checkOverlapNoSecondaries(String varstr1, String varstr2) {
        checkOverlapNoSecondaries(varstr1, varstr2, "0/0", "0/0", "0/0");
        checkOverlapNoSecondaries(varstr1, varstr2, "0/0", "0/1", "0/2");
        checkOverlapNoSecondaries(varstr1, varstr2, "0/1", "1/1", "2/2");
        //Test with missing values
        checkOverlapNoSecondaries(varstr1, varstr2, "1/1", ".", ".");
        checkOverlapNoSecondaries(varstr1, varstr2, ".", "1/1", "2/2");
        checkOverlapNoSecondaries(varstr1, varstr2, "1/.", "1/.", "2/.");
        //Test with haploids
        checkOverlapNoSecondaries(varstr1, varstr2, "0", "1", "2");
        checkOverlapNoSecondaries(varstr1, varstr2, "0", "0", "0");
    }

    public void checkSameNoSecondaries(String varstr1) {
        checkSameNoSecondaries(varstr1, varstr1, "0/0", "0/0");
        checkSameNoSecondaries(varstr1, varstr1, "0/0", "0/1");
        checkSameNoSecondaries(varstr1, varstr1, "0/1", "1/1");
        //Test with missing values
        checkSameNoSecondaries(varstr1, varstr1, "1/1", ".");
        checkSameNoSecondaries(varstr1, varstr1, ".", "1/1");
        checkSameNoSecondaries(varstr1, varstr1, "1/.", "1/.");
        //Test with haploids
        checkSameNoSecondaries(varstr1, varstr1, "0", "1");
        checkSameNoSecondaries(varstr1, varstr1, "0", "0");
    }

    public Variant checkOverlapNoSecondaries(String varstr1, String varstr2, String gt1, String gt2, String gt2Merged) {
        return checkMergeVariants(varstr1, varstr2, gt1, gt2, gt2Merged);
    }

    public Variant checkSameNoSecondaries(String varstr1, String varstr2, String gt1, String gt2) {
        return checkMergeVariants(varstr1, varstr2, gt1, gt2, gt2);
    }

    public Variant checkMergeVariants(String varstr1, String varstr2, String gt1, String gt2, String gt2Merged) {
        return checkMergeVariantsNoSecondaries(
                VariantTestUtils.generateVariant(varstr1, "S01", gt1),
                VariantTestUtils.generateVariant(varstr2, "S02", gt2), gt2Merged);
    }

    public Variant checkMergeVariantsNoSecondaries(Variant var1, Variant var2, String ...expectedVar2Gts) {
        assertTrue(var1.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().isEmpty());
        assertTrue(var2.getStudy(VariantTestUtils.STUDY_ID).getSecondaryAlternates().isEmpty());

        String vars1 = var1.toString();
        String vars2 = var2.toString();
        if (vars1.equals(vars2)) {
            //Same Variant
            return checkMergeVariants(var1, var2, Collections.emptyList(), expectedVar2Gts);
        } else {
            //Overlapped variants
            return checkMergeVariants(var1, var2, Collections.singletonList(vars2), expectedVar2Gts);
        }
    }

    /**
     * Check if the merge of the two variants is done well. Assumes that the variants can be merged and the genotypes of
     * the first variant are not going to be modified.
     *
     * @param var1  First variant to be merged
     * @param var2  Second variant to be merged
     * @param expectedVar2Gts Expected genotypes of the second variant
     * @return  The merged variant, for additional checks
     */
    public Variant checkMergeVariants(Variant var1, Variant var2, List<String> expectedSecondaryAlternates, String ...expectedVar2Gts) {
        List<AlternateCoordinate> expectedAlternatesList = new LinkedList<>();
        for (String expectedAlternate : expectedSecondaryAlternates) {
            Variant other = new Variant(expectedAlternate);
            AlternateCoordinate alternate = new AlternateCoordinate(other.getChromosome(), other.getStart(), other.getEnd(),
                    other.getReference(), other.getAlternate(), other.getType());
            if (!expectedAlternatesList.contains(alternate)) {
                expectedAlternatesList.add(alternate);
            }
        }

        ArrayList<String> samples = new ArrayList<>();
        samples.addAll(var1.getStudy(VariantTestUtils.STUDY_ID).getOrderedSamplesName());
        samples.addAll(var2.getStudy(VariantTestUtils.STUDY_ID).getOrderedSamplesName());

        List<String> gtsVar1 = var1.getStudy(VariantTestUtils.STUDY_ID).getSamplesData().stream().map(strings -> strings.get(0)).collect(Collectors.toList());

        Variant mergeVar = VARIANT_MERGER.merge(var1, var2);
        System.out.println("mergeVar.toJson() = " + mergeVar.toJson());
        StudyEntry se = mergeVar.getStudy(VariantTestUtils.STUDY_ID);
        assertEquals(samples, se.getOrderedSamplesName());
        for (int i = 0; i < gtsVar1.size(); i++) {
            assertEquals(gtsVar1.get(i), se.getSampleData(samples.get(i), "GT"));
        }
        for (int i = 0; i < expectedVar2Gts.length; i++) {
            assertEquals(expectedVar2Gts[i], se.getSampleData(samples.get(gtsVar1.size() + i), "GT"));
        }

        assertEquals(expectedAlternatesList, se.getSecondaryAlternates());

        return mergeVar;
    }

    @Test
    public void overlapsWith() throws Exception {
        overlaps("2:100:C:A");
        overlaps("2:100:CC:AA");
        overlaps("2:100:C:AA");
        overlaps("2:100:CC:A");
        overlaps("2:100::A");
        overlaps("2:100::A", "2:100::CTTNNN");
        overlaps("2:100::A", "2:100:C:A");
        overlaps("2:100:C:A", "2:100::A");
        notOverlaps("2:100::A", "2:101:A:C");
        notOverlaps("2:100::A", "2:99:A:C");

    }

    public void overlaps(String variant) {
        overlaps(variant, variant);
    }

    public void overlaps(String variant, String otherVariant) {
        System.out.printf("%s %15s\n", variant, otherVariant);
        assertTrue("Variant '" + variant + "' should overlap with '" + otherVariant + "'",
                new Variant(variant).overlapWith(new Variant(otherVariant), true));

    }

    public void notOverlaps(String variant, String otherVariant) {
        assertFalse("Variant '" + variant + "' should not overlap with '" + otherVariant + "'",
                new Variant(variant).overlapWith(new Variant(otherVariant), true));

    }
}
