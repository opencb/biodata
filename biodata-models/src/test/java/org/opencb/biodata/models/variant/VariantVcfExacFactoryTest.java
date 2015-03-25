package org.opencb.biodata.models.variant;

import org.junit.Test;
import org.opencb.commons.test.GenericTest;

import static org.junit.Assert.*;

public class VariantVcfExacFactoryTest extends GenericTest {

    @Test
    public void testGetHeterozygousGenotype() throws Exception {
        VariantVcfExacFactory factory = new VariantVcfExacFactory();
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            VariantVcfExacFactory.getHeterozygousGenotype(i, 4, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }
        
        Integer alleles[] = new Integer[2];
        VariantVcfExacFactory.getHeterozygousGenotype(3, 3, alleles);
        assertEquals(alleles[0], new Integer(1));
        assertEquals(alleles[1], new Integer(2));
        VariantVcfExacFactory.getHeterozygousGenotype(4, 4, alleles);
        assertEquals(alleles[0], new Integer(1));
        assertEquals(alleles[1], new Integer(2));
    }

    @Test
    public void testGetHomozygousGenotype() throws Exception {
        VariantVcfExacFactory factory = new VariantVcfExacFactory();
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            VariantVcfExacFactory.getHomozygousGenotype(i, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }

        Integer alleles[] = new Integer[2];
        VariantVcfExacFactory.getHomozygousGenotype(3, alleles);    // 0/0
        assertEquals(alleles[0], alleles[1]);
    }
}