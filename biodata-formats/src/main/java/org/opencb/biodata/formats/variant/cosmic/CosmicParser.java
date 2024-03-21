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

package org.opencb.biodata.formats.variant.cosmic;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.formats.variant.VariantAnnotationUtils;
import org.opencb.biodata.models.sequence.SequenceLocation;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CosmicParser {

    private static final int GENE_NAMES_COLUMN = 0;
    private static final int HGNC_COLUMN = 3;
    private static final int PRIMARY_SITE_COLUMN = 7;
    private static final int SITE_SUBTYPE_COLUMN = 8;
    private static final int PRIMARY_HISTOLOGY_COLUMN = 11;
    private static final int HISTOLOGY_SUBTYPE_COLUMN = 12;
    private static final int ID_COLUMN = 16;
    private static final int COSM_ID_COLUMN = 17;
    private static final int HGVS_COLUMN = 19;
    private static final int MUTATION_DESCRIPTION_COLUMN = 21;
    private static final int MUTATION_ZYGOSITY_COLUMN = 22;
    private static final int FATHMM_PREDICTION_COLUMN = 29;
    private static final int FATHMM_SCORE_COLUMN = 30;
    private static final int MUTATION_SOMATIC_STATUS_COLUMN = 31;
    private static final int PUBMED_PMID_COLUMN = 32;
    private static final int SAMPLE_SOURCE_COLUMN = 34;
    private static final int TUMOUR_ORIGIN_COLUMN = 35;

    private static final String SYMBOL = "symbol";

    private static final String HGVS_INSERTION_TAG = "ins";
    private static final String HGVS_SNV_CHANGE_SYMBOL = ">";
    private static final String HGVS_DELETION_TAG = "del";
    private static final String HGVS_DUPLICATION_TAG = "dup";
    private static final String CHROMOSOME = "CHR";
    private static final String START = "START";
    private static final String END = "END";
    private static final String REF = "REF";
    private static final String ALT = "ALT";

    private static final String VARIANT_STRING_PATTERN = "[ACGT]*";

    private static final Pattern mutationGRCh37GenomePositionPattern = Pattern.compile("(?<" + CHROMOSOME + ">\\S+):(?<" + START + ">\\d+)-(?<" + END + ">\\d+)");
    private static final Pattern snvPattern = Pattern.compile("c\\.\\d+((\\+|\\-|_)\\d+)?(?<" + REF + ">([ACTG])+)>(?<" + ALT + ">([ACTG])+)");

    private static Logger logger = LoggerFactory.getLogger(CosmicParser.class);

    private CosmicParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Method to parse the COSMIC file and call the callback function for the evidence entries for the given location
     *
     * @param cosmicFile Cosmic file to parse
     * @param version Cosmic version, e.g: v95
     * @param name Evidence source name, e.g.: cosmic
     * @param assembly Assembly, e.g.: GRCh38
     * @param callback Callback function to process the evidence entries for that location
     * @throws IOException
     */
    public static void parse(Path cosmicFile, String version, String name, String assembly, CosmicParserCallback callback)
            throws IOException {

        int totalNumberRecords = 0;
        int ignoredCosmicLines = 0;
        int numberProcessedRecords = 0;
        int invalidPositionLines = 0;
        int invalidSubstitutionLines = 0;
        int invalidDeletionLines = 0;
        int invalidInsertionLines = 0;
        int invalidDuplicationLines = 0;
        int invalidMutationCDSOtherReason = 0;

        try (BufferedReader cosmicReader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(cosmicFile)))) {
            long t0;
            long t1 = 0;
            long t2 = 0;
            List<EvidenceEntry> evidenceEntries = new ArrayList<>();
            SequenceLocation old = null;

            String headerLine = cosmicReader.readLine(); // First line is the header -> ignore it
            logger.info("Skipping header line: {}", headerLine);

            String line;
            while ((line = cosmicReader.readLine()) != null) {
                String[] fields = line.split("\t", -1);

                t0 = System.currentTimeMillis();
                EvidenceEntry evidenceEntry = buildCosmic(name, version, assembly, fields);
                t1 += System.currentTimeMillis() - t0;

                SequenceLocation sequenceLocation = parseLocation(fields);
                if (sequenceLocation == null) {
                    invalidPositionLines++;
                }
                if (old == null) {
                    old = sequenceLocation;
                }

                if (sequenceLocation != null) {
                    // Parse variant
                    boolean validVariant = false;
                    String mutationCds = fields[HGVS_COLUMN];
                    VariantType variantType = getVariantType(mutationCds);
                    if (variantType != null) {
                        switch (variantType) {
                            case SNV:
                                validVariant = parseSnv(mutationCds, sequenceLocation);
                                if (!validVariant) {
                                    invalidSubstitutionLines++;
                                }
                                break;
                            case DELETION:
                                validVariant = parseDeletion(mutationCds, sequenceLocation);
                                if (!validVariant) {
                                    invalidDeletionLines++;
                                }
                                break;
                            case INSERTION:
                                validVariant = parseInsertion(mutationCds, sequenceLocation);
                                if (!validVariant) {
                                    invalidInsertionLines++;
                                }
                                break;
                            case DUPLICATION:
                                validVariant = parseDuplication(mutationCds);
                                if (!validVariant) {
                                    invalidDuplicationLines++;
                                }
                                break;
                            default:
                                logger.warn("Skipping unkonwn variant type = {}", variantType);
                                validVariant = false;
                                invalidMutationCDSOtherReason++;
                        }
                    }

                    if (validVariant) {
                        if (sequenceLocation.getStart() == old.getStart() && sequenceLocation.getAlternate().equals(old.getAlternate())) {
                            evidenceEntries.add(evidenceEntry);
                        } else {
                            boolean success = callback.processEvidenceEntries(old, evidenceEntries);
                            t2 += System.currentTimeMillis() - t0;
                            if (success) {
                                numberProcessedRecords += evidenceEntries.size();
                            } else {
                                ignoredCosmicLines += evidenceEntries.size();
                            }
                            old = sequenceLocation;
                            evidenceEntries.clear();
                            evidenceEntries.add(evidenceEntry);
                        }
                    } else {
                        ignoredCosmicLines++;
                    }
                } else {
                    ignoredCosmicLines++;
                }
                totalNumberRecords++;

                if (totalNumberRecords % 10000 == 0) {
                    logger.info("totalNumberRecords = {}", totalNumberRecords);
                    logger.info("numberIndexedRecords = {} ({} %)", numberProcessedRecords,
                            (numberProcessedRecords * 100 / totalNumberRecords));
                    logger.info("ignoredCosmicLines = {}", ignoredCosmicLines);
                    logger.info("buildCosmic time = {}", t1);
                    logger.info("callback time = {}", t2);

                    t1 = 0;
                    t2 = 0;
                }
            }
        } finally {
            logger.info("Done");
            logger.info("Total number of parsed Cosmic records: {}", totalNumberRecords);
            logger.info("Number of processed Cosmic records: {}", numberProcessedRecords);
            NumberFormat formatter = NumberFormat.getInstance();
            if (logger.isInfoEnabled()) {
                logger.info("{} cosmic lines ignored: ", formatter.format(ignoredCosmicLines));
            }
            if (invalidPositionLines > 0 && logger.isInfoEnabled()) {
                logger.info("\t- {} lines by invalid position", formatter.format(invalidPositionLines));
            }
            if (invalidSubstitutionLines > 0 && logger.isInfoEnabled()) {
                logger.info("\t- {} lines by invalid substitution CDS", formatter.format(invalidSubstitutionLines));
            }
            if (invalidInsertionLines > 0 && logger.isInfoEnabled()) {
                logger.info("\t- {} lines by invalid insertion CDS", formatter.format(invalidInsertionLines));
            }
            if (invalidDeletionLines > 0 && logger.isInfoEnabled()) {
                logger.info("\t- {} lines by invalid deletion CDS", formatter.format(invalidDeletionLines));
            }
            if (invalidDuplicationLines > 0 && logger.isInfoEnabled()) {
                logger.info("\t- {} lines because mutation CDS is a duplication", formatter.format(invalidDuplicationLines));
            }
            if (invalidMutationCDSOtherReason > 0 && logger.isInfoEnabled()) {
                logger.info("\t- {} lines because mutation CDS is invalid for other reasons",
                        formatter.format(invalidMutationCDSOtherReason));
            }
        }
    }

    private static VariantType getVariantType(String mutationCds) {
        if (mutationCds.contains(HGVS_SNV_CHANGE_SYMBOL)) {
            return VariantType.SNV;
        } else if (mutationCds.contains(HGVS_DELETION_TAG)) {
            return VariantType.DELETION;
        } else if (mutationCds.contains(HGVS_INSERTION_TAG)) {
            return VariantType.INSERTION;
        } else if (mutationCds.contains(HGVS_DUPLICATION_TAG)) {
            return VariantType.DUPLICATION;
        } else {
            return null;
        }
    }

    private static boolean parseDuplication(String dup) {
        // TODO: The only Duplication in Cosmic V70 is a structural variation that is not going to be serialized
        return false;
    }

    private static boolean parseInsertion(String mutationCds, SequenceLocation sequenceLocation) {
        boolean validVariant = true;
        String[] insParts = mutationCds.split("ins");

        if (insParts.length > 1) {
            String insertedNucleotides = insParts[1];
            if (insertedNucleotides.matches("\\d+") || !insertedNucleotides.matches(VARIANT_STRING_PATTERN)) {
                //c.503_508ins30
                validVariant = false;
            } else {
                sequenceLocation.setReference("");
                sequenceLocation.setAlternate(getPositiveStrandString(insertedNucleotides, sequenceLocation.getStrand()));
            }
        } else {
            validVariant = false;
        }

        return validVariant;
    }

    private static boolean parseDeletion(String mutationCds, SequenceLocation sequenceLocation) {
        boolean validVariant = true;
        String[] mutationCDSArray = mutationCds.split("del");

        // For deletions, only deletions of, at most, deletionLength nucleotide are allowed
        if (mutationCDSArray.length < 2) { // c.503_508del (usually, deletions of several nucleotides)
            // TODO: allow these variants
            validVariant = false;
        } else if (mutationCDSArray[1].matches("\\d+")
                || !mutationCDSArray[1].matches(VARIANT_STRING_PATTERN)) { // Avoid allele strings containing Ns, for example
            validVariant = false;
        } else {
            sequenceLocation.setReference(getPositiveStrandString(mutationCDSArray[1], sequenceLocation.getStrand()));
            sequenceLocation.setAlternate("");
        }

        return validVariant;
    }

    private static boolean parseSnv(String mutationCds, SequenceLocation sequenceLocation) {
        boolean validVariant = true;
        Matcher snvMatcher = snvPattern.matcher(mutationCds);

        if (snvMatcher.matches()) {
            String ref = snvMatcher.group(REF);
            String alt = snvMatcher.group(ALT);
            if (!ref.equalsIgnoreCase("N") && !alt.equalsIgnoreCase("N")) {
                sequenceLocation.setReference(getPositiveStrandString(ref, sequenceLocation.getStrand()));
                sequenceLocation.setAlternate(getPositiveStrandString(alt, sequenceLocation.getStrand()));
            } else {
                validVariant = false;
            }
        } else {
            validVariant = false;
        }

        return validVariant;
    }

    private static String getPositiveStrandString(String alleleString, String strand) {
        if (strand.equals("-")) {
            return reverseComplementary(alleleString);
        } else {
            return alleleString;
        }
    }

    private static String reverseComplementary(String alleleString) {
        char[] reverseAlleleString = new StringBuilder(alleleString).reverse().toString().toCharArray();
        for (int i = 0; i < reverseAlleleString.length; i++) {
            reverseAlleleString[i] = VariantAnnotationUtils.COMPLEMENTARY_NT.get(reverseAlleleString[i]);
        }

        return String.valueOf(reverseAlleleString);
    }

    private static EvidenceEntry buildCosmic(String name, String version, String assembly, String[] fields) {
        String id = fields[ID_COLUMN];
        String url = "https://cancer.sanger.ac.uk/cosmic/search?q=" + id;

        EvidenceSource evidenceSource = new EvidenceSource(name, version, null);
        SomaticInformation somaticInformation = getSomaticInformation(fields);
        List<GenomicFeature> genomicFeatureList = getGenomicFeature(fields);

        List<Property> additionalProperties = new ArrayList<>();
        additionalProperties.add(new Property("COSM_ID", "Legacy COSM ID", fields[COSM_ID_COLUMN]));
        additionalProperties.add(new Property("MUTATION_DESCRIPTION", "Description", fields[MUTATION_DESCRIPTION_COLUMN]));
        if (StringUtils.isNotEmpty(fields[MUTATION_ZYGOSITY_COLUMN])) {
            additionalProperties.add(new Property("MUTATION_ZYGOSITY", "Mutation Zygosity", fields[MUTATION_ZYGOSITY_COLUMN]));
        }
        additionalProperties.add(new Property("FATHMM_PREDICTION", "FATHMM Prediction", fields[FATHMM_PREDICTION_COLUMN]));
        additionalProperties.add(new Property("FATHMM_SCORE", "FATHMM Score", "0" + fields[FATHMM_SCORE_COLUMN]));
        additionalProperties.add(new Property("MUTATION_SOMATIC_STATUS", "Mutation Somatic Status",
                fields[MUTATION_SOMATIC_STATUS_COLUMN]));

        List<String> bibliography = getBibliography(fields[PUBMED_PMID_COLUMN]);

        return new EvidenceEntry(evidenceSource, Collections.emptyList(), somaticInformation,
                url, id, assembly,
                getAlleleOriginList(Collections.singletonList(fields[MUTATION_SOMATIC_STATUS_COLUMN])),
                Collections.emptyList(), genomicFeatureList, null, null, null, null,
                EthnicCategory.Z, null, null, null, additionalProperties, bibliography);
    }

    private static SomaticInformation getSomaticInformation(String[] fields) {
        String primarySite = null;
        if (!isMissing(fields[PRIMARY_SITE_COLUMN])) {
            primarySite = fields[PRIMARY_SITE_COLUMN].replace("_", " ");
        }
        String siteSubtype = null;
        if (!isMissing(fields[SITE_SUBTYPE_COLUMN])) {
            siteSubtype = fields[SITE_SUBTYPE_COLUMN].replace("_", " ");
        }
        String primaryHistology = null;
        if (!isMissing(fields[PRIMARY_HISTOLOGY_COLUMN])) {
            primaryHistology = fields[PRIMARY_HISTOLOGY_COLUMN].replace("_", " ");
        }
        String histologySubtype = null;
        if (!isMissing(fields[HISTOLOGY_SUBTYPE_COLUMN])) {
            histologySubtype = fields[HISTOLOGY_SUBTYPE_COLUMN].replace("_", " ");
        }
        String tumourOrigin = null;
        if (!isMissing(fields[TUMOUR_ORIGIN_COLUMN])) {
            tumourOrigin = fields[TUMOUR_ORIGIN_COLUMN].replace("_", " ");
        }
        String sampleSource = null;
        if (!isMissing(fields[SAMPLE_SOURCE_COLUMN])) {
            sampleSource = fields[SAMPLE_SOURCE_COLUMN].replace("_", " ");
        }

        return new SomaticInformation(primarySite, siteSubtype, primaryHistology, histologySubtype, tumourOrigin, sampleSource);
    }

    private static List<String> getBibliography(String bibliographyString) {
        if (!isMissing(bibliographyString)) {
            return Collections.singletonList("PMID:" + bibliographyString);
        }

        return Collections.emptyList();
    }

    private static List<GenomicFeature> getGenomicFeature(String[] fields) {
        List<GenomicFeature> genomicFeatureList = new ArrayList<>(5);
        if (fields[GENE_NAMES_COLUMN].contains("_")) {
            genomicFeatureList.add(createGeneGenomicFeature(fields[GENE_NAMES_COLUMN].split("_")[0]));
        }
        // Add transcript ID
        if (StringUtils.isNotEmpty(fields[1])) {
            genomicFeatureList.add(createGeneGenomicFeature(fields[1], FeatureTypes.transcript));
        }
        if (!fields[HGNC_COLUMN].equalsIgnoreCase(fields[GENE_NAMES_COLUMN]) && !isMissing(fields[HGNC_COLUMN])) {
            genomicFeatureList.add(createGeneGenomicFeature(fields[HGNC_COLUMN]));
        }

        return genomicFeatureList;
    }

    private static SequenceLocation parseLocation(String[] fields) {
        SequenceLocation sequenceLocation = null;
        String locationString = fields[25];
        if (StringUtils.isNotEmpty(locationString)) {
            Matcher matcher = mutationGRCh37GenomePositionPattern.matcher(locationString);
            if (matcher.matches()) {
                sequenceLocation = new SequenceLocation();
                sequenceLocation.setChromosome(getCosmicChromosome(matcher.group(CHROMOSOME)));
                sequenceLocation.setStrand(fields[26]);

                String mutationCds = fields[HGVS_COLUMN];
                VariantType variantType = getVariantType(mutationCds);
                if (VariantType.INSERTION.equals(variantType)) {
                    sequenceLocation.setEnd(Integer.parseInt(matcher.group(START)));
                    sequenceLocation.setStart(Integer.parseInt(matcher.group(END)));
                } else {
                    sequenceLocation.setStart(Integer.parseInt(matcher.group(START)));
                    sequenceLocation.setEnd(Integer.parseInt(matcher.group(END)));
                }
            }
        }
        return sequenceLocation;
    }

    private static String getCosmicChromosome(String chromosome) {
        switch (chromosome) {
            case "23":
                return "X";
            case "24":
                return "Y";
            case "25":
                return "MT";
            default:
                return chromosome;
        }
    }

    private static GenomicFeature createGeneGenomicFeature(String gene) {
        Map<String, String> map = new HashMap<>(1);
        map.put(SYMBOL, gene);

        return new GenomicFeature(FeatureTypes.gene, null, map);
    }

    private static GenomicFeature createGeneGenomicFeature(String featureId, FeatureTypes featureTypes) {
        Map<String, String> map = new HashMap<>(1);
        map.put(SYMBOL, featureId);
        return new GenomicFeature(featureTypes, null, map);
    }

    private static List<AlleleOrigin> getAlleleOriginList(List<String> sourceOriginList) {
        List<AlleleOrigin> alleleOrigin;
        alleleOrigin = new ArrayList<>(sourceOriginList.size());
        for (String originString : sourceOriginList) {
            if (VariantAnnotationUtils.ORIGIN_STRING_TO_ALLELE_ORIGIN.containsKey(originString)) {
                alleleOrigin.add(VariantAnnotationUtils.ORIGIN_STRING_TO_ALLELE_ORIGIN.get(originString));
            } else {
                logger.debug("No SO term found for allele origin {}. Skipping.", originString);
            }
        }
        return alleleOrigin;
    }

    private static boolean isMissing(String string) {
        return !((string != null) && !string.isEmpty()
                && !string.replace(" ", "")
                .replace("not specified", "")
                .replace("NS", "")
                .replace("NA", "")
                .replace("na", "")
                .replace("NULL", "")
                .replace("null", "")
                .replace("\t", "")
                .replace(".", "")
                .replace("-", "").isEmpty());
    }
}
