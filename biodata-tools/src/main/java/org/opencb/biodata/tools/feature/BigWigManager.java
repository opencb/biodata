package org.opencb.biodata.tools.feature;

import htsjdk.samtools.SAMSequenceDictionary;
import org.broad.igv.bbfile.*;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;
import org.opencb.commons.utils.FileUtils;
import org.opencb.commons.utils.ListUtils;

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
    private List<Integer> zoomWindowSizes;

    @Deprecated
    private Path indexPath;

    @Deprecated
    public static final String BIGWIG_DB = "bigwig.db";

    /**
     * Constructor.
     *
     * @param bigwigPath  Path to the Big Wig file
     * @throws IOException
     */
    public BigWigManager(Path bigwigPath) throws IOException {
        this.bigWigFilePath = bigwigPath;

        init();
    }

    @Deprecated
    public BigWigManager(Path bigwigPath, Path indexPath) throws IOException {
        this.bigWigFilePath = bigwigPath;
        this.indexPath = indexPath;

        init();
    }

    private void init() throws IOException {
        FileUtils.checkPath(this.bigWigFilePath);

        bbFileReader = new BBFileReader(this.bigWigFilePath.toString());
        zoomWindowSizes = new ArrayList<>();
        for (int zoomLevel = 1; zoomLevel <= bbFileReader.getZoomLevelCount(); zoomLevel++) {
            zoomWindowSizes.add(bbFileReader.getZoomLevels().getZoomLevelHeader(zoomLevel).getReductionLevel());
        }
    }

    /**
     * Query by a given region.
     *
     * @param region    Region target
     * @return          Array of floating values for that region
     * @throws IOException
     */
    public float[] query(Region region) throws IOException {
        BigWigIterator bigWigIterator = iterator(region);
        float[] values = new float[region.getEnd() - region.getStart() + 1];
        while (bigWigIterator.hasNext()) {
            WigItem wigItem = bigWigIterator.next();
            for (int i = wigItem.getStartBase(), j = wigItem.getStartBase() - region.getStart(); i <= wigItem.getEndBase(); i++, j++) {
                values[j] = wigItem.getWigValue();
            }
        }
        return values;
    }

    /**
     * Get the iterator for the given region.
     *
     * @param region    Region target
     * @return          Big Wig file iterator
     */
    public BigWigIterator iterator(Region region) {
        // Sanity check
        WigUtils.validateRegion(region, bbFileReader);

        return bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(), region.getChromosome(), region.getEnd() + 1, false);
    }

    public ZoomLevelIterator iterator(Region region, int zoomLevel) {
        // Sanity check
        WigUtils.validateRegion(region, bbFileReader);

        return bbFileReader.getZoomLevelIterator(zoomLevel, region.getChromosome(), region.getStart(), region.getChromosome(), region.getEnd(), false);
    }

    public float[] groupBy(Region region, int windowSize) {
        int zoomLevel = -1;
        for (int level = 0; level < zoomWindowSizes.size(); level++) {
            if (windowSize < zoomWindowSizes.get(level)) {
                break;
            }
            zoomLevel++;
        }

        // Calculate the number of needed windows, ensure windowSize => 1
        windowSize = Math.max(1, windowSize);
        int start = region.getStart();
        int end = region.getEnd() % windowSize == 0 ? region.getEnd() : (region.getEnd() / windowSize + 1) * windowSize;
        int numWindows = (end - start + 1) / windowSize;
        float[] chunks = new float[numWindows];

        if (zoomLevel == -1) {
            // No zoom level available. This can happen because there are not zoom levels or the window size is too small
            BigWigIterator bigWigIterator = iterator(new Region(region.getChromosome(), start, end));
            WigItem wItem;
            int length, chunkStart, chunkEnd;
            while (bigWigIterator.hasNext()) {
                wItem = bigWigIterator.next();
                chunkStart = (Math.max(start, wItem.getStartBase()) - start) / windowSize;
                chunkEnd = (Math.min(end, wItem.getEndBase()) - start - 1) / windowSize;
                for (int chunk = chunkStart; chunk <= chunkEnd; chunk++) {
                    length = Math.min(wItem.getEndBase() - start, chunk * windowSize + windowSize)
                            - Math.max(wItem.getStartBase() - start, chunk * windowSize);
                    chunks[chunk] += (wItem.getWigValue() * length);
                }
            }
        } else {
            // We get the zoom iterator, we need to increment by 1.
            ZoomLevelIterator zoomIterator = iterator(new Region(region.getChromosome(), start, end), zoomLevel + 1);
            ZoomDataRecord wItem;
            int length, chunkStart, chunkEnd;
            while (zoomIterator.hasNext()) {
                wItem = zoomIterator.next();
                chunkStart = (Math.max(start, wItem.getChromStart()) - start) / windowSize;
                chunkEnd = (Math.min(end, wItem.getChromEnd()) - start - 1) / windowSize;
                for (int chunk = chunkStart; chunk <= chunkEnd; chunk++) {
                    length = Math.min(wItem.getChromEnd() - start, chunk * windowSize + windowSize)
                            - Math.max(wItem.getChromStart() - start, chunk * windowSize);
                    chunks[chunk] += (wItem.getMeanVal() * length);
                }
            }
        }

        for (int i = 0; i < chunks.length; i++) {
            chunks[i] /= windowSize;
        }

        return chunks;
    }

    public List<Integer> getZoomWindowSizes() {
        return zoomWindowSizes;
    }
}

