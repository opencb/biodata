package org.opencb.biodata.tools.variant.merge;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class VariantMergerTest {

    private static final VariantMerger VARIANT_MERGER = new VariantMerger();
    private static final String STUDY_ID = "";
    private Variant var;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        Variant tempate = generateVariant("1",10,"A","T",VariantType.SNV, 
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
        VARIANT_MERGER.merge(var, generateVariant("1:10:A:T", "S02", "0/1"));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/1")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));

        String[] samples = new String[]{"S01","S02"};
        Map<String, Integer> collect = IntStream.range(0, 2).mapToObj(i -> i).collect(Collectors.toMap(i -> (String) samples[i], i-> i));
        assertEquals(collect,
                VARIANT_MERGER.getStudy(var).getSamplesPosition());

        VARIANT_MERGER.merge(var, generateVariant("1:10:A:T", "S03", "0/0"));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/1"),lst("0/0")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));
    }

    @Test
    public void testMergeSame_2INDEL() {
        Variant var = VARIANT_MERGER.merge(generateVariant("1:10:A:", "S01", "0/0"), generateVariant("1:10:A:", "S02", "0/1"));
        StudyEntry se = var.getStudy(STUDY_ID);

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
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","G",VariantType.SNV,
                Arrays.asList("S02"),
                Arrays.asList("0/0")));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/0")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));
    }

    @Test
    public void testMergeDifferentComplex() {
        VARIANT_MERGER.merge(var, generateVariant("1", 10, "A", "G", VariantType.SNV,
                Arrays.asList("S02"),
                Arrays.asList("0/1")));
        StudyEntry se = var.getStudy(STUDY_ID);
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Collections.singletonList(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV)), se.getSecondaryAlternates());
        assertEquals(Arrays.asList(lst("0/1"),lst("0/2")),
                onlyField(se.getSamplesData(), 0));
        assertEquals(Arrays.asList("S01", "S02"), se.getOrderedSamplesName());
    }

    @Test
    public void testMergeDifferentComplex2() {
        Variant var = VARIANT_MERGER.merge(generateVariant("1:10:A:G", "S02", "0/1"), generateVariant("1:10:A:T", "S01", "0/1"));
        StudyEntry se = var.getStudy(STUDY_ID);
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
    public void testMergeSame_2SNV() {
        checkSameNoSecondaries("1:10:A:T");
        checkSameNoSecondaries("1:10:AT:TA");
        checkMergeVariantsNoSecondaries(
                generateVariant("1:10:A:T", "S1", "0/0", "S2", "0/0"),
                generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "1/1", "S06", "1/1"),
                "0/1", "1/1", "1/1", "1/1");
    }


    @Test
    public void testMergeSameWithSameAlternates_2SNV() {
        Variant var1 = generateVariant("1:10:A:T", "S1", "0/0", "S2", "0/0");
        Variant var2 = generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "1/1", "S06", "1/1");
        AlternateCoordinate alternate = new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV);
        var1.getStudy(STUDY_ID).getSecondaryAlternates().add(alternate);
        var2.getStudy(STUDY_ID).getSecondaryAlternates().add(alternate);
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G"), "0/1", "1/1", "1/1", "1/1");
    }

    @Test
    public void testMergeSameWithDifferentAlternates_2SNV() {
        Variant var1 = generateVariant("1:10:A:T", "S1", "0/0", "S2", "0/0");
        Variant var2 = generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "1/1", "S06", "1/1");
        var1.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV));
        var2.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "0/1", "1/1", "1/1", "1/1");
    }

    @Test
    public void testMergeSameWithSameUnorderedAlternates_2SNV() {
        Variant var1 = generateVariant("1:10:A:T", "S1", "1/2", "S2", "2/3");
        Variant var2 = generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "0/2", "S06", "0/3");
        var1.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV));
        var1.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        var2.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        var2.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "G", VariantType.SNV));
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

    @Test
    public void testMergeOverlap_2IN_2() {
        checkOverlapNoSecondaries("1:9::ATGG", "1:11::GGG");
    }

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
        Variant variant = generateVariant("1",10,"A","G",VariantType.SNV, 
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
        Variant var1 = generateVariant("1:10:A:T", "S01", "0/1");
        Variant var2 = generateVariant("1:10:A:G", "S02", "1/2");
        var2.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G", "1:10:A:C"), "2/3");
    }

    @Test
    public void testMergeWithSecondary_2SNP_3() {
        Variant var1 = generateVariant("1:10:A:T", "S01", "1/2");
        var1.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "C", VariantType.SNV));
        Variant var2 = generateVariant("1:10:A:G", "S02", "0/1");
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:C", "1:10:A:G"), "0/3");
    }

    @Test
    public void testMergeWithSameSecondary_2SNP() {
        Variant var1 = generateVariant("1:10:A:T", "S01", "0/1");
        Variant var2 = generateVariant("1:10:A:G", "S02", "1/2");
        var2.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "T", VariantType.SNV));
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:G"), "2/1");
    }

    @Test
    public void testMergeWithSameSecondary_2SNP_2(){
        Variant var1 = generateVariant("1:10:A:G", "S01", "1/2", "S02", "2/1");
        var1.getStudy(STUDY_ID).getSecondaryAlternates().add(new AlternateCoordinate("1", 10, 10, "A", "T", VariantType.SNV));
        Variant var2 = generateVariant("1:10:A:T", "S03", "0/1", "S04", "1/1", "S05", "0/0");
        checkMergeVariants(var1, var2, Arrays.asList("1:10:A:T"), "0/2", "2/2", "0/0");
    }

    @Test
    public void testMergeMonoAllelicSameAlt() {
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","T",VariantType.SNV, 
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
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","G",VariantType.SNV, 
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
        Variant v = generateVariant("1:10:A:", "S01", "0/0");
        v.setType(VariantType.NO_VARIATION);
        v.setEnd(100);

        checkMergeVariants(generateVariant("1:10:A:T", "S02", "0/1"), v, Collections.emptyList(), "0/0");
    }

    @Test
    public void testMergeSameSample() { // TODO check if this can happen and result is correct !!!
        thrown.expect(IllegalStateException.class);
        VARIANT_MERGER.merge(var, generateVariant("1", 9, "AAA", "-", VariantType.INDEL, Arrays.asList("S01"), Arrays.asList("0/1")));
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
        assertTrue(generateVariant("1", 10, "A", "T", VariantType.SNV).onSameStartPosition(generateVariant("1", 10, "A", "T", VariantType.SNV)));
        assertFalse(generateVariant("1", 10, "A", "T", VariantType.SNV).onSameStartPosition(generateVariant("1", 11, "A", "T", VariantType.SNV)));
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
                generateVariant(varstr1, "S01", gt1),
                generateVariant(varstr2, "S02", gt2), gt2Merged);
    }

    public Variant checkMergeVariantsNoSecondaries(Variant var1, Variant var2, String ...expectedVar2Gts) {
        assertTrue(var1.getStudy(STUDY_ID).getSecondaryAlternates().isEmpty());
        assertTrue(var2.getStudy(STUDY_ID).getSecondaryAlternates().isEmpty());

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
        samples.addAll(var1.getStudy(STUDY_ID).getOrderedSamplesName());
        samples.addAll(var2.getStudy(STUDY_ID).getOrderedSamplesName());

        List<String> gtsVar1 = var1.getStudy(STUDY_ID).getSamplesData().stream().map(strings -> strings.get(0)).collect(Collectors.toList());

        Variant mergeVar = VARIANT_MERGER.merge(var1, var2);
        System.out.println("mergeVar.toJson() = " + mergeVar.toJson());
        StudyEntry se = mergeVar.getStudy(STUDY_ID);
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

    private Variant generateVariant(String chr, int pos, String ref, String alt, VariantType vt) {
        return generateVariant(chr, pos, ref, alt, vt,Collections.emptyList(),Collections.emptyList());
    }

    private Variant generateVariant(String chr, int pos, String ref, String alt, VariantType vt,
                                    List<String> sampleIds, List<String> sampleGt) {
        return generateVariant(new Variant(chr, pos, pos + Math.max(ref.length(), alt.length()) - 1, ref, alt), vt, sampleIds, sampleGt);
    }

    private Variant generateVariant(String var, String... samplesData) {
        Variant variant = new Variant(var);
        List<String> sampleIds = new ArrayList<>(samplesData.length / 2);
        List<String> sampleGt = new ArrayList<>(samplesData.length / 2);
        for (int i = 0; i < samplesData.length; i = i + 2) {
            sampleIds.add(samplesData[i]);
            sampleGt.add(samplesData[i+1]);
        }
        return generateVariant(variant, variant.getType(), sampleIds, sampleGt);
    }

    private Variant generateVariant(String var, List<String> sampleIds, List<String> sampleGt) {
        Variant variant = new Variant(var);
        return generateVariant(variant, variant.getType(), sampleIds, sampleGt);
    }

    private Variant generateVariant(Variant variant, VariantType vt,
                                    List<String> sampleIds, List<String> sampleGt) {
        Variant var = variant;
        var.setType(vt);
        StudyEntry se = new StudyEntry(STUDY_ID);
        se.setFiles(Collections.singletonList(new FileEntry("", "", Collections.emptyMap())));
        se.setFormat(Collections.singletonList("GT"));
        Map<String, Integer> sp = new HashMap<String, Integer>();
        for(int i = 0; i < sampleIds.size(); ++i){
            sp.put(sampleIds.get(i), i);
        }
        se.setSamplesPosition(sp);
        List<List<String>> gt = sampleGt.stream().map(s -> Collections.singletonList(s))
                .collect(Collectors.toList());
        se.setSamplesData(gt);
        var.addStudyEntry(se );
        return var;
    }

}
