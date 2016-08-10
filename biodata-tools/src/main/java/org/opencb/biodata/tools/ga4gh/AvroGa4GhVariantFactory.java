package org.opencb.biodata.tools.ga4gh;

import org.ga4gh.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 09/08/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class AvroGa4GhVariantFactory implements Ga4ghVariantFactory<Variant, Call, CallSet, VariantSet, VariantSetMetadata> {

    @Override
    public CallSet newCallSet(String callSetId, String callSetName, String sampleId, ArrayList<String> variantSetIds,
                              long created, long updated, Map<String, List<String>> info) {
        return new CallSet(callSetId, callSetName, sampleId, variantSetIds, created, updated, info);
    }
    @Override
    public Variant newVariant(String id, String variantSetId, List<String> names, Long created, Long updated,
                                 String referenceName, Long start, Long end, String reference, List<String> alternates,
                                 Map<String, List<String>> info, List<Call> calls) {
        return new Variant(id, variantSetId, names, created, updated, referenceName,
                start,                // Ga4gh uses 0-based positions.
                end,                  // 0-based end does not change
                reference, alternates, info, calls);
    }

    @Override
    public Call newCall(String callSetName, String callSetId, List<Integer> allelesIdx, String phaseSet, List<Double> genotypeLikelihood,
                           Map<String, List<String>> info) {
        return new Call(callSetName, callSetId, allelesIdx, phaseSet, genotypeLikelihood, info);
    }

    @Override
    public VariantSet newVariantSet(String id, String name, String datasetId, String referenceSetId, List<VariantSetMetadata> metadata) {
        return new VariantSet(id, name, datasetId, referenceSetId, metadata);
    }

    @Override
    public VariantSetMetadata newVariantSetMetadata(String key, String value, String id, String type, String number, String description, Map<String, List<String>> info) {
        return new VariantSetMetadata(key, value, id, type, number, description, info);
    }

}
