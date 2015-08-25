package org.opencb.biodata.tools.variant.stats;

import org.junit.Test;
import org.opencb.biodata.models.variant.VariantAggregatedVcfFactory;
import org.opencb.commons.test.GenericTest;

import static org.junit.Assert.*;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedStatsCalculatorTest extends GenericTest {

    @Test
    public void getGenotype() {
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            VariantAggregatedStatsCalculator.getGenotype(i, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }

        Integer alleles[] = new Integer[2];
        VariantAggregatedStatsCalculator.getGenotype(0, alleles);    // 0/0
        assertEquals(alleles[0], alleles[1]);
        VariantAggregatedStatsCalculator.getGenotype(2, alleles);    // 1/1
        assertEquals(alleles[0], alleles[1]);
        VariantAggregatedStatsCalculator.getGenotype(5, alleles);    // 2/2
        assertEquals(alleles[0], alleles[1]);
        assertEquals(alleles[0], new Integer(2));
    }
}