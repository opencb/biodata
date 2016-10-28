package org.opencb.biodata.tools.alignment.tasks;

import org.opencb.biodata.tools.sequence.tasks.SequenceStatsCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfurio on 28/10/16.
 */
public abstract class AlignmentStatsCalculator<T> {

    class CIGAR {
        boolean hard = false;
        boolean soft = false;
        boolean in = false;
        boolean del = false;
        boolean pad = false;
        boolean skip = false;

        public CIGAR() {
        }
    }

    abstract boolean isProperlyPaired(T alignment);

    abstract int getInsertSize(T alignment);

    abstract boolean isFirstOfPair(T alignment);

    abstract boolean isSecondOfPair(T alignment);

    abstract int getMappingQuality(T alignment);

    abstract String getAlignedSequence(T alignment);

    abstract List<Integer> getAlignedQuality(T alignment);

    abstract CIGAR getActiveCigars(T alignment);

    abstract int getNumberOfMismatches(T alignment);

    abstract boolean isMapped(T alignment);

    public AlignmentStats compute(T alignment) {
        AlignmentStats stats = new AlignmentStats();

        if (isMapped(alignment)) {
            // Mapped
            stats.numMapped++;

            // Get number of mismatches
            stats.NM = getNumberOfMismatches(alignment);

            CIGAR cigar = getActiveCigars(alignment);
            if (cigar.hard) stats.numHardC++;
            if (cigar.soft) stats.numSoftC++;
            if (cigar.in) stats.numIn++;
            if (cigar.del) stats.numDel++;
            if (cigar.pad) stats.numPad++;
            if (cigar.skip) stats.numSkip++;

            int value;
            if (isProperlyPaired(alignment)) {
                stats.numPaired++;

                // insert
                int insert = Math.abs(getInsertSize(alignment));
                value = 1;
                stats.accInsert += insert;
                if (stats.insertMap.containsKey(insert)) {
                    value += stats.insertMap.get(insert);
                }
                stats.insertMap.put(insert, value);
            }

            if (isFirstOfPair(alignment)) {
                stats.numMappedFirst++;
            }
            if (isSecondOfPair(alignment)) {
                stats.numMappedSecond++;
            }

            // mapping quality
            int mappingQuality = getMappingQuality(alignment);
            value = 1;
            stats.accMappingQuality += mappingQuality;
            if (stats.mappingQualityMap.containsKey(mappingQuality)) {
                value += stats.mappingQualityMap.get(mappingQuality);
            }
            stats.mappingQualityMap.put(mappingQuality, value);

        } else {
            // Unmapped
            stats.numUnmapped++;
        }

        SequenceStatsCalculator calculator = new SequenceStatsCalculator();
        stats.seqStats = calculator.compute(getAlignedSequence(alignment), getAlignedQuality(alignment), 0);

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
