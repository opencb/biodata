package org.opencb.biodata.tools.variant.annotation;

import org.junit.Test;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;

public class VariantControlMongoAnnotatorTest {

    @Test
    public void testAnnot() throws Exception {
        Variant v = new Variant("1", 14653, 14653, "C", "T");
        VariantAnnotator va = new VariantControlMongoAnnotator();
        ArchivedVariantFile avf = new ArchivedVariantFile("TEST", "TEST", "TEST");
        v.addFile(avf);
        va.annot(v);
    }
}