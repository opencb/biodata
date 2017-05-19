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

package org.opencb.biodata.models.alignment.stats;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cgonzalez@cipf.es&gt;
 */
@Deprecated
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
