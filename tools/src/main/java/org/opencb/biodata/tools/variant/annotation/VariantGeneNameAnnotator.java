package org.opencb.biodata.tools.variant.annotation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;

import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantGeneNameAnnotator implements VariantAnnotator {

    private VariantSource study;

    public VariantGeneNameAnnotator(VariantSource study) {
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
        Set<String> geneNames = new HashSet<>();
        for (VariantEffect effect : batchEffect) {
            if (variant.getChromosome().equals(effect.getChromosome())
                    && variant.getStart() == effect.getPosition()
                    && variant.getReference().equals(effect.getReferenceAllele())
                    && variant.getAlternate().equals(effect.getAlternativeAllele())) {

                geneNames.add(effect.getGeneName());
            }
        }

        String geneNamesAll = Joiner.on(",").join(geneNames);

        if (geneNames.size() > 0) {
            file.addAttribute("GeneNames", geneNamesAll);
//            variant.addInfoField("GeneNames=" + geneNamesAll);
        }

    }

}
