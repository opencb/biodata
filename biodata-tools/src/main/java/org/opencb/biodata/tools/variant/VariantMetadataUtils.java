package org.opencb.biodata.tools.variant;

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.variant.metadata.VariantDatasetMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joaquin on 9/4/17.
 */
public class VariantMetadataUtils {

    /**
     * Get 
     *
     * @param variantDatasetMetadata
     * @return
     */
    public static List<String> getSampleNames(VariantDatasetMetadata variantDatasetMetadata) {
        if (variantDatasetMetadata == null) {
            return null;
        }

        List<String> sampleNames = new ArrayList<>();
        if (variantDatasetMetadata.getIndividuals() != null) {
            for (org.opencb.biodata.models.metadata.Individual individual : variantDatasetMetadata.getIndividuals()) {
                for (Sample sample : individual.getSamples()) {
                    if (!StringUtils.isEmpty(sample.getId())) {
                        sampleNames.add(sample.getId());
                    }
                }
            }
        }
        return sampleNames;
    }
}
