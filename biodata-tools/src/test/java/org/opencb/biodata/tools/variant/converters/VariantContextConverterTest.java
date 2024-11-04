package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.exceptions.NonStandardCompliantSampleField;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.opencb.biodata.tools.variant.converters.avro.VariantAvroToVariantContextConverter;
import org.opencb.biodata.tools.variant.merge.VariantMerger;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created on 29/11/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantContextConverterTest {

    @Test
    public void testDuplicatedAllele() throws NonStandardCompliantSampleField {
        String studyId = "s";
        Variant variant = Variant.newBuilder("1", 1000, null, "AGTATATTGT", "A")
                .setStudyId(studyId)
                .setSampleDataKeys("GT", "AD")
                .addSample("s1", "1/1", "10,10")
                .addSample("s2", "0/1", "0,10")
                .build();
        Variant variant2 = Variant.newBuilder("1", 1002, null, "TATATTGTGT", "TT,T")
                .setStudyId(studyId)
                .setSampleDataKeys("GT", "AD")
                .addSample("s3", "0/2", "1,1,10")
                .addSample("s4", "1/1", "1,10,1")
                .build();


        Variant normalized = new VariantNormalizer().normalize(Collections.singletonList(variant), false).get(0);
        Variant normalized2 = new VariantNormalizer().normalize(Collections.singletonList(variant2), false).get(0);

        Variant merged = new VariantMerger().merge(normalized, normalized2);

//        System.out.println("merged = " + merged.toJson());

        // Convert to VariantContext
        List<String> sampleNames = merged.getSampleNames(studyId);
        VariantAvroToVariantContextConverter converter = new VariantAvroToVariantContextConverter(studyId, sampleNames, Collections.emptyList());
        VariantContext context = converter.convert(merged);

//        System.out.println("context = " + context);

        // Print as VCF
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        VariantContextWriter writer = VcfUtils.createVariantContextWriter(os, null, Options.ALLOW_MISSING_FIELDS_IN_HEADER);
        writer.setHeader(new VCFHeader(Collections.emptySet(), sampleNames));
        writer.add(context);
        writer.close();
        System.out.println(os);
        assertArrayEquals(new String[]{"1", "1000", ".", "AGTATATTGTG", "AG,AGT", ".", ".", ".", "GT:AD",
                "1/1:10,10,0",
                "0/1:0,10,0",
                "0/1:1,10,1",
                "2/2:1,1,10"}, os.toString().trim().split("\t"));
    }

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
        testBuildAllele("1:1000-1100:A:<DEL>");
        testBuildAllele("1:1000-1100:A:<CN1>,<CN2>,<CN3>");
        testBuildAllele("1:1000-1100:A:<DEL:ME:ALU>");

        testBuildAllele("1:1000:A:]3:1234]A");
        testBuildAllele("1:1000:A:]3:1234]NNNA");
        testBuildAllele("1:1000:A:]3:1234]NNN");

        testBuildAllele("1:1000:A:A]3:1234]");
        testBuildAllele("1:1000:A:ANNN]3:1234]");
        testBuildAllele("1:1000:A:NNN]3:1234]");

        testBuildAllele("1:1000:A:ANNN]3:1234],ANNN]3:1234]");
        testBuildAllele("1:1000:A:]3:1234]NNNNA,ANNN]3:1234]");
        testBuildAllele("1:1000:A:ANNN]3:1234],NNN]3:1234]");
        testBuildAllele("1:1000:A:NNN]3:1234],ANNN]3:1234]");

    }

    private void testBuildAllele(String varStr) throws NonStandardCompliantSampleField {
        Variant origVariant = Variant.newBuilder(varStr)
                .setStudyId("S")
                .setFileId("F")
                .setSampleDataKeys("GT")
                .addSample("S1", "0/1").build();

        List<Variant> normalized = new VariantNormalizer().normalize(Collections.singletonList(origVariant), false);
        Variant v = normalized
                .stream()
                .filter(var -> var.getStudies().get(0).getFiles().get(0).getCall() != null)
                .filter(var -> var.getStudies().get(0).getFiles().get(0).getCall().getAlleleIndex() == 0)
                .findAny()
                .orElse(origVariant);

        assertNotNull(v);

        Map<Integer, Character> referenceMap = VariantContextConverter.buildReferenceAllelesMap(
                v.getStudies().get(0).getFiles()
                        .stream()
                        .map(entry -> entry.getCall() == null ? null : entry.getCall().getVariantId())
                        .iterator());

        Pair<Integer, Integer> adjustedRange = VariantAvroToVariantContextConverter.adjustedVariantStart(v, v.getStudy("S"), referenceMap);
        System.out.println("");
        System.out.println(varStr + " -> " + v + " ( " + normalized.stream().map(Object::toString).collect(Collectors.joining(" , ")) + " )");
        System.out.println(adjustedRange);
        System.out.println(referenceMap);

        Integer end = v.getEnd();
        if (v.getLength() == Variant.UNKNOWN_LENGTH) {
            end = v.getStart() + v.getReference().length() - 1;
        }
        assertEquals(origVariant.getReference(), VariantContextConverter.buildAllele(v.getChromosome(), v.getStart(), end, v.getReference(), adjustedRange, referenceMap));
        assertEquals(origVariant.getAlternate(), VariantContextConverter.buildAllele(v.getChromosome(), v.getStart(), end, v.getAlternate(), adjustedRange, referenceMap));
        assertEquals(origVariant.getStart(), adjustedRange.getKey());
        if (!origVariant.getType().equals(VariantType.BREAKEND)) {
            assertEquals(origVariant.getEnd(), adjustedRange.getValue());
        }

        List<AlternateCoordinate> origAlternates = origVariant.getStudies().get(0).getSecondaryAlternates();
        List<AlternateCoordinate> alternates = v.getStudies().get(0).getSecondaryAlternates();
        for (int i = 0; i < alternates.size(); i++) {
            Integer secStart = alternates.get(i).getStart();
            Integer secEnd = alternates.get(i).getEnd();
            assertEquals(origAlternates.get(i).getAlternate(),
                    VariantContextConverter.buildAllele(v.getChromosome(), secStart == null ? v.getStart() : secStart, secEnd == null ? v.getEnd() : secEnd, alternates.get(i).getAlternate(), adjustedRange, referenceMap));
        }

    }
}