package org.opencb.biodata.formats;

import org.junit.Test;
import org.opencb.biodata.formats.gaf.GafParser;
import org.opencb.biodata.models.core.AnnotationEvidence;
import org.opencb.biodata.models.core.FeatureOntologyTermAnnotation;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GafParserTest {

    @Test
    public void test() throws IOException, ParseException {
        Path goaFile = Paths.get(getClass().getResource("/goa_human.gaf.gz").getPath());
        Path oboFile = Paths.get(getClass().getResource("/go-basic.obo").getPath());

        GafParser parser = new GafParser();
        Map<String, List<FeatureOntologyTermAnnotation>> results = parser.parseGaf(goaFile, oboFile);
        assertEquals(1, results.size());

        List<FeatureOntologyTermAnnotation> annotations = results.get("A0A024RBG1");
        assertEquals(4, annotations.size());

        FeatureOntologyTermAnnotation annotation0 = annotations.get(0);
        AnnotationEvidence evidence = annotation0.getEvidence().get(0);
        assertEquals("GO:0005829", annotation0.getId());
        assertEquals("IDA", evidence.getCode());
        assertEquals("cytosol", annotation0.getName());
        assertNull(evidence.getQualifier());
        assertEquals("GO_REF:0000052", evidence.getReferences().toArray()[0]);
    }
}
