package org.opencb.biodata.tools.variant.merge;

import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created on 19/06/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantAlternateRearrangerTest {

    @Test
    public void testRearrange() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"));
        assertEquals(".,A,.", r.rearrangeNumberA("A"));
        assertEquals(".,A,B", r.rearrangeNumberA("A,B"));
        assertEquals("C,A,B", r.rearrangeNumberA("A,B,C"));

        assertEquals("R,.,A,.", r.rearrangeNumberR("R,A"));
        assertEquals("R,.,A,B", r.rearrangeNumberR("R,A,B"));
        assertEquals("R,C,A,B", r.rearrangeNumberR("R,A,B,C"));

        r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("B", "A", "C"));

        assertEquals(".,A,.", r.rearrangeNumberA("A"));
        assertEquals("B,A,.", r.rearrangeNumberA("A,B"));
        assertEquals("B,A,C", r.rearrangeNumberA("A,B,C"));

        assertEquals("R,.,A,.", r.rearrangeNumberR("R,A"));
        assertEquals("R,B,A,.", r.rearrangeNumberR("R,A,B"));
        assertEquals("R,B,A,C", r.rearrangeNumberR("R,A,B,C"));
    }

    @Test
    public void testRearrangeMissingAlternates() {
        // Missing alternate
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "X", "C"), Arrays.asList("B", "A", "C"));

        assertEquals(".,A,.", r.rearrangeNumberA("A"));
        assertEquals(".,A,.", r.rearrangeNumberA("A,X"));
        assertEquals(".,A,C", r.rearrangeNumberA("A,X,C"));

        assertEquals("R,.,A,.", r.rearrangeNumberR("R,A"));
        assertEquals("R,.,A,.", r.rearrangeNumberR("R,A,X"));
        assertEquals("R,.,A,C", r.rearrangeNumberR("R,A,X,C"));
    }

    @Test
    public void testRearrangeGenotypePloidy1() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"));
        assertEquals(".,.,.,.", r.rearrangeNumberG(".", ".", 1));
        assertEquals("0,.,1,.", r.rearrangeNumberG("0,1", ".", 1));
        assertEquals("0,3,1,2", r.rearrangeNumberG("0,1,2,3", ".", 1));
    }

    @Test
    public void testRearrangeGenotypePloidy2() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"));
        assertEquals(".,.,.,.,.,.,.,.,.,.", r.rearrangeNumberG(".", ".", 2));
        assertEquals("00,.,.,01,.,11,.,.,.,.", r.rearrangeNumberG("00,01,11", ".", 2));
        assertEquals("00,.,.,01,.,11,02,.,12,22", r.rearrangeNumberG("00,01,11,02,12,22", ".", 2));
        assertEquals("00,03,33,01,13,11,02,23,12,22", r.rearrangeNumberG("00,01,11,02,12,22,03,13,23,33", ".", 2));
    }

    @Test
    public void testRearrangeGenotypePloidy2_missingAlleles() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A"), Arrays.asList("C", "A", "B"));
        assertEquals(".,.,.,.,.,.,.,.,.,.", r.rearrangeNumberG(".", ".", 2));
        assertEquals("00,.,.,01,.,11,.,.,.,.", r.rearrangeNumberG("00,01,11", ".", 2));
    }


    @Test
    public void testRearrangeGenotype() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "B"), Arrays.asList("B", "A", "C"));
        assertEquals("2/1", r.rearrangeGenotype(new Genotype("1/2")).toString());
        assertEquals("./.", r.rearrangeGenotype(new Genotype("./.")).toString());
        assertEquals("./2", r.rearrangeGenotype(new Genotype("./1")).toString());
    }

    @Test
    public void testRearrangeGenotype_missingAlternates() {
        VariantAlternateRearranger r = new VariantAlternateRearranger(Arrays.asList("A", "T"), Arrays.asList("C", "A"));
        assertEquals("2/.", r.rearrangeGenotype(new Genotype("1/2")).toString());
        assertEquals("./.", r.rearrangeGenotype(new Genotype("./.")).toString());
        assertEquals("./2", r.rearrangeGenotype(new Genotype("./1")).toString());
    }
}
