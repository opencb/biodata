package org.opencb.biodata.tools.alignment.tasks;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public class AvroRegionDepthCalculator extends RegionDepthCalculator<ReadAlignment>{

    @Override
    public RegionDepth compute(ReadAlignment ra) {
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
    @Override
    public List<RegionDepth> computeAsList(ReadAlignment ra, int chunkSize) {
        return super.splitRegionDepthByChunks(compute(ra), chunkSize);
    }

    private RegionDepth computeRegionDepth(LinearAlignment la, int size) {
        RegionDepth regionDepth = new RegionDepth(la.getPosition().getReferenceName().toString(),
                la.getPosition().getPosition().intValue(), size);

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
     * compute the region size according to the cigar code
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
