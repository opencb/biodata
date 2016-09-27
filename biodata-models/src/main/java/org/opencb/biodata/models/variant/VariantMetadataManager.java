package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.biodata.models.metadata.Cohort;
import org.opencb.biodata.models.metadata.SampleSetType;
import org.opencb.biodata.models.metadata.Species;
import org.opencb.biodata.models.variant.metadata.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaquin on 9/26/16.
 */
public class VariantMetadataManager {

    private ObjectMapper mapper = null;
    private String metaFilename = null;
    private VariantMetadata variantMetadata = null;

    public VariantMetadataManager() {
        this("unknown", "unknown", "noname", "noname");
    }

    public VariantMetadataManager(String species, String assembly,
                                  String datasetName, String filename) {

        variantMetadata = new VariantMetadata();

        // set species
        variantMetadata.setSpecies(new Species(species, species, species, "unknown", assembly));

        // set file
        List<VariantFileMetadata> files = new ArrayList<>();
        VariantFileMetadata file = new VariantFileMetadata();
        file.setId(filename);
        files.add(file);

        // set dataset
        List<VariantDatasetMetadata> datasets = new ArrayList<>();
        VariantDatasetMetadata dataset = new VariantDatasetMetadata();
        dataset.setId(datasetName);
        dataset.setFiles(files);
        datasets.add(dataset);
        variantMetadata.setDatasets(datasets);

        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
    }

    public void load(String filename) throws IOException {
        this.metaFilename = filename;
        variantMetadata = mapper.readValue(filename, VariantMetadata.class);
    }


    public void setSampleIds(String fileId, List<String> sampleIds) {
        for (VariantDatasetMetadata dataset: variantMetadata.getDatasets()) {
            for (VariantFileMetadata file: dataset.getFiles()) {
                if (fileId.equals(file.getId())) {
                    file.setSampleIds(sampleIds);
                    return;
                }
            }
        }
        // error management, not file found !!
    }

    public void createCohort(String datasetId, String cohortId, List<String> sampleIds, SampleSetType type) {
        // set cohort
        for (VariantDatasetMetadata dataset : variantMetadata.getDatasets()) {
            if (datasetId.equals(dataset.getId())) {
                // check if cohort exists
                if (dataset.getCohorts() == null) {
                    dataset.setCohorts(new ArrayList<>());
                    dataset.getCohorts().add(new Cohort(cohortId, sampleIds, type));
                    return;
                } else {
                    for (Cohort cohort : dataset.getCohorts()) {
                        if (cohortId.equals(cohort.getId())) {
                            // error management
                        }
                    }
                    dataset.getCohorts().add(new Cohort(cohortId, sampleIds, type));
                    return;
                }
            }
        }
        // error management
    }

    public void save() throws IOException {
        save(metaFilename);
    }

    public void save(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename));
        writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                mapper.readValue(variantMetadata.toString(), Object.class)));
        writer.close();
    }

    public VariantMetadata getVariantMetadata() {
        return variantMetadata;
    }

    public void setVariantMetadata(VariantMetadata variantMetadata) {
        this.variantMetadata = variantMetadata;
    }
}
