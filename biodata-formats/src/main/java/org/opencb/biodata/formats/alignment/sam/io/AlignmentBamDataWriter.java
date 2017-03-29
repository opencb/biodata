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

package org.opencb.biodata.formats.alignment.sam.io;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriterFactory;
import org.opencb.biodata.formats.alignment.AlignmentConverter;
import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;
import org.opencb.biodata.models.alignment.AlignmentHeader;

import java.nio.file.Path;

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
        return true;
    }

    @Override
    public boolean pre() {
        if (this.input.toFile().exists()) {
            SAMFileHeader samFileHeader = AlignmentConverter.buildSAMFileHeader(reader.getHeader());
            writer = new SAMFileWriterFactory().makeBAMWriter(samFileHeader, true, this.input.toFile());
            super.pre();
            return true;
        } else {
            return false;
        }
    }

}
