package org.opencb.biodata.tools.alignment;

import htsjdk.samtools.*;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
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

    public static void printWigFileCoverage(RegionCoverage regionCoverage, int windowSize,
                                            boolean header, PrintStream ps) {
        // sanity check
        if (windowSize < 1) {
            windowSize = 1;
        }
        if (header) {
            ps.println("fixedStep chrom=" + regionCoverage.getChromosome() + " start=1 step=1 span=" + windowSize);
        }
        short[] values = regionCoverage.getValues();
        if (windowSize == 1) {
            for (int i = 0; i < values.length; i++) {
                ps.println(values[i]);
            }
        } else {
            int counter = 0;
            int sum = 0;
            for (int i = 0; i < values.length; i++) {
                counter++;
                sum += values[i];
                if (counter == windowSize) {
                    ps.println(sum / counter);
                    counter = 0;
                    sum = 0;
                }
            }
            if (counter > 0) {
                ps.println(sum / counter);
            }
        }
    }
}