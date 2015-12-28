package org.opencb.biodata.models.variant;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.commons.test.GenericTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
