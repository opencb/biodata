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

package org.opencb.biodata.tools.variant.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Multiples;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.biodata.models.metadata.Cohort;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.metadata.Species;
import org.opencb.biodata.models.variant.metadata.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.converters.avro.VCFHeaderToVariantFileHeaderConverter;
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
    private static final String INDIVIDUAL_ID = "individual.id";
    private static final String INDIVIDUAL_FAMILY = "individual.family";
    private static final String INDIVIDUAL_FATHER = "individual.father";
    private static final String INDIVIDUAL_MOTHER = "individual.mother";
    private static final String INDIVIDUAL_SEX = "individual.sex";
    private static final String INDIVIDUAL_PHENOTYPE = "individual.phenotype";

    public VariantMetadataManager() {
        this(new Species("hsapiens", "Homo sapiens", "", null, "GRCh38"), "");
    }

    public VariantMetadataManager(Species species, String description) {
        variantMetadata = new VariantMetadata();

        variantMetadata.setCreationDate(LocalDateTime.now().toString());
        variantMetadata.setSpecies(species);
        variantMetadata.setDescription(description);

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
        for (VariantStudyMetadata variantStudyMetadata: variantMetadata.getStudies()) {
            if (variantStudyMetadata.getIndividuals() != null) {
                for (org.opencb.biodata.models.metadata.Individual individual : variantStudyMetadata.getIndividuals()) {
                    for (Sample sample : individual.getSamples()) {
                        sample.getAnnotations().put(INDIVIDUAL_ID, individual.getId());
                        sample.getAnnotations().put(INDIVIDUAL_FAMILY, individual.getFamily());
                        sample.getAnnotations().put(INDIVIDUAL_FATHER, individual.getFather());
                        sample.getAnnotations().put(INDIVIDUAL_MOTHER, individual.getMother());
                        sample.getAnnotations().put(INDIVIDUAL_SEX, individual.getSex());
                        sample.getAnnotations().put(INDIVIDUAL_PHENOTYPE, individual.getPhenotype());
                    }
                }
            }
        }
    }


    /**
     * Retrieve the variant study metadata from its study ID.
     *
     * @param studyId Study ID
     * @return        VariantStudyMetadata object
     */
    public VariantStudyMetadata getVariantStudyMetadata(String studyId) {
        if (studyId != null) {
            if (variantMetadata.getStudies() == null) {
                variantMetadata.setStudies(new ArrayList<>());
            }
            for (VariantStudyMetadata study : variantMetadata.getStudies()) {
                if (studyId.equals(study.getId())) {
                    return study;
                }
            }
        } else {
            logger.error("Study ID is null");
        }
        return null;
    }

    /**
     * Add a variant study metadata. Study ID must not exist.
     *
     * @param variantStudyMetadata    Variant study metadata to add
     */
    public void addVariantDatasetMetadata(VariantStudyMetadata variantStudyMetadata) {
        if (variantStudyMetadata != null) {
            VariantStudyMetadata found = getVariantStudyMetadata(variantStudyMetadata.getId());
            // if there is not any study with that ID then we add the new one
            // TODO we need to think what to do when it exists, should we throw an exception?
            if (found == null) {
                if (variantMetadata.getStudies() == null) {
                    variantMetadata.setStudies(new ArrayList<>());
                }
                variantMetadata.getStudies().add(variantStudyMetadata);
            } else {
                logger.error("Study ID already exists");
            }
        }
    }

    /**
     * Remove a variant study metadata (from study ID).
     *
     * @param studyId     Study ID
     */
    public void removeVariantStudyMetadata(String studyId) {
        // Sanity check
        if (StringUtils.isEmpty(studyId)) {
            logger.error("Variant study metadata ID {} is null or empty.", studyId);
            return;
        }
        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Dataset not found. Check your study ID: '{}'", studyId);
            return;
        }
        for (int i = 0; i < variantMetadata.getStudies().size(); i++) {
            if (studyId.equals(variantMetadata.getStudies().get(i).getId())) {
                variantMetadata.getStudies().remove(i);
                return;
            }
        }
    }

    /**
     * Add a variant file metadata to a given variant study metadata (from study ID).
     *
     * @param fileMetadata  Variant file metadata to add
     * @param studyId       Study ID
     */
    public void addFile(VariantFileMetadata fileMetadata, String studyId) {
        // Sanity check
        if (fileMetadata == null || StringUtils.isEmpty(fileMetadata.getId())) {
            logger.error("Variant file metadata (or its ID) is null or empty.");
            return;
        }

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return;
        }
        if (variantStudyMetadata.getFiles() == null) {
            variantStudyMetadata.setFiles(new ArrayList<>());
        }
        for (VariantFileMetadata file: variantStudyMetadata.getFiles()) {
            if (file.getId() != null && file.getId().equals(fileMetadata.getId())) {
                logger.error("Variant file metadata with id '{}' already exists in study '{}'", fileMetadata.getId(),
                        studyId);
                return;
            }
        }
        // individual management
        if (variantStudyMetadata.getIndividuals() == null) {
            variantStudyMetadata.setIndividuals(new ArrayList<>());
        }
        if (!variantStudyMetadata.getIndividuals().isEmpty()) {
            // check if samples are already in study
            for (String sampleId: fileMetadata.getSampleIds()) {
                for (org.opencb.biodata.models.metadata.Individual individual: variantStudyMetadata.getIndividuals()) {
                    for (Sample sample: individual.getSamples()) {
                        if (sampleId.equals(sample.getId())) {
                            logger.error("Sample '{}' from file {} already exists in study '{}'",
                                    sampleId, fileMetadata.getId(), studyId);
                            return;
                        }
                    }
                }
            }
        }
        // by default, create individuals from sample, and individual ID takes the sample ID
        // TODO: manage multiple samples per individual
        for (String sampleId: fileMetadata.getSampleIds()) {
            List<Sample> samples = new ArrayList<>();
            Sample sample = new Sample();
            sample.setId(sampleId);
            sample.setAnnotations(new HashMap<>());
            samples.add(sample);

            org.opencb.biodata.models.metadata.Individual individual = new org.opencb.biodata.models.metadata.Individual();
            individual.setId(sampleId);
            individual.setSamples(samples);

            variantStudyMetadata.getIndividuals().add(individual);
        }


        variantStudyMetadata.getFiles().add(fileMetadata);
    }

    /**
     * Add a variant file metadata (from VCF file and header) to a given variant study metadata (from study ID).
     *
     * @param filename      VCF filename (as an ID)
     * @param vcfHeader     VCF header
     * @param studyId       Study ID
     */
    public void addFile(String filename, VCFHeader vcfHeader, String studyId) {
        // sanity check
        if (StringUtils.isEmpty(filename)) {
            logger.error("VCF filename is empty or null: '{}'", filename);
            return;
        }
        if (vcfHeader == null) {
            logger.error("VCF header is missingDataset not found. Check your study ID: '{}'", studyId);
            return;
        }

        VCFHeaderToVariantFileHeaderConverter headerConverter = new VCFHeaderToVariantFileHeaderConverter();
        VariantFileMetadata variantFileMetadata = new VariantFileMetadata();
        variantFileMetadata.setId(filename);
        variantFileMetadata.setSampleIds(vcfHeader.getSampleNamesInOrder());
        variantFileMetadata.setHeader(headerConverter.convert(vcfHeader));
        addFile(variantFileMetadata, studyId);
    }

    /**
     * Remove a variant file metadata of a given variant study metadata (from study ID).
     *
     * @param file      File
     * @param studyId   Study ID
     */
    public void removeFile(VariantFileMetadata file, String studyId) {
        // Sanity check
        if (file == null) {
            logger.error("Variant file metadata is null.");
            return;
        }
        removeFile(file.getId(), studyId);
    }

    /**
     * Remove a variant file metadata (from file ID) of a given variant study metadata (from study ID).
     *
     * @param fileId      File ID
     * @param studyId     Study ID
     */
    public void removeFile(String fileId, String studyId) {
        // Sanity check
        if (StringUtils.isEmpty(fileId)) {
            logger.error("Variant file metadata ID {} is null or empty.", fileId);
            return;
        }

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return;
        }
        if (variantStudyMetadata.getFiles() != null) {
            for (int i = 0; i < variantStudyMetadata.getFiles().size(); i++) {
                if (fileId.equals(variantStudyMetadata.getFiles().get(i).getId())) {
                    variantStudyMetadata.getFiles().remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Add an individual to a given variant study metadata (from study ID).
     *
     * @param individual  Individual to add
     * @param studyId   Study ID
     */
    public void addIndividual(org.opencb.biodata.models.metadata.Individual individual, String studyId) {
        // Sanity check
        if (individual == null || StringUtils.isEmpty(individual.getId())) {
            logger.error("Individual (or its ID) is null or empty.");
            return;
        }

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return;
        }
        if (variantStudyMetadata.getIndividuals() == null) {
            variantStudyMetadata.setIndividuals(new ArrayList<>());
        }
        for (org.opencb.biodata.models.metadata.Individual indi: variantStudyMetadata.getIndividuals()) {
            if (indi.getId() != null && indi.getId().equals(individual.getId())) {
                logger.error("Individual with id '{}' already exists in study '{}'", individual.getId(),
                        studyId);
                return;
            }
        }
        variantStudyMetadata.getIndividuals().add(individual);
    }

    /**
     * Remove an individual of a given variant study metadata (from study ID).
     *
     * @param individual Individual
     * @param studyId    Study ID
     */
    public void removeIndividual(Individual individual, String studyId) {
        // Sanity check
        if (individual == null) {
            logger.error("Individual is null.");
            return;
        }
        removeIndividual(individual.getName(), studyId);
    }

    /**
     * Remove an individual (from individual ID) of a given variant study metadata (from study ID).
     *
     * @param individualId  Individual ID
     * @param studyId       Study ID
     */
    public void removeIndividual(String individualId, String studyId) {
        // Sanity check
        if (StringUtils.isEmpty(individualId)) {
            logger.error("Individual ID {} is null or empty.", individualId);
            return;
        }

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return;
        }
        if (variantStudyMetadata.getIndividuals() != null) {
            for (int i = 0; i < variantStudyMetadata.getIndividuals().size(); i++) {
                if (individualId.equals(variantStudyMetadata.getIndividuals().get(i).getId())) {
                    variantStudyMetadata.getIndividuals().remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Add a cohort to a given variant study metadata (from study ID).
     *
     * @param cohort    Cohort to add
     * @param studyId   Study ID
     */
    public void addCohort(Cohort cohort, String studyId) {
        // Sanity check
        if (cohort == null || StringUtils.isEmpty(cohort.getId())) {
            logger.error("Cohort (or its ID) is null or empty.");
            return;
        }

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return;
        }
        if (variantStudyMetadata.getCohorts() == null) {
            variantStudyMetadata.setCohorts(new ArrayList<>());
        }
        for (Cohort coho: variantStudyMetadata.getCohorts()) {
            if (coho.getId() != null && coho.getId().equals(cohort.getId())) {
                logger.error("Cohort with id '{}' already exists in study '{}'", cohort.getId(),
                        studyId);
                return;
            }
        }
        variantStudyMetadata.getCohorts().add(cohort);
    }

    /**
     * Remove a cohort of a given variant study metadata (from study ID).
     *
     * @param cohort     Cohort
     * @param studyId  Study ID
     */
    public void removeCohort(Cohort cohort, String studyId) {
        // Sanity check
        if (cohort == null) {
            logger.error("Cohort is null.");
            return;
        }
        removeCohort(cohort.getId(), studyId);
    }

    /**
     * Remove a cohort (from cohort ID) of a given variant study metadata (from study ID).
     *
     * @param cohortId   Cohort ID
     * @param studyId    Study ID
     */
    public void removeCohort(String cohortId, String studyId) {
        // Sanity check
        if (StringUtils.isEmpty(cohortId)) {
            logger.error("Cohort ID {} is null or empty.", cohortId);
            return;
        }

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return;
        }
        if (variantStudyMetadata.getCohorts() != null) {
            for (int i = 0; i < variantStudyMetadata.getCohorts().size(); i++) {
                if (cohortId.equals(variantStudyMetadata.getCohorts().get(i).getId())) {
                    variantStudyMetadata.getCohorts().remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Retrieve all samples for a given study (from its study ID).
     *
     * @param studyId   Study ID
     * @return          Sample list
     */
    public List<Sample> getSamples(String studyId) {
        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata == null) {
            logger.error("Study not found. Check your study ID: '{}'", studyId);
            return null;
        }

        List<Sample> samples = new ArrayList<>();
        if (variantStudyMetadata.getIndividuals() != null) {
            for (org.opencb.biodata.models.metadata.Individual individual : variantStudyMetadata.getIndividuals()) {
                for (Sample sample : individual.getSamples()) {
                    if (sample.getAnnotations() == null) {
                        sample.setAnnotations(new HashMap<>());
                    }
                    samples.add(sample);
                }
            }
        }
        return samples;
    }

    public List<Sample> getSamples(Query query, String studyId) {
        List<Sample> sampleResult = new ArrayList<>();

        List<Sample> samples = getSamples(studyId);
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

    /**
     * Load a list of pedrigree objects into a given study (from its study ID).
     *
     * @param pedigrees     List of Pedigree objects to load
     * @param studyId       Study ID related to that pedigrees
     * @return              Variant metadata object
     */
    public VariantMetadata loadPedigree(List<Pedigree> pedigrees, String studyId) {
        VariantMetadata variantMetadata = null;
        for(Pedigree pedigree: pedigrees) {
            variantMetadata = loadPedigree(pedigree, studyId);
        }
        return variantMetadata;
    }

    /**
     * Load pedrigree into a given study (from its study ID).
     *
     * @param pedigree      Pedigree to load
     * @param studyId       Study ID related to that pedigree
     * @return              Variant metadata object
     */
    public VariantMetadata loadPedigree(Pedigree pedigree, String studyId) {
        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata != null) {
            boolean found;
            org.opencb.biodata.models.metadata.Individual dest = null;
            for (Individual src: pedigree.getMembers()) {
                found = false;
                for (int i = 0; i < variantStudyMetadata.getIndividuals().size(); i++) {
                    dest = variantStudyMetadata.getIndividuals().get(i);
                    if (dest.getId().equals(src.getName())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    dest.setFamily(pedigree.getName());
                    dest.setFather(src.getFather() != null ? src.getFather().getName() : null);
                    dest.setMother(src.getMother() != null ? src.getMother().getName() : null);
                    dest.setSex(src.getSex().toString());
                    dest.setPhenotype(src.getAffectionStatus().toString());
                    if (src.getAttributes() != null && src.getAttributes().size() > 0) {
                        found = false;
                        Sample sample = null;
                        // sanity check
                        if (dest.getSamples() == null) {
                            logger.warn("Loading pedigree, individual {} without samples: it will be added.", dest.getId());
                            dest.setSamples(new ArrayList<>());
                        }
                        for (int i = 0; i < dest.getSamples().size(); i++) {
                            sample = dest.getSamples().get(i);
                            if (sample.getId().equals(dest.getId())) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            // sample found, add new attributes
                            if (sample.getAnnotations() == null) {
                                sample.setAnnotations(new HashMap<>());
                            }
                        } else {
                            // sample not found, add as a new one
                            sample = new Sample();
                            sample.setId(dest.getId());
                            sample.setAnnotations(new HashMap<>());
                        }
                        // Default annotation (attributes from Individual)
                        sample.getAnnotations().put(INDIVIDUAL_ID, src.getName());
                        sample.getAnnotations().put(INDIVIDUAL_FAMILY, pedigree.getName());
                        if (src.getFather() != null) {
                            sample.getAnnotations().put(INDIVIDUAL_FATHER, src.getFather().getName());
                        }
                        if (src.getMother() != null) {
                            sample.getAnnotations().put(INDIVIDUAL_MOTHER, src.getMother().getName());
                        }
                        if (src.getSex() != null) {
                            sample.getAnnotations().put(INDIVIDUAL_SEX, src.getSex().toString());
                        }
                        if (src.getAffectionStatus() != null) {
                            sample.getAnnotations().put(INDIVIDUAL_PHENOTYPE, src.getAffectionStatus().toString());
                        }
                        // Custom annotation
                        for (String key: src.getAttributes().keySet()) {
                            if (pedigree.getAttributes().get(key) != null) {
                                sample.getAnnotations().put(key, src.getAttributes().get(key).toString());
                            }
                        }
                        if (!found) {
                            dest.getSamples().add(sample);
                        }
                    }
                } else {
                    logger.warn("Loading pedigree, individual {} not found in metadata file, it will not be added.", src.getId());
                }
            }
        } else {
            logger.warn("Loading pedigree, nothing to do because study ID '{}' does not exist.", studyId);
        }
        return variantMetadata;
    }

    /**
     * Retrieve the pedigree objects related to the input study ID.
     *
     * @param studyId     Study ID
     * @return            List of Pedigree objects
     */
    public List<Pedigree> getPedigree(String studyId) {
        Individual dest;
        Map<String, Pedigree> pedigreeMap = new HashMap<>();
        Map<String, Individual> individualMap = new HashMap<>();

        VariantStudyMetadata variantStudyMetadata = getVariantStudyMetadata(studyId);
        if (variantStudyMetadata != null) {

            // first loop
            for (org.opencb.biodata.models.metadata.Individual src: variantStudyMetadata.getIndividuals()) {

                String pedigreeName = src.getFamily();
                if (!pedigreeMap.containsKey(pedigreeName)) {
                    pedigreeMap.put(pedigreeName, new Pedigree(pedigreeName, new ArrayList<>(), new HashMap<>()));
                }

                // main fields
                dest = new Individual(src.getId(), Individual.Sex.getEnum(src.getSex()),
                        Individual.AffectionStatus.getEnum(src.getPhenotype()));

                // attributes
                if (src.getSamples() != null && src.getSamples().size() > 0) {
                    Map<String, String> annotation = src.getSamples().get(0).getAnnotations();
                    if (annotation != null) {
                        Map<String, Object> variables = new HashMap<>();
                        for (String key: annotation.keySet()) {
                            if (key.equals(INDIVIDUAL_ID) || key.equals(INDIVIDUAL_FAMILY)
                                    || key.equals(INDIVIDUAL_FATHER) || key.equals(INDIVIDUAL_MOTHER)
                                    || key.equals(INDIVIDUAL_SEX) || key.equals(INDIVIDUAL_PHENOTYPE)) {
                                continue;
                            }
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
                        dest.setAttributes(variables);
                    }
                }
                pedigreeMap.get(pedigreeName).getMembers().add(dest);
                individualMap.put(pedigreeName + "_" + dest.getName(), dest);
            }

            // second loop: setting fathers, mothers, partners and children
            for (org.opencb.biodata.models.metadata.Individual src: variantStudyMetadata.getIndividuals()) {
                // update father, mother and child
                Individual father = individualMap.get(src.getFamily() + "_" + src.getFather());
                Individual mother = individualMap.get(src.getFamily() + "_" + src.getMother());
                Individual child = individualMap.get(src.getFamily() + "_" + src.getId());

                // setting father and children
                if (father != null) {
                    child.setFather(father);
                    if (father.getMultiples() == null) {
                        Multiples multiples = new Multiples().setType("children").setSiblings(new ArrayList<>());
                        father.setMultiples(multiples);
                    }
                    father.getMultiples().getSiblings().add(child.getName());
                }

                // setting mother and children
                if (mother != null) {
                    child.setMother(mother);
                    if (mother.getMultiples() == null) {
                        Multiples multiples = new Multiples().setType("children").setSiblings(new ArrayList<>());
                        mother.setMultiples(multiples);
                    }
                    mother.getMultiples().getSiblings().add(child.getName());
                }
            }

        }

        // create the list of Pedigree objects from the map
        return new ArrayList<>(pedigreeMap.values());
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
        res.append("Num. studies: ").append(variantMetadata.getStudies().size()).append("\n");
        int counter, studyCounter = 0;
        for (VariantStudyMetadata study: variantMetadata.getStudies()) {
            studyCounter++;
            res.append("\tStudy #").append(studyCounter).append(": ").append(study.getId()).append("\n");

            res.append("\tNum. files: ").append(study.getFiles().size()).append("\n");
            counter = 0;
            for (VariantFileMetadata file: study.getFiles()) {
                counter++;
                res.append("\t\tFile #").append(counter).append(": ").append(file.getId());
                res.append(" (").append(file.getSampleIds().size()).append(" samples)\n");
            }

            res.append("\tNum. cohorts: ").append(study.getCohorts().size()).append("\n");
            counter = 0;
            for (Cohort cohort: study.getCohorts()) {
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
