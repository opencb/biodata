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

import ga4gh.Reads;
import htsjdk.samtools.*;
import htsjdk.samtools.seekablestream.SeekableStream;
import htsjdk.samtools.seekablestream.SeekableStreamFactory;
import htsjdk.samtools.util.Log;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.models.alignment.RegionCoverage;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.coverage.SamRecordRegionCoverageCalculator;
import org.opencb.biodata.tools.alignment.exceptions.AlignmentCoverageException;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;
import org.opencb.biodata.tools.alignment.iterators.BamIterator;
import org.opencb.biodata.tools.alignment.iterators.SAMRecordToAvroReadAlignmentBamIterator;
import org.opencb.biodata.tools.alignment.iterators.SAMRecordToProtoReadAlignmentBamIterator;
import org.opencb.biodata.tools.alignment.iterators.SamRecordBamIterator;
import org.opencb.biodata.tools.alignment.stats.AlignmentGlobalStats;
import org.opencb.biodata.tools.alignment.stats.SamRecordAlignmentGlobalStatsCalculator;
import org.opencb.biodata.tools.feature.BigWigManager;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by imedina on 14/09/15.
 */
public class BamManager {

    private Path bamFile;
    private SamReader samReader;

    public static final int DEFAULT_WINDOW_SIZE = 1;
    public static final int MAX_NUM_RECORDS = 50000;
    public static final int MAX_REGION_COVERAGE = 100000;
    public static final String COVERAGE_BIGWIG_EXTENSION = ".coverage.bw";

    private Logger logger;

    public BamManager(Path bamFilePath) throws IOException {
        FileUtils.checkFile(bamFilePath);
        this.bamFile = bamFilePath;

        logger = LoggerFactory.getLogger(BamManager.class);
    }

    private void init() throws IOException {
        FileUtils.checkFile(bamFile);

        if (this.samReader == null) {
            SamReaderFactory srf = SamReaderFactory.make();
            srf.validationStringency(ValidationStringency.LENIENT);
            this.samReader = srf.open(SamInputResource.of(bamFile.toFile()));
        }
    }

    /**
     * Creates a index file for the BAM or CRAM input file.
     * @return The path of the index file.
     * @throws IOException
     */
    public Path createIndex() throws IOException {
        Path indexPath = bamFile.getParent().resolve(bamFile.getFileName().toString() + ".bai");
        return createIndex(indexPath);
    }

