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

import org.opencb.biodata.tools.sequence.tasks.SequenceStats;

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
		mappingQualityMap = new HashMap<Integer, Integer> ();

		accInsert = 0;
		insertMap = new HashMap<Integer, Integer> ();

//		pos = 0;
//		cigar = null;
		
		seqStats = new SequenceStats();
	}

	public String toJSON() {
		int i, size;
		StringBuilder res = new StringBuilder();
		res.append("{");
		res.append("\"num_mapped\": " + numMapped);
		res.append(", \"num_unmapped\": " + numUnmapped);
		res.append(", \"num_paired\": " + numPaired);
		res.append(", \"num_mapped_first\": " + numMappedFirst);
		res.append(", \"num_mapped_second\": " + numMappedSecond);

		res.append(", \"num_mismatches\": " + NM);

		res.append(", \"num_hard_clipping\": " + numHardC);
		res.append(", \"num_soft_clipping\": " + numSoftC);
		res.append(", \"num_insertion\": " + numIn);
		res.append(", \"num_deletion\": " + numDel);
		res.append(", \"num_padding\": " + numPad);
		res.append(", \"num_skip\": " + numSkip);

		size = mappingQualityMap.size();
		res.append(", \"mapping_quality_mean\": " + (accMappingQuality / numMapped));
		res.append(", \"mapping_quality_map_size\": " + size);
		res.append(", \"mapping_quality_map_values\": [");
		i = 0;
		for(int key:mappingQualityMap.keySet()) {
			res.append("[" + key + ", " + mappingQualityMap.get(key) + "]");
			if (++i < size) res.append(", ");
		}
		res.append("]");

		if (numPaired > 0) {
			size = insertMap.size();
			res.append(", \"insert_mean\": " + (accInsert / numPaired));
			res.append(", \"insert_map_size\": " + size);
			res.append(", \"insert_map_values\": [");
			i = 0;
			for(int key:insertMap.keySet()) {
				res.append("[" + key + ", " + insertMap.get(key) + "]");
				if (++i < size) res.append(", ");
			}
			res.append("]");
		}

		res.append(", \"read_stats\": " + seqStats.toJSON());
		res.append("}");
		return res.toString();
	}

}
