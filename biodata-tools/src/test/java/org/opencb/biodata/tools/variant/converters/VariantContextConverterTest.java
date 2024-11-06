package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.commons.lang.StringUtils;
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

        checkVcf("1 1000 . AGTATATTGT A,AGT . . . GT:AD  1/1:10,10,0,0 0/1:0,10,0,0   ./.:.        2/2:1,0,1,10", merge(norm(variant), norm(variant2)));
        checkVcf("1 1001 . GTATATTGTG G,GT  . . . GT:AD  ./.:.         ./.:.          0/1:1,10,1,0 2/2:1,1,10,0", merge(norm(variant2), norm(variant)));
        checkVcf("1 1000 . AGTATATTGT A,AGT . . . GT:AD  1/1:10,10,0,0 0/1:0,10,0,0   ./.:.        2/2:1,0,1,10", merge(norm(variant), norm(variant2, 1)));
    }

    @Test
    public void testDuplicatedAlleleSV() throws NonStandardCompliantSampleField {
        Variant variant1 = Variant.newBuilder("1", 1000, 1020, "A", "<DEL>")
                .setStudyId("s")
                .setSampleDataKeys("GT", "AD")
                .addSample("s1", "1/1", "10,10")
                .addSample("s2", "0/1", "0,10")
                .build();
        Variant variant2 = Variant.newBuilder("1", 1002, 1022, "T", "<DEL>")
                .setStudyId("s")
                .setSampleDataKeys("GT", "AD")
                .addSample("s3", "0/1", "1,10")
                .addSample("s4", "1/1", "10,1")
                .build();
        Variant variantDup = Variant.newBuilder("1", 1002, 1022, "T", "<DUP>")
                .setStudyId("s")
                .setSampleDataKeys("GT", "AD")
                .addSample("s3", "0/1", "1,10")
                .addSample("s4", "1/1", "10,1")
                .build();
        Variant variantDupMatch = Variant.newBuilder("1", 1000, 1020, "T", "<DUP>")
                .setStudyId("s")
                .setSampleDataKeys("GT", "AD")
                .addSample("s3", "0/1", "1,10")
                .addSample("s4", "1/1", "10,1")
                .build();
        Variant variant3 = Variant.newBuilder("1", 1002, null, "TATATTGTGT", "TT,T")
                .setStudyId("s")
                .setSampleDataKeys("GT", "AD")
                .addSample("s5", "0/2", "1,1,10")
                .addSample("s6", "1/1", "1,10,1")
                .build();

        checkVcf("1 1000 . A <DEL> . . END=1020 GT:AD 1/1:10,10 0/1:0,10", variant1);
        checkVcf("1 1002 . T <DEL> . . END=1022 GT:AD 0/1:1,10 1/1:10,1", variant2);
        checkVcf("1 1002 . TATATTGTGT TT,T . . . GT:AD 0/2:1,1,10 1/1:1,10,1", variant3);
        checkVcf("1 1002 . T <DUP> . . END=1022 GT:AD 0/1:1,10 1/1:10,1", variantDup);
        checkVcf("1 1000 . T <DUP> . . END=1020 GT:AD 0/1:1,10 1/1:10,1", variantDupMatch);
        checkVcf("variant1 + variant2",       "1 1000 . A <DEL> . . END=1020 GT:AD 1/1:10,10,0 0/1:0,10,0 ./.:. ./.:.", merge(norm(variant1), norm(variant2)));
        checkVcf("variant2 + variant1",       "1 1002 . T <DEL> . . END=1022 GT:AD ./.:. ./.:. 0/1:1,10,0 1/1:10,1,0", merge(norm(variant2), norm(variant1)));
        checkVcf("variant2 + variant3",       "1 1002 . T <DEL> . . END=1022 GT:AD 0/1:1,10,0,0 1/1:10,1,0,0 ./.:. ./.:.", merge(norm(variant2), norm(variant3)));
        checkVcf("variant3 + variant2",       "1 1002 . TATATTGTGT T,TT  . . . GT:AD ./.:. ./.:. 0/1:1,10,1,0 2/2:1,1,10,0", merge(norm(variant3), norm(variant2)));
        checkVcf("variant1 + variantDup",     "1 1000 . A <DEL> . . END=1020 GT:AD 1/1:10,10,0 0/1:0,10,0 ./.:. ./.:.", merge(norm(variant1), norm(variantDup)));
        checkVcf("variant1 + variantDupMatch","1 1000 . A <DEL>,<DUP> . . END=1020 GT:AD 1/1:10,10,0 0/1:0,10,0 0/2:1,0,10 2/2:10,0,1", merge(norm(variant1), norm(variantDupMatch)));
    }

    public void checkVcf(String expectedVcf, Variant variant) {
        checkVcf(null, expectedVcf, variant);
    }
    public void checkVcf(String message, String expectedVcf, Variant variant) {
        if (message == null) {
            message = variant.toString();
        }
        String vcf = toVcf(variant).replace("\n", "");
//        System.out.println(message + " = " + vcf.replace("\t", " "));
        expectedVcf = expectedVcf.replaceAll(" +", " ");
        assertEquals(message, expectedVcf.replace(" ", "\t"), vcf);
    }

    private static Variant merge(Variant variant, Variant variant2) {
        return new VariantMerger().merge(variant, variant2);
    }

    private static VariantContext toContext(Variant variant) {
        String studyId = variant.getStudies().get(0).getStudyId();
        List<String> sampleNames = variant.getSampleNames(studyId);
        VariantAvroToVariantContextConverter converter = new VariantAvroToVariantContextConverter(studyId, sampleNames, Collections.emptyList());
        VariantContext context = converter.convert(variant);
        return context;
    }

    private static String toVcf(Variant variant) {
        return toVcf(toContext(variant));
    }

    private static String toVcf(VariantContext context) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        VariantContextWriter writer = VcfUtils.createVariantContextWriter(os, null, Options.ALLOW_MISSING_FIELDS_IN_HEADER);
        writer.setHeader(new VCFHeader(Collections.emptySet(), context.getSampleNamesOrderedByName()));
        writer.add(context);
        writer.close();
        return os.toString();
    }

    private static Variant norm(Variant variant) throws NonStandardCompliantSampleField {
        return norm(variant, 0);
    }

    private static Variant norm(Variant variant, int idx) throws NonStandardCompliantSampleField {
        return new VariantNormalizer().normalize(Collections.singletonList(variant), false).get(idx);
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

        Pair<Integer, Integer> adjustedRange = VariantAvroToVariantContextConverter.adjustedVariantStart(v, v.getStudy("S"), referenceMap, Collections.emptySet());
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