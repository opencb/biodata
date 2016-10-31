package org.opencb.biodata.tools.alignment.tasks;

import ga4gh.Reads;
import htsjdk.samtools.SAMRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public abstract class RegionDepthCalculator<T> {

    public abstract RegionDepth compute(T alignment);
    public abstract List<RegionDepth> computeAsList(T ra);

    public void updateChunkDepth(RegionDepth src, long chunk, RegionDepth chunkDepth) {
        short value;

        assert(src.chrom.equals(chunkDepth.chrom));
        assert(src.chunk == chunkDepth.chunk);

        int start = (int) Math.max(src.position, chunk * RegionDepth.CHUNK_SIZE);
        int end = (int) Math.min(src.position + src.size - 1, (chunk + 1) * RegionDepth.CHUNK_SIZE - 1);

        int srcOffset = (int) src.position;
        int destOffset = (int) (chunk * RegionDepth.CHUNK_SIZE);

        for (int i = start ; i <= end; i++) {
            value = src.array[i - srcOffset];
            chunkDepth.array[i - destOffset] += value;
        }
    }

    /*
     */
    protected List<RegionDepth> splitRegionDepthByChunks(RegionDepth src) {
        List<RegionDepth> regions = new ArrayList<>();
        if (src.size == 0) {
            return regions;
        }

        int startChunk = src.position / RegionDepth.CHUNK_SIZE;
        int endChunk = (src.position + src.size - 1) / RegionDepth.CHUNK_SIZE;

        if (startChunk == endChunk) {
            regions.add(src);
            return regions;
        }

        short value;
        int start, end, acc;
        RegionDepth dest;
        for (int chunk = startChunk; chunk <= endChunk; chunk++) {
            start = Math.max(src.position, chunk * RegionDepth.CHUNK_SIZE);
            end = Math.min(src.position + src.size - 1, (chunk + 1) * RegionDepth.CHUNK_SIZE - 1);

            dest = new RegionDepth(src.chrom, start, chunk, (end - start + 1));

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
}
