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

package org.opencb.biodata.tools.variant.converters.proto;

import com.google.protobuf.ProtocolStringList;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;
import org.opencb.biodata.tools.Converter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created on 17/02/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantToVcfSliceConverter implements Converter<List<Variant>, VcfSliceProtos.VcfSlice> {

    private final Set<String> attributeFields;
    private final Set<String> formatFields;

    public VariantToVcfSliceConverter() {
        this(null, null);
    }

    public VariantToVcfSliceConverter(Set<String> attributeFields, Set<String> formatFields) {
        this.attributeFields = attributeFields;
        this.formatFields = formatFields;
    }

    public VcfSliceProtos.VcfSlice convert(Variant variant) {
        return convert(Collections.singletonList(variant));
    }

    @Override
    public VcfSliceProtos.VcfSlice convert(List<Variant> variants) {
        return convert(variants, variants.isEmpty() ? 0 : variants.get(0).getStart());
    }

    public VcfSliceProtos.VcfSlice convert(List<Variant> variants, int slicePosition) {
        //Sort variants
        variants.sort(Comparator.comparingInt(Variant::getStart));

        VcfSliceProtos.VcfSlice.Builder builder = VcfSliceProtos.VcfSlice.newBuilder();

        VcfSliceProtos.Fields fields = buildDefaultFields(variants, attributeFields, formatFields);

        String chromosome = variants.isEmpty() ? "" : variants.get(0).getChromosome();

        VariantToProtoVcfRecord converter = new VariantToProtoVcfRecord(fields, attributeFields, formatFields);

        List<VcfSliceProtos.VcfRecord> vcfRecords = new ArrayList<>(variants.size());
        for (Variant variant : variants) {
            vcfRecords.add(converter.convertUsingSlicePosition(variant, slicePosition));
        }


        builder.setChromosome(chromosome)
                .setPosition(slicePosition)
                .setFields(fields)
                .addAllRecords(vcfRecords);

        return builder.build();
    }

    //With test visibility
    static VcfSliceProtos.Fields buildDefaultFields(List<Variant> variants) {
        return buildDefaultFields(variants, null, null);
    }

    static VcfSliceProtos.Fields buildDefaultFields(List<Variant> variants,
                                                            Set<String> attributeFields, Set<String> formatFields) {
        VcfSliceProtos.Fields.Builder fieldsBuilder = VcfSliceProtos.Fields.newBuilder();

        boolean includeAllAttributes = attributeFields == null;
        boolean includeNoneAttributes = attributeFields != null && attributeFields.isEmpty();
        boolean includeAllFormats = formatFields == null;
        boolean includeNoneFormats = formatFields != null && formatFields.isEmpty();


        Map<String, Integer> filters = new HashMap<>();
        Map<String, Integer> keys = new HashMap<>();
        Map<String, Integer> formats = new HashMap<>();
        Map<List<String>, Integer> keySets = new HashMap<>();
        Map<String, Integer> gts = new HashMap<>();

        for (Variant variant : variants) {
            for (StudyEntry studyEntry : variant.getStudies()) {

                if (!includeNoneFormats) {
                    String formatAsString;
                    if (includeAllFormats) {
                        formatAsString = studyEntry.getFormatAsString();
                    } else {
                        formatAsString = studyEntry.getFormat()
                                .stream()
                                .filter(formatFields::contains)
                                .collect(Collectors.joining(":"));
                    }
                    formats.merge(formatAsString, 1, Integer::sum);
                }

                if (!includeNoneAttributes) {
                    for (FileEntry fileEntry : studyEntry.getFiles()) {
                        Map<String, String> attributes = fileEntry.getAttributes();
                        List<String> keySet = attributes.keySet().stream().sorted().collect(Collectors.toList());
                        keySets.merge(keySet, 1, Integer::sum);

                        for (Map.Entry<String, String> entry : attributes.entrySet()) {
                            String key = entry.getKey();
                            if (includeAllAttributes || attributeFields.contains(key)) {
                                switch (key) {
                                    case StudyEntry.FILTER:
                                        String filter = entry.getValue();
                                        if (filter != null) {
                                            filters.merge(filter, 1, Integer::sum);
                                        }
                                        break;
                                    case StudyEntry.QUAL:
                                    case StudyEntry.SRC:
                                    case "END":
                                        // Ignore
                                        break;
                                    default:
                                        keys.merge(key, 1, Integer::sum);
                                        break;
                                }
                            }
                        }
                    }
                }

                Integer gtPosition = studyEntry.getFormatPositions().get("GT");
                if (gtPosition != null) {
                    for (List<String> sampleData : studyEntry.getSamplesData()) {
                        String gt = sampleData.get(gtPosition);
//                        gts.put(gt, gts.getOrDefault(gt, 0) + 1);
                        gts.merge(gt, 1, Integer::sum);
                    }
                }

            }
        }

        addDefaultValues(filters, fieldsBuilder::addFilters);
        addDefaultValues(keys, fieldsBuilder::addInfoKeys);
        addDefaultValues(formats, fieldsBuilder::addFormats);
        addDefaultValues(gts, fieldsBuilder::addGts);

        //Determine most common infoKeys list
        List<String> keySet = null;
        int max = 0;
        for (Map.Entry<List<String>, Integer> entry : keySets.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                keySet = entry.getKey();
            }
        }
        //If any, translate into IDs, sort and add to the builder.
        if (keySet != null) {
            ProtocolStringList keysList = fieldsBuilder.getInfoKeysList();
            List<Integer> defaultKeySet = new ArrayList<>(keySet.size());
            for (String key : keySet) {
                int indexOf = keysList.indexOf(key);
                if (indexOf >= 0) {
                    defaultKeySet.add(indexOf);
                }
            }
            defaultKeySet.sort(Integer::compareTo);
            fieldsBuilder.addAllDefaultInfoKeys(defaultKeySet);
        }

        return fieldsBuilder.build();
    }

    private static void addDefaultValues(Map<String, Integer> map, Consumer<String> consumer) {
        map.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Reverse order!!
                .forEach(e -> consumer.accept(e.getKey()));
    }
}
