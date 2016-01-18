package org.opencb.biodata.models.variant;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.commons.test.GenericTest;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created on 26/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantNormalizerTest extends GenericTest {

    private VariantNormalizer normalizer;

    @Before
    public void setUp() throws Exception {
        normalizer = new VariantNormalizer();
    }

    @Test
    public void testNormalizedSamplesDataSame() throws NonStandardCompliantSampleField {
        // C -> A  === C -> A
        testSampleNormalization(100, "C", "A", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData1() throws NonStandardCompliantSampleField {
        // AC -> AA  === C -> A
        testSampleNormalization(100, "AC", "AA", 101, "C", "A");
    }

    @Test
    public void testNormalizeSamplesData2() throws NonStandardCompliantSampleField {
        // CA -> AA  === C -> A
        testSampleNormalization(100, "CA", "AA", 100, "C", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftDeletion() throws NonStandardCompliantSampleField {
        // AC -> C  === A -> .
        testSampleNormalization(100, "AC", "C", 100, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataRightDeletion() throws NonStandardCompliantSampleField {
        // CA -> C  === A -> .
        testSampleNormalization(100, "CA", "C", 101, "A", "");
    }

    @Test
    public void testNormalizeSamplesDataAmbiguousDeletion() throws NonStandardCompliantSampleField {
        // AAA -> A  === AA -> .
        testSampleNormalization(100, "AAA", "A", 100, 101, "AA", "");
    }

    @Test
    public void testNormalizeSamplesDataIndel() throws NonStandardCompliantSampleField {
        // AAA -> A  === AA -> .
        testSampleNormalization(100, "ATC", "ACCC", 101, 102, "T", "CC");
    }

    @Test
    public void testNormalizeSamplesDataRightInsertion() throws NonStandardCompliantSampleField {
        // C -> AC  === . -> A
        testSampleNormalization(100, "C", "AC", 100, "", "A");
    }

    @Test
    public void testNormalizeSamplesDataLeftInsertion() throws NonStandardCompliantSampleField {
        // C -> CA  === . -> A
        testSampleNormalization(100, "C", "CA", 101, "", "A");
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


    private void testSampleNormalization(int position, String ref, String alt,
                                         int normPos, String normRef, String normAlt)
            throws NonStandardCompliantSampleField {
        testSampleNormalization(position, ref, alt, normPos, normPos, normRef, normAlt);
    }

    private void testSampleNormalization(int position, String ref, String alt,
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

        VariantNormalizer.VariantKeyFields keyFields = normalizer.normalize(position, ref, alt);
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

        Variant variant = new Variant("1", position, ref, altsList.get(0));
        String studyId = "2";
        StudyEntry studyEntry = new StudyEntry("1", studyId, altsList.subList(1, altsList.size()), Collections.singletonList("GT"));
        variant.addStudyEntry(studyEntry);
        List<Variant> variants = normalizer.normalize(Collections.singletonList(variant), false);

        for (int i = 0; i < variants.size(); i++) {
            Variant v = variants.get(i);
            VariantNormalizer.VariantKeyFields expected = expectedKeyFieldsList.get(i);
            assertEquals(expected.getStart(), v.getStart().intValue());
            assertEquals(expected.getAlternate(), v.getAlternate());
            assertEquals(expected.getReference(), v.getReference());
            assertEquals(expected.getNumAllele(), i);
            for (AlternateCoordinate alternate : v.getStudy(studyId).getSecondaryAlternates()) {
                assertNotNull(alternate);
            }
        }

        List<VariantNormalizer.VariantKeyFields> keyFieldsList = normalizer.normalize(position, ref, altsList);
        for (int i = 0; i < keyFieldsList.size(); i++) {

            VariantNormalizer.VariantKeyFields keyFields = keyFieldsList.get(i);
            VariantNormalizer.VariantKeyFields expectedKeyFields = expectedKeyFieldsList.get(i);

            assertEquals(expectedKeyFields, keyFields);


            String alt = altsList.get(i);
            int alleleCode = i + 1;
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
            normalizedSamplesData = normalizer.normalizeSamplesData(keyFields, samplesData, Collections.singletonList("GT"), ref,
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
            normalizedSamplesData = normalizer.normalizeSamplesData(keyFields, samplesData, Collections.singletonList("GT"), ref,
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
        }


    }
}
