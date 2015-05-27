package org.opencb.biodata.tools.alignment.tasks;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;

import java.util.Arrays;

/**
 * Created by jtarraga on 26/05/15.
 */
public class RegionDepthCalculator {
    public RegionDepth compute(ReadAlignment ra) {

        RegionDepth regionDepth;

        LinearAlignment la = (LinearAlignment) ra.getAlignment();
        if (la == null) {
            return new RegionDepth();
        }

        regionDepth = new RegionDepth(la.getPosition().getReferenceName().toString(),
                la.getPosition().getPosition(),
                la.getPosition().getPosition() / RegionDepth.CHUNK_SIZE,
                RegionDepth.CHUNK_SIZE);

        int arrayPos = 0;
        for (CigarUnit cu: la.getCigar()) {
            switch (cu.getOperation()) {
                case ALIGNMENT_MATCH:
                case SEQUENCE_MATCH:
                case SEQUENCE_MISMATCH:
                    for (int i = 0; i < cu.getOperationLength(); i++) {
                        regionDepth.array[arrayPos++]++;
                    }
                    break;
                case CLIP_HARD:
                case CLIP_SOFT:
                case DELETE:
                case INSERT:
                case PAD:
                    break;
                case SKIP:
                    // resize
                    regionDepth.size += cu.getOperationLength();
                    regionDepth.array = Arrays.copyOf(regionDepth.array, regionDepth.size);
                    arrayPos += cu.getOperationLength();
                    break;
                default:
                    break;
            }
        }

        return regionDepth;
    }

    public void update(RegionDepth src, RegionDepth dest) {
        updateChunk(src, src.chunk, dest);
    }

    public void updateChunk(RegionDepth src, long chunk, RegionDepth dest) {
        int start = (int) Math.max(src.position, chunk * RegionDepth.CHUNK_SIZE);
        int end = (int) Math.min(src.position + src.size - 1, (chunk + 1) * RegionDepth.CHUNK_SIZE - 1);

        int srcOffset = (int) src.position;
        int destOffset = (int) (chunk * RegionDepth.CHUNK_SIZE);

        for (int i = start ; i <= end; i++) {
            dest.array[i - destOffset] += src.array[i - srcOffset];
        }
    }
}
