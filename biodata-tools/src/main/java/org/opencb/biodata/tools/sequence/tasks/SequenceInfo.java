package org.opencb.biodata.tools.sequence.tasks;

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
        numA = 0;
        numT = 0;
        numG = 0;
        numC = 0;
        numN = 0;
        numQual = 0;
        accQual = 0;
    }
}
