package org.opencb.biodata.tools.variant.metadata;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.metadata.Individual;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.commons.datastore.core.Query;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by jtarraga on 11/08/17.
 */
public class VariantMetadataManagerTest {

    private VariantMetadataManager manager;
    private VariantMetadata variantMetadata;

    @BeforeEach
    public void setUp() throws Exception {
        variantMetadata = createMetadata();
        manager = new VariantMetadataManager();
        manager.setVariantMetadata(variantMetadata);
    }

    public VariantMetadata createMetadata() {
        VariantMetadata variantMetadata = new VariantMetadata();

        List<VariantStudyMetadata> datasets = new ArrayList<>();

        VariantStudyMetadata studyMetadata = new VariantStudyMetadata();

        List<Individual> individuals = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Individual individual = new Individual();
            individual.setId("Person_" + i);
            individual.setFamily("familyAA");
            individual.setFather("Father_" + i);
            individual.setMother("Mother_" + i);
            individual.setSex(i == 1 ? "male" : "female");
            individual.setPhenotype(i == 2 ? "cancer22" : "cancer00");

            List<Sample> samples = new ArrayList<>();
            for (int s = 0; s < 4; s++) {
                Sample sample = new Sample();
                sample.setId("Sample_" + i + "_" + s);
                sample.setAnnotations(new HashedMap());
                for (int a = 0; a < 4; a++) {
                    switch (a) {
                        case 0:
                            sample.getAnnotations().put("age", "" + (s + a * 10 + (i + 1) * 10));
                            break;
                        case 1:
                            sample.getAnnotations().put("weight", "" + (s + ((a + i) * 2) + 70));
                            break;
                        case 2:
                            sample.getAnnotations().put("hpo", (a > 1 ? "Q111" + s : "Q000" + a));
                            break;
                        case 3:
                            sample.getAnnotations().put("population", (a > 2 ? "P222" + s : "P000" + a));
                            break;
                    }
                }
                samples.add(sample);
            }
            individual.setSamples(samples);

            individuals.add(individual);
        }

        studyMetadata.setId("11");
        studyMetadata.setIndividuals(individuals);

        datasets.add(studyMetadata);
        variantMetadata.setStudies(datasets);

        return variantMetadata;
    }

    @Test
    public void getSamples() {
        List<Sample> samples = manager.getSamples(variantMetadata.getStudies().get(0).getId());
        System.out.println("Samples found: " + samples.size());
        for (int i = 0; i < samples.size(); i++) {
            System.out.println(samples.get(i));
        }

        assertEquals(samples.size(), 12);
    }

    @Test
    public void getSamplesByQuery() {
        Query query = new Query();
        query.put("age", ">=30");
        query.put("population", "=P2220");
        query.put("weight", "<78");

        List<Sample> samples = manager.getSamples(query, variantMetadata.getStudies().get(0).getId());
        System.out.println("Query:");
        System.out.println(query);
        System.out.println("Samples found: " + samples.size());
        for (int i = 0; i < samples.size(); i++) {
            System.out.println(samples.get(i));
        }

        assertEquals(samples.size(), 1);
        assertEquals("Sample_2_0", samples.get(0).getId());
    }


    @Test
    public void print() {
        try {
            manager.print();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void save() {
        try {
            Path path = Paths.get("/tmp/ds.meta.json");
            if (path.toFile().exists()) {
                path.toFile().delete();
            }
            manager.save(path);
            assert(path.toFile().exists());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void savePretty() {
        try {
            Path path = Paths.get("/tmp/ds.meta.json");
            if (path.toFile().exists()) {
                path.toFile().delete();
            }
            manager.save(path, true);
            assert(path.toFile().exists());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void load() {
        try {
            Path path = Paths.get("/tmp/ds.meta.json");
            if (path.toFile().exists()) {
                path.toFile().delete();
            }
            manager.save(path, true);

            manager = new VariantMetadataManager();
            manager.load(path);

            List<Sample> samples = manager.getSamples(variantMetadata.getStudies().get(0).getId());
            System.out.println("Samples found: " + samples.size());
            for (int i = 0; i < samples.size(); i++) {
                System.out.println(samples.get(i));
            }

            assertEquals(samples.size(), 12);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}