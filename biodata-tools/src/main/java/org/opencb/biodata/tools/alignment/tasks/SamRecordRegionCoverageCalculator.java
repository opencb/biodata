package org.opencb.biodata.tools.alignment.tasks;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

import java.util.List;

/**
 * Created by jtarraga on 28/10/16.
 */
public class SamRecordRegionCoverageCalculator extends RegionCoverageCalculator<SAMRecord> {

    @Override
    public RegionCoverage compute(SAMRecord sr) {
        if (sr.getReadUnmappedFlag()) {
            return new RegionCoverage();
        }

        // compute the region arraySize according to the cigar code
        int size = computeSizeByCigar(sr.getCigar());
        if (size == 0) {
            return new RegionCoverage();
        }

        return computeRegionCoverage(sr, size);
    }

    /*
    @Override
    public List<RegionCoverage> computeAsList(SAMRecord sr, int chunkSize) {
        return super.splitRegionCoverageByChunks(compute(sr), chunkSize);
    }
    */

    private RegionCoverage computeRegionCoverage(SAMRecord sr, int size) {
        RegionCoverage regionCoverage = new RegionCoverage(sr.getReferenceName(),
                sr.getStart(), sr.getStart() + size - 1);

        // update array (counter)
        int arrayPos = 0;
        for (CigarElement ce: sr.getCigar().getCigarElements()) {
            switch (ce.getOperator().toString()) {
                case "M":
                case "=":
                case "X":
                    for (int i = 0; i < ce.getLength(); i++) {
                        regionCoverage.array[arrayPos++]++;
                    }
                    break;
                case "N":
                case "D":
                    arrayPos += ce.getLength();
                    break;
                default:
                    break;
            }
        }

        return regionCoverage;
    }

    private int computeSizeByCigar(Cigar cigar) {
        int size = 0;
        for (CigarElement ce: cigar.getCigarElements()) {
            switch (ce.getOperator().toString()) {
                case "M":
                case "=":
                case "X":
                case "N":
                case "D":
                    size += ce.getLength();
                    break;
                default:
                    break;
            }
        }
        return size;
    }
}
