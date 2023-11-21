package org.opencb.biodata.models.constants;

import org.opencb.biodata.models.clinical.qc.RelatednessReport;

public class FieldConstants {
    public static final String PHENOTYPE_AGE_OF_ON_SET = "Indicates the age of on set of the phenotype";
    public static final String PHENOTYPE_STATUS = "Status of phenotype OBSERVED, NOT_OBSERVED, UNKNOWN";
    public static final String GENERIC_ID_DESCRIPTION = "Id to identify the object";
    public static final String GENERIC_DESCRIPTION_DESCRIPTION = "Users may provide a description for the entry.";
    public static final String SIGNATURE_TYPE_DESCRIPTION = "Signature type SNV, INDEL...";
    public static final String SIGNATURE_COUNTS_DESCRIPTION = "List of GenomeContextCount";
    public static final String SIGNATURE_FILES_DESCRIPTION = "List of files of signature";
    @Deprecated
    public static final String SIGNATURE_SIGNATURE_FITTING_SCORE_DESCRIPTION = "Signature fitting";
    public static final String SIGNATURE_SIGNATURE_FITTING_SCORES_DESCRIPTION = "List of signature fitting scores";
    public static final String GENOME_CONTEXT_COUNT_CONTEXT_DESCRIPTION = "Genome context to count";
    public static final String GENOME_CONTEXT_COUNT_TOTAL_DESCRIPTION = "Counted integer";
    public static final String SIGNATURE_FITTING_ID_DESCRIPTION = "Signature fitting ID";
    public static final String SIGNATURE_FITTING_METHOD_DESCRIPTION = "Method used to fit the signature";
    public static final String SIGNATURE_FITTING_SOURCE_DESCRIPTION = "Source of the fitting signature";
    public static final String SIGNATURE_FITTING_SIGNATURE_VERSION_DESCRIPTION = "Signature version of the fitting signature";
    public static final String SIGNATURE_FITTING_SCORES_DESCRIPTION = "Scores of the fitting signature";
    public static final String SIGNATURE_FITTING_COEFF_DESCRIPTION = "Coefficient of the fitting signature";
    public static final String SIGNATURE_FITTING_FILE_DESCRIPTION = "Files of the fitting signature";
    public static final String SIGNATURE_FITTING_PARAMS_DESCRIPTION = "Input parameters of the fitting signature";

    public static final String HRDETECT_SNV_FITTING_ID_DESCRIPTION = "Signature fitting ID for SNV";
    public static final String HRDETECT_SV_FITTING_ID_DESCRIPTION = "Signature fitting ID for SV";
    public static final String HRDETECT_CNV_QUERY_DESCRIPTION = "CNV query";
    public static final String HRDETECT_INDEL_QUERY_DESCRIPTION = "INDEL query";
    public static final String HRDETECT_PARAMS_DESCRIPTION = "Other HRDetect params";
    public static final String HRDETECT_SCORES_DESCRIPTION = "HRDetect scores";
    public static final String HRDETECT_FILES_DESCRIPTION = "HRDetect output files";

