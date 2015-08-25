
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

package org.opencb.biodata.formats.annotation.io;

import org.junit.Test;
import org.opencb.biodata.models.variant.annotation.VariantAnnotation;

import java.util.List;

import static org.junit.Assert.*;

public class VepFormatReaderTest {

    @Test
    public void testRead() throws Exception {
//        VepFormatReader vepFormatReader = new VepFormatReader("/home/fjlopez/EBI/eva/data/clinvar.vep");
        VepFormatReader vepFormatReader = new VepFormatReader("/tmp/test.vep");
//        VepFormatReader vepFormatReader = new VepFormatReader("/tmp/vep22.vep");
//        VepFormatReader vepFormatReader = new VepFormatReader("/tmp/test.vep");
//        VepFormatReader vepFormatReader = new VepFormatReader("/tmp/vep22.head.tsv");
//        VepFormatReader vepFormatReader = new VepFormatReader("/tmp/test1.vep");
        vepFormatReader.open();
        vepFormatReader.pre();
        List<VariantAnnotation> variantAnnotationList = vepFormatReader.read(10000);
        vepFormatReader.post();
        vepFormatReader.close();
    }
}