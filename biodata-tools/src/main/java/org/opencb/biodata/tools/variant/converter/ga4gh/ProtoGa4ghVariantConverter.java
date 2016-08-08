package org.opencb.biodata.tools.variant.converter.ga4gh;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import ga4gh.Variants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 08/08/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class ProtoGa4ghVariantConverter extends AbstractGa4ghVariantConverter<Variants.Variant, Variants.Call> {

    public ProtoGa4ghVariantConverter() {
    }

    public ProtoGa4ghVariantConverter(boolean addCallSetName, Map<String, Integer> callSetNameId) {
        super(addCallSetName, callSetNameId);
    }

    @Override
    protected Variants.Variant newVariant(String id, String variantSetId, List<String> names, Long created, Long updated,
                                          String referenceName, Long start, Long end, String reference, List<String> alternates,
                                          Map<String, List<String>> info, List<Variants.Call> calls) {
        Variants.Variant.Builder builder = Variants.Variant.newBuilder()
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

        putAll(info, builder.getMutableInfo());

        return builder.build();
    }

    @Override
    protected Variants.Call newCall(String callSetName, String callSetId, List<Integer> allelesIdx, String phaseSet,
                                    List<Double> genotypeLikelihood, Map<String, List<String>> info) {
        Variants.Call.Builder builder = Variants.Call.newBuilder()
                .setCallSetName(callSetName)
                .setCallSetId(callSetId)
                .addAllGenotype(allelesIdx)
                .setPhaseset(phaseSet)
                .addAllGenotypeLikelihood(genotypeLikelihood);

        putAll(info, builder.getMutableInfo());

        return builder.build();
    }

    public static void putAll(Map<String, List<String>> info, Map<String, ListValue> mutableInfo) {
        info.forEach((key, values) -> {
            List<Value> listValues = values.stream().map(v -> Value.newBuilder().setStringValue(key).build()).collect(Collectors.toList());
            mutableInfo.put(key, ListValue.newBuilder().addAllValues(listValues).build());
        });
    }
}
