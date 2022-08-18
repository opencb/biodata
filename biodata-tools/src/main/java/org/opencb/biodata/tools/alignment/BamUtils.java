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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.broad.igv.bbfile.*;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.exceptions.AlignmentCoverageException;
import org.opencb.biodata.tools.feature.BigWigManager;
import org.opencb.commons.utils.CollectionUtils;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
            throw new IllegalArgumentException("Missing chromosome for region: " + region.toString());
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

    /**
     * Return a list of RegionCoverage with a coverage less than or equal to the input maximum coverage.
     * @param coverageRegion
     * @param maxCoverage
     * @return
     * @throws IOException
     */
    public static List<RegionCoverage> filterByCoverage(RegionCoverage coverageRegion, int minCoverage, int maxCoverage) {
        List<RegionCoverage> selectedRegions = new ArrayList<>();

        float[] coverages = new float[coverageRegion.size()];
        int i = 0;
        int pos = coverageRegion.getStart();
        boolean isProcessing = false;
        RegionCoverage uncoveredRegion = null;
        for (float coverage: coverageRegion.getValues()) {
            if (coverage >= minCoverage && coverage <= maxCoverage) {
                if (!isProcessing) {
                    uncoveredRegion = new RegionCoverage(coverageRegion.getChromosome(), pos, 0);
                    isProcessing = true;
                    i = 0;
                }
                coverages[i] = coverage;
                i++;
            } else {
                if (isProcessing) {
                    uncoveredRegion.setEnd(pos - 1);
                    uncoveredRegion.setValues(Arrays.copyOf(coverages, i));
                    uncoveredRegion.updateStats();
                    selectedRegions.add(uncoveredRegion);
                    isProcessing = false;
                }
            }
            pos++;
        }

        // Check if a uncovered region is still processing
        if (isProcessing) {
            uncoveredRegion.setEnd(pos - 1);
            uncoveredRegion.setValues(Arrays.copyOf(coverages, i));
            uncoveredRegion.updateStats();
            selectedRegions.add(uncoveredRegion);
        }

        return selectedRegions;
    }

    /*

import math
import csv
CANONIC_CHROMOSOMES = ['chr1', 'chr2', 'chr3', 'chr4', 'chr5',
                       'chr6', 'chr7', 'chr8', 'chr9', 'chr10',
                       'chr11', 'chr12', 'chr13', 'chr14',
                       'chr15', 'chr16', 'chr17', 'chr18',
                       'chr19', 'chr20', 'chr21', 'chr22',
                       'chrX', 'chrY']
def process_coverage(cov_bw_tumour_fpath, cov_bw_normal_fpath, output_dir):
    """
    Calculate coverage to be used posteriorly for some tracks in circos plot

    :type cov_bw_tumour_fpath: str
    :param cov_bw_tumour_fpath: bigWig file path for tumour sample

    :type cov_bw_normal_fpath: str
    :param cov_bw_normal_fpath: bigWig file path for normal sample

    :type output_dir: str
    :param output_dir: output folder path
    """
    # Calculating ratio of normalised coverage
    produce_normalised_coverage(cov_bw_tumour_fpath, cov_bw_normal_fpath,
                                output_dir)
def get_region_norm_cov(region_counts_tumour, region_counts_normal,
                        total_counts_tumour, total_counts_normal):
    """
    Calculate ratio of normalised depth of coverage in log2 scale for a region

    :type region_counts_tumour: int
    :param region_counts_tumour: counts for region in tumour

    :type region_counts_normal: int
    :param region_counts_normal: counts for region in normal

    :type total_counts_tumour: int
    :param total_counts_tumour: total counts for tumour

    :type total_counts_normal: int
    :param total_counts_normal: total counts for normal
    """
    # Rescaling both coverages
    rescaled_tumour_cov = (region_counts_tumour/float(total_counts_tumour))*1000
    rescaled_normal_cov = (region_counts_normal/float(total_counts_normal))*1000
    # Returning None if tumour or normal coverage are low
    if (float(region_counts_normal)/100000) < 15:  # Raw mean cov is < 15
        return None
    if rescaled_normal_cov == 0:
        return None
    # Getting coverage ratio
    cov_ratio = float(rescaled_tumour_cov)/float(rescaled_normal_cov)
    if cov_ratio == 0:
        return None
    # Getting normalised coverage (log2)
    # log2(x) = log10(x) / log10(2)
    coverage_log2 = math.log10(cov_ratio)/math.log10(2)
    if coverage_log2 > 4:  # cov_ratio > 16
        return 4
    return coverage_log2

def get_total_counts(coverage_bw_fpath):
    """
    Get bigWig total coverage

    :type: coverage_bw_fpath: file
    :param coverage_bw_fpath: bigWig file
    """
    cov_fhand = open(coverage_bw_fpath, 'r')
    total_coverage = 0
    for line in cov_fhand:
        if line.startswith('#'):  # Skip header
            continue
        line = line.strip().split("\t")
        total_coverage += (float(line[7]) * float(line[3]))  # (size * mean)
    cov_fhand.close()
    return total_coverage

def sort_canonic_bigwig(bw_fpath, header=True):
    """
    Sort a bigWig file by canonic chromosome and start
    :type bw_fpath: str
    :param bw_fpath: bigwig file path
    :type header: bool
    :param header: indicates if file has header
    """
    bw_fhand = open(bw_fpath, 'r')
    csv_file = csv.reader(bw_fhand, delimiter='\t')
    # Returning header
    if header:
        yield '\t'.join(next(csv_file))
    # Sorting file by chromosome and start
    sort = sorted(csv_file, key=lambda x: (x[0], int(x[1])))
    for line in sort:
        # Removing non-canonic chromosomes
        if line[0] not in CANONIC_CHROMOSOMES:
            continue
        yield '\t'.join(line)
    bw_fhand.close()

def produce_normalised_coverage(cov_bw_tumour_fpath, cov_bw_normal_fpath,
                                output_dir):
    """
    Given tumour and normal coverage bigWig files, calculate ratio of
    normalised depth of coverage in log2 scale

    :type cov_bw_tumour_fpath: str
    :param cov_bw_tumour_fpath: bigWig file path for tumour sample

    :type cov_bw_normal_fpath: str
    :param cov_bw_normal_fpath: bigWig file path for normal sample

    :type output_dir: str
    :param output_dir: output folder path
    """
    # Output file
    cov_norm = open(output_dir + '/coverage.txt', 'w')
    # Getting total coverage for both coverage files
    total_counts_tumour = get_total_counts(cov_bw_tumour_fpath)
    total_counts_normal = get_total_counts(cov_bw_normal_fpath)
    # Opening coverage input files
    file_a = sort_canonic_bigwig(cov_bw_tumour_fpath)
    file_b = sort_canonic_bigwig(cov_bw_normal_fpath)
    # Skipping headers
    next(file_a)
    next(file_b)
    # Iterating over the two files at the same time
    for line_a in file_a:
        a_data = bw_line_to_dict(line_a)  # Current line in a
        b_data = bw_line_to_dict(next(file_b))  # Current line in b
        # Checking that we are in the same region in both files
        assert a_data['chr'] == b_data['chr']
        assert a_data['start'] == b_data['start']
        # Get ratio of normalised coverage
        normalised = get_region_norm_cov(a_data["counts"], b_data["counts"],
                                         total_counts_tumour,
                                         total_counts_normal)
        if normalised is not None:
            cov_norm.write(str(a_data['chr']).replace("chr", "hs") + " " +
                           str(a_data['start']) + " " +
                           str(a_data['end']) + " " +
                           str(normalised) + "\n")
    cov_norm.close()
def bw_line_to_dict(bw_line):
    """
    Given a bigWig file, return a dictionary with some of its data
    :type bw_line: str
    :param bw_line: line from bigWig file
    """
    bw_line = bw_line.strip().split('\t')
    bw_line_data = {
        'chr': bw_line[0],
        'start': int(bw_line[1]),
        'end': int(bw_line[2]),
        'size': int(bw_line[3]),
        'mean': float(bw_line[7])
    }
    bw_line_data['counts'] = (float(bw_line_data['mean']) *
                              float(bw_line_data['size']))
    return bw_line_data
     */

    public static RegionCoverage log2CoverageRatio(RegionCoverage coverage1, long totalCounts1,
                                                   RegionCoverage coverage2, long totalCounts2) throws AlignmentCoverageException {
        if (coverage1 != null && coverage1.getValues() != null && coverage2 != null && coverage2.getValues() != null
                && coverage1.getWindowSize() == coverage2.getWindowSize() && coverage1.getValues().length == coverage2.getValues().length) {

            float values[] = new float[coverage1.getValues().length];

            float factor = 1000.0f * coverage1.getWindowSize();

            for (int i = 0; i < values.length; i++) {
                // Checking if tumour or normal coverage are low, raw mean cov is >= 15
                if (coverage2.getValues()[i] /* coverage2.getWindowSize() / 100000.0f */ >= 15) {
                    // Rescaling both coverages
                    float rescaledCoverage1 = factor * coverage1.getValues()[i] / totalCounts1;
                    float rescaledCoverage2 = factor * coverage2.getValues()[i] / totalCounts2;

                    if (rescaledCoverage2 > 0) {
                        // Getting coverage ratio
                        float covRatio = rescaledCoverage1 / rescaledCoverage2;
                        if (covRatio > 0) {
                            // Getting normalised coverage (log2)
                            // log2(x) = log10(x) / log10(2)
                            float covLog2 = (float) (Math.log10(covRatio) / Math.log10(2));
                            if (covLog2 > 4) {
                                // cov_ratio > 16
                                covLog2 = 4;
                            }
                            values[i] = covLog2;
                        }
                    }
                }
            }

            Region region = new Region(coverage1.getChromosome(), coverage1.getStart(), coverage1.getEnd());
            return new RegionCoverage(region, coverage1.getWindowSize(), values);
        } else {
            throw new AlignmentCoverageException("Something wrong computing coverage ratio");
        }
    }
}