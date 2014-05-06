package org.opencb.biodata.tools.variant.annotation;

import com.google.common.base.Joiner;
import java.util.Arrays;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.effect.ConsequenceType;
import org.opencb.biodata.models.variant.effect.ConsequenceTypeMappings;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantConsequenceTypeAnnotator implements VariantAnnotator {

    private VariantSource study;

    public VariantConsequenceTypeAnnotator(VariantSource study) {
        this.study = study;
    }

    @Override
    public void annot(Variant elem) {
        annot(Arrays.asList(elem));
    }
    
    @Override
    public void annot(List<Variant> batch) {
        List<VariantEffect> batchEffect = EffectCalculator.getEffects(batch);

        for (Variant variant : batch) {
            ArchivedVariantFile file = variant.getFile(study.getAlias());
            if (file == null) {
                // The variant is not present in this file
                continue;
            }
            
            annotVariantEffect(variant, file, batchEffect);
        }
    }

    private void annotVariantEffect(Variant variant, ArchivedVariantFile file, List<VariantEffect> batchEffect) {
        Set<String> cts = new HashSet<>();
        for (VariantEffect effect : batchEffect) {
            if (variant.getChromosome().equals(effect.getChromosome())
                    && variant.getStart() == effect.getStart()
                    && variant.getReference().equals(effect.getReferenceAllele())) {
                
                for (ConsequenceType ct : effect.getConsequenceTypes().values()) {
                    for (int so : ct.getConsequenceTypes()) {
                        cts.add(ConsequenceTypeMappings.accessionToTerm.get(so));
                    }
                }
            }
        }

        String ct_all = Joiner.on(",").join(cts);
        if (cts.size() > 0) {
            file.addAttribute("ConsType", ct_all);
        }
    }

}
