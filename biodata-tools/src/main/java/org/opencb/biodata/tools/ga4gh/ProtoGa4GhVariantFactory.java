package org.opencb.biodata.tools.ga4gh;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import ga4gh.Variants.Call;
import ga4gh.Variants.CallSet;
import ga4gh.Variants.Variant;
import ga4gh.Variants.VariantSetMetadata;
import ga4gh.Variants.VariantSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 09/08/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class ProtoGa4GhVariantFactory implements Ga4ghVariantFactory<Variant, Call, CallSet, VariantSet, VariantSetMetadata> {

    @Override
    public Variant newVariant(String id, String variantSetId, List<String> names, Long created, Long updated,
                                          String referenceName, Long start, Long end, String reference, List<String> alternates,
                                          Map<String, List<String>> info, List<Call> calls) {
        Variant.Builder builder = Variant.newBuilder()
                .setId(id)
                .setVariantSetId(variantSetId)
                .addAllNames(names)
                .setCreated(created)
                .setUpdated(updated)
                .setReferenceName(referenceName)
                .setStart(start)
                .setEnd(end)
                .setReferenceBases(reference)
                .addAllAlternateBases(alternates)
                .addAllCalls(calls);

        putAll(builder.getMutableInfo(), info);

        return builder.build();
    }

    @Override
    public Call newCall(String callSetName, String callSetId, List<Integer> allelesIdx, String phaseSet,
                                    List<Double> genotypeLikelihood, Map<String, List<String>> info) {
        Call.Builder builder = Call.newBuilder()
                .setCallSetName(callSetName == null ? "" : callSetName)
                .setCallSetId(callSetId == null ? "" : callSetId)
                .addAllGenotype(allelesIdx)
                .setPhaseset(phaseSet)
                .addAllGenotypeLikelihood(genotypeLikelihood);

        putAll(builder.getMutableInfo(), info);

        return builder.build();
    }

    @Override
    public CallSet newCallSet(String callSetId, String callSetName, String sampleId, ArrayList<String> variantSetIds,
                                          long created, long updated, Map<String, List<String>> info) {
        CallSet.Builder builder = CallSet.newBuilder()
                .setId(callSetId)
                .setName(callSetName)
                .setSampleId(sampleId)
                .addAllVariantSetIds(variantSetIds)
                .setCreated(created)
                .setUpdated(updated);

        putAll(builder.getMutableInfo(), info);
        return builder.build();
    }

    @Override
    public VariantSet newVariantSet(String id, String name, String datasetId, String referenceSetId, List<VariantSetMetadata> metadata) {
        return VariantSet.newBuilder()
                .setId(id)
                .setName(name)
                .setDatasetId(datasetId)
                .setReferenceSetId(referenceSetId)
                .addAllMetadata(metadata)
                .build();
    }

    @Override
    public VariantSetMetadata newVariantSetMetadata(String key, String value, String id, String type, String number, String description, Map<String, List<String>> info) {
        VariantSetMetadata.Builder builder = VariantSetMetadata.newBuilder()
                .setKey(key)
                .setValue(value)
                .setId(id)
                .setType(type)
                .setNumber(number)
                .setDescription(description);
        putAll(builder.getMutableInfo(), info);
        return builder.build();
    }

    protected static void putAll(Map<String, ListValue> mutableInfo, Map<String, List<String>> info) {
        info.forEach((key, values) -> {
            List<Value> listValues = values
                    .stream()
                    .map(v -> Value.newBuilder().setStringValue(v).build())
                    .collect(Collectors.toList());
            mutableInfo.put(key, ListValue.newBuilder().addAllValues(listValues).build());
        });
    }
}
