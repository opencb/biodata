/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.annotation.io;

import org.junit.Ignore;
import org.junit.Test;
import org.opencb.biodata.formats.variant.annotation.io.VepFormatReader;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
//import org.opencb.biodata.models.variant.annotation.VariantAnnotation;

import java.util.List;
import static org.junit.Assert.assertEquals;

public class VepFormatReaderTest {

    @Test
    public void testRead() throws Exception {
        VepFormatReader vepFormatReader = new VepFormatReader(getClass().getResource("/vepoutputtest.tsv.gz").getFile());
        vepFormatReader.open();
        vepFormatReader.pre();
        List<VariantAnnotation> variantAnnotationList = vepFormatReader.read(10000);
        assertEquals(variantAnnotationList.size(),6);
        assertEquals(variantAnnotationList.get(0).getChromosome(), "1");
        assertEquals(variantAnnotationList.get(0).getStart(), Integer.valueOf(628314));
        assertEquals(variantAnnotationList.get(0).getReference(), "CAGGTGACACTGGGGACAC");
        assertEquals(variantAnnotationList.get(0).getAlternate(), "-");
        assertEquals(variantAnnotationList.get(0).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(0).getConsequenceTypes().get(0).getSequenceOntologyTerms().size(), 1);
        assertEquals(variantAnnotationList.get(0).getConsequenceTypes().get(0).getSequenceOntologyTerms().get(0).getName(), "intron_variant");

        assertEquals(variantAnnotationList.get(1).getChromosome(), "LGE22C19W28_E50C23");
        assertEquals(variantAnnotationList.get(1).getStart(), Integer.valueOf(351697));
        assertEquals(variantAnnotationList.get(1).getReference(), "-");
        assertEquals(variantAnnotationList.get(1).getAlternate(), "AT");
        assertEquals(variantAnnotationList.get(1).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(1).getConsequenceTypes().get(0).getSequenceOntologyTerms().size(), 1);
        assertEquals(variantAnnotationList.get(1).getConsequenceTypes().get(0).getSequenceOntologyTerms().get(0).getName(), "intergenic_variant");

        assertEquals(variantAnnotationList.get(2).getChromosome(), "10");
        assertEquals(variantAnnotationList.get(2).getStart(), Integer.valueOf(43615594));
        assertEquals(variantAnnotationList.get(2).getReference(), "G");
        assertEquals(variantAnnotationList.get(2).getAlternate(), "A");
        assertEquals(variantAnnotationList.get(2).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(2).getConsequenceTypes().get(0).getSequenceOntologyTerms().size(), 1);
        assertEquals(variantAnnotationList.get(2).getConsequenceTypes().get(0).getSequenceOntologyTerms().get(0).getName(), "synonymous_variant");

        assertEquals(variantAnnotationList.get(3).getChromosome(), "21");
        assertEquals(variantAnnotationList.get(3).getStart(), Integer.valueOf(9411239));
        assertEquals(variantAnnotationList.get(3).getReference(), "N");
        assertEquals(variantAnnotationList.get(3).getAlternate(), "A");
        assertEquals(variantAnnotationList.get(3).getId(), "rs559462325");
        assertEquals(variantAnnotationList.get(3).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(3).getConsequenceTypes().get(0).getSequenceOntologyTerms().size(), 1);
        assertEquals(variantAnnotationList.get(3).getConsequenceTypes().get(0).getSequenceOntologyTerms().get(0).getName(), "intergenic_variant");

        assertEquals(variantAnnotationList.get(4).getChromosome(), "21");
        assertEquals(variantAnnotationList.get(4).getStart(), Integer.valueOf(9412077));
        assertEquals(variantAnnotationList.get(4).getReference(), "NN");
        assertEquals(variantAnnotationList.get(4).getAlternate(), "-");
        assertEquals(variantAnnotationList.get(4).getId(), "rs374249157");
        assertEquals(variantAnnotationList.get(4).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(4).getConsequenceTypes().get(0).getSequenceOntologyTerms().size(), 1);
        assertEquals(variantAnnotationList.get(4).getConsequenceTypes().get(0).getSequenceOntologyTerms().get(0).getName(), "intergenic_variant");

        assertEquals(variantAnnotationList.get(5).getChromosome(), "21");
        assertEquals(variantAnnotationList.get(5).getStart(), Integer.valueOf(26192725));
        assertEquals(variantAnnotationList.get(5).getReference(), "-");
        assertEquals(variantAnnotationList.get(5).getAlternate(), "AAGAAAATTAATTTCTGTTGTCTGAAGTTG");
        assertEquals(variantAnnotationList.get(5).getId(), "rs557738790");
        assertEquals(variantAnnotationList.get(5).getConsequenceTypes().size(), 1);
        assertEquals(variantAnnotationList.get(5).getConsequenceTypes().get(0).getSequenceOntologyTerms().size(), 1);
        assertEquals(variantAnnotationList.get(5).getConsequenceTypes().get(0).getSequenceOntologyTerms().get(0).getName(), "intergenic_variant");

        vepFormatReader.post();
        vepFormatReader.close();

    }
}