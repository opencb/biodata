package org.opencb.biodata.tools.alignment.tasks;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.sequence.tasks.SequenceStatsCalculator;

import java.util.List;

/**
 * Created by jtarraga on 25/05/15.
 */
public class AlignmentStatsCalculator {

    public AlignmentStats compute(ReadAlignment ra) {
        AlignmentStats stats = new AlignmentStats();

        if (ra.getAlignment() != null) {
            // mapped
            stats.numMapped++;

            int value;
            LinearAlignment la = (LinearAlignment) ra.getAlignment();

            //System.out.println("chr " + la.getPosition().getReferenceName().toString() + " : " + la.getPosition().getPosition() + ", " + ra.getAlignedSequence().length());

            // num. mismatches
            if (ra.getInfo() != null) {
                List<String> values = ra.getInfo().get("NM");
                if (values != null) {
                    stats.NM = Integer.parseInt(values.get(1).toString());
                }
            }

//			pos = la.getPosition().getPosition();
//			cigar = la.getCigar();

            // clipping, indels...
            List<CigarUnit> cigar = la.getCigar();
            if (cigar != null) {
                boolean hard = false, soft = false, in = false, del = false, pad = false, skip = false;
                for (CigarUnit element: cigar) {
                    switch(element.getOperation()) {
                        case CLIP_HARD:
                            hard = true;
                            break;
                        case CLIP_SOFT:
                            soft = true;
                            break;
                        case DELETE:
                            del = true;
                            break;
                        case INSERT:
                            in = true;
                            break;
                        case PAD:
                            pad = true;
                            break;
                        case SKIP:
                            skip = true;
                            break;
                        default:
                            break;
                    }
                }
                if (hard) stats.numHardC++;
                if (soft) stats.numSoftC++;
                if (in) stats.numIn++;
                if (del) stats.numDel++;
                if (pad) stats.numPad++;
                if (skip) stats.numSkip++;
            }

            // paired, first, second
            if (!ra.getImproperPlacement()) {
                stats.numPaired++;

                // insert
                int insert = Math.abs(ra.getFragmentLength());
                value = 1;
                stats.accInsert += insert;
                if (stats.insertMap.containsKey(insert)) {
                    value += stats.insertMap.get(insert);
                }
                stats.insertMap.put(insert, value);

            }
            if (ra.getReadNumber() == 0) {
                stats.numMappedFirst++;
            }
            if (ra.getReadNumber() == ra.getNumberReads() - 1) {
                stats.numMappedSecond++;
            }

            // mapping quality
            int mappingQuality = la.getMappingQuality();
            value = 1;
            stats.accMappingQuality += mappingQuality;
            if (stats.mappingQualityMap.containsKey(mappingQuality)) {
                value += stats.mappingQualityMap.get(mappingQuality);
            }
            stats.mappingQualityMap.put(mappingQuality, value);

        } else {
            // unmapped
            stats.numUnmapped++;
        }

        SequenceStatsCalculator calculator = new SequenceStatsCalculator();
        stats.seqStats = calculator.compute(ra.getAlignedSequence().toString(), ra.getAlignedQuality(), 0);

        return stats;
    }

    public void update(AlignmentStats src, AlignmentStats dest) {
        int value;

        dest.numMapped += src.numMapped;
        dest.numUnmapped += src.numUnmapped;
        dest.numPaired += src.numPaired;
        dest.numMappedFirst += src.numMappedFirst;
        dest.numMappedSecond += src.numMappedSecond;

        dest.NM += src.NM;

        dest.numHardC += src.numHardC;
        dest.numSoftC += src.numSoftC;
        dest.numIn += src.numIn;
        dest.numDel += src.numDel;
        dest.numPad += src.numPad;
        dest.numSkip += src.numSkip;

        dest.accMappingQuality += src.accMappingQuality;
        for (int key : src.mappingQualityMap.keySet()) {
            value = src.mappingQualityMap.get(key);
            if (dest.mappingQualityMap.containsKey(key)) {
                value += dest.mappingQualityMap.get(key);
            }
            dest.mappingQualityMap.put(key, value);
        }

        dest.accInsert += src.accInsert;
        for (int key : src.insertMap.keySet()) {
            value = src.insertMap.get(key);
            if (dest.insertMap.containsKey(key)) {
                value += dest.insertMap.get(key);
            }
            dest.insertMap.put(key, value);
        }

        SequenceStatsCalculator calculator = new SequenceStatsCalculator();
        calculator.update(src.seqStats, dest.seqStats);
    }
}
