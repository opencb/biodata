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

package org.opencb.biodata.models.core;

import java.util.Map;

public class CancerHotspotVariant {

    private String aminoacidAlternate;
    private int count;
    private Map<String, Integer> sampleCount;

    public CancerHotspotVariant() {
    }

    public CancerHotspotVariant(String aminoacidAlternate, int count, Map<String, Integer> sampleCount) {
        this.aminoacidAlternate = aminoacidAlternate;
        this.count = count;
        this.sampleCount = sampleCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CancerHotspotVariant{");
        sb.append("aminoacidAlternate='").append(aminoacidAlternate).append('\'');
        sb.append(", count=").append(count);
        sb.append(", sampleCount=").append(sampleCount);
        sb.append('}');
        return sb.toString();
    }

    public String getAminoacidAlternate() {
        return aminoacidAlternate;
    }

    public CancerHotspotVariant setAminoacidAlternate(String aminoacidAlternate) {
        this.aminoacidAlternate = aminoacidAlternate;
        return this;
    }

    public int getCount() {
        return count;
    }

    public CancerHotspotVariant setCount(int count) {
        this.count = count;
        return this;
    }

    public Map<String, Integer> getSampleCount() {
        return sampleCount;
    }

    public CancerHotspotVariant setSampleCount(Map<String, Integer> sampleCount) {
        this.sampleCount = sampleCount;
        return this;
    }
}
