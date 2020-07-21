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
import java.util.Objects;


public class Exon implements Serializable{

	private String id;
	private String chromosome;
	private int start;
	private int end;
	private String strand;
	private int genomicCodingStart;
	private int genomicCodingEnd;
	private int cdnaCodingStart;
	private int cdnaCodingEnd;
	private int cdsStart;
	private int cdsEnd;
	private int phase;
	private int exonNumber;
	private String sequence;

	private static final long serialVersionUID = -6453125614383801773L;

	public Exon() {
	}

	@Deprecated
	public Exon(String id, String chromosome, Integer start, Integer end, String strand, Integer genomicCodingStart,
				Integer genomicCodingEnd, Integer cdnaCodingStart, Integer cdnaCodingEnd, Integer cdsStart, Integer cdsEnd,
				Integer phase, Integer exonNumber, String sequence) {
		super();
		this.id = id;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.genomicCodingStart = genomicCodingStart;
		this.genomicCodingEnd = genomicCodingEnd;
		this.cdnaCodingStart = cdnaCodingStart;
		this.cdnaCodingEnd = cdnaCodingEnd;
		this.cdsStart = cdsStart;
		this.cdsEnd = cdsEnd;
		this.phase = phase;
		this.exonNumber = exonNumber;
		this.sequence = sequence;
	}

	public Exon(String id, String chromosome, int start, int end, String strand, int genomicCodingStart, int genomicCodingEnd,
				int cdnaCodingStart, int cdnaCodingEnd, int cdsStart, int cdsEnd, int phase, int exonNumber, String sequence) {
		this.id = id;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.genomicCodingStart = genomicCodingStart;
		this.genomicCodingEnd = genomicCodingEnd;
		this.cdnaCodingStart = cdnaCodingStart;
		this.cdnaCodingEnd = cdnaCodingEnd;
		this.cdsStart = cdsStart;
		this.cdsEnd = cdsEnd;
		this.phase = phase;
		this.exonNumber = exonNumber;
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Exon{");
		sb.append("id='").append(id).append('\'');
		sb.append(", chromosome='").append(chromosome).append('\'');
		sb.append(", start=").append(start);
		sb.append(", end=").append(end);
		sb.append(", strand='").append(strand).append('\'');
		sb.append(", genomicCodingStart=").append(genomicCodingStart);
		sb.append(", genomicCodingEnd=").append(genomicCodingEnd);
		sb.append(", cdnaCodingStart=").append(cdnaCodingStart);
		sb.append(", cdnaCodingEnd=").append(cdnaCodingEnd);
		sb.append(", cdsStart=").append(cdsStart);
		sb.append(", cdsEnd=").append(cdsEnd);
		sb.append(", phase=").append(phase);
		sb.append(", exonNumber=").append(exonNumber);
		sb.append(", sequence='").append(sequence).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public Exon setId(String id) {
		this.id = id;
		return this;
	}

	public String getChromosome() {
		return chromosome;
	}

	public Exon setChromosome(String chromosome) {
		this.chromosome = chromosome;
		return this;
	}

	public int getStart() {
		return start;
	}

	public Exon setStart(int start) {
		this.start = start;
		return this;
	}

	public int getEnd() {
		return end;
	}

	public Exon setEnd(int end) {
		this.end = end;
		return this;
	}

	public String getStrand() {
		return strand;
	}

	public Exon setStrand(String strand) {
		this.strand = strand;
		return this;
	}

	public int getGenomicCodingStart() {
		return genomicCodingStart;
	}

	public Exon setGenomicCodingStart(int genomicCodingStart) {
		this.genomicCodingStart = genomicCodingStart;
		return this;
	}

	public int getGenomicCodingEnd() {
		return genomicCodingEnd;
	}

	public Exon setGenomicCodingEnd(int genomicCodingEnd) {
		this.genomicCodingEnd = genomicCodingEnd;
		return this;
	}

	public int getCdnaCodingStart() {
		return cdnaCodingStart;
	}

	public Exon setCdnaCodingStart(int cdnaCodingStart) {
		this.cdnaCodingStart = cdnaCodingStart;
		return this;
	}

	public int getCdnaCodingEnd() {
		return cdnaCodingEnd;
	}

	public Exon setCdnaCodingEnd(int cdnaCodingEnd) {
		this.cdnaCodingEnd = cdnaCodingEnd;
		return this;
	}

	public int getCdsStart() {
		return cdsStart;
	}

	public Exon setCdsStart(int cdsStart) {
		this.cdsStart = cdsStart;
		return this;
	}

	public int getCdsEnd() {
		return cdsEnd;
	}

	public Exon setCdsEnd(int cdsEnd) {
		this.cdsEnd = cdsEnd;
		return this;
	}

	public int getPhase() {
		return phase;
	}

	public Exon setPhase(int phase) {
		this.phase = phase;
		return this;
	}

	public int getExonNumber() {
		return exonNumber;
	}

	public Exon setExonNumber(int exonNumber) {
		this.exonNumber = exonNumber;
		return this;
	}

	public String getSequence() {
		return sequence;
	}

	public Exon setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Exon)) return false;
		Exon exon = (Exon) o;
		return getStart() == exon.getStart() &&
				getEnd() == exon.getEnd() &&
				getGenomicCodingStart() == exon.getGenomicCodingStart() &&
				getGenomicCodingEnd() == exon.getGenomicCodingEnd() &&
				getCdnaCodingStart() == exon.getCdnaCodingStart() &&
				getCdnaCodingEnd() == exon.getCdnaCodingEnd() &&
				getCdsStart() == exon.getCdsStart() &&
				getCdsEnd() == exon.getCdsEnd() &&
				getPhase() == exon.getPhase() &&
				getExonNumber() == exon.getExonNumber() &&
				Objects.equals(getId(), exon.getId()) &&
				Objects.equals(getChromosome(), exon.getChromosome()) &&
				Objects.equals(getStrand(), exon.getStrand()) &&
				Objects.equals(getSequence(), exon.getSequence());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getChromosome(), getStart(), getEnd(), getStrand(), getGenomicCodingStart(), getGenomicCodingEnd(), getCdnaCodingStart(), getCdnaCodingEnd(), getCdsStart(), getCdsEnd(), getPhase(), getExonNumber(), getSequence());
	}
}
