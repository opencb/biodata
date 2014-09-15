package org.opencb.biodata.models.variant;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
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

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000 + "TCACCC".length() - 1, "TCACCC", ""));

        List<Variant> result = factory.create(source, line);
        assertEquals(expResult, result);
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
        
        line = "1\t1000\trs123\tATC\t.\t.\t.\t.";
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
        ArchivedVariantFile file0 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var0.addFile(file0);

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

        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(4), na005);

        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(1, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(var0.getFile(source.getFileId(), source.getStudyId()).getSamplesData(), getVar0.getFile(source.getFileId(), source.getStudyId()).getSamplesData());
    }

    @Test
    public void testCreateVariantFromVcfMultiallelicVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        source.setSamples(sampleNames);
        String line ="1\t123456\t.\tT\tC,G\t110\tPASS\t.\tGT:AD:DP:GQ:PL\t0/1:10,5:17:94:94,0,286\t0/2:3,8:15:43:222,0,43\t0/0:.:18:.:.\t0/2:7,6:13:99:162,0,180"; // 4 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 123456, 123456, "T", "C");
        ArchivedVariantFile file0 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var0.addFile(file0);

        Variant var1 = new Variant("1", 123456, 123456, "T", "G");
        ArchivedVariantFile file1 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var1.addFile(file1);


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
        na004.put("GQ", "99");
        na004.put("PL", "162,0,180");

        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);

        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);


        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(var0.getFile(source.getFileId(), source.getStudyId()).getSamplesData(), getVar0.getFile(source.getFileId(), source.getStudyId()).getSamplesData());

        Variant getVar1 = result.get(1);
        assertEquals(var1.getFile(source.getFileId(), source.getStudyId()).getSamplesData(), getVar1.getFile(source.getFileId(), source.getStudyId()).getSamplesData());
    }

    @Test
    public void testCreateVariantFromVcfCoLocatedVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        source.setSamples(sampleNames);
        String line = "1\t10040\trs123\tT\tC,GC\t.\t.\t.\tGT:GL\t0/0:1,2,3,4,5,6,7,8,9,10\t0/1:1,2,3,4,5,6,7,8,9,10\t0/2:1,2,3,4,5,6,7,8,9,10\t1/1:1,2,3,4,5,6,7,8,9,10\t1/2:1,2,3,4,5,6,7,8,9,10\t2/2:1,2,3,4,5,6,7,8,9,10"; // 6 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 10041, 10041 + "C".length() - 1, "T", "C");
        ArchivedVariantFile file0 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var0.addFile(file0);

        Variant var1 = new Variant("1", 10050, 10050 + "GC".length() - 1, "T", "GC");
        ArchivedVariantFile file1 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var1.addFile(file1);

        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "0/0");
        na001.put("GL", "1,1,1");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "0/1");
        na002.put("GL", "1,2,3");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "0/1");
        na003.put("GL", "1,4,6");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "1/1");
        na004.put("GL", "1,2,3");
        Map<String, String> na005_C = new HashMap<>();
        na005_C.put("GT", "1/GC");
        na005_C.put("GL", "3,5,6");
        Map<String, String> na005_GC = new HashMap<>();
        na005_GC.put("GT", "C/1");
        na005_GC.put("GL", "3,5,6");
        Map<String, String> na006 = new HashMap<>();
        na006.put("GT", "1/1");
        na006.put("GL", "1,4,6");

        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(4), na005_C);

        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(4), na005_GC);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(5), na006);

        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(var0.getFile(source.getFileId(), source.getStudyId()).getSamplesData(), getVar0.getFile(source.getFileId(), source.getStudyId()).getSamplesData());

        Variant getVar1 = result.get(1);
        assertEquals(var1.getFile(source.getFileId(), source.getStudyId()).getSamplesData(), getVar1.getFile(source.getFileId(), source.getStudyId()).getSamplesData());
    }
    
    @Test
    public void testParseInfo() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        source.setSamples(sampleNames);
        String line ="1\t123456\t.\tT\tC,G\t110\tPASS\tAN=3;AC=1,2;AF=0.125,0.25;DP=63;NS=4;MQ=10685\tGT:AD:DP:GQ:PL\t" 
                + "0/1:10,5:17:94:94,0,286\t0/2:3,8:15:43:222,0,43\t0/0:.:18:.:.\t0/2:7,6:13:0:162,0,180"; // 4 samples
        
        // Initialize expected variants
        Variant var0 = new Variant("1", 123456, 123456, "T", "C");
        ArchivedVariantFile file0 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var0.addFile(file0);

        Variant var1 = new Variant("1", 123456, 123456, "T", "G");
        ArchivedVariantFile file1 = new ArchivedVariantFile(source.getFileId(), source.getStudyId());
        var1.addFile(file1);
        
        
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
        na004.put("GQ", "99");
        na004.put("PL", "162,0,180");

        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(0), na001);
        var0.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);

        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(1), na002);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(2), na003);
        var1.getFile(source.getFileId(), source.getStudyId()).addSampleData(sampleNames.get(3), na004);


        // Check proper conversion of samples
        List<Variant> result = factory.create(source, line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        ArchivedVariantFile getFile0 = getVar0.getFile(source.getFileId(), source.getStudyId());
        assertEquals(2, Integer.parseInt(getFile0.getAttribute("NS")));
        assertEquals(2, Integer.parseInt(getFile0.getAttribute("AN")));
        assertEquals(1, Integer.parseInt(getFile0.getAttribute("AC")));
        assertEquals(0.125, Double.parseDouble(getFile0.getAttribute("AF")), 1e-8);
        assertEquals(35, Integer.parseInt(getFile0.getAttribute("DP")));
        assertEquals(8836, Integer.parseInt(getFile0.getAttribute("MQ")));
        assertEquals(0, Integer.parseInt(getFile0.getAttribute("MQ0")));

        Variant getVar1 = result.get(1);
        ArchivedVariantFile getFile1 = getVar1.getFile(source.getFileId(), source.getStudyId());
        assertEquals(3, Integer.parseInt(getFile1.getAttribute("NS")));
        assertEquals(2, Integer.parseInt(getFile1.getAttribute("AN")));
        assertEquals(2, Integer.parseInt(getFile1.getAttribute("AC")));
        assertEquals(0.25, Double.parseDouble(getFile1.getAttribute("AF")), 1e-8);
        assertEquals(46, Integer.parseInt(getFile1.getAttribute("DP")));
        assertEquals(1849, Integer.parseInt(getFile1.getAttribute("MQ")));
        assertEquals(1, Integer.parseInt(getFile1.getAttribute("MQ0")));
    }
    
}
