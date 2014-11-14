package org.opencb.biodata.tools.variant.annotation;

import org.junit.Test;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;

public class VariantControlMongoAnnotatorTest {

    @Test
    public void testAnnot() throws Exception {
        Variant v = new Variant("1", 14653, 14653, "C", "T");
        VariantAnnotator va = new VariantControlMongoAnnotator();
        VariantSourceEntry avf = new VariantSourceEntry("TEST", "TEST");
        v.addSourceEntry(avf);
        va.annot(v);
    }
}