package org.opencb.biodata.tools.sequence.tasks;

import java.util.HashMap;

/**
 * Created by jtarraga on 22/05/15.
 */
public class SequenceStats {

    public int numSeqs;
    public int numA;
    public int numT;
    public int numG;
    public int numC;
    public int numN;
    public int minSeqLength;
    public int maxSeqLength;
    public int accSeqQual;

    public HashMap<Integer, Integer> lengthMap;
    public HashMap<Integer, SequenceInfo> infoMap;

    public SequenceKmers kmers;

    public SequenceStats() {
        numSeqs = 0;
        numA = 0;
        numT = 0;
        numG = 0;
        numC = 0;
        numN = 0;
        minSeqLength = Integer.MAX_VALUE;
        maxSeqLength = 0;
        accSeqQual = 0;
        lengthMap = new HashMap<>();
        infoMap = new HashMap<>();
        kmers = new SequenceKmers();
    }

    public SequenceStats(int kvalue) {
        numSeqs = 0;
        numA = 0;
        numT = 0;
        numG = 0;
        numC = 0;
        numN = 0;
        minSeqLength = Integer.MAX_VALUE;
        maxSeqLength = 0;
        accSeqQual = 0;
        lengthMap = new HashMap<>();
        infoMap = new HashMap<>();
        kmers = new SequenceKmers(kvalue);
    }

/*
    public SequenceStats(SequenceStats readStats) {
        set(readStats);
    }

    public void set(SequenceStats readStats) {
        if (readStats != null) {
            numSeqs = readStats.numSeqs;
            numA = readStats.numA;
            numT = readStats.numT;
            numG = readStats.numG;
            numC = readStats.numC;
            numN = readStats.numN;
            minSeqLength = readStats.minSeqLength;
            maxSeqLength = readStats.maxSeqLength;
            accSeqQual = readStats.accSeqQual;
            lengthMap = readStats.lengthMap;
            infoMap = readStats.infoMap;
            kmers = readStats.kmers;
        }
    }
*/

    public String toJSON() {
        StringBuilder res = new StringBuilder();
        res.append("{");
        res.append("\"num_reads\": " + numSeqs);
        res.append(", \"num_A\": " + numA);
        res.append(", \"num_T\": " + numT);
        res.append(", \"num_G\": " + numG);
        res.append(", \"num_C\": " + numC);
        res.append(", \"num_N\": " + numN);

        int mean_len = (numA + numT + numG + numC + numN) / numSeqs;
        res.append(", \"min_length\": " + minSeqLength);
        res.append(", \"mean_length\": " + mean_len);
        res.append(", \"max_length\": " + maxSeqLength);

        res.append(", \"mean_qual\": " + accSeqQual / numSeqs / mean_len);

        int i, size = lengthMap.size();
        res.append(", \"length_map_size\": " + size);
        res.append(", \"length_map_values\": [");

        i = 0;
        for(int key:lengthMap.keySet()) {
            res.append("[" + key + ", " + lengthMap.get(key) + "]");
            if (++i < size) res.append(", ");
        }
        res.append("]");

        size = infoMap.size();
        res.append(", \"info_map_size\": " + size);
        res.append(", \"info_map_values\": [");

        i = 0;
        for(int key:infoMap.keySet()) {
            final SequenceInfo info = infoMap.get(key);
            res.append("[" + key + ", " + info.numA + ", " + info.numT + ", " + info.numG + ", " + info.numC + ", " + info.numN + ", " + (1.0f * info.accQual / info.numQual) + "]");
            if (++i < size) res.append(", ");
        }
        res.append("]");

        // update kmers, if necessary
        if (kmers.kvalue > 0) {
            res.append(", \"kmers\": " + kmers.toJSON());
        }
        res.append("}");

        return res.toString();
    }
}
