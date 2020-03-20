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

package org.opencb.biodata.tools.variant.stats.writer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.variant.converters.avro.VariantStatsToPopulationFrequencyConverter;
import org.opencb.commons.io.DataWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports the given variant stats into a Json format.
 *
 * The writer will transform the VariantStats into PopulationFrequency objects using
 * the {@link VariantStatsToPopulationFrequencyConverter}
 * The output variants won't contain any StudyEntries or other extra annotations
 *
 * Created on 01/06/16.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsPopulationFrequencyExporter implements DataWriter<Variant> {

    private OutputStream outputStream;
    private final VariantStatsToPopulationFrequencyConverter converter;
    private SequenceWriter sequenceWriter;

    public VariantStatsPopulationFrequencyExporter(OutputStream outputStream) {
        this.outputStream = outputStream;
        converter = new VariantStatsToPopulationFrequencyConverter();

    }

    @Override
    public boolean pre() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        ObjectWriter objectWriter = objectMapper.writerFor(Variant.class);
        try {
            sequenceWriter = objectWriter.writeValues(outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return true;
    }


    @Override
    public boolean write(List<Variant> batch) {
        for (Variant variant : batch) {
            write(variant);
        }
        return true;
    }

    @Override
    public boolean write(Variant variant) {
        ArrayList<PopulationFrequency> frequencies = new ArrayList<>();
        for (StudyEntry studyEntry : variant.getStudies()) {
            for (VariantStats variantStats : studyEntry.getStats()) {
                String studyId = studyEntry.getStudyId();
                studyId = studyId.substring(studyId.lastIndexOf(":") + 1);
                PopulationFrequency populationFrequency = converter.convert(studyId,
                        variantStats, variant.getReference(), variant.getAlternate());
                // Write only frequencies non zero
                if (populationFrequency.getAltAlleleFreq() > 0 && !populationFrequency.getAltAlleleFreq().isNaN()) {
                    frequencies.add(populationFrequency);
                }
            }
        }
        Variant newVar = new Variant(variant.toString());
        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setPopulationFrequencies(frequencies);
        newVar.setAnnotation(annotation);
        try {
            sequenceWriter.write(newVar);
            outputStream.write('\n');
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return true;
    }
}
