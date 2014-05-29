package org.opencb.biodata.tools.variant.annotation;

import com.google.common.base.Joiner;
import java.util.Arrays;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
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
            for (Map.Entry<String, ArchivedVariantFile> file : variant.getFiles().entrySet()) {
                annotVariantEffect(variant, file.getValue());
            }
        }
    }

    private void annotVariantEffect(Variant variant, ArchivedVariantFile file) {
        Set<String> ct = new HashSet<>();
        for (VariantEffect effect : variant.getEffect()) {
            if (!effect.getConsequenceTypeObo().isEmpty())
                ct.add(effect.getConsequenceTypeObo());
        }

        if (ct.size() > 0) {
            file.addAttribute(this.ctTag, Joiner.on(",").join(ct));
        }

    }
}
