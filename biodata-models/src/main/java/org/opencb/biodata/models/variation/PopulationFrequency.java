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

package org.opencb.biodata.models.variation;

public class PopulationFrequency {

	private String study;
	private String pop;
	private String superPop;
	private String refAllele;
	private String altAllele;
	private float refAlleleFreq;
	private float altAlleleFreq;
	private float refHomGenotypeFreq;
	private float hetGenotypeFreq;
	private float altHomGenotypeFreq;

	public PopulationFrequency() {
	}

	public PopulationFrequency(String pop, String refAllele, String altAllele, float refAlleleFreq,
			float altAlleleFreq) {
		this(null, pop, null, refAllele, altAllele, refAlleleFreq, altAlleleFreq);
	}

	public PopulationFrequency(String study, String pop, String superPop, String refAllele, String altAllele, float refAlleleFreq,
			float altAlleleFreq) {
		this.study = study;
		this.pop = pop;
		this.superPop = superPop;
		this.refAllele = refAllele;
		this.altAllele = altAllele;
		this.refAlleleFreq = refAlleleFreq;
		this.altAlleleFreq = altAlleleFreq;
	}

	public PopulationFrequency(String pop, String superPop, String refAllele, String altAllele) {
		this.pop = pop;
		this.superPop = superPop;
		this.refAllele = refAllele;
		this.altAllele = altAllele;
	}

	public String getPop() {
		return pop;
	}

	public void setPop(String pop) {
		this.pop = pop;
	}

	public String getRefAllele() {
		return refAllele;
	}

	public void setRefAllele(String refAllele) {
		this.refAllele = refAllele;
	}

	public String getAltAllele() {
		return altAllele;
	}

	public void setAltAllele(String altAllele) {
		this.altAllele = altAllele;
	}

	public double getRefAlleleFreq() {
		return refAlleleFreq;
	}

	public void setRefAlleleFreq(float refAlleleFreq) {
		this.refAlleleFreq = refAlleleFreq;
	}

	public double getAltAlleleFreq() {
		return altAlleleFreq;
	}

	public void setAltAlleleFreq(float altAlleleFreq) {
		this.altAlleleFreq = altAlleleFreq;
	}

	public String getSuperPop() {
		return superPop;
	}

	public void setSuperPop(String superPop) {
		this.superPop = superPop;
	}

	public float getRefHomGenotypeFreq() {
		return refHomGenotypeFreq;
	}

	public void setRefHomGenotypeFreq(float refHomGenotypeFreq) {
		this.refHomGenotypeFreq = refHomGenotypeFreq;
	}

	public float getHetGenotypeFreq() {
		return hetGenotypeFreq;
	}

	public void setHetGenotypeFreq(float hetGenotypeFreq) {
		this.hetGenotypeFreq = hetGenotypeFreq;
	}

	public float getAltHomGenotypeFreq() { return altHomGenotypeFreq; }

	public void setAltHomGenotypeFreq(float altHomGenotypeFreq) {
		this.altHomGenotypeFreq = altHomGenotypeFreq;
	}

	public String getStudy() { return study; }

	public void setStudy(String study) { this.study = study; }

}
