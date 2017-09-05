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

package org.opencb.biodata.tools.variant;

import htsjdk.tribble.readers.LineIterator;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.vcf4.FullVcfCodec;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.converters.avro.VCFHeaderToVariantFileHeaderConverter;
import org.opencb.biodata.tools.variant.converters.avro.VariantContextToVariantConverter;
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

    private final VariantStudyMetadata metadata;
    private final VariantFileMetadata fileMetadata;
    private final InputStream inputStream;
    private final VariantNormalizer normalizer;
    private FullVcfCodec codec;
    private VCFHeader header;
    private VariantContextToVariantConverter converter;
    private LineIterator lineIterator;
    private List<String> headerLines;
    private Set<BiConsumer<String, RuntimeException>> malformHandlerSet = new HashSet<>();
    private boolean failOnError = false;

    public VariantVcfHtsjdkReader(InputStream inputStream, VariantStudyMetadata metadata) {
        this(inputStream, metadata, null);
    }

    public VariantVcfHtsjdkReader(InputStream inputStream, VariantStudyMetadata metadata, VariantNormalizer normalizer) {
        this.metadata = metadata;
        this.fileMetadata = new VariantFileMetadata(metadata.getFiles().get(0));
        this.inputStream = inputStream;
        this.normalizer = normalizer;
    }

    public VariantVcfHtsjdkReader registerMalformatedVcfHandler(BiConsumer<String, RuntimeException> handler) {
        this.malformHandlerSet.add(handler);
        return this;
    }

    public VariantVcfHtsjdkReader setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
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

        // Create converters and fill VariantSource
        converter = new VariantContextToVariantConverter(metadata.getId(), metadata.getId(), header.getSampleNamesInOrder());
        fileMetadata.setHeader(new VCFHeaderToVariantFileHeaderConverter().convert(header));
        fileMetadata.setSampleIds(header.getSampleNamesInOrder());
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
            } catch (RuntimeException e) {
                logMalformatedLine(line, e);
                if (failOnError) {
                    throw e;
                }
//                if (e.getMessage().startsWith("The provided VCF file is malformed at approximately line number")) {
//                } else {
//                    throw e;
//                }
            }
        }

        List<Variant> variants = converter.apply(variantContexts);

        if (normalizer != null) {
            variants = normalizer.apply(variants);
        }

        return variants;
    }

    private void logMalformatedLine(String line, RuntimeException exception) {
        logger.warn(exception.getMessage());
        for (BiConsumer<String, RuntimeException> consumer : this.malformHandlerSet) {
            consumer.accept(line, exception);
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

    @Override
    public VariantFileMetadata getVariantFileMetadata() {
        return fileMetadata;
    }

    @Deprecated
    public VariantFileMetadata getMetadata() {
        return getVariantFileMetadata();
    }
}
