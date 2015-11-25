/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.variant.ga4gh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opencb.biodata.ga4gh.GACall;
import org.opencb.biodata.ga4gh.GAVariant;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantFactory {

    /**
     * Given a list of variants, creates the equivalent set using the GA4GH API.
     * 
     * @param variants List of variants to transform
     * @return GA4GH variants representing the same data as the internal API ones
     */
    public static List<GAVariant> create(List<Variant> variants){//, Map<String, List<String>> samplesPerSource) {
        Set<GAVariant> gaVariants = new LinkedHashSet<>();

        for (Variant variant : variants) {
            String id = String.format("%s_%d_%s_%s", variant.getChromosome(), variant.getStart(), variant.getReference(), variant.getAlternate());

            Set<String> variantIds = variant.getIds();
            String[] names = variantIds.toArray(new String[variantIds.size()]);
            
            for (VariantSourceEntry file : variant.getSourceEntries().values()) {
                String[] alternates = new String[file.getSecondaryAlternates().length + 1];
                alternates[0] = variant.getAlternate();
                System.arraycopy(file.getSecondaryAlternates(), 0, alternates, 1, file.getSecondaryAlternates().length);
                
                GAVariant ga;
                if (file.getSamplesData().isEmpty()) {
                    // No genotypes, simplest case
                    ga = new GAVariant(id, file.getFileId(), names, System.currentTimeMillis(), System.currentTimeMillis(), 
                            variant.getChromosome(), variant.getStart(), variant.getEnd(), variant.getReference(), alternates, 
                            parseInfo(file.getAttributes()), null);
                } else {
                    ga = new GAVariant(id, file.getFileId(), names, System.currentTimeMillis(), System.currentTimeMillis(), 
                            variant.getChromosome(), variant.getStart(), variant.getEnd(), variant.getReference(), alternates, 
                            parseInfo(file.getAttributes()), parseCalls(file.getSamplesData()));
                }
                
                gaVariants.add(ga);
            }
        }

        
        return new ArrayList<>(gaVariants);
    }

    private static Map<String, List> parseInfo(Map<String, String> attributes) {
        Map<String, List> kvs = new HashMap<>();
        
        for (Map.Entry<String, String> field : attributes.entrySet()) {
            List<String> value = new ArrayList<>();
            value.add(field.getValue());
            kvs.put(field.getKey(), value);
        }
        
        return kvs;
    }
    
    private static GACall[] parseCalls(Map<String, Map<String, String>> samples) {
        List<GACall> calls = new LinkedList<>();
        
        for (Map.Entry<String, Map<String, String>> sample : samples.entrySet()) {
            Map<String, String> attrs = sample.getValue();
            
            // Transform genotype with form like 0|0 to the GA4GH style
            String gtField = attrs.get("GT");
            String[] alleles = gtField.split("/|\\|", -1);
            int[] genotype = new int[alleles.length];
            for (int i = 0; i < alleles.length; i++) {
                genotype[i] = (alleles[i].equals(".")) ? -1 : Integer.parseInt(alleles[i]);
            }
            
            // Check whether it is phased depending on the allele separator
            String phaseSet = gtField.contains("|") ? "phased" : "unphased";
            
            // Create the call object
            calls.add(new GACall(sample.getKey(), sample.getKey(), genotype, phaseSet, null, null));
        }
        
        return calls.toArray(new GACall[calls.size()]);
    }
    
}
