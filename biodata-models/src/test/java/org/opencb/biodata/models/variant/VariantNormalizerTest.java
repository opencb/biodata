package org.opencb.biodata.models.variant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.commons.test.GenericTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void test1() {
        l(10, "AAA", "CCA");
        l(20, "AAAAT", "ACCAT");
        l(30, "A", "AC");
        l(40, "A", "CA");
        l(50, "TCA", "CA");
        l(60, "TCA", "TC");
    }

    private void l(int position, String ref, String alt) {
        System.out.println("orig: " + position + ":" + ref + ":" + alt);
        System.out.println(normalizer.createVariantsFromNoEmptyRefAlt(position, ref, alt));
        System.out.println("-----------");
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
        List<Variant> variants = normalizer.apply(Collections.singletonList(variant));
        variants.forEach(System.out::println);
        variants.forEach((x) -> System.out.println(x.toJson()));

        Variant snp = variants.get(1);
        Variant indel = variants.get(2);

        assertEquals("1:101:C:T", snp.toString());
        assertEquals("1:105:T:-", indel.toString());


        assertEquals(true, snp.getStudies().get(0).getFormat().contains("PS"));
        assertEquals(true, indel.getStudies().get(0).getFormat().contains("PS"));

        assertEquals(0, snp.getStudies().get(0).getSecondaryAlternates().size());
        assertEquals(0, indel.getStudies().get(0).getSecondaryAlternates().size());

        assertEquals("1:100:ACTCGTAAA:ATTCGAAA", snp.getStudies().get(0).getSampleData("S1", "PS"));
        assertEquals("1:100:ACTCGTAAA:ATTCGAAA", indel.getStudies().get(0).getSampleData("S1", "PS"));
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
        testSampleNormalization(100, "C", "CA,T", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(101, 101, 0, "", "A"),
                new VariantNormalizer.VariantKeyFields(100, 100, 1, "C", "T")));
    }

    @Test
    public void testNormalizeMultiAllelicSnpIndel2() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "TACC", "TATC,T", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(102, 102, 0, "C", "T"),
                new VariantNormalizer.VariantKeyFields(101, 103, 1, "ACC", "")));
    }

    @Test
    public void testNormalizeMultiAllelicMultipleDeletions() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "GTACC", "GCC,G", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(101, 102, 0, "TA", ""),
                new VariantNormalizer.VariantKeyFields(101, 104, 1, "TACC", "")));
    }

    @Test
    public void testNormalizeMultiAllelicMultipleInsertions() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "G", "GCC,GCCTT", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(101, 102, 0, "", "CC"),
                new VariantNormalizer.VariantKeyFields(101, 104, 1, "", "CCTT")));
    }

    @Test
    public void testNormalizeMultiAllelicOverlapedReferenceRegions() throws NonStandardCompliantSampleField {
        testSampleNormalization(100, "AAAA", "TAAA,AAAT", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(100, 100, 0, "A", "T"),
                new VariantNormalizer.VariantKeyFields(103, 103, 1, "A", "T")));
    }

    @Test
    public void testNormalizeMultiAllelicMultipleDeletionsAndInsertions() throws NonStandardCompliantSampleField {
        testSampleNormalization(681, "TACACACACAC", "TACACACACACAC,TACAC,TACACAC,TACACACAC,TAC", Arrays.asList(
                new VariantNormalizer.VariantKeyFields(682, 683, 0, "", "AC"),
                new VariantNormalizer.VariantKeyFields(682, 687, 1, "ACACAC", ""),
                new VariantNormalizer.VariantKeyFields(682, 685, 2, "ACAC", ""),
                new VariantNormalizer.VariantKeyFields(682, 683, 3, "AC", ""),
                new VariantNormalizer.VariantKeyFields(682, 689, 4, "ACACACAC", "")));
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

        List<String> altsList = Arrays.asList(altsCsv.split(","));
        String studyId = "2";
        Variant variantToNormalize = newVariant(position, ref, altsList, studyId);
        List<Variant> variants = normalizer.normalize(Collections.singletonList(variantToNormalize), false);
        variants.forEach((x) -> System.out.println(x.toJson()));

        int numAllele = 0;
        for (int i = 0; i < variants.size(); i++) {
            Variant v = variants.get(i);
            assertTrue(v.getStart() <= v.getEnd());
            if (v.getType().equals(VariantType.NO_VARIATION)) {
                assertEquals(0, v.getStudy(studyId).getSecondaryAlternates().size());
                assertEquals("", v.getAlternate());
            } else {
                VariantNormalizer.VariantKeyFields expected = expectedKeyFieldsList.get(numAllele);
                assertEquals(expected.getStart(), v.getStart().intValue());
                assertEquals(expected.getAlternate(), v.getAlternate());
                assertEquals(expected.getReference(), v.getReference());
                assertEquals(expected.getNumAllele(), numAllele);
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
            int alleleCode = numAllele + 1;
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

    private Variant newVariant(int position, String ref, String altsCsv) {
        return newVariant(position, ref, Arrays.asList(altsCsv.split(",")), "2");
    }

    private Variant newVariant(int position, String ref, List<String> altsList, String studyId) {
        Variant variant = new Variant("1", position, ref, altsList.get(0));
        StudyEntry studyEntry = new StudyEntry(studyId, altsList.subList(1, altsList.size())
                .stream()
                .map(s -> new AlternateCoordinate(null, null, null, null, s, variant.getType()))
                .collect(Collectors.toList()), Collections.singletonList("GT"));
        studyEntry.setFileId("1");
        variant.addStudyEntry(studyEntry);
        return variant;
    }
}
