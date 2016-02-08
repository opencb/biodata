package org.opencb.biodata.tools.variant.merge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

public class VariantMergerTest {

    private static final VariantMerger VARIANT_MERGER = new VariantMerger();
    private static final String STUDY_ID = "";
    private Variant var;

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
    public void testMergeSame() {        
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","T",VariantType.SNV, 
                Arrays.asList("S02"), 
                Arrays.asList("0/1")));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/1")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));
        
        String[] samples = new String[]{"S01","S02"};
        Map<String, Integer> collect = IntStream.range(0, 2).mapToObj(i -> i).collect(Collectors.toMap(i -> (String) samples[i], i-> i));
        assertEquals(collect,
                VARIANT_MERGER.getStudy(var).getSamplesPosition());

        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","T",VariantType.SNV, 
                Arrays.asList("S03"), 
                Arrays.asList("0/0")));
        assertEquals(Arrays.asList(lst("0/1"),lst("0/1"),lst("0/0")),
                onlyField(VARIANT_MERGER.getStudy(var).getSamplesData(),0));
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
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","G",VariantType.SNV, 
                Arrays.asList("S02"), 
                Arrays.asList("0/1")));
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("0/2")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test
    public void testMergeMonoAllelic() {
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","G",VariantType.SNV, 
                Arrays.asList("S02"), 
                Arrays.asList("1")));
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("2")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test
    public void testMergeWithSecondary() {
        Variant variant = generateVariant("1",10,"A","G",VariantType.SNV, 
                Arrays.asList("S02"), 
                Arrays.asList("1/2"));
        variant.getStudies().get(0).setSecondaryAlternates(Arrays.asList(new AlternateCoordinate("1", 10, 11, "A", "C", VariantType.SNV)));
        VARIANT_MERGER.merge(var, variant);
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(2, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("2/3")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test
    public void testMergeMonoAllelicSameAlt() {
        VARIANT_MERGER.merge(var, generateVariant("1",10,"A","T",VariantType.SNV, 
                Arrays.asList("S02"), 
                Arrays.asList("1")));
        StudyEntry se = VARIANT_MERGER.getStudy(var);
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
        assertEquals(1, se.getSecondaryAlternates().size());
        assertEquals(Arrays.asList(lst("0/1"),lst("./2")),
                onlyField(se.getSamplesData(), 0));
    }

    @Test(expected=IllegalStateException.class)
    public void testMergeSameSample() { // TODO check if this can happen and result is correct !!!
        VARIANT_MERGER.merge(var, generateVariant("1", 9, "AAA", "-", VariantType.INDEL, Arrays.asList("S01"), Arrays.asList("0/1")));
        StudyEntry se = VARIANT_MERGER.getStudy(var);
        assertEquals(1, se.getSecondaryAlternates().size());
        // TODO not sure 1/2 is correct if the same individual has a variant with 0/1 and another variant with 0/2 overlapping each other
        assertEquals(Arrays.asList(lst("1/2")), se.getSamplesData());
    }

    private List<String> lst(String str) {
        return Collections.singletonList(str);
    }
    
    
    @Test
    public void sameStartPosition(){
        assertTrue(generateVariant("1", 10, "A", "T", VariantType.SNV).onSameStartPosition(generateVariant("1", 10, "A", "T", VariantType.SNV)));
        assertFalse(generateVariant("1", 10, "A", "T", VariantType.SNV).onSameStartPosition(generateVariant("1", 11, "A", "T", VariantType.SNV)));
    }

    private Variant generateVariant(String chr, int pos, String ref, String alt, VariantType vt) {
        return generateVariant(chr, pos, ref, alt, vt,Collections.emptyList(),Collections.emptyList());
    }

    private Variant generateVariant(String chr, int pos, String ref, String alt, VariantType vt, 
            List<String> sampleIds, List<String> sampleGt) {
        int end = pos + ref.length() ;
        Variant var = new Variant(chr,pos,end,ref,alt);
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
