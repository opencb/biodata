package org.opencb.biodata.formats.pedigree;

import org.junit.Test;
import org.opencb.biodata.models.core.pedigree.Pedigree;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jtarraga on 09/01/17.
 */
public class PedigreeManagerTest {

    @Test
    public void test() {
        try {
            Path inputPath = Paths.get(getClass().getResource("/test1.ped").toURI());
            Path outputPath = Paths.get("/tmp/output.ped");

            PedigreeManager pedigreeManager = new PedigreeManager();
            Pedigree pedigree = pedigreeManager.parse(inputPath);
            System.out.println(pedigree);
            pedigreeManager.save(pedigree, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}