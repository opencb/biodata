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

import org.junit.Ignore;
import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.test.GenericTest;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;


/** 
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantAggregatedVcfFactoryTest extends GenericTest {

    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");

    @Test
    public void testIndel() {
        String line = "1\t1000\trs123\tTCACCC\tTGACGG\t.\t.\t.";
        VariantVcfFactory factory = new VariantAggregatedVcfFactory();

        List<Variant> variants = factory.create(source, line);

        assertEquals(1, variants.size());
        assertEquals(new Variant("1", 1001, 1005, "CACCC", "GACGG"), variants.get(0));

    }

}