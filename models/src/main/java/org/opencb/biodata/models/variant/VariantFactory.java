package org.opencb.biodata.models.variant;

import java.util.List;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public interface VariantFactory {

    public List<Variant> create(VariantSource source, String line);


}
