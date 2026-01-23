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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

    private Path variantSummariesFile;
    private Path featureSummariesFile;
    private Path molecularProfileSummariesFile;
    private Path assertionSummariesFile;
    private Path clinicalEvidenceSummariesFile;
    private String version;
    private String assembly;
    private CivicParserCallback callback;

    Map<String, CivicFeature> featuresMap;
    Map<String, CivicClinicalEvidence> evidencesMap;
    Map<String, CivicAssertion> assertionsMap;
    Map<String, CivicMolecularProfile> profilesMap;

    Map<String, Set<String>> variantToProfilesMap;

    private static final Logger logger = LoggerFactory.getLogger(CivicParser.class);

    public CivicParser(Path variantSummariesFile, Path featureSummariesFile, Path molecularProfileSummariesFile,
                       Path assertionSummariesFile, Path clinicalEvidenceSummariesFile, String version, String assembly,
                       CivicParserCallback callback) {
        this.variantSummariesFile = variantSummariesFile;
        this.featureSummariesFile = featureSummariesFile;
        this.molecularProfileSummariesFile = molecularProfileSummariesFile;
        this.assertionSummariesFile = assertionSummariesFile;
        this.clinicalEvidenceSummariesFile = clinicalEvidenceSummariesFile;
        this.version = version;
        this.assembly = assembly;
        this.callback = callback;

        this.variantToProfilesMap = new HashMap<>();
    }

    public void parse() throws IOException, FileFormatException {
        logger.info("Starting CIViC parsing with version {} for assembly {}", version, assembly);

        // Step 1: Parse features first
        parseFeaturesFile();

        // Step 2: Parse clinical evidence and link to molecular profiles
        parseClinicalEvidencesFile();

        // Step 3: Parse assertions and complete them with evidences
        parseAssertionsFile();

        // Step 4: Parse molecular profiles and complete them with assertions and evidences
        parseMolecularProfilesFile();

        // Step 5: Parse variants and build complete objects
        parseVariantsFile();
    }

    private void parseFeaturesFile() throws IOException {
        featuresMap = new HashMap<>();

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

        logger.info("Parsed {} features", featuresMap.size());
    }

    private CivicFeature parseFeatureFields(String[] fields) {
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

    private void parseClinicalEvidencesFile() throws IOException {
        evidencesMap = new HashMap<>();

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

        logger.info("Parsed {} evidences", evidencesMap.size());
    }

    private CivicClinicalEvidence parseClinicalEvidenceFields(String[] fields) {
        // 0                    1                       2       3       4           5           6                           7
        // molecular_profile	molecular_profile_id	disease	doid	phenotypes	therapies	therapy_interaction_type	evidence_type
        // 8                    9                10             11                  12          13          14                  15
        // evidence_direction	evidence_level	significance	evidence_statement	citation_id	source_type	asco_abstract_id	citation
        // 16       17      18              19          20              21                  22                  23
        // nct_ids	rating	evidence_status	evidence_id	variant_origin	last_review_date	evidence_civic_url	molecular_profile_civic_url
        // 24
        // is_flagged
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

    private void parseAssertionsFile() throws IOException {
        assertionsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(assertionSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] fields = line.split("\t", -1);
                    CivicAssertion assertion = parseAssertionFields(fields);

                    // Set evidences from evidence map using the evidence IDs in the assertion
                    List<String> evidenceIds = parseStringList(getField(fields, 18));
                    for (String evidenceId : evidenceIds) {
                        if (evidencesMap.containsKey(evidenceId)) {
                            assertion.getEvidences().add(evidencesMap.get(evidenceId));
                        }
                    }

                    assertionsMap.put(assertion.getAssertionId(), assertion);
                }
            }
        }

        logger.info("Parsed {} assertions and complete with evidences", assertionsMap.size());
    }

    private CivicAssertion parseAssertionFields(String[] fields) {
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

    private void parseMolecularProfilesFile() throws IOException {
        profilesMap = new HashMap<>();

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

                    // Add to the variant to profiles map
                    // This is necessary because there are situations that a given profile is not include in the VariantSummaries.tsv file
                    // but in the MolecularProfilesSummarie.tsv file (usually when the profile includes multiple variants)
                    List<String> variantIds = parseStringList(getField(fields, 3));
                    for (String variantId : variantIds) {
                        if (!variantToProfilesMap.containsKey(variantId)) {
                            variantToProfilesMap.put(variantId, new HashSet<>());
                        }
                        variantToProfilesMap.get(variantId).add(profile.getMolecularProfileId());
                    }
                }
            }
        }

        logger.info("Parsed {} molecular profiles and complete with assertions and evidences", profilesMap.size());
    }

    private CivicMolecularProfile parseMolecularProfileFields(String[] fields) {
        // 0    1                       2       3           4                   5               6                   7
        // name	molecular_profile_id	summary	variant_ids	variants_civic_url	evidence_score	evidence_item_ids	evidence_items_civic_url
        // 8                9                       10       11                 12
        // assertion_ids	assertions_civic_url	aliases	last_review_date	is_flagged
        return new CivicMolecularProfile()
                .setName(getField(fields, 0))
                .setMolecularProfileId(getField(fields, 1))
                .setSummary(getField(fields, 2))
                .setEvidenceScore(getField(fields, 5))
                .setAliases(parseStringList(getField(fields, 10)))
                .setLastReviewDate(getField(fields, 11))
                .setFlagged(parseBoolean(getField(fields, 12)));
    }

    private void parseVariantsFile() throws IOException {
        int totalVariants = 0;
        int numVariants = 0;
        int numVariantsSkippedByAssemblyEmpty = 0;
        int numVariantsSkippedByAssemblyMismatch = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(variantSummariesFile)))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    totalVariants++;

                    String[] fields = line.split("\t", -1);

                    // Filter by assembly
                    String variantAssembly = getField(fields, 22);
                    if (StringUtils.isEmpty(variantAssembly)) {
                        numVariantsSkippedByAssemblyEmpty++;
                        logger.warn("Skipping variant ID {} due to assembly is empty", getField(fields, 0));
                        continue;
                    }
                    if (!assembly.equalsIgnoreCase(variantAssembly)) {
                        numVariantsSkippedByAssemblyMismatch++;
                        logger.warn("Skipping variant ID {} due to assembly mismatch: expected {}, found {}", getField(fields, 0),
                                assembly, variantAssembly);
                        continue;
                    }

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
                    if (StringUtils.isNotEmpty(singleVariantMolecularProfileId)) {
                        if (!variantToProfilesMap.containsKey(variant.getVariantId())) {
                            variantToProfilesMap.put(variant.getVariantId(), new HashSet<>());
                        }
                        variantToProfilesMap.get(variant.getVariantId()).add(singleVariantMolecularProfileId);
                    }

                    // Iterate the list of molecular profile IDs associated to the variant and link the profilesfrom the profiles map
                    List<String> profileIds = new ArrayList<>(variantToProfilesMap.get(variant.getVariantId()));
                    for (String profileId : profileIds) {
                        if (profilesMap.containsKey(profileId)) {
                            variant.getMolecularProfiles().add(profilesMap.get(profileId));
                        }
                    }

                    // Process variant through callback
                    if (callback.processCivicVariant(variant)) {
                        numVariants++;
                    } else {
                        // Add warning to log and continue the parsing
                        logger.warn("CIViC parsing callback returned false for variant ID: {}", variant.getVariantId());
                    }
                }
            }
        }

        logger.info("Parsed {} variants, {} passed the callback filter", totalVariants, numVariants);
        logger.info("Skipped {} variants due to empty assembly", numVariantsSkippedByAssemblyEmpty);
        logger.info("Skipped {} variants due to assembly mismatch", numVariantsSkippedByAssemblyMismatch);
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