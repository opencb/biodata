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

package org.opencb.biodata.formats.alignment.sam.io;

import net.sf.samtools.BAMFileWriter;
import org.opencb.biodata.models.alignment.AlignmentHeader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentBamDataWriter extends AlignmentSamDataWriter  {


    public AlignmentBamDataWriter(Path input, AlignmentHeader header) {
        super(input, header);
    }

    public AlignmentBamDataWriter(Path input, AlignmentDataReader reader) {
        super(input, reader);
    }

    @Override
    public boolean open() {
        if(this.input.toFile().exists()) {
            writer = new BAMFileWriter(this.input.toFile());
            return true;
        } else {
            return false;
        }
    }

}
