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
            Path inputPath = Paths.get(getClass().getResource("/pheno").toURI());
            Path outputPath = Paths.get("/tmp/output.ped");

            PedigreeManager pedigreeManager = new PedigreeManager();
            Pedigree pedigree = pedigreeManager.parse(inputPath);
            System.out.println(pedigree);
            pedigreeManager.save(pedigree, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test1() {
//        try {
//            String datasetId = "testing-pedigree";
//            VariantMetadataManager manager = new VariantMetadataManager();
//            manager.load("/tmp/test.vcf.avro.meta.json");
//
////            Pedigree pedigree = new PedigreeManager().parse(Paths.get("/home/jtarraga/appl-local/hpg-bigdata/hpg-bigdata-app/src/test/resources/test.ped"));
//            Pedigree pedigree = new PedigreeManager().parse(Paths.get("/tmp/test.ped"));
//            System.out.println("\n00:\n" + pedigree.toString());
//
//            manager.loadPedigree(pedigree, datasetId);
//            manager.save("/tmp/test.vcf.avro.meta.json.new");
//
//            Pedigree newPedigree = manager.getPedigree(datasetId);
//            System.out.println("\n11:\n" + newPedigree.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}