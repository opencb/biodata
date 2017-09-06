package org.opencb.biodata.tools.variant.stats;

import org.opencb.biodata.models.variant.metadata.Aggregation;

/**
 * Created on 16/08/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class AggregationUtils {

    public static boolean isAggregated(Aggregation aggregation) {
        return !Aggregation.NONE.equals(aggregation);
    }

}
