package org.opencb.biodata.tools.variant.stats;

import org.junit.Test;
import org.opencb.biodata.formats.variant.vcf4.VariantAggregatedVcfFactory;
import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.opencb.biodata.tools.variant.VariantVcfHtsjdkReader;
import org.opencb.commons.io.DataReader;
import org.opencb.commons.test.GenericTest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jmmut on 2015-08-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedStatsCalculatorTest extends GenericTest {

    private VariantFileMetadata fileMetadata = new VariantFileMetadata("filename.vcf", "fileId");
    protected VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("studyId");
    private VariantAggregatedVcfFactory factory = new VariantAggregatedVcfFactory();

    @Test
    public void parseAC_AN() {
        int an = 3854;
        int ac1 = 889;
        int ac2 = 61;
        int rc = an - ac1 - ac2;
        String line = "1\t54722\t.\tTTC\tT,TCTC\t999\tPASS\t" +
                "DP4=3122,3282,891,558;" +
                "DP=22582;" +
                "INDEL;" +
                "IS=3,0.272727;" +
                "VQSLOD=6.76;" +
                "AN=" + an + ";" +
                "AC=" + ac1 + "," + ac2 + ";" +
                "TYPE=del,ins;" +
                "HWE=0;" +
                "ICF=-0.155251";   // structure like uk10k

        List<Variant> variants = readLine(line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getStudy(metadata.getId()).getStats(StudyEntry.DEFAULT_COHORT);
        assertEquals(an, stats.getAlleleCount().longValue());
        assertEquals(rc, stats.getRefAlleleCount().longValue());
        assertEquals(ac1, stats.getAltAlleleCount().longValue());

        stats = variants.get(1).getStudy(metadata.getId()).getStats(StudyEntry.DEFAULT_COHORT);
        assertEquals(an, stats.getAlleleCount().longValue());
        assertEquals(rc, stats.getRefAlleleCount().longValue());
        assertEquals(ac2, stats.getAltAlleleCount().longValue());
        assertEquals(0.015827711, stats.getMaf(), 0.0001);
    }

    @Test
    public void parseMissing_AF() {
        String line = "1\t54722\t.\tT\tG\t999\tPASS\tAN=0;AC=0;AF=.;GTC=0,0,0";   // structure like gnomad

        List<Variant> variants = readLine(line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getStudy(metadata.getId()).getStats(StudyEntry.DEFAULT_COHORT);
        assertEquals(0, stats.getRefAlleleCount().longValue());
        assertEquals(0, stats.getAltAlleleCount().longValue());
        assertEquals(-1f, stats.getAltAlleleFreq(), 0.01);
    }

    @Test
    public void parseGTC () {
        String line = "20\t61098\trs6078030\tC\tT\t51254.56\tPASS\tAC=225;AN=996;GTC=304,163,31";   // structure like gonl

        List<Variant> variants = readLine(line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getStudy(metadata.getId()).getStats(StudyEntry.DEFAULT_COHORT);
        assertEquals(new Integer(304), stats.getGenotypeCount().get("0/0"));
        assertEquals(new Integer(163), stats.getGenotypeCount().get("0/1"));
        assertEquals(new Integer(31),  stats.getGenotypeCount().get("1/1"));
        assertEquals(304 / 498.0, stats.getGenotypeFreq().get("0/0").floatValue(), 0.0001);
        assertEquals(163 / 498.0, stats.getGenotypeFreq().get("0/1").floatValue(), 0.0001);
        assertEquals(31 / 498.0, stats.getGenotypeFreq().get("1/1").floatValue(), 0.0001);
        assertEquals(0.225903614, stats.getMaf(), 0.0001);
    }

    @Test
    public void parseCustomGTC () {
        String line = "1\t1225579\t.\tG\tA,C\t170.13\tPASS\tAC=3,8;AN=534;AF=0.006,0.015;"
                + "HPG_GTC="
                + "0/0:258,"
                + "0/1:1,"
                + "0/2:6,"
                + "1/1:1,"
                + "1/2:0,"
                + "2/2:1,"
                + "./.:0";  // structure like HPG

        Properties properties = new Properties();
        properties.put("ALL.GTC", "HPG_GTC");
        properties.put("ALL.AC", "AC");
        properties.put("ALL.AN", "AN");
        properties.put("ALL.AF", "AF");
        List<Variant> variants = new VariantNormalizer().apply(new VariantAggregatedVcfFactory().create(metadata, line));
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(properties);
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getStudy(metadata.getId()).getStats(StudyEntry.DEFAULT_COHORT);
        assertEquals(Integer.valueOf(523), stats.getRefAlleleCount());
        assertEquals(Integer.valueOf(3), stats.getAltAlleleCount());
        assertEquals(0.006, stats.getAltAlleleFreq(), 0.0001);
        assertEquals(3.0/534, stats.getMaf(), 0.0001);
        assertEquals(Integer.valueOf(258), stats.getGenotypeCount().get("0/0"));
        assertEquals(Integer.valueOf(1), stats.getGenotypeCount().get("0/1"));
        assertEquals(Integer.valueOf(1), stats.getGenotypeCount().get("1/1"));
        assertEquals(Integer.valueOf(6), stats.getGenotypeCount().get("0/2"));
        assertEquals(Integer.valueOf(0), stats.getGenotypeCount().get("./."));

        stats = variants.get(1).getStudy(metadata.getId()).getStats("ALL");
        assertEquals(Integer.valueOf(6), stats.getGenotypeCount().get("0/1"));

    }

    @Test
    public void parseWithGTS () {
        String line = "1\t861255\t.\tA\tG\t.\tPASS\tAC=2;AF=0.0285714285714286;AN=70;GTS=GG,GA,AA;GTC=1,0,34";

        List<Variant> variants = readLine(line);
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator();
        calculator.calculate(variants);

        VariantStats stats = variants.get(0).getStudy(metadata.getId()).getStats(StudyEntry.DEFAULT_COHORT);
        assertEquals(new Integer(34), stats.getGenotypeCount().get("0/0"));
        assertEquals(new Integer(0),  stats.getGenotypeCount().get("0/1"));
        assertEquals(new Integer(1),  stats.getGenotypeCount().get("1/1"));
        assertEquals(2.0/70, stats.getMaf(), 0.0001);
    }
    
    @Test
    public void getCohorts() {
        Properties properties = new Properties();
        properties.put("EUR.GTC", "EUR_HPG_GTC");
        properties.put("EUR.AC", "EUR_AC");
        properties.put("EUR.AN", "EUR_AN");
        properties.put("EUR.AF", "EUR_AF");
        properties.put("ALL.AN", "AN");
        properties.put("ALL.AF", "AF");
        properties.put(VariantAggregatedEVSStatsCalculator.GROUPS_ORDER, "EUR,ALL");

        assertEquals(new LinkedHashSet<>(Arrays.asList("EUR", "ALL")), VariantAggregatedStatsCalculator.getCohorts(properties));
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

    private List<Variant> readLine(String line) {
        return new VariantNormalizer().apply(factory.create(metadata, line));
    }

    @Test
    public void testGnomadGenomes_v3_1_2() {
        Properties properties = new Properties();
        properties.put("ALL.AC",   "AC");
        properties.put("ALL.AN",   "AN");
        properties.put("ALL.AF",   "AF");
        properties.put("ALL.HOMALT",  "nhomalt");
        properties.put("AFR.AC",   "AC_afr");
        properties.put("AFR.AN",   "AN_afr");
        properties.put("AFR.AF",   "AF_afr");
        properties.put("AFR.HOMALT",  "nhomalt_afr");

        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(properties);

        VariantNormalizer normalizer = new VariantNormalizer();
        VariantVcfHtsjdkReader vcfReader = new VariantVcfHtsjdkReader(
                this.getClass().getResourceAsStream("/datasets/gnomad_genomes/gnomad.genomes.v3.1.2.sites.small.vcf"), metadata);
        DataReader<Variant> reader = vcfReader.then(normalizer);

        reader.open();
        reader.pre();
        normalizer.configure(vcfReader.getVCFHeader());

        List<Variant> read = reader.read(3);
        reader.post();
        reader.close();

        assertEquals(2, read.size());

        org.opencb.biodata.models.variant.avro.VariantStats v1_expected_ALL = new org.opencb.biodata.models.variant.avro.VariantStats("ALL", -1, 1, 28186, 28174, 8, (float) 0.99972, (float) 2.8E-4, -1, -1, map("1/1", 3, "2/2", 4), map(), map("PASS", 1), map("PASS", 1.0), -1, (float) -1.0, (float) 1.4191442E-4, (float) -1.0, "A", null);
        org.opencb.biodata.models.variant.avro.VariantStats v1_expected_AFR = new org.opencb.biodata.models.variant.avro.VariantStats("AFR", -1, 1,  7616,  7616, 0, (float) 1      , (float) 0     , -1, -1, map("1/1", 0, "2/2", 0), map(), map("PASS", 1), map("PASS", 1.0), -1, (float) -1.0, (float) 0           , (float) -1.0, "C", null);
        org.opencb.biodata.models.variant.avro.VariantStats v2_expected_ALL = new org.opencb.biodata.models.variant.avro.VariantStats("ALL", -1, 1, 28186, 28174, 4, (float) 0.99986, (float) 1.4E-4, -1, -1, map("1/1", 4, "2/2", 3), map(), map("PASS", 1), map("PASS", 1.0), -1, (float) -1.0, (float) 1.4191442E-4, (float) -1.0, "A", null);
        org.opencb.biodata.models.variant.avro.VariantStats v2_expected_AFR = new org.opencb.biodata.models.variant.avro.VariantStats("AFR", -1, 1,  7616,  7616, 0, (float) 1      , (float) 0     , -1, -1, map("1/1", 0, "2/2", 0), map(), map("PASS", 1), map("PASS", 1.0), -1, (float) -1.0, (float) 0           , (float) -1.0, "A", null);

        for (Variant variant : read) {
            calculator.calculate(variant);
        }

        assertEquals(v1_expected_ALL.toString(), read.get(0).getStudy(metadata.getId()).getStats("ALL").getImpl().toString());
        assertEquals(v1_expected_AFR.toString(), read.get(0).getStudy(metadata.getId()).getStats("AFR").getImpl().toString());
        assertEquals(v2_expected_ALL.toString(), read.get(1).getStudy(metadata.getId()).getStats("ALL").getImpl().toString());
        assertEquals(v2_expected_AFR.toString(), read.get(1).getStudy(metadata.getId()).getStats("AFR").getImpl().toString());

    }

    @Test
    public void testGnomadExomes_v2_1_1() {
        Properties properties = new Properties();
        properties.put("ALL.AC",   "AC");
        properties.put("ALL.AN",   "AN");
        properties.put("ALL.AF",   "AF");
        properties.put("ALL.HOMALT",  "nhomalt");
        properties.put("AFR.AC",   "AC_afr");
        properties.put("AFR.AN",   "AN_afr");
        properties.put("AFR.AF",   "AF_afr");
        properties.put("AFR.HOMALT",  "nhomalt_afr");

        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(properties);

        VariantNormalizer normalizer = new VariantNormalizer();
        VariantVcfHtsjdkReader vcfReader = new VariantVcfHtsjdkReader(
                this.getClass().getResourceAsStream("/datasets/gnomad_genomes/gnomad.exomes.v2.1.1.sites.small.vcf"), metadata);
        DataReader<Variant> reader = vcfReader.then(normalizer);

        reader.open();
        reader.pre();
        normalizer.configure(vcfReader.getVCFHeader());

        List<Variant> read = reader.read(3);
        reader.post();
        reader.close();

        assertEquals(1, read.size());

        org.opencb.biodata.models.variant.avro.VariantStats v1_expected_ALL = new org.opencb.biodata.models.variant.avro.VariantStats("ALL", -1, 1, 248284, 245006, 3278, (float) 0.9867974, (float) 0.0132026, -1, -1, map("1/1", 84), map(), map("PASS", 1), map("PASS", 1.0), -1, (float) 8499682, (float) 0.0132026225, (float) -1.0, "T", null);
        org.opencb.biodata.models.variant.avro.VariantStats v1_expected_AFR = new org.opencb.biodata.models.variant.avro.VariantStats("AFR", -1, 1,  15962,  15931,   31, (float) 0.9980579, (float) 0.00194211, -1, -1, map("1/1", 0), map(), map("PASS", 1), map("PASS", 1.0), -1, (float) 8499682, (float) 0.0019421125, (float) -1.0, "T", null);

        for (Variant variant : read) {
            calculator.calculate(variant);
        }

        assertEquals(v1_expected_ALL.toString(), read.get(0).getStudy(metadata.getId()).getStats("ALL").getImpl().toString());
        assertEquals(v1_expected_AFR.toString(), read.get(0).getStudy(metadata.getId()).getStats("AFR").getImpl().toString());

    }

    public static <T> Map<String, T> map(Object... objects) {
        HashMap<String, T> map = new HashMap<>();
        for (int i = 0; i < objects.length; i += 2) {
            map.put(objects[i].toString(), (T) objects[i + 1]);
        }
        return map;
    }

}

