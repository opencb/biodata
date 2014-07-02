package org.opencb.biodata.tools;

import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.VariantAnnotation;
import org.opencb.biodata.tools.variant.EffectCalculator;
import org.opencb.commons.test.GenericTest;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class EffectCalculatorTest extends GenericTest {

    private static List<Variant> variants;

    @BeforeClass
    public static void init() {
        variants = new ArrayList<>();
        variants.add(new Variant("15", 89758364, 89758364, "A", "A"));
    }

    @Test
    public void testGetEffects() throws Exception {
        List<VariantAnnotation> effects = EffectCalculator.getEffects(variants);
        for (VariantAnnotation ve : effects) {
            System.out.println("ve = " + ve);
        }
    }

    @Test
    public void testGetEffectsWithPolyPhenAndSift() throws Exception {

    }

    @Test
    public void testGetEffectPerVariant() throws Exception {
        List<List<VariantAnnotation>> effects = EffectCalculator.getEffectPerVariant(variants);
        for (List<VariantAnnotation> ve : effects) {
            System.out.println(ve);
        }

    }
}
