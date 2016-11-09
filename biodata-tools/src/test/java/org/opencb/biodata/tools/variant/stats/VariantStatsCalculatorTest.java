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
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.*;

import static org.junit.Assert.*;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantStatsCalculatorTest {

    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");

    @Test
    public void testCalculateBiallelicStats() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        source.setSamples(sampleNames);
        String line = "1\t10040\trs123\tT\tC\t10.05\tHELLO\t.\tGT:GL\t"
                + "0/0:1,2,3\t0/1:1,2,3\t0/1:1,2,3\t"
                + "1/1:1,2,3\t./.:1,2,3\t1/1:1,2,3"; // 6 samples

        // Initialize expected variants
        List<Variant> result = readVariants(line);
        assertEquals(1, result.size());

        Variant variant = result.get(0);
        StudyEntry sourceEntry = variant.getSourceEntry(source.getFileId(), source.getStudyId());

        VariantStats biallelicStats = new VariantStats(result.get(0));
        VariantStatsCalculator.calculate(sourceEntry, sourceEntry.getAttributes(), null, biallelicStats);

        assertEquals("T", biallelicStats.getRefAllele());
        assertEquals("C", biallelicStats.getAltAllele());
        assertEquals(VariantType.SNV, biallelicStats.getVariantType());

        assertEquals(4, biallelicStats.getRefAlleleCount().longValue());
        assertEquals(6, biallelicStats.getAltAlleleCount().longValue());

//    private Map<Genotype, Integer> genotypesCount;

        assertEquals(2, biallelicStats.getMissingAlleles().longValue());
        assertEquals(1, biallelicStats.getMissingGenotypes().longValue());

        assertEquals(0.4, biallelicStats.getRefAlleleFreq(), 1e-6);
        assertEquals(0.6, biallelicStats.getAltAlleleFreq(), 1e-6);

//    private Map<Genotype, Float> genotypesFreq;
//    private float maf;
//    private float mgf;
//    private String mafAllele;
//    private String mgfGenotype;

        assertFalse(biallelicStats.hasPassedFilters());

        assertEquals(-1, biallelicStats.getMendelianErrors().longValue());
        assertEquals(-1, biallelicStats.getCasesPercentDominant(), 1e-6);
        assertEquals(-1, biallelicStats.getControlsPercentDominant(), 1e-6);
        assertEquals(-1, biallelicStats.getCasesPercentRecessive(), 1e-6);
        assertEquals(-1, biallelicStats.getCasesPercentRecessive(), 1e-6);

        assertTrue(biallelicStats.isTransition());
        assertFalse(biallelicStats.isTransversion());

        assertEquals(10.05, biallelicStats.getQuality(), 1e-6);
        assertEquals(6, biallelicStats.getNumSamples().longValue());

//    private VariantHardyWeinbergStats hw;
    }

    @Test
    public void testCalculateMultiallelicStats() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        source.setSamples(sampleNames);
        String line = "1\t10040\trs123\tT\tA,GC\t.\tPASS\t.\tGT:GL\t"
                + "0/0:1,2,3,4,5,6\t0/1:1,2,3,4,5,6\t0/2:1,2,3,4,5,6\t"
                + "1/1:1,2,3,4,5,6\t1/2:1,2,3,4,5,6\t1/2:1,2,3,4,5,6"; // 6 samples

        // Initialize expected variants
        List<Variant> result = readVariants(line);
        assertEquals(2, result.size());

        // Test first variant (alt allele C)
        Variant variant_C = result.get(0);
        StudyEntry sourceEntry_C = variant_C.getStudy(source.getStudyId());
        VariantStats multiallelicStats_C = new VariantStats(result.get(0));
        VariantStatsCalculator.calculate(sourceEntry_C, sourceEntry_C.getAttributes(), null, multiallelicStats_C);

        assertEquals("T", multiallelicStats_C.getRefAllele());
        assertEquals("A", multiallelicStats_C.getAltAllele());
        assertEquals(VariantType.SNV, multiallelicStats_C.getVariantType());

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

        assertTrue(multiallelicStats_C.hasPassedFilters());

        assertEquals(Integer.valueOf(-1), multiallelicStats_C.getMendelianErrors());
        assertEquals(-1, multiallelicStats_C.getCasesPercentDominant(), 1e-6);
        assertEquals(-1, multiallelicStats_C.getControlsPercentDominant(), 1e-6);
        assertEquals(-1, multiallelicStats_C.getCasesPercentRecessive(), 1e-6);
        assertEquals(-1, multiallelicStats_C.getCasesPercentRecessive(), 1e-6);

        assertFalse(multiallelicStats_C.isTransition());
        assertTrue(multiallelicStats_C.isTransversion());

        assertEquals(-1, multiallelicStats_C.getQuality(), 1e-6);
//        assertEquals(6, multiallelicStats_C.getNumSamples());


        // Test second variant (alt allele GC)
        Variant variant_GC = result.get(1);
        StudyEntry sourceEntry_GC = variant_GC.getSourceEntry(source.getFileId(), source.getStudyId());
        VariantStats multiallelicStats_GC = new VariantStats(result.get(1));
        VariantStatsCalculator.calculate(sourceEntry_GC, sourceEntry_GC.getAttributes(), null, multiallelicStats_GC);

        assertEquals("T", multiallelicStats_GC.getRefAllele());
        assertEquals("GC", multiallelicStats_GC.getAltAllele());

        assertEquals(multiallelicStats_GC.getRefAlleleCount().intValue(), 4);
        assertEquals(multiallelicStats_GC.getAltAlleleCount().intValue(), 3);

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
        return new VariantVcfFactory().create(source, line);
    }

}
