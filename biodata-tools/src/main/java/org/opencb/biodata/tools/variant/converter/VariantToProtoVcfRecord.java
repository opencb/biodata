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
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfRecord.Builder;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfSample;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 */
public class VariantToProtoVcfRecord implements Converter<Variant, VcfRecord> {

    //	private static final char STRING_JOIN_SEP = '~';
//	public static final String ILLUMINA_GVCF_BLOCK_END = "END";


    private final AtomicReference<VcfMeta> meta = new AtomicReference<VcfMeta>();


    private final Map<String, Integer> sample_to_index = new HashMap<String, Integer>();
    private final List<String> samples = new ArrayList<String>();
    private final AtomicReference<String> defaultFilterKeys = new AtomicReference<String>();
    //	private final AtomicReference<String> defaultInfoKeys = new AtomicReference<String>();
    private final List<String> defaultInfoKeys = new ArrayList<String>();
    private final List<String> defaultFormatKeys = new ArrayList<String>();
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
        int sampleSize = meta.getSamplesCount();
        sample_to_index.clear();
        samples.clear();
        ArrayList<String> lst = new ArrayList<String>(sampleSize);

        // add values
        for (int i = 0; i < sampleSize; ++i) {
            String sample = meta.getSamples(i);
            if (sample_to_index.containsKey(sample)) {
                throw new IllegalStateException(String.format("Duplicated sample '%s' found!!!", sample));
            }
            sample_to_index.put(sample, i);
            lst.add(sample);
        }

        ArrayList<String> infoKeys = new ArrayList<String>(meta.getInfoDefaultList());
        Collections.sort(infoKeys); // INFO keys only to be sorted

        samples.addAll(lst);
        defaultFilterKeys.set(meta.getFilterDefault());

//		defaultInfoKeys.set(StringUtils.join(infoKeys, STRING_JOIN_SEP));
        defaultInfoKeys.clear();
        defaultInfoKeys.addAll(infoKeys);

        defaultFormatKeys.clear();
        defaultFormatKeys.addAll(meta.getFormatDefaultList());

        setVcfMeta(meta);
    }

    @Override
    public VcfRecord convert(Variant variant) {
        return convert(variant, -1);
    }

    public VcfRecord convert(Variant variant, int chunkSize) {
        Builder recordBuilder = VcfRecord.newBuilder()
                .setRelativeStart(getSliceOffset(variant.getStart(), chunkSize))
                .setRelativeEnd(getSliceOffset(variant.getEnd(), chunkSize))
                .setReference(variant.getReference())
                .setAlternate(variant.getAlternate())
                .addAllIdNonDefault(decodeIds(variant.getIds()));

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

        // TODO check all worked
        return recordBuilder.build();
    }


    public void updateVcfMeta(VcfMeta meta) {
        this.init(meta);
    }

    /**
     * Calculate Slice given a position and a chunk size &gt; 0; if chunk size &lt;= 0, returns position
     *
     * @param position  genomic position
     * @param chunkSize chunk size
     * @return slice calculated using position and chunk size
     */
    public long getSlicePosition(long position, int chunkSize) {
        return chunkSize > 0
                ? position / (long) chunkSize
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

    /**
     * Creates a List of strings in the original order from the FORMAT string provided; Split the string by ":"
     *
     * @param format Format string with the keys separated by ":"
     * @return {@link List}
     */
    public List<String> decodeFormat(String format) {
        return Arrays.asList(format.split(":"));
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

}
