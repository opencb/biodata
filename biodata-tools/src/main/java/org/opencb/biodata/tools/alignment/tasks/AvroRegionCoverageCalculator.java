package org.opencb.biodata.tools.alignment.tasks;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;

import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public class AvroRegionCoverageCalculator extends RegionCoverageCalculator<ReadAlignment> {

    @Override
    public RegionCoverage compute(ReadAlignment ra) {
        LinearAlignment la = (LinearAlignment) ra.getAlignment();
        if (la == null) {
            return new RegionCoverage();
        }

        // compute the region arraySize according to the cigar code
        int size = computeSizeByCigar(la.getCigar());
        if (size == 0) {
            return new RegionCoverage();
        }

        return computeRegionCoverage(la, size);
    }

    /*
    @Override
    public List<RegionCoverage> computeAsList(ReadAlignment ra, int chunkSize) {
        return super.splitRegionDepthByChunks(compute(ra), chunkSize);
    }
     */

    private RegionCoverage computeRegionCoverage(LinearAlignment la, int size) {
        RegionCoverage regionCoverage = new RegionCoverage(la.getPosition().getReferenceName().toString(),
                la.getPosition().getPosition().intValue(), la.getPosition().getPosition().intValue() + size - 1);

        // update array (counter)
        int arrayPos = 0;
        for (CigarUnit cu: la.getCigar()) {
            switch (cu.getOperation()) {
                case ALIGNMENT_MATCH:
                case SEQUENCE_MATCH:
                case SEQUENCE_MISMATCH:
                    for (int i = 0; i < cu.getOperationLength(); i++) {
                        regionCoverage.array[arrayPos++]++;
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

        return regionCoverage;
    }

    /*
     * compute the region arraySize according to the cigar code
     */
    private int computeSizeByCigar(List<CigarUnit> cigar) {
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
}
