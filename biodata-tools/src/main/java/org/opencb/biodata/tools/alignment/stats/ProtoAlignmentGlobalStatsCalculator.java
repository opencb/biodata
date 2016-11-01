package org.opencb.biodata.tools.alignment.stats;

import com.google.protobuf.ListValue;
import ga4gh.Common;
import ga4gh.Reads;

import java.util.List;

/**
 * Created by pfurio on 31/10/16.
 */
public class ProtoAlignmentGlobalStatsCalculator extends AlignmentGlobalStatsCalculator<Reads.ReadAlignment>  {

    public ProtoAlignmentGlobalStatsCalculator() {
        super();
    }

    @Override
    boolean isProperlyPaired(Reads.ReadAlignment alignment) {
        return !alignment.getImproperPlacement();
    }

    @Override
    int getInsertSize(Reads.ReadAlignment alignment) {
        return alignment.getFragmentLength();
    }

    @Override
    boolean isFirstOfPair(Reads.ReadAlignment alignment) {
        return alignment.getReadNumber() == 0;
    }

    @Override
    boolean isSecondOfPair(Reads.ReadAlignment alignment) {
        return alignment.getReadNumber() == alignment.getNumberReads() - 1;
    }

    @Override
    int getMappingQuality(Reads.ReadAlignment alignment) {
        return alignment.getAlignment().getMappingQuality();
    }

    @Override
    String getAlignedSequence(Reads.ReadAlignment alignment) {
        return alignment.getAlignedSequence();
    }

    @Override
    List<Integer> getAlignedQuality(Reads.ReadAlignment alignment) {
        return alignment.getAlignedQualityList();
    }

    @Override
    CIGAR getActiveCigars(Reads.ReadAlignment alignment) {
        CIGAR ret = new CIGAR();

        List<Common.CigarUnit> cigar = alignment.getAlignment().getCigarList();
        if (cigar != null) {
            for (Common.CigarUnit element: cigar) {
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
    int getNumberOfMismatches(Reads.ReadAlignment alignment) {
        if (alignment.getInfo() != null) {
            ListValue values = alignment.getInfo().get("NM");
            if (values != null && values.getValuesCount() == 1) {
                return (int) values.getValues(0).getNumberValue();
            }
        }
        return 0;
    }

    @Override
    boolean isMapped(Reads.ReadAlignment alignment) {
        return alignment.getAlignment() != null;
    }
}
