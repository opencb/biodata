package org.opencb.biodata.tools.variant.converters;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;

import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Created by jtarraga on 08/03/17.
 */
public class VariantConverterUtils {
    /**
     * Create a VCFHeader to use with VariantContextWriter.
     *
     * @param config                Converter configuration
     * @param converter             Function to convert sample names
     * @return                      The VCF header
     */
    public static VCFHeader createVCFHeader(VariantConverterConfiguration config,
                                            Function<String, String> converter) {
        List<String> cohortNames = new ArrayList<>(config.getCohortNames());
        List<String> annotations = new ArrayList<>(config.getAnnotations());
        List<String> sampleNames = new ArrayList<>(config.getSampleNames());

        List<String> formatFields = new ArrayList<>();
        List<String> formatFieldsType = new ArrayList<>();
        List<String> formatFieldsDescr = new ArrayList<>();
        for (VariantConverterConfiguration.FormatField format: config.getFormats()) {
            formatFields.add(format.name);
            formatFieldsType.add(format.type);
            formatFieldsDescr.add(format.desc);
        }

        return createVCFHeader(cohortNames, annotations, formatFields, formatFieldsType, formatFieldsDescr,
                sampleNames, converter);
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
                    meta.add(new VCFInfoHeaderLine(VCFConstants.ALLELE_COUNT_KEY, VCFHeaderLineCount.A,
                            VCFHeaderLineType.Integer, "Total number of alternate alleles in called genotypes,"
                            + " for each ALT allele, in the same order as listed"));
                    meta.add(new VCFInfoHeaderLine(VCFConstants.ALLELE_FREQUENCY_KEY, VCFHeaderLineCount.A,
                            VCFHeaderLineType.Float, "Allele Frequency, for each ALT allele, calculated from AC and AN, in the range (0,1),"
                            + " in the same order as listed"));
                    meta.add(new VCFInfoHeaderLine(VCFConstants.ALLELE_NUMBER_KEY, 1,
                            VCFHeaderLineType.Integer, "Total number of alleles in called genotypes"));
                } else {
//            header.addMetaDataLine(new VCFInfoHeaderLine(cohortName + VCFConstants.ALLELE_COUNT_KEY, VCFHeaderLineCount.A,
//                    VCFHeaderLineType.Integer, "Total number of alternate alleles in called genotypes,"
//                    + " for each ALT allele, in the same order as listed"));
                    meta.add(new VCFInfoHeaderLine(cohortName + "_" + VCFConstants.ALLELE_FREQUENCY_KEY, VCFHeaderLineCount.A,
                            VCFHeaderLineType.Float,
                            "Allele frequency in the " + cohortName + " cohort calculated from AC and AN, in the range (0,1),"
                                    + " in the same order as listed"));
//            header.addMetaDataLine(new VCFInfoHeaderLine(cohortName + VCFConstants.ALLELE_NUMBER_KEY, 1, VCFHeaderLineType.Integer,
//                    "Total number of alleles in called genotypes"));
                }
            }

            // annotations
            if (annotations != null && annotations.size() > 0) {
                meta.add(new VCFInfoHeaderLine("ANN", 1, VCFHeaderLineType.String, "Consequence annotations from CellBase. "
                        + "Format: " +   String.join("|", annotations)));
            }

        /* FORMAT */
//        meta.add(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype"));
//        meta.add(new VCFFormatHeaderLine("PF", 1, VCFHeaderLineType.Integer,
//                "Variant was PASS (1) filter in original vcf"));
            for (int i = 0; i < formatFields.size(); i++) {
                meta.add(new VCFFormatHeaderLine(formatFields.get(i), 1,
                        VCFHeaderLineType.valueOf(formatFieldsType.get(i)), formatFieldsDescr.get(i)));
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
                                                                  Options options) {
        // setup writer
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setOutputStream(outputStream)
                .setReferenceDictionary(sequenceDictionary)
                .unsetOption(Options.INDEX_ON_THE_FLY);

        // options
        if (options != null) {
            builder.setOption(options);
        }

        return builder.build();
    }
}
