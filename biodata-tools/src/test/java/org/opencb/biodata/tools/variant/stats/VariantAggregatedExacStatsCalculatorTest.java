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
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.*;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.test.GenericTest;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by jmmut on 2015-03-25.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedExacStatsCalculatorTest extends GenericTest {

    private VariantSource source = new VariantSource("Exac", "Exac", "Exac", "Exac");
    private VariantFactory factory = new VariantAggregatedVcfFactory();

    @Test
    public void basicLine() {
        String line = "1\t13525\t.\tG\tT\t828.34\tVQSRTrancheSNP99.60to99.80\tAC=27;AC_AFR=0;AC_AMR=0;AC_Adj=22;AC_EAS=0;"
                + "AC_FIN=0;AC_Het=22;AC_Hom=0;AC_NFE=1;AC_OTH=0;AC_SAS=21;AF=6.558e-04;AN=41168;AN_AFR=422;AN_AMR=120;AN_Adj=10890;"
                + "AN_EAS=160;AN_FIN=8;AN_NFE=2772;AN_OTH=116;AN_SAS=7292;BaseQRankSum=-2.157e+00;ClippingRankSum=0.365;DP=155897;"
                + "FS=9.009;GQ_MEAN=15.16;GQ_STDDEV=18.34;Het_AFR=0;Het_AMR=0;Het_EAS=0;Het_FIN=0;Het_NFE=1;Het_OTH=0;Het_SAS=21;"
                + "Hom_AFR=0;Hom_AMR=0;Hom_EAS=0;Hom_FIN=0;Hom_NFE=0;Hom_OTH=0;Hom_SAS=0;InbreedingCoeff=-0.0798;MQ=31.32;MQ0=0;"
                + "MQRankSum=-7.020e-01;NCC=62633;QD=0.84;ReadPosRankSum=-2.070e-01;VQSLOD=-3.495e+00;culprit=MQ;DP_HIST=13297|"
                + "1771|705|1240|2938|369|143|60|23|17|12|5|2|2|0|0|0|0|0|0,0|0|1|2|5|1|8|1|3|2|2|2|0|0|0|0|0|0|0|0;GQ_HIST=325|"
                + "14020|111|71|2653|324|189|33|11|13|3|9|2450|315|24|26|0|2|0|5,0|2|1|2|2|1|1|0|1|3|0|0|2|1|2|2|0|2|0|5;CSQ=T|"
                + "ENSG00000223972|ENST00000456328|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|"
                + "773||||||1||1|DDX11L1|HGNC|37102|processed_transcript|YES||||||||3/3|||ENST00000456328.2:n.773G>T|||||||||||||||||"
                + "||,T|ENSG00000223972|ENST00000450305|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|"
                + "487||||||1||1|DDX11L1|HGNC|37102|transcribed_unprocessed_pseudogene|||||||||6/6|||ENST00000450305.2:n.487G>T||||||"
                + "|||||||||||||,T|ENSG00000223972|ENST00000515242|Transcript|non_coding_transcript_exon_variant&non_coding_transcript"
                + "_variant|766||||||1||1|DDX11L1|HGNC|37102|transcribed_unprocessed_pseudogene|||||||||3/3|||ENST00000515242.2:n."
                + "766G>T|||||||||||||||||||,T|ENSG00000223972|ENST00000518655|Transcript|non_coding_transcript_exon_variant&"
                + "non_coding_transcript_variant|604||||||1||1|DDX11L1|HGNC|37102|transcribed_unprocessed_pseudogene|||||||||3/4|||"
                + "ENST00000518655.2:n.604G>T|||||||||||||||||||,T||ENSR00000528767|RegulatoryFeature|regulatory_region_variant|||||||"
                + "1||||||regulatory_region|||||||||||||||||||||||||||||||";
        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        VariantAggregatedExacStatsCalculator calculator = new VariantAggregatedExacStatsCalculator();
        calculator.calculate(v);
        VariantSourceEntry sourceEntry = v.getSourceEntry(source.getFileId(), source.getStudyId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("0/0", "G", "T"), (10890 - 22 * 2) / 2);    // AN - alleles_in_gt_0/1: how many ref alleles there are in the genotype 0/0, as there are no 1/1
        genotypes.put(new Genotype("0/1", "G", "T"), 22);
        genotypes.put(new Genotype("1/1", "G", "T"), 0);

        VariantStats stats = sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT);
        assertEquals(genotypes, stats.getGenotypesCount());
        assertEquals(22, stats.getAltAlleleCount());
        assertEquals(10890 - 22, stats.getRefAlleleCount());
        assertEquals(22.0 / 10890, stats.getMaf(), 0.00001);
    }

    @Test
    public void multiallelicLine() {
        String line = "1\t69552\trs55874132\tG\tT,A,C\t6289.25\tVQSRTrancheSNP99.60to99.80\tAC=3,3,5;AC_AFR=0,0,0;AC_AMR=3,0,0;"
                + "AC_Adj=3,3,0;AC_EAS=0,0,0;AC_FIN=0,0,0;AC_Het=1,1,0,0,0,0;AC_Hom=1,1,0;AC_NFE=0,0,0;AC_OTH=0,0,0;AC_SAS=0,3,0;"
                + "AF=3.308e-05,3.308e-05,5.514e-05;AN=90684;AN_AFR=7828;AN_AMR=6546;AN_Adj=79012;AN_EAS=8394;AN_FIN=3354;"
                + "AN_NFE=39846;AN_OTH=606;AN_SAS=12438;BaseQRankSum=0.736;ClippingRankSum=0.198;DB;DP=1383162;FS=1.848;"
                + "GQ_MEAN=57.16;GQ_STDDEV=20.29;Het_AFR=0,0,0,0,0,0;Het_AMR=1,0,0,0,0,0;Het_EAS=0,0,0,0,0,0;Het_FIN=0,0,0,0,0,0;"
                + "Het_NFE=0,0,0,0,0,0;Het_OTH=0,0,0,0,0,0;Het_SAS=0,1,0,0,0,0;Hom_AFR=0,0,0;Hom_AMR=1,0,0;Hom_EAS=0,0,0;"
                + "Hom_FIN=0,0,0;Hom_NFE=0,0,0;Hom_OTH=0,0,0;Hom_SAS=0,1,0;InbreedingCoeff=0.0345;MQ=30.21;MQ0=0;MQRankSum=-1.231e+00;"
                + "NCC=25596;QD=9.65;ReadPosRankSum=0.920;VQSLOD=-2.686e+00;culprit=MQ;DP_HIST=4764|1048|70|7|7472|10605|4702|4511|"
                + "4937|3377|1835|886|500|250|128|63|35|22|13|117,0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|1|0|0|1,0|0|0|0|0|0|0|0|0|0|0|1|0"
                + "|0|0|0|0|0|0|1,3|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0;GQ_HIST=533|4275|91|84|874|13|15|4|2|0|1|0|28889|7105|1334|"
                + "1168|438|98|75|343,0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|2,0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|2,1|2|0|0|0|0|0|0|"
                + "0|0|0|0|0|0|0|0|0|0|0|0;CSQ=A|ENSG00000186092|ENST00000335137|Transcript|synonymous_variant|462|462|154|A|gcG/gcA|"
                + "rs55874132|2||1|OR4F5|HGNC|14825|protein_coding|YES|CCDS30547.1|ENSP00000334393|OR4F5_HUMAN||UPI0000041BC1|||1/1||"
                + "Pfam_domain:PF00001&Pfam_domain:PF10320&PROSITE_profiles:PS50262&Superfamily_domains:SSF81321|ENST00000335137.3:c."
                + "462G>A|ENST00000335137.3:c.462G>A(p.%3D)||||||||||||||||||,T|ENSG00000186092|ENST00000335137|Transcript|synonymous"
                + "_variant|462|462|154|A|gcG/gcT|rs55874132|1||1|OR4F5|HGNC|14825|protein_coding|YES|CCDS30547.1|ENSP00000334393|"
                + "OR4F5_HUMAN||UPI0000041BC1|||1/1||Pfam_domain:PF00001&Pfam_domain:PF10320&PROSITE_profiles:PS50262&Superfamily_"
                + "domains:SSF81321|ENST00000335137.3:c.462G>T|ENST00000335137.3:c.462G>T(p.%3D)||||||||||||||||||,C|ENSG00000186092|"
                + "ENST00000335137|Transcript|synonymous_variant|462|462|154|A|gcG/gcC|rs55874132|3||1|OR4F5|HGNC|14825|protein_coding|"
                + "YES|CCDS30547.1|ENSP00000334393|OR4F5_HUMAN||UPI0000041BC1|||1/1||Pfam_domain:PF00001&Pfam_domain:PF10320&PROSITE_"
                + "profiles:PS50262&Superfamily_domains:SSF81321|ENST00000335137.3:c.462G>C|ENST00000335137.3:c.462G>C(p.%3D)||||||||"
                + "||||||||||,A||ENSR00000278218|RegulatoryFeature|regulatory_region_variant||||||rs55874132|2||||||regulatory_region|"
                + "||||||||||||||||||||||||||||||,T||ENSR00000278218|RegulatoryFeature|regulatory_region_variant||||||rs55874132|1||||"
                + "||regulatory_region|||||||||||||||||||||||||||||||,C||ENSR00000278218|RegulatoryFeature|regulatory_region_variant||"
                + "||||rs55874132|3||||||regulatory_region|||||||||||||||||||||||||||||||";

        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 3);

        Variant v = res.get(0);
        VariantAggregatedExacStatsCalculator calculator = new VariantAggregatedExacStatsCalculator();
        calculator.calculate(res);
        VariantSourceEntry sourceEntry = v.getSourceEntry(source.getFileId(), source.getStudyId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("0/0", "G", "T"), (79012 - 4 * 2) / 2);    // AN - alleles_in_gt_0/1: how many ref alleles there are in the genotype 0/0, as there are no 1/1
        genotypes.put(new Genotype("0/1", "G", "T"), 1);
        genotypes.put(new Genotype("1/1", "G", "T"), 1);
        genotypes.put(new Genotype("0/2", "G", "T"), 1);
        genotypes.put(new Genotype("1/2", "G", "T"), 0);
        genotypes.put(new Genotype("2/2", "G", "T"), 1);
        genotypes.put(new Genotype("0/3", "G", "T"), 0);
        genotypes.put(new Genotype("1/3", "G", "T"), 0);
        genotypes.put(new Genotype("2/3", "G", "T"), 0);
        genotypes.put(new Genotype("3/3", "G", "T"), 0);

        assertEquals(genotypes, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getGenotypesCount());
        assertEquals(3, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getAltAlleleCount());
        assertEquals(79012 - 1 - 2 - 1 - 2, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getRefAlleleCount());
        assertEquals(0, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getMaf(), 0.00001);   // how can a multiallelic variant have an allele count of 0? the "Adjusted" just removed it

        genotypes.clear();
        genotypes.put(new Genotype("0/0", "G", "A"), (79012 - 4 * 2) / 2);    // AN - alleles_in_gt_0/1: how many ref alleles there are in the genotype 0/0, as there are no 1/1
        genotypes.put(new Genotype("0/1", "G", "A"), 1);
        genotypes.put(new Genotype("1/1", "G", "A"), 1);
        genotypes.put(new Genotype("0/2", "G", "A"), 1);
        genotypes.put(new Genotype("1/2", "G", "A"), 0);
        genotypes.put(new Genotype("2/2", "G", "A"), 1);
        genotypes.put(new Genotype("0/3", "G", "A"), 0);
        genotypes.put(new Genotype("1/3", "G", "A"), 0);
        genotypes.put(new Genotype("2/3", "G", "A"), 0);
        genotypes.put(new Genotype("3/3", "G", "A"), 0);

        sourceEntry = res.get(1).getSourceEntry(source.getFileId(), source.getStudyId());

        assertEquals(genotypes, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getGenotypesCount());
        assertEquals(3, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getAltAlleleCount());
        assertEquals(79012 - 1 - 2 - 1 - 2, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getRefAlleleCount());

        genotypes.clear();
        genotypes.put(new Genotype("0/0", "G", "C"), (79012 - 4 * 2) / 2);    // AN - alleles_in_gt_0/1: how many ref alleles there are in the genotype 0/0, as there are no 1/1
        genotypes.put(new Genotype("0/1", "G", "C"), 0);
        genotypes.put(new Genotype("1/1", "G", "C"), 0);
        genotypes.put(new Genotype("0/2", "G", "C"), 1);
        genotypes.put(new Genotype("1/2", "G", "C"), 0);
        genotypes.put(new Genotype("2/2", "G", "C"), 1);
        genotypes.put(new Genotype("0/3", "G", "C"), 1);
        genotypes.put(new Genotype("1/3", "G", "C"), 0);
        genotypes.put(new Genotype("2/3", "G", "C"), 0);
        genotypes.put(new Genotype("3/3", "G", "C"), 1);

        sourceEntry = res.get(2).getSourceEntry(source.getFileId(), source.getStudyId());

        assertEquals(genotypes, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getGenotypesCount());
        assertEquals(0, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getAltAlleleCount());
        assertEquals(79012 - 1 - 2 - 1 - 2, sourceEntry.getStats(VariantSourceEntry.DEFAULT_COHORT).getRefAlleleCount());
    }

    @Test
    public void multiallelicPopulationGenotypes() {
        String line = "1\t13528\t.\tC\tG,T\t1771.54\tVQSRTrancheSNP99.60to99.80\tAC=21,11;AC_AFR=12,0;AC_AMR=1,0;AC_Adj=13,9;"
                + "AC_EAS=0,0;AC_FIN=0,0;AC_Het=13,9,0;AC_Hom=0,0;AC_NFE=0,2;AC_OTH=0,0;AC_SAS=0,7;AF=6.036e-04,3.162e-04;"
                + "AN=34792;AN_AFR=390;AN_AMR=116;AN_Adj=10426;AN_EAS=150;AN_FIN=8;AN_NFE=2614;AN_OTH=116;AN_SAS=7032;"
                + "BaseQRankSum=1.23;ClippingRankSum=0.056;DP=144988;FS=0.000;GQ_MEAN=14.54;GQ_STDDEV=16.53;Het_AFR=12,0,0;"
                + "Het_AMR=1,0,0;Het_EAS=0,0,0;Het_FIN=0,0,0;Het_NFE=0,2,0;Het_OTH=0,0,0;Het_SAS=0,7,0;Hom_AFR=0,0;Hom_AMR=0,0;"
                + "Hom_EAS=0,0;Hom_FIN=0,0;Hom_NFE=0,0;Hom_OTH=0,0;Hom_SAS=0,0;InbreedingCoeff=0.0557;MQ=31.08;MQ0=0;"
                + "MQRankSum=-5.410e-01;NCC=67387;QD=1.91;ReadPosRankSum=0.206;VQSLOD=-2.705e+00;culprit=MQ;DP_HIST=10573|1503|"
                + "705|1265|2477|613|167|52|18|11|8|3|0|0|1|0|0|0|0|0,2|6|2|1|4|0|3|1|0|0|2|0|0|0|0|0|0|0|0|0,1|0|0|0|1|1|3|0|"
                + "1|1|1|0|0|0|1|0|0|0|0|0;GQ_HIST=342|11195|83|56|3154|517|367|60|12|4|5|7|1373|180|15|16|1|0|1|8,0|0|1|0|1|0|"
                + "3|1|0|1|2|0|1|2|0|1|1|0|1|6,0|1|0|0|1|1|0|0|1|0|0|1|1|1|1|0|0|0|0|2;CSQ=T|ENSG00000223972|ENST00000456328|"
                + "Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|776||||||2||1|DDX11L1|HGNC|"
                + "37102|processed_transcript|YES||||||||3/3|||ENST00000456328.2:n.776C>T|||||||||||||||||||,G|ENSG00000223972|"
                + "ENST00000456328|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|776||||||1||1|"
                + "DDX11L1|HGNC|37102|processed_transcript|YES||||||||3/3|||ENST00000456328.2:n.776C>G|||||||||||||||||||,T|"
                + "ENSG00000223972|ENST00000450305|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|"
                + "490||||||2||1|DDX11L1|HGNC|37102|transcribed_unprocessed_pseudogene|||||||||6/6|||ENST00000450305.2:n.490C>T|"
                + "||||||||||||||||||,G|ENSG00000223972|ENST00000450305|Transcript|non_coding_transcript_exon_variant&non_coding"
                + "_transcript_variant|490||||||1||1|DDX11L1|HGNC|37102|transcribed_unprocessed_pseudogene|||||||||6/6|||"
                + "ENST00000450305.2:n.490C>G|||||||||||||||||||,T|ENSG00000223972|ENST00000515242|Transcript|non_coding_"
                + "transcript_exon_variant&non_coding_transcript_variant|769||||||2||1|DDX11L1|HGNC|37102|transcribed_unprocessed"
                + "_pseudogene|||||||||3/3|||ENST00000515242.2:n.769C>T|||||||||||||||||||,G|ENSG00000223972|ENST00000515242|"
                + "Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|769||||||1||1|DDX11L1|HGNC|37102|"
                + "transcribed_unprocessed_pseudogene|||||||||3/3|||ENST00000515242.2:n.769C>G|||||||||||||||||||,T|ENSG00000223972|"
                + "ENST00000518655|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|607||||||2||1|DDX11L1|"
                + "HGNC|37102|transcribed_unprocessed_pseudogene|||||||||3/4|||ENST00000518655.2:n.607C>T|||||||||||||||||||,G|"
                + "ENSG00000223972|ENST00000518655|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|"
                + "607||||||1||1|DDX11L1|HGNC|37102|transcribed_unprocessed_pseudogene|||||||||3/4|||ENST00000518655.2:n.607C>G|"
                + "||||||||||||||||||,T||ENSR00000528767|RegulatoryFeature|regulatory_region_variant|||||||2||||||regulatory_region|"
                + "||||||||||||||||||||||||||||||,G||ENSR00000528767|RegulatoryFeature|regulatory_region_variant|||||||1||||||"
                + "regulatory_region|||||||||||||||||||||||||||||||";

        Properties properties = new Properties();
        properties.put("AFR.AC",   "AC_AFR");
        properties.put("AFR.AN",   "AN_AFR");
        properties.put("AFR.HET", "Het_AFR");
        properties.put("AFR.HOM", "Hom_AFR");
        properties.put("AMR.AC",   "AC_AMR");
        properties.put("AMR.AN",   "AN_AMR");
        properties.put("AMR.HET", "Het_AMR");
        properties.put("AMR.HOM", "Hom_AMR");
        properties.put("EAS.AC",   "AC_EAS");
        properties.put("EAS.AN",   "AN_EAS");
        properties.put("EAS.HET", "Het_EAS");
        properties.put("EAS.HOM", "Hom_EAS");
        properties.put("FIN.AC",   "AC_FIN");
        properties.put("FIN.AN",   "AN_FIN");
        properties.put("FIN.HET", "Het_FIN");
        properties.put("FIN.HOM", "Hom_FIN");
        properties.put("NFE.AC",   "AC_NFE");
        properties.put("NFE.AN",   "AN_NFE");
        properties.put("NFE.HET", "Het_NFE");
        properties.put("NFE.HOM", "Hom_NFE");
        properties.put("OTH.AC",   "AC_OTH");
        properties.put("OTH.AN",   "AN_OTH");
        properties.put("OTH.HET", "Het_OTH");
        properties.put("OTH.HOM", "Hom_OTH");
        properties.put("SAS.AC",   "AC_SAS");
        properties.put("SAS.AN",   "AN_SAS");
        properties.put("SAS.HET", "Het_SAS");
        properties.put("SAS.HOM", "Hom_SAS");
        properties.put("ALL.AC",  "AC_Adj");
        properties.put("ALL.AN",  "AN_Adj");
        properties.put("ALL.HET", "AC_Het");
        properties.put("ALL.HOM", "AC_Hom");
        VariantFactory exacFactory = new VariantAggregatedVcfFactory();
        List<Variant> res = exacFactory.create(source, line);

        assertTrue(res.size() == 2);

        Variant v = res.get(0);
        VariantAggregatedExacStatsCalculator calculator = new VariantAggregatedExacStatsCalculator(properties);
        calculator.calculate(res);
        VariantSourceEntry sourceEntry = v.getSourceEntry(source.getFileId(), source.getStudyId());

        // Allele and genotype counts
        assertEquals(12, sourceEntry.getCohortStats("AFR").getAltAlleleCount());
        Genotype genotype = new Genotype("0/1", v.getReference(), v.getAlternate());
        assertEquals(12, (int) sourceEntry.getCohortStats("AFR").getGenotypesCount().get(genotype));
        genotype = new Genotype("0/2", v.getReference(), v.getAlternate());
        assertEquals(7, (int) sourceEntry.getCohortStats("SAS").getGenotypesCount().get(genotype));
        genotype = new Genotype("1/1", v.getReference(), v.getAlternate());
        assertEquals(0, (int) sourceEntry.getCohortStats("SAS").getGenotypesCount().get(genotype));
        genotype = new Genotype("0/1", v.getReference(), v.getAlternate());
        assertEquals(0, (int) sourceEntry.getCohortStats("SAS").getGenotypesCount().get(genotype));
        assertEquals(7025, sourceEntry.getCohortStats("SAS").getRefAlleleCount());
        assertEquals(0, sourceEntry.getCohortStats("SAS").getAltAlleleCount());
        
        // Minor allele frequencies
        assertEquals(9 / 10426.0, sourceEntry.getCohortStats("ALL").getMaf(), 0.00001);
        assertEquals(0, sourceEntry.getCohortStats("SAS").getMaf(), 0.001);
        assertEquals(0, sourceEntry.getCohortStats("AFR").getMaf(), 0.00001);
        assertEquals(0, sourceEntry.getCohortStats("AMR").getMaf(), 0.00001);

        System.out.println("genotypes for C -> G in SAS: " + sourceEntry.getCohortStats("SAS").getGenotypesCount());

        v = res.get(1);
        sourceEntry = v.getSourceEntry(source.getFileId(), source.getStudyId());

        assertEquals(2, sourceEntry.getCohortStats("NFE").getAltAlleleCount());
        genotype = new Genotype("0/2", v.getReference(), v.getAlternate());
        assertEquals(12, (int) sourceEntry.getCohortStats("AFR").getGenotypesCount().get(genotype));
        genotype = new Genotype("0/1", v.getReference(), v.getAlternate());
        assertEquals(7, (int) sourceEntry.getCohortStats("SAS").getGenotypesCount().get(genotype));
        assertEquals(7025, sourceEntry.getCohortStats("SAS").getRefAlleleCount());
        assertEquals(7, sourceEntry.getCohortStats("SAS").getAltAlleleCount());
        genotype = new Genotype("0/0", v.getReference(), v.getAlternate());
        assertEquals(7018 / 2, (int) sourceEntry.getCohortStats("SAS").getGenotypesCount().get(genotype));
        System.out.println("genotypes for C -> T in SAS: " + sourceEntry.getCohortStats("SAS").getGenotypesCount());
    }

    @Test
    public void testGetHeterozygousGenotype() throws Exception {
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            VariantAggregatedExacStatsCalculator.getHeterozygousGenotype(i, 4, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }

        Integer alleles[] = new Integer[2];
        VariantAggregatedExacStatsCalculator.getHeterozygousGenotype(3, 3, alleles);
        assertEquals(alleles[0], new Integer(1));
        assertEquals(alleles[1], new Integer(2));
        VariantAggregatedExacStatsCalculator.getHeterozygousGenotype(4, 4, alleles);
        assertEquals(alleles[0], new Integer(1));
        assertEquals(alleles[1], new Integer(2));
    }

    @Test
    public void testGetHomozygousGenotype() throws Exception {
        for (int i = 0; i < 11; i++) {
            Integer alleles[] = new Integer[2];
            VariantAggregatedExacStatsCalculator.getHomozygousGenotype(i, alleles);
            System.out.println("alleles[" + i + "] = " + alleles[0] + "/" + alleles[1]);
        }

        Integer alleles[] = new Integer[2];
        VariantAggregatedExacStatsCalculator.getHomozygousGenotype(3, alleles);    // 0/0
        assertEquals(alleles[0], alleles[1]);
    }
}
