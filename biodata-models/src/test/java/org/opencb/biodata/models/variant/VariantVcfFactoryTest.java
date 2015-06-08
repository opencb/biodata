package org.opencb.biodata.models.variant;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.exceptions.NotAVariantException;

import java.util.*;
import static org.junit.Assert.assertArrayEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantVcfFactoryTest {

    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");
    private VariantFactory factory = new VariantVcfFactory();

    @Before
    public void setUp() throws Exception {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        source.setSamples(sampleNames);
    }

    @Test
    public void testCreateVariantFromVcfSameLengthRefAlt() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\trs123\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1005, "CACCC", "GACGG"));

        List<Variant> result = factory.create(source, line);
        assertEquals(expResult, result);

        // Test when there are not differences at the end of the sequence
        line = "1\t1000\trs123\tTCACCC\tTGACGC\t.\t.\t.";

        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1004, "CACC", "GACG"));

        result = factory.create(source, line);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariantFromVcfInsertionEmptyRef() {
        String line = "1\t1000\trs123\t.\tTGACGC\t.\t.\t.";

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000 + "TGACGC".length() - 1, "", "TGACGC"));

        List<Variant> result = factory.create(source, line);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariantFromVcfDeletionEmptyAlt() {
        String line = "1\t1000\trs123\tTCACCC\t.\t.\t.\t.";

//        if this were not considered as a bad input
//        List<Variant> expResult = new LinkedList<>();
//        expResult.add(new Variant("1", 1000, 1000 + "TCACCC".length() - 1, "TCACCC", ""));
//        assertEquals(expResult, result);

        try {
            List<Variant> result = factory.create(source, line);
            fail();
        } catch (NotAVariantException e) {
            return;
        }
    }

    @Test
    public void testCreateVariantFromVcfIndelNotEmptyFields() {
        String line = "1\t1000\trs123\tCGATT\tTAC\t.\t.\t.";

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000 + "CGATT".length() - 1, "CGATT", "TAC"));
        List<Variant> result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tAT\tA\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1001, "T", ""));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tATC\t\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1002, "ATC", ""));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\t.\tATC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1002, "", "ATC"));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tA\tATC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1002, "", "TC"));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tAC\tACT\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1002, 1002, "", "T"));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        // Printing those that are not currently managed
        line = "1\t1000\trs123\tAT\tT\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000, "A", ""));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tATC\tTC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000, "A", ""));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tATC\tAC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1001, "T", ""));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tAC\tATC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1001, "", "T"));
        result = factory.create(source, line);
        assertEquals(expResult, result);
        
        line = "1\t1000\trs123\tATC\tGC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1001, "AT", "G"));
        result = factory.create(source, line);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariantFromVcfCoLocatedVariants_MainFields() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        source.setSamples(sampleNames);

        String line = "1\t10040\trs123\tTGACGTAACGATT\tT,TGACGTAACGGTT,TGACGTAATAC\t.\t.\t.\tGT\t0/0\t0/1\t0/2\t1/2"; // 4 samples

        // Check proper conversion of main fields
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 10040, 10040 + "TGACGTAACGAT".length() - 1, "TGACGTAACGAT", ""));
        expResult.add(new Variant("1", 10050, 10050 + "A".length() - 1, "A", "G"));
        expResult.add(new Variant("1", 10048, 10048 + "CGATT".length() - 1, "CGATT", "TAC"));

        List<Variant> result = factory.create(source, line);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariant_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005");
        source.setSamples(sampleNames);

        String line = "1\t10040\trs123\tT\tC\t.\t.\t.\tGT\t0/0\t0/1\t0/.\t./1\t1/1"; // 5 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 10041, 10041 + "C".length() - 1, "T", "C");
        VariantSourceEntry file0 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var0.addSourceEntry(file0);

        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "0/0");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "0/1");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "0/.");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "./1");
        Map<String, String> na005 = new HashMap<>();
        na005.put("GT", "1/1");

        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(4), na005);

        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(1, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(var0.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData(), getVar0.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData());
    }

    @Test
    public void testCreateVariantFromVcfMultiallelicVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        source.setSamples(sampleNames);
        String line ="1\t123456\t.\tT\tC,G\t110\tPASS\t.\tGT:AD:DP:GQ:PL\t0/1:10,5:17:94:94,0,286\t0/2:3,8:15:43:222,0,43\t0/0:.:18:.:.\t1/2:7,6:13:99:162,0,180"; // 4 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 123456, 123456, "T", "C");
        VariantSourceEntry file0 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var0.addSourceEntry(file0);

        Variant var1 = new Variant("1", 123456, 123456, "T", "G");
        VariantSourceEntry file1 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var1.addSourceEntry(file1);


        // Initialize expected samples in variant 1 (alt allele C)
        Map<String, String> na001_C = new HashMap<>();
        na001_C.put("GT", "0/1");
        na001_C.put("AD", "10,5");
        na001_C.put("DP", "17");
        na001_C.put("GQ", "94");
        na001_C.put("PL", "94,0,286");
        Map<String, String> na002_C = new HashMap<>();
        na002_C.put("GT", "0/2");
        na002_C.put("AD", "3,8");
        na002_C.put("DP", "15");
        na002_C.put("GQ", "43");
        na002_C.put("PL", "222,0,43");
        Map<String, String> na003_C = new HashMap<>();
        na003_C.put("GT", "0/0");
        na003_C.put("AD", ".");
        na003_C.put("DP", "18");
        na003_C.put("GQ", ".");
        na003_C.put("PL", ".");
        Map<String, String> na004_C = new HashMap<>();
        na004_C.put("GT", "1/2");
        na004_C.put("AD", "7,6");
        na004_C.put("DP", "13");
        na004_C.put("GQ", "99");
        na004_C.put("PL", "162,0,180");

        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004_C);
        
        // Initialize expected samples in variant 2 (alt allele G)
        Map<String, String> na001_G = new HashMap<>();
        na001_G.put("GT", "0/2");
        na001_G.put("AD", "10,5");
        na001_G.put("DP", "17");
        na001_G.put("GQ", "94");
        na001_G.put("PL", "94,0,286");
        Map<String, String> na002_G = new HashMap<>();
        na002_G.put("GT", "0/1");
        na002_G.put("AD", "3,8");
        na002_G.put("DP", "15");
        na002_G.put("GQ", "43");
        na002_G.put("PL", "222,0,43");
        Map<String, String> na003_G = new HashMap<>();
        na003_G.put("GT", "0/0");
        na003_G.put("AD", ".");
        na003_G.put("DP", "18");
        na003_G.put("GQ", ".");
        na003_G.put("PL", ".");
        Map<String, String> na004_G = new HashMap<>();
        na004_G.put("GT", "2/1");
        na004_G.put("AD", "7,6");
        na004_G.put("DP", "13");
        na004_G.put("GQ", "99");
        na004_G.put("PL", "162,0,180");
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001_G);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002_G);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003_G);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004_G);


        // Check proper conversion of samples and alternate alleles
        List<Variant> result = factory.create(source, line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(
                var0.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData(), 
                getVar0.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData());
        assertArrayEquals(new String[]{ "G" }, getVar0.getSourceEntry(source.getFileId(), source.getStudyId()).getSecondaryAlternates());

        Variant getVar1 = result.get(1);
        assertEquals(
                var1.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData(), 
                getVar1.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData());
        assertArrayEquals(new String[]{ "C" }, getVar1.getSourceEntry(source.getFileId(), source.getStudyId()).getSecondaryAlternates());
    }

    @Test
    public void testCreateVariantFromVcfCoLocatedVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        source.setSamples(sampleNames);
        String line = "1\t10040\trs123\tT\tC,GC\t.\t.\t.\tGT:GL\t0/0:1,2,3,4,5,6,7,8,9,10\t0/1:1,2,3,4,5,6,7,8,9,10\t0/2:1,2,3,4,5,6,7,8,9,10\t1/1:1,2,3,4,5,6,7,8,9,10\t1/2:1,2,3,4,5,6,7,8,9,10\t2/2:1,2,3,4,5,6,7,8,9,10"; // 6 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 10041, 10041 + "C".length() - 1, "T", "C");
        VariantSourceEntry file0 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var0.addSourceEntry(file0);

        Variant var1 = new Variant("1", 10050, 10050 + "GC".length() - 1, "T", "GC");
        VariantSourceEntry file1 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var1.addSourceEntry(file1);

        // Initialize expected samples in variant 1 (alt allele C)
        Map<String, String> na001_C = new HashMap<>();
        na001_C.put("GT", "0/0");
        na001_C.put("GL", "1,1,1");
        Map<String, String> na002_C = new HashMap<>();
        na002_C.put("GT", "0/1");
        na002_C.put("GL", "1,2,3");
        Map<String, String> na003_C = new HashMap<>();
        na003_C.put("GT", "0/2");
        na003_C.put("GL", "1,4,6");
        Map<String, String> na004_C = new HashMap<>();
        na004_C.put("GT", "1/1");
        na004_C.put("GL", "1,2,3");
        Map<String, String> na005_C = new HashMap<>();
        na005_C.put("GT", "1/2");
        na005_C.put("GL", "3,5,6");
        Map<String, String> na006_C = new HashMap<>();
        na006_C.put("GT", "2/2");
        na006_C.put("GL", "1,4,6");

        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(4), na005_C);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(5), na006_C);

        // TODO Initialize expected samples in variant 2 (alt allele GC)
        Map<String, String> na001_GC = new HashMap<>();
        na001_GC.put("GT", "0/0");
        na001_GC.put("GL", "1,1,1");
        Map<String, String> na002_GC = new HashMap<>();
        na002_GC.put("GT", "0/2");
        na002_GC.put("GL", "1,2,3");
        Map<String, String> na003_GC = new HashMap<>();
        na003_GC.put("GT", "0/1");
        na003_GC.put("GL", "1,4,6");
        Map<String, String> na004_GC = new HashMap<>();
        na004_GC.put("GT", "2/2");
        na004_GC.put("GL", "1,2,3");
        Map<String, String> na005_GC = new HashMap<>();
        na005_GC.put("GT", "2/1");
        na005_GC.put("GL", "3,5,6");
        Map<String, String> na006_GC = new HashMap<>();
        na006_GC.put("GT", "1/1");
        na006_GC.put("GL", "1,4,6");
        
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001_GC);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002_GC);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003_GC);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004_GC);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(4), na005_GC);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(5), na006_GC);

        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(
                var0.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData(), 
                getVar0.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData());
        assertArrayEquals(new String[]{ "GC" }, getVar0.getSourceEntry(source.getFileId(), source.getStudyId()).getSecondaryAlternates());

        Variant getVar1 = result.get(1);
        assertEquals(
                var1.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData(), 
                getVar1.getSourceEntry(source.getFileId(), source.getStudyId()).getSamplesData());
        assertArrayEquals(new String[]{ "C" }, getVar1.getSourceEntry(source.getFileId(), source.getStudyId()).getSecondaryAlternates());
    }
    
    @Test
    public void testCreateVariantWithMissingGenotypes() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        source.setSamples(sampleNames);
        String line = "1\t1407616\t.\tC\tG\t43.74\tPASS\t.\tGT:AD:DP:GQ:PL\t./.:.:.:.:.\t1/1:0,2:2:6:71,6,0\t./.:.:.:.:.\t./.:.:.:.:.";
    
        // Initialize expected variants
        Variant var0 = new Variant("1", 1407616, 1407616, "C", "G");
        VariantSourceEntry file0 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var0.addSourceEntry(file0);
        
        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "./.");
        na001.put("AD", ".");
        na001.put("DP", ".");
        na001.put("GQ", ".");
        na001.put("PL", ".");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "1/1");
        na002.put("AD", "0,2");
        na002.put("DP", "2");
        na002.put("GQ", "6");
        na002.put("PL", "71,6,0");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "./.");
        na003.put("AD", ".");
        na003.put("DP", ".");
        na003.put("GQ", ".");
        na003.put("PL", ".");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "./.");
        na004.put("AD", ".");
        na004.put("DP", ".");
        na004.put("GQ", ".");
        na004.put("PL", ".");
        
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);
        
        
        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(1, result.size());

        Variant getVar0 = result.get(0);
        VariantSourceEntry getFile0 = getVar0.getSourceEntry(source.getFileId(), source.getStudyId());
        
        Map<String, String> na001Data = getFile0.getSampleData("NA001");
        assertEquals("./.", na001Data.get("GT"));
        assertEquals(".", na001Data.get("AD"));
        assertEquals(".", na001Data.get("DP"));
        assertEquals(".", na001Data.get("GQ"));
        assertEquals(".", na001Data.get("PL"));
        
        Map<String, String> na002Data = getFile0.getSampleData("NA002");
        assertEquals("1/1", na002Data.get("GT"));
        assertEquals("0,2", na002Data.get("AD"));
        assertEquals("2", na002Data.get("DP"));
        assertEquals("6", na002Data.get("GQ"));
        assertEquals("71,6,0", na002Data.get("PL"));
        
        Map<String, String> na003Data = getFile0.getSampleData("NA003");
        assertEquals("./.", na003Data.get("GT"));
        assertEquals(".", na003Data.get("AD"));
        assertEquals(".", na003Data.get("DP"));
        assertEquals(".", na003Data.get("GQ"));
        assertEquals(".", na003Data.get("PL"));
        
        Map<String, String> na004Data = getFile0.getSampleData("NA004");
        assertEquals("./.", na004Data.get("GT"));
        assertEquals(".", na004Data.get("AD"));
        assertEquals(".", na004Data.get("DP"));
        assertEquals(".", na004Data.get("GQ"));
        assertEquals(".", na004Data.get("PL"));
    }
    
    @Test
    public void testParseInfo() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        source.setSamples(sampleNames);
        String line ="1\t123456\t.\tT\tC,G\t110\tPASS\tAN=3;AC=1,2;AF=0.125,0.25;DP=63;NS=4;MQ=10685\tGT:AD:DP:GQ:PL\t" 
                + "0/1:10,5:17:94:94,0,286\t0/2:3,8:15:43:222,0,43\t0/0:.:18:.:.\t0/2:7,6:13:0:162,0,180"; // 4 samples
        
        // Initialize expected variants
        Variant var0 = new Variant("1", 123456, 123456, "T", "C");
        VariantSourceEntry file0 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var0.addSourceEntry(file0);

        Variant var1 = new Variant("1", 123456, 123456, "T", "G");
        VariantSourceEntry file1 = new VariantSourceEntry(source.getFileId(), source.getStudyId());
        var1.addSourceEntry(file1);
        
        
        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "0/1");
        na001.put("AD", "10,5");
        na001.put("DP", "17");
        na001.put("GQ", "94");
        na001.put("PL", "94,0,286");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "0/1");
        na002.put("AD", "3,8");
        na002.put("DP", "15");
        na002.put("GQ", "43");
        na002.put("PL", "222,0,43");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "0/0");
        na003.put("AD", ".");
        na003.put("DP", "18");
        na003.put("GQ", ".");
        na003.put("PL", ".");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "0/1");
        na004.put("AD", "7,6");
        na004.put("DP", "13");
        na004.put("GQ", "0");
        na004.put("PL", "162,0,180");

        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var0.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);

        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var1.getSourceEntry(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);


        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        VariantSourceEntry getFile0 = getVar0.getSourceEntry(source.getFileId(), source.getStudyId());
        assertEquals(4, Integer.parseInt(getFile0.getAttribute("NS")));
