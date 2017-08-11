/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.biodata.models.core.pedigree.VariableField;
import org.opencb.biodata.models.metadata.Cohort;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.metadata.Species;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joaquin on 9/26/16.
 */
public class VariantMetadataManager {

    private VariantMetadata variantMetadata;

    private ObjectMapper mapper;
    private Logger logger;

    private static final Pattern OPERATION_PATTERN = Pattern.compile("(<=?|>=?|!=|!?=?~|==?)([^=<>~!]+.*)$");

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

    /**
     * Load variant metadata file.
     *
     * @param path          Path to the variant metadata file
     * @throws IOException  IOException
     */
    public void load(Path path) throws IOException {
        FileUtils.checkPath(path);
        logger.debug("Loading variant metadata from '{}'", path.toAbsolutePath().toString());
        variantMetadata = mapper.readValue(path.toFile(), VariantMetadata.class);

        // We need to add Individual info fields to their sample annotations to allow more complex queries
        for (VariantDatasetMetadata variantDatasetMetadata : variantMetadata.getDatasets()) {
            for (org.opencb.biodata.models.metadata.Individual individual : variantDatasetMetadata.getIndividuals()) {
                for (Sample sample : individual.getSamples()) {
                    sample.getAnnotations().put("individual.id", individual.getId());
                    sample.getAnnotations().put("individual.family", individual.getFamily());
                    sample.getAnnotations().put("individual.father", individual.getFather());
                    sample.getAnnotations().put("individual.mother", individual.getMother());
                    sample.getAnnotations().put("individual.sex", individual.getSex());
                    sample.getAnnotations().put("individual.phenotype", individual.getPhenotype());
                }
            }
        }
    }


    /**
     * Retrieve the variant dataset metadata from its dataset ID.
     *
     * @param datasetId Dataset ID
     * @return          VariantDatasetMetadata object
     */
    public VariantDatasetMetadata getVariantDatasetMetadata(String datasetId) {
        if (datasetId != null) {
            for (VariantDatasetMetadata dataset : variantMetadata.getDatasets()) {
                if (datasetId.equals(dataset.getId())) {
                    return dataset;
                }
            }
        } else {
            logger.error("Dataset ID is null");
        }
        return null;
    }

    /**
     * Add a variant dataset metadata. Dataset ID must not exist.
     *
     * @param variantDatasetMetadata    Variant dataset metadata to add
     */
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

