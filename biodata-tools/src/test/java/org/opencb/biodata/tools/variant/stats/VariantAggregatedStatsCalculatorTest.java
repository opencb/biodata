package org.opencb.biodata.tools.variant.stats;

import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantAggregatedVcfFactory;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.test.GenericTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedStatsCalculatorTest extends GenericTest {

    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");
    private VariantAggregatedVcfFactory factory = new VariantAggregatedVcfFactory();

    @Test
    public void parseAC_AN() {
        String line = "1\t54722\t.\tTTC\tT,TCTC\t999\tPASS\tDP4=3122,3282,891,558;DP=22582;INDEL;IS=3,0.272727;VQSLOD=6.76;AN=3854;AC=889,61;TYPE=del,ins;HWE=0;ICF=-0.155251";   // structure like uk10k

        List<Variant> variants = factory.create(source, line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(2904, stats.getRefAlleleCount());
        assertEquals(889, stats.getAltAlleleCount());

        stats = variants.get(1).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(2904, stats.getRefAlleleCount());
        assertEquals(61, stats.getAltAlleleCount());
        assertEquals(0.015827711, stats.getMaf(), 0.0001);
    }

    @Test
    public void parseGTC () {
        String line = "20\t61098\trs6078030\tC\tT\t51254.56\tPASS\tAC=225;AN=996;GTC=304,163,31";   // structure like gonl

        List<Variant> variants = factory.create(source, line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(new Integer(304), stats.getGenotypesCount().get(new Genotype("0/0", "C", "T")));
        assertEquals(new Integer(163), stats.getGenotypesCount().get(new Genotype("0/1", "C", "T")));
        assertEquals(new Integer(31),  stats.getGenotypesCount().get(new Genotype("T/T", "C", "T")));
        assertEquals(0.225903614, stats.getMaf(), 0.0001);
    }

    @Test
    public void parseCohorts() {
        String line = "1\t54722\t.\tTTC\tT,TCTC\t999\tPASS\tDP4=3122,3282,891,558;DP=22582;INDEL;IS=3,0.272727;" +
                "VQSLOD=6.76;C1_AN=3854;C1_AC=889,61;C2_AN=2000;C2_AC=400,20;TYPE=del,ins;HWE=0;ICF=-0.155251";

        List<Variant> variants = factory.create(source, line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(new HashSet<>(Arrays.asList("C1", "C2")));
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("C1");
        assertEquals(2904, stats.getRefAlleleCount());
        assertEquals(889, stats.getAltAlleleCount());

        stats = variants.get(1).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("C1");
        assertEquals(2904, stats.getRefAlleleCount());
        assertEquals(61, stats.getAltAlleleCount());
        assertEquals(0.015827711, stats.getMaf(), 0.0001);

        stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("C2");
        assertEquals(1580, stats.getRefAlleleCount());
        assertEquals(400, stats.getAltAlleleCount());

        stats = variants.get(1).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("C2");
        assertEquals(1580, stats.getRefAlleleCount());
        assertEquals(20, stats.getAltAlleleCount());
        assertEquals(0.01, stats.getMaf(), 0.0001);
    }

    @Test
    public void parseCustomGTC () {
        String line = "1\t1225579\t.\tG\tA,C\t170.13\tPASS\tAC=3,8;AN=534;AF=0.006,0.015;HPG_GTC=0/0:258,0/1:1,0/2:6,1/1:1,1/2:0,2/2:1,./.:0";  // structure like HPG

        Properties properties = new Properties();
        properties.put("ALL.GTC", "HPG_GTC");
        properties.put("ALL.AC", "AC");
        properties.put("ALL.AN", "AN");
        properties.put("ALL.AF", "AF");
        List<Variant> variants = new VariantAggregatedVcfFactory().create(source, line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(properties);
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("ALL");
        assertEquals(523, stats.getRefAlleleCount());
        assertEquals(3, stats.getAltAlleleCount());
        assertEquals(0.006, stats.getAltAlleleFreq(), 0.0001);
        assertEquals(3.0/534, stats.getMaf(), 0.0001);
        assertEquals(new Integer(258), stats.getGenotypesCount().get(new Genotype("0/0", "G", "A")));
        assertEquals(new Integer(1), stats.getGenotypesCount().get(new Genotype("0/1", "G", "A")));
        assertEquals(new Integer(1), stats.getGenotypesCount().get(new Genotype("A/A", "G", "A")));
        assertEquals(new Integer(6), stats.getGenotypesCount().get(new Genotype("0/2", "G", "A")));
        assertEquals(new Integer(0), stats.getGenotypesCount().get(new Genotype("./.", "G", "A")));

        stats = variants.get(1).getSourceEntry(source.getFileId(), source.getStudyId()).getCohortStats("ALL");
        assertEquals(new Integer(6), stats.getGenotypesCount().get(new Genotype("0/1", "G", "C")));

    }

    @Test
    public void parseWithGTS () {
        String line = "1\t861255\t.\tA\tG\t.\tPASS\tAC=2;AF=0.0285714285714286;AN=70;GTS=GG,GA,AA;GTC=1,0,34";

        List<Variant> variants = factory.create(source, line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getSourceEntry(source.getFileId(), source.getStudyId()).getStats();
        assertEquals(new Integer(34), stats.getGenotypesCount().get(new Genotype("0/0", "A", "G")));
        assertEquals(new Integer(0),  stats.getGenotypesCount().get(new Genotype("0/1", "A", "G")));
        assertEquals(new Integer(1),  stats.getGenotypesCount().get(new Genotype("G/G", "A", "G")));
        assertEquals(2.0/70, stats.getMaf(), 0.0001);
    }
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