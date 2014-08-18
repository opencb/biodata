package org.opencb.biodata.models.alignment.stats;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cgonzalez@cipf.es&gt;
 */
public class RegionCoverage  {

    private short[] all;
    private short[] a;
    private short[] c;
    private short[] g;
    private short[] t;

    private String chromosome;
    private long start;     //Start of the coverage
    private long end;       //End of the coverage

    public RegionCoverage() { }

    public RegionCoverage(int length) {
        this.a = new short[length];
        this.c = new short[length];
        this.g = new short[length];
        this.t = new short[length];
        this.all = new short[length];
    }

    public RegionCoverage(short[] a, short[] c, short[] g, short[] t) {
        this.a = a;
        this.c = c;
        this.g = g;
        this.t = t;
        this.all = new short[a.length];
        for (int i = 0; i < a.length; i++) {
            this.all[i] = (short) (a[i] + c[i] + g[i] + t[i]);
        }
    }

    public RegionCoverage(short[] all, short[] a, short[] c, short[] g, short[] t) {
        this.all = all;
        this.a = a;
        this.c = c;
        this.g = g;
        this.t = t;
    }

    public short[] getA() {
        return a;
    }

    public void setA(short[] a) {
        this.a = a;
    }

    public short[] getAll() {
        return all;
    }

    public void setAll(short[] all) {
        this.all = all;
    }

    public short[] getC() {
        return c;
    }

    public void setC(short[] c) {
        this.c = c;
    }

    public short[] getG() {
        return g;
    }

    public void setG(short[] g) {
        this.g = g;
    }

    public short[] getT() {
        return t;
    }

    public void setT(short[] t) {
        this.t = t;
    }


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }
    
    
}
