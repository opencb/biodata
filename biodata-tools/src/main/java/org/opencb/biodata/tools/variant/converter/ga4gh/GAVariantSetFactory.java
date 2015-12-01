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

package org.opencb.biodata.tools.variant.converter.ga4gh;

import org.ga4gh.models.VariantSet;
import org.ga4gh.models.VariantSetMetadata;
import org.opencb.biodata.models.variant.VariantSource;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantSetFactory {
    
    public static List<VariantSet> create(List<VariantSource> variantSources) {
        Set<VariantSet> gaVariantSets = new LinkedHashSet<>();

        for (VariantSource source : variantSources) {
            // TODO This header should be already split
            List<VariantSetMetadata> setMetadata = new ArrayList<>();
            String header = source.getMetadata().get("header").toString();

            for (String line : header.split("\n")) {
                if (line.startsWith("#CHROM")) {
                    continue;
                }

                VariantSetMetadata metadata = getMetadataLine(line);
                setMetadata.add(metadata);
            }

            VariantSet variantSet = new VariantSet(source.getFileId(), source.getStudyId(), "", setMetadata);
            gaVariantSets.add(variantSet);
        }

        return new ArrayList<>(gaVariantSets);
    }

    private static VariantSetMetadata getMetadataLine(String line) {
        VariantSetMetadata metadata = new VariantSetMetadata();
        // Split by square brackets that are NOT between quotes
        String[] split = line.split("(<|>)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        if (split.length > 1) { // Header entries like INFO or FORMAT
            // Remove leading ## and trailing equals symbol
            metadata.setKey(split[0].substring(2, split[0].length()-1));
            metadata.setValue(split[1]);

            // Split by commas that are NOT between quotes
            String[] valueSplit = split[1].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            for (String pair : valueSplit) { // Key-value pairs
                String[] pairSplit = pair.split("=", 2);
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
                    default:
//                        metadata.addInfo(pairSplit[0], pairSplit[1]);
                }
            }
        } else {
            // Simpler entry like "assembly=GRCh37"
            split = line.split("=", 2);
            // Remove leading ## and trailing equals symbol
            metadata.setKey(split[0].substring(2));
            metadata.setId(split[0].substring(2));
            if (split.length > 1) {
                metadata.setValue(split[1]);
            }
        }

        return metadata;
    }
}
