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
    private BBFileReader bbFileReader;

    public BigWigManager(Path path) throws IOException {
        this.bigWigFilePath = path;

        init();
    }

    private void init() throws IOException {
        FileUtils.checkPath(this.bigWigFilePath);
        bbFileReader = new BBFileReader(this.bigWigFilePath.toString());
    }

    public float[] query(Region region) throws IOException {
        BigWigIterator bigWigIterator = bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(),
                region.getChromosome(), region.getEnd(), true);
        float[] values = new float[region.getEnd() - region.getStart()];
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

    public BigWigIterator iterator(Region region) throws IOException {
        return bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(), region.getChromosome(), region.getEnd(), true);
    }

    public void index(ChunkFrequencyManager cfManager) throws IOException {
        index(bbFileReader.getChromosomeNames(), cfManager);
    }

    public void index(List<String> chromosomes, ChunkFrequencyManager cfManager) throws IOException {
        int fileId = 0;
        int chunkSize = 1000; //cfManager.getChunkSize();
        int chromSize;

        boolean toInsert;
        int startChunk, endChunk, partial;

        for (String chromosome: chromosomes) {
            // get chromosome size from name to allocate space for values
            chromSize  = 10000000;
            int[] values = new int[chromSize];
            // and then iterate
            toInsert = false;
            BigWigIterator bwIterator = bbFileReader.getBigWigIterator(chromosome, 1, chromosome, chromSize, true);
            while (bwIterator.hasNext()) {
                WigItem next = bwIterator.next();

                toInsert = true;
                startChunk = next.getStartBase() / chunkSize;
                endChunk = next.getEndBase() / chunkSize;
                for (int chunk = startChunk, pos = startChunk * chunkSize;
                     chunk <= endChunk;
                     chunk++, pos += chunkSize) {
                    partial = Math.min(next.getEndBase(), pos + chunkSize) - Math.max(next.getStartBase(), pos);
                    values[chunk] += (partial * next.getWigValue());
                }
            }

            if (toInsert) {
                List<Integer> meanValues = new ArrayList<>(values.length);
                for (int v : values) {
                    meanValues.add(v / chunkSize);
                }
//                cfManager.insert(chromosome, meanValues, fileId);
            }
        }
    }
}
