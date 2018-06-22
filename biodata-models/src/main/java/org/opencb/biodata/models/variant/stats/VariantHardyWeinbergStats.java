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

package org.opencb.biodata.models.variant.stats;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
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
        chi2 = Float.MAX_VALUE;
        pValue = Float.MAX_VALUE;
    }

    public VariantHardyWeinbergStats(int n_AA, int n_Aa, int n_aa) {
        this();
        this.n_AA = n_AA;
        this.n_Aa = n_Aa;
        this.n_aa = n_aa;
    }

    public float getChi2() {
        if (chi2 == Float.MAX_VALUE) {
            calculate();
        }
        return chi2;
    }

    public float getpValue() {
        if (pValue == Float.MAX_VALUE) {
            calculate();
        }
        return pValue;
    }

    public float getP() {
        return p;
    }

    public float getQ() {
        return q;
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

    public void incN_AA() {
        this.n_AA++;
    }

    public void incN_Aa() {
        this.n_Aa++;
    }

    public void incN_aa() {
        this.n_aa++;
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

    public void calculate() {
        this.n = this.n_AA + this.n_Aa + this.n_aa;

        int n = this.n;
        int n_AA = this.n_AA;
        int n_Aa = this.n_Aa;
        int n_aa = this.n_aa;

        if (n > 0) {
            float p = (float) ((2.0 * n_AA + n_Aa) / (2 * n));
            float q = 1 - p;

            this.setP(p);
            this.setQ(q);

            this.setE_AA(p * p * n);
            this.setE_Aa(2 * p * q * n);
            this.setE_aa(q * q * n);

            if (this.e_AA == n_AA) {
                n_AA = 1;
                this.setE_AA(n_AA);
            }

            if (this.e_Aa == n_Aa) {
                n_Aa = 1;
                this.setE_Aa(n_Aa);
            }

            if (this.e_aa == n_aa) {
                n_aa = 1;
                this.setE_aa(n_aa);
            }

            chi2 = (n_AA - this.e_AA) * (n_AA - this.e_AA) / this.e_AA
                    + (n_Aa - this.e_Aa) * (n_Aa - this.e_Aa) / this.e_Aa
                    + (n_aa - this.e_aa) * (n_aa - this.e_aa) / this.e_aa;

            // TODO Calculate p-value
            // GSL call: hw->p_value = 1-gsl_cdf_chisq_P(hw->chi2,1);
//            pValue = chiSquareTest.chiSquare(chi, 1);
            pValue = -1;
        }
    }

}
