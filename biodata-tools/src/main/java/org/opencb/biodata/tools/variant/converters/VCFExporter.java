package org.opencb.biodata.tools.variant.converters;

import com.google.common.collect.Iterators;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.converters.avro.VariantAvroToVariantContextConverter;
import org.opencb.biodata.tools.variant.converters.avro.VariantStudyMetadataToVCFHeaderConverter;
import org.opencb.biodata.tools.variant.metadata.VariantMetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class VCFExporter implements Closeable {

    private VariantStudyMetadata metadata;
    private VCFHeader vcfHeader;

    private List<String> formats;
    private List<String> annotations;

    // conversion: variant -> variant context, output stream and writer
    private VariantAvroToVariantContextConverter variantConverter;
    private OutputStream outputStream;
    private VariantContextWriter writer;

    // logger
    protected Logger logger = LoggerFactory.getLogger(this.getClass().toString());

    /**
     * Constructor from variant dataset metadata.
     *
     * @param metadata  Variant dataset metadata
     */
    public VCFExporter(VariantStudyMetadata metadata) {
        this.metadata = metadata;

        // Build VCF header from variant dataset metadata
        VariantStudyMetadataToVCFHeaderConverter headConverter = new VariantStudyMetadataToVCFHeaderConverter();
        this.vcfHeader = headConverter.convert(metadata);

        // default use
        sampleNames = VariantMetadataUtils.getSampleNames(metadata);
        formats = Arrays.asList("GT");
        annotations = Collections.emptyList();
    }

    /**
     * Export a variant to VCF file.
     *
     * @param variant           Variant
     */
    public void export(Variant variant) {
        export(Iterators.singletonIterator(variant));
    }

    /**
     * Export variants in VCF file format from a variant list.
     *
     * @param variants          Variant list
     */
    public void export(List<Variant> variants) {
        export(variants.iterator());
    }

    /**
     * Export variants in VCF file format from a variant iterator.
     *
     * @param iterator          Variant iterator
     */
    public void export(Iterator<Variant> iterator) {
        // sanity check
        if (writer == null) {
            logger.error("Error exporting VCF data: exporter must be opened");
            return;
        }

        // main loop (from iterator)
        while (iterator.hasNext()) {
            try {
                VariantContext variantContext = variantConverter.convert(iterator.next());
                //System.out.println(variantContext.toString());
                writer.add(variantContext);
            } catch (Exception e) {
                logger.error("Error exporting VCF data: {}", e.getMessage(), e);
            }
        }

//      // close everything
//      close();
    }

    public void open(Path outPath, Options... writerOptions) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outPath.toString());
        open(os, writerOptions);
    }

    public void open(OutputStream os, Options... writerOptions) {
        variantConverter = new VariantAvroToVariantContextConverter(metadata.getId(), sampleNames, formats, annotations);

        // create the variant context writer
        outputStream = Objects.requireNonNull(os);
        writer = VcfUtils.createVariantContextWriter(outputStream,
                vcfHeader.getSequenceDictionary(), writerOptions);

        // write VCF header
        writer.writeHeader(vcfHeader);
    }

    public void close() throws IOException {
        // close everything
        if (writer != null) {
            writer.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }

    private List<String> sampleNames;

    public List<String> getSampleNames() {
        return sampleNames;
    }

    public void setSampleNames(List<String> sampleNames) {
        this.sampleNames = sampleNames;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }
}
