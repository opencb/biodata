package org.opencb.biodata.tools.variant.annotation;

import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;

import static org.junit.Assert.assertEquals;

public class VariantSNPAnnotatorTest {

    @Test
    public void testAnnot1() throws Exception {

        Variant v = new Variant("1", 14653, 14653, "C", "T");
        VariantAnnotator va = new VariantSNPAnnotator();
        va.annot(v);
        assertEquals(v.getId(), "rs62635297");
    }
}