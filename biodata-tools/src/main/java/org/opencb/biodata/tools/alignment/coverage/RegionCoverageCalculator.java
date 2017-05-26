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

package org.opencb.biodata.tools.alignment.coverage;

import org.opencb.biodata.models.alignment.RegionCoverage;

import java.util.ArrayList;
import java.util.List;


public abstract class RegionCoverageCalculator<T> {

    protected int minBaseQuality;

    public RegionCoverageCalculator() {
        this(0);
    }

    public RegionCoverageCalculator(int minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
    }

    public abstract void update(T alignment, RegionCoverage dest);

    public void update(RegionCoverage src, RegionCoverage dest) {
        if (!src.getChromosome().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        int start = Math.max(src.getStart(), dest.getStart());
        int end = Math.min(src.getEnd(), dest.getEnd());

        for (int i = start ; i <= end; i++) {
            dest.getValues()[i - dest.getStart()] += src.getValues()[i - src.getStart()];
        }
    }

    public int getMinBaseQuality() {
        return minBaseQuality;
    }

    public void setMinBaseQuality(int minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
    }
}
