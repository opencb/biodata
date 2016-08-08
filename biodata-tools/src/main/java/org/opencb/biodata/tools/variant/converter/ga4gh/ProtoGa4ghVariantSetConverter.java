package org.opencb.biodata.tools.variant.converter.ga4gh;

import ga4gh.Variants;

import java.util.List;
import java.util.Map;

/**
 * Created on 08/08/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class ProtoGa4ghVariantSetConverter extends AbstractGa4ghVariantSetConverter<Variants.VariantSet, Variants.VariantSetMetadata> {
    @Override
    Variants.VariantSet newVariantSet(String id, String name, String datasetId, String referenceSetId, List<Variants.VariantSetMetadata> metadata) {
        return null;
    }

    @Override
    protected Variants.VariantSetMetadata newVariantSetMetadata(String key, String value, String id, String type, String number, String description, Map<String, List<String>> info) {
        return null;
    }
}
