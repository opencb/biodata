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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by imedina on 14/09/15.
 */
public class BamUtils {

    /**
     * Creates a BAM index file, the
     * @param input
     * @param outdir
     * @return
     * @throws IOException
     */
    public static Path createBai(Path input, Path outdir) throws IOException {
        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        SamReader reader = srf.open(SamInputResource.of(input.toFile()));

        Path bamIndexPath = outdir.resolve(input.getFileName().toString() + ".bai");
        BAMIndexer.createIndex(reader, bamIndexPath.toFile());

        return bamIndexPath;
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
