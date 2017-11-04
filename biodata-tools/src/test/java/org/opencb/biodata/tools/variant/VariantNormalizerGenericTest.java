package org.opencb.biodata.tools.variant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.tools.variant.merge.VariantAlternateRearranger;
import org.opencb.commons.test.GenericTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by priesgo on 04/10/17.
 */
public class VariantNormalizerGenericTest extends GenericTest {

    protected VariantNormalizer normalizer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        normalizer = new VariantNormalizer();
        normalizer.setGenerateReferenceBlocks(true);
    }

    protected void testSampleNormalization(String chromosome, int position, String ref, String alt,
                                         int normPos, String normRef, String normAlt)
            throws NonStandardCompliantSampleField {

        testSampleNormalization(chromosome, position, ref, alt, normPos, normPos, normRef, normAlt);
    }

    protected void testSampleNormalization(String chromosome, int position, String ref, String alt,
                                         int normPos, String normRef, String normAlt, Boolean normalizeSamplesData)
            throws NonStandardCompliantSampleField {

        testSampleNormalization(chromosome, position, ref, alt, normPos, normPos, normRef, normAlt, normalizeSamplesData);
    }

    protected void testSampleNormalization(String chromosome, int position, String ref, String alt,
                                         int normStart, int normEnd, String normRef, String normAlt)
            throws NonStandardCompliantSampleField {

        testSampleNormalization(chromosome, position, ref, alt, normStart, normEnd, normRef, normAlt, true);
    }

    protected void testSampleNormalization(String chromosome, int position, String ref, String alt,
                                         int normStart, int normEnd, String normRef, String normAlt,
                                         Boolean normalizeSamplesData)
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
        if (normalizeSamplesData) {
            samplesData = normalizer.normalizeSamplesData(keyFields, samplesData, Collections.singletonList("GT"), ref,
                    Collections.singletonList(alt), null);

            assertEquals("0/1", samplesData.get(0).get(0));
            assertEquals("0/0", samplesData.get(1).get(0));
            assertEquals("1/0", samplesData.get(2).get(0));
            assertEquals("1/1", samplesData.get(3).get(0));
            assertEquals("0/1", samplesData.get(4).get(0));
            assertEquals("0/0", samplesData.get(5).get(0));
            assertEquals("1/0", samplesData.get(6).get(0));
            assertEquals("1/1", samplesData.get(7).get(0));
        }
    }


    protected void testSampleNormalization(int position, String ref, String altsCsv,
                                         List<VariantNormalizer.VariantKeyFields> expectedKeyFieldsList)
            throws NonStandardCompliantSampleField {
        testSampleNormalization(position, ref, altsCsv, expectedKeyFieldsList, null);
    }

    protected void testSampleNormalization(int position, String ref, String altsCsv,
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
            if (v.getReference().isEmpty()) {
                assertEquals(v.getStart() - 1, v.getEnd().intValue());
            } else {
                assertTrue(v.getStart() <= v.getEnd());
            }
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

            VariantAlternateRearranger rearranger = null;
            if (expectedKeyFields.getNumAllele() > 0) {
                List<String> reordered = new ArrayList<>();
                reordered.add(alt);
                for (String s : altsList) {
                    if (!s.equals(alt)) {
                        reordered.add(alt);
                    }
                }
                rearranger = new VariantAlternateRearranger(altsList, reordered);
            }

            normalizer.setNormalizeAlleles(true);
            normalizedSamplesData = normalizer.normalizeSamplesData(normalizedKeyFields, samplesData, Collections.singletonList("GT"), ref,
                    altsList, rearranger);

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
                    altsList, rearranger);

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

    protected Variant newVariant(int position, String ref, String altsCsv) {
        return newVariant(position, position, ref, Arrays.asList(altsCsv.split(",")), "2");
    }

    protected Variant newVariant(int position, String ref, List<String> altsList, String studyId) {
        return newVariant(position, position, ref, altsList, studyId);
    }

    protected Variant newVariant(int position, int end, String ref, List<String> altsList, String studyId) {
        return newVariantBuilder(position, end, ref, altsList, studyId).build();
    }

    protected VariantBuilder newVariantBuilder(int position, int end, String ref, List<String> altsList, String studyId) {
        return Variant.newBuilder("1", position, end, ref, String.join(",", altsList))
                .setStudyId(studyId)
                .setFormat("GT")
                .setSamplesData(new ArrayList<>())
                .setFileId("1");
    }
}
