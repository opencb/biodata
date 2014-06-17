package org.opencb.biodata.models.variant;

import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VariantVcfEVSFactoryTest {

    private VariantSource source = new VariantSource("EVS", "EVS", "EVS", "EVS");
   private VariantFactory factory = new VariantVcfEVSFactory();

    @Test
    public void testCreate_AA_AC_TT_GT() throws Exception { // AA,AC,TT,GT,...

        String line="1\t69428\trs140739101\tT\tG\t.\tPASS\tMAF=4.5707,0.3663,3.0647;GTS=GG,GT,TT;GTC=93,141,5101";

        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        ArchivedVariantFile avf = v.getFile(source.getFileId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("0/0","T","G"), new Integer(5101));
        genotypes.put(new Genotype("1/0","T","G"), new Integer(141));
        genotypes.put(new Genotype("1/1","T","G"), new Integer(93));

        assertEquals(avf.getStats().getGenotypesCount(), genotypes);

    }

    @Test
    public void testCreate_A_C_T_G(){ // A,C,T,G

        String line = "Y\t25375759\trs373156833\tT\tA\t.\tPASS\tMAF=0.0,0.1751,0.0409;GTS=A,T;GTC=1,2442";
        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);
        ArchivedVariantFile avf = v.getFile(source.getFileId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("0/0", "T", "A"), new Integer(2442));
        genotypes.put(new Genotype("1/1", "T", "A"), new Integer(1));

        assertEquals(avf.getStats().getGenotypesCount(), genotypes);

    }

    @Test
    public void testCreate_R_RR_A1R_A1A1(){ // R, RR, A1R, A1A1
        String line  ="X\t100117423\t.\tAG\tA\t.\tPASS\tMAF=0.0308,0.0269,0.0294;GTS=A1A1,A1R,RR,R;GTC=1,1,3947,2306;";

        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);

        assertEquals(v.getReference(), "G");
        assertEquals(v.getAlternate(), "");


        ArchivedVariantFile avf = v.getFile(source.getFileId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("1/1", "G", ""), new Integer(1));
        genotypes.put(new Genotype("1/0", "G", ""), new Integer(1));
        genotypes.put(new Genotype("0/0", "G", ""), new Integer(6253));

        assertEquals(avf.getStats().getGenotypesCount(), genotypes);

    }

    @Test
    public void testCreate_R_RR_A1A1_A1R_A1(){ // A1,A2,A3
        String line = "X\t106362078\trs3216052\tCT\tC\t.\tPASS\tMAF=18.1215,25.2889,38.7555;GTS=A1A1,A1R,A1,RR,R;GTC=960,1298,737,1691,1570";


        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 1);

        Variant v = res.get(0);

        assertEquals(v.getReference(), "T");
        assertEquals(v.getAlternate(), "");


        ArchivedVariantFile avf = v.getFile(source.getFileId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("1/1", "T", ""), new Integer(1697));
        genotypes.put(new Genotype("1/0", "T", ""), new Integer(1298));
        genotypes.put(new Genotype("0/0", "T", ""), new Integer(3261));

        assertEquals(avf.getStats().getGenotypesCount(), genotypes);
    }

    @Test
    public void testCreate_A1A1_A1A2_A2R_A2_RR_R(){// A1A2,A1A3...

        String line = "X\t14039552\t.\tCA\tCAA,C\t.\tPASS\tMAF=5.3453,4.2467,4.9459;GTS=A1A1,A1A2,A1R,A1,A2A2,A2R,A2,RR,R;GTC=0,0,134,162,4,92,107,3707,2027;";

        List<Variant> res = factory.create(source, line);

        assertTrue(res.size() == 2);

        Variant v = res.get(0);

        assertEquals(v.getReference(), "");
        assertEquals(v.getAlternate(), "A");


        ArchivedVariantFile avf = v.getFile(source.getFileId());

        Map<Genotype, Integer> genotypes = new HashMap<>();

        genotypes.put(new Genotype("1/1", "", "A"), new Integer(162));
        genotypes.put(new Genotype("1/0", "", "A"), new Integer(134));
        genotypes.put(new Genotype("0/0", "", "A"), new Integer(5734));
        genotypes.put(new Genotype("./.", "", "A"), new Integer(203));

        assertEquals(avf.getStats().getGenotypesCount(), genotypes);


        v = res.get(1);

        assertEquals(v.getReference(), "A");
        assertEquals(v.getAlternate(), "");


        avf = v.getFile(source.getFileId());

        genotypes = new HashMap<>();

        genotypes.put(new Genotype("1/1", "A", ""), new Integer(111));
        genotypes.put(new Genotype("1/0", "A", ""), new Integer(92));
        genotypes.put(new Genotype("0/0", "A", ""), new Integer(5734));
        genotypes.put(new Genotype("./.", "A", ""), new Integer(296));

        assertEquals(avf.getStats().getGenotypesCount(), genotypes);


    }

}