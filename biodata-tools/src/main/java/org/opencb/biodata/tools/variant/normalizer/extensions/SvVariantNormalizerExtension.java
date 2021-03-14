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

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.tools.variant.VcfUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SvVariantNormalizerExtension extends VariantNormalizerExtension {

    private String caller;
    private static final Map<String, String> SUPPORTED_SVTYPE_CALLERS;
    private static final Map<String, String> SUPPORTED_SVLEN_CALLERS;

    public static final String EXT_SVTYPE = "EXT_SVTYPE";
    public static final String EXT_SVLEN = "EXT_SVLEN";

    static {
        SUPPORTED_SVTYPE_CALLERS = new HashMap<>();
        SUPPORTED_SVTYPE_CALLERS.put("BRASS", "SVCLASS");

        SUPPORTED_SVLEN_CALLERS = new HashMap<>();
        SUPPORTED_SVLEN_CALLERS.put("PINDEL", "LEN");
    }

    public SvVariantNormalizerExtension() {
        this("");
    }

    public SvVariantNormalizerExtension(String caller) {
        this.caller = caller.toUpperCase();
    }


    @Override
    public void init() {
        // Check if a supported variant caller parameter has been provided in the constructor
        if (StringUtils.isNotEmpty(caller) && VcfUtils.checkCaller(caller, fileMetadata)) {
            return;
        }

        // Guess the caller from the VCF header
        caller = VcfUtils.getCaller(fileMetadata);
    }

    @Override
    protected boolean canUseExtension(VariantFileMetadata fileMetadata) {
        // canCalculateVaf is calculated in the init() method after checking the VCF header fields
        return SUPPORTED_SVTYPE_CALLERS.containsKey(caller) || SUPPORTED_SVLEN_CALLERS.containsKey(caller);
    }

    @Override
    protected void normalizeHeader(VariantFileMetadata fileMetadata) {
        if (SUPPORTED_SVTYPE_CALLERS.containsKey(caller)) {
            // Add EXT_SVTYPE
            VariantFileHeaderComplexLine newSampleMetadataLine = new VariantFileHeaderComplexLine( "INFO",
                    EXT_SVTYPE,
                    "Variant SVTYPE obtained from " + SUPPORTED_SVTYPE_CALLERS.get(caller)
                            + ", several variant callers supported. NOTE: this is a OpenCB extension field.",
                    "1",
                    "String",
                    Collections.emptyMap());
            fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
        }

        if (SUPPORTED_SVLEN_CALLERS.containsKey(caller)) {
            // Add EXT_SVLEN
            VariantFileHeaderComplexLine newSampleMetadataLine = new VariantFileHeaderComplexLine( "INFO",
                    EXT_SVLEN,
                    "Variant SVLEN obtained from " + SUPPORTED_SVLEN_CALLERS.get(caller)
                            + ", several variant callers supported. NOTE: this is a OpenCB extension field.",
                    "1",
                    "Integer",
                    Collections.emptyMap());
            fileMetadata.getHeader().getComplexLines().add(newSampleMetadataLine);
        }
    }

    @Override
    protected void normalizeFile(Variant variant, StudyEntry study, FileEntry file) {
        // Check if we can get SVTYPE from this caller
        if (SUPPORTED_SVTYPE_CALLERS.containsKey(caller)) {
            VariantType svtype = parseSvtype(file);
            // Check returned svtype, some variants could miss the svtype
            if (svtype != null) {
                study.addFileData(file.getFileId(), EXT_SVTYPE, svtype.name());
            }
        }

        // Check if we can get SVLEN from this caller
        if (SUPPORTED_SVLEN_CALLERS.containsKey(caller)) {
            String svlen = file.getData().get(SUPPORTED_SVLEN_CALLERS.get(caller));
            // Check returned svlen, some variants could miss the svlen
            if (StringUtils.isNotEmpty(svlen)) {
                study.addFileData(file.getFileId(), EXT_SVLEN, svlen);
            }
        }
    }

    private VariantType parseSvtype(FileEntry file) {
        VariantType SVTYPE = null;
        String fileSvType = file.getData().get(SUPPORTED_SVTYPE_CALLERS.get(caller));

        if (StringUtils.isNotEmpty(fileSvType)) {
            switch (fileSvType.toUpperCase()) {
                case "INS":
                case "INSERTION":
                    SVTYPE = VariantType.INSERTION;
                    break;
                case "DEL":
                case "DELETION":
                    SVTYPE = VariantType.DELETION;
                    break;
                case "DUP":
                case "DUPLICATION":
                    SVTYPE = VariantType.DUPLICATION;
                    break;
                case "INV":
                case "INVERSION":
                    SVTYPE = VariantType.INVERSION;
                    break;
                case "CNV":
                case "COPY_NUMBER":
                    SVTYPE = VariantType.COPY_NUMBER;
                    break;
                case "BND":
                case "BREAKEND":
                    SVTYPE = VariantType.BREAKEND;
                    break;
                case "TRANS":
                case "TRANSLOCATION":
                    SVTYPE = VariantType.TRANSLOCATION;
                    break;
                case "TANDEM-DUPLICATION":
                case "TANDEM_DUPLICATION":
                    SVTYPE = VariantType.TANDEM_DUPLICATION;
                    break;
                default:
                    break;
            }
        }

        return SVTYPE;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SvVariantNormalizerExtension{");
        sb.append("caller='").append(caller).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
