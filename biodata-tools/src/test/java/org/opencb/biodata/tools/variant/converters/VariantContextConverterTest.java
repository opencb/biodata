package org.opencb.biodata.tools.variant.converters;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.opencb.biodata.tools.variant.converters.avro.VariantAvroToVariantContextConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created on 29/11/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantContextConverterTest {

    @Test
    public void adjustedVariantStart() throws Exception {
        testBuildAllele("1:824337:TGC:TC,TG");
        testBuildAllele("1:1000:C:CC");
        testBuildAllele("1:1000:CC:C");
        testBuildAllele("1:1000:CT:CCT,C");
        testBuildAllele("1:1000:CT:C");
        testBuildAllele("1:1000:T:TC");
        testBuildAllele("1:1000:CT:C,CCT");
        testBuildAllele("1:1000:A:AA,AAT");
        testBuildAllele("1:1000:A:AAT,AA");
    }

    private void testBuildAllele(String varStr) throws NonStandardCompliantSampleField {
        Variant origVariant = Variant.newBuilder(varStr)
                .setStudyId("S")
                .setFileId("F")
                .setFormat("GT")
                .addSample("S1", "0/1").build();

        Variant v = new VariantNormalizer().normalize(Collections.singletonList(origVariant), false)
                .stream()
                .filter(var -> var.getStudies().get(0).getFiles().get(0).getCall().endsWith("0"))
                .findAny()
                .orElse(null);

        assertNotNull(v);

        Map<Integer, Character> referenceMap = VariantContextConverter.buildReferenceAllelesMap(v.getStudies().get(0).getFiles().stream().map(FileEntry::getCall).iterator());

        Pair<Integer, Integer> adjustedRange = VariantAvroToVariantContextConverter.adjustedVariantStart(v, v.getStudy("S"), referenceMap);
        System.out.println("");
        System.out.println(varStr + " -> " + v);
        System.out.println(adjustedRange);
        System.out.println(referenceMap);

        assertEquals(origVariant.getReference(), VariantContextConverter.buildAllele(v.getChromosome(), v.getStart(), v.getEnd(), v.getReference(), adjustedRange, referenceMap));
        assertEquals(origVariant.getAlternate(), VariantContextConverter.buildAllele(v.getChromosome(), v.getStart(), v.getEnd(), v.getAlternate(), adjustedRange, referenceMap));
        assertEquals(origVariant.getStart(), adjustedRange.getKey());
        assertEquals(origVariant.getEnd(), adjustedRange.getValue());

        List<AlternateCoordinate> origAlternates = origVariant.getStudies().get(0).getSecondaryAlternates();
        List<AlternateCoordinate> alternates = v.getStudies().get(0).getSecondaryAlternates();
        for (int i = 0; i < alternates.size(); i++) {
            assertEquals(origAlternates.get(i).getAlternate(),
                    VariantContextConverter.buildAllele(v.getChromosome(), alternates.get(i).getStart(), alternates.get(i).getEnd(), alternates.get(i).getAlternate(), adjustedRange, referenceMap));
        }

    }
}