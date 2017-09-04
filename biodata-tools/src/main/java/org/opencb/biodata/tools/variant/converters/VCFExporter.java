package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.metadata.Individual;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.biodata.tools.variant.converters.avro.VariantDatasetMetadataToVCFHeaderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VCFExporter {

    private VariantDatasetMetadata metadata;
    private VCFHeader vcfHeader;

    // conversion: variant (AVRO) -> variant context (VCF), output stream and writer
    private VariantContextToAvroVariantConverter variantConverter;
    private OutputStream outputStream;
    private VariantContextWriter writer;

    // logger
    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    /**
     * Constructor from variant dataset metadata.
     *
     * @param metadata  Variant dataset metadata
     */
    public VCFExporter(VariantDatasetMetadata metadata) {
        this.metadata = metadata;

        // Build VCF header from variant dataset metadata
        VariantDatasetMetadataToVCFHeaderConverter headConverter = new VariantDatasetMetadataToVCFHeaderConverter();
        this.vcfHeader = headConverter.convert(metadata);
    }

    /**
     * Export a variant (AVRO model) to VCF file.
     *
     * @param variantAvro       Variant
     * @param writerOptions     Writing options
     * @param outPath           Output path (VCF file)
     */
    public void export(VariantAvro variantAvro, Options writerOptions, Path outPath) {
        export(new Variant(variantAvro), writerOptions, outPath);
    }

    /**
     * Export a variant to VCF file.
     *
     * @param variant           Variant
     * @param writerOptions     Writing options
     * @param outPath           Output path (VCF file)
     */
    public void export(Variant variant, Options writerOptions, Path outPath) {
        try {
            // prepare
            prepare(writerOptions, outPath);

            // writing variant
            VariantContext variantContext = variantConverter.from(variant);
            System.out.println(variantContext.toString());
            writer.add(variantContext);

            // close everything
            close();
        } catch (Exception e) {
            logger.error("Error exporting VCF data: {}", e.getMessage());
        }
    }


    /**
     * Export variants (AVRO model) in VCF file format from a variant list.
     *
     * @param variants          Variant list
     * @param writerOptions     Writing options
     * @param outPath           Output path (VCF file)
     */
    public void export(List<VariantAvro> variants, Options writerOptions, Path outPath) {
        try {
            // prepare
            prepare(writerOptions, outPath);

            // main loop (from list)
            for (VariantAvro variantAvro: variants) {
                Variant variant = new Variant(variantAvro);
                VariantContext variantContext = variantConverter.from(variant);
                System.out.println(variantContext.toString());
                writer.add(variantContext);
            }

            // close everything
            close();
        } catch (Exception e) {
            logger.error("Error exporting VCF data: {}", e.getMessage());
        }
    }

    /**
     * Export variants (AVRO model) in VCF file format from a variant iterator.
     *
     * @param iterator          Variant iterator
     * @param writerOptions     Writing options
     * @param outPath           Output path (VCF file)
     */
    public void export(Iterator<VariantAvro> iterator, Options writerOptions, Path outPath) {
        try {
            // prepare
            prepare(writerOptions, outPath);

            // main loop (from iterator)
            while (iterator.hasNext()) {
                Variant variant = new Variant(iterator.next());
                VariantContext variantContext = variantConverter.from(variant);
                //System.out.println(variantContext.toString());
                writer.add(variantContext);
            }

            // close everything
            close();
        } catch (Exception e) {
            logger.error("Error exporting VCF data: {}", e.getMessage());
        }
    }

    private void prepare(Options writerOptions, Path outPath) throws FileNotFoundException {
        // TODO: VariantContextToAvroVariantConverter takes as input parameters sampleNames, sampleFormats and annotations
        // TODO (cont.): these parameters should be taken into account
        // conversion: variant (AVRO) -> variant context (VCF)

        // TODO: sampleNames should be taken from vcfHeader.getSampleNamesInOrder()
        // (snippet from VariantMetadataManager)
        List<String> sampleNames = new ArrayList<>();
        for (Individual individual : metadata.getIndividuals()) {
            for (Sample sample: individual.getSamples()) {
                sampleNames.add(sample.getId());
            }
        }
        variantConverter =
//                new VariantContextToAvroVariantConverter(metadata.getId(), Collections.emptyList(), Collections.emptyList());
//                new VariantContextToAvroVariantConverter(metadata.getId(), vcfHeader.getSampleNamesInOrder(), Collections.emptyList());
        new VariantContextToAvroVariantConverter(metadata.getId(), sampleNames, Collections.emptyList());

        // create the variant context writer
        outputStream = new FileOutputStream(outPath.toString());
        writer = VcfUtils.createVariantContextWriter(outputStream,
                vcfHeader.getSequenceDictionary(), writerOptions);

        // write VCF header
        writer.writeHeader(vcfHeader);
    }

    private void close() throws IOException {
        // close everything
        writer.close();
        outputStream.close();
    }
}
