package org.opencb.biodata.models.variant;

import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.test.GenericTest;

import java.util.List;

import static org.junit.Assert.*;


/**
 * // TODO check multiallelic
 * 
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactoryTest extends GenericTest {
    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");
    private VariantAggregatedVcfFactory factory = new VariantAggregatedVcfFactory();
    @Test
    public void parseGTC () {
        String line = "20\t61098\trs6078030\tC\tT\t51254.56\tPASS\tAC=225;AN=996;GTC=304,163,31";   // from gonl

        List<Variant> variants = factory.create(source, line);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(stats.getGenotypesCount().get(new Genotype("0/0", "C", "T")), new Integer(304));
        assertEquals(stats.getGenotypesCount().get(new Genotype("0/1", "C", "T")), new Integer(163));
        assertEquals(stats.getGenotypesCount().get(new Genotype("T/T", "C", "T")), new Integer(31));
        
    }
    
    @Test
    public void getGenotype() {
        VariantAggregatedVcfFactory factory = new VariantAggregatedVcfFactory();
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            factory.getGenotype(i, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }
    }
}