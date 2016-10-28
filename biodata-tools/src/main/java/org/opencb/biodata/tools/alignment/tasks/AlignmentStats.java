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

package org.opencb.biodata.tools.alignment.tasks;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.opencb.biodata.tools.sequence.tasks.SequenceStats;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class AlignmentStats {

	public int numMapped;
	public int numUnmapped;
	public int numPaired;
	public int numMappedFirst;
	public int numMappedSecond;

	public int NM;
	
	public int numHardC;
	public int numSoftC;
	public int numIn;
	public int numDel;
	public int numPad;
	public int numSkip;
	
	public int accMappingQuality;
	public HashMap<Integer, Integer> mappingQualityMap;

	public int accInsert;
	public HashMap<Integer, Integer> insertMap;

//	public long pos;
//	public List<CigarUnit> cigar;

	public SequenceStats seqStats;

	public AlignmentStats() {
		numMapped = 0;
		numUnmapped = 0;
		numPaired = 0;
		numMappedFirst = 0;
		numMappedSecond = 0;

		NM = 0;
		
		numHardC = 0;
		numSoftC = 0;
		numIn = 0;
		numDel = 0;
		numPad = 0;
		numSkip = 0;

		accMappingQuality = 0;
		mappingQualityMap = new HashMap<> ();

		accInsert = 0;
		insertMap = new HashMap<> ();

//		pos = 0;
//		cigar = null;
		
		seqStats = new SequenceStats();
	}

	public String toJSON() throws IOException {
		ObjectWriter objectWriter = new ObjectMapper().writer();
		return objectWriter.writeValueAsString(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AlignmentStats{");
		sb.append("numMapped=").append(numMapped);
		sb.append(", numUnmapped=").append(numUnmapped);
		sb.append(", numPaired=").append(numPaired);
		sb.append(", numMappedFirst=").append(numMappedFirst);
		sb.append(", numMappedSecond=").append(numMappedSecond);
		sb.append(", NM=").append(NM);
		sb.append(", numHardC=").append(numHardC);
		sb.append(", numSoftC=").append(numSoftC);
		sb.append(", numIn=").append(numIn);
		sb.append(", numDel=").append(numDel);
		sb.append(", numPad=").append(numPad);
		sb.append(", numSkip=").append(numSkip);
		sb.append(", accMappingQuality=").append(accMappingQuality);
		sb.append(", mappingQualityMap=").append(mappingQualityMap);
		sb.append(", accInsert=").append(accInsert);
		sb.append(", insertMap=").append(insertMap);
		sb.append(", seqStats=").append(seqStats);
		sb.append('}');
		return sb.toString();
	}

	public int getNumMapped() {
		return numMapped;
	}

	public AlignmentStats setNumMapped(int numMapped) {
		this.numMapped = numMapped;
		return this;
	}

	public int getNumUnmapped() {
		return numUnmapped;
	}

	public AlignmentStats setNumUnmapped(int numUnmapped) {
		this.numUnmapped = numUnmapped;
		return this;
	}

	public int getNumPaired() {
		return numPaired;
	}

	public AlignmentStats setNumPaired(int numPaired) {
		this.numPaired = numPaired;
		return this;
	}

	public int getNumMappedFirst() {
		return numMappedFirst;
	}

	public AlignmentStats setNumMappedFirst(int numMappedFirst) {
		this.numMappedFirst = numMappedFirst;
		return this;
	}

	public int getNumMappedSecond() {
		return numMappedSecond;
	}

	public AlignmentStats setNumMappedSecond(int numMappedSecond) {
		this.numMappedSecond = numMappedSecond;
		return this;
	}

	public int getNM() {
		return NM;
	}

	public AlignmentStats setNM(int NM) {
		this.NM = NM;
		return this;
	}

	public int getNumHardC() {
		return numHardC;
	}

	public AlignmentStats setNumHardC(int numHardC) {
		this.numHardC = numHardC;
		return this;
	}

	public int getNumSoftC() {
		return numSoftC;
	}

	public AlignmentStats setNumSoftC(int numSoftC) {
		this.numSoftC = numSoftC;
		return this;
	}

	public int getNumIn() {
		return numIn;
	}

	public AlignmentStats setNumIn(int numIn) {
		this.numIn = numIn;
		return this;
	}

	public int getNumDel() {
		return numDel;
	}

	public AlignmentStats setNumDel(int numDel) {
		this.numDel = numDel;
		return this;
	}

	public int getNumPad() {
		return numPad;
	}

	public AlignmentStats setNumPad(int numPad) {
		this.numPad = numPad;
		return this;
	}

	public int getNumSkip() {
		return numSkip;
	}

	public AlignmentStats setNumSkip(int numSkip) {
		this.numSkip = numSkip;
		return this;
	}

	public int getAccMappingQuality() {
		return accMappingQuality;
	}

	public AlignmentStats setAccMappingQuality(int accMappingQuality) {
		this.accMappingQuality = accMappingQuality;
		return this;
	}

	public HashMap<Integer, Integer> getMappingQualityMap() {
		return mappingQualityMap;
	}

	public AlignmentStats setMappingQualityMap(HashMap<Integer, Integer> mappingQualityMap) {
		this.mappingQualityMap = mappingQualityMap;
		return this;
	}

	public int getAccInsert() {
		return accInsert;
	}

	public AlignmentStats setAccInsert(int accInsert) {
		this.accInsert = accInsert;
		return this;
	}

	public HashMap<Integer, Integer> getInsertMap() {
		return insertMap;
	}

	public AlignmentStats setInsertMap(HashMap<Integer, Integer> insertMap) {
		this.insertMap = insertMap;
		return this;
	}

	public SequenceStats getSeqStats() {
		return seqStats;
	}

	public AlignmentStats setSeqStats(SequenceStats seqStats) {
		this.seqStats = seqStats;
		return this;
	}

}
