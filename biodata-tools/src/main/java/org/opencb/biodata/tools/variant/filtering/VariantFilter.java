package org.opencb.biodata.tools.variant.filtering;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.commons.filters.Filter;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public abstract class VariantFilter extends Filter<Variant> {

    public VariantFilter() {
        super(0);
    }

    public VariantFilter(int priority) {
        super(priority);
    }
}