    /**
     * Add a variant file metadata to a given variant dataset metadata (from dataset ID).
     *
     * @param fileMetadata  Variant file metadata to add
     * @param datasetId     Dataset ID
     */
    public void addFile(VariantFileMetadata fileMetadata, String datasetId) {
        // Sanity check
        if (fileMetadata == null || StringUtils.isEmpty(fileMetadata.getId())) {
            logger.error("Variant file metadata (or its ID) is null or empty.");
            return;
        }

        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata == null) {
            logger.error("Dataset not found. Check your dataset ID: '{}'", datasetId);
            return;
        }
        if (variantDatasetMetadata.getFiles() == null) {
            variantDatasetMetadata.setCohorts(new ArrayList<>());
        }
        for (VariantFileMetadata file: variantDatasetMetadata.getFiles()) {
            if (file.getId() != null && file.getId().equals(fileMetadata.getId())) {
                logger.error("Variant file metadata with id '{}' already exists in dataset '{}'", fileMetadata.getId(),
                        datasetId);
                return;
            }
        }
        variantDatasetMetadata.getFiles().add(fileMetadata);
    }

    /**
     * Add an individual to a given variant dataset metadata (from dataset ID).
     *
     * @param individual  Individual to add
     * @param datasetId   Dataset ID
     */
    public void addIndividual(org.opencb.biodata.models.metadata.Individual individual, String datasetId) {
        // Sanity check
        if (individual == null || StringUtils.isEmpty(individual.getId())) {
            logger.error("Individual (or its ID) is null or empty.");
            return;
        }

        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata == null) {
            logger.error("Dataset not found. Check your dataset ID: '{}'", datasetId);
            return;
        }
        if (variantDatasetMetadata.getIndividuals() == null) {
            variantDatasetMetadata.setIndividuals(new ArrayList<>());
        }
        for (org.opencb.biodata.models.metadata.Individual indi: variantDatasetMetadata.getIndividuals()) {
            if (indi.getId() != null && indi.getId().equals(individual.getId())) {
                logger.error("Individual with id '{}' already exists in dataset '{}'", individual.getId(),
                        datasetId);
                return;
            }
        }
        variantDatasetMetadata.getIndividuals().add(individual);
    }

    /**
     * Add a cohort to a given variant dataset metadata (from dataset ID).
     *
     * @param cohort    Cohort to add
     * @param datasetId Dataset ID
     */
    public void addCohort(Cohort cohort, String datasetId) {
        // Sanity check
        if (cohort == null || StringUtils.isEmpty(cohort.getId())) {
            logger.error("Cohort (or its ID) is null or empty.");
            return;
        }

        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata == null) {
            logger.error("Dataset not found. Check your dataset ID: '{}'", datasetId);
            return;
        }
        if (variantDatasetMetadata.getCohorts() == null) {
            variantDatasetMetadata.setCohorts(new ArrayList<>());
        }
        for (Cohort coho: variantDatasetMetadata.getCohorts()) {
            if (coho.getId() != null && coho.getId().equals(cohort.getId())) {
                logger.error("Cohort with id '{}' already exists in dataset '{}'", cohort.getId(),
                        datasetId);
                return;
            }
        }
        variantDatasetMetadata.getCohorts().add(cohort);
    }

    /**
     * Retrieve all samples for a given dataset (from its dataset ID).
     * For each sample, add a new annotation: INDIVIDUAL_ID.
     *
     * @param datasetId Dataset ID
     * @return          Sample list
     */
    public List<Sample> getSamples(String datasetId) {
        VariantDatasetMetadata variantDatasetMetadata = getVariantDatasetMetadata(datasetId);
        if (variantDatasetMetadata == null) {
            logger.error("Dataset not found. Check your dataset ID: '{}'", datasetId);
            return null;
        }

        List<Sample> samples = new ArrayList<>();
        for (org.opencb.biodata.models.metadata.Individual individual: variantDatasetMetadata.getIndividuals()) {
            for (Sample sample : individual.getSamples()) {
                if (sample.getAnnotations() == null) {
                    sample.setAnnotations(new HashMap<>());
                }
                sample.getAnnotations().put("INDIVIDUAL_ID", individual.getId());
                samples.add(sample);
            }
        }
        return samples;
    }

    public List<Sample> getSamples(Query query, String datasetId) {
        List<Sample> sampleResult = new ArrayList<>();

        List<Sample> samples = getSamples(datasetId);
        List<Predicate<Sample>> predicates = parseSampleQuery(query);
        boolean passFilter;
        for (Sample sample : samples) {
            passFilter = true;
            for (Predicate<Sample> predicate : predicates) {
                if (!predicate.test(sample)) {
                    passFilter = false;
                    break;
                }
            }

            if (passFilter) {
                sampleResult.add(sample);
            }
        }

        return sampleResult;
    }

    /*
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
*/

    /**
     * Load pedrigree into a given dataset (from its dataset ID).
     *
     * @param pedigree      Pedigree to load
     * @param datasetId     Dataset ID related to that pedigree
     * @return              Variant metadata object
     */
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

    /**
     * Retrieve the pedigree related to the input dataset ID.
     *
     * @param datasetId     Dataset ID
     * @return              Pedigree object
     */
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

    /**
     * Print to the standard output the variant metadata manager in pretty JSON format.
     *
     * @throws IOException  IOException
     */
    public void print() throws IOException {
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(variantMetadata));
    }

    /**
     * Print to the standard output a summary of the variant metadata manager.
     *
     * @throws IOException  IOException
     */
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

    /**
     * Save variant metadata manager in JSON format into the given filename.
     *
     * @param filename      Filename where to store the metadata manager
     * @throws IOException  IOException
     */
    public void save(Path filename) throws IOException {
       save(filename, false);
    }

    /**
     * Save variant metadata manager in JSON format into the given filename.
     *
     * @param filename      Filename where to store the metadata manager
     * @param pretty        Flag to print pretty JSON
     * @throws IOException  IOException
     */
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

    protected List<Predicate<Sample>> parseSampleQuery(Query query) {
        List<Predicate<Sample>> filters = new ArrayList<>();

        Iterator<String> iterator = query.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = query.getString(key);

            Matcher matcher = OPERATION_PATTERN.matcher(value);
            if (matcher.matches()) {
                String comparator = matcher.group(1);
                String queryValue = matcher.group(2);

                switch (comparator) {
                    case "=":
                    case "==":
                    case "!=":
                        filters.add(sample -> {
                            String s = sample.getAnnotations().getOrDefault(key, "");

                            // TODO think about this
//                            if (s.equals("")) {
//                                return true;
//                            }

                            try {
                                if (!comparator.equals("!=")) {
                                    return Double.parseDouble(s) == Double.parseDouble(queryValue);
                                } else {
                                    return Double.parseDouble(s) != Double.parseDouble(queryValue);
                                }
                            } catch (NumberFormatException e) {
                                if (!comparator.equals("!=")) {
                                    return s.equals(queryValue);
                                } else {
                                    return !s.equals(queryValue);
                                }
                            }
                        });
                        break;
                    case "<":
                        filters.add(sample -> {
                            try {
                                String s = sample.getAnnotations().getOrDefault(key, "");
                                return Double.parseDouble(s) < Double.parseDouble(queryValue);
                            } catch (NumberFormatException e) {
                               return false;
                            }
                        });
                        break;
                    case "<=":
                        filters.add(sample -> {
                            try {
                                String s = sample.getAnnotations().getOrDefault(key, "");
                                return Double.parseDouble(s) <= Double.parseDouble(queryValue);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        });
                        break;
                    case ">":
                        filters.add(sample -> {
                            try {
                                String s = sample.getAnnotations().getOrDefault(key, "");
                                return Double.parseDouble(s) > Double.parseDouble(queryValue);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        });
                        break;
                    case ">=":
                        filters.add(sample -> {
                            try {
                                String s = sample.getAnnotations().getOrDefault(key, "");
                                return Double.parseDouble(s) >= Double.parseDouble(queryValue);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        });
                        break;
                    case "~=":
                        filters.add(sample -> {
                            try {
                                String s = sample.getAnnotations().getOrDefault(key, "").trim();
                                return queryValue.contains(s);
                            } catch (Exception e) {
                                return false;
                            }
                        });
                        break;
                    default:
                        break;
                }

            }
        }

        return filters;
    }
}
