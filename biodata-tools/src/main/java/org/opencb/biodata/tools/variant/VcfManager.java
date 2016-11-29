package org.opencb.biodata.tools.variant;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.tabix.TabixFormat;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.tools.variant.filters.VariantFilters;
import org.opencb.biodata.tools.variant.iterators.VariantContextToAvroVariantVcfIterator;
import org.opencb.biodata.tools.variant.iterators.VariantContextToProtoVariantVcfIterator;
import org.opencb.biodata.tools.variant.iterators.VariantContextVcfIterator;
import org.opencb.biodata.tools.variant.iterators.VcfIterator;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder.OutputType.BLOCK_COMPRESSED_VCF;

/**
 * Created by jtarraga on 29/11/16.
 */
public class VcfManager {
    private Path input;

    private Path dataPath;
    private Path indexPath;

    private VCFFileReader vcfReader;

    private static final int DEFAULT_MAX_NUM_RECORDS = 50000;

    public VcfManager(Path input) throws IOException {
        FileUtils.checkFile(input);
        this.input = input;
        this.dataPath = input;
    }

    /**
     * Creates a index file for the VCF input file.
     * @return The path of the index file.
     * @throws IOException
     */
    public Path createIndex() throws IOException {
        return createIndex(null);
    }

    /**
     * Creates a VCF index file.
     * @param indexPath The index created.
     * @return
     * @throws IOException
     */
    public Path createIndex(Path indexPath) throws IOException {
        VCFFileReader reader = new VCFFileReader(input.toFile(), false);

        // compress vcf if necessary, .gz
        if (!dataPath.getFileName().endsWith(".gz")) {
            dataPath = Paths.get(input + ".gz");
            System.out.println("Creating compressed file: " + dataPath);
            VariantContextWriter writer = new VariantContextWriterBuilder()
                    .setOutputFile(dataPath.toFile())
                    .setOutputFileType(BLOCK_COMPRESSED_VCF)
                    .build();

            writer.writeHeader(reader.getFileHeader());
            for (VariantContext vc: reader) {
                if (vc != null) {
                    writer.add(vc);
                }
            }
            writer.close();
        }


        // and then create the tabix index, .tbi
        if (indexPath == null) {
            this.indexPath = Paths.get(dataPath + ".tbi");
        } else {
            this.indexPath = indexPath;
        }


        System.out.println("Creating index file: " + this.indexPath);
        IndexFactory.createTabixIndex(dataPath.toFile(), new VCFCodec(),
                TabixFormat.VCF, reader.getFileHeader().getSequenceDictionary())
                .write(this.indexPath.toFile());

        reader.close();

        return this.indexPath;
    }

    /**
     * This method aims to provide a very simple, safe and quick way of accessing to a small fragment of the VCF file.
     * This must not be used in production for reading big data files. It returns a maximum of 10,000 variant records.
     *
     * @param region @return
     * @throws IOException
     */
    public List<VariantContext> query(Region region) throws Exception {
        return query(region, null, new VariantOptions(), VariantContext.class);
    }

    public List<VariantContext> query(Region region, VariantOptions options) throws Exception {
        return query(region, null, options, VariantContext.class);
    }

    public List<VariantContext> query(Region region, VariantFilters<VariantContext> filters, VariantOptions options) throws Exception {
        return query(region, filters, options, VariantContext.class);
    }

//    public List<VariantContext> query() throws Exception {
//        return query(null, null, new VariantOptions(), VariantContext.class);
//    }

    public List<VariantContext> query(VariantFilters<VariantContext> filters) throws Exception {
        return query(null, filters, null, VariantContext.class);
    }

    public List<VariantContext> query(VariantFilters<VariantContext> filters, VariantOptions options) throws Exception {
        return query(null, filters, options, VariantContext.class);
    }

    public <T> List<T> query(VariantFilters<VariantContext> filters, VariantOptions options, Class<T> clazz) throws Exception {
        return query(null, filters, options, clazz);
    }

    public <T> List<T> query(Region region, VariantFilters<VariantContext> filters, VariantOptions options, Class<T> clazz) throws Exception {
        open();

        if (options == null) {
            options = new VariantOptions();
        }

        // Number of returned records, if not set then DEFAULT_MAX_NUM_RECORDS is returned
        int maxNumberRecords = DEFAULT_MAX_NUM_RECORDS;
        if (options.getLimit() > 0) {  // && VariantOptions.getLimit() <= DEFAULT_MAX_NUM_RECORDS
            maxNumberRecords = options.getLimit();
        }

        List<T> results = new ArrayList<>(maxNumberRecords);
        VcfIterator<T> vcfIterator = (region != null)
                ? iterator(region, filters, clazz)
                : iterator(filters, clazz);

        while (vcfIterator.hasNext() && results.size() < maxNumberRecords) {
            results.add(vcfIterator.next());
        }
        vcfIterator.close();
        return results;
    }

    /**
     * This method aims to provide a very simple, safe and quick way to iterate through VCF files.
     *
     */

    public VcfIterator<VariantContext> iterator() {
        return iterator(null, VariantContext.class);
    }

    public VcfIterator<VariantContext> iterator(VariantFilters<VariantContext> filters) {
        return iterator(filters, VariantContext.class);
    }

    public <T> VcfIterator<T> iterator(VariantFilters<VariantContext> filters, Class<T> clazz) {
        open();

        CloseableIterator<VariantContext> variantContextIterator = vcfReader.iterator();
        return getVariantIterator(filters, clazz, variantContextIterator);
    }

    public VcfIterator<VariantContext> iterator(Region region) {
        return iterator(region, null, VariantContext.class);
    }

    public VcfIterator<VariantContext> iterator(Region region, VariantFilters<VariantContext> filters) {
        return iterator(region, filters, VariantContext.class);
    }

    public <T> VcfIterator<T> iterator(Region region, VariantFilters<VariantContext> filters, Class<T> clazz) {
        open();

        CloseableIterator<VariantContext> variantContextIterator =
                vcfReader.query(region.getChromosome(), region.getStart(), region.getEnd());
        return getVariantIterator(filters, clazz, variantContextIterator);
    }

    private <T> VcfIterator<T> getVariantIterator(VariantFilters<VariantContext> filters, Class<T> clazz,
                                                  CloseableIterator<VariantContext> variantContextIterator) {
        if (Variant.class == clazz) { // AVRO
            return (VcfIterator<T>) new VariantContextToAvroVariantVcfIterator(variantContextIterator, filters);
        } else if (VariantProto.Variant.class == clazz) { // PROTOCOL BUFFER
            return (VcfIterator<T>) new VariantContextToProtoVariantVcfIterator(variantContextIterator, filters);
        } else if (VariantContext.class == clazz) {
            return (VcfIterator<T>) new VariantContextVcfIterator(variantContextIterator, filters);
        } else {
            throw new IllegalArgumentException("Unknown variant class " + clazz);
        }
    }

    public void close() throws IOException {
        if (vcfReader != null) {
            vcfReader.close();
        }
    }

    private void open() {
        if (vcfReader == null) {
            if (indexPath != null) {
                vcfReader = new VCFFileReader(dataPath.toFile(), indexPath.toFile());
            } else {
                vcfReader = new VCFFileReader(dataPath.toFile());
            }
        }
    }

}