    /**
     * Creates a BAM/CRAM index file.
     * @param outputIndex The bai index file to be created.
     * @return
     * @throws IOException
     */
    public Path createIndex(Path outputIndex) throws IOException {
        FileUtils.checkDirectory(outputIndex.toAbsolutePath().getParent(), true);

        SamReaderFactory srf = SamReaderFactory.make().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS);
        srf.validationStringency(ValidationStringency.LENIENT);
        try (SamReader reader = srf.open(SamInputResource.of(bamFile.toFile()))) {

            // Files need to be sorted by coordinates to create the index
            SAMFileHeader.SortOrder sortOrder = reader.getFileHeader().getSortOrder();
            if (!sortOrder.equals(SAMFileHeader.SortOrder.coordinate)) {
                throw new IOException("Sorted file expected. File '" + bamFile.toString()
                        + "' is not sorted by coordinates (" + sortOrder.name() + ")");
            }

            if (reader.type().equals(SamReader.Type.BAM_TYPE)) {
                BAMIndexer.createIndex(reader, outputIndex.toFile(), Log.getInstance(BamManager.class));
            } else {
                if (reader.type().equals(SamReader.Type.CRAM_TYPE)) {
                    // TODO This really needs to be tested!
                    SeekableStream streamFor = SeekableStreamFactory.getInstance().getStreamFor(bamFile.toString());
                    CRAMBAIIndexer.createIndex(streamFor, outputIndex.toFile(), Log.getInstance(BamManager.class),
                            ValidationStringency.DEFAULT_STRINGENCY);
                } else {
                    throw new IOException("This is not a BAM or CRAM file. SAM files cannot be indexed");
                }
            }
        }
        return outputIndex;
    }

    public Path calculateBigWigCoverage(int windowSize) throws IOException {
        return calculateBigWigCoverage(Paths.get(this.bamFile.toFile().getAbsolutePath() + COVERAGE_BIGWIG_EXTENSION), windowSize);
    }

    public Path calculateBigWigCoverage(Path bigWigPath, int windowSize) throws IOException {
        checkBaiFileExists();
        FileUtils.checkDirectory(bigWigPath.toAbsolutePath().getParent(), true);

        // Check windowsSize is valid, it must be greater or equal than 1
        if (windowSize < 1) {
            windowSize = DEFAULT_WINDOW_SIZE;
        }
        String windowSizeStr = String.valueOf(windowSize);

        // Execute the bamCoverage utility from deepTools package, assuming it is installed in the system
        // deepTools installation: pip install deepTools
        ProcessBuilder processBuilder = new ProcessBuilder(
                Arrays.asList("bamCoverage", "-b", bamFile.toString(), "-o", bigWigPath.toString(), "-of", "bigwig", "-bs", windowSizeStr));
        Process p = processBuilder.start();
        BufferedReader processBufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = processBufferedReader.readLine()) != null) {
            logger.info(line);
        }

        return bigWigPath;
    }


    public String header() throws IOException {
        init();
        return samReader.getFileHeader().getTextHeader();
    }

    /*
     * These methods aim to provide a very simple, safe and quick way of accessing to a small fragment of the BAM/CRAM file.
     * This must not be used in production for reading big data files. It returns a maximum of 50,000 SAM records,
     * you can use iterator methods for reading more reads.
     *
     */
    public List<SAMRecord> query(AlignmentFilters<SAMRecord> filters) throws IOException {
        return query(null, filters, null, SAMRecord.class);
    }

    public List<SAMRecord> query(AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws IOException {
        return query(null, filters, options, SAMRecord.class);
    }

    public <T> List<T> query(AlignmentFilters<SAMRecord> filters, AlignmentOptions options, Class<T> clazz) throws IOException {
        return query(null, filters, options, clazz);
    }

    public List<SAMRecord> query(Region region) throws IOException {
        return query(region, null, new AlignmentOptions(), SAMRecord.class);
    }

    public List<SAMRecord> query(Region region, AlignmentOptions options) throws IOException {
        return query(region, null, options, SAMRecord.class);
    }

    public List<SAMRecord> query(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws IOException {
        return query(region, filters, options, SAMRecord.class);
    }

    public <T> List<T> query(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options, Class<T> clazz) throws IOException {
        if (options == null) {
            options = new AlignmentOptions();
        }

        // Set number of returned records up to MAX_NUM_RECORDS, if not set then MAX_NUM_RECORDS is returned
        int maxNumberRecords = MAX_NUM_RECORDS;
        if (options.getLimit() > 0) {
            maxNumberRecords = Math.min(options.getLimit(), MAX_NUM_RECORDS);
        }

        List<T> results = new ArrayList<>(maxNumberRecords);
        BamIterator<T> bamIterator = (region != null)
                ? iterator(region, filters, options, clazz)
                : iterator(filters, options, clazz);

        while (bamIterator.hasNext() && results.size() < maxNumberRecords) {
            results.add(bamIterator.next());
        }
        bamIterator.close();
        return results;
    }


    /*
     * These methods aim to provide a very simple, safe and quick way of iterating BAM/CRAM files.
     */
    public BamIterator<SAMRecord> iterator() throws IOException {
        return iterator(null, new AlignmentOptions(), SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(AlignmentOptions options) throws IOException {
        return iterator(null, options, SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws IOException {
        return iterator(filters, options, SAMRecord.class);
    }

    public <T> BamIterator<T> iterator(AlignmentFilters<SAMRecord> filters, AlignmentOptions options, Class<T> clazz) throws IOException {
        init();
        checkBaiFileExists();

        SAMRecordIterator samRecordIterator = samReader.iterator();
        return getAlignmentIterator(filters, options, clazz, samRecordIterator);
    }

    public BamIterator<SAMRecord> iterator(Region region) throws IOException {
        return iterator(region, null, new AlignmentOptions(), SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(Region region, AlignmentOptions options) throws IOException {
        return iterator(region, null, options, SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws IOException {
        return iterator(region, filters, options, SAMRecord.class);
    }

    public <T> BamIterator<T> iterator(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options, Class<T> clazz)
            throws IOException {
        init();
        checkBaiFileExists();

        if (options == null) {
            options = new AlignmentOptions();
        }
        SAMRecordIterator samRecordIterator =
                samReader.query(region.getChromosome(), region.getStart(), region.getEnd(), options.isContained());
        return getAlignmentIterator(filters, options, clazz, samRecordIterator);
    }

    private <T> BamIterator<T> getAlignmentIterator(AlignmentFilters<SAMRecord> filters, AlignmentOptions alignmentOptions, Class<T> clazz,
                                                    SAMRecordIterator samRecordIterator) {
        if (alignmentOptions == null) {
            alignmentOptions = new AlignmentOptions();
        }

        int limit = -1;
        if (alignmentOptions.getLimit() > 0) {
            limit = alignmentOptions.getLimit();
        }

        if (ReadAlignment.class == clazz) {
            // AVRO
            return (BamIterator<T>) new SAMRecordToAvroReadAlignmentBamIterator(samRecordIterator, filters, alignmentOptions.isBinQualities(), limit);
        } else if (Reads.ReadAlignment.class == clazz) {
            // PROTOCOL BUFFER
            return (BamIterator<T>) new SAMRecordToProtoReadAlignmentBamIterator(samRecordIterator, filters, alignmentOptions.isBinQualities(), limit);
        } else if (SAMRecord.class == clazz) {
            return (BamIterator<T>) new SamRecordBamIterator(samRecordIterator, filters, limit);
        } else {
            throw new IllegalArgumentException("Unknown alignment model class: " + clazz);
        }
    }

    /**
     * Return the coverage average given a window size from a BigWig file. This is expected to have the same name
     * that the BAM file with .coverage.bw or .bw suffix.
     * If no BigWig file is found and windowSize is 1 then we calculate te coverage from the BAM file.
     * @param region Region from which return the coverage
     * @param windowSize Window size to average
     * @return One average score per window size spanning the region
     * @throws IOException If any error happens reading BigWig file
     */
    public RegionCoverage coverage(Region region, int windowSize) throws IOException, AlignmentCoverageException {
        if (Paths.get(bamFile.toString() + ".bw").toFile().exists()) {
            return BamUtils.getCoverageFromBigWig(region, windowSize, Paths.get(bamFile.toString() + ".bw"));
        } else {
            if (Paths.get(bamFile.toString() + COVERAGE_BIGWIG_EXTENSION).toFile().exists()) {
                return BamUtils.getCoverageFromBigWig(region, windowSize, Paths.get(this.bamFile.toString() + COVERAGE_BIGWIG_EXTENSION));
            } else {
                // If BigWig file is not found and windowSize is 1 then we calculate it from the BAM file
                if (windowSize == 1) {
                    return coverage(region, null, new AlignmentOptions());
                } else {
                    throw new AlignmentCoverageException("No bigwig file has been found and windowSize is > 1");
                }
            }
        }
    }

    /**
     * This method get some filters and calculate the coverage from the BAM file with the reads filtered.
     * @param region Region to calculate coverage from
     * @param filters Filters to be applied to reads
     * @param options Other possible options
     * @return The coverage for each position of the region (windowSize == 1)
     */
    public RegionCoverage coverage(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options)
            throws AlignmentCoverageException {
        // Check region size is smaller than MAX_REGION_COVERAGE
        if (region.size() > MAX_REGION_COVERAGE) {
            throw new AlignmentCoverageException("Region size is bigger than MAX_REGION_COVERAGE [" + MAX_REGION_COVERAGE +"]");
        }

        RegionCoverage regionCoverage = new RegionCoverage(region);
        if (options == null) {
            options = new AlignmentOptions();
        }
        SamRecordRegionCoverageCalculator calculator = new SamRecordRegionCoverageCalculator(options.getMinBaseQuality());
        try (BamIterator<SAMRecord> iterator = iterator(region, filters, options)) {
            while (iterator.hasNext()) {
                SAMRecord next = iterator.next();
                if (!next.getReadUnmappedFlag()) {
                    calculator.update(next, regionCoverage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regionCoverage;
    }

    /**
     * Return a list of RegionCoverage with a coverage less than o equal to the input maximum coverage.
     * @param region
     * @param maxCoverage
     * @return
     * @throws IOException
     */
    public List<RegionCoverage> getUncoveredRegions(Region region, int maxCoverage) throws IOException, AlignmentCoverageException {
        List<RegionCoverage> uncoveredRegions = new ArrayList<>();
        RegionCoverage coverageRegion = coverage(region, 1);

        float[] coverages = new float[region.size()];
        int i = 0;
        int pos = coverageRegion.getStart();
        RegionCoverage uncoveredRegion = null;
        for (float coverage: coverageRegion.getValues()) {
            if (coverage < maxCoverage) {
                if (uncoveredRegion == null) {
                    uncoveredRegion = new RegionCoverage(region.getChromosome(), pos, 0);
                    i = 0;
                }
                coverages[i] = coverage;
                i++;
            } else {
                if (uncoveredRegion != null) {
                    uncoveredRegion.setEnd(pos);
                    uncoveredRegion.setValues(Arrays.copyOf(coverages, i));
                    uncoveredRegions.add(uncoveredRegion);
                    uncoveredRegion = null;
                }
            }
            pos++;
        }

        // Check if a uncovered region is still pending
        if (uncoveredRegion != null) {
            uncoveredRegion.setEnd(pos - 1);
            uncoveredRegion.setValues(Arrays.copyOf(coverages, i));
            uncoveredRegions.add(uncoveredRegion);
        }

        return uncoveredRegions;
    }

    public AlignmentGlobalStats stats() throws IOException {
        return calculateGlobalStats(iterator());
    }

    public AlignmentGlobalStats stats(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws IOException {
        return calculateGlobalStats(iterator(region, filters, options));
    }

    private AlignmentGlobalStats calculateGlobalStats(BamIterator<SAMRecord> iterator) throws IOException {
        AlignmentGlobalStats alignmentGlobalStats = new AlignmentGlobalStats();
        SamRecordAlignmentGlobalStatsCalculator calculator = new SamRecordAlignmentGlobalStatsCalculator();
        while (iterator.hasNext()) {
            AlignmentGlobalStats computed = calculator.compute(iterator.next());
            calculator.update(computed, alignmentGlobalStats);
        }
        iterator.close();
        return alignmentGlobalStats;
    }


    public void close() throws IOException {
        if (samReader != null) {
            samReader.close();
        }
    }

    private void checkBaiFileExists() throws IOException {
        if (!new File(bamFile.toString() + ".bai").exists()) {
            throw new IOException("Missing BAM index (.bai file) for " + bamFile.toString());
        }
    }

    public Path getBamFile() {
        return bamFile;
    }

    public BamManager setBamFile(Path bamFilePath) {
        this.bamFile = bamFilePath;
        return this;
    }
}
