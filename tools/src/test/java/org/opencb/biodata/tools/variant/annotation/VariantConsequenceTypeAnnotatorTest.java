package org.opencb.biodata.tools.variant.annotation;

import org.junit.Test;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;

import static org.junit.Assert.assertTrue;

public class VariantConsequenceTypeAnnotatorTest {

    @Test
    public void testAnnot() throws Exception {

        Variant v = new Variant("1", 14653, 14653, "C", "T");
        VariantAnnotator va = new VariantConsequenceTypeAnnotator();
        ArchivedVariantFile avf = new ArchivedVariantFile("TEST", "TEST");
        v.addFile(avf);
        va.annot(v);
        String gn = v.getFile("TEST", "TEST").getAttribute("ConsType");
        assertTrue(gn.contains("SNP"));
    }
}