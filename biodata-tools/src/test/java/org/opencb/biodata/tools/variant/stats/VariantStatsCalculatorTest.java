/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.variant.stats;

import org.junit.Test;
import org.opencb.biodata.formats.variant.vcf4.VariantVcfFactory;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.variant.VariantNormalizer;

import java.util.*;

import static org.junit.Assert.*;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantStatsCalculatorTest {

    private VariantFileMetadata fileMetadata = new VariantFileMetadata("filename.vcf", "fileId");
    private VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("studyId");

    @Test
    public void testCalculateBiallelicStats() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        fileMetadata.setSampleIds(sampleNames);
        String line = "1\t10040\trs123\tT\tC\t10.05\tHELLO\t.\tGT:GL\t"
                + "0/0:1,2,3\t0/1:1,2,3\t0/1:1,2,3\t"
                + "1/1:1,2,3\t./.:1,2,3\t1/1:1,2,3"; // 6 samples

        // Initialize expected variants
        List<Variant> result = readVariants(line);
        assertEquals(1, result.size());

        Variant variant = result.get(0);
        StudyEntry studyEntry = variant.getStudy(metadata.getId());

        VariantStats biallelicStats = VariantStatsCalculator.calculate(variant, studyEntry);

//        assertEquals("T", biallelicStats.getRefAllele());
//        assertEquals("C", biallelicStats.getAltAllele());
//        assertEquals(VariantType.SNV, biallelicStats.getVariantType());

        assertEquals(10, biallelicStats.getAlleleCount().intValue());
        assertEquals(4, biallelicStats.getRefAlleleCount().longValue());
        assertEquals(6, biallelicStats.getAltAlleleCount().longValue());

//    private Map<Genotype, Integer> genotypesCount;

        assertEquals(2, biallelicStats.getMissingAlleleCount().longValue());
        assertEquals(1, biallelicStats.getMissingGenotypeCount().longValue());

        assertEquals(0.4, biallelicStats.getRefAlleleFreq(), 1e-6);
        assertEquals(0.6, biallelicStats.getAltAlleleFreq(), 1e-6);

//    private Map<Genotype, Float> genotypesFreq;
//    private float maf;
//    private float mgf;
//    private String mafAllele;
//    private String mgfGenotype;
    }

    @Test
    public void testCalculateMultiallelicStats() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        fileMetadata.setSampleIds(sampleNames);
        String line = "1\t10040\trs123\tT\tA,GC\t.\tPASS\t.\tGT:GL\t"
                + "0/0:1,2,3,4,5,6\t0/1:1,2,3,4,5,6\t0/2:1,2,3,4,5,6\t"
                + "1/1:1,2,3,4,5,6\t1/2:1,2,3,4,5,6\t1/2:1,2,3,4,5,6"; // 6 samples

        // Initialize expected variants
        List<Variant> result = readVariants(line);
        assertEquals(2, result.size());

        // Test first variant (alt allele C)
        Variant variant_C = result.get(0);
        StudyEntry sourceEntry_C = variant_C.getStudy(metadata.getId());
        VariantStats multiallelicStats_C = VariantStatsCalculator.calculate(variant_C, sourceEntry_C);

//        assertEquals("T", multiallelicStats_C.getRefAllele());
//        assertEquals("A", multiallelicStats_C.getAltAllele());
//        assertEquals(VariantType.SNV, multiallelicStats_C.getVariantType());

        assertEquals(12, multiallelicStats_C.getAlleleCount().intValue());
        assertEquals(multiallelicStats_C.getRefAlleleCount().intValue(), 4);
        assertEquals(multiallelicStats_C.getAltAlleleCount().intValue(), 5);
//
////    private Map<Genotype, Integer> genotypesCount;
//
//        assertEquals(0, multiallelicStats_C.getMissingAlleles());
//        assertEquals(0, multiallelicStats_C.getMissingGenotypes());
//
//        assertEquals(0.375, multiallelicStats_C.getRefAlleleFreq(), 1e-6);
//        assertEquals(0.5, multiallelicStats_C.getAltAlleleFreq(), 1e-6);

//    private Map<Genotype, Float> genotypesFreq;
//    private float maf;
//    private float mgf;
//    private String mafAllele;
//    private String mgfGenotype;


        // Test second variant (alt allele GC)
        Variant variant_GC = result.get(1);
        StudyEntry sourceEntry_GC = variant_GC.getStudy(metadata.getId());
        VariantStats multiallelicStats_GC = VariantStatsCalculator.calculate(variant_GC, sourceEntry_GC);

