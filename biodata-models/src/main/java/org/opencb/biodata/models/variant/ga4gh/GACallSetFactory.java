package org.opencb.biodata.models.variant.ga4gh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.opencb.biodata.ga4gh.GACallSet;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GACallSetFactory {
    
    public static List<GACallSet> create(List<String> variantSetNames, List<List<String>> callSets) {
        List<GACallSet> sets = new ArrayList<>();
        
        Iterator<String> variantSetNamesIterator = variantSetNames.iterator();
        Iterator<List<String>> callSetsIterator = callSets.iterator();
        
        while (variantSetNamesIterator.hasNext() && callSetsIterator.hasNext()) {
            String fileName = variantSetNamesIterator.next();
            List<String> callsInFile = callSetsIterator.next();
            // Add all samples in the file
            for (String callName : callsInFile) {
                GACallSet callset = new GACallSet(callName, callName, callName, new ArrayList<>(Arrays.asList(fileName)), 
                        System.currentTimeMillis(), System.currentTimeMillis(), null);
                sets.add(callset);
            }
        }
        
        return sets;
    }
}
