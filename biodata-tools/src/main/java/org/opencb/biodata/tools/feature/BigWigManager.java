package org.opencb.biodata.tools.feature;

import org.broad.igv.bbfile.BBFileReader;
import org.broad.igv.bbfile.BigWigIterator;
import org.broad.igv.bbfile.WigItem;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManager {

    private Path bigWigFilePath;
    private Path indexPath;
    private BBFileReader bbFileReader;

    public static final String BIGWIG_DB = "bigwig.db";

    /**
     * Constructor.
     *
     * @param bigwigPath  Path to the Big Wig file
     * @throws IOException
     */
    public BigWigManager(Path bigwigPath) throws IOException {
        this(bigwigPath, bigwigPath.getParent().resolve(BIGWIG_DB));
    }

    public BigWigManager(Path bigwigPath, Path indexPath) throws IOException {
        this.bigWigFilePath = bigwigPath;
        this.indexPath = indexPath;
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
     * Index the entire Big Wig file content in a SQLite database.
     *
     * @return              Path to the index (database file)
     * @throws Exception
     */
    public Path index() throws Exception {
        return index(bigWigFilePath);
    }

    /**
     * Index the entire Big Wig file content in a SQLite database.
     *
     * @param bigwigPath    Path to the Big Wig file to index
     * @return              Path to the index (database file)
     * @throws Exception
     */
    public Path index(Path bigwigPath) throws Exception {
//        FileUtils.checkFile(indexPath);
        ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(indexPath);

        // get the chunk size
        int chunkSize = chunkFrequencyManager.getChunkSize();

        int prevChunk = 0, startChunk, endChunk, partial;
        String currChrom, prevChrom = null;
        List<Integer> values = new ArrayList<>();

        // and then iterate BigWig file
        BigWigIterator bwIterator = bbFileReader.getBigWigIterator();
        while (bwIterator.hasNext()) {
            WigItem wigItem = bwIterator.next();

            // get info from wig item
            currChrom = wigItem.getChromosome();
            startChunk = wigItem.getStartBase() / chunkSize;
            endChunk = wigItem.getEndBase() / chunkSize;

            // chromosome change, we must store previous chromosome values
            if (prevChrom != currChrom) {
                if (values.size() > 0) {
                    WigUtils.computeAndSaveMeanValues(values, bigwigPath, prevChrom, chunkSize, chunkFrequencyManager);
                }
                currChrom = prevChrom;
            }

            if (prevChunk != startChunk) {
                for (int chunk = prevChunk; chunk < startChunk; chunk++) {
                    values.add(0);
                }
            }

            for (int chunk = startChunk, pos = startChunk * chunkSize;
                 chunk <= endChunk;
                 chunk++, pos += chunkSize) {
                // compute how many values are within the current chunk
                // and update the chunk
                partial = Math.min(wigItem.getEndBase(), pos + chunkSize) - Math.max(wigItem.getStartBase(),
                        pos);
                values.add((int) (partial * wigItem.getWigValue()));
            }
            prevChunk = endChunk;
        }

        if (values.size() > 0) {
            WigUtils.computeAndSaveMeanValues(values, bigwigPath, prevChrom, chunkSize, chunkFrequencyManager);
        }

        return indexPath;
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