//        assertEquals("T", multiallelicStats_GC.getRefAllele());
//        assertEquals("GC", multiallelicStats_GC.getAltAllele());

        assertEquals(12, multiallelicStats_GC.getAlleleCount().intValue());
        assertEquals(multiallelicStats_GC.getRefAlleleCount().intValue(), 4);
        assertEquals(multiallelicStats_GC.getAltAlleleCount().intValue(), 3);

    }

    @Test
    public void testCalculateFromCounts() {
        VariantStats stats = VariantStatsCalculator.calculate(
                new Variant("1:100:A:C"), new GtMap()
                        .append("0/0", 10)
                        .append("0/1", 20)
                        .append("1/2", 20), true);

        assertEquals(100, stats.getAlleleCount().intValue());
        assertEquals(40, stats.getRefAlleleCount().intValue());
        assertEquals(40, stats.getAltAlleleCount().intValue());

        assertEquals(new HashSet<>(Arrays.asList(
                new Genotype("0/0"),
                new Genotype("0/1"),
                new Genotype("1/1"),
                new Genotype("1/2"))),
                stats.getGenotypeCount().keySet());

        stats = VariantStatsCalculator.calculate(
                new Variant("1:100:A:C"), new GtMap()
                        .append("0/0", 10)
                        .append("0/1", 20)
                        .append("1/2", 20), false);

        assertEquals(100, stats.getAlleleCount().intValue());
        assertEquals(40, stats.getRefAlleleCount().intValue());
        assertEquals(40, stats.getAltAlleleCount().intValue());

        assertEquals(new HashSet<>(Arrays.asList(
                new Genotype("0/0"),
                new Genotype("0/1"),
                new Genotype("1/1"))), stats.getGenotypeCount().keySet());

    }

    @Test
    public void testCalculatePhasedGts() {
        VariantStats stats = VariantStatsCalculator.calculate(
                new Variant("1:100:A:C"), new GtMap()
                        .append("0/0", 5)
                        .append("0|0", 5)
                        .append("0/1", 10)
                        .append("0|1", 5)
                        .append("1|0", 5)
                        .append("1/2", 20), true);

        assertEquals(100, stats.getAlleleCount().intValue());
        assertEquals(40, stats.getRefAlleleCount().intValue());
        assertEquals(40, stats.getAltAlleleCount().intValue());

        assertEquals(stats.getGenotypeCount().keySet(), stats.getGenotypeFreq().keySet());
        assertEquals(new HashSet<>(Arrays.asList(new Genotype("0/0"), new Genotype("0/1"), new Genotype("1/1"), new Genotype("1/2"))),
                stats.getGenotypeCount().keySet());
        assertEquals(0.0, stats.getMgf().doubleValue(), 0.00001);
        assertEquals("1/1", stats.getMgfGenotype());



        stats = VariantStatsCalculator.calculate(
                new Variant("1:100:A:C"), new GtMap()
                        .append("0/0", 5)
                        .append("0|0", 5)
                        .append("0/1", 10)
                        .append("0|1", 5)
                        .append(".|1", 5)
                        .append("./1", 5)
                        .append("./.", 200)
                        .append("1|0", 5)
                        .append("1|1", 2)
                        .append("1/1", 3)
                        .append("1/2", 20), true);

        assertEquals(120, stats.getAlleleCount().intValue());
        assertEquals(40, stats.getRefAlleleCount().intValue());
        assertEquals(60, stats.getAltAlleleCount().intValue());

        assertEquals(new GtMap()
                        .append("0/0", 10)
                        .append("0/1", 20)
                        .append("1/1", 5)
                        .append("./.", 200)
                        .append("./1", 10)
                        .append("1/2", 20),
                stats.getGenotypeCount());
        assertEquals(new HashSet<>(Arrays.asList(
                new Genotype("0/0"),
                new Genotype("0/1"),
                new Genotype("1/1"),
                new Genotype("1/2"))),
                stats.getGenotypeFreq().keySet());
        assertEquals(5.0 / 55.0, stats.getMgf().doubleValue(), 0.00001);
        assertEquals("1/1", stats.getMgfGenotype());
    }

    public static class GtMap extends HashMap<Genotype, Integer> {
        public GtMap append(String gt, Integer count) {
            super.put(new Genotype(gt), count);
            return this;
        }
    }


    @Test
    public void testCreate_1000g_38_liftover() throws Exception {

        String line = "1\t69428\trs140739101\tT\tC\t.\tPASS\t" +
                "dbSNP_144;" +
                "TSA=insertion;" +
                "E_Multiple_observations;" +
                "E_1000G;" +
                "MA=C;" +
                "MAF=0.425319;" +
                "MAC=2130;" +
                "EAS_AF=0.3363;" +
                "AMR_AF=0.3602;" +
                "AFR_AF=0.4909;" +
                "EUR_AF=0.4056;" +
                "SAS_AF=0.4949";

        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 1);


        Variant v = res.get(0);
        Properties tagMap = get1000gLiftOverTagMap();
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(tagMap);
        calculator.calculate(v);
        StudyEntry s = v.getStudies().get(0);

        Map<String, Double> freqs = new HashMap<>();
        freqs.put("ALL", 0.4253);
        freqs.put("EAS", 0.3363);
        freqs.put("AMR", 0.3602);
        freqs.put("AFR", 0.4909);
        freqs.put("EUR", 0.4056);
        freqs.put("SAS", 0.4949);

        freqs.forEach((coh, freq) -> assertEquals(freq, s.getStats().get(coh).getAltAlleleFreq(), 0.0001));
