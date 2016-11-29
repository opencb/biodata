package org.opencb.biodata.tools.variant;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.tools.variant.filters.VariantFilters;
import org.opencb.biodata.tools.variant.iterators.VariantContextToAvroVariantVcfIterator;
import org.opencb.biodata.tools.variant.iterators.VariantContextToProtoVariantVcfIterator;
import org.opencb.biodata.tools.variant.iterators.VariantContextVcfIterator;
import org.opencb.biodata.tools.variant.iterators.VcfIterator;
import org.opencb.commons.utils.FileUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtarraga on 29/11/16.
 */
public class VcfManager {
    private Path input;
    private VCFFileReader vcfReader;

    private static final int DEFAULT_MAX_NUM_RECORDS = 50000;

    public VcfManager() {
    }

    public VcfManager(Path input) throws IOException {
        FileUtils.checkFile(input);
        this.input = input;

        this.vcfReader = new VCFFileReader(input.toFile());
    }

    /**
     * Creates a index file for the VCF input file.
     * @return The path of the index file.
     * @throws IOException
     */
    public Path createIndex() throws IOException {
        Path indexPath = input.getParent().resolve(input.getFileName().toString() + ".gz.tbi");
        return createIndex(indexPath);
    }

    /**
     * Creates a VCF index file.
     * @param outputIndex The index created.
     * @return
     * @throws IOException
     */
    public Path createIndex(Path outputIndex) throws IOException {
        // first, sort

        // compress, gz
        //BlockCompressedOutputStream(indexFile))

        // and then create the tabix index, .tbi
        //IndexFactory.createTabixIndex()

//        FileUtils.checkDirectory(outputIndex.toAbsolutePath().getParent(), true);
//
//        SamReaderFactory srf = SamReaderFactory.make().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS);
//        srf.validationStringency(ValidationStringency.LENIENT);
//        try (SamReader reader = srf.open(SamInputResource.of(input.toFile()))) {
//
//            // Files need to be sorted by coordinates to create the index
//            SAMFileHeader.SortOrder sortOrder = reader.getFileHeader().getSortOrder();
//            if (!sortOrder.equals(SAMFileHeader.SortOrder.coordinate)) {
//                throw new IOException("Expected sorted file. File '" + input.toString()
//                        + "' is not sorted by coordinates (" + sortOrder.name() + ")");
//            }
//
//            if (reader.type().equals(SamReader.Type.BAM_TYPE)) {
//                BAMIndexer.createIndex(reader, outputIndex.toFile(), Log.getInstance(BamManager.class));
//            } else {
//                if (reader.type().equals(SamReader.Type.CRAM_TYPE)) {
//                    // TODO This really needs to be tested!
//                    SeekableStream streamFor = SeekableStreamFactory.getInstance().getStreamFor(input.toString());
//                    CRAMBAIIndexer.createIndex(streamFor, outputIndex.toFile(), Log.getInstance(BamManager.class),
//                            ValidationStringency.DEFAULT_STRINGENCY);
//                } else {
//                    throw new IOException("This is not a BAM or CRAM file. SAM files cannot be indexed");
//                }
//            }
//        }
        return outputIndex;
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
                ? iterator(region, filters, options, clazz)
                : iterator(filters, options, clazz);

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
        return iterator(null, new VariantOptions(), VariantContext.class);
    }

    public VcfIterator<VariantContext> iterator(VariantOptions options) {
        return iterator(null, options, VariantContext.class);
    }

    public VcfIterator<VariantContext> iterator(VariantFilters<VariantContext> filters, VariantOptions options) {
        return iterator(filters, options, VariantContext.class);
    }

    public <T> VcfIterator<T> iterator(VariantFilters<VariantContext> filters, VariantOptions options, Class<T> clazz) {
        if (options == null) {
            options = new VariantOptions();
        }
        CloseableIterator<VariantContext> variantContextIterator = vcfReader.iterator();
        return getVariantIterator(filters, clazz, variantContextIterator);
    }

    public VcfIterator<VariantContext> iterator(Region region) {
        return iterator(region, null, new VariantOptions(), VariantContext.class);
    }

    public VcfIterator<VariantContext> iterator(Region region, VariantOptions options) {
        return iterator(region, null, options, VariantContext.class);
    }

    public VcfIterator<VariantContext> iterator(Region region, VariantFilters<VariantContext> filters, VariantOptions options) {
        return iterator(region, filters, options, VariantContext.class);
    }

    public <T> VcfIterator<T> iterator(Region region, VariantFilters<VariantContext> filters, VariantOptions VariantOptions, Class<T> clazz) {
        if (VariantOptions == null) {
            VariantOptions = new VariantOptions();
        }
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

}
