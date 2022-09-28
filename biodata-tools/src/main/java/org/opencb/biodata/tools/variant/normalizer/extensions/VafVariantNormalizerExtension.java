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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.tools.variant.VcfUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VafVariantNormalizerExtension extends VariantNormalizerExtension {

    private String caller;
    private boolean calculateVaf;
    private boolean calculateDp;

    public static final String EXT_VAF = "EXT_VAF";

    public VafVariantNormalizerExtension() {
        this("");
    }

    public VafVariantNormalizerExtension(String caller) {
        this.caller = caller.toUpperCase();
    }

    @Override
    public void init() {
        this.calculateVaf = false;
        this.calculateDp = false;

        // Check if a supported variant caller parameter has been provided in the constructor
        if (StringUtils.isNotEmpty(caller) && VcfUtils.checkCaller(caller, fileMetadata)) {
            calculateVaf = true;
            calculateDp = true;
            return;
        }

        // Guess the caller from the VCF header
        caller = VcfUtils.getCaller(fileMetadata);

        // Check if we can calculate the VAF
        if (StringUtils.isNotEmpty(caller)) {
            // Good news, a supported caller found
            calculateVaf = true;
            calculateDp = true;
        } else {
            // No caller found, but we can still calculate VAF if standard fields AD and DP are found
            // let's check if can find the fields needed to calculate the VAF

            // Parse and init internal configuration
            boolean containsFormatAD = false;
            boolean containsFormatDP = false;
            boolean containsInfoDP = false;
            boolean containsFormatExtVaf = false;
            for (VariantFileHeaderComplexLine complexLine : fileMetadata.getHeader().getComplexLines()) {
                if (complexLine.getKey().equalsIgnoreCase("INFO")) {
                    if (complexLine.getId().equals("DP")) {
                        containsInfoDP = true;
                    }
                } else if (complexLine.getKey().equalsIgnoreCase("FORMAT")) {
                    switch (complexLine.getId()) {
                        case "AD":
                            containsFormatAD = true;
                            break;
                        case "DP":
                            containsFormatDP = true;
                            break;
                        case EXT_VAF:
                            containsFormatExtVaf = true;
                            break;
                    }
                }
            }

            if (containsFormatAD && (containsFormatDP || containsInfoDP)) {
                calculateVaf = true;
                if (!containsFormatDP) {
                    calculateDp = true;
                }
            }

            // Important: If EXT_VAF filter already exist in the VCF header we cannot do anything
            if (containsFormatExtVaf) {
                calculateVaf = false;
            }
        }
    }

    @Override
    protected boolean canUseExtension(VariantFileMetadata fileMetadata) {
        // canCalculateVaf is calculated in the init() method after checking the VCF header fields
        return calculateVaf;
    }

    @Override
    protected void normalizeHeader(VariantFileMetadata fileMetadata) {
        if (calculateVaf) {
            // Add EXT_VAF
            VariantFileHeaderComplexLine newSampleMetadataLine = new VariantFileHeaderComplexLine( "FORMAT",
                    EXT_VAF,
                    "Variant Allele Fraction (VAF), several variant callers supported. NOTE: this is a OpenCB extension field.",
                    "1",
                    "Float",
                    Collections.emptyMap());
            fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
        }

        if (calculateDp) {
            // Add DP to FORMAT
            VariantFileHeaderComplexLine newSampleMetadataLine = new VariantFileHeaderComplexLine( "FORMAT",
                    "DP",
                    "Variant Depth (DP), several variant callers supported. NOTE: this is a OpenCB extension field.",
                    "1",
                    "Integer",
                    Collections.emptyMap());
            fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
        }
    }

    @Override
    protected void normalizeSample(Variant variant, StudyEntry study, FileEntry file, String sampleId, SampleEntry sample) {
        MutablePair<Float, Integer> pair = calculateVaf(variant, study, file, sample);

        // VAF
        study.addSampleDataKey(EXT_VAF);
        study.addSampleData(sampleId, EXT_VAF, pair.getLeft() >= 0
                ? String.valueOf(pair.getLeft())
                : VCFConstants.MISSING_VALUE_v4);

        if (calculateDp) {
            study.addSampleDataKey(VCFConstants.DEPTH_KEY);
            study.addSampleData(sampleId, VCFConstants.DEPTH_KEY, pair.getRight() >= 0
                    ? String.valueOf(pair.getRight())
                    : VCFConstants.MISSING_VALUE_v4);
        }
    }

    private MutablePair<Float, Integer> calculateVaf(Variant variant, StudyEntry study, FileEntry file, SampleEntry sample) {
        // If we reach this point is because canCalculateVaf is true and therefore we know we can calculate VAF
        // Init internal variables, this method calculates VAF and DEPTH and return them in a Pair tuple
        float VAF = -1f;
        int DP = -1;
        if (StringUtils.isNotEmpty(caller)) {
            List<String> formatFields;
            Integer index;
            switch (caller) {
                case "CAVEMAN":
                    // DEPTH
                    formatFields = Arrays.asList("FAZ", "FCZ", "FGZ", "FTZ", "RAZ", "RCZ", "RGZ", "RTZ");
                    for (String formatField : formatFields) {
                        index = study.getSampleDataKeyPositions().get(formatField);
                        if (index != null && index >= 0) {
                            DP += Integer.parseInt(sample.getData().get(index));
                        }
                    }
                    // VAF
                    VAF = Float.parseFloat(sample.getData().get(study.getSampleDataKeyPosition("PM")));
                    break;
                case "PINDEL":
                    int PU = Integer.parseInt(sample.getData().get(study.getSampleDataKeyPosition("PU")));
                    int NU = Integer.parseInt(sample.getData().get(study.getSampleDataKeyPosition("NU")));
                    int PR = Integer.parseInt(sample.getData().get(study.getSampleDataKeyPosition("PR")));
                    int NR = Integer.parseInt(sample.getData().get(study.getSampleDataKeyPosition("NR")));

                    DP = PR + NR;
                    VAF = (float) (PU + NU) / (PR + NR);
                    break;
                case "BRASS":
                    int RC = Integer.parseInt(sample.getData().get(study.getSampleDataKeyPosition("RC")));
                    int PS = Integer.parseInt(sample.getData().get(study.getSampleDataKeyPosition("PS")));
                    DP = RC + PS;
                    if (DP > 0) {
                        VAF = (float) RC / DP;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected variant caller: " + caller);
            }
        } else {
            // We assume AD and DP fields exist because canCalculateVaf is true and no caller has been found
            // 1. Get AD
            int AD = 0;
            Integer adIndex = study.getSampleDataKeyPosition("AD");
            if (adIndex != null && adIndex >= 0) {
                String adString = sample.getData().get(adIndex);
                if (StringUtils.isNotEmpty(adString) && !adString.equals(".")) {
                    String[] split = adString.split(",");
                    if (split.length > 1) {
                        AD = Integer.parseInt(split[1]);
                    }
                }
            }

            // 2. Get DEPTH
            // DP field can be located in the FORMAT (preferred) or in the INFO columns.
            // If DP is not found we can calculate it from AD
            // First, search in the FORMAT field
            if (study.getSampleDataKeyPositions().containsKey("DP")) {
                Integer depthIndex = study.getSampleDataKeyPosition("DP");
                if (adIndex != null && adIndex >= 0) {
                    String dpString = sample.getData().get(depthIndex);
                    if (StringUtils.isNotEmpty(dpString) && !dpString.equals(".")) {
                        DP = Integer.parseInt(dpString);
                    }
                }
            } else {
                // Second, some callers store DP in the INFO field when there is ONLY one sample per VCF
                if (study.getSamples().size() == 1 && file.getData().containsKey("DP")) {
                    String depthString = file.getData().getOrDefault("DP", "");
                    if (StringUtils.isNotEmpty(depthString) && !depthString.equals(".")) {
                        DP = Integer.parseInt(depthString);
                    }
                } else {
                    // Third, try to calculate DP from AD field
                    if (adIndex != null && adIndex >= 0) {
                        String adString = sample.getData().get(adIndex);
                        if (StringUtils.isNotEmpty(adString) && !adString.equals(".")) {
                            String[] ads = adString.split(",");
                            for (String ad : ads) {
                                DP += Integer.parseInt(ad);
                            }
                        }
                    }
                }
            }

            if (AD != 0 && DP > 0) {
                VAF = (float) AD / DP;
            }
        }

        return new MutablePair<>(VAF, DP);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VafVariantNormalizerExtension{");
        sb.append("caller='").append(caller).append('\'');
        sb.append(", calculateVaf=").append(calculateVaf);
        sb.append(", calculateDp=").append(calculateDp);
        sb.append('}');
        return sb.toString();
    }
}
