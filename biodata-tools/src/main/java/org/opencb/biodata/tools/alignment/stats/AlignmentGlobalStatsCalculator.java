/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.alignment.stats;

import java.util.List;

/**
 * Created by pfurio on 28/10/16.
 */
public abstract class AlignmentGlobalStatsCalculator<T> {

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

    public AlignmentGlobalStats compute(T alignment) {
        AlignmentGlobalStats stats = new AlignmentGlobalStats();

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

    public void update(AlignmentGlobalStats src, AlignmentGlobalStats dest) {
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
