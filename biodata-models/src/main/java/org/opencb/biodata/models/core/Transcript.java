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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class Transcript implements Serializable {

	private String id;
	private String name;
	private String chromosome;
	private int start;
	private int end;
	private String strand;
	private String biotype;
	private String status;
	private int genomicCodingStart;
	private int genomicCodingEnd;
	private int cdnaCodingStart;
	private int cdnaCodingEnd;
	private int cdsLength;
	private String cDnaSequence;
	private String proteinId;
	private String proteinSequence;
	private String description;
	private String version;
	private String source;
	private List<Exon> exons;
	private List<Xref> xrefs;
	private List<TranscriptTfbs> tfbs;
	private Set<String> flags;
	private TranscriptAnnotation annotation;

	private static final long serialVersionUID = 2069002722080532350L;

	public Transcript() {
	}

	@Deprecated
	public Transcript(String id, String name, String biotype, String status, String chromosome, Integer start, Integer end,
					  String strand, Integer codingRegionStart, Integer codingRegionEnd, Integer cdnaCodingStart,
					  Integer cdnaCodingEnd, Integer cdsLength, String proteinId, String description, List<Xref> xrefs,
					  List<Exon> exons, List<TranscriptTfbs> tfbs, TranscriptAnnotation annotation) {
		this.id = id;
		this.name = name;
		this.biotype = biotype;
		this.status = status;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.genomicCodingStart = codingRegionStart;
		this.genomicCodingEnd = codingRegionEnd;
		this.cdnaCodingStart = cdnaCodingStart;
		this.cdnaCodingEnd = cdnaCodingEnd;
		this.cdsLength = cdsLength;
		this.proteinId = proteinId;
		this.description = description;
		this.xrefs = xrefs;
		this.exons = exons;
		this.tfbs = tfbs;
		this.annotation = annotation;
	}

	public Transcript(String id, String name, String chromosome, int start, int end, String strand, String biotype, String status,
					  int genomicCodingStart, int genomicCodingEnd, int cdnaCodingStart, int cdnaCodingEnd, int cdsLength,
					  String cDnaSequence, String proteinId, String proteinSequence, String description, List<Exon> exons,
					  List<Xref> xrefs, List<TranscriptTfbs> tfbs, Set<String> flags, TranscriptAnnotation annotation) {
		this.id = id;
		this.name = name;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.biotype = biotype;
		this.status = status;
		this.genomicCodingStart = genomicCodingStart;
		this.genomicCodingEnd = genomicCodingEnd;
		this.cdnaCodingStart = cdnaCodingStart;
		this.cdnaCodingEnd = cdnaCodingEnd;
		this.cdsLength = cdsLength;
		this.cDnaSequence = cDnaSequence;
		this.proteinId = proteinId;
		this.proteinSequence = proteinSequence;
		this.description = description;
		this.exons = exons;
		this.xrefs = xrefs;
		this.tfbs = tfbs;
		this.flags = flags;
		this.annotation = annotation;
	}

	public Transcript(String id, String name, String biotype, String status, String source, String chromosome, Integer start, Integer end,
					  String strand, String version, Integer codingRegionStart, Integer codingRegionEnd,
					  Integer cdnaCodingStart, Integer cdnaCodingEnd, Integer cdsLength, String proteinId, String description,
					  List<Xref> xrefs, List<Exon> exons, List<TranscriptTfbs> tfbs, TranscriptAnnotation annotation) {
		this.id = id;
		this.name = name;
		this.biotype = biotype;
		this.status = status;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.version = version;
		this.source = source;
		this.genomicCodingStart = codingRegionStart;
		this.genomicCodingEnd = codingRegionEnd;
		this.cdnaCodingStart = cdnaCodingStart;
		this.cdnaCodingEnd = cdnaCodingEnd;
		this.cdsLength = cdsLength;
		this.proteinId = proteinId;
		this.description = description;
		this.xrefs = xrefs;
		this.exons = exons;
		this.tfbs = tfbs;
		this.annotation = annotation;
	}

	public boolean unconfirmedStart() {
		return (this.getFlags() != null && this.getFlags().contains("cds_start_NF"));
	}

	public boolean unconfirmedEnd() {
		return (this.getFlags() != null && this.getFlags().contains("cds_end_NF"));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Transcript{");
		sb.append("id='").append(id).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", chromosome='").append(chromosome).append('\'');
		sb.append(", start=").append(start);
		sb.append(", end=").append(end);
		sb.append(", strand='").append(strand).append('\'');
		sb.append(", biotype='").append(biotype).append('\'');
		sb.append(", status='").append(status).append('\'');
		sb.append(", genomicCodingStart=").append(genomicCodingStart);
		sb.append(", genomicCodingEnd=").append(genomicCodingEnd);
		sb.append(", cdnaCodingStart=").append(cdnaCodingStart);
		sb.append(", cdnaCodingEnd=").append(cdnaCodingEnd);
		sb.append(", cdsLength=").append(cdsLength);
		sb.append(", cDnaSequence='").append(cDnaSequence).append('\'');
		sb.append(", proteinId='").append(proteinId).append('\'');
		sb.append(", proteinSequence='").append(proteinSequence).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", version=").append(version);
		sb.append(", source='").append(source).append('\'');
		sb.append(", exons=").append(exons);
		sb.append(", xrefs=").append(xrefs);
		sb.append(", tfbs=").append(tfbs);
		sb.append(", annotationFlags=").append(flags);
		sb.append(", annotation=").append(annotation);
		sb.append('}');
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public Transcript setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Transcript setName(String name) {
		this.name = name;
		return this;
	}

	public String getBiotype() {
		return biotype;
	}

	public Transcript setBiotype(String biotype) {
		this.biotype = biotype;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public Transcript setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getChromosome() {
		return chromosome;
	}

	public Transcript setChromosome(String chromosome) {
		this.chromosome = chromosome;
		return this;
	}

	public int getStart() {
		return start;
	}

	public Transcript setStart(int start) {
		this.start = start;
		return this;
	}

	public int getEnd() {
		return end;
	}

	public Transcript setEnd(int end) {
		this.end = end;
		return this;
	}

	public String getStrand() {
		return strand;
	}

	public Transcript setStrand(String strand) {
		this.strand = strand;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public Transcript setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getSource() {
		return source;
	}

	public Transcript setSource(String source) {
		this.source = source;
		return this;
	}

	public int getGenomicCodingStart() {
		return genomicCodingStart;
	}

	public Transcript setGenomicCodingStart(int genomicCodingStart) {
		this.genomicCodingStart = genomicCodingStart;
		return this;
	}

	public int getGenomicCodingEnd() {
		return genomicCodingEnd;
	}

	public Transcript setGenomicCodingEnd(int genomicCodingEnd) {
		this.genomicCodingEnd = genomicCodingEnd;
		return this;
	}

	public int getCdnaCodingStart() {
		return cdnaCodingStart;
	}

	public Transcript setCdnaCodingStart(int cdnaCodingStart) {
		this.cdnaCodingStart = cdnaCodingStart;
		return this;
	}

	public int getCdnaCodingEnd() {
		return cdnaCodingEnd;
	}

	public Transcript setCdnaCodingEnd(int cdnaCodingEnd) {
		this.cdnaCodingEnd = cdnaCodingEnd;
		return this;
	}

	public int getCdsLength() {
		return cdsLength;
	}

	public Transcript setCdsLength(int cdsLength) {
		this.cdsLength = cdsLength;
		return this;
	}

	public String getProteinId() {
		return proteinId;
	}

	public Transcript setProteinId(String proteinId) {
		this.proteinId = proteinId;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Transcript setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getProteinSequence() {
		return proteinSequence;
	}

	public Transcript setProteinSequence(String proteinSequence) {
		this.proteinSequence = proteinSequence;
		return this;
	}

	public String getcDnaSequence() {
		return cDnaSequence;
	}

	public Transcript setcDnaSequence(String cDnaSequence) {
		this.cDnaSequence = cDnaSequence;
		return this;
	}

	public List<Xref> getXrefs() {
		return xrefs;
	}

	public Transcript setXrefs(List<Xref> xrefs) {
		this.xrefs = xrefs;
		return this;
	}

	public List<TranscriptTfbs> getTfbs() {
		return tfbs;
	}

	public Transcript setTfbs(List<TranscriptTfbs> tfbs) {
		this.tfbs = tfbs;
		return this;
	}

	public List<Exon> getExons() {
		return exons;
	}

	public Transcript setExons(List<Exon> exons) {
		this.exons = exons;
		return this;
	}

	public Set<String> getFlags() {
		return flags;
	}

	public Transcript setFlags(Set<String> flags) {
		this.flags = flags;
		return this;
	}

	public TranscriptAnnotation getAnnotation() {
		return annotation;
	}

	public Transcript setAnnotation(TranscriptAnnotation annotation) {
		this.annotation = annotation;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Transcript)) return false;
		Transcript that = (Transcript) o;
		return getStart() == that.getStart() &&
				getEnd() == that.getEnd() &&
				getGenomicCodingStart() == that.getGenomicCodingStart() &&
				getGenomicCodingEnd() == that.getGenomicCodingEnd() &&
				getCdnaCodingStart() == that.getCdnaCodingStart() &&
				getCdnaCodingEnd() == that.getCdnaCodingEnd() &&
				getCdsLength() == that.getCdsLength() &&
				getVersion() == that.getVersion() &&
				Objects.equals(getId(), that.getId()) &&
				Objects.equals(getName(), that.getName()) &&
				Objects.equals(getChromosome(), that.getChromosome()) &&
				Objects.equals(getStrand(), that.getStrand()) &&
				Objects.equals(getBiotype(), that.getBiotype()) &&
				Objects.equals(getStatus(), that.getStatus()) &&
				Objects.equals(getcDnaSequence(), that.getcDnaSequence()) &&
				Objects.equals(getProteinId(), that.getProteinId()) &&
				Objects.equals(getProteinSequence(), that.getProteinSequence()) &&
				Objects.equals(getDescription(), that.getDescription()) &&
				Objects.equals(getSource(), that.getSource()) &&
				Objects.equals(getExons(), that.getExons()) &&
				Objects.equals(getXrefs(), that.getXrefs()) &&
				Objects.equals(getTfbs(), that.getTfbs()) &&
				Objects.equals(getFlags(), that.getFlags()) &&
				Objects.equals(getAnnotation(), that.getAnnotation());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getChromosome(), getStart(), getEnd(), getStrand(), getBiotype(), getStatus(), getGenomicCodingStart(), getGenomicCodingEnd(), getCdnaCodingStart(), getCdnaCodingEnd(), getCdsLength(), getcDnaSequence(), getProteinId(), getProteinSequence(), getDescription(), getVersion(), getSource(), getExons(), getXrefs(), getTfbs(), getFlags(), getAnnotation());
	}
}
