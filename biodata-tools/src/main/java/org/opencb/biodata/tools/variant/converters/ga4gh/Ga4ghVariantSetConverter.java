/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant.converters.ga4gh;

import ga4gh.Variants;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderLine;
import org.opencb.biodata.tools.variant.converters.ga4gh.factories.Ga4ghVariantFactory;
import org.opencb.biodata.tools.variant.converters.ga4gh.factories.ProtoGa4GhVariantFactory;
import org.opencb.biodata.tools.variant.converters.Converter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 08/08/16.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class Ga4ghVariantSetConverter<VS> implements Converter<VariantFileMetadata, VS> {

    private final Ga4ghVariantFactory<?, ?, ?, VS, ?> factory;

    public Ga4ghVariantSetConverter(Ga4ghVariantFactory<?, ?, ?, VS, ?> factory) {
        this.factory = factory;
    }

    public Ga4ghVariantSetConverter() {
        this((Ga4ghVariantFactory) new ProtoGa4GhVariantFactory());
    }

    public static Ga4ghVariantSetConverter<Variants.VariantSet> converter() {
        return new Ga4ghVariantSetConverter<>(new ProtoGa4GhVariantFactory());
    }

    @Override
    public VS convert(VariantFileMetadata source) {
        return apply(Collections.singletonList(source)).get(0);
    }

    @Override
    public List<VS> apply(List<VariantFileMetadata> variantFileMetadata) {
        Set<VS> gaVariantSets = new LinkedHashSet<>();

        for (VariantFileMetadata fileMetadata : variantFileMetadata) {
            List<Object> metadata = new ArrayList<>();
            for (VariantFileHeaderLine line : fileMetadata.getHeader().getLines()) {
                Map<String, List<String>> info = line.getAttributes().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                value -> Arrays.asList(value.getValue().split(","))));
                metadata.add(factory.newVariantSetMetadata(line.getKey(), null, line.getId(), line.getType(), line.getNumber(), line.getDescription(), info));
            }
            fileMetadata.getHeader().getAttributes().forEach((key, value) ->
                    metadata.add(factory.newVariantSetMetadata(key, value, null, null, null, null, Collections.emptyMap())));

            @SuppressWarnings("unchecked")
            VS variantSet = (VS) factory.newVariantSet(fileMetadata.getId(), fileMetadata.getAlias(), "", "", (List) metadata);
            gaVariantSets.add(variantSet);
        }

        return new ArrayList<>(gaVariantSets);
    }

    private Object getMetadataLine(String key, Map<String, String> map) {
        String id = "";
        String number = "";
        String type = "";
        String description = "";
        Map<String, List<String>> info = new HashMap<>();

        for (Map.Entry<String, String> e : map.entrySet()) {
            switch (e.getKey().toLowerCase()) {
                case "id":
                    id = e.getValue();
                    break;
                case "number":
                    number = e.getValue();
                    break;
                case "type":
                    type = e.getValue();
                    break;
                case "description":
                    description = e.getValue();
                    break;
                default:
                    if (e.getValue().contains(",")) {
                        info.put(e.getKey(), Arrays.asList(e.getValue().split(",")));
                    } else {
                        info.put(e.getKey(), Collections.singletonList(e.getValue()));
                    }
            }
        }
        return factory.newVariantSetMetadata(key, "", id, type, number, description, info);
    }

    protected Object getMetadataLine(String line) {

        String key = "";
        String value = "";
        String id = "";
        String number = "";
        String type = "";
        String description = "";
        Map<String, List<String>> info = Collections.emptyMap();

        // Split by square brackets that are NOT between quotes
        String[] split = line.split("(<|>)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        if (split.length > 1) { // Header entries like INFO or FORMAT
            // Remove leading ## and trailing equals symbol
            key = split[0].substring(2, split[0].length()-1);
            value = split[1];

            // Split by commas that are NOT between quotes
            String[] valueSplit = split[1].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            for (String pair : valueSplit) { // Key-value pairs
                String[] pairSplit = pair.split("=", 2);
                switch (pairSplit[0]) {
                    case "ID":
                        id = pairSplit[1];
                        break;
                    case "Number":
                        number = pairSplit[1];
                        break;
                    case "Type":
                        type = pairSplit[1];
                        break;
                    case "Description":
                        description = pairSplit[1];
                        break;
                    default:
//                        metadata.addInfo(pairSplit[0], pairSplit[1]);
                }
            }
        } else {
            // Simpler entry like "assembly=GRCh37"
            split = line.split("=", 2);
            // Remove leading ## and trailing equals symbol
            key = split[0].substring(2);
            id = split[0].substring(2);
            if (split.length > 1) {
                value = split[1];
            }
        }

        return factory.newVariantSetMetadata(key, value, id, type, number, description, info);
    }

}
