package org.opencb.biodata.formats.pedigree;

import org.junit.Test;
import org.opencb.biodata.models.core.pedigree.Pedigree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jtarraga on 09/01/17.
 */
public class PedigreeManagerTest {

    @Test
    public void test() {
        try {
            Path inputPath = Paths.get(getClass().getResource("/pheno").toURI());
            Path outputPath = Paths.get("/tmp/output.ped");

            PedigreeManager pedigreeManager = new PedigreeManager();
            List<Pedigree> pedigrees = pedigreeManager.parse(inputPath);
            for (Pedigree pedigree: pedigrees) {
                System.out.println(pedigree.toJSON());
            }
            pedigreeManager.save(pedigrees, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}