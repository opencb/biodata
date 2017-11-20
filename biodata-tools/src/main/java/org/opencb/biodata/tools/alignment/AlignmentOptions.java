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

package org.opencb.biodata.tools.alignment;

/**
 * Created by pfurio on 26/10/16.
 */
public class AlignmentOptions {

    private boolean contained;
    private boolean binQualities;
    private boolean calculateMD;
    private int minBaseQuality;
    private int windowSize;
    private int limit;

    public static final int DEFAULT_LIMIT = 50000;

    public AlignmentOptions() {
        this(false, false, false, 0, 1, DEFAULT_LIMIT);
    }

    public AlignmentOptions(boolean contained, boolean binQualities, boolean calculateMD, int minBaseQuality, int windowSize, int limit) {
        this.contained = contained;
        this.binQualities = binQualities;
        this.calculateMD = calculateMD;
        this.minBaseQuality = minBaseQuality;
        this.windowSize = windowSize;
        this.limit = limit;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlignmentOptions{");
        sb.append("contained=").append(contained);
        sb.append(", binQualities=").append(binQualities);
        sb.append(", calculateMD=").append(calculateMD);
        sb.append(", minBaseQuality=").append(minBaseQuality);
        sb.append(", windowSize=").append(windowSize);
        sb.append(", limit=").append(limit);
        sb.append('}');
        return sb.toString();
    }

    public AlignmentOptions setContained(boolean contained) {
        this.contained = contained;
        return this;
    }

    public AlignmentOptions setBinQualities(boolean binQualities) {
        this.binQualities = binQualities;
        return this;
    }

    public AlignmentOptions setCalculateMD(boolean calculateMD) {
        this.calculateMD = calculateMD;
        return this;
    }

    public AlignmentOptions setMinBaseQuality(int minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
        return this;
    }

    public AlignmentOptions setWindowSize(int windowSize) {
        this.windowSize = windowSize;
        return this;
    }

    public AlignmentOptions setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public boolean isContained() {
        return contained;
    }

    public boolean isBinQualities() {
        return binQualities;
    }

    public boolean isCalculateMD() {
        return calculateMD;
    }

    public int getMinBaseQuality() {
        return minBaseQuality;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getLimit() {
        return limit;
    }
}
