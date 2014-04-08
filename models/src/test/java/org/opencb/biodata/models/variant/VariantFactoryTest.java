package org.opencb.biodata.models.variant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantFactoryTest extends TestCase {
    
    private String fileName = "filename.vcf";
    private String fileId = "file1";
    private String studyId = "study1";
    
    public void testCreateVariantFromVcfSameLengthRefAlt() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        
        // Test when there are differences at the end of the sequence
        String[] fields = new String[] { "1", "1000", "rs123", "TCACCC", "TGACGG", ".", ".", "."};
        
        List<Variant> expResult = new LinkedList<>();
//        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) + 1, Integer.parseInt(fields[1]) + 1, "C", "G"));
//        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) + 4, Integer.parseInt(fields[1]) + 5, "CC", "GG"));
        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) + 1, Integer.parseInt(fields[1]) + 5, "CACCC", "GACGG"));
        
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
        
        // Test when there are not differences at the end of the sequence
        fields = new String[] { "1", "1000", "rs123", "TCACCC", "TGACGC", ".", ".", "."};
        
        expResult = new LinkedList<>();
//        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) + 1, Integer.parseInt(fields[1]) + 1, "C", "G"));
//        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) + 4, Integer.parseInt(fields[1]) + 4, "C", "G"));
        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) + 1, Integer.parseInt(fields[1]) + 5, "CACCC", "GACGC"));
        
        result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
    }

    public void testCreateVariantFromVcfInsertionEmptyRef() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        String[] fields = new String[] { "1", "1000", "rs123", ".", "TGACGG", ".", ".", "."};
        
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]) - 1, Integer.parseInt(fields[1]) + fields[4].length(), "", fields[4]));
        
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
    }
    
    public void testCreateVariantFromVcfDeletionEmptyAlt() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        String[] fields = new String[] { "1", "1000", "rs123", "TCACCC", ".", ".", ".", "."};
        
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[1]) + fields[3].length() - 1, fields[3], ""));
        
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
    }
    
    public void testCreateVariantFromVcfIndelNotEmptyFields() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        String[] fields = new String[] { "1", "1000", "rs123", "CGATT", "TAC", ".", ".", "."};
        
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[1]) + fields[3].length() - 1, fields[3], fields[4]));
        
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
    }
    
    public void testCreateVariantFromVcfCoLocatedVariants_MainFields() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        String[] fields = new String[] { "1", "10040", "rs123", "TGACGTAACGATT", "T,TGACGTAACGGTT,TGACGTAATAC", ".", ".", ".", "GT", 
                                         "0/0", "0/1", "0/2", "1/2"}; // 4 samples
        
        // Check proper conversion of main fields
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant(fields[0], 10041, 10041 + "GACGTAACGATT".length() - 1, "GACGTAACGATT", ""));
        expResult.add(new Variant(fields[0], 10050, 10050 + "ATT".length() - 1, "ATT", "GTT"));
        expResult.add(new Variant(fields[0], 10048, 10048 + "CGATT".length() - 1, "CGATT", "TAC"));
        
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
    }
    
    
    public void testCreateVariant_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005");
        String[] fields = new String[] { "1", "10040", "rs123", "T", "C", ".", ".", ".", "GT", 
                                         "0/0", "0/1", "0/.", "./1", "1/1"}; // 5 samples
        
        // Initialize expected variants
        Variant var0 = new Variant(fields[0], 10041, 10041 + "C".length() - 1, "T", "C");
        ArchivedVariantFile file0 = new ArchivedVariantFile(fileName, fileId, studyId);
        var0.addFile(file0);
        
        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "T/T");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "T/C");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "T/.");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "./C");
        Map<String, String> na005 = new HashMap<>();
        na005.put("GT", "C/C");
        
        var0.getFile(fileId).addSampleData(sampleNames.get(0), na001);
        var0.getFile(fileId).addSampleData(sampleNames.get(1), na002);
        var0.getFile(fileId).addSampleData(sampleNames.get(2), na003);
        var0.getFile(fileId).addSampleData(sampleNames.get(3), na004);
        var0.getFile(fileId).addSampleData(sampleNames.get(4), na005);
        
        // Check proper conversion of samples
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(1, result.size());
        
        Variant getVar0 = result.get(0);
        assertEquals(var0.getFile(fileId).getSamplesData(), getVar0.getFile(fileId).getSamplesData());
    }
    
    public void testCreateVariantFromVcfCoLocatedVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        String[] fields = new String[] { "1", "10040", "rs123", "T", "C,GC", ".", ".", ".", "GT:GL", 
                                         "0/0:1,2,3,4,5,6,7,8,9,10", "0/1:1,2,3,4,5,6,7,8,9,10", "0/2:1,2,3,4,5,6,7,8,9,10", 
                                         "1/1:1,2,3,4,5,6,7,8,9,10", "1/2:1,2,3,4,5,6,7,8,9,10", "2/2:1,2,3,4,5,6,7,8,9,10"}; // 6 samples
        
        // Initialize expected variants
        Variant var0 = new Variant(fields[0], 10041, 10041 + "C".length() - 1, "T", "C");
        ArchivedVariantFile file0 = new ArchivedVariantFile(fileName, fileId, studyId);
        var0.addFile(file0);
        
        Variant var1 = new Variant(fields[0], 10050, 10050 + "GC".length() - 1, "T", "GC");
        ArchivedVariantFile file1 = new ArchivedVariantFile(fileName, fileId, studyId);
        var1.addFile(file1);
        
        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "T/T");
        na001.put("GL", "1,1,1");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "T/C");
        na002.put("GL", "1,2,3");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "T/GC");
        na003.put("GL", "1,4,6");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "C/C");
        na004.put("GL", "1,2,3");
        Map<String, String> na005 = new HashMap<>();
        na005.put("GT", "C/GC");
        na005.put("GL", "3,5,6");
        Map<String, String> na006 = new HashMap<>();
        na006.put("GT", "GC/GC");
        na006.put("GL", "1,4,6");
        
        var0.getFile(fileId).addSampleData(sampleNames.get(0), na001);
        var0.getFile(fileId).addSampleData(sampleNames.get(1), na002);
        var0.getFile(fileId).addSampleData(sampleNames.get(3), na004);
        var0.getFile(fileId).addSampleData(sampleNames.get(4), na005);
        
        var1.getFile(fileId).addSampleData(sampleNames.get(0), na001);
        var1.getFile(fileId).addSampleData(sampleNames.get(2), na003);
        var1.getFile(fileId).addSampleData(sampleNames.get(4), na005);
        var1.getFile(fileId).addSampleData(sampleNames.get(5), na006);
        
        // Check proper conversion of samples
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(2, result.size());
        
        Variant getVar0 = result.get(0);
        assertEquals(var0.getFile(fileId).getSamplesData(), getVar0.getFile(fileId).getSamplesData());
        
        Variant getVar1 = result.get(1);
        assertEquals(var1.getFile(fileId).getSamplesData(), getVar1.getFile(fileId).getSamplesData());
    }
}
