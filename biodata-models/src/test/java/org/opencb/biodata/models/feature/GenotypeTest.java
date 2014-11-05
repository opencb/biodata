package org.opencb.biodata.models.feature;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GenotypeTest {
    
    @Test
    public void testEncodeUnphased() {
        Genotype gt00 = new Genotype("0/0");
        Genotype gt01 = new Genotype("0/1");
        Genotype gt10 = new Genotype("1/0");
        Genotype gt11 = new Genotype("1/1");
        Genotype gt12 = new Genotype("1/2");
        Genotype gt21 = new Genotype("2/1");
        
        assertEquals(-0, gt00.encode());
        assertEquals(-1, gt01.encode());
        assertEquals(-10, gt10.encode());
        assertEquals(-11, gt11.encode());
        assertEquals(-12, gt12.encode());
        assertEquals(-21, gt21.encode());
    }
    
    @Test
    public void testEncodePhased() {
        Genotype gt00 = new Genotype("0|0");
        Genotype gt01 = new Genotype("0|1");
        Genotype gt10 = new Genotype("1|0");
        Genotype gt11 = new Genotype("1|1");
        Genotype gt12 = new Genotype("1|2");
        Genotype gt21 = new Genotype("2|1");
        
        assertEquals(0, gt00.encode());
        assertEquals(1, gt01.encode());
        assertEquals(10, gt10.encode());
        assertEquals(11, gt11.encode());
        assertEquals(12, gt12.encode());
        assertEquals(21, gt21.encode());
    }
    
    @Test
    public void testGetNormalizedAllelesIdx() {
        Genotype gt00 = new Genotype("0|0");
        Genotype gt01 = new Genotype("0|1");
        Genotype gt10 = new Genotype("1|0");
        Genotype gt11 = new Genotype("1|1");
        Genotype gt120 = new Genotype("1|2|0");
        Genotype gt010 = new Genotype("0|1|0");
        
        assertArrayEquals(new int[] {0, 0}, gt00.getNormalizedAllelesIdx());
        assertArrayEquals(new int[] {0, 1}, gt01.getNormalizedAllelesIdx());
        assertArrayEquals(new int[] {0, 1}, gt10.getNormalizedAllelesIdx());
        assertArrayEquals(new int[] {1, 1}, gt11.getNormalizedAllelesIdx());
        assertArrayEquals(new int[] {0, 1, 2}, gt120.getNormalizedAllelesIdx());
        assertArrayEquals(new int[] {0, 0, 1}, gt010.getNormalizedAllelesIdx());
    }
    
}
