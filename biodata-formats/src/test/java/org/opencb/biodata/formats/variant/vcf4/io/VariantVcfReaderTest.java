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

package org.opencb.biodata.formats.variant.vcf4.io;

import org.junit.Test;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.formats.variant.vcf4.VariantAggregatedVcfFactory;
import org.opencb.biodata.models.variant.VariantFileMetadata;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VariantVcfReaderTest {

    @Test
    public void readFile() {
        String inputFile = getClass().getResource("/variant-test-file.vcf.gz").getFile();
        VariantFileMetadata fileMetadata = new VariantFileMetadata(inputFile, "test");
    
        VariantReader reader = new VariantVcfReader(fileMetadata.toVariantDatasetMetadata("studyId"), inputFile);

        List<Variant> variants;

        assertTrue(reader.open());
        assertTrue(reader.pre());

        int i = 0;
        do {
            variants = reader.read();
            if(variants != null){
                i+=variants.size();
            }
        } while (variants != null);

        assertEquals(i, 999);

        assertTrue(reader.post());
        assertTrue(reader.close());
    }

    @Test
    public void readAggregatedFile() {
        String inputFile = getClass().getResource("/evs.vcf.gz").getFile();
        VariantFileMetadata fileMetadata = new VariantFileMetadata(inputFile, "evs");
        
        VariantReader reader = new VariantVcfReader(fileMetadata.toVariantDatasetMetadata("studyId"), inputFile, new VariantAggregatedVcfFactory());

        List<Variant> variants;

        assertTrue(reader.open());
        assertTrue(reader.pre());

        int i = 0;
        do {
            variants = reader.read();
            if(variants != null){
                i+=variants.size();
            }
        } while (variants != null);

        assertEquals(i, 1000);

        assertTrue(reader.post());
        assertTrue(reader.close());
    }
}
