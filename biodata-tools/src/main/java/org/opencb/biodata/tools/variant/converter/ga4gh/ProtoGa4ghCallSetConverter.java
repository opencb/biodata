package org.opencb.biodata.tools.variant.converter.ga4gh;

import ga4gh.Variants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 08/08/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class ProtoGa4ghCallSetConverter<CS> extends AbstractGa4ghCallSetConverter<Variants.CallSet> {

    public ProtoGa4ghCallSetConverter() {
        super();
    }

    @Override
    protected Variants.CallSet newCallSet(String callSetId, String callSetName, String sampleId, ArrayList<String> variantSetIds,
                                          long created, long updated, Map<String, List<String>> info) {
        Variants.CallSet.Builder builder = Variants.CallSet.newBuilder()
                .setId(callSetId)
                .setName(callSetName)
                .setSampleId(sampleId)
                .addAllVariantSetIds(variantSetIds)
                .setCreated(created)
                .setUpdated(updated);
        ProtoGa4ghVariantConverter.putAll(info, builder.getMutableInfo());
        return builder.build();
    }
}
