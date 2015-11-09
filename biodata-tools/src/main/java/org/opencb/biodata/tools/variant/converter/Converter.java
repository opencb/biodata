/**
 * 
 */
package org.opencb.biodata.tools.variant.converter;

import org.opencb.commons.run.ParallelTaskRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
public interface Converter <FROM,TO> extends ParallelTaskRunner.Task<FROM, TO> {

    TO convert(FROM from);

    default List<TO> apply(List<FROM> from) {
        List<TO> convertedBatch = new ArrayList<>(from.size());
        for (FROM variantContext : from) {
            convertedBatch.add(convert(variantContext));
        }
        return convertedBatch;
    }

}
