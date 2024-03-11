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

package org.opencb.biodata.models.core;

import org.opencb.biodata.models.variant.avro.EvidenceEntry;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;

import java.util.List;

public class SnpAnnotation {

    private List<String> flags;
    private String gene;
    private List<PopulationFrequency> populationFrequencies;
    private List<EvidenceEntry> traitAssociation;

    public SnpAnnotation() {
    }

    public SnpAnnotation(List<String> flags, String gene, List<PopulationFrequency> populationFrequencies, List<EvidenceEntry> traitAssociation) {
        this.flags = flags;
        this.gene = gene;
        this.populationFrequencies = populationFrequencies;
        this.traitAssociation = traitAssociation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SnpAnnotation{");
        sb.append("flags=").append(flags);
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", populationFrequencies=").append(populationFrequencies);
        sb.append(", traitAssociation=").append(traitAssociation);
        sb.append('}');
        return sb.toString();
    }

    public List<String> getFlags() {
        return flags;
    }

    public SnpAnnotation setFlags(List<String> flags) {
        this.flags = flags;
        return this;
    }

    public String getGene() {
        return gene;
    }

    public SnpAnnotation setGene(String gene) {
        this.gene = gene;
        return this;
    }

    public List<PopulationFrequency> getPopulationFrequencies() {
        return populationFrequencies;
    }

    public SnpAnnotation setPopulationFrequencies(List<PopulationFrequency> populationFrequencies) {
        this.populationFrequencies = populationFrequencies;
        return this;
    }

    public List<EvidenceEntry> getTraitAssociation() {
        return traitAssociation;
    }

    public SnpAnnotation setTraitAssociation(List<EvidenceEntry> traitAssociation) {
        this.traitAssociation = traitAssociation;
        return this;
    }
}
