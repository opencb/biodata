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

import org.opencb.biodata.models.variant.avro.Expression;
import org.opencb.biodata.models.variant.avro.GeneDrugInteraction;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class Gene implements Serializable {

    private String id;
    private String name;
    private String chromosome;
    private int start;
    private int end;
    private String strand;
    private String biotype;
    private String status;
    private String source;
    private String version;
    private String description;
    private List<Transcript> transcripts;
    private MiRnaGene mirna;
    private GeneAnnotation annotation;

    private static final long serialVersionUID = 5804770440067183880L;

    public Gene() {
    }

    @Deprecated
    public Gene(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
                String strand, String source, String description, List<Transcript> transcripts, MiRnaGene mirna,
                List<Expression> expressionValues, List<GeneDrugInteraction> drugInteractions) {
        super();
        this.id = id;
        this.name = name;
        this.biotype = biotype;
        this.status = status;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.source = source;
        this.description = description;
        this.transcripts = transcripts;
        this.mirna = mirna;
//        this.annotation = annotation;
    }

    @Deprecated
	public Gene(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
				String strand, String source, String description, List<Transcript> transcripts, MiRnaGene mirna,
				List<Expression> expressionValueList) {
		this(id, name, biotype, status, chromosome, start, end, strand, source, description, transcripts, mirna,
				expressionValueList, null);
	}

    @Deprecated
    public Gene(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
                String strand, String source, String description, List<Transcript> transcripts, MiRnaGene mirna, GeneAnnotation annotation) {
        this(id, name, biotype, status, chromosome, start, end, strand, source, description, transcripts, mirna, null, null);
        this.annotation = annotation;
    }

    @Deprecated
    public Gene(String id, String name, String chromosome, int start, int end, String strand, String biotype, String status, String source,
                String description, List<Transcript> transcripts, MiRnaGene mirna, GeneAnnotation annotation) {
        this.id = id;
        this.name = name;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.biotype = biotype;
        this.status = status;
        this.source = source;
        this.description = description;
        this.transcripts = transcripts;
        this.mirna = mirna;
        this.annotation = annotation;
    }

    public Gene(String id, String name, String chromosome, int start, int end, String strand, String version, String biotype, String status,
                String source, String description, List<Transcript> transcripts, MiRnaGene mirna, GeneAnnotation annotation) {
        this.id = id;
        this.name = name;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.version = version;
        this.biotype = biotype;
        this.status = status;
        this.source = source;
        this.description = description;
        this.transcripts = transcripts;
        this.mirna = mirna;
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Gene{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", strand='").append(strand).append('\'');
        sb.append(", biotype='").append(biotype).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", version=").append(version);
        sb.append(", description='").append(description).append('\'');
        sb.append(", transcripts=").append(transcripts);
        sb.append(", mirna=").append(mirna);
        sb.append(", annotation=").append(annotation);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Gene setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Gene setName(String name) {
        this.name = name;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Gene setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public Gene setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public Gene setEnd(int end) {
        this.end = end;
        return this;
    }

    public String getStrand() {
        return strand;
    }

    public Gene setStrand(String strand) {
        this.strand = strand;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Gene setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getBiotype() {
        return biotype;
    }

    public Gene setBiotype(String biotype) {
        this.biotype = biotype;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Gene setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Gene setSource(String source) {
        this.source = source;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Gene setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<Transcript> getTranscripts() {
        return transcripts;
    }

    public Gene setTranscripts(List<Transcript> transcripts) {
        this.transcripts = transcripts;
        return this;
    }

    public MiRnaGene getMirna() {
        return mirna;
    }

    public Gene setMirna(MiRnaGene mirna) {
        this.mirna = mirna;
        return this;
    }

    public GeneAnnotation getAnnotation() {
        return annotation;
    }

    public Gene setAnnotation(GeneAnnotation annotation) {
        this.annotation = annotation;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gene)) return false;
        Gene gene = (Gene) o;
        return getStart() == gene.getStart() &&
                getEnd() == gene.getEnd() &&
                getVersion() == gene.getVersion() &&
                Objects.equals(getId(), gene.getId()) &&
                Objects.equals(getName(), gene.getName()) &&
                Objects.equals(getChromosome(), gene.getChromosome()) &&
                Objects.equals(getStrand(), gene.getStrand()) &&
                Objects.equals(getBiotype(), gene.getBiotype()) &&
                Objects.equals(getStatus(), gene.getStatus()) &&
                Objects.equals(getSource(), gene.getSource()) &&
                Objects.equals(getDescription(), gene.getDescription()) &&
                Objects.equals(getTranscripts(), gene.getTranscripts()) &&
                Objects.equals(getMirna(), gene.getMirna()) &&
                Objects.equals(getAnnotation(), gene.getAnnotation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getChromosome(), getStart(), getEnd(), getStrand(), getBiotype(), getStatus(), getSource(), getVersion(), getDescription(), getTranscripts(), getMirna(), getAnnotation());
    }
}
