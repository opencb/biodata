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

package org.opencb.biodata.formats.variant.civic;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.models.core.civic.*;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

public class CivicParser {

    private static final Logger logger = LoggerFactory.getLogger(CivicParser.class);

    private CivicParser() {
        throw new IllegalStateException("Utility class");
    }

    public static void parse(Path variantSummariesFile, Path featureSummariesFile, Path molecularProfileSummariesFile,
                             Path assertionSummariesFile, Path clinicalEvidenceSummariesFile, String version,
                             CivicParserCallback callback) throws IOException, FileFormatException {

        logger.info("Starting CIViC parsing with version: {}", version);

        // Step 1: Parse features first
        Map<String, CivicFeature> featuresMap = parseFeaturesFile(featureSummariesFile);
        logger.info("Parsed {} features", featuresMap.size());

        // Step 2: Parse clinical evidence and link to molecular profiles
        Map<String, CivicClinicalEvidence> evidencesMap = parseClinicalEvidencesFile(clinicalEvidenceSummariesFile);
        logger.info("Parsed {} evidences", evidencesMap.size());

        // Step 3: Parse assertions and complete them with evidences
        Map<String, CivicAssertion> assertionsMap = parseAssertionsFile(assertionSummariesFile, evidencesMap);
        logger.info("Parsed {} assertions and complete with evidences", assertionsMap.size());

        // Step 4: Parse molecular profiles and complete them with assertions and evidences
        Map<String, CivicMolecularProfile> profilesMap = parseMolecularProfilesFile(molecularProfileSummariesFile, assertionsMap,
                evidencesMap);
        logger.info("Parsed {} molecular profiles and complete with assertions and evidences", profilesMap.size());


        // Step 5: Parse variants and build complete objects
        int numVariants = parseVariantsFile(variantSummariesFile, profilesMap, featuresMap, callback);
        logger.info("Completed CIViC parsing: {} variants processed", numVariants);
    }

