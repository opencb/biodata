package org.opencb.biodata.tools.alignment.coverage;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.models.alignment.RegionCoverage;

import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public class AvroRegionCoverageCalculator extends RegionCoverageCalculator<ReadAlignment> {

    public AvroRegionCoverageCalculator() {
        super(0);
    }

    public AvroRegionCoverageCalculator(int minBaseQuality) {
        super(minBaseQuality);
    }

    @Override
    public void update(ReadAlignment ra, RegionCoverage dest) {
        LinearAlignment la = ra.getAlignment();
        if ( la == null || !la.getPosition().getReferenceName().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        // counters for bases and qualities
        int refPos = Math.toIntExact(la.getPosition().getPosition());
        int qualityPos = 0;

        List<Integer> qualities = ra.getAlignedQuality();
        short[] values = dest.getValues();

        for (CigarUnit cu: la.getCigar()) {
            switch (cu.getOperation()) {
                case ALIGNMENT_MATCH:
                case SEQUENCE_MATCH:
                case SEQUENCE_MISMATCH:
                    for (int i = 0; i < cu.getOperationLength(); i++) {
                        if (refPos >= dest.getStart() && refPos <= dest.getEnd()) {
                            if (qualities.get(qualityPos) >= minBaseQuality) {
                                values[refPos - dest.getStart()]++;
                            }
                        }
                        qualityPos++;
                        refPos++;
                    }
                    break;
                case SKIP:
                case DELETE:
                    refPos += cu.getOperationLength();
                    break;
                case CLIP_SOFT:
                case INSERT:
                    qualityPos += cu.getOperationLength();
                    break;
                default:
                    break;
            }
        }
    }
}
