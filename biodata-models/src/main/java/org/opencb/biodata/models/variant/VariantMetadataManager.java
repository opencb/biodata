package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.biodata.models.core.pedigree.VariableField;
import org.opencb.biodata.models.metadata.Cohort;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.metadata.SampleSetType;
import org.opencb.biodata.models.metadata.Species;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by joaquin on 9/26/16.
 */
public class VariantMetadataManager {

    private VariantMetadata variantMetadata;

    private ObjectMapper mapper;
    private Logger logger;

    public VariantMetadataManager() {
        this(new Species("hsapiens", "Homo sapiens", "", null, "GRCh38"), "");
    }

    public VariantMetadataManager(Species species, String description) {
        variantMetadata = new VariantMetadata();

        variantMetadata.setDate(LocalDateTime.now().toString());
        variantMetadata.setSpecies(species);
        variantMetadata.setDescription(description);

//        // set file
//        List<VariantFileMetadata> files = new ArrayList<>();
//        VariantFileMetadata file = new VariantFileMetadata();
//        file.setId(filename);
//        files.add(file);
//
//        // set dataset
//        List<VariantDatasetMetadata> datasets = new ArrayList<>();
//        VariantDatasetMetadata dataset = new VariantDatasetMetadata();
//        dataset.setId(datasetName);
//        dataset.setFiles(files);
//        datasets.add(dataset);
//        variantMetadata.setDatasets(datasets);

        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);