//        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(s.getStats()));
    }


    @Test
    public void testCreate_1000g_38_liftover_wrong() throws Exception {

        String line = "1\t69521\trs553724620\tT\tA,C\t.\t.\t" +
                "dbSNP_144;" +
                "TSA=SNV;" +
                "E_Multiple_observations;" +
                "E_1000G;" +
                "MA=A;" +
                "MAF=0.000399361;" +
                "MAC=2;" +
                "EAS_AF=0;" +
                "AMR_AF=0.0029;" +
                "AFR_AF=0;" +
                "EUR_AF=0;" +
                "SAS_AF=0" +
                "\n";

        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 2);

        Variant v = res.get(0);
        Properties tagMap = get1000gLiftOverTagMap();
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(tagMap);
        calculator.calculate(v);
        StudyEntry s = v.getStudies().get(0);

        Map<String, Double> freqs = new HashMap<>();
        freqs.put("ALL", -1.0);
        freqs.put("EAS", -1.0);
        freqs.put("AMR", -1.0);
        freqs.put("AFR", -1.0);
        freqs.put("EUR", -1.0);
        freqs.put("SAS", -1.0);

        freqs.forEach((coh, freq) -> assertEquals(freq, s.getStats().get(coh).getAltAlleleFreq(), 0.0001));

//        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(s.getStats()));
    }

    @Test
    public void testCreate_1000g_38_liftover_indel() throws Exception {

        String line = "1\t69521\trs553724620\tTA\tA\t.\t.\t" +
                "dbSNP_144;" +
                "TSA=SNV;" +
                "E_Multiple_observations;" +
                "E_1000G;" +
                "MA=-;" +
                "MAF=0.000399361;" +
                "MAC=2;" +
                "EAS_AF=0;" +
                "AMR_AF=0.0029;" +
                "AFR_AF=0;" +
                "EUR_AF=0;" +
                "SAS_AF=0" +
                "\n";

        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        Properties tagMap = get1000gLiftOverTagMap();
        VariantAggregatedStatsCalculator calculator = new VariantAggregatedStatsCalculator(tagMap);
        calculator.calculate(v);
        StudyEntry s = v.getStudies().get(0);

        Map<String, Double> freqs = new HashMap<>();
        freqs.put("ALL", 0.000399361);
        freqs.put("EAS", 0.0);
        freqs.put("AMR", 0.0029);
        freqs.put("AFR", 0.0);
        freqs.put("EUR", 0.0);
        freqs.put("SAS", 0.0);

        freqs.forEach((coh, freq) -> assertEquals(freq, s.getStats().get(coh).getAltAlleleFreq(), 0.0001));

//        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(s.getStats()));
    }

    private Properties get1000gLiftOverTagMap() {
        Properties tagMap = new Properties();
        tagMap.put("ALL.MAF", "MAF");
        tagMap.put("ALL.MA", "MA");
        tagMap.put("EAS.AF", "EAS_AF");
        tagMap.put("AMR.AF", "AMR_AF");
        tagMap.put("AFR.AF", "AFR_AF");
        tagMap.put("EUR.AF", "EUR_AF");
        tagMap.put("SAS.AF", "SAS_AF");
        return tagMap;
    }

    private List<Variant> readVariants(String line) {
        return new VariantNormalizer().apply(new VariantVcfFactory().create(metadata, line));
    }

}
