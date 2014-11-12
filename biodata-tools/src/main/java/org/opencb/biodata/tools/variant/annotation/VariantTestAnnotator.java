package org.opencb.biodata.tools.variant.annotation;

import java.util.List;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSourceEntry;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantTestAnnotator implements VariantAnnotator {

    private String text;

    public VariantTestAnnotator(String text) {
        this.text = text;
    }

    @Override
    public void annot(List<Variant> batch) {
        for (Variant vr : batch) {
            vr.addSourceEntry(new VariantSourceEntry(text, text));
            annot(vr);
        }
    }

    @Override
    public void annot(Variant elem) {
        elem.getSourceEntry(text, null).addAttribute("TEXT", text);
    }
}
