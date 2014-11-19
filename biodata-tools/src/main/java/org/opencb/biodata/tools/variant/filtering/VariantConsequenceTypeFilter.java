package org.opencb.biodata.tools.variant.filtering;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.annotation.VariantEffect;
import org.opencb.biodata.models.variant.annotation.ConsequenceTypeMappings;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class VariantConsequenceTypeFilter extends VariantFilter {

    private String consequenceType;
    private int consequenceTypeAccession;

    public VariantConsequenceTypeFilter(String consequenceType) {
        this.consequenceType = consequenceType;
        this.consequenceTypeAccession = ConsequenceTypeMappings.termToAccession.get(consequenceType);
    }

    public VariantConsequenceTypeFilter(String consequenceType, int priority) {
        super(priority);
        this.consequenceType = consequenceType;
        this.consequenceTypeAccession = ConsequenceTypeMappings.termToAccession.get(consequenceType);
    }

    @Override
    public boolean apply(Variant variant) {
        Map<Variant, Set<VariantEffect>> batchEffect = EffectCalculator.getEffects(Arrays.asList(variant));

        for (Set<VariantEffect> list : batchEffect.values()) {
            for (VariantEffect ct : list) {
                for (int so : ct.getConsequenceTypes()) {
                    if (so == this.consequenceTypeAccession) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
