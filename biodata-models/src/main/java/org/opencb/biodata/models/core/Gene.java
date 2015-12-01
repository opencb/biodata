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

package org.opencb.biodata.models.core;

import org.opencb.biodata.models.variant.avro.Expression;
import org.opencb.biodata.models.variant.avro.GeneDrugInteraction;

import java.io.Serializable;
import java.util.List;


public class Gene implements Serializable {

    private static final long serialVersionUID = 5804770440067183880L;

    private String id;
    private String name;
    private String biotype;
    private String status;
    private String chromosome;
    private int start;
    private int end;
    private String strand;
    private String source;
    private String description;
    private List<Transcript> transcripts;
    private MiRNAGene mirna;
    private GeneAnnotation annotation;

    @Deprecated
    private List<Expression> expressionValues;
    @Deprecated
    private List<GeneDrugInteraction> drugInteractions;

    public Gene() {

    }

    public Gene(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
                String strand, String source, String description, List<Transcript> transcripts, MiRNAGene mirna,
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
		this.expressionValues = expressionValues;
		this.drugInteractions = drugInteractions;
    }

	public Gene(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
				String strand, String source, String description, List<Transcript> transcripts, MiRNAGene mirna,
				List<Expression> expressionValueList) {
		this(id, name, biotype, status, chromosome, start, end, strand, source, description, transcripts, mirna,
				expressionValueList, null);
	}

    public Gene(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
                String strand, String source, String description, List<Transcript> transcripts, MiRNAGene mirna, GeneAnnotation annotation) {
        this(id, name, biotype, status, chromosome, start, end, strand, source, description, transcripts, mirna, null, null);
        this.annotation = annotation;
    }


    @Override
    public String toString() {
        return "Gene [id=" + id + ", name=" + name
                + ", biotype=" + biotype + ", status=" + status
                + ", chromosome=" + chromosome + ", start=" + start + ", end="
                + end + ", strand=" + strand + ", source=" + source
                + ", description=" + description + ", transcripts="
                + transcripts + ", mirna=" + mirna + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiotype() {
        return biotype;
    }

    public void setBiotype(String biotype) {
        this.biotype = biotype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Transcript> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(List<Transcript> transcripts) {
        this.transcripts = transcripts;
    }

    public MiRNAGene getMirna() {
        return mirna;
    }

    public void setMirna(MiRNAGene mirna) {	this.mirna = mirna; }

    public GeneAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(GeneAnnotation annotation) {
        this.annotation = annotation;
    }

    public List<Expression> getExpressionValues() { return expressionValues; }

    public void setExpressionValues(List<Expression> expressionValues) { this.expressionValues = expressionValues;	}

    public List<GeneDrugInteraction> getDrugInteractions() { return drugInteractions; }

    public void setDrugInteractions(List<GeneDrugInteraction> drugInteractions) { this.drugInteractions = drugInteractions; }
}
