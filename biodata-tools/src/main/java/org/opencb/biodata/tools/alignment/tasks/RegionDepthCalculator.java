package org.opencb.biodata.tools.alignment.tasks;

import org.opencb.biodata.models.alignment.RegionCoverage;

import java.util.ArrayList;
import java.util.List;


public abstract class RegionDepthCalculator<T> {

    public abstract RegionCoverage compute(T alignment);

    public abstract List<RegionCoverage> computeAsList(T ra, int chunkSize);

    public void updateChunkDepth(RegionCoverage regionCoverage, RegionCoverage chunkDepth, int chunk, int chunkSize) {
        short value;

        assert(regionCoverage.getChromosome().equals(chunkDepth.getChromosome()));
//        assert(src.chunk == chunkDepth.chunk);

        int start = Math.max(regionCoverage.getStart(), chunk * chunkSize);
        int end = Math.min(regionCoverage.getEnd(), (chunk + 1) * chunkSize - 1);

        int srcOffset = regionCoverage.getStart();
        int destOffset = chunk * chunkSize;

        for (int i = start ; i <= end; i++) {
            value = regionCoverage.getValues()[i - srcOffset];
            chunkDepth.getValues()[i - destOffset] += value;
        }
    }


    protected List<RegionCoverage> splitRegionDepthByChunks(RegionCoverage regionCoverage, int chunkSize) {
        List<RegionCoverage> regions = new ArrayList<>();
        if (regionCoverage.getEnd() - regionCoverage.getStart() == 0) {
            return regions;
        }

        int startChunk = regionCoverage.getStart() / chunkSize;
        int endChunk = (regionCoverage.getEnd()) / chunkSize;

        if (startChunk == endChunk) {
            regions.add(regionCoverage);
            return regions;
        }

        short value;
        int start, end, acc;
        RegionCoverage dest;
        for (int chunk = startChunk; chunk <= endChunk; chunk++) {
            start = Math.max(regionCoverage.getStart(), chunk * chunkSize);
            end = Math.min(regionCoverage.getEnd(), (chunk + 1) * chunkSize - 1);

//            dest = new RegionDepth(src.chrom, start, chunk, (end - start + 1));
            dest = new RegionCoverage(regionCoverage.getChromosome(), start, (end - start + 1));

            acc = 0;
            start -= regionCoverage.getStart();
            end -= regionCoverage.getStart();
            for (int i = start, j = 0; i <= end; i++, j++) {
                value = regionCoverage.getValues()[i];
                dest.getValues()[j] = value;
                acc += value;
            }

            if (acc > 0) {
                regions.add(dest);
            }
        }

        return regions;
    }
}
