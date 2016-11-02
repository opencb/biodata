package org.opencb.biodata.tools.alignment.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public abstract class RegionCoverageCalculator<T> {

    public abstract RegionCoverage compute(T alignment);
//    public abstract List<RegionCoverage> computeAsList(T ra, int chunkSize);

    public void update(RegionCoverage src, RegionCoverage dest) {
        if (!src.getChromosome().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        int start = Math.max(src.getStart(), dest.getStart());
        int end = Math.min(src.getEnd(), dest.getEnd());

        for (int i = start ; i <= end; i++) {
            dest.array[i - dest.getStart()] += src.array[i - src.getStart()];
        }
    }

    /*
    public void updateChunkCoverage(RegionCoverage src, RegionCoverage chunkDepth, int chunk, int chunkSize) {
        short value;

        assert(src.chrom.equals(chunkDepth.chrom));
//        assert(src.chunk == chunkDepth.chunk);

        int start = (int) Math.max(src.position, chunk * chunkSize);
        int end = (int) Math.min(src.position + src.arraySize - 1, (chunk + 1) * chunkSize - 1);

        int srcOffset = (int) src.position;
        int destOffset = (int) (chunk * chunkSize);

        for (int i = start ; i <= end; i++) {
            value = src.array[i - srcOffset];
            chunkDepth.array[i - destOffset] += value;
        }
    }

    protected List<RegionCoverage> splitRegionCoverageByChunks(RegionCoverage src, int chunkSize) {
        List<RegionCoverage> regions = new ArrayList<>();
        if (src.arraySize == 0) {
            return regions;
        }

        int startChunk = src.getStart() / chunkSize;
        int endChunk = src.getEnd() / chunkSize;

        if (startChunk == endChunk) {
            regions.add(src);
            return regions;
        }

        short value;
        int start, end, acc;
        RegionCoverage dest;
        for (int chunk = startChunk; chunk <= endChunk; chunk++) {
            start = Math.max(src.getStart(), chunk * chunkSize);
            end = Math.min(src.getEnd(), (chunk + 1) * chunkSize - 1);

//            dest = new RegionCoverage(src.chrom, start, chunk, (end - start + 1));
            dest = new RegionCoverage(src.getChromosome(), start, (end - start + 1));

            acc = 0;
            start -= src.position;
            end -= src.position;
            for (int i = start, j = 0; i <= end; i++, j++) {
                value = src.array[i];
                dest.array[j] = value;
                acc += value;
            }

            if (acc > 0) {
                regions.add(dest);
            }
        }

        return regions;
    }
     */
}
