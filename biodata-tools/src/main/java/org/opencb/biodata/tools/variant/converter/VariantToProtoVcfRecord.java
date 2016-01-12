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

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.models.variant.protobuf.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord.Builder;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfSample;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 */
public class VariantToProtoVcfRecord implements Converter<Variant, VcfRecord> {

    //	private static final char STRING_JOIN_SEP = '~';
//	public static final String ILLUMINA_GVCF_BLOCK_END = "END";


    private final AtomicReference<VcfMeta> meta = new AtomicReference<>();


    private Map<String, Integer> samplesPosition = new HashMap<>();
    private List<String> samples = new ArrayList<>();
    private final AtomicReference<String> defaultFilterKeys = new AtomicReference<>();
    //	private final AtomicReference<String> defaultInfoKeys = new AtomicReference<String>();
    private final List<String> defaultInfoKeys = new ArrayList<>();
    private final List<String> defaultFormatKeys = new ArrayList<>();
    private final Set<String> ignoredKeys = new HashSet<>();

    /**
     *
     */
    public VariantToProtoVcfRecord() {
        // do nothing
        ignoredKeys.add(VariantVcfFactory.FILTER);
        ignoredKeys.add(VariantVcfFactory.QUAL);
        ignoredKeys.add(VariantVcfFactory.SRC);
    }

    private void init(VcfMeta meta) {

        // add values
        samplesPosition = meta.getVariantSource().getSamplesPosition();
        samples = meta.getVariantSource().getSamples();

        ArrayList<String> infoKeys = new ArrayList<String>(meta.getInfoDefault());
        Collections.sort(infoKeys); // INFO keys only to be sorted

        defaultFilterKeys.set(meta.getFilterDefault());

//		defaultInfoKeys.set(StringUtils.join(infoKeys, STRING_JOIN_SEP));
        defaultInfoKeys.clear();
        defaultInfoKeys.addAll(infoKeys);

        defaultFormatKeys.clear();
        defaultFormatKeys.addAll(meta.getFormatDefault());

        setVcfMeta(meta);
    }

    @Override
    public VcfRecord convert(Variant variant) {
        return convertUsingSliceposition(variant, 0);
    }
    public VcfRecord convert(Variant variant, int chunkSize) {
        return convertUsingSliceposition(variant, chunkSize > 0 ? getSlicePosition(variant.getStart(), chunkSize) : 0);
    }

    public VcfRecord convertUsingSliceposition(Variant variant, int slicePosition) {
        Builder recordBuilder = VcfRecord.newBuilder()
                // Warning: start and end can be at different chunks.
                // Do not use getSliceOffset independently
                .setRelativeStart(variant.getStart() - slicePosition)
                .setReference(variant.getReference())
                .addAlternate(variant.getAlternate())
                .addAllIdNonDefault(decodeIds(variant.getIds()));

        //Set end only if is different to the start position
        if (!Objects.equals(variant.getEnd(), variant.getStart())) {
            recordBuilder.setRelativeEnd(variant.getEnd() - slicePosition);
        }

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
        FileEntry file = study.getFiles().get(0);
        Map<String, String> attr = Collections.unmodifiableMap(file.getAttributes());   //DO NOT MODIFY

        recordBuilder.setCall(file.getCall());

		/* Filter */
        String filter = decodeFilter(attr.get(VariantVcfFactory.FILTER));
        if (filter != null) {
            recordBuilder.setFilterNonDefault(filter);
        }

		/* QUAL */
        recordBuilder.setQuality(decodeQual(attr.get(VariantVcfFactory.QUAL)));

		/* INFO */
        List<String> infoKeys = decodeInfoKeys(attr);
        boolean isInfoDefault = isDefaultInfoKeys(infoKeys);
        List<String> infoValues = decodeInfoValues(attr, infoKeys);
        if (!isInfoDefault) {
            recordBuilder.addAllInfoKey(infoKeys);
        } else {
            recordBuilder.addAllInfoKey(Arrays.asList(new String[]{}));
        }
        recordBuilder.addAllInfoValue(infoValues);

		/* FORMAT */
        List<String> formatLst = study.getFormat();
        if (!isDefaultFormat(formatLst)) {
            recordBuilder.addAllSampleFormatNonDefault(formatLst); // maybe empty if default
        }
        recordBuilder.addAllSamples(decodeSamples(study.getSamplesData()));

        /* TYPE */
        recordBuilder.setType(getProtoVariantType(variant.getType()));

        // TODO check all worked
        return recordBuilder.build();
    }


