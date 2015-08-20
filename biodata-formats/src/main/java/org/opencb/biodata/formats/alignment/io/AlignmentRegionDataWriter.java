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

package org.opencb.biodata.formats.alignment.io;



import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.biodata.models.alignment.AlignmentRegion;
import org.opencb.commons.io.DataWriter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 2/7/14
 * Time: 5:43 PM
 *
 */
public class AlignmentRegionDataWriter implements DataWriter<AlignmentRegion> {
    private AlignmentDataWriter alignmentDataWriter;


    public AlignmentRegionDataWriter(AlignmentDataWriter alignmentDataWriter) {
        this.alignmentDataWriter = alignmentDataWriter;
    }

    @Override
    public boolean open() {
        return alignmentDataWriter.open();
    }

    @Override
    public boolean close() {
        return alignmentDataWriter.close();
    }

    @Override
    public boolean pre() {
        return alignmentDataWriter.pre();
    }

    @Override
    public boolean post() {
        return alignmentDataWriter.post();
    }

    @Override
    public boolean write(AlignmentRegion elem) {
        // get reference sequence
        for (Alignment alignment : elem.getAlignments()) {
            if(!alignmentDataWriter.write(alignment)){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean write(List<AlignmentRegion> batch) {
        for(AlignmentRegion r : batch){
            if(!write(r))
                return false;
        }
        return true;
    }
}
