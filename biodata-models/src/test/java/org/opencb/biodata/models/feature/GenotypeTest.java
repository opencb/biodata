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

package org.opencb.biodata.models.feature;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.models.variant.Genotype;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GenotypeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEncodeUnphased() {
        Genotype gt00 = new Genotype("0/0");
        Genotype gt01 = new Genotype("0/1");
        Genotype gt10 = new Genotype("1/0");
        Genotype gt11 = new Genotype("1/1");
        Genotype gt12 = new Genotype("1/2");
        Genotype gt21 = new Genotype("2/1");

        assertEquals(-0, gt00.encode());
        assertEquals(-1, gt01.encode());
        assertEquals(-10, gt10.encode());
        assertEquals(-11, gt11.encode());
        assertEquals(-12, gt12.encode());
        assertEquals(-21, gt21.encode());
    }

    @Test
    public void testEncodePhased() {
        Genotype gt00 = new Genotype("0|0");
        Genotype gt01 = new Genotype("0|1");
        Genotype gt10 = new Genotype("1|0");
        Genotype gt11 = new Genotype("1|1");
        Genotype gt12 = new Genotype("1|2");
        Genotype gt21 = new Genotype("2|1");

        assertEquals(0, gt00.encode());
        assertEquals(1, gt01.encode());
        assertEquals(10, gt10.encode());
        assertEquals(11, gt11.encode());
        assertEquals(12, gt12.encode());
        assertEquals(21, gt21.encode());
    }

    @Test
    public void testGetNormalizedAllelesIdx() {
        Genotype gt00 = new Genotype("0|0");
        Genotype gt01 = new Genotype("0|1");
        Genotype gt10 = new Genotype("1|0");
        Genotype gt11 = new Genotype("1|1");
        Genotype gt120 = new Genotype("1|2|0");
        Genotype gt010 = new Genotype("0|1|0");

        assertArrayEquals(new int[]{0, 0}, gt00.getNormalizedAllelesIdx());
        assertArrayEquals(new int[]{0, 1}, gt01.getNormalizedAllelesIdx());
        assertArrayEquals(new int[]{0, 1}, gt10.getNormalizedAllelesIdx());
        assertArrayEquals(new int[]{1, 1}, gt11.getNormalizedAllelesIdx());
        assertArrayEquals(new int[]{0, 1, 2}, gt120.getNormalizedAllelesIdx());
        assertArrayEquals(new int[]{0, 0, 1}, gt010.getNormalizedAllelesIdx());
    }

    @Test
    public void testParse() {
        assertEquals("0|0", new Genotype("0|0").toString());
        assertEquals("0|1", new Genotype("0|1").toString());
        assertEquals("1|1", new Genotype("1|1").toString());
        assertEquals("1|2", new Genotype("1|2").toString());
        assertEquals("1|3", new Genotype("1|3").toString());
        assertEquals("0|2", new Genotype("A|C", "A", Arrays.asList("G", "C", "T")).toString());
        assertEquals("0|3", new Genotype("A|T", "A", Arrays.asList("G", "C", "T")).toString());
    }

    @Test
    public void testParseFail1() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unknown allele");
        new Genotype("AAA|T", "A", Arrays.asList("G", "C", "T")).getAllelesIdx();
    }

    @Test
    public void testParseFail2() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Empty allele");
        new Genotype("A|T|", "A", Arrays.asList("G", "C", "T")).getAllelesIdx();
    }

    @Test
    public void testParseFail3() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Empty allele");
        new Genotype("|C", "A", Arrays.asList("G", "C", "T")).getAllelesIdx();
    }

    @Test
    public void testHomHet() {
        assertFalse(Genotype.isHom("."));
        assertFalse(Genotype.isHet("."));
        assertFalse(Genotype.isHom("./."));
        assertFalse(Genotype.isHet("./."));
        assertFalse(Genotype.isHom("1/."));
        assertFalse(Genotype.isHet("1/."));

        testHom("0");
        testHom("1");
        testHom("1/1");
        testHom("1/1/1");
        testHom("1|1");
        testHom("10|10");
        testHom("2|2");

        testHet("1/2");
        testHet("0/1");
        testHet("1/2/3");
        testHet("2/10");

    }

    private void testHet(String s) {
        assertTrue(Genotype.isHet(s));
        assertFalse(Genotype.isHom(s));
    }

    private void testHom(String s) {
        assertTrue(Genotype.isHom(s));
        assertFalse(Genotype.isHet(s));
    }

    @Test
    public void testHasMainAlternate() {
        assertTrue(Genotype.hasMainAlternate("1"));
        assertTrue(Genotype.hasMainAlternate("1/1"));
        assertTrue(Genotype.hasMainAlternate("1/1/1"));
        assertTrue(Genotype.hasMainAlternate("1|1"));
        assertTrue(Genotype.hasMainAlternate("1/2"));
        assertTrue(Genotype.hasMainAlternate("0/1"));
        assertTrue(Genotype.hasMainAlternate("./1"));
        assertTrue(Genotype.hasMainAlternate("2/2/1"));

        assertFalse(Genotype.hasMainAlternate("."));
        assertFalse(Genotype.hasMainAlternate("0"));
        assertFalse(Genotype.hasMainAlternate("./."));
        assertFalse(Genotype.hasMainAlternate("10|10"));
        assertFalse(Genotype.hasMainAlternate("2|2"));
        assertFalse(Genotype.hasMainAlternate("2/10"));
    }
}
