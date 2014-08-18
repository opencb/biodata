package org.opencb.biodata.models.variant;

import java.util.List;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public interface VariantFactory {

    public List<Variant> create(VariantSource source, String line);


}
