package org.opencb.biodata.formats.feature.gtf.io;

import org.junit.Test;
import org.opencb.biodata.formats.feature.gtf.Gtf;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class GtfReaderTest {

    @Test
    public void whitespaceAttributeParseBug() throws Exception {
        String gtfRecord = "4\tensembl_havana\tgene\t7392435\t7442901\t.\t+\t.\tgene_id \"ENSDARG00000076014\"; gene_version \"3\"; gene_name \"ERC1 (3 of 3)\"; gene_source \"ensembl_havana\"; gene_biotype \"protein_coding\";";

        Map<String, String> attributes = new HashMap<String, String>() {{
            put("gene_id", "ENSDARG00000076014");
            put("gene_version", "3");
            put("gene_name", "ERC1 (3 of 3)");
            put("gene_source", "ensembl_havana");
            put("gene_biotype", "protein_coding");
        }};
        final Gtf expected = new Gtf("4", "ensembl_havana", "gene", 7392435, 7442901, ".", "+", ".", attributes);

        final GtfReader gtfReader = new GtfReader(new StringReader(gtfRecord));
        final Gtf actual = gtfReader.read();

        assertEquals(expected, actual);
    }
}