package org.opencb.biodata.tools.variant.converters;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.biodata.tools.variant.converters.avro.VariantDatasetMetadataToVCFHeaderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;

public class VCFExporter {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    public void export(VariantDatasetMetadata metadata, Iterator<VariantAvro> iterator, Options writerOptions, Path outPath) {
        // Build VCF header from variant dataset metadata
        VariantDatasetMetadataToVCFHeaderConverter headConverter = new VariantDatasetMetadataToVCFHeaderConverter();
        VCFHeader vcfHeader = headConverter.convert(metadata);

        // conversion: variant (AVRO) -> variant context (VCF)
        VariantContextToAvroVariantConverter variantConverter =
                new VariantContextToAvroVariantConverter(metadata.getId(), Collections.emptyList(), Collections.emptyList());

        try {
            // create the variant context writer
            OutputStream outputStream = new FileOutputStream(outPath.toString());
            VariantContextWriter writer = VcfUtils.createVariantContextWriter(outputStream,
                    vcfHeader.getSequenceDictionary(), writerOptions);

            // write VCF header
            writer.writeHeader(vcfHeader);

            while (iterator.hasNext()) {
                Variant variant = new Variant(iterator.next());
                VariantContext variantContext = variantConverter.from(variant);
                System.out.println(variantContext.toString());
                writer.add(variantContext);
            }


            // close everything
            writer.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("Error exporting VCF data: {}", e.getMessage());
        }

/*
        // create VCF header by getting information from metadata or study configuration
        List<String> cohortNames = null;
        List<String> annotations = null;
        List<String> formatFields = null;
        List<String> formatFieldsType = null;
        List<String> formatFieldsDescr = null;
        List<String> sampleNames = null;
        Function<String, String> converter = null;

        VCFHeader vcfHeader = VcfUtils.createVCFHeader(cohortNames, annotations, formatFields,
                formatFieldsType, formatFieldsDescr, sampleNames, converter);


        // create the variant context writer
        OutputStream outputStream = new FileOutputStream(exportVariantsCommandOptions.outFilename);
        Options writerOptions = null;
        VariantContextWriter writer = VcfUtils.createVariantContextWriter(outputStream,
                vcfHeader.getSequenceDictionary(), writerOptions);

        // write VCF header
        writer.writeHeader(vcfHeader);

        // TODO: get study id/name
        VariantContextToAvroVariantConverter variantContextToAvroVariantConverter =
                new VariantContextToAvroVariantConverter(0, Collections.emptyList(), Collections.emptyList());
        VariantDBIterator iterator = variantStorageEngine.iterator(query, options);
        while (iterator.hasNext()) {
            Variant variant = iterator.next();
            VariantContext variantContext = variantContextToAvroVariantConverter.from(variant);
            System.out.println(variantContext.toString());

            writer.add(variantContext);
        }

        // close
        writer.close();
        outputStream.close();
        */
    }
}
