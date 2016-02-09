package org.opencb.biodata.models.variant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VariantTransformToEnsemblFormatTest {

    @Test
    public void testTransformToEnsemblFormatDel_TA_T() throws Exception {

        Variant v1 = new Variant("12",10144,10145,"TA","T");
        Variant v1_esembl_format = new Variant("12",10145,10145,"A","-");
        v1 = v1.copyInEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatIns_A_AC() throws Exception {

        Variant v1 = new Variant("12",724498,724499,"A","AC");
        Variant v1_esembl_format = new Variant("12",724499,724498,"-","C");
        v1 = v1.copyInEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatComplex_CAAATCTGGAT_CGAATCTGGAC() throws Exception {

        Variant v1 = new Variant("12",717318,717328,"CAAATCTGGAT","CGAATCTGGAC");
        Variant v1_esembl_format = new Variant ("12",717319,717328,"AAATCTGGAT","GAATCTGGAC");
        v1 = v1.copyInEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatIns_A_AC_normalized() throws Exception {

        String insertion = "8\t12601\t12600\t-/C\t+\n";
        String serializedInsertion;

        VariantVcfFactory variantVcfFactory = new VariantVcfFactory();
        VariantSource source = new VariantSource("filename", "fid", "sid", "sname");
        String vcfline = "8\t12600\t.\tA\tAC\t.\t.\t.";
        Variant v = variantVcfFactory.create(source, vcfline).get(0);
        v = v.copyInEnsemblFormat();
        serializedInsertion = formatAsEnsembl(v);

        assertEquals(insertion, serializedInsertion);

    }

    @Test
    public void testTransformToEnsemblFormatIns_A_ACT_normalized() throws Exception {

        String insertion = "8\t12601\t12600\t-/CT\t+\n";
        String serializedInsertion;

        VariantVcfFactory variantVcfFactory = new VariantVcfFactory();
        VariantSource source = new VariantSource("filename", "fid", "sid", "sname");
        String vcfline = "8\t12600\t.\tA\tACT\t.\t.\t.";
        Variant v = variantVcfFactory.create(source, vcfline).get(0);
        v = v.copyInEnsemblFormat();
        serializedInsertion = formatAsEnsembl(v);

        assertEquals(insertion, serializedInsertion);

    }

    @Test
    public void testTransformToEnsemblFormatIns_AG_ACT_normalized() throws Exception {

        String insertion = "8\t12602\t12601\tG/CT\t+\n";
        String serializedInsertion;

        VariantVcfFactory variantVcfFactory = new VariantVcfFactory();
        VariantSource source = new VariantSource("filename", "fid", "sid", "sname");
        String vcfline = "8\t12600\t.\tAG\tACT\t.\t.\t.";
        Variant v = variantVcfFactory.create(source, vcfline).get(0);
        v = v.copyInEnsemblFormat();
        serializedInsertion = formatAsEnsembl(v);

        assertEquals(insertion, serializedInsertion);
    }

    @Test
    public void testTransformToEnsemblFormatDel_TAGC_T_normalized() throws Exception {

        String insertion = "8\t12601\t12603\tAGC/-\t+\n";
        String serializedInsertion;

        VariantVcfFactory variantVcfFactory = new VariantVcfFactory();
        VariantSource source = new VariantSource("filename", "fid", "sid", "sname");
        String vcfline = "8\t12600\t.\tTAGC\tT\t.\t.\t.";
        Variant v = variantVcfFactory.create(source, vcfline).get(0);
        v = v.copyInEnsemblFormat();
        serializedInsertion = formatAsEnsembl(v);

        assertEquals(insertion, serializedInsertion);
    }

    private String formatAsEnsembl(Variant v) {
        return String.format("%s\t%s\t%s\t%s/%s\t+\n",
                v.getChromosome(),
                v.getStart(),
                v.getEnd(),
                v.getReference(),
                v.getAlternate());
    }
}
