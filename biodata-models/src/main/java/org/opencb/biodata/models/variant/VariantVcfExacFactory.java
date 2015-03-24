package org.opencb.biodata.models.variant;

import java.util.Properties;

/**
 * Created by jmmut on 23/03/15.
 */
public class VariantVcfExacFactory extends VariantAggregatedVcfFactory {
    public VariantVcfExacFactory() {
        this(null);
    }

    public VariantVcfExacFactory(Properties tagMap) {
        super(tagMap);
    }
}
