package org.opencb.biodata.tools.variant.annotation;

import com.google.common.base.Joiner;
import java.util.Arrays;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.models.variant.effect.ConsequenceTypeMappings;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantConsequenceTypeAnnotator implements VariantAnnotator {

    private String ctTag;

    public VariantConsequenceTypeAnnotator() {
        this("ConsType");
    }

    public VariantConsequenceTypeAnnotator(String ctTag) {
        this.ctTag = ctTag;
    }

    @Override
    public void annot(Variant elem) {
        annot(Arrays.asList(elem));
    }
    
    @Override
    public void annot(List<Variant> batch) {
        EffectCalculator.setEffects(batch);

        for (Variant variant : batch) {
            for (Map.Entry<String, VariantSourceEntry> file : variant.getSourceEntries().entrySet()) {
                annotVariantEffect(variant, file.getValue());
            }
        }
    }

    private void annotVariantEffect(Variant variant, VariantSourceEntry file) {
        Set<String> cts = new HashSet<>();

        for (List<VariantEffect> list : variant.getAnnotation().getEffects().values()) {
            for (VariantEffect ct : list) {
                for (int so : ct.getConsequenceTypes()) {
                    String term = ConsequenceTypeMappings.accessionToTerm.get(so);
                    if (term != null) {
                        cts.add(term);
                    } else {
                        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Mapping not found for SO code {0}", so);
                    }
                }
            }
        }
        
        if (cts.size() > 0) {
            file.addAttribute(this.ctTag, Joiner.on(",").join(cts));
        }

    }
}

