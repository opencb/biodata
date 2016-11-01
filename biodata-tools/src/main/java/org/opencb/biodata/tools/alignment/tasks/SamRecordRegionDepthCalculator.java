package org.opencb.biodata.tools.alignment.tasks;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;

import java.util.List;

/**
 * Created by jtarraga on 28/10/16.
 */
public class SamRecordRegionDepthCalculator extends RegionDepthCalculator<SAMRecord> {

    @Override
    public RegionDepth compute(SAMRecord sr) {
        if (sr.getReadUnmappedFlag()) {
            return new RegionDepth();
        }

        // compute the region size according to the cigar code
        int size = computeSizeByCigar(sr.getCigar());
        if (size == 0) {
            return new RegionDepth();
        }

        return computeRegionDepth(sr, size);
    }

    @Override
    public List<RegionDepth> computeAsList(SAMRecord sr) {
        return super.splitRegionDepthByChunks(compute(sr));
    }

    private RegionDepth computeRegionDepth(SAMRecord sr, int size) {
        RegionDepth regionDepth = new RegionDepth(sr.getReferenceName(), sr.getStart(),
                sr.getStart() / RegionDepth.CHUNK_SIZE, size);

        // update array (counter)
        int arrayPos = 0;
        for (CigarElement ce: sr.getCigar().getCigarElements()) {
            switch (ce.getOperator().toString()) {
                case "M":
                case "=":
                case "X":
                    for (int i = 0; i < ce.getLength(); i++) {
                        regionDepth.array[arrayPos++]++;
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

        return regionDepth;
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
