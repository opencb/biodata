package org.opencb.biodata.tools.variant.normalizer.extensions;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;

import java.util.Objects;
import java.util.function.Function;

public class VariantNormalizerExtensionFileToSample extends VariantNormalizerExtension {

    private final String fileDataKey;
    private final String sampleDataKey;
    private VariantFileHeaderComplexLine newSampleMetadataLine;
    private final Function<String, String> fieldMapper;


    public VariantNormalizerExtensionFileToSample(String fileDataKey) {
        this(fileDataKey, null);
    }

    public VariantNormalizerExtensionFileToSample(String fileDataKey, Function<String, String> fieldMapper) {
        this.fileDataKey = fileDataKey;
        this.sampleDataKey = fileDataKey;
        newSampleMetadataLine = null;
        this.fieldMapper = fieldMapper == null ? Function.identity() : fieldMapper;
    }

    public VariantNormalizerExtensionFileToSample(String fileDataKey, String sampleDataKey,
                                                  VariantFileHeaderComplexLine newSampleMetadataLine) {
        this(fileDataKey, sampleDataKey, newSampleMetadataLine, null);
    }

    public VariantNormalizerExtensionFileToSample(String fileDataKey, String sampleDataKey,
                                                  VariantFileHeaderComplexLine newSampleMetadataLine,
                                                  Function<String, String> fieldMapper) {
        this.fileDataKey = fileDataKey;
        this.sampleDataKey = sampleDataKey;
        this.newSampleMetadataLine = Objects.requireNonNull(newSampleMetadataLine);
        this.fieldMapper = fieldMapper == null ? Function.identity() : fieldMapper;
    }

    @Override
    protected boolean canUseExtension(VariantFileMetadata fileMetadata) {
        if (fileMetadata.getSampleIds().size() != 1) {
            // Fields from FILE_DATA can only be moved to SAMPLE_DATA if there is only one sample
            return false;
        }

        VariantFileHeaderComplexLine headerLine = getFileHeaderLine(fileMetadata, "INFO", fileDataKey);
        if (headerLine == null) {
            // Need to have the field in the INFO
            return false;
        }

        return true;
    }

    @Override
    protected void normalizeHeader(VariantFileMetadata fileMetadata) {
        if (getFileHeaderLine(fileMetadata, "FORMAT", sampleDataKey) == null) {
            if (newSampleMetadataLine == null) {
                VariantFileHeaderComplexLine info = getFileHeaderLine(fileMetadata, "INFO", fileDataKey);
                newSampleMetadataLine = new VariantFileHeaderComplexLine(
                        "FORMAT",
                        info.getId(),
                        info.getDescription(),
                        info.getNumber(),
                        info.getType(),
                        info.getGenericFields());
            }
            fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
        }
    }

    @Override
    protected void normalizeSample(Variant variant, StudyEntry study, FileEntry file, String sampleId, SampleEntry sample) {
        String fileValue = file.getData().get(fileDataKey);
        if (fileValue == null || fileValue.isEmpty() || fileValue.equals(".")) {
            // Nothing to do
            return;
        }

        if (study.getSampleDataKeySet().contains(sampleDataKey)) {
            study.addSampleDataKey(sampleDataKey);
        }

        Integer sampleDataKeyPosition = study.getSampleDataKeyPosition(sampleDataKey);
        String sampleValue = sample.getData().get(sampleDataKeyPosition);
        if (StringUtils.isEmpty(sampleValue)) {
            String newSampleValue = fieldMapper.apply(fileValue);
            while (sample.getData().size() < sampleDataKeyPosition) {
                sample.getData().add("");
            }
            sample.getData().set(sampleDataKeyPosition, newSampleValue);
        }
    }
}
