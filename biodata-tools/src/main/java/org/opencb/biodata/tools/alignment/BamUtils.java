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

package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.*;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.exceptions.AlignmentCoverageException;
import org.opencb.biodata.tools.feature.BigWigManager;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pfurio on 25/10/16.
 */
public class BamUtils {

    /**
     * Adjusts the quality value for optimized 8-level mapping quality scores.
     * Quality range -> Mapped quality
     * 1     ->  1
     * 2-9   ->  6
     * 10-19 ->  15
     * 20-24 ->  22
     * 25-29 ->  27
     * 30-34 ->  33
     * 35-39 ->  27
     * >=40  ->  40
     * Read more: http://www.illumina.com/documents/products/technotes/technote_understanding_quality_scores.pd
     *
     * @param quality original quality
     * @return Adjusted quality
     */
    public static int adjustQuality(int quality) {
        final int adjustedQuality;

        if (quality <= 1) {
            adjustedQuality = quality;
        } else {
            int qualRange = quality / 5;
            switch (qualRange) {
                case 0:
                case 1:
                    adjustedQuality = 6;
                    break;
                case 2:
                case 3:
                    adjustedQuality = 15;
                    break;
                case 4:
                    adjustedQuality = 22;
                    break;
                case 5:
                    adjustedQuality = 27;
                    break;
                case 6:
                    adjustedQuality = 33;
                    break;
                case 7:
                    adjustedQuality = 37;
                    break;
                case 8:
                default:
                    adjustedQuality = 40;
                    break;
            }
        }
        return adjustedQuality;
    }

    public static List<Integer> adjustQuality(List<Integer> qualities) {
        List<Integer> adjustedQualities = new ArrayList<>(qualities.size());
        for (Integer quality : qualities) {
            adjustedQualities.add(adjustQuality(quality));
        }
        return adjustedQualities;
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

    /**
     * Check if the file is a sorted binary bam file.
     *
     * @param is          Bam InputStream
     * @param bamFileName Bam FileName
     * @throws IOException
     */
    public static void checkBamOrCramFile(InputStream is, String bamFileName) throws IOException {
        checkBamOrCramFile(is, bamFileName, true);
    }

    /**
     * Check if the file is a sorted binary bam file.
     *
     * @param is          Bam InputStream
     * @param bamFileName Bam FileName
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
                    throw new IOException("Expected sorted file. File '" + bamFileName + "' is not sorted by coordinates("
                            + sortOrder.name() + ")");
            }
        }
    }

    /**
     * Return the coverage average given a window size from the BigWig file passed.
     * @param region Region from which return the coverage
     * @param windowSize Window size to average
     * @param bigwigPath BigWig path with coverage
     * @return One average score per window size spanning the region
     * @throws IOException If any error happens reading BigWig file
     */
    public static RegionCoverage getCoverageFromBigWig(Region region, int windowSize, Path bigwigPath) throws IOException {
        FileUtils.checkFile(bigwigPath);

        BigWigManager bigWigManager = new BigWigManager(bigwigPath);
        float[] avgCoverage = bigWigManager.groupBy(region, windowSize);
        return new RegionCoverage(region, windowSize, avgCoverage);
    }

    /**
     * Write in wig file format the coverage for the region given. It uses fixedStep with step equals to 1.
     *
     * @param regionCoverage    Region containing the coverage values
     * @param span              Span (to group coverage contiguous values in a mean coverage)
     * @param header            Flag, to write a header line (assuming fixedStep, and start=1 and step=1)
     * @param writer            File writer
     */
    public static void printWigFormatCoverage(RegionCoverage regionCoverage, int span, boolean header, PrintWriter writer) {
        // sanity check
        if (span < 1) {
            span = 1;
        }
        if (header) {
            writer.println("fixedStep chrom=" + regionCoverage.getChromosome() + " start=1 step=1 span=" + span);
        }
        float[] values = regionCoverage.getValues();
        if (span == 1) {
            for (int i = 0; i < values.length; i++) {
                writer.println(values[i]);
            }
        } else {
            int counter = 0;
            int sum = 0;
            for (int i = 0; i < values.length; i++) {
                counter++;
                sum += values[i];
                if (counter == span) {
                    writer.println(sum / counter);
                    counter = 0;
                    sum = 0;
                }
            }
            if (counter > 0) {
                writer.println(sum / counter);
            }
        }
    }

    /**
     * Utility to compute the coverage from a BAM file, and create a wig format file with the coverage values according
     * to a window size (i.e., span in wig format specification).
     *
     * @param bamPath           BAM file
     * @param coveragePath      Wig file name where to save coverage values
     * @param span              Span (to group coverage contiguous values in a mean coverage)
     * @throws IOException
     */
    public static void createCoverageWigFile(Path bamPath, Path coveragePath, int span) throws IOException, AlignmentCoverageException {
        SAMFileHeader fileHeader = BamUtils.getFileHeader(bamPath);

        AlignmentOptions options = new AlignmentOptions();
        options.setContained(false);

        BamManager alignmentManager = new BamManager(bamPath);
        Iterator<SAMSequenceRecord> iterator = fileHeader.getSequenceDictionary().getSequences().iterator();
        PrintWriter writer = new PrintWriter(coveragePath.toFile());
        // chunkSize = 100000 (too small, it takes loooooong...)
        int chunkSize = Math.max(span, 200000 / span * span);
        while (iterator.hasNext()) {
            SAMSequenceRecord next = iterator.next();
            for (int i = 0; i < next.getSequenceLength(); i += chunkSize) {
                Region region = new Region(next.getSequenceName(), i + 1, Math.min(i + chunkSize, next.getSequenceLength()));
                RegionCoverage regionCoverage = alignmentManager.coverage(region, null, options);
                printWigFormatCoverage(regionCoverage, span, (i == 0), writer);
            }
        }
        writer.close();
    }

    public static void validateRegion(Region region, SamReader samReader) {
        String chrom = region.getChromosome();
        if (StringUtils.isEmpty(chrom)) {
            throw new IllegalArgumentException("Unknown chromosome for region: " + region.toString());
        }

        SAMSequenceDictionary sequenceDictionary = samReader.getFileHeader().getSequenceDictionary();
        if (sequenceDictionary.getSequenceIndex(chrom) == -1) {
            if (chrom.startsWith("chr")) {
                chrom = chrom.replace("chr", "");
                if (sequenceDictionary.getSequenceIndex(chrom) == -1) {
                    throw new IllegalArgumentException("Unknown chromosome: " + region.getChromosome());
                } else {
                    region.setChromosome(chrom);
                }
            } else {
                if (sequenceDictionary.getSequenceIndex("chr" + chrom) == -1) {
                    throw new IllegalArgumentException("Unknown chromosome: " + region.getChromosome());
                } else {
                    region.setChromosome("chr" + chrom);
                }
            }
        }
    }
}