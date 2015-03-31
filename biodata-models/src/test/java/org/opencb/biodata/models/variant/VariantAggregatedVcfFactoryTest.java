package org.opencb.biodata.models.variant;

import org.junit.Ignore;
import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.test.GenericTest;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;


/** 
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactoryTest extends GenericTest {
    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");
    private VariantAggregatedVcfFactory factory = new VariantAggregatedVcfFactory();
    
    @Test
    public void parseAC_AN() {
        String line = "1\t54722\t.\tTTC\tT,TCTC\t999\tPASS\tDP4=3122,3282,891,558;DP=22582;INDEL;IS=3,0.272727;VQSLOD=6.76;AN=3854;AC=889,61;TYPE=del,ins;HWE=0;ICF=-0.155251";   // structure like uk10k

        List<Variant> variants = factory.create(source, line);
        
        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(2904, stats.getRefAlleleCount());
        assertEquals(889, stats.getAltAlleleCount());
        
        stats = variants.get(1).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(2904, stats.getRefAlleleCount());
        assertEquals(61, stats.getAltAlleleCount());
    }
    
    @Test
    public void parseGTC () {
        String line = "20\t61098\trs6078030\tC\tT\t51254.56\tPASS\tAC=225;AN=996;GTC=304,163,31";   // structure like gonl

        List<Variant> variants = factory.create(source, line);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(stats.getGenotypesCount().get(new Genotype("0/0", "C", "T")), new Integer(304));
        assertEquals(stats.getGenotypesCount().get(new Genotype("0/1", "C", "T")), new Integer(163));
        assertEquals(stats.getGenotypesCount().get(new Genotype("T/T", "C", "T")), new Integer(31));
        
    }
    
    @Test
    public void parseCustomGTC () {
        String line = "1\t1225579\t.\tG\tA,C\t170.13\tPASS\tAC=3,8;AN=534;AF=0.006,0.015;HPG_GTC=0/0:258,0/1:1,0/2:6,1/1:1,1/2:0,2/2:1,./.:0";  // structure like HPG

        Properties properties = new Properties();
        properties.put("ALL.GTC", "HPG_GTC");
        properties.put("ALL.AC", "AC");
        properties.put("ALL.AN", "AN");
        properties.put("ALL.AF", "AF");
        List<Variant> variants = new VariantAggregatedVcfFactory(properties).create(source, line);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("ALL");
        assertEquals(523, stats.getRefAlleleCount());
        assertEquals(3, stats.getAltAlleleCount());
        assertEquals(0.006, stats.getAltAlleleFreq(), 0.0001);
        assertEquals(new Integer(258), stats.getGenotypesCount().get(new Genotype("0/0", "G", "A")));
        assertEquals(new Integer(1), stats.getGenotypesCount().get(new Genotype("0/1", "G", "A")));
        assertEquals(new Integer(1), stats.getGenotypesCount().get(new Genotype("A/A", "G", "A")));
        assertEquals(new Integer(6), stats.getGenotypesCount().get(new Genotype("0/2", "G", "A")));
        assertEquals(new Integer(0), stats.getGenotypesCount().get(new Genotype("./.", "G", "A")));
        
        stats = variants.get(1).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("ALL");
        assertEquals(new Integer(6), stats.getGenotypesCount().get(new Genotype("0/1", "G", "C")));
        
    }
    
    @Test
    public void getGenotype() {
        VariantAggregatedVcfFactory factory = new VariantAggregatedVcfFactory();
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            VariantAggregatedVcfFactory.getGenotype(i, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }

        Integer alleles[] = new Integer[2];
        VariantAggregatedVcfFactory.getGenotype(0, alleles);    // 0/0
        assertEquals(alleles[0], alleles[1]);
        VariantAggregatedVcfFactory.getGenotype(2, alleles);    // 1/1
        assertEquals(alleles[0], alleles[1]);
        VariantAggregatedVcfFactory.getGenotype(5, alleles);    // 2/2
        assertEquals(alleles[0], alleles[1]);
        assertEquals(alleles[0], new Integer(2));
    }
}