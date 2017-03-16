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

package org.opencb.biodata.tools.alignment.stats;

import org.ga4gh.models.CigarUnit;
import org.ga4gh.models.ReadAlignment;

import java.util.List;

/**
 * Created by jtarraga on 25/05/15.
 */
public class AvroAlignmentGlobalStatsCalculator extends AlignmentGlobalStatsCalculator<ReadAlignment> {

    public AvroAlignmentGlobalStatsCalculator() {
        super();
    }

    @Override
    public boolean isProperlyPaired(ReadAlignment alignment) {
        return !alignment.getImproperPlacement();
    }

    @Override
    public int getInsertSize(ReadAlignment alignment) {
        return alignment.getFragmentLength();
    }

    @Override
    public boolean isFirstOfPair(ReadAlignment alignment) {
        return alignment.getReadNumber() == 0;
    }

    @Override
    public boolean isSecondOfPair(ReadAlignment alignment) {
        return alignment.getReadNumber() == alignment.getNumberReads() - 1;
    }

    @Override
    public int getMappingQuality(ReadAlignment alignment) {
        return alignment.getAlignment().getMappingQuality();
    }

    @Override
    public String getAlignedSequence(ReadAlignment alignment) {
        return alignment.getAlignedSequence();
    }

    @Override
    public List<Integer> getAlignedQuality(ReadAlignment alignment) {
        return alignment.getAlignedQuality();
    }

    @Override
    public CIGAR getActiveCigars(ReadAlignment alignment) {
        CIGAR ret = new CIGAR();

        List<CigarUnit> cigar = alignment.getAlignment().getCigar();
        if (cigar != null) {
            for (CigarUnit element: cigar) {
                switch(element.getOperation()) {
                    case CLIP_HARD:
                        ret.hard = true;
                        break;
                    case CLIP_SOFT:
                        ret.soft = true;
                        break;
                    case DELETE:
                        ret.del = true;
                        break;
                    case INSERT:
                        ret.in = true;
                        break;
                    case PAD:
                        ret.pad = true;
                        break;
                    case SKIP:
                        ret.skip = true;
                        break;
                    default:
                        break;
                }
            }
        }
        return ret;
    }

    @Override
    public int getNumberOfMismatches(ReadAlignment alignment) {
        if (alignment.getInfo() != null) {
            List<String> values = alignment.getInfo().get("NM");
            if (values != null) {
                return Integer.parseInt(values.get(1).toString());
            }
        }
        return 0;
    }

    @Override
    public boolean isMapped(ReadAlignment alignment) {
        return alignment.getAlignment() != null;
    }

}
