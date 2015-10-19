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

import htsjdk.samtools.*;
import htsjdk.samtools.seekablestream.SeekableStream;
import htsjdk.samtools.seekablestream.SeekableStreamFactory;
import htsjdk.samtools.util.Log;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 14/09/15.
 */
public class AlignmentFileUtils {


    /**
     * Creates a index file for the BAM or CRAM input file.
     * @param input A sorted BAM or CRAM file
     * @return The path of the index file.
     * @throws IOException
     */
    public static Path createIndex(Path input) throws IOException {
        FileUtils.checkFile(input);
        Path indexPath = input.getParent().resolve(input.getFileName().toString() + ".bai");
        return createIndex(input, indexPath);
    }

    /**
     * Creates a BAM/CRAM index file.
     * @param input The BAM or CRAM file to be indexed.
     * @param outputIndex The index created.
     * @return
     * @throws IOException
     */
    public static Path createIndex(Path input, Path outputIndex) throws IOException {
        FileUtils.checkFile(input);
        FileUtils.checkDirectory(outputIndex.toAbsolutePath().getParent(), true);

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        try (SamReader reader = srf.open(SamInputResource.of(input.toFile()))) {

            // Files need to be sorted by coordinates to crete the index
            SAMFileHeader.SortOrder sortOrder = reader.getFileHeader().getSortOrder();
            if (!sortOrder.equals(SAMFileHeader.SortOrder.coordinate)) {
                throw new IOException("Expected sorted file. File '" + input.toString()
                        + "' is not sorted by coordinates (" + sortOrder.name() + ")");
            }

            if (reader.type().equals(SamReader.Type.BAM_TYPE)) {
                BAMIndexer.createIndex(reader, outputIndex.toFile(), Log.getInstance(AlignmentFileUtils.class));
            } else {
                if (reader.type().equals(SamReader.Type.CRAM_TYPE)) {
                    // TODO This really needs to be tested!
                    SeekableStream streamFor = SeekableStreamFactory.getInstance().getStreamFor(input.toString());
                    CRAMIndexer.createIndex(streamFor, outputIndex.toFile(), Log.getInstance(AlignmentFileUtils.class));
                } else {
                    throw new IOException("This is not a BAM or CRAM file. SAM files cannot be indexed");
                }
            }
        }
        return outputIndex;
    }

    /**
     * This method aims to provide a very simple, safe and quick way of accessing to a small fragment of the BAM/CRAM file.
     * This must not be used in production for reading big data files. It returns a maximum of 10,000 SAM records.
     * @param input
     * @param chromosome
     * @param start
     * @param end
     * @param contained
     * @param maxNumberRecords
     * @return
     * @throws IOException
     */
    public static List<SAMRecord> query(Path input, String chromosome, int start, int end, boolean contained, int maxNumberRecords) throws IOException {
        FileUtils.checkFile(input);

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        List<SAMRecord> results;
        try (SamReader reader = srf.open(SamInputResource.of(input.toFile()))) {

            // no more than 10,000 records can be returned
            maxNumberRecords = (maxNumberRecords > 0 && maxNumberRecords <= 10000) ? maxNumberRecords : 10000;
            results = new ArrayList<>(maxNumberRecords);
            SAMRecordIterator samRecordIterator = reader.query(chromosome, start, end, contained);
            while (samRecordIterator.hasNext()) {
                results.add(samRecordIterator.next());
            }
            samRecordIterator.close();
        }
        return results;
    }


    public static SAMFileHeader getFileHeader(Path input) throws IOException {
        FileUtils.checkFile(input);

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        SamReader reader = srf.open(SamInputResource.of(input.toFile()));
        SAMFileHeader fileHeader = reader.getFileHeader();
        reader.close();

        return fileHeader;
    }

    public static String getFileHeaderAsString(Path input) throws IOException {
        return getFileHeader(input).toString();
    }

    /**
     * Check if the file is a sorted binary bam file.
     * @param is            Bam InputStream
     * @param bamFileName   Bam FileName
     * @throws IOException
     */
    public static void checkBamOrCramFile(InputStream is, String bamFileName) throws IOException {
        checkBamOrCramFile(is, bamFileName, true);
    }

    /**
     * Check if the file is a sorted binary bam file.
     * @param is            Bam InputStream
     * @param bamFileName   Bam FileName
     * @param checkSort
     * @throws IOException
     */
    public static void checkBamOrCramFile(InputStream is, String bamFileName, boolean checkSort) throws IOException {
        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);

        SamReader reader = srf.open(SamInputResource.of(is));
        SAMFileHeader fileHeader = reader.getFileHeader();
        SAMFileHeader.SortOrder sortOrder = fileHeader.getSortOrder();
        reader.close();

        if (reader.type().equals(SamReader.Type.SAM_TYPE)) {
            throw new IOException("Expected binary SAM file. File " + bamFileName + " is not binary.");
        }

        if (checkSort) {
            switch (sortOrder) {
                case coordinate:
                    break;
                case queryname:
                case unsorted:
                default:
                    throw new IOException("Expected sorted file. File '" + bamFileName + "' is not sorted by coordinates(" + sortOrder.name() + ")");
            }
        }
    }
}
