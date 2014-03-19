package org.opencb.biodata.models.variant.stats;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 9/2/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariantHardyWeinbergStats {

    private float chi2;
    private float pValue;
    private int n;
    private int n_AA;
    private int n_Aa;
    private int n_aa;
    private float e_AA;
    private float e_Aa;
    private float e_aa;
    private float p;
    private float q;

    public VariantHardyWeinbergStats() {
    }

    public void incNAA() {
        this.n_AA++;
    }

    public void incNAa() {
        this.n_Aa++;
    }

    public void incNaa() {
        this.n_aa++;
    }

    public float getChi2() {
        return chi2;
    }

    public float getpValue() {
        return pValue;
    }

    public int getN() {
        return n;
    }

    public int getN_AA() {
        return n_AA;
    }

    public int getN_Aa() {
        return n_Aa;
    }

    public int getN_aa() {
        return n_aa;
    }

    public float getE_AA() {
        return e_AA;
    }

    public float getE_Aa() {
        return e_Aa;
    }

    public float getE_aa() {
        return e_aa;
    }

    public float getP() {
        return p;
    }

    public float getQ() {
        return q;
    }

    public void setChi2(float chi2) {
        this.chi2 = chi2;
    }

    public void setpValue(float pValue) {
        this.pValue = pValue;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setN_AA(int n_AA) {
        this.n_AA = n_AA;
    }

    public void setN_Aa(int n_Aa) {
        this.n_Aa = n_Aa;
    }

    public void setN_aa(int n_aa) {
        this.n_aa = n_aa;
    }

    public void setE_AA(float e_AA) {
        this.e_AA = e_AA;
    }

    public void setE_Aa(float e_Aa) {
        this.e_Aa = e_Aa;
    }

    public void setE_aa(float e_aa) {
        this.e_aa = e_aa;
    }

    public void setP(float p) {
        this.p = p;
    }

    public void setQ(float q) {
        this.q = q;
    }
}
