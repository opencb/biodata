package org.opencb.biodata.models.variant;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class VariantVcfEVSFactoryTest {

    private VariantSource source = new VariantSource("EVS", "EVS", "EVS", "EVS");
   private VariantFactory factory = new VariantVcfEVSFactory();

    @Ignore
    @Test
    public void testCreate() throws Exception {

        String line="1\t69428\trs140739101\tT\tG\t.\tPASS\tDBSNP=dbSNP_134;EA_AC=313,6535;AA_AC=14,3808;TAC=327,10343;MAF=4.5707,0.3663,3.0647;GTS=GG,GT,TT;EA_GTC=92,129,3203;AA_GTC=1,12,1898;GTC=93,141,5101;DP=110;GL=OR4F5;CP=1.0;CG=0.9;AA=T;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=NM_001005484.1:missense;HGVS_CDNA_VAR=NM_001005484.1:c.338T>G;HGVS_PROTEIN_VAR=NM_001005484.1:p.(F113C);CDS_SIZES=NM_001005484.1:918;GS=205;PH=probably-damaging:0.999;EA_AGE=.;AA_AGE=.";

        List<Variant> res = factory.create(source, line);

        Variant v = res.get(0);
        ArchivedVariantFile avf = v.getFile(source.getFileId());

        System.out.println("v = " + v);
        System.out.println(avf.getAttribute("GTS"));
        System.out.println(avf.getAttribute("GTC"));

        System.out.println("Genotypes count = " + avf.getStats().getGenotypesCount());


    }

    @Ignore
    @Test
    public void testCreate2(){ // AA, CC
        String line="1\t156706559\trs8658\tA\tG,C\t.\tPASS\tDBSNP=dbSNP_52;EA_AC=5849,2751,0;AA_AC=3464,942,0;TAC=9313,3693,0;MAF=31.9884,21.3799,28.3946;GTS=GG,GC,GA,CC,CA,AA;EA_GTC=2013,1823,0,464,0,0;AA_GTC=1439,586,0,178,0,0;GTC=3452,2409,0,642,0,0;DP=36;GL=RRNAD1;CP=0.0;CG=-0.4;AA=C;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=NM_015997.3:utr-3,NM_001142560.1:utr-3,NM_015997.3:utr-3,NM_001142560.1:utr-3;HGVS_CDNA_VAR=NM_015997.3:c.*14A>C,NM_001142560.1:c.*123A>C,NM_015997.3:c.*14A>G,NM_001142560.1:c.*123A>G;HGVS_PROTEIN_VAR=.,.,.,.;CDS_SIZES=NM_015997.3:1428,NM_001142560.1:834,NM_015997.3:1428,NM_001142560.1:834;GS=.,.,.,.;PH=.,.,.,.;EA_AGE=.;AA_AGE=.";
        List<Variant> res = factory.create(source, line);

        for(Variant v : res){
        ArchivedVariantFile avf = v.getFile(source.getFileId());

        System.out.println("v = " + v);
        System.out.println(avf.getAttribute("GTS"));
        System.out.println(avf.getAttribute("GTC"));

        System.out.println("Genotypes count = " + avf.getStats().getGenotypesCount());
        }

    }

    @Ignore
    @Test
    public void testCreate3(){
        String line  ="1\t981860\t.\tGC\tGCC,G\t.\tPASS\tDBSNP=.;EA_AC=87,176,7897;AA_AC=75,129,3992;TAC=162,305,11889;MAF=3.223,4.8618,3.7795;GTS=A1A1,A1A2,A1R,A2A2,A2R,RR;EA_GTC=1,0,85,1,174,3819;AA_GTC=3,0,69,2,125,1899;GTC=4,0,154,3,299,5718;DP=15;GL=AGRN;CP=0.0;CG=-4.9;AA=.;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=NM_198576.3:frameshift,NM_198576.3:frameshift;HGVS_CDNA_VAR=NM_198576.3:c.2996del1,NM_198576.3:c.2995_2996insC;HGVS_PROTEIN_VAR=NM_198576.3:p.(G1002Afs*38),NM_198576.3:p.(G1002Rfs*58);CDS_SIZES=NM_198576.3:6138,NM_198576.3:6138;GS=.,.;PH=.,.;EA_AGE=.;AA_AGE=.";

        List<Variant> res = factory.create(source, line);

        for(Variant v : res){
            ArchivedVariantFile avf = v.getFile(source.getFileId());

            System.out.println("v = " + v);
            System.out.println(avf.getAttribute("GTS"));
            System.out.println(avf.getAttribute("GTC"));

            System.out.println("Genotypes count = " + avf.getStats().getGenotypesCount());
        }
    }

        @Test
        public void testCreate4() {
            String line = "1\t6291918\trs148639379\tTA\tTAAAAA,TAA,TAAA,T\t.\tPASS\tDBSNP=dbSNP_134;EA_AC=603,913,70,792,5874;AA_AC=234,1122,546,188,2174;TAC=837,2035,616,980,8048;MAF=28.8173,49.015,35.6983;GTS=A1A1,A1A2,A1A3,A1A4,A1R,A2A2,A2A3,A2A4,A2R,A3A3,A3A4,A3R,A4A4,A4R,RR;EA_GTC=0,10,0,2,591,11,10,9,862,0,1,59,0,780,1791;AA_GTC=2,57,17,0,156,65,184,16,735,9,9,318,1,161,402;GTC=2,67,17,2,747,76,194,25,1597,9,10,377,1,941,2193;DP=22;GL=ICMT;CP=0.0;CG=0.6;AA=.;CA=.;EXOME_CHIP=no;GWAS_PUBMED=.;FG=NM_012405.3:intron,NM_012405.3:intron,NM_012405.3:intron,NM_012405.3:intron;HGVS_CDNA_VAR=NM_012405.3:c.672+43del1,NM_012405.3:c.672+43_672+44insTT,NM_012405.3:c.672+43_672+44insT,NM_012405.3:c.672+43_672+44insTTTT;HGVS_PROTEIN_VAR=.,.,.,.;CDS_SIZES=NM_012405.3:855,NM_012405.3:855,NM_012405.3:855,NM_012405.3:855;GS=.,.,.,.;PH=.,.,.,.;EA_AGE=.;AA_AGE=.";

            List<Variant> res = factory.create(source, line);

            for (Variant v : res) {
                ArchivedVariantFile avf = v.getFile(source.getFileId());

                System.out.println("v = " + v);
                System.out.println(avf.getAttribute("GTS"));
                System.out.println(avf.getAttribute("GTC"));

                System.out.println("Genotypes count = " + avf.getStats().getGenotypesCount());
            }
        }
}