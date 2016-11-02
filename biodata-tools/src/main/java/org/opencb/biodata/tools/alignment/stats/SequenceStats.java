package org.opencb.biodata.tools.alignment.stats;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public Map<Integer, Integer> lengthMap;
    public Map<Integer, SequenceInfo> infoMap;

    public SequenceKmers kmers;

    public SequenceStats() {
        minSeqLength = Integer.MAX_VALUE;
        lengthMap = new HashMap<>();
        infoMap = new HashMap<>();
        kmers = new SequenceKmers();
    }

    public SequenceStats(int kvalue) {
        minSeqLength = Integer.MAX_VALUE;
        lengthMap = new HashMap<>();
        infoMap = new HashMap<>();
        kmers = new SequenceKmers(kvalue);
    }

    public String toJSON() throws IOException {
        ObjectWriter objectWriter = new ObjectMapper().writer();
        return objectWriter.writeValueAsString(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SequenceStats{");
        sb.append("numSeqs=").append(numSeqs);
        sb.append(", numA=").append(numA);
        sb.append(", numT=").append(numT);
        sb.append(", numG=").append(numG);
        sb.append(", numC=").append(numC);
        sb.append(", numN=").append(numN);
        sb.append(", minSeqLength=").append(minSeqLength);
        sb.append(", maxSeqLength=").append(maxSeqLength);
        sb.append(", accSeqQual=").append(accSeqQual);
        sb.append(", lengthMap=").append(lengthMap);
        sb.append(", infoMap=").append(infoMap);
        sb.append(", kmers=").append(kmers);
        sb.append('}');
        return sb.toString();
    }
}