    private static Map<String, CivicFeature> parseFeaturesFile(Path featureSummariesFile) throws IOException, FileFormatException {
        Map<String, CivicFeature> featuresMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(featureSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] fields = line.split("\t", -1);
                    CivicFeature feature = parseFeatureFields(fields);
                    featuresMap.put(feature.getFeatureId(), feature);
                }
            }
        }

        return featuresMap;
    }

    private static CivicFeature parseFeatureFields(String[] fields) {
        return new CivicFeature()
                .setFeatureId(getField(fields, 0))
                .setFeatureCivicUrl(getField(fields, 1))
                .setFeatureType(getField(fields, 2))
                .setName(getField(fields, 3))
                .setFeatureAliases(parseStringList(getField(fields, 4)))
                .setDescription(getField(fields, 5))
                .setLastReviewDate(getField(fields, 6))
                .setFlagged(parseBoolean(getField(fields, 7)))
                .setEntrezId(getField(fields, 8))
                .setNcitId(getField(fields, 9))
                .setFivePrimePartnerStatus(getField(fields, 10))
                .setThreePrimePartnerStatus(getField(fields, 11))
                .setFivePrimeGeneId(getField(fields, 12))
                .setFivePrimeGeneName(getField(fields, 13))
                .setFivePrimeGeneEntrezId(getField(fields, 14))
                .setThreePrimeGeneId(getField(fields, 15))
                .setThreePrimeGeneName(getField(fields, 16))
                .setThreePrimeGeneEntrezId(getField(fields, 17));
    }

    private static Map<String, CivicClinicalEvidence> parseClinicalEvidencesFile(Path clinicalEvidenceSummariesFile) throws IOException {

        Map<String, CivicClinicalEvidence> evidencesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(clinicalEvidenceSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] fields = line.split("\t", -1);
                    CivicClinicalEvidence evidence = parseClinicalEvidenceFields(fields);
                    evidencesMap.put(evidence.getEvidenceId(), evidence);
                }
            }
        }

        return evidencesMap;
    }

    private static CivicClinicalEvidence parseClinicalEvidenceFields(String[] fields) {
        return new CivicClinicalEvidence()
                .setDisease(getField(fields, 2))
                .setDoid(getField(fields, 3))
                .setPhenotypes(parseStringList(getField(fields, 4)))
                .setTherapies(parseStringList(getField(fields, 5)))
                .setTherapyInteractionType(getField(fields, 6))
                .setEvidenceType(getField(fields, 7))
                .setEvidenceDirection(getField(fields, 8))
                .setEvidenceLevel(getField(fields, 9))
                .setSignificance(getField(fields, 10))
                .setEvidenceStatement(getField(fields, 11))
                .setCitationId(getField(fields, 12))
                .setSourceType(getField(fields, 13))
                .setAscoAbstractId(getField(fields, 14))
                .setCitation(getField(fields, 15))
                .setNctIds(parseStringList(getField(fields, 16)))
                .setRating(getField(fields, 17))
                .setEvidenceStatus(getField(fields, 18))
                .setEvidenceId(getField(fields, 19))
                .setVariantOrigin(getField(fields, 20))
                .setLastReviewDate(getField(fields, 21))
                .setEvidenceCivicUrl(getField(fields, 22))
                .setFlagged(parseBoolean(getField(fields, 24)));
    }

    private static  Map<String, CivicAssertion> parseAssertionsFile(Path assertionSummariesFile, Map<String,
            CivicClinicalEvidence> evidenceMap) throws IOException {

        Map<String, CivicAssertion> assertionsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(assertionSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] fields = line.split("\t", -1);
                    CivicAssertion assertion = parseAssertionFields(fields);

                    // Set evidences from evidence map using the evidence IDs in the assertion
                    List<String> evidenceIds = parseStringList(getField(fields, 18));
                    for (String evidenceId : evidenceIds) {
                        if (evidenceMap.containsKey(evidenceId)) {
                            assertion.getEvidences().add(evidenceMap.get(evidenceId));
                        }
                    }

                    assertionsMap.put(assertion.getAssertionId(), assertion);
                }
            }
        }

        return assertionsMap;
    }

    private static CivicAssertion parseAssertionFields(String[] fields) {
        return new CivicAssertion()
                .setDisease(getField(fields, 2))
                .setDoid(getField(fields, 3))
                .setPhenotypes(parseStringList(getField(fields, 4)))
                .setTherapies(parseStringList(getField(fields, 5)))
                .setAssertionType(getField(fields, 6))
                .setAssertionDirection(getField(fields, 7))
                .setSignificance(getField(fields, 8))
                .setAcmgCodes(parseStringList(getField(fields, 9)))
                .setAmpCategory(getField(fields, 10))
                .setNccnGuideline(getField(fields, 11))
                .setNccnGuidelineVersion(getField(fields, 12))
                .setRegulatoryApproval(getField(fields, 13))
                .setFdaCompanionTest(getField(fields, 14))
                .setAssertionSummary(getField(fields, 15))
                .setAssertionDescription(getField(fields, 16))
                .setAssertionId(getField(fields, 17))
                .setLastReviewDate(getField(fields, 19))
                .setAssertionCivicUrl(getField(fields, 20))
                .setFlagged(parseBoolean(getField(fields, 23)));
    }

    private static Map<String, CivicMolecularProfile> parseMolecularProfilesFile(Path molecularProfileSummariesFile,
                                                                                 Map<String, CivicAssertion> assertionsMap,
                                                                                 Map<String, CivicClinicalEvidence> evidencesMap)
            throws IOException {

        Map<String, CivicMolecularProfile> profilesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(molecularProfileSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] fields = line.split("\t", -1);
                    CivicMolecularProfile profile = parseMolecularProfileFields(fields);

                    // Set evidences from evidence map using the evidence IDs in the molecular profile
                    List<String> evidenceIds = parseStringList(getField(fields, 6));
                    for (String evidenceId : evidenceIds) {
                        if (evidencesMap.containsKey(evidenceId)) {
                            profile.getEvidences().add(evidencesMap.get(evidenceId));
                        }
                    }

                    // Set assertions from assertion map using the assertion IDs in the molecular profile
                    List<String> assertionIds = parseStringList(getField(fields, 8));
                    for (String assertionId : assertionIds) {
                        if (assertionsMap.containsKey(assertionId)) {
                            profile.getAssertions().add(assertionsMap.get(assertionId));
                        }
                    }

                    profilesMap.put(profile.getMolecularProfileId(), profile);
                }
            }
        }

        return profilesMap;
    }

    private static CivicMolecularProfile parseMolecularProfileFields(String[] fields) {
        return new CivicMolecularProfile()
                .setName(getField(fields, 0))
                .setMolecularProfileId(getField(fields, 1))
                .setSummary(getField(fields, 2))
                .setEvidenceScore(getField(fields, 5))
                .setAliases(parseStringList(getField(fields, 10)))
                .setLastReviewDate(getField(fields, 11))
                .setFlagged(parseBoolean(getField(fields, 12)));
    }

    private static int parseVariantsFile(Path variantSummariesFile, Map<String, CivicMolecularProfile> profilesMap,
                                         Map<String, CivicFeature> featuresMap, CivicParserCallback callback) throws IOException {

        int numVariants = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(variantSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] fields = line.split("\t", -1);
                    CivicVariant variant = parseVariantFields(fields);

                    // Link feature and enhance with transcript/exon info
                    String featureId = getField(fields, 3);
                    if (featuresMap.containsKey(featureId)) {
                        CivicFeature feature = featuresMap.get(featureId);
                        // Enhance feature with transcript/exon information from variant file
                        feature.setFivePrimeTranscript(getField(fields, 32))
                                .setFivePrimeEndExon(getField(fields, 33))
                                .setFivePrimeExonOffset(getField(fields, 34))
                                .setFivePrimeExonOffsetDirection(getField(fields, 35))
                                .setThreePrimeTranscript(getField(fields, 36))
                                .setThreePrimeStartExon(getField(fields, 37))
                                .setThreePrimeExonOffset(getField(fields, 38))
                                .setThreePrimeExonOffsetDirection(getField(fields, 39));

                        variant.setFeature(feature);
                    }

                    // Link molecular profiles from profiles map using the single variant molecular profile ID
                    String singleVariantMolecularProfileId = getField(fields, 11);
                    if (StringUtils.isNotBlank(singleVariantMolecularProfileId)
                            && profilesMap.containsKey(singleVariantMolecularProfileId)) {
                        variant.setMolecularProfile(profilesMap.get(singleVariantMolecularProfileId));
                    }

                    // Process variant through callback
                    if (!callback.processCivicVariant(variant)) {
                        // Stop parsing if callback returns false
                        logger.warn("CIViC parsing stopped by callback request.");
                        break;
                    }
                    numVariants++;
                }
            }
        }

        return numVariants;
    }

    private static CivicVariant parseVariantFields(String[] fields) {
        return new CivicVariant()
                .setVariantId(getField(fields, 0))
                .setVariantCivicUrl(getField(fields, 1))
                .setVariant(getField(fields, 6))
                .setVariantAliases(parseStringList(getField(fields, 7)))
                .setFlagged(parseBoolean(getField(fields, 8)))
                .setVariantGroups(parseStringList(getField(fields, 9)))
                .setVariantTypes(parseStringList(getField(fields, 10)))
                .setLastReviewDate(getField(fields, 12))
                .setGene(getField(fields, 13))
                .setEntrezId(getField(fields, 14))
                .setChromosome(getField(fields, 15))
                .setStart(getField(fields, 16))
                .setStop(getField(fields, 17))
                .setReferenceBases(getField(fields, 18))
                .setVariantBases(getField(fields, 19))
                .setRepresentativeTranscript(getField(fields, 20))
                .setEnsemblVersion(getField(fields, 21))
                .setReferenceBuild(getField(fields, 22))
                .setHgvsDescriptions(parseStringList(getField(fields, 23)))
                .setAlleleRegistryId(getField(fields, 24))
                .setClinvarIds(parseStringList(getField(fields, 25)))
                .setNcitId(getField(fields, 26))
                .setViccCompliantName(getField(fields, 31));
    }

    // Helper methods
    private static String getField(String[] fields, int index) {
        if (index < fields.length && StringUtils.isNotBlank(fields[index])) {
            return fields[index];
        }
        return null;
    }

    private static Boolean parseBoolean(String value) {
        if (StringUtils.isNotBlank(value)) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return null;
    }

    private static List<String> parseStringList(String value) {
        if (StringUtils.isNotBlank(value)) {
            return Arrays.asList(value.split(",\\s*"));
        }
        return new ArrayList<>();
    }
}