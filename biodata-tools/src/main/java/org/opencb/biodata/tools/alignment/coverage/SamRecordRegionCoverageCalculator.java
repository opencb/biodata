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

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;
import org.opencb.biodata.models.alignment.RegionCoverage;

/**
 * Created by jtarraga on 28/10/16.
 */
public class SamRecordRegionCoverageCalculator extends RegionCoverageCalculator<SAMRecord> {

    public SamRecordRegionCoverageCalculator() {
        super(0);
    }

    public SamRecordRegionCoverageCalculator(int minBaseQuality) {
        super(minBaseQuality);
    }

    @Override
    public void update(SAMRecord sr, RegionCoverage dest) {
        if (sr.getReadUnmappedFlag() || !sr.getReferenceName().equals(dest.getChromosome())) {
            // nothing to do
            return;
        }

        // counters for bases and qualities
        int refPos = sr.getAlignmentStart();
        int qualityPos = 0;

        byte[] qualities = sr.getBaseQualities();
        short[] values = dest.getValues();

        for (CigarElement ce: sr.getCigar().getCigarElements()) {
            switch (ce.getOperator().toString()) {
                case "M":
                case "=":
                case "X":
                    for (int i = 0; i < ce.getLength(); i++) {
                        if (refPos >= dest.getStart() && refPos <= dest.getEnd()) {
                            if (qualities[qualityPos] >= minBaseQuality) {
                                values[refPos - dest.getStart()]++;
                            }
                        }
                        qualityPos++;
                        refPos++;
                    }
                    break;
                case "N":
                case "D":
                    refPos += ce.getLength();
                    break;
                case "S":
                case "I":
                    qualityPos += ce.getLength();
                    break;
                default:
                    break;
            }
        }
    }
}
