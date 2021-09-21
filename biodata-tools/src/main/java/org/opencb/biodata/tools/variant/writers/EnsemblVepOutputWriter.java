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

package org.opencb.biodata.tools.variant.writers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.commons.io.DataWriter;

import java.io.*;
import java.util.List;
import java.util.Map;

public class EnsemblVepOutputWriter implements DataWriter<Variant> {

    private final Writer dataOutputStream;
    private final boolean closeStream;
    private int writtenVariants;

    protected EnsemblVepOutputWriter(OutputStream os) {
        this(new OutputStreamWriter(os));
    }

    protected EnsemblVepOutputWriter(Writer dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
        this.closeStream = false;
    }

    @Override
    public boolean pre() {
        writtenVariants = 0;
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
        StringBuilder sb = new StringBuilder();
        String variantId, location, alternateAllele;
        String gene, feature, featureType, consequence, cdnaPosition, cdsPosiiton, proteinPosition, aaChange, codonChange;
        Map<String, String> extra;
        try {
            variantId = variant.getChromosome() + ":" + variant.getStart() + ":" + variant.getReference() + ":" + variant.getAlternate();
            location = variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd();
            alternateAllele = variant.getAlternate();

            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                for (ConsequenceType consequenceType : variant.getAnnotation().getConsequenceTypes()) {
                    gene = StringUtils.isNotEmpty(consequenceType.getGeneName()) ? consequenceType.getGeneName() : "-";
                    if (StringUtils.isNotEmpty(consequenceType.getTranscriptId())) {
                        feature = consequenceType.getTranscriptId();
                        featureType = "transcript";
                        cdnaPosition = String.valueOf(consequenceType.getCdnaPosition());
                        cdsPosiiton = String.valueOf(consequenceType.getCdsPosition());
                        codonChange = consequenceType.getCodon();
                        if (consequenceType.getProteinVariantAnnotation() != null) {
                            proteinPosition = String.valueOf(consequenceType.getProteinVariantAnnotation().getPosition());
                            aaChange = consequenceType.getProteinVariantAnnotation().getAlternate();
                        } else {
                            proteinPosition = "-";
                            aaChange = "-";
                        }
                    } else {
                        feature = "-";
                        featureType = "-";
                        cdnaPosition = "-";
                        cdsPosiiton = "-";
                        codonChange = "-";
                        proteinPosition = "-";
                        aaChange = "-";
                    }
                    for (SequenceOntologyTerm sequenceOntologyTerm : consequenceType.getSequenceOntologyTerms()) {
                        consequence = sequenceOntologyTerm.getName();
                        sb.append(variantId).append(location).append(alternateAllele)
                                .append(gene).append(feature).append(featureType).append(consequence)
                                .append(cdnaPosition).append(cdsPosiiton).append(proteinPosition).append(aaChange).append(codonChange)
                                .append("-").append("-").append("\n");
                    }
                }
            } else {
                sb.append(variantId).append(location).append(alternateAllele)
                        .append("-").append("-").append("-").append("-")
                        .append("-").append("-").append("-").append("-").append("-")
                        .append("-").append("-").append("\n");
            }

            dataOutputStream.write(sb.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        writtenVariants++;
        return true;
    }

    @Override
    public boolean post() {
        try {
            dataOutputStream.flush();
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean close() {
        if (closeStream) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        return true;
    }

    public int getWrittenVariants() {
        return writtenVariants;
    }

    public EnsemblVepOutputWriter setWrittenVariants(int writtenVariants) {
        this.writtenVariants = writtenVariants;
        return this;
    }
}
