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
import org.opencb.biodata.formats.variant.VariantFactory;
import org.opencb.biodata.formats.variant.vcf4.VariantAggregatedVcfFactory;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.VariantNormalizer;
import org.opencb.commons.test.GenericTest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedEVSStatsCalculatorTest extends GenericTest {

    private final VariantNormalizer normalizer = new VariantNormalizer();
    private VariantFileMetadata fileMetadata = new VariantFileMetadata("EVS", "EVS");
    private VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("EVS");
    private VariantFactory factory = new VariantAggregatedVcfFactory();

    @Test
    public void testCreate_AA_AC_TT_GT() throws Exception { // AA,AC,TT,GT,...

        String line = "1\t69428\trs140739101\tT\tG\t.\tPASS\tMAF=4.5707,0.3663,3.0647;GTS=GG,GT,TT;GTC=93,141,5101";

        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator();
        calculator.calculate(v);

        StudyEntry avf = v.getStudy(metadata.getId());
        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("0/0", "T", "G"), 5101);
        genotypes.put(new Genotype("0/1", "T", "G"), 141);
        genotypes.put(new Genotype("1/1", "T", "G"), 93);

        assertEquals(genotypes, avf.getStats(StudyEntry.DEFAULT_COHORT).getGenotypeCount());

    }

    @Test
    public void testCreate_A_C_T_G() { // A,C,T,G

        String line = "Y\t25375759\trs373156833\tT\tA\t.\tPASS\tMAF=0.0,0.1751,0.0409;GTS=A,T;GTC=1,2442";
        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator();
        calculator.calculate(v);
        StudyEntry avf = v.getStudy(metadata.getId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("0", "T", "A"), 2442);
        genotypes.put(new Genotype("1", "T", "A"), 1);

        assertEquals(genotypes, avf.getStats(StudyEntry.DEFAULT_COHORT).getGenotypeCount());

    }

    @Test
    public void testCreate_R_RR_A1R_A1A1() { // R, RR, A1R, A1A1
        String line = "X\t100117423\t.\tAG\tA\t.\tPASS\tMAF=0.0308,0.0269,0.0294;GTS=A1A1,A1R,RR,R;GTC=1,1,3947,2306;";

        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator();
        calculator.calculate(v);

        assertEquals(v.getReference(), "G");
        assertEquals(v.getAlternate(), "");


        StudyEntry avf = v.getStudy(metadata.getId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("1/1", "AG", "A"), 1);
        genotypes.put(new Genotype("0/1", "AG", "A"), 1);
//        genotypes.put(new Genotype("0/0", "G", ""), 6253);
        genotypes.put(new Genotype("0/0", "AG", "A"), 3947);
        genotypes.put(new Genotype("0", "AG", "A"), 2306);

        assertEquals(avf.getStats(StudyEntry.DEFAULT_COHORT).getGenotypeCount(), genotypes);

    }

    @Test
    public void testCreate_R_RR_A1A1_A1R_A1() { // A1,A2,A3
        String line = "X\t106362078\trs3216052\tCT\tC\t.\tPASS\tMAF=18.1215,25.2889,38.7555;GTS=A1A1,A1R,A1,RR,R;GTC=960,1298,737,1691,1570";


        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator();
        calculator.calculate(v);

        assertEquals(v.getReference(), "T");
        assertEquals(v.getAlternate(), "");


        StudyEntry avf = v.getStudy(metadata.getId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

//        genotypes.put(new Genotype("1/1", "CT", "C"), 1697);
        genotypes.put(new Genotype("1/1", "CT", "C"), 960);
        genotypes.put(new Genotype("1", "CT", "C"), 737);
        genotypes.put(new Genotype("0/1", "CT", "C"), 1298);
//        genotypes.put(new Genotype("0/0", "CT", "C"), 3261);
        genotypes.put(new Genotype("0/0", "CT", "C"), 1691);
        genotypes.put(new Genotype("0", "CT", "C"), 1570);

        assertEquals(avf.getStats(StudyEntry.DEFAULT_COHORT).getGenotypeCount(), genotypes);
    }

    @Test
    public void testCreate_A1A1_A1A2_A2R_A2_RR_R() {// A1A2,A1A3...

        String line = "X\t14039552\t.\tCA\tCAA,C\t.\tPASS\tMAF=5.3453,4.2467,4.9459;GTS=A1A1,A1A2,A1R,A1,A2A2,A2R,A2,RR,R;GTC=0,0,134,162,4,92,107,3707,2027;";

        List<Variant> res = readVariants(line);

        assertTrue(res.size() == 2);

        Variant v = res.get(0);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator();
        calculator.calculate(res);

        assertEquals(v.getReference(), "");
        assertEquals(v.getAlternate(), "A");


        StudyEntry avf = v.getStudy(metadata.getId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

//        genotypes.put(new Genotype("1/1", "CA", Arrays.asList("CAA", "C")), 162);
        genotypes.put(new Genotype("1/1", "CA", Arrays.asList("CAA", "C")), 0);
        genotypes.put(new Genotype("1", "CA", Arrays.asList("CAA", "C")), 162);
        genotypes.put(new Genotype("1/2", "CA", Arrays.asList("CAA", "C")), 0);
        genotypes.put(new Genotype("0/1", "CA", Arrays.asList("CAA", "C")), 134);
//        genotypes.put(new Genotype("0/0", "CA", Arrays.asList("CAA", "C")), 5734);
        genotypes.put(new Genotype("0/0", "CA", Arrays.asList("CAA", "C")), 3707);
        genotypes.put(new Genotype("0", "CA", Arrays.asList("CAA", "C")), 2027);
//        genotypes.put(new Genotype("2/2", "CA", Arrays.asList("CAA", "C")), 111);
        genotypes.put(new Genotype("2/2", "CA", Arrays.asList("CAA", "C")), 4);
        genotypes.put(new Genotype("2", "CA", Arrays.asList("CAA", "C")), 107);
        genotypes.put(new Genotype("0/2", "CA", Arrays.asList("CAA", "C")), 92);

        assertEquals(genotypes, avf.getStats(StudyEntry.DEFAULT_COHORT).getGenotypeCount());


        v = res.get(1);

        assertEquals(v.getReference(), "A");
        assertEquals(v.getAlternate(), "");


        avf = v.getStudy(metadata.getId());

        genotypes = new HashMap<>();

//        genotypes.put(new Genotype("1/1", "CA", Arrays.asList("CAA", "C")), 111);
        genotypes.put(new Genotype("1/1", "CA", Arrays.asList("CAA", "C")), 4);
        genotypes.put(new Genotype("1", "CA", Arrays.asList("CAA", "C")), 107);
        genotypes.put(new Genotype("1/2", "CA", Arrays.asList("CAA", "C")), 0);
        genotypes.put(new Genotype("0/1", "CA", Arrays.asList("CAA", "C")), 92);
//        genotypes.put(new Genotype("0/0", "CA", Arrays.asList("CAA", "C")), 5734);
        genotypes.put(new Genotype("0/0", "CA", Arrays.asList("CAA", "C")), 3707);
        genotypes.put(new Genotype("0", "CA", Arrays.asList("CAA", "C")), 2027);
//        genotypes.put(new Genotype("2/2", "CA", Arrays.asList("CAA", "C")), 162);
        genotypes.put(new Genotype("2/2", "CA", Arrays.asList("CAA", "C")), 0);
        genotypes.put(new Genotype("2", "CA", Arrays.asList("CAA", "C")), 162);
        genotypes.put(new Genotype("0/2", "CA", Arrays.asList("CAA", "C")), 134);

        assertEquals(genotypes, avf.getStats(StudyEntry.DEFAULT_COHORT).getGenotypeCount());


    }

    /**
     * This tests the population values for MAF, AC and GTC, both for INDELs and SNVs
     */
    @Test
    public void testPopulation() {
        // ------------- INDEL
        String line = "21\t9908404\t.\tTG\tT\t.\tPASS\tDBSNP=.;EA_AC=1,3849;AA_AC=2,2120;TAC=3,5969;MAF=0.026,0.0943,0.0502;GTS=A1A1,A1R,RR;EA_GTC=0,1,1924;AA_GTC=0,2,1059;GTC=0,3,2983;DP=17;GL=.;CP=0.1;CG=0.1;AA=.;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=intergenic;HGVS_CDNA_VAR=.;HGVS_PROTEIN_VAR=.;CDS_SIZES=.;GS=.;PH=.;EA_AGE=.;AA_AGE=.";
        Properties properties = new Properties();
        properties.put("EA.AC", "EA_AC");
        properties.put("EA.GTC", "EA_GTC");
        properties.put("AA.AC", "AA_AC");
        properties.put("AA.GTC", "AA_GTC");
        properties.put("ALL.AC", "TAC");
        properties.put("ALL.GTC", "ALL_GTC");
        properties.put(VariantAggregatedEVSStatsCalculator.GROUPS_ORDER, "EA,AA,ALL");
        VariantFactory evsFactory = new VariantAggregatedVcfFactory();

        List<Variant> res = readVariants(line, evsFactory);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator(properties);
        calculator.calculate(res);

        // Allele count
        assertEquals(1, res.get(0).getStudy(metadata.getId()).getStats("EA").getAltAlleleCount().longValue());
        assertEquals(3849, res.get(0).getStudy(metadata.getId()).getStats("EA").getRefAlleleCount().longValue());
        assertEquals(3, res.get(0).getStudy(metadata.getId()).getStats("ALL").getAltAlleleCount().longValue());
        assertEquals(5969, res.get(0).getStudy(metadata.getId()).getStats("ALL").getRefAlleleCount().longValue());

        // MAF
        assertEquals(0.026 / 100, 0.000001, res.get(0).getStudy(metadata.getId()).getStats("EA").getMaf());
        assertEquals(0.0943 / 100, 0.000001, res.get(0).getStudy(metadata.getId()).getStats("AA").getMaf());
        assertEquals(0.0502 / 100, 0.000001, res.get(0).getStudy(metadata.getId()).getStats("ALL").getMaf());

        // GTC
        List<Genotype> genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "TG", "T"));
        genotypes.add(new Genotype("0/1", "TG", "T"));
        genotypes.add(new Genotype("0/0", "TG", "T"));
        List<Integer> counts = new ArrayList<>(Arrays.asList(0, 1, 1924));
        Map<Genotype, Integer> genotypesCount = res.get(0).getStudy(metadata.getId()).getStats("EA").getGenotypeCount();
        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(genotypesCount.get(genotypes.get(i)), counts.get(i));
        }


        // -------------- SNV, (GTS are expressed in another way)
        line = "21\t10862547\trs373689868\tG\tA\t.\tPASS\tDBSNP=dbSNP_138;EA_AC=0,3182;AA_AC=6,1378;TAC=6,4560;MAF=0.0,0.4335,0.1314;GTS=AA,AG,GG;EA_GTC=0,0,1591;AA_GTC=0,6,686;GTC=0,6,2277;DP=93;GL=.;CP=0.0;CG=-1.5;AA=G;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=intergenic;HGVS_CDNA_VAR=.;HGVS_PROTEIN_VAR=.;CDS_SIZES=.;GS=.;PH=.;EA_AGE=.;AA_AGE=.";

        res = readVariants(line, evsFactory);
        calculator.calculate(res);

        genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "G", "A"));
        genotypes.add(new Genotype("0/1", "G", "A"));
        genotypes.add(new Genotype("0/0", "G", "A"));
        counts = new ArrayList<>(Arrays.asList(0, 6, 686));
        genotypesCount = res.get(0).getStudy(metadata.getId()).getStats("AA").getGenotypeCount();
        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(genotypesCount.get(genotypes.get(i)), counts.get(i));
        }
    }

    /**
     * this tests the population stats AC and GTC for multiallelic variants, both for INDELs and SNVs
     */
    @Test
    public void testPopulationMultiallelic() {
        String line = "21\t47976819\t.\tCTT\tCTTT,C,CT\t.\tPASS\tDBSNP=.;EA_AC=393,35,531,6861;AA_AC=172,13,221,3174;TAC=565,48,752,10035;MAF=12.2634,11.3408,11.9737;GTS=A1A1,A1A2,A1A3,A1R,A2A2,A2A3,A2R,A3A3,A3R,RR;EA_GTC=1,2,3,4,5,6,7,8,9,10;AA_GTC=7,0,3,155,1,0,11,6,206,1401;GTC=10,0,8,537,4,0,40,15,714,4372;DP=8;GL=DIP2A;CP=0.0;CG=1.2;AA=.;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=NM_015151.3:intron,NM_015151.3:intron,NM_015151.3:intron,NM_001146116.1:intron,NM_001146116.1:intron,NM_001146116.1:intron;HGVS_CDNA_VAR=NM_015151.3:c.3499-32del1,NM_015151.3:c.3499-32_3499-31del2,NM_015151.3:c.3499-33_3499-32insT,NM_001146116.1:c.3487-32del1,NM_001146116.1:c.3487-32_3487-31del2,NM_001146116.1:c.3487-33_3487-32insT;HGVS_PROTEIN_VAR=.,.,.,.,.,.;CDS_SIZES=NM_015151.3:4716,NM_015151.3:4716,NM_015151.3:4716,NM_001146116.1:4704,NM_001146116.1:4704,NM_001146116.1:4704;GS=.,.,.,.,.,.;PH=.,.,.,.,.,.;EA_AGE=.;AA_AGE=.\n";
        Properties properties = new Properties();
        properties.put("EA.AC", "EA_AC");
        properties.put("EA.GTC", "EA_GTC");
        properties.put("AA.AC", "AA_AC");
        properties.put("AA.GTC", "AA_GTC");
        properties.put("ALL.AC", "TAC");
        properties.put("ALL.GTC", "ALL_GTC");
        properties.put(VariantAggregatedEVSStatsCalculator.GROUPS_ORDER, "EA,AA,ALL");
        VariantFactory evsFactory = new VariantAggregatedVcfFactory();

        List<Variant> res = readVariants(line, evsFactory);
        VariantAggregatedEVSStatsCalculator calculator = new VariantAggregatedEVSStatsCalculator(properties);
        calculator.calculate(res);

        // testing multiallelic AC 
        assertEquals(172, res.get(0).getStudy(metadata.getId()).getStats("AA").getAltAlleleCount().longValue());
        assertEquals(3174, res.get(0).getStudy(metadata.getId()).getStats("AA").getRefAlleleCount().longValue());
        assertEquals(13, res.get(1).getStudy(metadata.getId()).getStats("AA").getAltAlleleCount().longValue());
        assertEquals(3174, res.get(1).getStudy(metadata.getId()).getStats("AA").getRefAlleleCount().longValue());
        assertEquals(221, res.get(2).getStudy(metadata.getId()).getStats("AA").getAltAlleleCount().longValue());
        assertEquals(3174, res.get(2).getStudy(metadata.getId()).getStats("AA").getRefAlleleCount().longValue());

        assertEquals(565, res.get(0).getStudy(metadata.getId()).getStats("ALL").getAltAlleleCount().longValue());
        assertEquals(10035, res.get(0).getStudy(metadata.getId()).getStats("ALL").getRefAlleleCount().longValue());
        assertEquals(48, res.get(1).getStudy(metadata.getId()).getStats("ALL").getAltAlleleCount().longValue());
        assertEquals(10035, res.get(1).getStudy(metadata.getId()).getStats("ALL").getRefAlleleCount().longValue());
        assertEquals(752, res.get(2).getStudy(metadata.getId()).getStats("ALL").getAltAlleleCount().longValue());
        assertEquals(10035, res.get(2).getStudy(metadata.getId()).getStats("ALL").getRefAlleleCount().longValue());


        // testing multiallelic GTS=A1A1,A1A2,A1A3,A1R,A2A2,A2A3,A2R,A3A3,A3R,RR;EA_GTC=1,2,3,4,5,6,7,8,9,10
        // first allele variant
        List<Genotype> genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("1/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("1/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/1", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("2/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("2/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("3/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/0", "CTT", Arrays.asList("CTTT", "C", "CT")));

        List<Integer> counts = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Map<Genotype, Integer> genotypesCount = res.get(0).getStudy(metadata.getId()).getStats("EA").getGenotypeCount();

        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(counts.get(i), genotypesCount.get(genotypes.get(i)));
        }
//        genotypes.add(new Genotype("2/0", "CTT", "C"));
//        genotypes.add(new Genotype("0/0", "CTT", "CT"));

        // second allele variant
        genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("1/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("1/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/1", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("2/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("2/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("3/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/0", "CTT", Arrays.asList("CTTT", "C", "CT")));
        counts = new ArrayList<>(Arrays.asList(5, 2, 6, 7, 1, 3, 4, 8, 9, 10)); // taking A2 as if it were the first allele A1, and moving A1 to A2
        genotypesCount = res.get(1).getStudy(metadata.getId()).getStats("EA").getGenotypeCount();

        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(genotypesCount.get(genotypes.get(i)), counts.get(i));
        }

        // third allele variant
        genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("1/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("1/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/1", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("2/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("2/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/2", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("3/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/3", "CTT", Arrays.asList("CTTT", "C", "CT")));
        genotypes.add(new Genotype("0/0", "CTT", Arrays.asList("CTTT", "C", "CT")));
        counts = new ArrayList<>(Arrays.asList(8, 3, 6, 9, 1, 2, 4, 5, 7, 10));// taking A3 as if it were the first allele A1, and moving A1 to A2, and A2 to A3
        genotypesCount = res.get(2).getStudy(metadata.getId()).getStats("EA").getGenotypeCount();

        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(genotypesCount.get(genotypes.get(i)), counts.get(i));
        }


        // --------------------- testing multiallelic SNV
        line = "9\t17579190\trs4961573\tC\tG,A\t.\tPASS\tDBSNP=dbSNP_111;EA_AC=8156,0,0;AA_AC=4110,10,0;TAC=12266,10,0;" +
                "MAF=0.0,0.2427,0.0815;GTS=GG,GA,GC,AA,AC,CC;EA_GTC=1,2,3,4,5,6;AA_GTC=2050,10,0,0,0,0;" +
                "GTC=6128,10,0,0,0,0;DP=6;GL=SH3GL2;CP=0.0;CG=-1.8;AA=G;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;" +
                "FG=NM_003026.2:utr-5,NM_003026.2:utr-5;HGVS_CDNA_VAR=NM_003026.2:c.-51C>A,NM_003026.2:c.-51C>G;" +
                "HGVS_PROTEIN_VAR=.,.;CDS_SIZES=NM_003026.2:1059,NM_003026.2:1059;GS=.,.;PH=.,.;EA_AGE=.;AA_AGE=.";

        res = readVariants(line, evsFactory);
        calculator.calculate(res);

        // testing AC
        assertEquals(4110, res.get(0).getStudy(metadata.getId()).getStats("AA").getAltAlleleCount().longValue());
        assertEquals(0, res.get(0).getStudy(metadata.getId()).getStats("AA").getRefAlleleCount().longValue());
        assertEquals(10, res.get(1).getStudy(metadata.getId()).getStats("AA").getAltAlleleCount().longValue());
        assertEquals(0, res.get(1).getStudy(metadata.getId()).getStats("AA").getRefAlleleCount().longValue());

        genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "C", "G,A"));
        genotypes.add(new Genotype("1/2", "C", "G,A"));
        genotypes.add(new Genotype("0/1", "C", "G,A"));
        genotypes.add(new Genotype("2/2", "C", "G,A"));
        genotypes.add(new Genotype("0/2", "C", "G,A"));
        genotypes.add(new Genotype("0/0", "C", "G,A"));

        counts = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        genotypesCount = res.get(0).getStudy(metadata.getId()).getStats("EA").getGenotypeCount();

        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(genotypesCount.get(genotypes.get(i)), counts.get(i));
        }


        genotypes = new LinkedList<>();
        genotypes.add(new Genotype("1/1", "C", "G,A"));
        genotypes.add(new Genotype("1/2", "C", "G,A"));
        genotypes.add(new Genotype("0/1", "C", "G,A"));
        genotypes.add(new Genotype("2/2", "C", "G,A"));
        genotypes.add(new Genotype("0/2", "C", "G,A"));
        genotypes.add(new Genotype("0/0", "C", "G,A"));
        counts = new ArrayList<>(Arrays.asList(4, 2, 5, 1, 3, 6));
        genotypesCount = res.get(1).getStudy(metadata.getId()).getStats("EA").getGenotypeCount();

        for (int i = 0; i < genotypes.size(); i++) {
            assertEquals(genotypesCount.get(genotypes.get(i)), counts.get(i));
        }

    }

    private List<Variant> readVariants(String line) {
        return readVariants(line, factory);
    }

    private List<Variant> readVariants(String line, VariantFactory evsFactory) {
        return normalizer.apply(evsFactory.create(metadata, line));
    }

}
