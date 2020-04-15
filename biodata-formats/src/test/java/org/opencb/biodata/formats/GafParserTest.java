package org.opencb.biodata.formats;

import org.junit.Test;
import org.opencb.biodata.formats.gaf.GafParser;
import org.opencb.biodata.models.core.AnnotationEvidence;
import org.opencb.biodata.models.core.FeatureOntologyTermAnnotation;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GafParserTest {

    @Test
    public void test() throws IOException, ParseException {
        BufferedReader bufferedReader = FileUtils.newBufferedReader(Paths.get(getClass()
                .getResource("/goa_human.gaf.gz").getPath()));

        GafParser parser = new GafParser();
        Map<String, List<FeatureOntologyTermAnnotation>> results = parser.parseGaf(bufferedReader);
        assertEquals(1, results.size());

        List<FeatureOntologyTermAnnotation> annotations = results.get("A0A024RBG1");
        assertEquals(4, annotations.size());

        FeatureOntologyTermAnnotation annotation0 = annotations.get(0);
        AnnotationEvidence evidence = annotation0.getEvidence().get(0);
        assertEquals("GO:0005829", annotation0.getId());
        assertEquals("IDA", evidence.getCode());
        assertNull(evidence.getQualifier());
        assertEquals("GO_REF:0000052", evidence.getPublications().toArray()[0]);
    }
}
