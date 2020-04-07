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

import org.opencb.biodata.models.variant.avro.Cytoband;

import java.util.List;

public class Chromosome {

	private String name;
	private int start;
	private int end;
	private int size;
	private int isCircular;
	private int numberGenes;
	private List<Cytoband> cytobands;

	public Chromosome() {
	}

	public Chromosome(String name, int start, int end, int size, int isCircular, int numberGenes, List<Cytoband> cytobands) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.size = size;
		this.isCircular = isCircular;
		this.numberGenes = numberGenes;
		this.cytobands = cytobands;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Chromosome{");
		sb.append("name='").append(name).append('\'');
		sb.append(", start=").append(start);
		sb.append(", end=").append(end);
		sb.append(", size=").append(size);
		sb.append(", isCircular=").append(isCircular);
		sb.append(", numberGenes=").append(numberGenes);
		sb.append(", cytobands=").append(cytobands);
		sb.append('}');
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public Chromosome setName(String name) {
		this.name = name;
		return this;
	}

	public int getStart() {
		return start;
	}

	public Chromosome setStart(int start) {
		this.start = start;
		return this;
	}

	public int getEnd() {
		return end;
	}

	public Chromosome setEnd(int end) {
		this.end = end;
		return this;
	}

	public int getSize() {
		return size;
	}

	public Chromosome setSize(int size) {
		this.size = size;
		return this;
	}

	public int getIsCircular() {
		return isCircular;
	}

	public Chromosome setIsCircular(int isCircular) {
		this.isCircular = isCircular;
		return this;
	}

	public int getNumberGenes() {
		return numberGenes;
	}

	public Chromosome setNumberGenes(int numberGenes) {
		this.numberGenes = numberGenes;
		return this;
	}

	public List<Cytoband> getCytobands() {
		return cytobands;
	}

	public Chromosome setCytobands(List<Cytoband> cytobands) {
		this.cytobands = cytobands;
		return this;
	}
}
