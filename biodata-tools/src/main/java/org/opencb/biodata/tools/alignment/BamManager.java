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
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;
import org.opencb.biodata.tools.alignment.iterators.BamIterator;
import org.opencb.biodata.tools.alignment.iterators.SAMRecordToAvroReadAlignmentBamIterator;
import org.opencb.biodata.tools.alignment.iterators.SAMRecordToProtoReadAlignmentBamIterator;
import org.opencb.biodata.tools.alignment.iterators.SamRecordBamIterator;
import org.opencb.biodata.tools.alignment.stats.AlignmentGlobalStats;
import org.opencb.biodata.tools.alignment.stats.SamRecordAlignmentGlobalStatsCalculator;
import org.opencb.biodata.tools.feature.BigWigManager;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by imedina on 14/09/15.
 */
public class BamManager {

    private Path bamPath;
    private SamReader samReader;

    private static final int DEFAULT_MAX_NUM_RECORDS = 50000;

    @Deprecated
    public BamManager() {
    }

    public BamManager(Path bamPath) throws IOException {
        FileUtils.checkFile(bamPath);
        this.bamPath = bamPath;

        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        this.samReader = srf.open(SamInputResource.of(bamPath.toFile()));
    }

    /**
     * Creates a index file for the BAM or CRAM input file.
     * @return The path of the index file.
     * @throws IOException
     */
    public Path createIndex() throws IOException {
        Path indexPath = bamPath.getParent().resolve(bamPath.getFileName().toString() + ".bai");
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
        try (SamReader reader = srf.open(SamInputResource.of(bamPath.toFile()))) {

            // Files need to be sorted by coordinates to create the index
            SAMFileHeader.SortOrder sortOrder = reader.getFileHeader().getSortOrder();
            if (!sortOrder.equals(SAMFileHeader.SortOrder.coordinate)) {
                throw new IOException("Expected sorted file. File '" + bamPath.toString()
                        + "' is not sorted by coordinates (" + sortOrder.name() + ")");
            }

            if (reader.type().equals(SamReader.Type.BAM_TYPE)) {
                BAMIndexer.createIndex(reader, outputIndex.toFile(), Log.getInstance(BamManager.class));
            } else {
                if (reader.type().equals(SamReader.Type.CRAM_TYPE)) {
                    // TODO This really needs to be tested!
                    SeekableStream streamFor = SeekableStreamFactory.getInstance().getStreamFor(bamPath.toString());
                    CRAMBAIIndexer.createIndex(streamFor, outputIndex.toFile(), Log.getInstance(BamManager.class),
                            ValidationStringency.DEFAULT_STRINGENCY);
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
     *
     * @param region @return
     * @throws IOException
     */
    public List<SAMRecord> query(Region region) throws Exception {
        return query(region, null, new AlignmentOptions(), SAMRecord.class);
    }

    public List<SAMRecord> query(Region region, AlignmentOptions options) throws Exception {
        return query(region, null, options, SAMRecord.class);
    }

    public List<SAMRecord> query(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws Exception {
        return query(region, filters, options, SAMRecord.class);
    }

    public List<SAMRecord> query(AlignmentFilters<SAMRecord> filters) throws Exception {
        return query(null, filters, null, SAMRecord.class);
    }

    public List<SAMRecord> query(AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws Exception {
        return query(null, filters, options, SAMRecord.class);
    }

    public <T> List<T> query(AlignmentFilters<SAMRecord> filters, AlignmentOptions options, Class<T> clazz) throws Exception {
        return query(null, filters, options, clazz);
    }

    public <T> List<T> query(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions alignmentOptions, Class<T> clazz)
            throws Exception {
        if (alignmentOptions == null) {
            alignmentOptions = new AlignmentOptions();
        }

        // Number of returned records, if not set then DEFAULT_MAX_NUM_RECORDS is returned
        int maxNumberRecords = DEFAULT_MAX_NUM_RECORDS;
        if (alignmentOptions.getLimit() > 0) {  // && alignmentOptions.getLimit() <= DEFAULT_MAX_NUM_RECORDS
            maxNumberRecords = alignmentOptions.getLimit();
        }

        List<T> results = new ArrayList<>(maxNumberRecords);
        BamIterator<T> bamIterator = (region != null)
                ? iterator(region, filters, alignmentOptions, clazz)
                : iterator(filters, alignmentOptions, clazz);

        while (bamIterator.hasNext() && results.size() < maxNumberRecords) {
            results.add(bamIterator.next());
        }
        bamIterator.close();
        return results;
    }


    /*
     * These methods aim to provide a very simple, safe and quick way of iterating BAM/CRAM files.
     */
    public BamIterator<SAMRecord> iterator() {
        return iterator(null, new AlignmentOptions(), SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(AlignmentOptions options) {
        return iterator(null, options, SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(AlignmentFilters<SAMRecord> filters, AlignmentOptions options) {
        return iterator(filters, options, SAMRecord.class);
    }

    public <T> BamIterator<T> iterator(AlignmentFilters<SAMRecord> filters, AlignmentOptions alignmentOptions, Class<T> clazz) {
        SAMRecordIterator samRecordIterator = samReader.iterator();
        return getAlignmentIterator(filters, alignmentOptions, clazz, samRecordIterator);
    }

    public BamIterator<SAMRecord> iterator(Region region) {
        return iterator(region, null, new AlignmentOptions(), SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(Region region, AlignmentOptions options) {
        return iterator(region, null, options, SAMRecord.class);
    }

    public BamIterator<SAMRecord> iterator(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) {
        return iterator(region, filters, options, SAMRecord.class);
    }

    public <T> BamIterator<T> iterator(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions alignmentOptions, Class<T> clazz) {
        if (alignmentOptions == null) {
            alignmentOptions = new AlignmentOptions();
        }
        SAMRecordIterator samRecordIterator =
                samReader.query(region.getChromosome(), region.getStart(), region.getEnd(), alignmentOptions.isContained());
        return getAlignmentIterator(filters, alignmentOptions, clazz, samRecordIterator);
    }

    private <T> BamIterator<T> getAlignmentIterator(AlignmentFilters<SAMRecord> filters, AlignmentOptions options, Class<T> clazz,
                                                    SAMRecordIterator samRecordIterator) {
        if (options == null) {
            options = new AlignmentOptions();
        }

        if (ReadAlignment.class == clazz) {
            // AVRO
            return (BamIterator<T>) new SAMRecordToAvroReadAlignmentBamIterator(samRecordIterator, filters, options.isBinQualities());
        } else if (Reads.ReadAlignment.class == clazz) {
            // PROTOCOL BUFFER
            return (BamIterator<T>) new SAMRecordToProtoReadAlignmentBamIterator(samRecordIterator, filters, options.isBinQualities());
        } else if (SAMRecord.class == clazz) {
            return (BamIterator<T>) new SamRecordBamIterator(samRecordIterator, filters);
        } else {
            throw new IllegalArgumentException("Unknown alignment model class: " + clazz);
        }
    }


    public RegionCoverage coverage(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) {
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
     * Return the coverage average given a window size from a BigWig file. This is expected to have the same name
     * that the BAM file with .coverage.bw or .bw suffix.
     * If no BigWig file is founf and windowSize is 1 then we calculate te coverage from the BAM file.
     * @param region Region from which return the coverage
     * @param windowSize Window size to average
     * @return One average score per window size spanning the region
     * @throws IOException If any error happens reading BigWig file
     */
    public RegionCoverage coverage(Region region, int windowSize) throws IOException {
        if (this.bamPath.resolve(".bw").toFile().exists()) {
            return coverage(region, windowSize, this.bamPath.resolve(".bw"));
        } else {
            if (this.bamPath.resolve(".coverage.bw").toFile().exists()) {
                return coverage(region, windowSize, this.bamPath.resolve(".coverage.bw"));
            } else {
                // If not BigWig file is found and windowSize is 1 then we calculate it from the BAM file
                if (windowSize == 1) {
                    return coverage(region, null, new AlignmentOptions());
                }
            }
        }
        return null;
    }

    /**
     * Return the coverage average given a window size from the BigWig file passed.
     * @param region Region from which return the coverage
     * @param windowSize Window size to average
     * @param bigwigPath BigWig path with coverage
     * @return One average score per window size spanning the region
     * @throws IOException If any error happens reading BigWig file
     */
    public RegionCoverage coverage(Region region, int windowSize, Path bigwigPath) throws IOException {
        BigWigManager bigWigManager = new BigWigManager(bigwigPath);
        float[] avgCoverage = bigWigManager.groupBy(region, windowSize);
        return new RegionCoverage(region, windowSize, avgCoverage);
    }


    public AlignmentGlobalStats stats() throws Exception {
        return calculateGlobalStats(iterator());
    }

    public AlignmentGlobalStats stats(Region region, AlignmentFilters<SAMRecord> filters, AlignmentOptions options) throws Exception {
        return calculateGlobalStats(iterator(region, filters, options));
    }

    private AlignmentGlobalStats calculateGlobalStats(BamIterator<SAMRecord> iterator) throws Exception {
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
}
