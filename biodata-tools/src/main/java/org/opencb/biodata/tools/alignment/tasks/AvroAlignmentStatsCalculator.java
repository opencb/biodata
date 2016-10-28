package org.opencb.biodata.tools.alignment.tasks;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.ReadAlignment;

import java.util.List;

/**
 * Created by jtarraga on 25/05/15.
 */
public class AvroAlignmentStatsCalculator extends AlignmentStatsCalculator<ReadAlignment> {

    public AvroAlignmentStatsCalculator() {
        super();
    }

    @Override
    public boolean isProperlyPaired(ReadAlignment alignment) {
        return !alignment.getImproperPlacement();
    }

    @Override
    public int getInsertSize(ReadAlignment alignment) {
        return alignment.getFragmentLength();
    }

    @Override
    public boolean isFirstOfPair(ReadAlignment alignment) {
        return alignment.getReadNumber() == 0;
    }

    @Override
    public boolean isSecondOfPair(ReadAlignment alignment) {
        return alignment.getReadNumber() == alignment.getNumberReads() - 1;
    }

    @Override
    public int getMappingQuality(ReadAlignment alignment) {
        return alignment.getAlignment().getMappingQuality();
    }

    @Override
    public String getAlignedSequence(ReadAlignment alignment) {
        return alignment.getAlignedSequence().toString();
    }

    @Override
    public List<Integer> getAlignedQuality(ReadAlignment alignment) {
        return alignment.getAlignedQuality();
    }

    @Override
    public CIGAR getActiveCigars(ReadAlignment alignment) {
        CIGAR ret = new CIGAR();

        List<CigarUnit> cigar = alignment.getAlignment().getCigar();
        if (cigar != null) {
            for (CigarUnit element: cigar) {
                switch(element.getOperation()) {
                    case CLIP_HARD:
                        ret.hard = true;
                        break;
                    case CLIP_SOFT:
                        ret.soft = true;
                        break;
                    case DELETE:
                        ret.del = true;
                        break;
                    case INSERT:
                        ret.in = true;
                        break;
                    case PAD:
                        ret.pad = true;
                        break;
                    case SKIP:
                        ret.skip = true;
                        break;
                    default:
                        break;
                }
            }
        }
        return ret;
    }

    @Override
    public int getNumberOfMismatches(ReadAlignment alignment) {
        if (alignment.getInfo() != null) {
            List<String> values = alignment.getInfo().get("NM");
            if (values != null) {
                return Integer.parseInt(values.get(1).toString());
            }
        }
        return 0;
    }

    @Override
    public boolean isMapped(ReadAlignment alignment) {
        return alignment.getAlignment() != null;
    }

}
