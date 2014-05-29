package org.opencb.biodata.tools.variant.annotation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantPolyphenSIFTAnnotator implements VariantAnnotator {

    private String polyphenScoreTag;
    private String polyphenEffectTag;
    private String siftScoreTag;
    private String siftEffectTag;


    public VariantPolyphenSIFTAnnotator() {
        this("PolyphenScore", "PolyphenEffect", "SIFTScore", "SIFTEffect");
    }

    public VariantPolyphenSIFTAnnotator(String polyphenScoreTag, String polyphenEffectTag, String siftScoreTag, String siftEffectTag) {
        this.polyphenScoreTag = polyphenScoreTag;
        this.polyphenEffectTag = polyphenEffectTag;
        this.siftScoreTag = siftScoreTag;
        this.siftEffectTag = siftEffectTag;
    }

    @Override
    public void annot(Variant elem) {
        annot(Arrays.asList(elem));
    }
    
    @Override
    public void annot(List<Variant> batch) {
        EffectCalculator.setEffects(batch, true, true);

        for (Variant variant : batch) {
            for (Map.Entry<String, ArchivedVariantFile> file : variant.getFiles().entrySet()) {
                annotPolyphenSIFT(variant, file.getValue());
            }
        }

    }

    private void annotPolyphenSIFT(Variant variant, ArchivedVariantFile file) {
        if (!file.hasAttribute(this.polyphenScoreTag)) {
            annotPolyphen(variant, file);
        }

        if (!file.hasAttribute(this.siftScoreTag)) {
            annotSIFT(variant, file);
        }
    }

    private void annotPolyphen(Variant variant, ArchivedVariantFile file) {
        double poly = -1;
        int effect = 0;

        for (VariantEffect ve : variant.getEffect()) {
            if (ve.getPolyphenScore() != -1 && ve.getPolyphenScore() > poly) {
                poly = ve.getPolyphenScore();
                effect = ve.getPolyphenEffect();
            }
        }
        if (poly >= 0) {
            file.addAttribute(this.polyphenScoreTag, String.valueOf(poly));
            file.addAttribute(this.polyphenEffectTag, String.valueOf(effect));
        }
    }

    private void annotSIFT(Variant variant, ArchivedVariantFile file) {
        double sift = 2;
        int effect = 0;

        for (VariantEffect ve : variant.getEffect()) {
            if (ve.getSiftScore() != -1 && ve.getSiftScore() < sift) {
                sift = ve.getSiftScore();
            }
        }
        if (sift <= 1) {
            file.addAttribute(this.siftScoreTag, String.valueOf(sift));
            file.addAttribute(this.siftEffectTag, String.valueOf(effect));
        }
    }

}
