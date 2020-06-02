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

public class GenomeSequenceChunk {

	private String sequenceName;
	private String chunkId;
	private int start;
	private int end;
    private String sequenceType;
    private String assembly;
	private String sequence;

	
	public GenomeSequenceChunk(String sequenceName, String chunkId, int start, int end, String sequence) {
        this(sequenceName, chunkId, start, end, "", "", sequence);
	}

    public GenomeSequenceChunk(String sequenceName, String chunkId, int start, int end, String sequenceType, String assembly, String sequence) {
		this.sequenceName = sequenceName;
		this.chunkId = chunkId;
		this.start = start;
		this.end = end;
        this.sequenceType = sequenceType;
        this.assembly = assembly;
		this.sequence = sequence;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public GenomeSequenceChunk setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
		return this;
	}

	public String getChunkId() {
		return chunkId;
	}

	public GenomeSequenceChunk setChunkId(String chunkId) {
		this.chunkId = chunkId;
		return this;
	}

	public int getStart() {
		return start;
	}

	public GenomeSequenceChunk setStart(int start) {
		this.start = start;
		return this;
	}

	public int getEnd() {
		return end;
	}

	public GenomeSequenceChunk setEnd(int end) {
		this.end = end;
		return this;
	}

	public String getSequenceType() {
		return sequenceType;
	}

	public GenomeSequenceChunk setSequenceType(String sequenceType) {
		this.sequenceType = sequenceType;
		return this;
	}

	public String getAssembly() {
		return assembly;
	}

	public GenomeSequenceChunk setAssembly(String assembly) {
		this.assembly = assembly;
		return this;
	}

	public String getSequence() {
		return sequence;
	}

	public GenomeSequenceChunk setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}
}
