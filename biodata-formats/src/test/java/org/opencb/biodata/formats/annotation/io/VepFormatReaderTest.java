package org.opencb.biodata.formats.annotation.io;

import org.junit.Test;
import org.opencb.biodata.models.variant.annotation.VariantAnnotation;

import java.util.List;

import static org.junit.Assert.*;

public class VepFormatReaderTest {

    @Test
    public void testRead() throws Exception {
        VepFormatReader vepFormatReader = new VepFormatReader(getClass().getResource("/vepoutputtest.tsv.gz").getFile());
        vepFormatReader.open();
        vepFormatReader.pre();
        List<VariantAnnotation> variantAnnotationList = vepFormatReader.read(10000);
        assertEquals(variantAnnotationList.size(),2);
        assertEquals(variantAnnotationList.get(0).getChromosome(), "1");
        assertEquals(variantAnnotationList.get(0).getStart(), 628314);
        assertEquals(variantAnnotationList.get(0).getEnd(), 628332);
        assertEquals(variantAnnotationList.get(0).getReferenceAllele(), "CAGGTGACACTGGGGACAC");
        assertEquals(variantAnnotationList.get(0).getAlternativeAllele(), "-");
        assertEquals(variantAnnotationList.get(0).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(0).getConsequenceTypes().get(0).getSoTerms().size(), 1);
        assertEquals(variantAnnotationList.get(0).getConsequenceTypes().get(0).getSoTerms().get(0).getSoName(), "intron_variant");
        assertEquals(variantAnnotationList.get(1).getChromosome(), "LGE22C19W28_E50C23");
        assertEquals(variantAnnotationList.get(1).getStart(), 351697);
        assertEquals(variantAnnotationList.get(1).getEnd(), 351697);
        assertEquals(variantAnnotationList.get(1).getReferenceAllele(), "-");
        assertEquals(variantAnnotationList.get(1).getAlternativeAllele(), "AT");
        assertEquals(variantAnnotationList.get(1).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(1).getConsequenceTypes().get(0).getSoTerms().size(), 1);
        assertEquals(variantAnnotationList.get(1).getConsequenceTypes().get(0).getSoTerms().get(0).getSoName(), "intergenic_variant");
        vepFormatReader.post();
        vepFormatReader.close();
    }
}