    public static final String GENOMEPLOT_CONFIG_DESCRIPTION = "Config of the genomePlot";
    public static final String GENOMEPLOT_FILE_DESCRIPTION = "File of the genomePlot";
    public static final String GENOME_PLOT_CONFIG_TITLE_DESCRIPTION = "Title of the genome plot configuration";
    public static final String GENOME_PLOT_CONFIG_DENSITY_DESCRIPTION = "Density of the genome plot configuration";
    public static final String GENOME_PLOT_CONFIG_GENERAL_QUERY_DESCRIPTION = "Map for the general query of the genome plot configuration";
    public static final String GENOME_PLOT_CONFIG_TRACKS_DESCRIPTION = "List of GenomePlotTrack";
    public static final String GENOME_PLOT_TRACK_TYPE_DESCRIPTION = "Genome Plot Track Type";
    public static final String GENOME_PLOT_TRACK_DESCRIPTION_DESCRIPTION = "Genome Plot Track description";
    public static final String GENOME_PLOT_TRACK_QUERY_DESCRIPTION = "Genome Plot Track map for query";
    public static final String GENERIC_QUERY_DESCRIPTION = "Map for query";
    public static final String SAMPLE_QC_VARIANT_STATS_STATS = "Stats result set";
    public static final String GENERIC_NAME_DESCRIPTION = "Object name";
    public static final String ONTOLOGY_SOURCE_DESCRIPTION = "Ontology source";
    public static final String ONTOLOGY_URL_DESCRIPTION = "Ontology url";
    public static final String GENERIC_ATTRIBUTES_DESCRIPTION = "Dictionary that can be customised by users to store any additional "
            + "information users may require..";
    public static final String CLINICAL_COMMENT_AUTHOR_DESCRIPTION = "Clinical comment author";
    public static final String CLINICAL_COMMENT_MESSAGE_DESCRIPTION = "Clinical comment message";
    public static final String CLINICAL_COMMENT_TAGS_DESCRIPTION = "List of tags for the clinical comment";
    public static final String CLINICAL_COMMENT_DATE_DESCRIPTION = "Date of the clinical comment";
    public static final String RELATEDNESS_REPORT_METHOD_DESCRIPTION = "Method of the relatedness report";
    public static final String CLINICAL_CONFIDENCE_AUTHOR_DESCRIPTION = "Clinical confidence author";
    public static final String CLINICAL_CONFIDENCE_DATE_DESCRIPTION = "Date of the clinical confidence";
    public static final String CLINICAL_CONFIDENCE_VALUE_DESCRIPTION = "Date of the clinical confidence";

    public static final String RELATEDNESS_REPORT_MAF_DESCRIPTION = "Minor allele frequency to filter variants, e.g.: 1kg_phase3:CEU>0.35,"
            + " cohort:ALL>0.05";
    public static final String RELATEDNESS_REPORT_HAPLOID_CALL_MODE_DESCRIPTION = "Haploid call mode, equivalent to the PLINK/IBD parameter"
            + " vcf-half-call, accepts the following values: " + RelatednessReport.HAPLOID_CALL_MODE_DEFAUT_VALUE + ", "
            + RelatednessReport.HAPLOID_CALL_MODE_MISSING_VALUE + " and " + RelatednessReport.HAPLOID_CALL_MODE_REF_VALUE;
    public static final String RELATEDNESS_REPORT_SCORES_DESCRIPTION = "Relatedness scores for pair of samples";
    public static final String RELATEDNESS_REPORT_FILES_DESCRIPTION = "List of files of Relatedness Report";

    public static final String CLINICAL_ANALYST_DATE_DESCRIPTION = "Date of the clinical analyst";
    public static final String CLINICAL_ANALYST_ASSIGNED_BY_DESCRIPTION = "Assigned by field";

    public static final String CLINICAL_ANALYST_EMAIL_DESCRIPTION = "Email of the analyst";
    public static final String AUDIT_AUTHOR_DESCRIPTION = "Audit author";
    public static final String AUDIT_MESSAGE_DESCRIPTION = "Audit message";
    public static final String AUDIT_ACTION_DESCRIPTION = "Enum action that can have the values "
            + " CREATE_CLINICAL_ANALYSIS, CREATE_INTERPRETATION, UPDATE_CLINICAL_ANALYSIS, DELETE_CLINICAL_ANALYSIS,"
            + " UPDATE_INTERPRETATION, REVERT_INTERPRETATION, CLEAR_INTERPRETATION, MERGE_INTERPRETATION, SWAP_INTERPRETATION and"
            + " DELETE_INTERPRETATION";
    public static final String AUDIT_DATE_DESCRIPTION = "Date of the audit";

    public static final String SOFTWARE_NAME = "Software name";
    public static final String SOFTWARE_VERSION = "Software version";
    public static final String SOFTWARE_REPOSITORY = "Software repository";
    public static final String SOFTWARE_COMMIT = "Software commit";
    public static final String SOFTWARE_WEBSITE = "Software website";
    public static final String SOFTWARE_PARAMS = "Software params";

}