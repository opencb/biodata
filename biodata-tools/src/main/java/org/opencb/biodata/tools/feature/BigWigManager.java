package org.opencb.biodata.tools.feature;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceRecord;
import org.broad.igv.bbfile.BBFileReader;
import org.broad.igv.bbfile.BigWigIterator;
import org.broad.igv.bbfile.WigItem;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.alignment.BamUtils;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManager {

    private Path bigWigFilePath;
    private BBFileReader bbFileReader;

    /**
     * Constructor.
     *
     * @param path  Path to the Big Wig file
     * @throws IOException
     */
    public BigWigManager(Path path) throws IOException {
        this.bigWigFilePath = path;
        init();
    }

    /**
     * Query by a given region.
     *
     * @param region    Region target
     * @return          Array of floating values for that region
     * @throws IOException
     */
    public float[] query(Region region) throws IOException {
        BigWigIterator bigWigIterator = bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(),
                region.getChromosome(), region.getEnd(), true);
        float[] values = new float[region.getEnd() - region.getStart() + 1];
        while (bigWigIterator.hasNext()) {
            WigItem next = bigWigIterator.next();
//            System.out.println(next.getChromosome() + ":" + next.getStartBase() + "-" + next.getEndBase()
//                    + ", " + next.getWigValue());
            for (int i = next.getStartBase(), j = next.getStartBase() - region.getStart();
                 i <= next.getEndBase();
                 i++, j++) {
                values[j] = next.getWigValue();
            }
        }
        return values;
    }

    /**
     * Get the iterator for the given region.
     *
     * @param region    Region target
     * @return          Big Wig file iterator
     * @throws IOException
     */
    public BigWigIterator iterator(Region region) throws IOException {
        return bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(),
                region.getChromosome(), region.getEnd(), true);
    }

    /**
     * Index the entire Big Wig file content in a SQLite database managed by the ChunkFrequencyManager.
     *
     * @param bamPath   BAM file associated to the Big Wig file
     * @param cfManager ChunkFrequencyManager who manages the SQLite database
     * @throws Exception
     */
    public void index(Path bamPath, ChunkFrequencyManager cfManager) throws Exception {
        SAMFileHeader samHeader = BamUtils.getFileHeader(bamPath);
        List chromosomeNames = new ArrayList<String>();
        samHeader.getSequenceDictionary().getSequences().forEach(s -> chromosomeNames.add(s.getSequenceName()));
        index(chromosomeNames, bamPath, cfManager);
    }

    /**
     * Index the values for the given chromosomes of the Big Wig file in
     * a SQLite database managed by the ChunkFrequencyManager.
     *
     * @param bamPath   BAM file associated to the Big Wig file
     * @param cfManager ChunkFrequencyManager who manages the SQLite database
     * @throws Exception
     */
    public void index(List<String> chromosomes, Path bamPath, ChunkFrequencyManager cfManager) throws Exception {
        // insert file into the DB and get its ID, and the ChunkSize as well
        int fileId = cfManager.insertFile(bamPath);
        int chunkSize = cfManager.readChunkSize();

        // for efficiency purposes
        Set chromSet = new HashSet<String>();
        chromosomes.forEach(c -> chromSet.add(c));

        boolean isEmpty = true;
        int startChunk, endChunk, partial;

        // iterate over chromosome names, and then over their lengths looking for WigItems
        SAMFileHeader fileHeader = BamUtils.getFileHeader(bamPath);
        Iterator<SAMSequenceRecord> chromIterator = fileHeader.getSequenceDictionary().getSequences().iterator();
        while (chromIterator.hasNext()) {
            SAMSequenceRecord samSequenceRecord = chromIterator.next();
            // is it requested chromosome ?
            if (chromSet.contains(samSequenceRecord.getSequenceName())) {
                isEmpty = true;
                //System.err.println("Processing chromosome " + samSequenceRecord.getSequenceName());
                // then, allocate memory for chunk values
                int[] values = new int[samSequenceRecord.getSequenceLength() / chunkSize + 1];
                // and then iterate BigWig file
                BigWigIterator bwIterator = bbFileReader.getBigWigIterator(samSequenceRecord.getSequenceName(), 1,
                        samSequenceRecord.getSequenceName(), samSequenceRecord.getSequenceLength(), true);
                while (bwIterator.hasNext()) {
                    isEmpty = false;
                    WigItem wigItem = bwIterator.next();

                    startChunk = wigItem.getStartBase() / chunkSize;
                    endChunk = wigItem.getEndBase() / chunkSize;
                    for (int chunk = startChunk, pos = startChunk * chunkSize;
                         chunk <= endChunk;
                         chunk++, pos += chunkSize) {
                        // compute how many values are within the current chunk
                        // and update the chunk
                        partial = Math.min(wigItem.getEndBase(), pos + chunkSize) - Math.max(wigItem.getStartBase(),
                                pos);
                        values[chunk] += (partial * wigItem.getWigValue());
                    }
                }

                if (!isEmpty) {
                    //System.err.println("\tComputing mean values");
                    // compute mean values and save into the DB
                    List<Integer> meanValues = new ArrayList<>(values.length);
                    for (int v : values) {
                        meanValues.add(v / chunkSize);
                    }
                    cfManager.insert(samSequenceRecord.getSequenceName(), meanValues, fileId);
                }
            }
        }
    }

    /**
     * P R I V A T E   M E T H O D S
     */

    /**
     * Create the Big Wig file reader.
     *
     * @throws IOException
     */
    private void init() throws IOException {
        FileUtils.checkPath(this.bigWigFilePath);
        bbFileReader = new BBFileReader(this.bigWigFilePath.toString());
    }
}
