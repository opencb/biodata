package org.opencb.biodata.models.variant.ga4gh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.ga4gh.GACall;
import org.opencb.biodata.ga4gh.GAKeyValue;
import org.opencb.biodata.ga4gh.GAVariant;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantFactory {

    public static List<GAVariant> create(List<Variant> variants) {
        Set<GAVariant> gaVariants = new LinkedHashSet<>();

        for (Variant variant : variants) {
            for (VariantSourceEntry file : variant.getSourceEntries().values()) {
                String[] vcfLine = file.getAttribute("src").split("\t");
                String id = variant.getId().isEmpty() ? vcfLine[0] + "_" + vcfLine[1] : variant.getId();
                
                GAVariant ga;
                if (vcfLine.length > 8) {
                    ga = new GAVariant(id, file.getFileId(), vcfLine[2].split(";"), System.currentTimeMillis(), System.currentTimeMillis(),
                            vcfLine[0], Integer.parseInt(vcfLine[1]), Integer.parseInt(vcfLine[1]) + vcfLine[3].length(), 
                            vcfLine[3], vcfLine[4].split(","), parseInfo(vcfLine[7].split(";")),
                            parseCalls(vcfLine[8].split(":"), Arrays.copyOfRange(vcfLine, 9, vcfLine.length), file.getFileId()));
                } else {
                    ga = new GAVariant(id, file.getFileId(), vcfLine[2].split(";"), System.currentTimeMillis(), System.currentTimeMillis(),
                            vcfLine[0], Integer.parseInt(vcfLine[1]), Integer.parseInt(vcfLine[1]) + vcfLine[3].length(), 
                            vcfLine[3], vcfLine[4].split(","), parseInfo(vcfLine[7].split(";")), null);
                }
                
                gaVariants.add(ga);
            }
        }

        return new ArrayList<>(gaVariants);
    }

    private static Map<String, List> parseInfo(String[] infoFields) {
        Map<String, List> kvs = new HashMap<>();
        
        for (String subfield : infoFields) {
            String[] parts = subfield.split("=");
            if (parts.length > 1) {
                kvs.put(parts[0], new ArrayList<>(Arrays.asList(parts[1].split(","))));
            } else {
                kvs.put(parts[0], new ArrayList<>());
            }
        }
        
        return kvs;
    }
    
    private static GACall[] parseCalls(String[] formatFields, String[] samplesFields, String callSetName) {
        List<GACall> calls = new LinkedList<>();
        int idxLikelihoodField = Arrays.binarySearch(formatFields, "GL");
        
        for (String sample : samplesFields) {
            String[] parts = sample.split(":");
            
            String[] alleles = parts[0].split("/|\\|", -1);
            int[] genotype = new int[alleles.length];
            for (int i = 0; i < alleles.length; i++) {
                genotype[i] = (alleles[i].equals(".")) ? -1 : Integer.parseInt(alleles[i]);
            }
            
            String phaseSet = parts[0].contains("|") ? "phased" : null;
            
            double[] genotypeLikelihood = null;
            if (idxLikelihoodField > -1) {
                String[] glParts = parts[idxLikelihoodField].split(",");
                if (glParts.length > 0) {
                    genotypeLikelihood = new double[glParts.length];
                    for (int i = 0; i < glParts.length; i++) {
                        genotypeLikelihood[i] = (glParts[i].equals(".")) ? -1 : Double.parseDouble(glParts[i]);
                    }
                }
            }
            
            // Process the rest of fields in the sample
            // Avoid processing the GT and GL fields again
            GAKeyValue[] info = (parts.length > 2) ? new GAKeyValue[parts.length-2] : null;
            if (info != null) {
                int curIdx = 0;
                for (int i = 1; i < parts.length; i++) {
                    if (i != idxLikelihoodField) { // Do not parse GL again
                        info[curIdx++] = new GAKeyValue(formatFields[i], parts[i]);
                    }
                }
            }
            
            calls.add(new GACall(callSetName, callSetName, genotype, phaseSet, genotypeLikelihood, info));
        }
        
        GACall[] retCalls = new GACall[calls.size()];
        return calls.toArray(retCalls);
    }
    
}
