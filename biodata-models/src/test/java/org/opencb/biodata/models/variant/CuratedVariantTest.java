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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.variant.exceptions.NotAVariantException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Pablo Riesgo Ferreiro &lt;pablo.ferreiro@genomicsengland.co.uk&gt;
 */
public class CuratedVariantTest {

    private VariantSource source = new VariantSource("filename.vcf", "fileId", "studyId", "studyName");
    private VariantFactory factory = new VariantVcfFactory();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        source.setSamples(sampleNames);
    }

    @Test
    public void testCreateEmptyCuratedVariant() {
        CuratedVariant curatedVariant = new CuratedVariant();
        assertEquals("VUS", curatedVariant.getCurationClassification());
        assertEquals(new Integer(0), curatedVariant.getCurationScore());
        assertNotNull(curatedVariant.getCurationHistory());
        assertEquals(0, curatedVariant.getCurationHistory().size());
        assertNotNull(curatedVariant.getEvidences());
        assertEquals(0, curatedVariant.getEvidences().size());
        assertNotNull(curatedVariant.getComments());
        assertEquals(0, curatedVariant.getComments().size());
        assertNotNull(curatedVariant.getVariant());
    }

    @Test
    public void testCreateCuratedVariantFromVariantAndDefaultValues() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\t.\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> result = factory.create(source, line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));

        Variant variant = result.get(0);
        CuratedVariant curatedVariant = new CuratedVariant(variant);
        assertEquals("VUS", curatedVariant.getCurationClassification());
        assertEquals(new Integer(0), curatedVariant.getCurationScore());
        assertNotNull(curatedVariant.getCurationHistory());
        assertEquals(0, curatedVariant.getCurationHistory().size());
        assertNotNull(curatedVariant.getEvidences());
        assertEquals(0, curatedVariant.getEvidences().size());
        assertNotNull(curatedVariant.getComments());
        assertEquals(0, curatedVariant.getComments().size());
        assertNotNull(curatedVariant.getVariant());
        assertEquals(variant, curatedVariant.getVariant());
    }

    @Test
    public void testCreateCuratedVariantFromVariant() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\t.\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> result = factory.create(source, line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));

        Variant variant = result.get(0);
        CuratedVariant curatedVariant = new CuratedVariant(variant,
                "DISEASE_ASSOCIATED_VARIANT", 5,
                null, null, null);
        assertEquals("DISEASE_ASSOCIATED_VARIANT", curatedVariant.getCurationClassification());
        assertEquals(new Integer(5), curatedVariant.getCurationScore());
        assertNotNull(curatedVariant.getCurationHistory());
        assertEquals(0, curatedVariant.getCurationHistory().size());
        assertNotNull(curatedVariant.getEvidences());
        assertEquals(0, curatedVariant.getEvidences().size());
        assertNotNull(curatedVariant.getComments());
        assertEquals(0, curatedVariant.getComments().size());
        assertNotNull(curatedVariant.getVariant());
        assertEquals(variant, curatedVariant.getVariant());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCuratedVariantIllegalClassification() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\t.\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> result = factory.create(source, line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));

        Variant variant = result.get(0);
        CuratedVariant curatedVariant = new CuratedVariant(variant,
                "MY_CUSTOM_VARIANT", 5,
                null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCuratedVariantIllegalScore() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\t.\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> result = factory.create(source, line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));

        Variant variant = result.get(0);
        CuratedVariant curatedVariant = new CuratedVariant(variant,
                "VUS", 6,
                null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCuratedVariantIllegalScore2() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\t.\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> result = factory.create(source, line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));

        Variant variant = result.get(0);
        CuratedVariant curatedVariant = new CuratedVariant(variant,
                "VUS", -1,
                null, null, null);
    }
}
