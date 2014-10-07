package org.opencb.biodata.formats.variant.io;

import java.util.List;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.commons.io.DataReader;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public interface VariantReader extends DataReader<Variant> {
    
    public List<String> getSampleNames();

    public String getHeader();
}