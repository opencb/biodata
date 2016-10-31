package org.opencb.biodata.tools.alignment.tasks;

import htsjdk.samtools.SAMRecord;
import org.junit.Test;
import org.opencb.biodata.tools.alignment.AlignmentManager;
import org.opencb.biodata.tools.alignment.iterators.AlignmentIterator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jtarraga on 31/10/16.
 */
public abstract class RegionDepthCalculatorTest {

    @Test
    public void calculateDepthAvroVSSamRecord() throws Exception {
        // SAM depth calculator
        SamRecordRegionDepthCalculator samCalculator = new SamRecordRegionDepthCalculator();

        Path inputPath = Paths.get(getClass().getResource("/HG00096.chrom20.small.bam").toURI());
        AlignmentManager alignmentManager = new AlignmentManager(inputPath);

        List<RegionDepth> list;
        RegionDepth currChunk = null, nextChunk = null;

        LinkedList<RegionDepth> regionList = new LinkedList<>();

        try(AlignmentIterator<SAMRecord> iterator = alignmentManager.iterator()) {
            while (iterator.hasNext()) {
                list = samCalculator.computeAsList(iterator.next());
                RegionDepth depth = list.get(0);
                if (currChunk == null) {
                    currChunk = new RegionDepth(depth.chrom, depth.chunk * depth.size, depth.chunk, depth.size);
                }
                if (depth.chunk == currChunk.chunk) {
                    samCalculator.updateChunkDepth(depth, depth.chunk, currChunk);
                } else if (depth.chunk > currChunk.chunk) {
                    // current chunk is complete now, save it and swap chunks
                    // currChunk.save();
                    currChunk = nextChunk;
                    nextChunk = null;
                } else {
                    // error: bam is not sorted
                }
                for (int i = 1; i < list.size(); i++) {
                    depth = list.get(i);
                    if (depth.chunk == currChunk.chunk) {
                        samCalculator.updateChunkDepth(depth, depth.chunk, currChunk);
                    } else if (depth.chunk > currChunk.chunk) {
                        if (nextChunk == null) {
                            nextChunk = new RegionDepth(depth.chrom, depth.chunk * depth.size, depth.chunk, depth.size);
                        }
                        if (depth.chunk == nextChunk.chunk) {
                            samCalculator.updateChunkDepth(depth, depth.chunk, nextChunk);
                        } else {
                            // error: bam is not sorted
                        }
                    } else {
                        // error: bam is not sorted
                    }
                }
            }
        }
    }
}
