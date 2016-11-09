package org.opencb.biodata.tools.alignment.stats;

import org.opencb.biodata.models.sequence.Read;

import java.util.List;

/**
 * Created by jtarraga on 22/05/15.
 */
public class SequenceStatsCalculator {

    public SequenceStats compute(Read read) {
        return compute(read.getSequence().toString(), read.getQuality().toString(), 0);
    }

    public SequenceStats compute(Read read, int kvalue) {
        return compute(read.getSequence().toString(), read.getQuality().toString(), kvalue);
    }

    public SequenceStats compute(final String sequence, final String quality, int kvalue) {
        SequenceStats stats = new SequenceStats(kvalue);

        stats.numSeqs++;
        SequenceInfo info = null;
        //final String read = fastqRecord.getReadString();
        //final String quality = fastqRecord.getBaseQualityString();
        final int len = sequence.length();

        int accQual = 0;
        int qual = 0;

        // read length
        stats.lengthMap.put(len, stats.lengthMap.containsKey(len) ? stats.lengthMap.get(len) + 1: 1);
        if (len < stats.minSeqLength) {
            stats.minSeqLength = len;
        }
        if (len > stats.maxSeqLength) {
            stats.maxSeqLength = len;
        }

        for (int i=0; i < len; i++) {
            // info management
            if (stats.infoMap.containsKey(i)) {
                info = stats.infoMap.get(i);
            } else {
                info = new SequenceInfo();
                stats.infoMap.put(i, info);
            }

            // quality
            qual = (int) quality.charAt(i);
            accQual += qual;
            info.numQual++;
            info.accQual += qual;

            // nucleotide content
            switch (sequence.charAt(i)) {
                case 'A':
                case 'a': {
                    stats.numA++;
                    info.numA++;
                    break;
                }
                case 'T':
                case 't': {
                    stats.numT++;
                    info.numT++;
                    break;
                }
                case 'G':
                case 'g': {
                    stats.numG++;
                    info.numG++;
                    break;
                }
                case 'C':
                case 'c': {
                    stats.numC++;
                    info.numC++;
                    break;
                }
                default: {
                    stats.numN++;
                    info.numN++;
                    break;
                }
            } // end switch
        } // end for

        // read quality
        stats.accSeqQual += accQual;

        if (kvalue > 0) {
            stats.kmers = new SequenceKmersCalculator().compute(sequence, kvalue);
        }

        return stats;
    }

    public SequenceStats compute(final String sequence, final List<Integer> quality, int kvalue) {
        SequenceStats stats = new SequenceStats(kvalue);

        stats.numSeqs++;
        SequenceInfo info = null;
        //final String read = fastqRecord.getReadString();
        //final String quality = fastqRecord.getBaseQualityString();
        final int len = sequence.length();

        int accQual = 0;
        int qual = 0;

        // read length
        stats.lengthMap.put(len, stats.lengthMap.containsKey(len) ? stats.lengthMap.get(len) + 1: 1);
        if (len < stats.minSeqLength) {
            stats.minSeqLength = len;
        }
        if (len > stats.maxSeqLength) {
            stats.maxSeqLength = len;
        }

        for (int i=0; i < len; i++) {
            // info management
            if (stats.infoMap.containsKey(i)) {
                info = stats.infoMap.get(i);
            } else {
                info = new SequenceInfo();
                stats.infoMap.put(i, info);
            }

            // quality
            qual = quality.get(i);
            accQual += qual;
            info.numQual++;
            info.accQual += qual;

            // nucleotide content
            switch (sequence.charAt(i)) {
                case 'A':
                case 'a': {
                    stats.numA++;
                    info.numA++;
                    break;
                }
                case 'T':
                case 't': {
                    stats.numT++;
                    info.numT++;
                    break;
                }
                case 'G':
                case 'g': {
                    stats.numG++;
                    info.numG++;
                    break;
                }
                case 'C':
                case 'c': {
                    stats.numC++;
                    info.numC++;
                    break;
                }
                default: {
                    stats.numN++;
                    info.numN++;
                    break;
                }
            } // end switch
        } // end for

        // read quality
        stats.accSeqQual += accQual;

        if (kvalue > 0) {
            stats.kmers = new SequenceKmersCalculator().compute(sequence, kvalue);
        }

        return stats;
    }

    public void update(SequenceStats src, SequenceStats dest) {
        dest.numSeqs += src.numSeqs;
        dest.numA += src.numA;
        dest.numT += src.numT;
        dest.numG += src.numG;
        dest.numC += src.numC;
        dest.numN += src.numN;

        if (src.minSeqLength < dest.minSeqLength) {
            dest.minSeqLength = src.minSeqLength;
        }
        if (src.maxSeqLength > dest.maxSeqLength) {
            dest.maxSeqLength = src.maxSeqLength;
        }

        dest.accSeqQual += src.accSeqQual;

        int value;
        for(int key:src.lengthMap.keySet()) {
            value = src.lengthMap.get(key);
            if (dest.lengthMap.containsKey(key)) {
                value += dest.lengthMap.get(key);
            }
            dest.lengthMap.put(key, value);
        }

        SequenceInfo destInfo, info;
        for(int key:src.infoMap.keySet()) {
            if (dest.infoMap.containsKey(key)) {
                destInfo = dest.infoMap.get(key);
                info = src.infoMap.get(key);
                destInfo.numA += info.numA;
                destInfo.numT += info.numT;
                destInfo.numG += info.numG;
                destInfo.numC += info.numC;
                destInfo.numN += info.numN;
                destInfo.numQual += info.numQual;
                destInfo.accQual += info.accQual;
            } else {
                destInfo = src.infoMap.get(key);
            }
            dest.infoMap.put(key, destInfo);
        }

        // update kmers, if necessary
        if (dest.kmers.kvalue > 0) {
            new SequenceKmersCalculator().update(src.kmers, dest.kmers);
        }
    }
}
