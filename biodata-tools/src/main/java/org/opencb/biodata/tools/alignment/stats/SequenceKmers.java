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

package org.opencb.biodata.tools.alignment.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SequenceKmers {

	public int kvalue;
	public Map<String, Integer> kmersMap;

	public SequenceKmers() {
		this(0);
	}

	public SequenceKmers(int k) {
		kvalue = k;
		kmersMap = new HashMap<>();
	}

	public int getKvalue() {
		return kvalue;
	}

	public void setKvalue(int k) {
		kvalue = k;
	}

	public String toJSON() throws IOException {
		ObjectWriter objectWriter = new ObjectMapper().writer();
		return objectWriter.writeValueAsString(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SequenceKmers{");
		sb.append("kvalue=").append(kvalue);
		sb.append(", kmersMap=").append(kmersMap);
		sb.append('}');
		return sb.toString();
	}
}