        logger = LoggerFactory.getLogger(VariantMetadataManager.class);
    }

    public void load(Path path) throws IOException {
        FileUtils.checkPath(path);
        logger.debug("Loading variant metadata from '{}'", path.toAbsolutePath().toString());
        variantMetadata = mapper.readValue(path.toFile(), VariantMetadata.class);
    }


    public VariantDatasetMetadata getVariantDatasetMetadata(String datasetId) {
        for (VariantDatasetMetadata dataset : variantMetadata.getDatasets()) {
            if (datasetId.equals(dataset.getId())) {
                return dataset;
            }
        }
        return null;
    }

    public void addVariantDatasetMetadata(VariantDatasetMetadata variantDatasetMetadata) {
        if (variantDatasetMetadata != null) {
            VariantDatasetMetadata found = getVariantDatasetMetadata(variantDatasetMetadata.getId());
            // if there is not any dataset with that ID then we add the new one
            // TODO we need to think what to do when it exists, should we throw an exception?
            if (found != null) {
                variantMetadata.getDatasets().add(variantDatasetMetadata);
            } else {
                logger.error("Dataset ID already exists");
            }
        }
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
        // error management: file not found !!
    }

    public void createCohort(String datasetId, String cohortId, List<String> sampleIds, SampleSetType type) {
        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata != null) {
            // check if cohort exists
            if (variantDatasetMetadata.getCohorts() == null) {
                variantDatasetMetadata.setCohorts(new ArrayList<>());
                variantDatasetMetadata.getCohorts().add(new Cohort(cohortId, sampleIds, type));
            } else {
                for (Cohort cohort : variantDatasetMetadata.getCohorts()) {
                    if (cohortId.equals(cohort.getId())) {
                        // error management: cohort already exists !
                        return;
                    }
                }
                variantDatasetMetadata.getCohorts().add(new Cohort(cohortId, sampleIds, type));
            }
        }
        // else: error management: dataset (datasetId) not found !
    }

    public void renameCohort(String datasetId, String oldName, String newName) {
        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata != null) {
            for (Cohort cohort : variantDatasetMetadata.getCohorts()) {
                if (oldName.equals(cohort.getId())) {
                    cohort.setId(newName);
                    return;
                }
            }
            // error management: cohort not found !
        }
        // else: error management: dataset (datasetId) not found !
    }

    public void renameDataset(String oldName, String newName) {
        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(oldName);
        if (variantDatasetMetadata != null) {
            variantDatasetMetadata.setId(newName);
        }
        // else: error management: dataset (old name) not found !
    }

    public VariantMetadata loadPedigree(Pedigree pedigree, String datasetId) {
        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata != null) {
            org.opencb.biodata.models.metadata.Individual dest;
            List<org.opencb.biodata.models.metadata.Individual> individuals = new ArrayList<>();
            for (Individual src: pedigree.getIndividuals().values()) {
                dest = new org.opencb.biodata.models.metadata.Individual();
                dest.setId(src.getId());
                dest.setFamily(src.getFamily());
                dest.setFather(src.getFather() != null ? src.getFather().getId() : null);
                dest.setMother(src.getMother() != null ? src.getMother().getId() : null);
                dest.setSex(src.getSex().toString());
                dest.setPhenotype(src.getPhenotype().toString());
                if (src.getVariables() != null && src.getVariables().size() > 0) {
                    Sample sample = new Sample();
                    sample.setId(dest.getId());
                    Map<String, String> annotation = new HashMap<>();
                    for (String key: src.getVariables().keySet()) {
                        if (pedigree.getVariables().get(key) != null) {
                            VariableField.VariableType type = pedigree.getVariables().get(key).getType();
                            if (type == VariableField.VariableType.INTEGER) {
                                annotation.put(key + ":i", src.getVariables().get(key).toString());
                            } else if (type == VariableField.VariableType.DOUBLE) {
                                annotation.put(key + ":d", src.getVariables().get(key).toString());
                            } else if (type == VariableField.VariableType.BOOLEAN) {
                                annotation.put(key + ":b", src.getVariables().get(key).toString());
                            } else {
                                annotation.put(key + ":s", src.getVariables().get(key).toString());
                            }
                        }
                    }
                    sample.setAnnotations(annotation);
                    dest.setSamples(Collections.singletonList(sample));
                }

                individuals.add(dest);
            }
            if (individuals.size() > 0) {
                variantDatasetMetadata.setIndividuals(individuals);
            }
        }
        return variantMetadata;
    }

    public Pedigree getPedigree(String datasetId) {
        Pedigree pedigree = null;

        Individual dest;
        Map<String, Individual> individualMap = new HashMap<>();

        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata != null) {

            // first loop
            for (org.opencb.biodata.models.metadata.Individual src: variantDatasetMetadata.getIndividuals()) {
                // main fields
                dest = new Individual()
                        .setId(src.getId())
                        .setFamily(src.getFamily())
                        .setSex(src.getSex())
                        .setPhenotype(src.getPhenotype());
                // attributes
                if (src.getSamples() != null && src.getSamples().size() > 0) {
                    Map<String, String> annotation = src.getSamples().get(0).getAnnotations();
                    if (annotation != null) {
                        Map<String, Object> variables = new HashMap<>();
                        for (String key: annotation.keySet()) {
                            String fields[] = key.split(":");
                            if (fields.length > 1) {
                                switch (fields[1].toLowerCase()) {
                                    case "i":
                                        variables.put(fields[0], Integer.parseInt(annotation.get(key)));
                                        break;
                                    case "d":
                                        variables.put(fields[0], Double.parseDouble(annotation.get(key)));
                                        break;
                                    case "b":
                                        variables.put(fields[0], Boolean.parseBoolean(annotation.get(key)));
                                        break;
                                    default:
                                        variables.put(fields[0], annotation.get(key));
                                }
                            } else {
                                variables.put(fields[0], annotation.get(key));
                            }
                        }
                        dest.setVariables(variables);
                    }
                }
                individualMap.put(Pedigree.key(dest), dest);
            }

            // second loop: setting fathers, mothers, partners and children
            for (org.opencb.biodata.models.metadata.Individual src: variantDatasetMetadata.getIndividuals()) {
                // update father, mother and child
                Pedigree.updateIndividuals(individualMap.get(Pedigree.key(src.getFamily(), src.getFather())),
                        individualMap.get(Pedigree.key(src.getFamily(), src.getMother())),
                        individualMap.get(Pedigree.key(src.getFamily(), src.getId())));
            }

            pedigree = new Pedigree(individualMap);
        }

        return pedigree;
    }


    public void print() throws IOException {
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(variantMetadata));
    }

    public void printSummary() {
        StringBuilder res = new StringBuilder();
        res.append("Num. datasets: ").append(variantMetadata.getDatasets().size()).append("\n");
        int counter, datasetCounter = 0;
        for (VariantDatasetMetadata dataset : variantMetadata.getDatasets()) {
            datasetCounter++;
            res.append("\tDataset #").append(datasetCounter).append(": ").append(dataset.getId()).append("\n");

            res.append("\tNum. files: ").append(dataset.getFiles().size()).append("\n");
            counter = 0;
            for (VariantFileMetadata file: dataset.getFiles()) {
                counter++;
                res.append("\t\tFile #").append(counter).append(": ").append(file.getId());
                res.append(" (").append(file.getSampleIds().size()).append(" samples)\n");
            }

            res.append("\tNum. cohorts: ").append(dataset.getCohorts().size()).append("\n");
            counter = 0;
            for (Cohort cohort: dataset.getCohorts()) {
                counter++;
                res.append("\t\tCohort #").append(counter).append(": ").append(cohort.getId());
                res.append(" (").append(cohort.getSampleIds().size()).append(" samples)\n");
            }
        }
        System.out.println(res.toString());
    }


    public void save(Path filename) throws IOException {
       save(filename, false);
    }

    public void save(Path filename, boolean pretty) throws IOException {
        if (filename == null || Files.exists(filename)) {
            throw new IOException("File path not correct, either it is null or file already exists: " + filename);
        }

        String text;
        if (pretty) {
            text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(variantMetadata);
        } else {
            text = mapper.writeValueAsString(variantMetadata);
        }

        PrintWriter writer = new PrintWriter(new FileOutputStream(filename.toFile()));
        writer.write(text);
        writer.close();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariantMetadataManager{");
        sb.append("variantMetadata=").append(variantMetadata);
        sb.append(", mapper=").append(mapper);
        sb.append('}');
        return sb.toString();
    }

    public VariantMetadata getVariantMetadata() {
        return variantMetadata;
    }

    public VariantMetadataManager setVariantMetadata(VariantMetadata variantMetadata) {
        this.variantMetadata = variantMetadata;
        return this;
    }

}
