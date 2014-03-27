package org.opencb.biodata.models.variant;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    
    public void testCreateVariantFromVcfCoLocatedVariants() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        String[] fields = new String[] { "1", "10040", "rs123", "TGACGTAACGATT", "T,TGACGTAACGGTT,TGACGTAATAC", ".", ".", ".", 
                                         "0/0", "0/1", "0/2", "1/2"}; // 4 samples
        
        // Check proper conversion of main fields
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant(fields[0], 10041, 10041 + "GACGTAACGATT".length() - 1, "GACGTAACGATT", ""));
        expResult.add(new Variant(fields[0], 10050, 10050 + "ATT".length() - 1, "ATT", "GTT"));
        expResult.add(new Variant(fields[0], 10048, 10048 + "CGATT".length() - 1, "CGATT", "TAC"));
        
        List<Variant> result = VariantFactory.createVariantFromVcf(fileName, fileId, studyId, sampleNames, fields);
        assertEquals(expResult, result);
        
        // Check proper conversion of samples
        
    }
}
