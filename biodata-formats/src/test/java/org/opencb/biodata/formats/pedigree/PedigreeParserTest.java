package org.opencb.biodata.formats.pedigree;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jtarraga on 09/01/17.
 */
public class PedigreeParserTest {

    @Test
    public void test() {
        try {
            Path inputPath = Paths.get(getClass().getResource("/pheno").toURI());
            Path outputPath = Paths.get("/tmp/output.ped");

            PedigreeParser pedigreeParser = new PedigreeParser();
            List<Pedigree> pedigrees = pedigreeParser.parse(inputPath);
            for (Pedigree pedigree: pedigrees) {
                System.out.println(pedigree.toJSON());
            }
            pedigreeParser.save(pedigrees, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}