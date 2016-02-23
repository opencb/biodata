package org.opencb.biodata.tools.variant.converter;

import com.google.protobuf.ProtocolStringList;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created on 17/02/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantToVcfSliceConverter implements Converter<List<Variant>, VcfSliceProtos.VcfSlice> {

    private final VariantToProtoVcfRecord converter;

    public VariantToVcfSliceConverter() {
        converter = new VariantToProtoVcfRecord();
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
        variants.sort((v1, v2) -> Integer.compare(v1.getStart(), v2.getStart()));

        VcfSliceProtos.VcfSlice.Builder builder = VcfSliceProtos.VcfSlice.newBuilder();

        VcfSliceProtos.Fields fields = buildDefaultFields(variants, VcfSliceProtos.Fields.newBuilder()).build();

        String chromosome = variants.isEmpty() ? "" : variants.get(0).getChromosome();

        converter.updateMeta(fields);
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
    static VcfSliceProtos.Fields.Builder buildDefaultFields(List<Variant> variants, VcfSliceProtos.Fields.Builder fieldsBuilder) {
        if (fieldsBuilder == null) {
            fieldsBuilder = VcfSliceProtos.Fields.newBuilder();
        }

        Map<String, Integer> filters = new HashMap<>();
        Map<String, Integer> keys = new HashMap<>();
        Map<String, Integer> formats = new HashMap<>();
        Map<List<String>, Integer> keySets = new HashMap<>();
        Map<String, Integer> gts = new HashMap<>();

        for (Variant variant : variants) {
            for (StudyEntry studyEntry : variant.getStudies()) {

                String formatAsString = studyEntry.getFormatAsString();
                formats.put(formatAsString, formats.getOrDefault(formatAsString, 0) + 1);

                for (FileEntry fileEntry : studyEntry.getFiles()) {
                    Map<String, String> attributes = fileEntry.getAttributes();
                    List<String> keySet = attributes.keySet().stream().sorted().collect(Collectors.toList());
                    keySets.put(keySet, keySets.getOrDefault(keySet, 0) + 1);

                    for (Map.Entry<String, String> entry : attributes.entrySet()) {
                        String key = entry.getKey();
                        switch (key) {
                            case VariantVcfFactory.FILTER:
                                String filter = entry.getValue();
                                if (filter != null) {
                                    filters.put(filter, filters.getOrDefault(filter, 0) + 1);
                                }
                                break;
                            case VariantVcfFactory.QUAL:
                            case VariantVcfFactory.SRC:
                            case "END":
                                // Ignore
                                break;
                            default:
                                keys.put(key, keys.getOrDefault(key, 0) + 1);
                                break;
                        }
                    }
                }

                Integer gtPosition = studyEntry.getFormatPositions().get("GT");
                if (gtPosition != null) {
                    for (List<String> sampleData : studyEntry.getSamplesData()) {
                        String gt = sampleData.get(gtPosition);
                        gts.put(gt, gts.getOrDefault(gt, 0) + 1);
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

        return fieldsBuilder;
    }

    private static void addDefaultValues(Map<String, Integer> map, Consumer<String> consumer) {
        map.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Reverse order!!
                .forEach(e -> consumer.accept(e.getKey()));
    }
}
