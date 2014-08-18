package org.opencb.biodata.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.tools.variant.EffectCalculator;
import org.opencb.commons.test.GenericTest;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
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
        Map<Variant, Set<VariantEffect>> effects = EffectCalculator.getEffects(variants);
        for (Set<VariantEffect> ve : effects.values()) {
            System.out.println("ve = " + ve);
        }
    }

    @Test
    public void testGetEffectsWithPolyPhenAndSift() throws Exception {

    }

}
