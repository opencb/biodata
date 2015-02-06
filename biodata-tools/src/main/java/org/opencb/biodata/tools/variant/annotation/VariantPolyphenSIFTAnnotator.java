package org.opencb.biodata.tools.variant.annotation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.annotation.ProteinSubstitutionScores;
import org.opencb.biodata.tools.variant.EffectCalculator;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@Deprecated
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
            for (Map.Entry<String, VariantSourceEntry> file : variant.getSourceEntries().entrySet()) {
                annotPolyphenSIFT(variant, file.getValue());
            }
        }

    }

    private void annotPolyphenSIFT(Variant variant, VariantSourceEntry file) {
        if (!file.hasAttribute(this.polyphenScoreTag)) {
            annotPolyphen(variant, file);
        }

        if (!file.hasAttribute(this.siftScoreTag)) {
            annotSIFT(variant, file);
        }
    }

    private void annotPolyphen(Variant variant, VariantSourceEntry file) {
        ProteinSubstitutionScores scores = variant.getAnnotation().getProteinSubstitutionScores();
        if (scores.getPolyphenScore() >= 0) {
            file.addAttribute(this.polyphenScoreTag, String.valueOf(scores.getPolyphenScore()));
            file.addAttribute(this.polyphenEffectTag, String.valueOf(scores.getPolyphenEffect().name()));
        }
    }

    private void annotSIFT(Variant variant, VariantSourceEntry file) {
        ProteinSubstitutionScores scores = variant.getAnnotation().getProteinSubstitutionScores();
        if (scores.getSiftScore() >= 0) {
            file.addAttribute(this.siftScoreTag, String.valueOf(scores.getSiftScore()));
            file.addAttribute(this.siftEffectTag, String.valueOf(scores.getSiftEffect().name()));
        }
    }

}
