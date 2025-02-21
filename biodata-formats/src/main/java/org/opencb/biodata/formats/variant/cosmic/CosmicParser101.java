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
import org.opencb.biodata.formats.io.FileFormatException;
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

public class CosmicParser101 {

    // GenomeScreensMutant
    private static final int GENE_SYMBOL_COL = 0;
    private static final int COSMIC_GENE_ID_COL = 1;
    private static final int TRANSCRIPT_ACCESSION_COL = 2;
    private static final int COSMIC_SAMPLE_ID_COL = 3;
    private static final int SAMPLE_NAME_COL = 4;
    private static final int COSMIC_PHENOTYPE_ID_COL = 5;
    private static final int GENOMIC_MUTATION_ID_COL = 6;
    private static final int LEGACY_MUTATION_ID_COL = 7;
    private static final int MUTATION_ID_COL = 8;
    private static final int MUTATION_CDS_COL = 9;
    private static final int MUTATION_AA_COL = 10;
    private static final int MUTATION_DESCRIPTION_COL = 11;
    private static final int MUTATION_ZYGOSITY_COL = 12;
    private static final int LOH_COL = 13;
    private static final int CHROMOSOME_COL = 14;
    private static final int GENOME_START_COL = 15;
    private static final int GENOME_STOP_COL = 16;
    private static final int STRAND_COL = 17;
    private static final int PUBMED_PMID_COL = 18;
    private static final int COSMIC_STUDY_ID_COL = 19;
    private static final int HGVSP_COL = 20;
    private static final int HGVSC_COL = 21;
    private static final int HGVSG_COL = 22;
    private static final int GENOMIC_WT_ALLELE_COL = 23;
    private static final int GENOMIC_MUT_ALLELE_COL = 24;
    private static final int MUTATION_SOMATIC_STATUS_COL = 25;

    // Clasification
    private static final int COSMIC_PHENOTYPE_ID_CLASSIFICATION_COL = 0;
    private static final int PRIMARY_SITE_COL = 1;
    private static final int SITE_SUBTYPE_1_COL = 2;
    private static final int SITE_SUBTYPE_2_COL = 3;
    private static final int SITE_SUBTYPE_3_COL = 4;
    private static final int PRIMARY_HISTOLOGY_COL = 5;
    private static final int HISTOLOGY_SUBTYPE_1_COL = 6;
    private static final int HISTOLOGY_SUBTYPE_2_COL = 7;
    private static final int HISTOLOGY_SUBTYPE_3_COL = 8;
    private static final int NCI_CODE_COL = 9;
    private static final int EFO_COL = 10;

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

    private static Logger logger = LoggerFactory.getLogger(CosmicParser101.class);

    private CosmicParser101() {
        throw new IllegalStateException("Utility class");
    }

