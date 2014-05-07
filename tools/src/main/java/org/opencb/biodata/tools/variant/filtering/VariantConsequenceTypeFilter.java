package org.opencb.biodata.tools.variant.filtering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.ConsequenceType;
import org.opencb.biodata.models.variant.effect.ConsequenceTypeMappings;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
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
        List<Variant> batch = new ArrayList<>();
        batch.add(variant);

        List<VariantEffect> batchEffect = EffectCalculator.getEffects(batch);

        Iterator<VariantEffect> it = batchEffect.iterator();

        VariantEffect effect;
        while (it.hasNext()) {
            effect = it.next();

            for (List<ConsequenceType> list : effect.getConsequenceTypes().values()) {
                for (ConsequenceType ct : list) {
                    for (int so : ct.getConsequenceTypes()) {
                        if (so == this.consequenceTypeAccession) {
                            return true;
                        }
                    }
                }
            }
//            if (effect.getConsequenceTypeObo().equalsIgnoreCase(this.consequenceType)) {
//                return true;
//            }

        }

        return false;
    }
}
