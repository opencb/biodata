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

package org.opencb.biodata.tools.alignment.stats;

import java.util.*;

public class SequenceKmers {

	public int kvalue;
	public HashMap<String, Integer> kmersMap;

	public SequenceKmers() {
		this(0);
	}

	public SequenceKmers(int k) {
		kvalue = k;
		kmersMap = new HashMap<String, Integer>();		
	}

	public int getKvalue() {
		return kvalue;
	}

	public void setKvalue(int k) {
		kvalue = k;
	}

	public String toJSON() {
		int key;
		TreeMap<Integer, List<String>> sortedMap = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
		for(Map.Entry entry: kmersMap.entrySet()) {
			key = (Integer) entry.getValue();
			if (!sortedMap.containsKey(key)) {
				sortedMap.put(key, new ArrayList<String>());
			}
			sortedMap.get(key).add((String) entry.getKey());
		}
		
		StringBuilder res = new StringBuilder();
		
		res.append("{\"kvalue\": " + kvalue);
		
		int i, size = kmersMap.size();
		res.append(", \"kmers_values\": [");
		i = 0;
		for(Map.Entry entry: sortedMap.entrySet()) {
			for(String value: (List<String>) entry.getValue()) {
				res.append("[\"" + value + "\", " + (Integer) entry.getKey() + "]");
				if (i >= 99) break;
				if (++i < size) res.append(", ");
			}
			if (i >= 99) break;
		}
		res.append("]}");

		return res.toString();
	}
}
