package org.opencb.biodata.tools.variant.annotation;

import java.util.List;
import org.opencb.biodata.models.variant.Variant;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public interface VariantAnnotator {
    
    public void annot(List<Variant> batch);

    public void annot(Variant elem);
    
}
