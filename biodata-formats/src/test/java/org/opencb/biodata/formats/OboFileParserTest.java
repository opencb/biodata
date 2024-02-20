package org.opencb.biodata.formats;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.formats.obo.OboParser;
import org.opencb.biodata.models.core.OntologyTerm;
import org.opencb.commons.utils.FileUtils;

import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;

public class OboFileParserTest {

    @Test
    public void testOntology() throws IOException, ParseException {
        BufferedReader bufferedReader = FileUtils.newBufferedReader(Paths.get(getClass()
                .getResource("/hp.obo").getPath()));

        OboParser parser = new OboParser();
        List<OntologyTerm> terms = parser.parseOBO(bufferedReader, "Human Phenotype Ontology");
        assertEquals(4, terms.size());

        OntologyTerm term0 = terms.get(0);
        assertEquals("HP:0000001", term0.getId());
        assertEquals("All", term0.getName());
        assertNull(term0.getDescription());
        assertEquals("Root of all terms in the Human Phenotype Ontology.", term0.getComment());
        assertEquals("Human Phenotype Ontology", term0.getSource());

//        [Term]
//        id: HP:0000002
//        name: Abnormality of body height
//        def: "Deviation from the norm of height with respect to that which is expected according to age and gender norms." [HPO:probinson]
//        synonym: "Abnormality of body height" EXACT layperson []
//        xref: UMLS:C4025901
//        is_a: HP:0001507 ! Growth abnormality

        OntologyTerm term1 = terms.get(1);
        assertEquals("HP:0000002", term1.getId());
        assertEquals("Abnormality of body height", term1.getName());
        assertEquals("Deviation from the norm of height with respect to that which is expected according to age and gender norms.", term1.getDescription());
        assertEquals("Abnormality of body height", term1.getSynonyms().get(0));
        assertEquals("Testing A", term1.getSynonyms().get(1));
        assertEquals("UMLS:C4025901", term1.getXrefs().get(0));
        assertEquals("UMLS:test", term1.getXrefs().get(1));
        assertEquals("HP:0001507", term1.getParents().get(0));
        assertEquals("HP:0000000", term1.getParents().get(1));


        bufferedReader = FileUtils.newBufferedReader(Paths.get(getClass()
                .getResource("/go-basic.obo").getPath()));
        parser = new OboParser();
        terms = parser.parseOBO(bufferedReader, "GO");
        assertEquals(3112, terms.size());
    }
}
