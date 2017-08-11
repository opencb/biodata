package org.opencb.biodata.tools.variant;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.opencb.biodata.models.metadata.Individual;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;
import org.opencb.commons.datastore.core.Query;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jtarraga on 11/08/17.
 */
public class VariantMetadataManagerTest {

    public VariantMetadata createMetadata() {
        VariantMetadata variantMetadata = new VariantMetadata();

        List<VariantDatasetMetadata> datasets = new ArrayList<>();

        VariantDatasetMetadata variantDatasetMetadata = new VariantDatasetMetadata();

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

            System.out.println(individual);
            individuals.add(individual);
        }

        variantDatasetMetadata.setId("11");
        variantDatasetMetadata.setIndividuals(individuals);

        datasets.add(variantDatasetMetadata);
        variantMetadata.setDatasets(datasets);

        return variantMetadata;
    }

    @Test
    public void parseQuery() {
        VariantMetadata variantMetadata = createMetadata();
        VariantMetadataManager manager = new VariantMetadataManager();
        manager.setVariantMetadata(variantMetadata);

        Query query = new Query();
        query.put("age", ">=30");
        query.put("population", "=P2220");
        query.put("weight", "<78");

        List<Sample> samples = manager.getSamples(query, variantMetadata.getDatasets().get(0).getId());
        System.out.println("Query:");
        System.out.println(query);
        System.out.println("Samples found: " + samples.size());
        for (int i = 0; i < samples.size(); i++) {
            System.out.println(samples.get(i));
        }

        assertEquals(samples.size(), 1);
        assertEquals("Sample_2_0", samples.get(0).getId());
    }
}