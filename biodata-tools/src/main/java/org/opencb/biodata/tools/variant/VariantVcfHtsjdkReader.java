package org.opencb.biodata.tools.variant;

import htsjdk.tribble.TribbleException;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.vcf4.FullVcfCodec;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantNormalizer;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.tools.variant.converter.VCFHeaderToAvroVcfHeaderConverter;
import org.opencb.biodata.tools.variant.converter.VariantContextToVariantConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Reads a VCF file using the library HTSJDK.
 *
 * Optionally, normalizes the variants.
 *
 * Created on 16/05/16.
 *
 * @author Jacobo Coll &lt;jVariantVcfHtsjdkReaderacobo167@gmail.com&gt;
 */
public class VariantVcfHtsjdkReader implements VariantReader {

    private final Logger logger = LoggerFactory.getLogger(VariantVcfHtsjdkReader.class);

    private final VariantSource source;
    private final InputStream inputStream;
    private final VariantNormalizer normalizer;
    private FullVcfCodec codec;
    private VCFHeader header;
    private VariantContextToVariantConverter converter;
    private LineIterator lineIterator;
    private List<String> headerLines;
    private Set<BiConsumer<String, RuntimeException>> malformHandlerSet = new HashSet<>();

    public VariantVcfHtsjdkReader(InputStream inputStream, VariantSource source) {
        this(inputStream, source, null);
    }

    public VariantVcfHtsjdkReader(InputStream inputStream, VariantSource source, VariantNormalizer normalizer) {
        this.source = source;
        this.inputStream = inputStream;
        this.normalizer = normalizer;
    }

    public VariantVcfHtsjdkReader registerMalformatedVcfHandler(BiConsumer<String, RuntimeException> handler) {
        this.malformHandlerSet.add(handler);
        return this;
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public boolean pre() {
        codec = new FullVcfCodec();
        lineIterator = codec.makeSourceFromStream(inputStream);

        // Read the header
        headerLines = new LinkedList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.peek();
            if (line.startsWith(VCFHeader.HEADER_INDICATOR)) {
                headerLines.add(line);
                lineIterator.next();
            } else {
                break;
            }
        }

        // Parse the header
        header = (VCFHeader) codec.readActualHeader(new LineIteratorImpl(new LineReader() {
            Iterator<String> iterator = headerLines.iterator();
            @Override
            public String readLine() throws IOException {
                if (iterator.hasNext()) {
                    return iterator.next();
                } else {
                    return null;
                }
            }
            @Override public void close() {}
        }));

        // Create converter and fill VariantSource
        converter = new VariantContextToVariantConverter(source.getStudyId(), source.getFileId(), header.getSampleNamesInOrder());
        source.setHeader(new VCFHeaderToAvroVcfHeaderConverter().convert(header));
        source.setSamples(header.getSampleNamesInOrder());
        return true;
    }

    @Override
    public List<Variant> read(int batchSize) {
        List<VariantContext> variantContexts = new ArrayList<>(batchSize);
        while (lineIterator.hasNext() && variantContexts.size() < batchSize) {
            String line = lineIterator.next();
            if (line.startsWith("#") || line.trim().isEmpty()) {
                continue;
            }
            try {
                variantContexts.add(codec.decode(line));
            } catch (TribbleException e) {
                if (e.getMessage().startsWith("The provided VCF file is malformed at approximately line number")) {
                    logMalformatedLine(line, e);
                } else {
                    throw e;
                }
            }
        }

        List<Variant> variants = converter.apply(variantContexts);

        if (normalizer != null) {
            variants = normalizer.apply(variants);
        }

        return variants;
    }

    private void logMalformatedLine(String line, RuntimeException error) {
        logger.warn(error.getMessage());
        for (BiConsumer<String, RuntimeException> consumer : this.malformHandlerSet) {
            consumer.accept(line, error);
        }
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    @Override
    public List<String> getSampleNames() {
        return header.getSampleNamesInOrder();
    }

    @Override
    public String getHeader() {
        return String.join("\n", headerLines);
    }

    public VariantSource getSource() {
        return source;
    }
}
