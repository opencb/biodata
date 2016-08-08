package org.opencb.biodata.tools.variant.converter.ga4gh;

import java.util.*;

/**
 * Created on 08/08/16.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public abstract class AbstractGa4ghCallSetConverter<CS> {

    public List<CS> convert(List<String> variantSetNames, List<List<String>> callSets) {
        List<CS> sets = new ArrayList<>();

        Iterator<String> variantSetNamesIterator = variantSetNames.iterator();
        Iterator<List<String>> callSetsIterator = callSets.iterator();

        while (variantSetNamesIterator.hasNext() && callSetsIterator.hasNext()) {
            String fileName = variantSetNamesIterator.next();
            List<String> callsInFile = callSetsIterator.next();

            long time = 0;

            // Add all samples in the file
            for (String callName : callsInFile) {
                ArrayList<String> variantSetIds = new ArrayList<>(1);
                variantSetIds.add(fileName);
                CS callset = newCallSet(callName, callName, callName, variantSetIds, time, time, Collections.emptyMap());
                sets.add(callset);
            }
        }

        return sets;
    }

    protected abstract CS newCallSet(String callSetId, String callSetName, String sampleId, ArrayList<String> variantSetIds,
                                     long created, long updated, Map<String, List<String>> info);

}
