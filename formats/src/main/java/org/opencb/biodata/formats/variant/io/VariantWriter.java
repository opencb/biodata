package org.opencb.biodata.formats.variant.io;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.commons.io.DataWriter;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public interface VariantWriter extends DataWriter<Variant> {

    void includeStats(boolean stats);

    void includeSamples(boolean samples);

    void includeEffect(boolean effect);
}
