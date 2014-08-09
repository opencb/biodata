package org.opencb.biodata.tools.variant.annotation;

import java.util.List;
import org.opencb.biodata.models.variant.Variant;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public interface VariantAnnotator {
    
    public void annot(List<Variant> batch);

    public void annot(Variant elem);
    
}
