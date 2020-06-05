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

import java.util.ArrayList;
import java.util.List;

public class MiRnaGene {

	/**
	 * miRBase accession, e.g. MI0000001
	 */
	private String accession;
	/**
	 * miRBase id, example: hsa-let-7a-1
	 */
	private String id;
	/**
	 * status, e.g. UNCHANGED
	 */
	private String status;
	private String sequence;
	private List<MiRnaMature> matures;

	public MiRnaGene() {

	}

	public MiRnaGene(String accession, String id, String status, String sequence, List<MiRnaMature> matures) {
		this.accession = accession;
		this.id = id;
		this.status = status;
		this.sequence = sequence;
		this.matures = matures;
	}

	@Deprecated
	public void addMiRNAMature(String accession, String id, String sequence, int start, int end) {
		if (matures == null) {
			matures = new ArrayList<>();
		}
		matures.add(new MiRnaMature(accession, id, sequence, start, end));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MiRnaGene{");
		sb.append("accession='").append(accession).append('\'');
		sb.append(", id='").append(id).append('\'');
		sb.append(", status='").append(status).append('\'');
		sb.append(", sequence='").append(sequence).append('\'');
		sb.append(", matures=").append(matures);
		sb.append('}');
		return sb.toString();
	}

	public String getAccession() {
		return accession;
	}

	public MiRnaGene setAccession(String accession) {
		this.accession = accession;
		return this;
	}

	public String getId() {
		return id;
	}

	public MiRnaGene setId(String id) {
		this.id = id;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public MiRnaGene setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getSequence() {
		return sequence;
	}

	public MiRnaGene setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}

	public List<MiRnaMature> getMatures() {
		return matures;
	}

	public MiRnaGene setMatures(List<MiRnaMature> matures) {
		this.matures = matures;
		return this;
	}
}