    /*
        [column number:label] Heading                                           Description
        --------------------------------------------------------------------------------------------------------
        [00:A]                GENE_SYMBOL                  The gene name for which the data has been curated in COSMIC. In most cases this is the accepted HGNC identifier.
        [01:B]                COSMIC_GENE_ID               A unique COSMIC gene identifier (COSG) is used to identify a gene within the file. This identifier can be used to retrieve additional Gene information from the Cosmic_Genes file.
        [02:C]                TRANSCRIPT_ACCESSION         Unique Ensembl Transcript identifier (ENST). For details see: https://www.ensembl.org/info/genome/stable_ids/index.html. This identifier can be used to retrieve additional Transcript information from the Cosmic_Transcripts file.
        [03:D]                COSMIC_SAMPLE_ID             A unique COSMIC sample identifier (COSS) is used to identify a sample. This identifier can be used to retrieve additional Sample information from the Cosmic_Sample file.
        [04:E]                SAMPLE_NAME                  The sample name can be derived from a number of sources. In many cases it originates from the cell line name. Other sources include names assigned by the annotators, or an incremented number assigned during an anonymization process.
        [05:F]                COSMIC_PHENOTYPE_ID          A unique COSMIC identifier (COSO) for the classification. This identifier can be used to retrieve tissue and histology information from the classification file.
        [06:G]                GENOMIC_MUTATION_ID          Genomic mutation identifier (COSV) to indicate the definitive position of the variant on the genome. This identifier is trackable and stable between different versions of the release. This identifier can be used to retrieve additional legacy mutation ids from the Cosmic_MutationTracking file.
        [07:H]                LEGACY_MUTATION_ID           Legacy mutation identifier (COSM) or (COSN) that will represent existing COSM or COSN mutation identifiers.
        [08:I]                MUTATION_ID                  An internal mutation identifier to uniquely represent each mutation on a specific transcript on a given assembly build. This identifier can be used to retrieve additional legacy mutation ids from the Cosmic_MutationTracking file.
        [09:J]                MUTATION_CDS                 The change that has occurred in the nucleotide sequence. Formatting is identical to the method used for the peptide sequence.
        [10:K]                MUTATION_AA                  The change that has occurred in the peptide sequence. Formatting is based on the recommendations made by the Human Genome Variation Society. The description of each type can be found by following the link to the Mutation Overview page.
        [11:L]                MUTATION_DESCRIPTION         Types of mutations at the amino acid level. Aggregated sequence ontology terms, for more details see: https://www.ensembl.org/info/genome/variation/prediction/predicted_data.html#consequences
        [12:M]                MUTATION_ZYGOSITY            Information on whether the mutation was reported to be homozygous, heterozygous or unknown within the sample.
        [13:N]                LOH                          LOH Information on whether the gene was reported to have loss of heterozygosity in the sample: yes, no or unknown.
        [14:O]                CHROMOSOME                   The chromosome location of a given genome screen (1-22, X, Y or MT).
        [15:P]                GENOME_START                 The start coordinate of a given genome screen.
        [16:Q]                GENOME_STOP                  The end coordinate of a given genome screen.
        [17:R]                STRAND                       Positive or negative (+/-).
        [18:S]                PUBMED_PMID                  The PUBMED ID for the paper that the sample was noted in, linking to pubmed to provide more details of the publication.
        [19:T]                COSMIC_STUDY_ID              A unique COSMIC study identifier (COSU) is used to identify a study that have involved this sample.
        [20:U]                HGVSP                        Human Genome Variation Society peptide syntax.
        [21:V]                HGVSC                        Human Genome Variation Society coding dna sequence syntax (CDS).
        [22:W]                HGVSG                        Human Genome Variation Society genomic syntax (3' shifted).
        [23:X]                GENOMIC_WT_ALLELE            Genomic Wild type allele sequence.
        [24:Y]                GENOMIC_MUT_ALLELE           Genomic mutation allele sequence.
        [25:Z]                MUTATION_SOMATIC_STATUS      Information on whether the sample was reported to be Confirmed somatic variant, Reported in another cancer sample as somatic or Variant of unknown origin:
                                                            * Reported in another cancer sample as somatic = when the mutation has been reported as somatic previously but not in current paper
                                                            * Confirmed somatic variant = if the mutation has been confirmed to be somatic in the experiment by sequencing both the tumour and a matched normal from the same patient
                                                            * Variant of unknown origin = When the tumour has been sequenced without a matched normal tissue from the same individual, the somatic status of the variant cannot be assessed
    */
    /**
     * Method to parse the COSMIC data (from version 101) and call the callback function for the evidence entries for the given location
     *
     * @param genomeScreensMutantFile Cosmic GenomeScreensMutant file
     * @param classificationFile Cosmic Classification file
     * @param version Cosmic version, e.g: v101
     * @param name Evidence source name, e.g.: cosmic
     * @param assembly Assembly, e.g.: GRCh38
     * @param callback Callback function to process the evidence entries for that location
     * @throws IOException
     */
    public static void parse(Path genomeScreensMutantFile, Path classificationFile, String version, String name, String assembly,
                             CosmicParserCallback callback)
            throws IOException, FileFormatException {
        final int numFields = 26;
        Map<String, String[]> classificationMap = getClassificationMap(classificationFile);

        long t0;
        long t1 = 0;
        long t2 = 0;
        List<EvidenceEntry> evidenceEntries = new ArrayList<>();
        SequenceLocation old = null;

        int totalNumberRecords = 0;
        int ignoredCosmicLines = 0;
        int numberProcessedRecords = 0;
        int invalidPositionLines = 0;
        int invalidSubstitutionLines = 0;
        int invalidDeletionLines = 0;
        int invalidInsertionLines = 0;
        int invalidDuplicationLines = 0;
        int invalidMutationCDSOtherReason = 0;

        int numLine = 1;
        try (BufferedReader cosmicReader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(genomeScreensMutantFile)))) {
            String line = cosmicReader.readLine(); // First line is the header -> ignore it
            logger.info("Skipping header line: {}", line);
            getFields(line, numFields, numLine++);

            while ((line = cosmicReader.readLine()) != null) {
                String[] fields = getFields(line, numFields, numLine);

                t0 = System.currentTimeMillis();
                EvidenceEntry evidenceEntry = buildCosmic(name, version, assembly, fields, classificationMap);
                t1 += System.currentTimeMillis() - t0;

                String mutationCds = fields[MUTATION_CDS_COL];
                VariantType variantType = getVariantType(mutationCds);

                SequenceLocation sequenceLocation = parseLocation(fields[CHROMOSOME_COL], fields[STRAND_COL], fields[GENOME_START_COL],
                        fields[GENOME_STOP_COL], variantType);

                if (sequenceLocation == null) {
                    invalidPositionLines++;
                }
                if (old == null) {
                    old = sequenceLocation;
                }

                if (sequenceLocation != null) {
                    // Parse variant
                    boolean validVariant = false;

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
                            evidenceEntries = new ArrayList<>();
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

                numLine++;
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

    /*
        [column number:label] Heading                          Description
        --------------------------------------------------------------------------------------------------------
        [00:A]                COSMIC_PHENOTYPE_ID               A unique COSMIC identifier (COSO) for the classification. Other download files can be linked to this file using this identifier.
        [01:B]                PRIMARY_SITE                      Primary tissue specified in COSMIC.
        [02:C]                SITE_SUBTYPE_1                    Sub tissue specified in COSMIC.
        [03:D]                SITE_SUBTYPE_2                    Sub tissue specified in COSMIC.
        [04:E]                SITE_SUBTYPE_3                    Sub tissue specified in COSMIC.
        [05:F]                PRIMARY_HISTOLOGY                 Primary histology specified in COSMIC.
        [06:G]                HISTOLOGY_SUBTYPE_1               Sub histology specified in COSMIC.
        [07:H]                HISTOLOGY_SUBTYPE_2               Sub histology specified in COSMIC.
        [08:I]                HISTOLOGY_SUBTYPE_3               Sub histology specified in COSMIC.
        [09:J]                NCI_CODE                          NCI thesaurus code for tumour histological classification. For details see https://ncit.nci.nih.gov
        [10:Q]                EFO                               Experimental Factor Ontology (EFO), for details see https://www.ebi.ac.uk/efo/
    */
    private static Map<String, String[]> getClassificationMap(Path classificationFile) throws FileFormatException, IOException {
        final int numFields = 11;
        Map<String, String[]> classificationMap = new HashMap<>();

        int numLine = 1;
        try (BufferedReader cosmicReader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(classificationFile)))) {
            String line = cosmicReader.readLine(); // First line is the header -> ignore it
            logger.info("Skipping header line: {}", line);
            getFields(line, numFields, numLine++);

            while ((line = cosmicReader.readLine()) != null) {
                String[] fields = getFields(line, numFields, numLine);
                String cosoId = fields[0];
                if (StringUtils.isEmpty(cosoId)) {
                    throw new FileFormatException("Missing COSMIC_PHENOTYPE_ID at line #" + numLine + ": " + line);
                }

                // Add to the map
                classificationMap.put(cosoId, fields);
                numLine++;
            }
        }

        return classificationMap;
    }

    private static String[] getFields(String line, int numFields, int numLine) throws FileFormatException {
        String[] fields = line.split("\t", -1);
        if (fields.length != numFields) {
            throw new FileFormatException("Invalid COSMIC format file. Expected " + numFields + " fields, got " + fields.length
                    + " at line #" + numLine + ": " + line);
        }
        return fields;
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
            return VariantAnnotationUtils.reverseComplement(alleleString, true);
        } else {
            return alleleString;
        }
    }

    private static EvidenceEntry buildCosmic(String name, String version, String assembly, String[] fields,
                                             Map<String, String[]> classificationMap) {
        String id = fields[GENOMIC_MUTATION_ID_COL];
        String cosoId = fields[COSMIC_PHENOTYPE_ID_COL];
        String url = "https://cancer.sanger.ac.uk/cosmic/search?q=" + id;

        EvidenceSource evidenceSource = new EvidenceSource(name, version, null);
        SomaticInformation somaticInformation = getSomaticInformation(classificationMap.get(cosoId));
        List<GenomicFeature> genomicFeatureList = getGenomicFeature(fields);

        List<Property> additionalProperties = new ArrayList<>();
        if (StringUtils.isNotEmpty(fields[GENOMIC_MUTATION_ID_COL])) {
            additionalProperties.add(new Property("GENOMIC_MUTATION_ID", "Genomic mutation ID (COSV)", fields[GENOMIC_MUTATION_ID_COL]));
        }
        if (StringUtils.isNotEmpty(fields[LEGACY_MUTATION_ID_COL])) {
            additionalProperties.add(new Property("LEGACY_MUTATION_ID", "Legacy ID (COSM) or (COSN)", fields[LEGACY_MUTATION_ID_COL]));
        }
        if (StringUtils.isNotEmpty(fields[MUTATION_CDS_COL])) {
            additionalProperties.add(new Property("MUTATION_CDS", "Change in the nucleotide sequence", fields[MUTATION_CDS_COL]));
        }
        if (StringUtils.isNotEmpty(fields[MUTATION_AA_COL])) {
            additionalProperties.add(new Property("MUTATION_AA", "Change in the peptide sequence", fields[MUTATION_AA_COL]));
        }
        if (StringUtils.isNotEmpty(fields[MUTATION_DESCRIPTION_COL])) {
            additionalProperties.add(new Property("MUTATION_DESCRIPTION", "Description", fields[MUTATION_DESCRIPTION_COL]));
        }
        if (StringUtils.isNotEmpty(fields[MUTATION_ZYGOSITY_COL])) {
            additionalProperties.add(new Property("MUTATION_ZYGOSITY", "Mutation Zygosity", fields[MUTATION_ZYGOSITY_COL]));
        }
        if (StringUtils.isNotEmpty(fields[MUTATION_SOMATIC_STATUS_COL])) {
            additionalProperties.add(new Property("MUTATION_SOMATIC_STATUS", "Mutation Somatic Status",
                    fields[MUTATION_SOMATIC_STATUS_COL]));
        }
        if (StringUtils.isNotEmpty(fields[LOH_COL])) {
            additionalProperties.add(new Property("LOH", "Loss of heterozygosity", fields[LOH_COL]));
        }
        if (StringUtils.isNotEmpty(fields[HGVSP_COL])) {
            additionalProperties.add(new Property("HGVSP", "HGVS (peptide)", fields[HGVSP_COL]));
        }
        if (StringUtils.isNotEmpty(fields[HGVSC_COL])) {
            additionalProperties.add(new Property("HGVSC", "HGVS (CDS)", fields[HGVSC_COL]));
        }
        if (StringUtils.isNotEmpty(fields[HGVSG_COL])) {
            additionalProperties.add(new Property("HGVSG", "HGVS (3' shifted)", fields[HGVSG_COL]));
        }

        List<String> bibliography = getBibliography(fields[PUBMED_PMID_COL]);

        return new EvidenceEntry(evidenceSource, Collections.emptyList(), somaticInformation,
                url, id, assembly,
                getAlleleOriginList(Collections.singletonList(fields[MUTATION_SOMATIC_STATUS_COL])),
                Collections.emptyList(), genomicFeatureList, null, null, null, null,
                EthnicCategory.Z, null, null, null, additionalProperties, bibliography);
    }

    private static SomaticInformation getSomaticInformation(String[] fields) {
        String primarySite = null;
        if (!isMissing(fields[PRIMARY_SITE_COL])) {
            primarySite = fields[PRIMARY_SITE_COL].replace("_", " ");
        }
        String siteSubtype = null;
        if (!isMissing(fields[SITE_SUBTYPE_1_COL])) {
            siteSubtype = fields[SITE_SUBTYPE_1_COL].replace("_", " ");
        }
        String primaryHistology = null;
        if (!isMissing(fields[PRIMARY_HISTOLOGY_COL])) {
            primaryHistology = fields[PRIMARY_HISTOLOGY_COL].replace("_", " ");
        }
        String histologySubtype = null;
        if (!isMissing(fields[HISTOLOGY_SUBTYPE_1_COL])) {
            histologySubtype = fields[HISTOLOGY_SUBTYPE_1_COL].replace("_", " ");
        }
        String tumourOrigin = null;

        String sampleSource = null;
        if (!isMissing(fields[SAMPLE_NAME_COL])) {
            sampleSource = fields[SAMPLE_NAME_COL].replace("_", " ");
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
        // Add gene symbol and COSMIC gene
        if (StringUtils.isNotEmpty(fields[GENE_SYMBOL_COL])) {
            Map<String, String> xrefs = new HashMap<>();
            if (StringUtils.isNotEmpty(fields[COSMIC_GENE_ID_COL])) {
                xrefs.put("COSMIC_GENE", fields[COSMIC_GENE_ID_COL]);
            }
            genomicFeatureList.add(createGeneGenomicFeature(fields[GENE_SYMBOL_COL].split("_")[0], FeatureTypes.gene, xrefs));
        }

        // Add transcript ID
        if (StringUtils.isNotEmpty(fields[TRANSCRIPT_ACCESSION_COL])) {
            genomicFeatureList.add(createGeneGenomicFeature(fields[TRANSCRIPT_ACCESSION_COL], FeatureTypes.transcript));
        }

        return genomicFeatureList;
    }

    private static SequenceLocation parseLocation(String chrom, String strand, String start, String end, VariantType variantType) {
        SequenceLocation sequenceLocation = new SequenceLocation();
        sequenceLocation.setChromosome(getCosmicChromosome(chrom));
        sequenceLocation.setStrand(strand);
        if (VariantType.INSERTION.equals(variantType)) {
            sequenceLocation.setEnd(Integer.parseInt(start));
            sequenceLocation.setStart(Integer.parseInt(end));
        } else {
            sequenceLocation.setStart(Integer.parseInt(start));
            sequenceLocation.setEnd(Integer.parseInt(end));
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

    private static GenomicFeature createGeneGenomicFeature(String featureId, FeatureTypes featureTypes) {
        Map<String, String> map = new HashMap<>(1);
        map.put(SYMBOL, featureId);
        return new GenomicFeature(featureTypes, null, map);
    }

    private static GenomicFeature createGeneGenomicFeature(String featureId, FeatureTypes featureTypes, Map<String, String> xrefs) {
        xrefs.put(SYMBOL, featureId);
        return new GenomicFeature(featureTypes, null, xrefs);
    }

    private static Map<String, AlleleOrigin> ORIGIN_STRING_TO_ALLELE_ORIGIN = new HashMap<>();

    static {

        ///////////////////////////////////////////////////////////////////////
        /////   ClinVar and Cosmic allele origins to SO terms   ///////////////
        ///////////////////////////////////////////////////////////////////////
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("germline", AlleleOrigin.germline_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("maternal", AlleleOrigin.maternal_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("de novo", AlleleOrigin.de_novo_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("paternal", AlleleOrigin.paternal_variant);
        ORIGIN_STRING_TO_ALLELE_ORIGIN.put("somatic", AlleleOrigin.somatic_variant);
    }


    private static List<AlleleOrigin> getAlleleOriginList(List<String> sourceOriginList) {
        List<AlleleOrigin> alleleOrigin;
        alleleOrigin = new ArrayList<>(sourceOriginList.size());
        for (String originString : sourceOriginList) {
            AlleleOrigin alleleOriginValue = VariantAnnotationUtils.parseAlleleOrigin(originString);
            if (alleleOriginValue != null) {
                alleleOrigin.add(alleleOriginValue);
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
