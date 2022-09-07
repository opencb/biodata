package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFSampleHeaderLine;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;

import java.util.*;
import java.util.stream.Collectors;

public class VCFHeaderToVariantFileMetadataConverter {

    public VariantFileMetadata convert(VCFHeader header, String id, String path) {
        return convert(header, new VariantFileMetadata(id, path));
    }

    public VariantFileMetadata convert(VCFHeader header, VariantFileMetadata variantFileMetadata) {
        VariantFileHeader variantFileHeader = new VCFHeaderToVariantFileHeaderConverter().convert(header);
        Map<String, String> sampleMapping = getSampleMapping(variantFileHeader);
        List<String> samples = getSamples(header, sampleMapping);

        // Create converters and fill VariantSource
        variantFileMetadata.setHeader(variantFileHeader);
        variantFileMetadata.setSampleIds(samples);
        if (variantFileMetadata.getAttributes() == null) {
            variantFileMetadata.setAttributes(new HashMap<>());
        }
        variantFileMetadata.getAttributes().put("originalSamples", String.join(",", header.getGenotypeSamples()));

        return variantFileMetadata;
    }

    public List<String> getSamples(VCFHeader header, Map<String, String> sampleNameMapping) {
        if (sampleNameMapping == null) {
            sampleNameMapping = Collections.emptyMap();
        }

        List<String> samplesInOriginalOrder = header.getGenotypeSamples();
        List<String> renamedSamples = new ArrayList<>(samplesInOriginalOrder.size());
        for (String sample : samplesInOriginalOrder) {
            renamedSamples.add(sampleNameMapping.getOrDefault(sample, sample));
        }

        return renamedSamples;
    }

    public Map<String, String> getSampleMapping(VCFHeader header) {
        Map<String, String> sampleNameMapping = new HashMap<>();
        for (VCFHeaderLine line : header.getMetaDataInInputOrder()) {
            if (line instanceof VCFSampleHeaderLine) {
                VCFSampleHeaderLine sampleHeaderLine = (VCFSampleHeaderLine) line;
                getActualSampleName(sampleHeaderLine.getID(), sampleHeaderLine.getGenericFields(), sampleNameMapping);
            }
        }
        return sampleNameMapping;
    }

    public Map<String, String> getSampleMapping(VariantFileHeader header) {
        Map<String, String> sampleNameMapping = new HashMap<>();
        for (VariantFileHeaderComplexLine line : header.getComplexLines()) {
            if (line.getKey().equals(VCFConstants.SAMPLE_HEADER_KEY)) {
                getActualSampleName(line.getId(), line.getGenericFields(), sampleNameMapping);
            }
        }
        return sampleNameMapping;
    }

    private void getActualSampleName(String id, Map<String, String> genericFields, Map<String, String> sampleNameMapping) {
        String sampleName = getValueIgnoreCase(genericFields, "SampleName");
        if (sampleName == null) {
            sampleName = getValueIgnoreCase(genericFields, "SampleId");
        }
        if (sampleName == null) {
            sampleName = getValueIgnoreCase(genericFields, "Name");
        }
        if (sampleName != null) {
            sampleNameMapping.put(id, sampleName);
        }
    }

    private String getValueIgnoreCase(Map<String, String> map, String key) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                String value = entry.getValue();
                if (StringUtils.isNotEmpty(value)) {
                    return value;
                }
            }
        }
        return null;
    }

}
