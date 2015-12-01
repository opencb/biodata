package org.opencb.biodata.tools.alignment.tasks;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public class RegionDepthCalculator {

    private RegionDepth computeRegionDepth(LinearAlignment la, int size) {
        RegionDepth regionDepth;
        regionDepth = new RegionDepth(
                la.getPosition().getReferenceName().toString(),
                la.getPosition().getPosition().intValue(),
                la.getPosition().getPosition().intValue() / RegionDepth.CHUNK_SIZE,
                size);

        // update array (counter)
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
                case SKIP:
                case DELETE:
                    arrayPos += cu.getOperationLength();
                    break;
                default:
                    break;
            }
        }

        return regionDepth;
    }

    /*
<<<<<<< HEAD
     * compute the region size according to the cigar code
     */
    public int computeSizeByCigar(List<CigarUnit> cigar) {
        int size = 0;
        for (CigarUnit cu: cigar) {
            switch (cu.getOperation()) {
                case ALIGNMENT_MATCH:
                case SEQUENCE_MATCH:
                case SEQUENCE_MISMATCH:
                case SKIP:
                case DELETE:
                    size += cu.getOperationLength();
                    break;
                default:
                    break;
            }
        }
        return size;
    }

    /*
=======
>>>>>>> hotfix/0.4

     */
    public RegionDepth compute(ReadAlignment ra) {

        RegionDepth regionDepth;

        LinearAlignment la = (LinearAlignment) ra.getAlignment();
        if (la == null) {
            return new RegionDepth();
        }

        // compute the region size according to the cigar code
        int size = computeSizeByCigar(la.getCigar());
        if (size == 0) {
            return new RegionDepth();
        }

        return computeRegionDepth(la, size);
    }

    /*

     */
    public List<RegionDepth> computeAsList(ReadAlignment ra) {
        List<RegionDepth> regions = new ArrayList<RegionDepth>();

        RegionDepth src = compute(ra);
        if (src.size == 0) {
            return regions;
        }

        int startChunk = (int) (src.position / RegionDepth.CHUNK_SIZE);
        int endChunk = (int) ((src.position + src.size - 1) / RegionDepth.CHUNK_SIZE);

        if (startChunk == endChunk) {
            regions.add(src);
            return regions;
        }

        short value;
        int start, end, acc;
        RegionDepth dest;
        for (int chunk = startChunk; chunk <= endChunk; chunk++) {
            start = (int) Math.max(src.position, chunk * RegionDepth.CHUNK_SIZE);
            end = (int) Math.min(src.position + src.size - 1, (chunk + 1) * RegionDepth.CHUNK_SIZE - 1);

            dest = new RegionDepth(src.chrom, start, chunk, (int) (end - start + 1));

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

    public void updateChunk(RegionDepth src, long chunk, RegionDepth dest) {
        short value;
        int start = (int) Math.max(src.position, chunk * RegionDepth.CHUNK_SIZE);
        int end = (int) Math.min(src.position + src.size - 1, (chunk + 1) * RegionDepth.CHUNK_SIZE - 1);

        int srcOffset = (int) src.position;
        int destOffset = (int) (chunk * RegionDepth.CHUNK_SIZE);

        for (int i = start ; i <= end; i++) {
            value = src.array[i - srcOffset];
            dest.array[i - destOffset] += value;
        }
    }
}
