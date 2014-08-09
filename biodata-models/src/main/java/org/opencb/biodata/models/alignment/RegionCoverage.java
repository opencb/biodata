package org.opencb.biodata.models.alignment;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cgonzalez@cipf.es>
 */
public class RegionCoverage {
    
    private short[] all;
    private short[] a;
    private short[] c;
    private short[] g;
    private short[] t;
    
    public RegionCoverage() { }
    
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
    
    
}
