package org.opencb.biodata.models.variant.ga4gh;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.opencb.biodata.ga4gh.GAVariantSet;
import org.opencb.biodata.ga4gh.GAVariantSetMetadata;
import org.opencb.biodata.models.variant.VariantSource;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantSetFactory {
    
    public static List<GAVariantSet> create(List<VariantSource> variantSources) {
        Set<GAVariantSet> gaVariantSets = new LinkedHashSet<>();
        
        for (VariantSource source : variantSources) {
            // TODO This header should be already split
            List<GAVariantSetMetadata> setMetadata = new ArrayList<>();
            String header = source.getMetadata().get("header").toString();
            
            for (String line : header.split("\n")) {
                if (line.startsWith("#CHROM")) {
                    continue;
                }
                
                GAVariantSetMetadata metadata = getMetadataLine(line);
                setMetadata.add(metadata);
            }
            
            GAVariantSet variantSet = new GAVariantSet(source.getFileId(), source.getStudyId(), setMetadata);
            gaVariantSets.add(variantSet);
        }
        
        return new ArrayList<>(gaVariantSets);
    }
    
    private static GAVariantSetMetadata getMetadataLine(String line) {
        GAVariantSetMetadata metadata = new GAVariantSetMetadata();
        String[] split = line.split("<|>");
        
        if (split.length > 1) { // Header entries like INFO or FORMAT
            // Remove leading ## and trailing equals symbol
            metadata.setKey(split[0].substring(2, split[0].length()-1));
            
            String[] valueSplit = split[1].split(",");
            
            for (String pair : valueSplit) { // Key-value pairs
                String[] pairSplit = pair.split("=");
                switch (pairSplit[0]) {
                    case "ID":
                        metadata.setId(pairSplit[1]);
                        break;
                    case "Number":
                        metadata.setNumber(pairSplit[1]);
                        break;
                    case "Type":
                        metadata.setType(pairSplit[1]);
                        break;
                    case "Description":
                        metadata.setDescription(pairSplit[1]);
                        break;
                }
            }
        } else {
            // Simpler entry like "assembly=GRCh37"
            split = line.split("=");
            metadata.setKey(split[0]);
            if (split.length > 1) {
                metadata.setValue(split[1]);
            }
        }
        
        return metadata;
    }
}
