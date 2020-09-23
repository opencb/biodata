package org.opencb.biodata.tools.variant.normalizer.extensions;

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderSimpleLine;
import org.opencb.commons.run.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class VariantNormalizerExtension implements Task<Variant, Variant> {

    private VariantFileMetadata metadata;

    public final VariantNormalizerExtension init(VariantFileMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public void pre() throws Exception {
        normalizeHeader(metadata);
    }

    @Override
    public final List<Variant> apply(List<Variant> list) throws Exception {
        for (Variant variant : list) {
            normalizeVariant(variant);
            if (variant.getStudies() == null || variant.getStudies().isEmpty()) {
                continue;
            }
            // Only one study expected
            StudyEntry study = variant.getStudies().get(0);
            FileEntry fileEntry;
            if (study.getFiles() != null && !study.getFiles().isEmpty()) {
               // Only one file expected
                fileEntry = study.getFiles().get(0);
                normalizeFile(variant, study, fileEntry);
                if (study.getSamples() != null) {
                    for (Map.Entry<String, Integer> entry : study.getSamplesPosition().entrySet()) {
                        normalizeSample(variant, study, fileEntry, entry.getKey(), study.getSample(entry.getValue()));
                    }
                    for (FileEntry file : study.getFiles()) {
                        normalizeFile(variant, study, file);
                    }
                }
            }
        }
        return list;
    }

    protected final VariantFileHeaderComplexLine getFileHeaderLine(VariantFileMetadata fileMetadata, String key, String id) {
        for (VariantFileHeaderComplexLine line : fileMetadata.getHeader().getComplexLines()) {
            if (line.getKey().equals(key)) {
                if (line.getId().equals(id)) {
                    return line;
                }
            }
        }
        return null;
    }

    protected final List<VariantFileHeaderSimpleLine> getFileHeaderLine(VariantFileMetadata fileMetadata, String key) {
        List<VariantFileHeaderSimpleLine> lines = new LinkedList<>();
        for (VariantFileHeaderSimpleLine line : fileMetadata.getHeader().getSimpleLines()) {
            if (line.getKey().equals(key)) {
                lines.add(line);
            }
        }
        return lines;
    }

    protected abstract boolean canUseExtension(VariantFileMetadata fileMetadata);

    protected void normalizeHeader(VariantFileMetadata fileMetadata) {}

    protected void normalizeVariant(Variant variant) {}

    protected void normalizeSample(Variant variant, StudyEntry study, FileEntry file, String sampleId, SampleEntry sample) {}

    protected void normalizeFile(Variant variant, StudyEntry study, FileEntry file) {}

}


