package org.opencb.biodata.models.feature;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class GenotypeTest {
    
    @Test
    public void testEncodeNotPhased() {
        Genotype gt00 = new Genotype("0/0");
        Genotype gt01 = new Genotype("0/1");
        Genotype gt10 = new Genotype("1/0");
        Genotype gt11 = new Genotype("1/1");
        Genotype gt12 = new Genotype("1/2");
        Genotype gt21 = new Genotype("2/1");
        
        assertEquals(0, gt00.encode());
        assertEquals(1, gt01.encode());
        assertEquals(10, gt10.encode());
        assertEquals(11, gt11.encode());
        assertEquals(12, gt12.encode());
        assertEquals(21, gt21.encode());
    }
    
    @Test
    public void testEncodePhased() {
        Genotype gt00 = new Genotype("0|0");
        Genotype gt01 = new Genotype("0|1");
        Genotype gt10 = new Genotype("1|0");
        Genotype gt11 = new Genotype("1|1");
        Genotype gt12 = new Genotype("1|2");
        Genotype gt21 = new Genotype("2|1");
        
        assertEquals(100, gt00.encode());
        assertEquals(101, gt01.encode());
        assertEquals(110, gt10.encode());
        assertEquals(111, gt11.encode());
        assertEquals(112, gt12.encode());
        assertEquals(121, gt21.encode());
    }
    
}