    public void updateVcfMeta(VcfMeta meta) {
        this.init(meta);
    }

    public void updateVcfMeta(VariantSource source) {
        this.init(new VcfMeta(source));
    }

    /**
     * Calculate Slice given a position and a chunk size &gt; 0; if chunk size &lt;= 0, returns position
     *
     * @param position  genomic position
     * @param chunkSize chunk size
     * @return slice calculated using position and chunk size
     */
    public int getSlicePosition(int position, int chunkSize) {
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
    public int getSliceOffset(int position, int chunkSize) {
        return chunkSize > 0
                ? position % chunkSize
                : position;
    }


    private Iterable<String> decodeIds(List<String> ids) {
        // TODO check if "." are removed!!!
        return ids.stream().map(x -> x.toString()).collect(Collectors.toList());
    }

    private String decodeQual(String value) {
        if (null != value) {
            return value.toString();
        }
        return StringUtils.EMPTY;
    }

    private String decodeFilter(String filter) {
        if (null != filter) {
            if (filter.equals(meta.get().getFilterDefault())) {
                return null;
            } else {
                return filter;
            }
        }
        return StringUtils.EMPTY;
    }


    private List<String> decodeInfoValues(Map<String, String> attr, List<String> infoKeys) {
//		infoKeys.stream().map(x -> attr.get(x)).collect(Collectors.toList()); // not sure if order is protected
        List<String> values = new ArrayList<>(infoKeys.size());
        for (String key : infoKeys) {
            values.add(attr.get(key));
        }
        return values;
    }

    /**
     * Creates a sorted list of strings from the INFO KEYs
     *
     * @param attr {@link Map} of key-value info pairs
     * @return {@link List}
     */
    private List<String> decodeInfoKeys(Map<String, String> attr) {
        // sorted key list
        List<String> keyList = attr.keySet().stream()
                .map(String::toString)
                .filter(s -> !ignoredKeys.contains(s))
                .sorted().collect(Collectors.toList());
        return keyList;
    }

    private boolean isDefaultInfoKeys(List<String> keyList) {
        return this.getDefaultInfoKeys().equals(keyList);
//		String str = StringUtils.join(keyList,STRING_JOIN_SEP);
//		return StringUtils.equals(str, getDefaultInfoKeys());
    }

    public boolean isDefaultFormat(List<String> keyList) {
        return getDefaultFormatKeys().equals(keyList);
    }

    public List<VcfSample> decodeSamples(List<List<String>> samplesData) {
        List<VcfSample> ret = new ArrayList<>(samplesData.size());
        for (List<String> sampleData : samplesData) {
            // samplesData should have fields in the same order than formatLst
            ret.add(VcfSample.newBuilder().addAllSampleValues(sampleData).build());
        }

        return ret;
    }

    public VcfSample decodeSample(List<String> formatLst, Map<String, String> data) {
        List<String> values = new ArrayList<>(formatLst.size());
        for (String f : formatLst) {
            values.add(data.getOrDefault(f, StringUtils.EMPTY).toString());
        }
        return VcfSample.newBuilder().addAllSampleValues(values).build();
    }

    private void setVcfMeta(VcfMeta meta) {
        this.meta.set(meta);
    }

    public VcfMeta getVcfMeta() {
        return meta.get();
    }

    public List<String> getSamples() {
        return samples;
    }

    public void setSamples(List<String> samples) {
        this.samples.clear();
        this.samples.addAll(samples);
    }

    public String getDefaultFilterKeys() {
        return defaultFilterKeys.get();
    }

    public void setDefaultFilterKeys(String value) {
        defaultFilterKeys.set(value);
    }

    public List<String> getDefaultInfoKeys() {
        return defaultInfoKeys;
    }

    public void setDefaultInfoKeys(List<String> keys) {
        defaultInfoKeys.clear();
        defaultInfoKeys.addAll(keys);
    }

    public List<String> getDefaultFormatKeys() {
        return defaultFormatKeys;
    }

    public void setDefaultFormatKeys(List<String> keys) {
        defaultFormatKeys.clear();
        defaultFormatKeys.addAll(keys);
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
