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

package org.opencb.biodata.tools.variant.converter;

import com.google.protobuf.ProtocolStringList;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.AlternateCoordinate;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord.Builder;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfSample;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 */
public class VariantToProtoVcfRecord implements Converter<Variant, VcfRecord> {

    private VcfSliceProtos.Fields fields;

    private final Map<String, Integer> formatIndexMap = new HashMap<>();
    private final Map<String, Integer> filterIndexMap = new HashMap<>();
    private final Map<String, Integer> infoKeyIndexMap = new HashMap<>();
    private final Map<String, Integer> gtIndexMap = new HashMap<>();

    private static final Set<String> IGNORED_KEYS = new HashSet<>();

    static {
        IGNORED_KEYS.add(VariantVcfFactory.FILTER); // Save FILTER on VcfRecord specific field
        IGNORED_KEYS.add(VariantVcfFactory.QUAL);   // Save QUAL on VcfRecord specific field
        IGNORED_KEYS.add("END");                    // Save END on VcfRecord specific field
        IGNORED_KEYS.add(VariantVcfFactory.SRC);    // Never save SRC
    }

    /**
     *
     */
    public VariantToProtoVcfRecord() {
        // do nothing
    }

    private void init(VcfSliceProtos.Fields fields) {

        this.fields = fields;

        listToMap(fields.getInfoKeysList(), this.infoKeyIndexMap);
        listToMap(fields.getFiltersList(), this.filterIndexMap);
        listToMap(fields.getFormatsList(), this.formatIndexMap);
        listToMap(fields.getGtsList(), this.gtIndexMap);

    }

    private Map<String, Integer> listToMap(ProtocolStringList list, Map<String, Integer> map) {
        map.clear();
        int i = 0;
        for (String key : list) {
            map.put(key, i++);
        }
        return map;
    }

    @Override
    public VcfRecord convert(Variant variant) {
        return convertUsingSlicePosition(variant, 0);
    }

    public VcfRecord convert(Variant variant, int chunkSize) {
        return convertUsingSlicePosition(variant, chunkSize > 0 ? getSlicePosition(variant.getStart(), chunkSize) : 0);
    }

    public VcfRecord convertUsingSlicePosition(Variant variant, int slicePosition) {
        int relativeStart = variant.getStart() - slicePosition;
        Builder recordBuilder = VcfRecord.newBuilder()
                // Warning: start and end can be at different chunks.
                // Do not use getSliceOffset independently
                .setRelativeStart(relativeStart)
                .setReference(variant.getReference())
                .setAlternate(variant.getAlternate())
                .addAllIdNonDefault(encodeIds(variant.getIds()));

        recordBuilder.setRelativeEnd(getRelativeEnd(variant.getStart(), variant.getEnd(), slicePosition));

		/* Get Study (one only expected  */
        List<StudyEntry> studies = variant.getStudies();

        if (null == studies || studies.size() == 0) {
            throw new UnsupportedOperationException(String.format("No Study found for variant: %s", variant));
        }
        if (studies.size() > 1) {
            throw new UnsupportedOperationException(String.format("Only one Study supported - found %s studies instead!!!", studies.size()));
        }

        StudyEntry study = studies.get(0);

        if (study.getFiles() == null || study.getFiles().isEmpty()) {
            throw new UnsupportedOperationException(String.format("No File found for variant: %s", variant));
        }
        if (study.getFiles().size() > 1) {
            throw new UnsupportedOperationException(String.format("Only one File supported - found %s studies instead!!!", study.getFiles().size()));
        }

        List<AlternateCoordinate> secondaryAlternates = study.getSecondaryAlternates();
        if (secondaryAlternates != null) {
            for (AlternateCoordinate alternate : secondaryAlternates) {
                recordBuilder.addSecondaryAlternates(VariantProto.AlternateCoordinate
                        .newBuilder()
                        .setChromosome(alternate.getChromosome() != null? alternate.getChromosome() : "")
                        .setStart(alternate.getStart() != null? alternate.getStart() : 0)
                        .setEnd(alternate.getEnd() != null? alternate.getEnd() : 0)
                        .setReference(alternate.getReference() != null? alternate.getReference() : "")
                        .setAlternate(alternate.getAlternate() != null? alternate.getAlternate() : "")
                        .setType(getProtoVariantType(alternate.getType()))
                        .build());
            }
        }

        FileEntry file = study.getFiles().get(0);
        Map<String, String> attr = Collections.unmodifiableMap(file.getAttributes());   //DO NOT MODIFY

        if ( !variant.getType().equals(VariantType.NO_VARIATION)
                && file.getCall() != null && !file.getCall().isEmpty()
                && !file.getCall().equals(variant.toString() + ":0" ) ) {
            recordBuilder.setCall(file.getCall());
        }

		/* Filter */
        int filter = encodeFilter(attr.get(VariantVcfFactory.FILTER));
        recordBuilder.setFilterIndex(filter);

		/* QUAL */
        recordBuilder.setQuality(encodeQuality(attr.get(VariantVcfFactory.QUAL)));

		/* INFO */
        setInfoKeyValues(recordBuilder, attr);

		/* FORMAT */
        setFormat(recordBuilder, study);

        recordBuilder.addAllSamples(encodeSamples(study.getFormatPositions(), study.getSamplesData()));

        /* TYPE */
        recordBuilder.setType(getProtoVariantType(variant.getType()));

        // TODO check all worked
        return recordBuilder.build();
    }

