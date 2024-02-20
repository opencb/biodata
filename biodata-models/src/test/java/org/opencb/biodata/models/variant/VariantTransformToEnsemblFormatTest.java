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

package org.opencb.biodata.models.variant;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class VariantTransformToEnsemblFormatTest {

    @Test
    public void testTransformToEnsemblFormatDel_TA_T() throws Exception {

        Variant v1 = new Variant("12",10144,10145,"TA","T");
        Variant v1_esembl_format = new Variant("12",10145,10145,"A","-");
        v1.transformToEnsemblFormat();
//        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatIns_A_AC() throws Exception {

        Variant v1 = new Variant("12",724498,724499,"A","AC");
        Variant v1_esembl_format = new Variant("12",724499,724498,"-","C");
        v1.transformToEnsemblFormat();
//        assertEquals(v1, v1_esembl_format);

    }

    @Test
    public void testTransformToEnsemblFormatComplex_CAAATCTGGAT_CGAATCTGGAC() throws Exception {

        Variant v1 = new Variant("12",717318,717328,"CAAATCTGGAT","CGAATCTGGAC");
        Variant v1_esembl_format = new Variant ("12",717319,717328,"AAATCTGGAT","GAATCTGGAC");
        v1.transformToEnsemblFormat();
        assertEquals(v1, v1_esembl_format);

    }
}
