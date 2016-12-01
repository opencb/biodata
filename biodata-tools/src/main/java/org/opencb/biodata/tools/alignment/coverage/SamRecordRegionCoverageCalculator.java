package org.opencb.biodata.tools.alignment.coverage;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;
import org.opencb.biodata.models.alignment.RegionCoverage;

/**
 * Created by jtarraga on 28/10/16.
 */
public class SamRecordRegionCoverageCalculator extends RegionCoverageCalculator<SAMRecord> {

    public SamRecordRegionCoverageCalculator() {
        super(0);
    }

    public SamRecordRegionCoverageCalculator(int minBaseQuality) {
        super(minBaseQuality);
    }

    @Override
    public void update(SAMRecord sr, RegionCoverage dest) {
        if (sr.getReadUnmappedFlag() || !sr.getReferenceName().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        // counters for bases and qualities
        int refPos = sr.getAlignmentStart();
        int qualityPos = 0;

        byte[] qualities = sr.getBaseQualities();
        short[] values = dest.getValues();

        for (CigarElement ce: sr.getCigar().getCigarElements()) {
            switch (ce.getOperator().toString()) {
                case "M":
                case "=":
                case "X":
                    for (int i = 0; i < ce.getLength(); i++) {
                        if (refPos >= dest.getStart() && refPos <= dest.getEnd()) {
                            if (qualities[qualityPos] >= minBaseQuality) {
                                values[refPos - dest.getStart()]++;
                            }
                        }
                        qualityPos++;
                        refPos++;
                    }
                    break;
                case "N":
                case "D":
                    refPos += ce.getLength();
                    break;
                case "S":
                case "I":
                    qualityPos += ce.getLength();
                    break;
                default:
                    break;
            }
        }
    }
}