    private int getRelativeEnd(Integer start, Integer end, int slicePosition) {
        int relativeEnd = 0;
        //Set end only if is different to the start position
        if (!end.equals(start)) {
            relativeEnd = end - slicePosition;
            // If relativeEnd is 0, relativeStart is used instead.
            // So, real 0 values must be used saved in a different way.
            // Subtract one to all the relativeEnd lessOrEqual to 0.
            if (relativeEnd <= 0) {
                relativeEnd--;
            }
        } // else { relativeEnd = 0 }
        return relativeEnd;
    }

    private void setInfoKeyValues(Builder recordBuilder, Map<String, String> attr) {
        List<Integer> infoKeys = encodeInfoKeys(attr.keySet());
        List<String> infoValues = encodeInfoValues(attr, infoKeys);

        if (!isDefaultInfoKeys(infoKeys)) {
            recordBuilder.addAllInfoKeyIndex(infoKeys);
        } else {
            recordBuilder.addAllInfoKeyIndex(Collections.emptyList());
        }
        recordBuilder.addAllInfoValue(infoValues);
    }

    private void setFormat(Builder recordBuilder, StudyEntry study) {
        List<String> formatLst = study.getFormat();
        String format = String.join(":", formatLst);
        Integer formatIndex = formatIndexMap.get(format);
        if (formatIndex == null || formatIndex < 0) {
            throw new IllegalArgumentException("Unknown format " + format);
        }
        recordBuilder.setFormatIndex(formatIndex);
    }

    public void updateMeta(VcfSliceProtos.Fields fields) {
        this.init(fields);
    }

    /**
     * Calculate Slice given a position and a chunk size &gt; 0; if chunk size &lt;= 0, returns position
     *
     * @param position  genomic position
     * @param chunkSize chunk size
     * @return slice calculated using position and chunk size
     */
    public static int getSlicePosition(int position, int chunkSize) {
        return chunkSize > 0
                ? position - (position % chunkSize)
                : position;
    }

    /**
     * Calculate offset to a slice junction given a position and a chunk size &gt; 0; if chunk size &lt;= 0, return position
     *
     * @param position  genomic position
     * @param chunkSize chunk size
     * @return offset calculated to the slice start position
     */
    public static int getSliceOffset(int position, int chunkSize) {
        return chunkSize > 0
                ? position % chunkSize
                : position;
    }


    private Iterable<String> encodeIds(List<String> ids) {
        // TODO check if "." are removed!!!
        return ids == null ? Collections.emptyList() : ids.stream().map(String::toString).collect(Collectors.toList());
    }

