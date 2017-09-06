package org.opencb.biodata.models.variant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opencb.biodata.models.metadata.SampleSetType;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantSetStats;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;

import java.util.*;

/**
 * Created on 09/08/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
@JsonIgnoreProperties({"impl", "samplesPosition"})
public class VariantFileMetadata {

    private final org.opencb.biodata.models.variant.metadata.VariantFileMetadata impl;
    private volatile LinkedHashMap<String, Integer> samplesPosition;

    VariantFileMetadata() {
        impl = new org.opencb.biodata.models.variant.metadata.VariantFileMetadata();
        samplesPosition = null;
    }

    public VariantFileMetadata(org.opencb.biodata.models.variant.metadata.VariantFileMetadata variantFileMetadata) {
        impl = variantFileMetadata;
        samplesPosition = null;
    }

    public VariantFileMetadata(String id, String alias) {
        this(id, alias, null, null, null);
    }

    public VariantFileMetadata(String id, String alias, List<String> sampleIds, VariantSetStats stats, VariantFileHeader header) {
        impl = new org.opencb.biodata.models.variant.metadata.VariantFileMetadata(id, alias, sampleIds, stats, header, new HashMap<>());
        samplesPosition = null;
    }

    public VariantStudyMetadata toVariantDatasetMetadata(String studyId) {
        return VariantStudyMetadata.newBuilder()
                .setId(studyId)
                .setFiles(Collections.singletonList(getImpl()))
                .setSampleSetType(SampleSetType.UNKNOWN)
                .build();
    }

    public Map<String, Integer> getSamplesPosition() {
        if (samplesPosition == null) {
            updateSamplesPosition();
        }
        return Collections.unmodifiableMap(samplesPosition);
    }

    public void setSamplesPosition(Map<String, Integer> samplesPosition) {
        if (samplesPosition == null) {
            setSampleIds(null);
        } else {
            ArrayList<String> samples = new ArrayList<>(samplesPosition.size());
            for (int i = 0; i < samplesPosition.size(); i++) {
                samples.add(null);    //Populate empty array
            }
            for (Map.Entry<String, Integer> entry : samplesPosition.entrySet()) {
                samples.set(entry.getValue(), entry.getKey());
            }
            setSampleIds(samples);
        }
        updateSamplesPosition();
    }

    private synchronized void updateSamplesPosition() {
        if (samplesPosition == null) {
            List<String> samples = getSampleIds();
            if (samples == null) {
                samplesPosition = null;
            } else {
                LinkedHashMap<String, Integer> newSamplesPosition = getSamplesPositionMap(samples);
                samplesPosition = newSamplesPosition;
            }
        }
    }

    public static LinkedHashMap<String, Integer> getSamplesPositionMap(List<String> samples) {
        LinkedHashMap<String, Integer> newSamplesPosition = new LinkedHashMap<>(samples.size());
        int idx = 0;
        for (String sample : samples) {
            newSamplesPosition.put(sample, idx++);
        }
        return newSamplesPosition;
    }

    public String getId() {
        return impl.getId();
    }

    public VariantFileMetadata setId(String id) {
        impl.setId(id);
        return this;
    }

    public String getPath() {
        return impl.getPath();
    }

    public VariantFileMetadata setPath(String path) {
        impl.setPath(path);
        return this;
    }

    @Deprecated
    public String getAlias() {
        return getPath();
    }

    @Deprecated
    public VariantFileMetadata setAlias(String alias) {
        setPath(alias);
        return this;
    }

    public List<String> getSampleIds() {
        return impl.getSampleIds() == null ? null : Collections.unmodifiableList(impl.getSampleIds());
    }

    public VariantFileMetadata setSampleIds(List<String> sampleIds) {
        impl.setSampleIds(sampleIds);
        samplesPosition = null;
        return this;
    }

    public VariantFileMetadata addSampleIds(List<String> newSampleIds) {
        impl.getSampleIds().addAll(newSampleIds);
        samplesPosition = null;
        return this;
    }

    public VariantGlobalStats getStats() {
        return impl.getStats() == null ? null : new VariantGlobalStats(impl.getStats());
    }

    public void setStats(VariantGlobalStats stats) {
        impl.setStats(stats == null ? null : stats.getImpl());
    }

    public VariantFileHeader getHeader() {
        return impl.getHeader();
    }

    public VariantFileMetadata setHeader(VariantFileHeader header) {
        impl.setHeader(header);
        return this;
    }

    public Map<String, String> getAttributes() {
        return impl.getAttributes();
    }

    public VariantFileMetadata setAttributes(Map<String, String> attributes) {
        impl.setAttributes(attributes);
        return this;
    }


    public org.opencb.biodata.models.variant.metadata.VariantFileMetadata getImpl() {
        return impl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariantFileMetadata)) return false;

        VariantFileMetadata that = (VariantFileMetadata) o;

        return !(impl != null ? !impl.equals(that.impl) : that.impl != null);

    }

    @Override
    public int hashCode() {
        return impl != null ? impl.hashCode() : 0;
    }

}