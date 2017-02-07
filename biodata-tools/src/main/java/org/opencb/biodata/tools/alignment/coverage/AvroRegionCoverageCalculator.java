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

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.LinearAlignment;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.models.alignment.RegionCoverage;

import java.util.List;

/**
 * Created by jtarraga on 26/05/15.
 */
public class AvroRegionCoverageCalculator extends RegionCoverageCalculator<ReadAlignment> {

    public AvroRegionCoverageCalculator() {
        super(0);
    }

    public AvroRegionCoverageCalculator(int minBaseQuality) {
        super(minBaseQuality);
    }

    @Override
    public void update(ReadAlignment ra, RegionCoverage dest) {
        LinearAlignment la = ra.getAlignment();
        if ( la == null || !la.getPosition().getReferenceName().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        // counters for bases and qualities
        int refPos = Math.toIntExact(la.getPosition().getPosition());
        int qualityPos = 0;

        List<Integer> qualities = ra.getAlignedQuality();
        short[] values = dest.getValues();

        for (CigarUnit cu: la.getCigar()) {
            switch (cu.getOperation()) {
                case ALIGNMENT_MATCH:
                case SEQUENCE_MATCH:
                case SEQUENCE_MISMATCH:
                    for (int i = 0; i < cu.getOperationLength(); i++) {
                        if (refPos >= dest.getStart() && refPos <= dest.getEnd()) {
                            if (qualities.get(qualityPos) >= minBaseQuality) {
                                values[refPos - dest.getStart()]++;
                            }
                        }
                        qualityPos++;
                        refPos++;
                    }
                    break;
                case SKIP:
                case DELETE:
                    refPos += cu.getOperationLength();
                    break;
                case CLIP_SOFT:
                case INSERT:
                    qualityPos += cu.getOperationLength();
                    break;
                default:
                    break;
            }
        }
    }
}