    /**
     * Encodes the {@link String} value of the Quality into a float.
     *
     * See {@link VcfRecordToVariantConverter#getQuality(float)}
     * Increments one to the quality value. 0 means missing or unknown.
     *
     * @param value String quality value
     * @return Quality
     */
    static float encodeQuality(String value) {
        final float qual;
        if (StringUtils.isNotEmpty(value) && !value.equals(".")) {
            qual = Float.parseFloat(value);
        } else {
            qual = -1;
        }
        return qual + 1;
    }

    private int encodeFilter(String filter) {
        Integer index;
        if (null != filter) {
            index = filterIndexMap.get(filter);
        } else {
            index = filterIndexMap.get(".");
        }
        if (index == null || index < 0) {
            throw new IllegalArgumentException("Unknown filter " + filter);
        }
        return index;
    }


    private List<String> encodeInfoValues(Map<String, String> attr, List<Integer> infoKeys) {
//		infoKeys.stream().map(x -> attr.get(x)).collect(Collectors.toList()); // not sure if order is protected
        List<String> values = new ArrayList<>(infoKeys.size());
        for (Integer key : infoKeys) {
            String value = attr.get(fields.getInfoKeysList().get(key));
            //Null values are not accepted in proto
            values.add(value == null ? "" : value);
        }
        return values;
    }

    /**
     * Creates a sorted list of strings from the INFO KEYs
     *
     * @return {@link List}
     * @param keys
     */
    private List<Integer> encodeInfoKeys(Collection<String> keys) {
        // sorted key list
        List<Integer> idxKeys = new ArrayList<>(keys.size());
        for (String key : keys) {
            if (!IGNORED_KEYS.contains(key)) {
                Integer idx = infoKeyIndexMap.get(key);
                if (idx != null) {
                    idxKeys.add(idx);
                } else {
                    throw new IllegalArgumentException("Unknown key value " + key);
                }
            }
        }
        idxKeys.sort(Integer::compareTo);
        return idxKeys;
    }

    private boolean isDefaultInfoKeys(List<Integer> keyList) {
        return fields.getDefaultInfoKeysList().equals(keyList);
    }

    public boolean isDefaultFormat(List<String> format) {
        return isDefaultFormat(String.join(":", format));
    }

    public boolean isDefaultFormat(String format) {
        return fields.getFormats(0).equals(format);
    }

    public List<VcfSample> encodeSamples(Map<String, Integer> formatPositions, List<List<String>> samplesData) {
        List<VcfSample> ret = new ArrayList<>(samplesData.size());
        Integer gtPosition = formatPositions.get("GT");
        // samplesData should have fields in the same order than formatLst
        if (gtPosition == null) {
            for (List<String> sampleData : samplesData) {
                ret.add(VcfSample.newBuilder().addAllSampleValues(sampleData).build());
            }
        } else {
            if (gtPosition != 0) {
                throw new IllegalArgumentException("GT must be in the first position or missing");
            }
            for (List<String> sampleData : samplesData) {
                String gt = sampleData.get(gtPosition);
                int gtIndex = gtIndexMap.get(gt);
                ret.add(VcfSample.newBuilder().setGtIndex(gtIndex)
                        .addAllSampleValues(sampleData.subList(1, sampleData.size())).build());
            }
        }

        return ret;
    }


    public static VariantProto.VariantType getProtoVariantType(VariantType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case SNV: return VariantProto.VariantType.SNV;
            case SNP: return VariantProto.VariantType.SNP;
            case MNV: return VariantProto.VariantType.MNV;
            case MNP: return VariantProto.VariantType.MNP;
            case INDEL: return VariantProto.VariantType.INDEL;
            case SV: return VariantProto.VariantType.SV;
            case INSERTION: return VariantProto.VariantType.INSERTION;
            case DELETION: return VariantProto.VariantType.DELETION;
            case TRANSLOCATION: return VariantProto.VariantType.TRANSLOCATION;
            case INVERSION: return VariantProto.VariantType.INVERSION;
            case CNV: return VariantProto.VariantType.CNV;
            case NO_VARIATION: return VariantProto.VariantType.NO_VARIATION;
            case SYMBOLIC: return VariantProto.VariantType.SYMBOLIC;
            case MIXED: return VariantProto.VariantType.MIXED;
            default: return VariantProto.VariantType.valueOf(type.name());
        }
    }

}
