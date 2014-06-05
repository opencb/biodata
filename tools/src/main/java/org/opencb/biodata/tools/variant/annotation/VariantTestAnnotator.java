package org.opencb.biodata.tools.variant.annotation;

import java.util.List;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.ArchivedVariantFile;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantTestAnnotator implements VariantAnnotator {

    private String text;

    public VariantTestAnnotator(String text) {
        this.text = text;
    }

    @Override
    public void annot(List<Variant> batch) {
        for (Variant vr : batch) {
            vr.addFile(new ArchivedVariantFile(text, text, text));
            annot(vr);
        }
    }

    @Override
    public void annot(Variant elem) {
        elem.getFile(text).addAttribute("TEXT", text);
    }
}
