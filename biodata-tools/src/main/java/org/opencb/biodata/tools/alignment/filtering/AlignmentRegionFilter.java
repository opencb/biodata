/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.alignment.filtering;


import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.feature.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joaquín Tárraga Giménez &lt;jtarraga@cipf.es&gt;
 */
public class AlignmentRegionFilter extends AlignmentFilter {

    private List<Region> regionList;

    public AlignmentRegionFilter(String chromosome, int start, int end) {
        super();
        regionList = new ArrayList<>();
        regionList.add(new Region(chromosome, start, end));
    }

    public AlignmentRegionFilter(String chromosome, int start, int end, int priority) {
        super(priority);
        regionList = new ArrayList<>();
        regionList.add(new Region(chromosome, start, end));

    }

    public AlignmentRegionFilter(String regions) {
        super();
        regionList = new ArrayList<>();

        String[] splits = regions.split(",");
        for (String split : splits) {
            regionList.add(new Region(split));
        }
    }

    public AlignmentRegionFilter(String regions, int priority) {
        super(priority);
        regionList = new ArrayList<>();

        String[] splits = regions.split(",");
        for (String split : splits) {
            regionList.add(new Region(split));
        }
    }

    @Override
    public boolean apply(Alignment align) {
        for (Region r : regionList) {
            if (r.contains(align.getChromosome(), align.getStart())) {
                return true;
            }
        }
        return false;
    }
}
