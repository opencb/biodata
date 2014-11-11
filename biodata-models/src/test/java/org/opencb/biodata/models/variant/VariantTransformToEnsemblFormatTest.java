package org.opencb.biodata.models.variant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VariantTransformToEnsemblFormatTest {

    @Test
    public void testTransformToEnsemblFormatDel_TA_T() throws Exception {

        Variant v1 = new Variant("12",10144,10145,"TA","T");
        Variant v1_esembl_format = new Variant("12",10145,10145,"A","-");
        v1.transformToEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatIns_A_AC() throws Exception {

        Variant v1 = new Variant("12",724498,724499,"A","AC");
        Variant v1_esembl_format = new Variant("12",724499,724498,"-","C");
        v1.transformToEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatComplex_CAAATCTGGAT_CGAATCTGGAC() throws Exception {

        Variant v1 = new Variant("12",717318,717328,"CAAATCTGGAT","CGAATCTGGAC");
        Variant v1_esembl_format = new Variant ("12",717319,717328,"AAATCTGGAT","GAATCTGGAC");
        v1.transformToEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }
}
