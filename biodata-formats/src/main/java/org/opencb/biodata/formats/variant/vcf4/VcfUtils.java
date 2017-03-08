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

package org.opencb.biodata.formats.variant.vcf4;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;

import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VcfUtils {

    public static String getInfoColumn(StudyEntry file) {
        StringBuilder info = new StringBuilder();

        for (Map.Entry<String, String> entry : file.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (!key.equalsIgnoreCase("QUAL") && !key.equalsIgnoreCase("FILTER")) {
                info.append(key);

                String value = entry.getValue();
                if (value.length() > 0) {
                    info.append("=");
                    info.append(value);
                }

                info.append(";");
            }
        }

        return info.toString().isEmpty() ? "." : info.toString();
    }

    public static String getInfoColumn(Variant variant, String fileId, String studyId) {
        return VcfUtils.getInfoColumn(variant.getSourceEntry(fileId, studyId));
    }

    public static String getJoinedSampleFields(StudyEntry file, String sampleName) {
        Map<String, String> data = file.getSampleData(sampleName);
        if (data == null) {
            return "";
        }

        StringBuilder info = new StringBuilder();
        for (String formatField : file.getFormatAsString().split(":")) {
            info.append(data.get(formatField)).append(":");
        }

        return info.toString().isEmpty() ? "." : info.toString();
    }

    public static String getJoinedSampleFields(Variant variant, StudyEntry file, String sampleName) {
        return VcfUtils.getJoinedSampleFields(variant.getSourceEntry(file.getFileId(), file.getStudyId()), sampleName);
    }
}
