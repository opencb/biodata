package org.opencb.biodata.models.variant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.StructuralVariation;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.commons.test.GenericTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.opencb.biodata.models.variant.VariantTestUtils.generateVariantWithFormat;

/**
 * Created on 26/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantNormalizerTest extends GenericTest {

    private VariantNormalizer normalizer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        normalizer = new VariantNormalizer();
        normalizer.setGenerateReferenceBlocks(true);
    }

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
        testSampleNormalization("1", 100, "ATC", "ACCC", 101, 102, "T", "CC");
    }

    @Test
    public void testNormalizeSamplesDataRightInsertion() throws NonStandardCompliantSampleField {
        // C -> AC  === . -> A
        testSampleNormalization("1", 100, "C", "AC", 100, "", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftInsertion() throws NonStandardCompliantSampleField {
        // C -> CA  === . -> A
        testSampleNormalization("1", 100, "C", "CA", 101, "", "A");
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

        assertTrue(snp.getStudies().get(0).getFormat().contains("PS"));
        assertTrue(indel.getStudies().get(0).getFormat().contains("PS"));
        assertEquals("1:100:ACTCGTAAA:ATTCGAAA", snp.getStudies().get(0).getSampleData("S1", "PS"));
        assertEquals("1:100:ACTCGTAAA:ATTCGAAA", indel.getStudies().get(0).getSampleData("S1", "PS"));

        for (Variant refBlock : refBlocks) {
            assertFalse(refBlock.getStudies().get(0).getFormat().contains("PS"));
            assertEquals("0/0", refBlock.getStudies().get(0).getSampleData("S1", "GT"));
            assertEquals("0/0", refBlock.getStudies().get(0).getSampleData("S2", "GT"));
            assertEquals("./.", refBlock.getStudies().get(0).getSampleData("S3", "GT"));
            assertEquals("0", refBlock.getStudies().get(0).getSampleData("S4", "GT"));
        }

    }

    @Test
    public void testNormalizeSamplesDataMNV2() throws NonStandardCompliantSampleField {
        normalizer.setDecomposeMNVs(true);
        Variant variant = newVariant(100, "ACTCGTA", "ATTCGA,ACTCCTA");
        variant.getStudies().get(0).addSampleData("S1", Collections.singletonList("0/0"));
        variant.getStudies().get(0).addSampleData("S2", Collections.singletonList("0/1"));
        variant.getStudies().get(0).addSampleData("S3", Collections.singletonList("0/2"));
        System.out.println(variant.toJson());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Unable to resolve");
        List<Variant> variants = normalizer.apply(Collections.singletonList(variant));
//
//        variants.forEach(System.out::println);
//        variants.forEach((v) -> System.out.println(v.toJson()));
//        assertEquals("1:101:C:T", variants.get(0).toString());
//        assertEquals("1:105:T:-", variants.get(1).toString());
//        assertEquals("1:104:G:C", variants.get(2).toString());
//
//        assertEquals(true, variants.get(0).getStudies().get(0).getFormat().contains("PS"));
//        assertEquals(true, variants.get(1).getStudies().get(0).getFormat().contains("PS"));
//        assertEquals(false, variants.get(2).getStudies().get(0).getFormat().contains("PS"));
//
//        assertEquals(????, variants.get(0).getStudies().get(0).getSecondaryAlternates().size());
//        assertEquals(????, variants.get(1).getStudies().get(0).getSecondaryAlternates().size());
//        assertEquals(????, variants.get(2).getStudies().get(0).getSecondaryAlternates().size());
//
//        assertEquals("1:100:ACTCGTA:ATTCGA", variants.get(0).getStudies().get(0).getSampleData("S1", "PS"));
//        assertEquals("1:100:ACTCGTA:ATTCGA", variants.get(1).getStudies().get(0).getSampleData("S1", "PS"));
//        assertEquals("0/2", variants.get(0).getStudies().get(0).getSampleData("S3", "GT"));
//        assertEquals("0/2", variants.get(1).getStudies().get(0).getSampleData("S3", "GT"));
//        assertEquals("0/1", variants.get(2).getStudies().get(0).getSampleData("S3", "GT"));

    }


    @Test
    public void testNormalizeNoVariation() throws NonStandardCompliantSampleField {
        Variant variant = new Variant("2", 10, 1000, "A", "");
        variant.setType(VariantType.NO_VARIATION);

        Variant normalizedVariant = normalizer.normalize(Collections.singletonList(variant), false).get(0);
        assertEquals(variant, normalizedVariant);

    }

    @Test
    public void testNormalizeMultiAllelicSnpIndel() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "C", "T,CA", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(100, 100, 0, "C", "T"),
                new VariantNormalizer.VariantKeyFields(101, 101, 1, "", "A")));
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
                new VariantNormalizer.VariantKeyFields(101, 102, 0, "", "CC"),
                new VariantNormalizer.VariantKeyFields(101, 104, 1, "", "CCTT")),
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
                new VariantNormalizer.VariantKeyFields(682, 683, 0, "", "AC"),
                new VariantNormalizer.VariantKeyFields(682, 687, 1, "ACACAC", ""),
                new VariantNormalizer.VariantKeyFields(682, 685, 2, "ACAC", ""),
                new VariantNormalizer.VariantKeyFields(682, 683, 3, "AC", ""),
                new VariantNormalizer.VariantKeyFields(682, 689, 4, "ACACACAC", "")),
                Arrays.asList(
                        new VariantNormalizer.VariantKeyFields(681, 681, "T", ""),
                        new VariantNormalizer.VariantKeyFields(690, 691, "A", "")));
    }

    @Test
    public void testNormalizeMultiAllelicPL() throws NonStandardCompliantSampleField {
        Variant variant = generateVariantWithFormat("X:100:A:T", "GT:GL", "S01", "0/0", "1,2,3", "S02", "0", "1,2");
        Variant variant2 = generateVariantWithFormat("X:100:A:T,C", "GT:GL", "S01", "0/0", "1,2,3,4,5,6", "S02", "A", "1,2,3");

        List<Variant> normalize1 = normalizer.normalize(Collections.singletonList(variant), false);
        assertEquals(normalize1.get(0).getStudies().get(0).getSampleData("S01", "GL"), "1,2,3");
        assertEquals(normalize1.get(0).getStudies().get(0).getSampleData("S02", "GL"), "1,2");

        List<Variant> normalize2 = normalizer.normalize(Collections.singletonList(variant2), false);
//        assertEquals(normalize2.get(0).getStudies().get(0).getSampleData("S01", "GL"), "1,2,3,4,5,6");
//        assertEquals(normalize2.get(1).getStudies().get(0).getSampleData("S01", "GL"), "1,4,5,2,6,3");
        assertEquals(normalize2.get(0).getStudies().get(0).getSampleData("S02", "GL"), "1,2,3");
        assertEquals(normalize2.get(1).getStudies().get(0).getSampleData("S02", "GL"), "1,3,2");
    }

    private void testSampleNormalization(String chromosome, int position, String ref, String alt,
                                         int normPos, String normRef, String normAlt)
            throws NonStandardCompliantSampleField {
        testSampleNormalization(chromosome, position, ref, alt, normPos, normPos, normRef, normAlt);
    }

    private void testSampleNormalization(String chromosome, int position, String ref, String alt,
                                         int normStart, int normEnd, String normRef, String normAlt)
            throws NonStandardCompliantSampleField {
        List<List<String>> samplesData = Arrays.asList(
                Collections.singletonList(ref + "/" + alt),
                Collections.singletonList(ref + "/" + ref),
                Collections.singletonList(alt + "/" + ref),
                Collections.singletonList(alt + "/" + alt),
                Collections.singletonList("0" + "/" + "1"),
                Collections.singletonList("0" + "/" + "0"),
                Collections.singletonList("1" + "/" + "0"),
                Collections.singletonList("1" + "/" + "1")
        );
        System.out.println("-----------------");
        System.out.println("orig: " + position + ":" + ref + ":" + alt);
        List<VariantNormalizer.VariantKeyFields> list = normalizer.normalize(chromosome, position, ref, alt);
        VariantNormalizer.VariantKeyFields keyFields = null;
        for (VariantNormalizer.VariantKeyFields kf : list) {
            System.out.println(kf);
            if (!kf.isReferenceBlock()) {
                keyFields = kf;
            }
        }
        assertNotNull(keyFields);
        assertEquals(new VariantNormalizer.VariantKeyFields(normStart, normEnd, normRef, normAlt), keyFields);
        samplesData = normalizer.normalizeSamplesData(keyFields, samplesData, Collections.singletonList("GT"), ref,
                Collections.singletonList(alt));

        assertEquals("0/1", samplesData.get(0).get(0));
        assertEquals("0/0", samplesData.get(1).get(0));
        assertEquals("1/0", samplesData.get(2).get(0));
        assertEquals("1/1", samplesData.get(3).get(0));
        assertEquals("0/1", samplesData.get(4).get(0));
        assertEquals("0/0", samplesData.get(5).get(0));
        assertEquals("1/0", samplesData.get(6).get(0));
        assertEquals("1/1", samplesData.get(7).get(0));

    }


    private void testSampleNormalization(int position, String ref, String altsCsv,
                                         List<VariantNormalizer.VariantKeyFields> expectedKeyFieldsList)
            throws NonStandardCompliantSampleField {
        testSampleNormalization(position, ref, altsCsv, expectedKeyFieldsList, null);
    }

    private void testSampleNormalization(int position, String ref, String altsCsv,
                                         List<VariantNormalizer.VariantKeyFields> expectedKeyFieldsList,
                                         List<VariantNormalizer.VariantKeyFields> expectedRefKeyFieldsList)
            throws NonStandardCompliantSampleField {

        List<String> altsList = Arrays.asList(altsCsv.split(","));
        String studyId = "2";
        Variant variantToNormalize = newVariant(position, ref, altsList, studyId);
        List<Variant> variants = normalizer.normalize(Collections.singletonList(variantToNormalize), false);
        variants.forEach((x) -> System.out.println(x.toJson()));

        int numAllele = 0;
        int numRefBlock = 0;
        for (int i = 0; i < variants.size(); i++) {
            Variant v = variants.get(i);
            assertTrue(v.getStart() <= v.getEnd());
            if (v.getType().equals(VariantType.NO_VARIATION)) {
                assertEquals(0, v.getStudy(studyId).getSecondaryAlternates().size());
                assertEquals("", v.getAlternate());
                if (expectedRefKeyFieldsList != null) {
                    VariantNormalizer.VariantKeyFields expected = expectedRefKeyFieldsList.get(numRefBlock);
                    assertEquals(expected.getStart(), v.getStart().intValue());
                    assertEquals(expected.getAlternate(), v.getAlternate());
                    assertEquals(expected.getReference(), v.getReference());
                    assertTrue(v.getStudy(studyId).getSecondaryAlternates().isEmpty());
                }
                numRefBlock++;
            } else {
                VariantNormalizer.VariantKeyFields expected = expectedKeyFieldsList.get(numAllele);
                assertEquals(expected.getStart(), v.getStart().intValue());
                assertEquals(expected.getAlternate(), v.getAlternate());
                assertEquals(expected.getReference(), v.getReference());
                int actual = Integer.parseInt(v.getStudies().get(0).getFiles().get(0).getCall().split(":")[3]);
                assertEquals(expected.getNumAllele(), actual);
                for (AlternateCoordinate alternate : v.getStudy(studyId).getSecondaryAlternates()) {
                    assertNotNull(alternate);
                }
                assertEquals(expectedKeyFieldsList.size() - 1, v.getStudy(studyId).getSecondaryAlternates().size());

                numAllele++;
            }
        }

        List<VariantNormalizer.VariantKeyFields> normalizedKeyFieldsList = normalizer.normalize("1", position, ref, altsList);
        numAllele = 0;
        for (int i = 0; i < normalizedKeyFieldsList.size(); i++) {

            VariantNormalizer.VariantKeyFields normalizedKeyFields = normalizedKeyFieldsList.get(i);
            if (normalizedKeyFields.isReferenceBlock()) {
                continue;
            }
            VariantNormalizer.VariantKeyFields expectedKeyFields = expectedKeyFieldsList.get(numAllele);

            assertEquals(expectedKeyFields, normalizedKeyFields);


            String alt = altsList.get(numAllele);
            int alleleCode = expectedKeyFields.getNumAllele() + 1;
            List<List<String>> normalizedSamplesData;
            final List<List<String>> samplesData = Arrays.asList(
                    Collections.singletonList(ref + "/" + alt),
                    Collections.singletonList(ref + "/" + ref),
                    Collections.singletonList(alt + "/" + ref),
                    Collections.singletonList(alt + "/" + alt),
                    Collections.singletonList("0" + "/" + alleleCode),
                    Collections.singletonList("0" + "/" + "0"),
                    Collections.singletonList(alleleCode + "/" + "0"),
                    Collections.singletonList(alleleCode + "/" + alleleCode),
                    Collections.singletonList(alleleCode + "|" + "0")
            );

            normalizer.setNormalizeAlleles(true);
            normalizedSamplesData = normalizer.normalizeSamplesData(normalizedKeyFields, samplesData, Collections.singletonList("GT"), ref,
                    altsList);

            assertEquals("0/1", normalizedSamplesData.get(0).get(0));
            assertEquals("0/0", normalizedSamplesData.get(1).get(0));
            assertEquals("0/1", normalizedSamplesData.get(2).get(0));
            assertEquals("1/1", normalizedSamplesData.get(3).get(0));
            assertEquals("0/1", normalizedSamplesData.get(4).get(0));
            assertEquals("0/0", normalizedSamplesData.get(5).get(0));
            assertEquals("0/1", normalizedSamplesData.get(6).get(0));
            assertEquals("1/1", normalizedSamplesData.get(7).get(0));
            assertEquals("1|0", normalizedSamplesData.get(8).get(0));

            normalizer.setNormalizeAlleles(false);
            normalizedSamplesData = normalizer.normalizeSamplesData(normalizedKeyFields, samplesData, Collections.singletonList("GT"), ref,
                    altsList);

            assertEquals("0/1", normalizedSamplesData.get(0).get(0));
            assertEquals("0/0", normalizedSamplesData.get(1).get(0));
            assertEquals("1/0", normalizedSamplesData.get(2).get(0));
            assertEquals("1/1", normalizedSamplesData.get(3).get(0));
            assertEquals("0/1", normalizedSamplesData.get(4).get(0));
            assertEquals("0/0", normalizedSamplesData.get(5).get(0));
            assertEquals("1/0", normalizedSamplesData.get(6).get(0));
            assertEquals("1/1", normalizedSamplesData.get(7).get(0));
            assertEquals("1|0", normalizedSamplesData.get(8).get(0));

            numAllele++;
        }


    }

    @Test
    public void testCNVsNormalization() {
        try {

            Variant variant = newVariant(100, 200, "C", Collections.singletonList("<CN0>"), "2");
            variant.getStudies().get(0).getFile("1").getAttributes().put("CIEND", "-50,11");
            variant.getStudies().get(0).getFile("1").getAttributes().put("CIPOS", "-14,50");
            variant.getStudies().get(0).addSampleData("HG00096", Arrays.asList("0|0"));
            List<Variant> normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
            assertEquals(normalizedVariantList.size(), 1);
            assertEquals(normalizedVariantList.get(0).getSv(), new StructuralVariation(86, 150, 150, 211, 0));

            variant = newVariant(100, 200, "C", Arrays.asList("<CN0>", "<CN2>", "<CN3>", "<CN4>"), "2");
            variant.getStudies().get(0).addSampleData("HG00096", Arrays.asList("0|1"));
            variant.getStudies().get(0).addSampleData("HG00097", Arrays.asList("0|2"));
            variant.getStudies().get(0).addSampleData("HG00098", Arrays.asList("0|3"));
            variant.getStudies().get(0).addSampleData("HG00099", Arrays.asList("0|4"));
            normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
            assertEquals(normalizedVariantList.size(), 4);
            assertEquals(normalizedVariantList.get(0).getSv(), new StructuralVariation(100, 100, 200, 200, 0));
            assertEquals(normalizedVariantList.get(1).getSv(), new StructuralVariation(100, 100, 200, 200, 2));
            assertEquals(normalizedVariantList.get(2).getSv(), new StructuralVariation(100, 100, 200, 200, 3));
            assertEquals(normalizedVariantList.get(3).getSv(), new StructuralVariation(100, 100, 200, 200, 4));

            variant = newVariant(100, 200, "C", Arrays.asList("<CNV>"), "2");
            variant.getStudies().get(0).addFormat("CN");
            variant.getStudies().get(0).addSampleData("HG00096", Arrays.asList("0|1","3"));
            normalizedVariantList = normalizer.normalize(Collections.singletonList(variant), true);
            assertEquals(normalizedVariantList.size(), 1);
            assertEquals(normalizedVariantList.get(0).getSv(), new StructuralVariation(100, 100, 200, 200, 3));

        } catch (NonStandardCompliantSampleField nonStandardCompliantSampleField) {
            nonStandardCompliantSampleField.printStackTrace();
        }
    }

    private Variant newVariant(int position, String ref, String altsCsv) {
        return newVariant(position, position, ref, Arrays.asList(altsCsv.split(",")), "2");
    }

    private Variant newVariant(int position, String ref, List<String> altsList, String studyId) {
        return newVariant(position, position, ref, altsList, studyId);
    }

    private Variant newVariant(int position, int end, String ref, List<String> altsList, String studyId) {
        Variant variant;
        // Different constructor calls since the one that does not include the "end" sets the variant.end by making an
        // inference of the variant length
        if (position == end) {
            variant = new Variant("1", position, ref, altsList.get(0));
        } else {
            variant = new Variant("1", position, end, ref, altsList.get(0));
        }
        StudyEntry studyEntry = new StudyEntry(studyId, altsList.subList(1, altsList.size())
                .stream()
                .map(s -> new AlternateCoordinate(null, null, null, null, s, variant.getType()))
                .collect(Collectors.toList()), Collections.singletonList("GT"));
        studyEntry.setFileId("1");
        variant.addStudyEntry(studyEntry);
        return variant;
    }
}
