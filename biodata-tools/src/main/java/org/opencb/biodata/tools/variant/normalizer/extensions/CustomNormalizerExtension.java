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

package org.opencb.biodata.tools.variant.normalizer.extensions;

import htsjdk.variant.vcf.*;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.tools.variant.converters.avro.VCFHeaderToVariantFileHeaderConverter;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomNormalizerExtension extends VariantNormalizerExtension {

    private List<String> header;
    private Map<String, String> variantValuesMap;
    private boolean isVCustomFileValid;
    private boolean normalizeFile;
    private boolean normalizeSample;
    public static final String CUSTOM_FILE_EXTENSION = ".custom.annotation.txt";

    private static Logger logger = LoggerFactory.getLogger(CustomNormalizerExtension.class);

    public CustomNormalizerExtension() {
    }

    @Override
    public void init() {
        // Custom annotation file must end with ".custom.annotation.txt"
        Path customFilePath = Paths.get(fileMetadata.getPath() + CUSTOM_FILE_EXTENSION);
        if (!Files.exists(customFilePath)) {
            // File doesn't exist. Skip using extension
            logger.info("Not using " + getClass().getSimpleName() + " as file {} does not exist.", customFilePath);
            isVCustomFileValid = false;
            return;
        }
        try {
            FileUtils.checkFile(customFilePath);

            // Store the INFO and FORMAT lines
            header = new ArrayList<>();
            variantValuesMap = new HashMap<>();
            int lines = 0;
            // Init valid variable and check everything is good
            isVCustomFileValid = true;
            try (BufferedReader bufferedReader = FileUtils.newBufferedReader(customFilePath)) {
                String line = bufferedReader.readLine();
                while (StringUtils.isNotEmpty(line)) {
                    lines++;
                    if (line.startsWith("##")) {
                        header.add(line);
                    } else {
                        String[] split = line.split("\t");
                        if (split.length == 2) {
//                            Variant variant = new Variant(split[0]);
                            Variant variant = new VariantBuilder(split[0]).ignoreMissingEnd(true).build();
                            variantValuesMap.put(variant.toString(), split[1]);
                        } else {
                            throw new IOException("Malformed custom normalization file " + customFilePath + " in line: " + lines);
                        }
                    }
                    // read next line
                    line = bufferedReader.readLine();
                }
            }

            // Header is mandatory
            if (header.isEmpty()) {
                throw new IOException("Missing header in custom normalization file " + customFilePath);
            } else {
                for (String line : header) {
                    normalizeFile |= line.startsWith(VCFConstants.INFO_HEADER_START);
                    normalizeSample |= line.startsWith(VCFConstants.FORMAT_HEADER_START);
                }
                if (normalizeFile && normalizeSample) {
                    throw new IOException("Unable to mix FORMAT and INFO in the same custom normalizer extension file.");
                }
            }
        } catch (IOException e) {
            isVCustomFileValid = false;
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected boolean canUseExtension(VariantFileMetadata fileMetadata) {
        return isVCustomFileValid;
    }

    @Override
    protected void normalizeHeader(VariantFileMetadata fileMetadata) {
        for (String line : header) {
            VCFCompoundHeaderLine vcfCompoundHeaderLine;
            if (line.startsWith(VCFConstants.INFO_HEADER_START)) {
                vcfCompoundHeaderLine =
                        new VCFInfoHeaderLine(line.substring(VCFConstants.INFO_HEADER_START.length() + 1), VCFHeaderVersion.VCF4_2);
            } else if (line.startsWith(VCFConstants.FORMAT_HEADER_START)) {
                vcfCompoundHeaderLine =
                        new VCFFormatHeaderLine(line.substring(VCFConstants.FORMAT_HEADER_START.length() + 1), VCFHeaderVersion.VCF4_2);
            } else {
                logger.info("Ignore custom header line: " + line);
                continue;
            }
            VariantFileHeaderComplexLine newSampleMetadataLine = VCFHeaderToVariantFileHeaderConverter
                    .convertComplexLine(vcfCompoundHeaderLine);
            fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
        }
    }

    @Override
    protected void normalizeFile(Variant variant, StudyEntry study, FileEntry file) {
        // Check if we can get SVTYPE from this caller
        if (normalizeFile) {
            String value = getVariantValue(variant, file);
            if (value != null) {
                String[] split = value.split(";");
                for (String s : split) {
                    String[] keyValue = s.split("=");
                    study.addFileData(file.getFileId(), keyValue[0], keyValue[1]);
                }
            }
        }
    }

    @Override
    protected void normalizeSample(Variant variant, StudyEntry study, FileEntry file, String sampleId, SampleEntry sample) {
        if (normalizeSample) {
            String value = getVariantValue(variant, file);
            if (value != null) {
                String[] split = value.split(";");
                for (String s : split) {
                    String[] keyValue = s.split("=");
                    study.addSampleDataKey(keyValue[0]);
                    study.addSampleData(sampleId, keyValue[0], keyValue[1]);
                }
            }
        }
    }

    private String getVariantValue(Variant variant, FileEntry file) {
        String variantId;
        if (file.getCall() == null || file.getCall().getVariantId() == null) {
            variantId = variant.toStringSimple();
        } else {
            variantId = new Variant(file.getCall().getVariantId()).toStringSimple();
        }
        return variantValuesMap.get(variantId);
    }

}
