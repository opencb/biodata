package org.opencb.biodata.tools.variant;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.opencb.biodata.models.variant.VariantTestUtils.STUDY_ID;
import static org.opencb.biodata.models.variant.VariantTestUtils.generateVariantWithFormat;

/**
 * Created on 26/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantNormalizerTest extends VariantNormalizerGenericTest {

    @Test
    public void testReverseDiff() {
        assertEquals(-1, VariantNormalizer.reverseIndexOfDifference("", ""));
        assertEquals(-1, VariantNormalizer.reverseIndexOfDifference(null, null));
        assertEquals(0, VariantNormalizer.reverseIndexOfDifference("AAA", "AAC"));
        assertEquals(-1, VariantNormalizer.reverseIndexOfDifference("ACA", "ACA"));
        assertEquals(1, VariantNormalizer.reverseIndexOfDifference("GGA", "TTA"));
        assertEquals(3, VariantNormalizer.reverseIndexOfDifference("GGGGGGGGGGGGG", "GGG"));
        assertEquals(3, VariantNormalizer.reverseIndexOfDifference("GGG", "GGGGGGGGGGGGG"));
    }

    @Test
    public void testSimpleNormalize() {
        Variant v = new Variant("1:100:A:C,AC");
        List<Variant> l = normalizer.apply(Collections.singletonList(v));

        v = l.get(0);
        OriginalCall call = v.getStudies().get(0).getFiles().get(0).getCall();
        assertEquals("1:100:A:C", v.toString());
        assertEquals("1:100:A:C,AC", call.getVariantId());
        assertEquals(0, call.getAlleleIndex().intValue());
        assertEquals(v, normalizer.apply(new Variant(call.getVariantId())).get(call.getAlleleIndex()));

        v = l.get(1);
        call = v.getStudies().get(0).getFiles().get(0).getCall();
        assertEquals("1:101:-:C", v.toString());
        assertEquals("1:100:A:C,AC", call.getVariantId());
        assertEquals(1, call.getAlleleIndex().intValue());
        assertEquals(v, normalizer.apply(new Variant(call.getVariantId())).get(call.getAlleleIndex()));

    }

    @Test
    public void testNormalizedSamplesDataSame() throws NonStandardCompliantSampleField {
        // C -> A  === C -> A
        testSampleNormalization("1", 100, "C", "A", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData1() throws NonStandardCompliantSampleField {
        // AC -> AA  === C -> A
        testSampleNormalization("1", 100, "AC", "AA", 101, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData2() throws NonStandardCompliantSampleField {
        // CA -> AA  === C -> A
        testSampleNormalization("1", 100, "CA", "AA", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftDeletion() throws NonStandardCompliantSampleField {
        // AC -> C  === A -> .
        testSampleNormalization("1", 100, "AC", "C", 100, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataRightDeletion() throws NonStandardCompliantSampleField {
        // CA -> C  === A -> .
        testSampleNormalization("1", 100, "CA", "C", 101, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataAmbiguousDeletion() throws NonStandardCompliantSampleField {
        // AAA -> A  === AA -> .
        testSampleNormalization("1", 100, "AAA", "A", 100, 101, "AA", "");
    }

    @Test
    public void testNormalizeSamplesDataIndel() throws NonStandardCompliantSampleField {
        // ATC -> ACCC  === T -> CC
        testSampleNormalization("1", 100, "ATC", "ACCC", 101, 101, "T", "CC");
    }

    @Test
    public void testNormalizeSamplesDataRightInsertion() throws NonStandardCompliantSampleField {
        // C -> AC  === . -> A
        testSampleNormalization("1", 100, "C", "AC", 100, 99, "", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftInsertion() throws NonStandardCompliantSampleField {
        // C -> CA  === . -> A
        testSampleNormalization("1", 100, "C", "CA", 101, 100, "", "A");
    }

    @Test
    public void testNormalizeFalseMNV() throws NonStandardCompliantSampleField {

        Variant variant = newVariant(100, "CA", "TA");
        variant.getStudies().get(0).addSampleData("HG00096", Collections.singletonList("0|1"));
        assertEquals(VariantType.MNV, variant.getType());
        assertEquals(2, variant.getLength().intValue());

        normalizer.setGenerateReferenceBlocks(false);
        List<Variant> normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
        assertEquals(1, normalizedVariantList.size());
        assertEquals(VariantType.SNV, normalizedVariantList.get(0).getType());
        assertEquals(1, normalizedVariantList.get(0).getLength().intValue());
    }

    @Test
    public void testNormalizeSamplesDataMNV() throws NonStandardCompliantSampleField {
        normalizer.setDecomposeMNVs(true);
        Variant variant = newVariant(100, "ACTCGTAAA", "ATTCGAAA");
        variant.getStudies().get(0).addSampleData("S1", Collections.singletonList("0/0"));
        variant.getStudies().get(0).addSampleData("S2", Collections.singletonList("0/1"));
        variant.getStudies().get(0).addSampleData("S3", Collections.singletonList("./."));
        variant.getStudies().get(0).addSampleData("S4", Collections.singletonList("1"));
        List<Variant> variants = normalizer.apply(Collections.singletonList(variant));

        System.out.println(variant.toJson());
        System.out.println("------------");
        variants.forEach((x) -> System.out.println(x.toJson()));

        Variant snp = variants.get(1);
        Variant indel = variants.get(3);
        List<Variant> refBlocks = Arrays.asList(variants.get(0), variants.get(2), variants.get(4));

        assertEquals(VariantType.NO_VARIATION, variants.get(0).getType());
        assertEquals(100, variants.get(0).getStart().intValue());
        assertEquals(100, variants.get(0).getEnd().intValue());
        assertEquals("1:101:C:T", snp.toString());
        assertEquals(VariantType.NO_VARIATION, variants.get(2).getType());
        assertEquals(102, variants.get(2).getStart().intValue());
        assertEquals(104, variants.get(2).getEnd().intValue());
        assertEquals("1:105:T:-", indel.toString());
        assertEquals(VariantType.NO_VARIATION, variants.get(4).getType());
        assertEquals(106, variants.get(4).getStart().intValue());
        assertEquals(108, variants.get(4).getEnd().intValue());




        assertEquals(0, snp.getStudies().get(0).getSecondaryAlternates().size());
        assertEquals(0, indel.getStudies().get(0).getSecondaryAlternates().size());

        assertTrue(snp.getStudies().get(0).getSampleDataKeys().contains("PS"));
        assertTrue(indel.getStudies().get(0).getSampleDataKeys().contains("PS"));
        assertEquals("1:101:C:T,1:105:T:-", snp.getStudies().get(0).getSampleData("S1", "PS"));
        assertEquals("1:101:C:T,1:105:T:-", indel.getStudies().get(0).getSampleData("S1", "PS"));

        for (Variant refBlock : refBlocks) {
            assertFalse(refBlock.getStudies().get(0).getSampleDataKeys().contains("PS"));
            assertEquals("0/0", refBlock.getStudies().get(0).getSampleData("S1", "GT"));
            assertEquals("0/0", refBlock.getStudies().get(0).getSampleData("S2", "GT"));
            assertEquals("./.", refBlock.getStudies().get(0).getSampleData("S3", "GT"));
            assertEquals("0", refBlock.getStudies().get(0).getSampleData("S4", "GT"));
        }

    }

    @Test
    public void testNormalizeSamplesDataMNV2() throws NonStandardCompliantSampleField {
        normalizer.setDecomposeMNVs(true);
        normalizer.setGenerateReferenceBlocks(false);
        Variant variant = newVariant(100, "ACTCGTA", "ATTCGA,ACTCCTA");
        variant.getStudies().get(0).addSampleData("S1", Collections.singletonList("0/0"));
        variant.getStudies().get(0).addSampleData("S2", Collections.singletonList("0/1"));
        variant.getStudies().get(0).addSampleData("S3", Collections.singletonList("0/2"));
        List<Variant> variants = normalizer.apply(Collections.singletonList(variant));

        assertEquals(3, variants.size());
        assertEquals("1:101:C:T", variants.get(0).toString());
        assertEquals("1:104:G:C", variants.get(1).toString());
        assertEquals("1:105:T:-", variants.get(2).toString());

//        variants.forEach(System.out::println);
//        variants.forEach((v) -> System.out.println(v.toJson()));
        assertEquals("1:101:C:T", variants.get(0).toString());
        assertEquals("1:104:G:C", variants.get(1).toString());
        assertEquals("1:105:T:-", variants.get(2).toString());

        assertEquals(true, variants.get(0).getStudies().get(0).getSampleDataKeys().contains("PS"));
        assertEquals(false, variants.get(1).getStudies().get(0).getSampleDataKeys().contains("PS"));
        assertEquals(true, variants.get(2).getStudies().get(0).getSampleDataKeys().contains("PS"));

        assertEquals("1:101:C:T,1:105:T:-", variants.get(0).getStudies().get(0).getSampleData("S1", "PS"));
        assertEquals("1:101:C:T,1:105:T:-", variants.get(0).getStudies().get(0).getSampleData("S2", "PS"));
        assertEquals("1:101:C:T,1:105:T:-", variants.get(0).getStudies().get(0).getSampleData("S3", "PS"));
        assertEquals("1:101:C:T,1:105:T:-", variants.get(2).getStudies().get(0).getSampleData("S1", "PS"));
        assertEquals("1:101:C:T,1:105:T:-", variants.get(2).getStudies().get(0).getSampleData("S2", "PS"));
        assertEquals("1:101:C:T,1:105:T:-", variants.get(2).getStudies().get(0).getSampleData("S3", "PS"));

        assertEquals(1, variants.get(0).getStudies().get(0).getSecondaryAlternates().size());
        assertEquals(1, variants.get(1).getStudies().get(0).getSecondaryAlternates().size());
        assertEquals(1, variants.get(2).getStudies().get(0).getSecondaryAlternates().size());

        assertEquals(new AlternateCoordinate("1",
                        104,
                        104,
                        "G",
                        "C",
                        VariantType.SNV),
                variants.get(0).getStudies().get(0).getSecondaryAlternates().get(0));
        assertEquals(new AlternateCoordinate("1",
                        101,
                        105,
                        "CTCGT",
                        "TTCG",
                        VariantType.INDEL),
                variants.get(1).getStudies().get(0).getSecondaryAlternates().get(0));
        assertEquals(new AlternateCoordinate("1",
                104,
                104,
                "G",
                "C",
                VariantType.SNV),
                variants.get(2).getStudies().get(0).getSecondaryAlternates().get(0));
    }

    @Test
    public void testNormalizeMNV() throws NonStandardCompliantSampleField {
        normalizer.setDecomposeMNVs(true);
        normalizer.setGenerateReferenceBlocks(false);
        Variant variant = generateVariantWithFormat("X:100:ATG:ACC", "GT:AD:GL", "S01", "0/0", "1,2", "1,2,3");
        List<Variant> normalize = normalizer.normalize(Collections.singletonList(variant), false);
        for (Variant v : normalize) {
            System.out.println(v);
        }
        for (Variant v : normalize) {
            System.out.println(v.toJson());
        }
        assertEquals(2, normalize.size());
        Map<String, Variant> map = normalize.stream().collect(Collectors.toMap(Variant::toString, v -> v));

        assertEquals("1,2,3", map.get("X:101:T:C").getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,2,3", map.get("X:102:G:C").getStudies().get(0).getSampleData("S01", "GL"));

        assertEquals("1,2", map.get("X:101:T:C").getStudies().get(0).getSampleData("S01", "AD"));
        assertEquals("1,2", map.get("X:102:G:C").getStudies().get(0).getSampleData("S01", "AD"));
    }

    @Test
    public void testNormalizeMultiAllelicMNV() throws NonStandardCompliantSampleField {
        normalizer.setDecomposeMNVs(true);
        normalizer.setGenerateReferenceBlocks(false);
        Variant variant = generateVariantWithFormat("X:100:ACGT:TCGA,ATGT", "GT:AD:GL", "S01", "0/0", "1,2,3", "1,2,3,4,5,6");
        List<Variant> normalize = normalizer.normalize(Collections.singletonList(variant), false);

        assertEquals(3, normalize.size());
        Map<String, Variant> map = normalize.stream().collect(Collectors.toMap(Variant::toString, v -> v));

        assertEquals("1,2,3,4,5,6", map.get("X:100:A:T").getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,2,3,4,5,6", map.get("X:103:T:A").getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,4,6,2,5,3", map.get("X:101:C:T").getStudies().get(0).getSampleData("S01", "GL"));

        assertEquals("1,2,3", map.get("X:100:A:T").getStudies().get(0).getSampleData("S01", "AD"));
        assertEquals("1,2,3", map.get("X:103:T:A").getStudies().get(0).getSampleData("S01", "AD"));
        assertEquals("1,3,2", map.get("X:101:C:T").getStudies().get(0).getSampleData("S01", "AD"));

    }

    @Test
    @Ignore("FIXME: Normalizer should not return duplicated variants")
    public void testNormalizeMultiAllelicDuplicatedMNV() throws NonStandardCompliantSampleField {
        normalizer.setDecomposeMNVs(true);
        normalizer.setGenerateReferenceBlocks(false);
        Variant variant = generateVariantWithFormat("X:100:ACGT:TCGA,ATGA", "GT:AD:GL", "S01", "1/2", "1,2,3", "1,2,3,4,5,6");

        List<Variant> normalize = normalizer.normalize(Collections.singletonList(variant), false);
        for (Variant v : normalize) {
            System.out.println(v);
        }
        for (Variant v : normalize) {
            System.out.println(v.toJson());
        }

        // FIXME
        assertEquals(3, normalize.size());
    }

    @Test
    public void testNormalizeNoVariation() throws NonStandardCompliantSampleField {
        Variant variant = new Variant("2", 10, 1000, "A", ".");

        assertEquals(VariantType.NO_VARIATION, variant.getType());
        Variant normalizedVariant = normalizer.normalize(Collections.singletonList(variant), false).get(0);
        assertNotSame(variant, normalizedVariant);
        assertEquals(variant, normalizedVariant);
    }

    @Test
    public void testNormalizeNoVariationWithSamples() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2:10-1000:A:.")
                .setStudyId("study")
                .setSampleDataKeys("GT")
                .addSample("sample", "0/0")
                .build();

        assertEquals(VariantType.NO_VARIATION, variant.getType());
        Variant normalizedVariant = normalizer.normalize(Collections.singletonList(variant), false).get(0);
        assertEquals(variant, normalizedVariant);
        assertEquals(VariantType.NO_VARIATION, variant.getType());
    }

    @Test
    public void testNormalizeNoVariationSymbolic() throws NonStandardCompliantSampleField {
        Variant variant = new Variant("2", 10, 1000, "A", "<NON_REF>");

        Variant normalizedVariant = normalizer.normalize(Collections.singletonList(variant), false).get(0);
        assertEquals("<*>", normalizedVariant.getAlternate());
        variant.setAlternate("<*>");
        assertNotSame(variant, normalizedVariant);
        assertEquals(variant, normalizedVariant);
    }

    @Test
    public void testNormalizeNoVariationSymbolicWithSample() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2", 10, 1000, "A", "<NON_REF>")
                .setStudyId("study")
                .setFileId("file")
                .setSampleDataKeys("GT")
                .addSample("sample", "0/0")
                .build();

        Variant normalizedVariant = normalizer.normalize(Collections.singletonList(variant), false).get(0);
        assertEquals("<*>", normalizedVariant.getAlternate());
        variant.getStudies().get(0).getFile(0).setCall(new OriginalCall(variant.toString(), 0));
        variant.setAlternate("<*>");
        assertNotSame(variant, normalizedVariant);
        assertEquals(variant, normalizedVariant);
    }

    @Test
    public void testNormalizeMultiallelicNoVariationSymbolic() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2", 10, 10, "A", "C,<*>").setStudyId("s").setFileId("f")
                .setSampleDataKeys(Collections.emptyList())
                .setSamples(Collections.emptyList()).build();

        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(1, variants.size());
        Variant normalizedVariant = variants.get(0);
        String call = normalizedVariant.getStudies().get(0).getFiles().get(0).getCall().getVariantId();
        assertEquals("2:10:A:C,<*>", call);
        variant.getStudies().get(0).getFiles().get(0).setCall(new OriginalCall("2:10:A:C,<*>", 0));
        assertEquals(variant.toJson(), normalizedVariant.toJson());
    }

    @Test
    public void testNormalizeMultiallelicNoVariationSymbolicNonRef() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2", 10, 10, "A", "C,<NON_REF>").setStudyId("s").setFileId("f")
                .setSampleDataKeys(Collections.emptyList())
                .setSamples(Collections.emptyList()).build();

        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(1, variants.size());
        Variant normalizedVariant = variants.get(0);
        String call = normalizedVariant.getStudies().get(0).getFiles().get(0).getCall().getVariantId();
        assertEquals("2:10:A:C,<NON_REF>", call);
        variant.getStudies().get(0).getFiles().get(0).setCall(new OriginalCall("2:10:A:C,<NON_REF>", 0));
        variant.getStudies().get(0).getSecondaryAlternates().get(0).setAlternate("<*>");
        assertEquals(variant.toJson(), normalizedVariant.toJson());
    }

    @Test
    public void testNormalizeSymbolicNonRefAlone() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2", 10, 10, "A", "<NON_REF>").setStudyId("s")
                .setFileId("f")
                .setSampleDataKeys(Collections.emptyList())
                .setSamples(Collections.emptyList()).build();


        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(1, variants.size());
        Variant normalizedVariant = variants.get(0);

        Variant expected = Variant.newBuilder("2", 10, 10, "A", "<*>").setStudyId("s")
                .setFileId("f")
                .setSampleDataKeys(Collections.emptyList())
                .setCall(new OriginalCall("2:10:A:<NON_REF>", 0))
                .setSamples(Collections.emptyList()).build();
        assertEquals(expected.toJson(), normalizedVariant.toJson());
    }

    @Test
    public void testNormalizeMultiallelicSpanningDeletion() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2", 10, 10, "A", "C,*").setStudyId("s")
                .setFileId("f")
                .setSampleDataKeys("GT", "AD")
                .addSample("S1", "1/2", "0,1,2").build();

        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(1, variants.size());
        Variant normalizedVariant = variants.get(0);
        String call = normalizedVariant.getStudies().get(0).getFiles().get(0).getCall().getVariantId();
        assertEquals("2:10:A:C,*", call);
        variant.getStudies().get(0).getFiles().get(0).setCall(new OriginalCall("2:10:A:C,*", 0));
        assertEquals(variant.toJson(), normalizedVariant.toJson());
    }

    @Test
    public void testNormalizeMultiallelicSpanningDeletionInverseOrder() throws NonStandardCompliantSampleField {
        Variant spanDelFirst = Variant.newBuilder("2", 10, 10, "A", "*,C").setStudyId("s")
                .setFileId("f")
                .setSampleDataKeys("GT", "AD")
                .addSample("S1", "1/2", "0,1,2").build();

        List<Variant> variants = normalizer.normalize(Collections.singletonList(spanDelFirst), false);
        assertEquals(1, variants.size());
        Variant normalizedVariant = variants.get(0);
        String call = normalizedVariant.getStudies().get(0).getFiles().get(0).getCall().getVariantId();
        assertEquals("2:10:A:*,C", call);

        Variant expected = Variant.newBuilder("2", 10, 10, "A", "C,*").setStudyId("s")
                .setFileId("f")
                .setSampleDataKeys("GT", "AD")
                .setCall(new OriginalCall("2:10:A:*,C", 1))
                .addSample("S1", "2/1", "0,2,1").build();
        assertEquals(expected.toJson(), normalizedVariant.toJson());
    }

    @Test
    public void testNormalizeSpanningDeletionAlone() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("2", 10, 10, "A", "*").setStudyId("s")
                .setFileId("f")
                .setSampleDataKeys("GT", "AD")
                .addSample("S1", "1/2", "0,1,2").build();

        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(1, variants.size());
        Variant normalizedVariant = variants.get(0);
        assertEquals(variant.toJson(), normalizedVariant.toJson());
    }

    @Test
    public void testNormalize() throws NonStandardCompliantSampleField {
//        Variant v1 = new Variant("1:100:AATATATATATAT:AATATATATATATAT");
        Variant v1 = newVariant(100, "AATATATATATAT", Arrays.asList("AATATATATATATAT","A"), "1");
        System.out.println("v1.getStudies().get(0) = " + v1.getStudies().get(0));
        StudyEntry se = v1.getStudies().get(0);
        Map<String, Integer> map = new HashMap<>();
        map.put("S1",0);
        se.setSamplesPosition(map);
        se.setSamples(Collections.singletonList(new SampleEntry(null, null, Collections.singletonList("1/2"))));
        System.out.println("v1.getStudies().get(0) = " + v1.getStudies().get(0));
        List<AlternateCoordinate> secalt = se.getSecondaryAlternates();
        System.out.println("v1.toJson() = " + v1.toJson());
        List<Variant> normalize = normalizer.normalize(Collections.singletonList(v1), false);
        System.out.println("normalize = " + normalize);
        normalize.stream().forEach(v -> System.out.println(v.getStudies().get(0).getFiles().get(0).getCall()));
        Set<String> gts = normalize.stream().map(v -> v.getStudies().get(0).getSampleData("S1", "GT")).collect
                (Collectors.toSet());
        assertEquals(new HashSet<>(Arrays.asList("0/0","1/2","2/1")), gts);

    }

    @Test
    public void testNormalizeMultiAllelicSnpIndel() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "C", "T,CA", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(100, 100, 0, "C", "T"),
                new VariantNormalizer.VariantKeyFields(101, 100, 1, "", "A")));
    }

    @Test
    public void testNormalizeMultiAllelicSnpIndel2() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "TACC", "T,TATC", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(101, 103, 0, "ACC", ""),
                new VariantNormalizer.VariantKeyFields(102, 102, 1, "C", "T")),
                Arrays.asList(new VariantNormalizer.VariantKeyFields(100, 100, "T", "")));
    }

    @Test
    public void testNormalizeMultiAllelicMultipleDeletions() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "GTACC", "GCC,G", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(101, 102, 0, "TA", ""),
                new VariantNormalizer.VariantKeyFields(101, 104, 1, "TACC", "")),
                Arrays.asList(new VariantNormalizer.VariantKeyFields(100, 100, "G", "")));
    }

    @Test
    public void testNormalizeMultiAllelicMultipleInsertions() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "G", "GCC,GCCTT", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(101, 100, 0, "", "CC"),
                new VariantNormalizer.VariantKeyFields(101, 100, 1, "", "CCTT")),
                Arrays.asList(new VariantNormalizer.VariantKeyFields(100, 100, "G", "")));
    }

    @Test
    public void testNormalizeMultiAllelicOverlapedReferenceRegions() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "AAAA", "TAAA,AAAT", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(100, 100, 0, "A", "T"),
                new VariantNormalizer.VariantKeyFields(103, 103, 1, "A", "T")),
                Arrays.asList(new VariantNormalizer.VariantKeyFields(101, 102, "A", "")));
    }

    @Test
    public void testNormalizeMultiAllelicMultipleDeletionsAndInsertions() throws NonStandardCompliantSampleField {
        testSampleNormalization(681, "TACACACACAC", "TACACACACACAC,TACAC,TACACAC,TACACACAC,TAC", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(682, 681, 0, "", "AC"),
                new VariantNormalizer.VariantKeyFields(682, 687, 1, "ACACAC", ""),
                new VariantNormalizer.VariantKeyFields(682, 685, 2, "ACAC", ""),
                new VariantNormalizer.VariantKeyFields(682, 683, 3, "AC", ""),
                new VariantNormalizer.VariantKeyFields(682, 689, 4, "ACACACAC", "")),
                Arrays.asList(
                        new VariantNormalizer.VariantKeyFields(681, 681, "T", ""),
                        new VariantNormalizer.VariantKeyFields(690, 691, "A", "")));
    }

    @Test
    public void testNormalizeAlleles() throws NonStandardCompliantSampleField {
        Variant variant = generateVariantWithFormat("6:109522683:T:A,G", "GT", "S01", "1/2");
        normalizer.setNormalizeAlleles(true);
        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(2, variants.size());
        assertEquals("1/2", variants.get(0).getStudies().get(0).getSampleData("S01", "GT"));
        assertEquals("1/2", variants.get(1).getStudies().get(0).getSampleData("S01", "GT"));
    }

    @Test
    public void testNormalizeMultiallelic() throws NonStandardCompliantSampleField {
        // Sort variants even when the main allele has a start position after the secondary alternate
        Variant variant = generateVariantWithFormat("22:16349650:G:GT,T", "GT", "S01", "0/2", "S02", "0/1");
        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(2, variants.size());

        assertEquals("22:16349650:G:T", variants.get(0).toString());
        assertEquals("0/1", variants.get(0).getStudies().get(0).getSampleData("S01", "GT"));
        assertEquals("0/2", variants.get(0).getStudies().get(0).getSampleData("S02", "GT"));

        assertEquals("22:16349651:-:T", variants.get(1).toString());
        assertEquals("0/2", variants.get(1).getStudies().get(0).getSampleData("S01", "GT"));
        assertEquals("0/1", variants.get(1).getStudies().get(0).getSampleData("S02", "GT"));

    }

    @Test
    public void testMultiSNP() throws NonStandardCompliantSampleField {
        //6       109522683       .       TTTTT   TTTAT,TATTT
        //8       32269959        .       TATATAT TATACAT,TACATAT
        Variant variant = generateVariantWithFormat("6:109522683:TTTTT:TTTAT,TATTT", "GT", "S01", "1/2");

        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), true);
        variants.forEach(v -> System.out.println(v.toJson()));

        assertEquals("6:109522683:T:.", variants.get(0).toString());
        assertEquals(VariantType.NO_VARIATION, variants.get(0).getType());
        assertEquals(1, variants.get(0).getLength().intValue());

        assertEquals("6:109522684:T:A", variants.get(1).toString());
        assertEquals(VariantType.SNV, variants.get(1).getType());
        assertEquals(1, variants.get(1).getLength().intValue());

        assertEquals("6:109522685:T:.", variants.get(2).toString());
        assertEquals(VariantType.NO_VARIATION, variants.get(2).getType());
        assertEquals(1, variants.get(2).getLength().intValue());

        assertEquals("6:109522686:T:A", variants.get(3).toString());
        assertEquals(VariantType.SNV, variants.get(3).getType());
        assertEquals(1, variants.get(3).getLength().intValue());

        assertEquals("6:109522687:T:.", variants.get(4).toString());
        assertEquals(VariantType.NO_VARIATION, variants.get(4).getType());
        assertEquals(1, variants.get(4).getLength().intValue());
    }

    @Test
    public void testNormalizeMultiAllelicPL() throws NonStandardCompliantSampleField {
        Variant variant = generateVariantWithFormat("X:100:A:T", "GT:GL", "S01", "0/0", "1,2,3", "S02", "0", "1,2");

        List<Variant> normalize1 = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals("1,2,3", normalize1.get(0).getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,2", normalize1.get(0).getStudies().get(0).getSampleData("S02", "GL"));

        Variant variant2 = generateVariantWithFormat("X:100:A:T,C", "GT:GL", "S01", "0/0", "1,2,3,4,5,6", "S02", "A", "1,2,3");
        List<Variant> normalize2 = normalizer.normalize(Collections.singletonList(variant2), false);
        assertEquals("1,2,3,4,5,6", normalize2.get(0).getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,4,6,2,5,3", normalize2.get(1).getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,2,3", normalize2.get(0).getStudies().get(0).getSampleData("S02", "GL"));
        assertEquals("1,3,2", normalize2.get(1).getStudies().get(0).getSampleData("S02", "GL"));

        Variant variant3 = generateVariantWithFormat("X:100:A:T,C,G", "GT:GL", "S01", "0/0", "1,2,3,4,5,6,7,8,9,10", "S02", "A", "1,2,3,4");
        Map<String, Variant> normalize3 = normalizer.normalize(Collections.singletonList(variant3), false)
                .stream().collect(Collectors.toMap(Variant::getAlternate, v -> v));

        assertEquals("1,2,3,4,5,6,7,8,9,10", normalize3.get("T").getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,4,6,2,5,3,7,9,8,10", normalize3.get("C").getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,7,10,2,8,3,4,9,5,6", normalize3.get("G").getStudies().get(0).getSampleData("S01", "GL"));
        assertEquals("1,2,3,4", normalize3.get("T").getStudies().get(0).getSampleData("S02", "GL"));
        assertEquals("1,3,2,4", normalize3.get("C").getStudies().get(0).getSampleData("S02", "GL"));
        assertEquals("1,4,2,3", normalize3.get("G").getStudies().get(0).getSampleData("S02", "GL"));

    }

    @Test
    public void testCNVsNormalization() throws Exception {
        Variant variant = newVariantBuilder(100, 200, "C", Collections.singletonList("<CN0>"), "2")
                .addFileData("CIPOS", "-14,50")
                .addFileData("CIEND", "-50,11")
                .addSample("HG00096", "0|0")
                .build();

        List<Variant> normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
        assertEquals(1, normalizedVariantList.size());
        assertEquals(new StructuralVariation(86, 150, 150, 211, 0, null, null,
                StructuralVariantType.COPY_NUMBER_LOSS, null), normalizedVariantList.get(0).getSv());
        // Normalize CNV alternate
        assertEquals("<CN0>", normalizedVariantList.get(0).getAlternate());
        assertEquals("1:86<100<150-150<200<211:C:<CN0>", normalizedVariantList.get(0).getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(0, normalizedVariantList.get(0).getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());
    }

    @Test
    public void testVNCNormalizationMultiallelic() throws NonStandardCompliantSampleField {
        Variant variant = Variant.newBuilder("1", 100, 200, "C", "<CN0>,<CN2>,<CN3>,<CN4>")
                .setStudyId("1")
                .setFileId("1")
                .setSampleDataKeys("GT")
                .addSample("HG00096", "0|1")
                .addSample("HG00097", "0|2")
                .addSample("HG00098", "0|3")
                .addSample("HG00099", "0|4")
                .addFileData("AF", "0.1,0.2,0.3,0.4")
                .addFileData("CIPOS", "-10,10")
                .build();
        List<Variant> normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
        assertEquals(4, normalizedVariantList.size());
        assertEquals(new StructuralVariation(90, 110, null, null, 0,
                null, null, StructuralVariantType.COPY_NUMBER_LOSS, null),
                normalizedVariantList.get(0).getSv());
        assertEquals(new StructuralVariation(90, 110, null, null, 2,
                null, null, null, null), normalizedVariantList.get(1).getSv());
        assertEquals(new StructuralVariation(90, 110, null, null, 3,
                null, null, StructuralVariantType.COPY_NUMBER_GAIN, null), normalizedVariantList.get(2).getSv());
        assertEquals(new StructuralVariation(90, 110, null, null, 4,
                null, null, StructuralVariantType.COPY_NUMBER_GAIN, null), normalizedVariantList.get(3).getSv());

        assertEquals("1:90<100<110-200:C:<CN0>,<CN2>,<CN3>,<CN4>", normalizedVariantList.get(0).getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(0, normalizedVariantList.get(0).getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());
        assertEquals(VariantType.COPY_NUMBER_LOSS, normalizedVariantList.get(0).getType());
        assertEquals("1:90<100<110-200:C:<CN0>,<CN2>,<CN3>,<CN4>", normalizedVariantList.get(1).getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(1, normalizedVariantList.get(1).getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());
        assertEquals(VariantType.COPY_NUMBER, normalizedVariantList.get(1).getType());
        assertEquals("1:90<100<110-200:C:<CN0>,<CN2>,<CN3>,<CN4>", normalizedVariantList.get(2).getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(2, normalizedVariantList.get(2).getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());
        assertEquals(VariantType.COPY_NUMBER_GAIN, normalizedVariantList.get(2).getType());
        assertEquals("1:90<100<110-200:C:<CN0>,<CN2>,<CN3>,<CN4>", normalizedVariantList.get(3).getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(3, normalizedVariantList.get(3).getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());
        assertEquals(VariantType.COPY_NUMBER_GAIN, normalizedVariantList.get(3).getType());

        for (Variant v : normalizedVariantList) {
            assertEquals(101, v.getStart().intValue());
        }
    }

    @Test
    public void testCNVsNormalizationCopyNumber() throws NonStandardCompliantSampleField {
        Variant variant;
        List<Variant> normalizedVariantList;
        variant = newVariantBuilder(100, 200, "C", Arrays.asList("<CNV>"), "2")
                .setSampleDataKeys("GT", "CN")
                .addSample("HG00096", "0|1","3")
                .build();
        normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
        assertEquals(1, normalizedVariantList.size());
        Variant normalizedVariant = normalizedVariantList.get(0);
        assertEquals(new StructuralVariation(null, null, null, null, 3, null, null,
                StructuralVariantType.COPY_NUMBER_GAIN, null), normalizedVariant.getSv());
        // Normalize CNV alternate
        assertEquals("<CN3>", normalizedVariant.getAlternate());
        assertEquals(101, normalizedVariant.getStart().intValue());
        assertEquals("", normalizedVariant.getReference());
        assertEquals("1:100-200:C:<CNV>", normalizedVariant.getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(0, normalizedVariant.getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());

    }

    @Test
    @Ignore
    // TODO: This should work!
    public void testCNVsNormalizationCopyNumberMultiSample() throws NonStandardCompliantSampleField {
        Variant variant;
        List<Variant> normalizedVariantList;
        variant = newVariant(100, 200, "C", Arrays.asList("<CNV>"), "2");
        variant.getStudies().get(0).addSampleDataKey("CN");
        variant.getStudies().get(0).addSampleData("HG00096", Arrays.asList("0|1","3"));
        variant.getStudies().get(0).addSampleData("HG00097", Arrays.asList("0|1","2"));
        normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(2, normalizedVariantList.size());
        assertEquals(new StructuralVariation(100, 100, 200, 200, 3,
                null, null, null, null), normalizedVariantList.get(0).getSv());
        assertEquals(new StructuralVariation(100, 100, 200, 200, 2,
                null, null, null, null), normalizedVariantList.get(1).getSv());
    }

    @Test
    public void testNormalizeSV() throws NonStandardCompliantSampleField {
        String alt = "C" + StringUtils.repeat('A', 50);

        Variant variant = newVariant(100, "C", alt);
        variant.getStudies().get(0).addSampleData("HG00096", Collections.singletonList("0|1"));
        assertEquals(VariantType.INSERTION, variant.getType());
        assertEquals(51, variant.getLength().intValue());

        normalizer.setGenerateReferenceBlocks(false);
        List<Variant> normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
        assertEquals(1, normalizedVariantList.size());
        assertEquals(50, normalizedVariantList.get(0).getLength().intValue());
        assertEquals(VariantType.INDEL, normalizedVariantList.get(0).getType());
    }

    @Test
    public void testNormalizeDEL() throws NonStandardCompliantSampleField {

        Variant variant = newVariant(100, 200, "N", Collections.singletonList("<DEL>"), STUDY_ID);
        List<Variant> normalized = normalizer.normalize(Collections.singletonList(variant), false);

        assertEquals(1, normalized.size());
        assertEquals(101, normalized.get(0).getStart().intValue());
        assertEquals(200, normalized.get(0).getEnd().intValue());
        assertEquals(new StructuralVariation(), normalized.get(0).getSv());
        System.out.println(normalized.get(0).toJson());
    }

    @Test
    public void testNormalizeINS() throws NonStandardCompliantSampleField {

        String seq = "ACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTGACTG";
        Variant variant = newVariantBuilder(100, 100, "N", Collections.singletonList("<INS>"), STUDY_ID)
                .addFileData("SVINSSEQ", seq)
                .build();
        List<Variant> list = new VariantNormalizer().normalize(Collections.singletonList(variant), false);

        assertEquals(1, list.size());
        Variant normalized = list.get(0);
        assertEquals(101, normalized.getStart().intValue());
        assertEquals(100, normalized.getEnd().intValue());
        assertEquals(seq.length(), normalized.getLength().intValue());
        assertEquals(seq.length(), normalized.getLengthAlternate().intValue());
        assertEquals(0, normalized.getLengthReference().intValue());
        assertEquals("", normalized.getReference());
        assertEquals(seq, normalized.getAlternate());
        assertEquals(new StructuralVariation(), normalized.getSv());
        assertEquals("1:100-100:N:<INS>", normalized.getStudies().get(0).getFiles().get(0).getCall().getVariantId());
        assertEquals(0, normalized.getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex().intValue());
    }

    @Test
    public void testNormalizeSvToIndel() throws NonStandardCompliantSampleField {
        // This variant, after normalizing, looses the StructuralVariant field
        String seq = "C" + StringUtils.repeat("A", Variant.SV_THRESHOLD);
        Variant variant = newVariant(100, 101, "C", seq);
        assertEquals(VariantType.INSERTION, variant.getType());
        assertEquals(Variant.SV_THRESHOLD + 1, variant.getLengthAlternate().intValue());
        assertNotNull(variant.getSv());

        Variant normVar = new VariantNormalizer().normalize(Collections.singletonList(variant), false).get(0);
        assertEquals(VariantType.INDEL, normVar.getType());
        assertEquals(Variant.SV_THRESHOLD, normVar.getLengthAlternate().intValue());
        assertNull(normVar.getSv());

        // Check that the original variant has not been modified, and check again, but reusing the input variant
        assertEquals(VariantType.INSERTION, variant.getType());
        assertEquals(Variant.SV_THRESHOLD + 1, variant.getLengthAlternate().intValue());
        assertNotNull(variant.getSv());
        Variant normVarReuse = new VariantNormalizer().normalize(Collections.singletonList(variant), true).get(0);
        assertEquals(VariantType.INDEL, normVarReuse.getType());
        assertEquals(Variant.SV_THRESHOLD, normVarReuse.getLengthAlternate().intValue());
        assertNull(normVarReuse.getSv());

    }

    @Test
    public void testNormalizeBND() throws NonStandardCompliantSampleField {
        normalizeBnd(newVariant(101, 100, "", ".[9:10["),  newVariant(100, 99, "A", "A[chr9:10["));
        normalizeBnd(newVariant(100, 99, "", "[22:10[."),  newVariant(100, 99, "A", "[chr22:10[A"));
        normalizeBnd(newVariant(101, 100, "", ".]9:10]"),  newVariant(100, 99, "A", "A]chr9:10]"));
        normalizeBnd(newVariant(100, 99, "", "]22:10]."),  newVariant(100, 99, "A", "]chr22:10]A"));
        normalizeBnd(newVariant(100, 99, "", "]22:10]NNN"),  newVariant(100, 99, "A", "]chr22:10]NNNA"));

        normalizeBnd(newVariant(100, 99, "", "[1:10[T"),  newVariant(100, 99, "A", "[1:10[TA"));
        normalizeBnd(newVariant(100, 99, "", "[1:10[T"),  newVariant(100, 99, "AC", "[1:10[TAC"));

        normalizeBnd(newVariant(100, 99, "TAC", "[1:10[AC"),  newVariant(100, 99, "TAC", "[1:10[AC"));
        normalizeBnd(newVariant(100, 99, "TAC", "TA[1:10["),  newVariant(100, 99, "TAC", "TA[1:10["));
    }

    private void normalizeBnd(Variant expectedVariant, Variant variant) throws NonStandardCompliantSampleField {
        System.out.println("---");
        boolean expectsNormalization = !expectedVariant.equals(variant);

        System.out.println(" - Actual");
        System.out.println("    " + variant.toString());
        System.out.println("    " + variant.toJson());
        System.out.println(" - Expected");
        System.out.println("    " + expectedVariant.toString());
        System.out.println("    " + expectedVariant.toJson());
        System.out.println(" - Normalized (same = " + !expectsNormalization + ")");
        List<Variant> normalized = normalizer.normalize(Collections.singletonList(variant), false);

        for (Variant v : normalized) {
            System.out.println("    " + v.toString());
            System.out.println("    " + v.toJson());
            if (expectsNormalization) {
                assertNotNull(v.getStudies().get(0).getFiles().get(0).getCall());
                v.getStudies().get(0).getFiles().get(0).setCall(null);
            }
            assertEquals(expectedVariant, v);
        }
    }


    @Test
    public void testNormalizeBND2() throws NonStandardCompliantSampleField {


        for (String alternate : Arrays.asList(
                "A[chr9:10[",
                "A]chr9:10]",
                "[chr22:10[A",
                "]chr22:10]A",
                "]chr22:10]NNNA")) {
            System.out.println("---");
            Variant variant = newVariant(100, "A", alternate);
            System.out.println(variant.toString());
            List<Variant> normalized = normalizer.normalize(Collections.singletonList(variant), false);

            for (Variant v : normalized) {
                System.out.println(v.toString());
                System.out.println(v.toJson());
            }
        }

    }

}
