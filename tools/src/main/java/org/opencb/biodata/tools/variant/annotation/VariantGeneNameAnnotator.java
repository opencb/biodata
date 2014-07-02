package org.opencb.biodata.tools.variant.annotation;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.ConsequenceType;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantGeneNameAnnotator implements VariantAnnotator {

    private String geneNameTag;

    public VariantGeneNameAnnotator() {
        this("GeneNames");
    }

    public VariantGeneNameAnnotator(String geneNameTag) {
        this.geneNameTag = geneNameTag;
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
                annotGeneName(variant, file.getValue());
            }
        }
    }

    private void annotGeneName(Variant variant, ArchivedVariantFile file) {
        Set<String> geneNames = new HashSet<>();

        for (List<ConsequenceType> list : variant.getEffect().getConsequenceTypes().values()) {
            for (ConsequenceType ct : list) {
                if (!ct.getGeneName().isEmpty()) {
                    geneNames.add(ct.getGeneName());
                }
            }
        }

        if (geneNames.size() > 0) {
            file.addAttribute(this.geneNameTag, Joiner.on(",").join(geneNames));
        }

    }

}
