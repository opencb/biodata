package org.opencb.biodata.tools.variant.annotation;

import org.junit.Test;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;

import static org.junit.Assert.assertEquals;

public class VariantGeneNameAnnotatorTest {

    @Test
    public void testAnnot() throws Exception {

        Variant v = new Variant("1", 14653, 14653, "C", "T");
        VariantAnnotator va = new VariantGeneNameAnnotator();
        ArchivedVariantFile avf = new ArchivedVariantFile("TEST", "TEST", "TEST");
        v.addFile(avf);
        va.annot(v);
        String gn = v.getFile("TEST").getAttribute("GeneNames");
        assertEquals(gn, "WASH7P,DDX11L1");
    }
}