//        assertEquals(2, Integer.parseInt(getFile0.getAttribute("AN")));
        assertEquals(1, Integer.parseInt(getFile0.getAttribute("AC")));
        assertEquals(0.125, Double.parseDouble(getFile0.getAttribute("AF")), 1e-8);
        assertEquals(63, Integer.parseInt(getFile0.getAttribute("DP")));
        assertEquals(10685, Integer.parseInt(getFile0.getAttribute("MQ")));
        assertEquals(1, Integer.parseInt(getFile0.getAttribute("MQ0")));

        Variant getVar1 = result.get(1);
        VariantSourceEntry getFile1 = getVar1.getSourceEntry(source.getFileId(), source.getStudyId());
        assertEquals(4, Integer.parseInt(getFile1.getAttribute("NS")));
//        assertEquals(2, Integer.parseInt(getFile1.getAttribute("AN")));
        assertEquals(2, Integer.parseInt(getFile1.getAttribute("AC")));
        assertEquals(0.25, Double.parseDouble(getFile1.getAttribute("AF")), 1e-8);
        assertEquals(63, Integer.parseInt(getFile1.getAttribute("DP")));
        assertEquals(10685, Integer.parseInt(getFile1.getAttribute("MQ")));
        assertEquals(1, Integer.parseInt(getFile1.getAttribute("MQ0")));
    }
    
}
