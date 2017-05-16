/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.variant.vcf4;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VcfUtils {

    public static final String ANNOTATION_INFO_KEY = "CT";
    public static final List<String> ANNOTATION_INFO_VALUES = Arrays.asList("ALLELE", "GENE", "ENSEMBL_GENE", "ENSEMBL_TRANSCRIPT",
            "BIOTYPE", "SO", "PROTEIN_POS", "PROTEIN_ALLELES", "SIFT", "POLYPHEN");
    public static final String POPFREQ_INFO_KEY = "POPFREQ";
    public static final String STATS_INFO_KEY = "COHORT";

    public static final List<String> DEFAULT_SAMPLE_FORMAT = Arrays.asList("GT", "AD", "DP", "GQ", "PL");

    public static String getInfoColumn(StudyEntry file) {
        StringBuilder info = new StringBuilder();

        for (Map.Entry<String, String> entry : file.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (!key.equalsIgnoreCase("QUAL") && !key.equalsIgnoreCase("FILTER")) {
                info.append(key);

                String value = entry.getValue();
                if (value.length() > 0) {
                    info.append("=");
                    info.append(value);
                }

                info.append(";");
            }
        }

        return info.toString().isEmpty() ? "." : info.toString();
    }

    public static String getInfoColumn(Variant variant, String fileId, String studyId) {
        return VcfUtils.getInfoColumn(variant.getSourceEntry(fileId, studyId));
    }

    public static String getJoinedSampleFields(StudyEntry file, String sampleName) {
        Map<String, String> data = file.getSampleData(sampleName);
        if (data == null) {
            return "";
        }

        StringBuilder info = new StringBuilder();
        for (String formatField : file.getFormatAsString().split(":")) {
            info.append(data.get(formatField)).append(":");
        }

        return info.toString().isEmpty() ? "." : info.toString();
    }

    public static String getJoinedSampleFields(Variant variant, StudyEntry file, String sampleName) {
        return VcfUtils.getJoinedSampleFields(variant.getSourceEntry(file.getFileId(), file.getStudyId()), sampleName);
    }

    /**
     * Create a VCFHeader to use with VariantContextWriter.
     *
     * @param cohortNames           List of cohort names
     * @param annotations           List of annotations to include in the header
     * @param formatFields          List of formats
     * @param formatFieldsType      List of format types
     * @param formatFieldsDescr     List of format descriptions
     * @param sampleNames           List of sample names
     * @param converter             Function to convert sample names
     * @return                      The VCF header
     */
    public static VCFHeader createVCFHeader(List<String> cohortNames, List<String> annotations,
                                            List<String> formatFields, List<String> formatFieldsType,
                                            List<String> formatFieldsDescr, List<String> sampleNames,
                                            Function<String, String> converter) {

        LinkedHashSet<VCFHeaderLine> meta = new LinkedHashSet<>();

        // sample name management
        AtomicReference<Function<String, String>> sampleNameConverter = new AtomicReference<>(s -> s);
        if (converter != null) {
            sampleNameConverter.set(converter);
        }
        Map<String, String> sampleNameMapping = new ConcurrentHashMap<>();
//        sampleNameMapping.putAll(sampleNames.stream().collect(
//                Collectors.toMap(s -> s, s -> sampleNameConverter.get().apply(s))));
//        List<String> names = sampleNames.stream().map(s -> sampleNameMapping.get(s)).collect(Collectors.toList());
        List<String> names = sampleNames;

        /* FILTER */
        meta.add(new VCFFilterHeaderLine("PASS", "Valid variant"));
        meta.add(new VCFFilterHeaderLine(".", "No FILTER info"));

        /* INFO */
        // cohort info
        for (String cohortName : cohortNames) {
            if (cohortName.toUpperCase().equals("ALL")) {
//                meta.add(new VCFInfoHeaderLine(VCFConstants.ALLELE_COUNT_KEY, VCFHeaderLineCount.A,
//                        VCFHeaderLineType.Integer, "Total number of alternate alleles in called genotypes,"
//                        + " for each ALT allele, in the same order as listed"));
                meta.add(new VCFInfoHeaderLine(VCFConstants.ALLELE_FREQUENCY_KEY, VCFHeaderLineCount.A,
                        VCFHeaderLineType.Float, "Allele Frequency, for each ALT allele, calculated from AC and AN, in the range (0,1),"
                        + " in the same order as listed"));
//                meta.add(new VCFInfoHeaderLine(VCFConstants.ALLELE_NUMBER_KEY, 1,
//                        VCFHeaderLineType.Integer, "Total number of alleles in called genotypes"));
            } else {
                meta.add(new VCFInfoHeaderLine(cohortName + "_" + VCFConstants.ALLELE_FREQUENCY_KEY, VCFHeaderLineCount.A,
                        VCFHeaderLineType.Float,
                        "Allele frequency in the " + cohortName + " cohort calculated from AC and AN, in the range (0,1),"
                                + " in the same order as listed"));
            }
        }

        // annotations
        if (annotations != null && annotations.size() > 0) {
            meta.add(new VCFInfoHeaderLine(STATS_INFO_KEY, 1, VCFHeaderLineType.String, "Allele frequency "
                    + " for cohorts (separated by |), e.g.: ALL:0.0564705|MXL:0.0886758"));
            meta.add(new VCFInfoHeaderLine(ANNOTATION_INFO_KEY, 1, VCFHeaderLineType.String, "Consequence annotations (separated "
                    + " by &) from CellBase. Format: " +   String.join("|", ANNOTATION_INFO_VALUES)));
            meta.add(new VCFInfoHeaderLine(POPFREQ_INFO_KEY, 1, VCFHeaderLineType.String, "Alternate allele frequencies "
                    + " for study and population (separated by |), e.g.: 1kG_phase3_IBS:0.06542056|1kG_phase3_CEU:0.08585858"));
        }

        /* FORMAT */
//        meta.add(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype"));
//        meta.add(new VCFFormatHeaderLine("PF", 1, VCFHeaderLineType.Integer,
//                "Variant was PASS (1) filter in original vcf"));
        if (formatFields != null) {
            for (int i = 0; i < formatFields.size(); i++) {
                switch(formatFields.get(i)) {
                    case "GT":
                        meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1, VCFHeaderLineType.String, "Genotype"));
                        break;
                    case "AD":
                        meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1, VCFHeaderLineType.String, "Allelic depth"));
                        break;
                    case "DP":
                        meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1, VCFHeaderLineType.Integer, "Read depth"));
                        break;
                    case "GQ":
                        meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1, VCFHeaderLineType.Integer, "Genotype quality"));
                        break;
                    case "PL":
                        meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1, VCFHeaderLineType.Integer,
                                "Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification"));
                        break;
                    default:
                        meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1, VCFHeaderLineType.valueOf(formatFieldsType.get(i)), formatFieldsDescr.get(i)));
                        break;
                }
            }
        }

        return new VCFHeader(meta, names);
    }

    /**
     * Create a VariantContextWriter.
     *
     * @param outputStream              Output stream to write.
     * @param sequenceDictionary        SAM sequence directory (it can be got from VCFHeader)
     * @param options                   Writer options
     * @return                          The variant context writer
     */
    public static VariantContextWriter createVariantContextWriter(OutputStream outputStream,
                                                                  SAMSequenceDictionary sequenceDictionary,
                                                                  Options ...options) {
        // setup writer
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setOutputStream(outputStream)
                .setReferenceDictionary(sequenceDictionary)
                .setOption(Options.WRITE_FULL_FORMAT_FIELD)
                .unsetOption(Options.INDEX_ON_THE_FLY);

        // options
        if (options != null) {
            for (Options option : options) {
                builder.setOption(option);
            }
        }

        return builder.build();
    }

}
