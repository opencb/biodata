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

import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeaderVersion;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CustomNormalizerExtension extends VariantNormalizerExtension {

    private List<String> header;
    private Map<String, String> variantValuesMap;
    private boolean isVCustomFileValid;

    public static final String CUSTOM_FILE_EXTENSION = ".custom.annotation.txt";

    public CustomNormalizerExtension() {
    }

    @Override
    public void init() {
        try {
            // Custom annotation file must end with ".custom.annotation.txt"
            Path customFilePath = Paths.get(fileMetadata.getPath()).resolve(CUSTOM_FILE_EXTENSION);
            FileUtils.checkFile(customFilePath);

            // Store the INFO and FORMAT lines
            header = new ArrayList<>();
            variantValuesMap = new HashMap<>();

            // Init valid variable and check is all good
            isVCustomFileValid = true;
            BufferedReader bufferedReader = FileUtils.newBufferedReader(customFilePath);
            String line = bufferedReader.readLine();
            while(StringUtils.isNotEmpty(line)) {
                if (line.startsWith("##")) {
                    header.add(line);
                } else {
                    String[] split = line.split("\t");
                    if (split.length == 2) {
                        variantValuesMap.put(split[0], split[1]);
                    } else {
                        isVCustomFileValid = false;
                        break;
                    }
                }
                // read next line
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            // Header is mandatory
            if (header.size() == 0) {
                isVCustomFileValid = false;
            }
        } catch (IOException e) {
            isVCustomFileValid = false;
            e.printStackTrace();
        }
    }

    @Override
    protected boolean canUseExtension(VariantFileMetadata fileMetadata) {
        return isVCustomFileValid;
    }

    @Override
    protected void normalizeHeader(VariantFileMetadata fileMetadata) {
        for (String line : header) {
            if (line.startsWith("##INFO")) {
                VCFInfoHeaderLine vcfInfoHeaderLine =
                        new VCFInfoHeaderLine(line.substring(VCFConstants.INFO_HEADER_START.length() + 1), VCFHeaderVersion.VCF4_2);
                String count;
                if (vcfInfoHeaderLine.getCountType().name().equalsIgnoreCase("INTEGER")) {
                    count = String.valueOf(vcfInfoHeaderLine.getCount());
                } else {
                    count = vcfInfoHeaderLine.getCountType().name();
                }
                VariantFileHeaderComplexLine newSampleMetadataLine = new VariantFileHeaderComplexLine( "INFO",
                        vcfInfoHeaderLine.getID(),
                        vcfInfoHeaderLine.getDescription(),
                        count,
                        vcfInfoHeaderLine.getType().name(),
                        Collections.emptyMap());
                fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
            } else {
                if (line.startsWith("##FORMAT")) {
                    VCFFormatHeaderLine vcfFormatHeaderLine =
                            new VCFFormatHeaderLine(line.substring(VCFConstants.FORMAT_HEADER_START.length() + 1), VCFHeaderVersion.VCF4_2);
                    String count;
                    if (vcfFormatHeaderLine.getCountType().name().equalsIgnoreCase("INTEGER")) {
                        count = String.valueOf(vcfFormatHeaderLine.getCount());
                    } else {
                        count = vcfFormatHeaderLine.getCountType().name();
                    }
                    VariantFileHeaderComplexLine newSampleMetadataLine = new VariantFileHeaderComplexLine( "FORMAT",
                            vcfFormatHeaderLine.getID(),
                            vcfFormatHeaderLine.getDescription(),
                            count,
                            vcfFormatHeaderLine.getType().name(),
                            Collections.emptyMap());
                    fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
                } else {
                    System.out.println("Ignore custom header line: " + line);
                }
            }
        }
    }

    @Override
    protected void normalizeFile(Variant variant, StudyEntry study, FileEntry file) {
        // Check if we can get SVTYPE from this caller
        if (header.get(0).startsWith("##INFO")) {
            String value = variantValuesMap.get(variant.getId());
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
        if (header.get(0).startsWith("##FORMAT")) {
            String value = variantValuesMap.get(variant.getId());
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

}
