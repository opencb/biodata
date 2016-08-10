package org.opencb.biodata.tools.ga4gh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 09/08/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public interface Ga4ghVariantFactory<VARIANT, CALL, CALL_SET, VARIANT_SET, VARIANT_SET_METADATA> {


    CALL_SET newCallSet(String callSetId, String callSetName, String sampleId, ArrayList<String> variantSetIds,
                        long created, long updated, Map<String, List<String>> info);


    VARIANT newVariant(String id, String variantSetId, List<String> names, Long created, Long updated,
                       String referenceName, Long start, Long end, String reference, List<String> alternates,
                       Map<String, List<String>> info, List<CALL> calls) ;

    CALL newCall(String callSetName, String callSetId, List<Integer> allelesIdx, String phaseSet,
                 List<Double> genotypeLikelihood, Map<String, List<String>> info);


    VARIANT_SET newVariantSet(String id, String name, String datasetId, String referenceSetId, List<VARIANT_SET_METADATA> metadata);

    VARIANT_SET_METADATA newVariantSetMetadata(String key, String value, String id, String type, String number, String description,
                                               Map<String, List<String>> info);
}
