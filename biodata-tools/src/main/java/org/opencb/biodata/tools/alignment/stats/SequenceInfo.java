package org.opencb.biodata.tools.alignment.stats;

/**
 * Created by jtarraga on 22/05/15.
 */
public class SequenceInfo {
    public int numA;
    public int numT;
    public int numG;
    public int numC;
    public int numN;
    public int numQual;
    public int accQual;

    public SequenceInfo() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SequenceInfo{");
        sb.append("numA=").append(numA);
        sb.append(", numT=").append(numT);
        sb.append(", numG=").append(numG);
        sb.append(", numC=").append(numC);
        sb.append(", numN=").append(numN);
        sb.append(", numQual=").append(numQual);
        sb.append(", accQual=").append(accQual);
        sb.append('}');
        return sb.toString();
    }
}
