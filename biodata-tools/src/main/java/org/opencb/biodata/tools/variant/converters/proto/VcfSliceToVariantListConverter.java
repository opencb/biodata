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

package org.opencb.biodata.tools.variant.converters.proto;

import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos;
import org.opencb.biodata.tools.commons.Converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created on 05/11/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VcfSliceToVariantListConverter implements Converter<VcfSliceProtos.VcfSlice, List<Variant>> {

    private final LinkedHashMap<String, Integer> samplesPosition;
    private final String fileId;
    private final String studyId;

    public VcfSliceToVariantListConverter(VariantStudyMetadata metadata) {
        this(VariantFileMetadata.getSamplesPositionMap(metadata.getFiles().get(0).getSampleIds()),
                metadata.getFiles().get(0).getId(), metadata.getId());
    }

    public VcfSliceToVariantListConverter(Map<String, Integer> samplesPosition, String fileId, String studyId) {
        this.samplesPosition = StudyEntry.sortSamplesPositionMap(samplesPosition);
        this.fileId = fileId;
        this.studyId = studyId;
    }

    @Override
    public List<Variant> convert(VcfSliceProtos.VcfSlice vcfSlice) {
        return convert(vcfSlice, null);
    }

    public List<Variant> convert(VcfSliceProtos.VcfSlice vcfSlice, Predicate<VcfSliceProtos.VcfRecord> filter) {
        VcfRecordProtoToVariantConverter recordConverter = new VcfRecordProtoToVariantConverter(vcfSlice.getFields(),
                samplesPosition, fileId, studyId);
        List<Variant> variants = new ArrayList<>(vcfSlice.getRecordsCount());
        for (VcfSliceProtos.VcfRecord vcfRecord : vcfSlice.getRecordsList()) {
            if (filter == null || filter.test(vcfRecord)) {
                variants.add(recordConverter.convert(vcfRecord, vcfSlice.getChromosome(), vcfSlice.getPosition()));
            }
        }
        return variants;
    }

}
