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

package org.opencb.biodata.tools.alignment;

import ga4gh.Reads;
import htsjdk.samtools.*;
import htsjdk.samtools.seekablestream.SeekableStream;
import htsjdk.samtools.seekablestream.SeekableStreamFactory;
import htsjdk.samtools.util.Log;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.alignment.filtering.AlignmentFilter;
import org.opencb.biodata.tools.alignment.iterators.AlignmentIterator;
import org.opencb.biodata.tools.alignment.iterators.AvroIterator;
import org.opencb.biodata.tools.alignment.iterators.ProtoIterator;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 14/09/15.
 */
public class AlignmentManager {

    private Path input;
    private SamReader samReader;
    private final int MAX_NUM_RECORDS = 50000;

    public AlignmentManager() {
    }

    public AlignmentManager(Path input) throws IOException {
        FileUtils.checkFile(input);
        this.input = input;

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        this.samReader = srf.open(SamInputResource.of(input.toFile()));
    }

    /**
     * Creates a index file for the BAM or CRAM input file.
     * @return The path of the index file.
     * @throws IOException
     */
    public Path createIndex() throws IOException {
        Path indexPath = input.getParent().resolve(input.getFileName().toString() + ".bai");
        return createIndex(indexPath);
    }

    /**
     * Creates a BAM/CRAM index file.
     * @param outputIndex The index created.
     * @return
     * @throws IOException
     */
    public Path createIndex(Path outputIndex) throws IOException {
        FileUtils.checkDirectory(outputIndex.toAbsolutePath().getParent(), true);

        SamReaderFactory srf = SamReaderFactory.make().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS);
        srf.validationStringency(ValidationStringency.LENIENT);
        try (SamReader reader = srf.open(SamInputResource.of(input.toFile()))) {

            // Files need to be sorted by coordinates to crete the index
            SAMFileHeader.SortOrder sortOrder = reader.getFileHeader().getSortOrder();
            if (!sortOrder.equals(SAMFileHeader.SortOrder.coordinate)) {
                throw new IOException("Expected sorted file. File '" + input.toString()
                        + "' is not sorted by coordinates (" + sortOrder.name() + ")");
            }

            if (reader.type().equals(SamReader.Type.BAM_TYPE)) {
                BAMIndexer.createIndex(reader, outputIndex.toFile(), Log.getInstance(AlignmentManager.class));
            } else {
                if (reader.type().equals(SamReader.Type.CRAM_TYPE)) {
                    // TODO This really needs to be tested!
                    SeekableStream streamFor = SeekableStreamFactory.getInstance().getStreamFor(input.toString());
                    CRAMIndexer.createIndex(streamFor, outputIndex.toFile(), Log.getInstance(AlignmentManager.class));
                } else {
                    throw new IOException("This is not a BAM or CRAM file. SAM files cannot be indexed");
                }
            }
        }
        return outputIndex;
    }

    // TODO
    /*
     * This method aims to provide a very simple, safe and quick way of accessing to a small fragment of the BAM/CRAM file.
     * This must not be used in production for reading big data files. It returns a maximum of 10,000 SAM records.
     * @param chromosome
     * @param start
     * @param end
     * @param contained
     * @param maxNumberRecords
     * @return
     * @throws IOException
     */
    public List<ReadAlignment> query(String chromosome, int start, int end) throws Exception {
        return query(chromosome, start, end, new AlignmentOptions(), ReadAlignment.class);
    }

    public List<ReadAlignment> query(String chromosome, int start, int end, AlignmentOptions options) throws Exception {
        return query(chromosome, start, end, options, ReadAlignment.class);
    }

    public <T> List<T> query(String chromosome, int start, int end, AlignmentOptions options, Class<T> clazz)
            throws Exception {
        int maxNumberRecords = (options.getMaxNumberRecords() > 0 && options.getMaxNumberRecords() <= MAX_NUM_RECORDS)
                ? options.getMaxNumberRecords() : MAX_NUM_RECORDS;
        List<T> results = new ArrayList<>(maxNumberRecords);
        AlignmentIterator<T> alignmentIterator = iterator(chromosome, start, end, options, null, clazz);
        while (alignmentIterator.hasNext() && results.size() < maxNumberRecords) {
            results.add(alignmentIterator.next());
        }
        alignmentIterator.close();
        return results;
    }

    public AlignmentIterator<ReadAlignment> iterator(String chromosome, int start, int end) {
        return iterator(chromosome, start, end, new AlignmentOptions(), null, ReadAlignment.class);
    }

    public AlignmentIterator<ReadAlignment> iterator(String chromosome, int start, int end, AlignmentOptions options) {
        return iterator(chromosome, start, end, options, null, ReadAlignment.class);
    }

    public AlignmentIterator<ReadAlignment> iterator(String chromosome, int start, int end, AlignmentOptions options,
                                                     AlignmentFilter filters) {
        return iterator(chromosome, start, end, options, filters, ReadAlignment.class);
    }

    public <T> AlignmentIterator<T> iterator(String chromosome, int start, int end, AlignmentOptions options, AlignmentFilter filters,
                                             Class<T> clazz) {
        SAMRecordIterator samRecordIterator = samReader.query(chromosome, start, end, options.isContained());
        if (ReadAlignment.class == clazz) { // AVRO
            return (AlignmentIterator<T>) new AvroIterator(samRecordIterator, filters);
        } else if (Reads.ReadAlignment.class == clazz) { // PROTOCOL BUFFER
            return (AlignmentIterator<T>) new ProtoIterator(samRecordIterator, filters);
        } else if (SAMRecord.class == clazz) {
            // TODO: Add new java class
            return (AlignmentIterator<T>) samRecordIterator;
        } else {
            throw new IllegalArgumentException("Unknown alignment class " + clazz);
        }
//        return alignmentIterator.setFilters(filters.getFilters());
    }
}
