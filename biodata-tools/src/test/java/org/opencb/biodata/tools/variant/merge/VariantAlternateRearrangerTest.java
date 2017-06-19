package org.opencb.biodata.tools.variant.merge;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created on 19/06/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantAlternateRearrangerTest {

    @Test
    public void testRearrange() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"), 2);
        assertEquals(".,A,.", r.rearrangeNumberA("A"));
        assertEquals(".,A,B", r.rearrangeNumberA("A,B"));
        assertEquals("C,A,B", r.rearrangeNumberA("A,B,C"));

        assertEquals("R,.,A,.", r.rearrangeNumberR("R,A"));
        assertEquals("R,.,A,B", r.rearrangeNumberR("R,A,B"));
        assertEquals("R,C,A,B", r.rearrangeNumberR("R,A,B,C"));

        r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("B", "A", "C"), 2);

        assertEquals(".,A,.", r.rearrangeNumberA("A"));
        assertEquals("B,A,.", r.rearrangeNumberA("A,B"));
        assertEquals("B,A,C", r.rearrangeNumberA("A,B,C"));

        assertEquals("R,.,A,.", r.rearrangeNumberR("R,A"));
        assertEquals("R,B,A,.", r.rearrangeNumberR("R,A,B"));
        assertEquals("R,B,A,C", r.rearrangeNumberR("R,A,B,C"));
    }

    @Test
    public void testRearrangeGenotypePloidy1() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"), 1);
        assertEquals(".,.,.,.", r.rearrangeNumberG("."));
        assertEquals("0,.,1,.", r.rearrangeNumberG("0,1"));
        assertEquals("0,3,1,2", r.rearrangeNumberG("0,1,2,3"));
    }

    @Test
    public void testRearrangeGenotypePloidy2() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"), 2);
        assertEquals(".,.,.,.,.,.,.,.,.,.", r.rearrangeNumberG("."));
        assertEquals("00,.,.,01,.,11,.,.,.,.", r.rearrangeNumberG("00,01,11"));
        assertEquals("00,.,.,01,.,11,02,.,12,22", r.rearrangeNumberG("00,01,11,02,12,22"));
        assertEquals("00,03,33,01,13,11,02,23,12,22", r.rearrangeNumberG("00,01,11,02,12,22,03,13,23,33"));
    }

    @Test
    public void testRearrangeGenotypePloidy2_missingAlleles() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A"), Arrays.asList("C", "A", "B"), 2);
        assertEquals(".,.,.,.,.,.,.,.,.,.", r.rearrangeNumberG("."));
        assertEquals("00,.,.,01,.,11,.,.,.,.", r.rearrangeNumberG("00,01,11"));
    }
}