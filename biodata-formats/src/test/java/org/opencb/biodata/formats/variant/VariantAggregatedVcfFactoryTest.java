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

package org.opencb.biodata.formats.variant;

import org.junit.Test;
import org.opencb.biodata.formats.variant.vcf4.VariantAggregatedVcfFactory;
import org.opencb.biodata.formats.variant.vcf4.VariantVcfFactory;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;
import org.opencb.commons.test.GenericTest;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


/** 
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactoryTest extends GenericTest {

    private VariantFileMetadata fileMetadata = new VariantFileMetadata("filename.vcf", "fileId");
    private VariantDatasetMetadata metadata = fileMetadata.toVariantDatasetMetadata("studyId");

    @Test
    public void testIndel() {
        String line = "1\t1000\trs123\tTCACCC\tTGACGG\t.\t.\t.";
        VariantVcfFactory factory = new VariantAggregatedVcfFactory();

        List<Variant> variants = factory.create(metadata, line);

        assertEquals(1, variants.size());
        Variant variant = variants.get(0);
        variant.setStudies(Collections.<StudyEntry>emptyList());

        Variant expected = new Variant("1", 1000, 1005, "TCACCC", "TGACGG");
        expected.setIds(Collections.singletonList("rs123"));

        assertEquals(expected, variant);

    }

}