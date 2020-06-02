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

public class GenomeSequenceFeature {

    private String sequenceName;
    private int start;
    private int end;
    private int strand;
    private String sequenceType;
    private String assembly;
    private String sequence;

    public GenomeSequenceFeature() {}

    public GenomeSequenceFeature(String chromosome, int start, int end, String sequence) {
        this.sequenceName = chromosome;
        this.start = start;
        this.end = end;
        this.strand = 1;
        this.sequence = sequence;
    }

    public GenomeSequenceFeature(String chromosome, int start, int end, int strand, String sequence) {
        this.sequenceName = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.sequence = sequence;
    }

    public GenomeSequenceFeature(String chromosome, int start, int end, int strand, String type, String assembly, String sequence) {
        this.sequenceName = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.sequenceType = type;
        this.assembly = assembly;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenomeSequenceFeature{");
        sb.append("sequenceName='").append(sequenceName).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", strand=").append(strand);
        sb.append(", sequenceType='").append(sequenceType).append('\'');
        sb.append(", assembly='").append(assembly).append('\'');
        sb.append(", sequence='").append(sequence).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public GenomeSequenceFeature setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
        return this;
    }

    public int getStart() {
        return start;
    }

    public GenomeSequenceFeature setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public GenomeSequenceFeature setEnd(int end) {
        this.end = end;
        return this;
    }

    public int getStrand() {
        return strand;
    }

    public GenomeSequenceFeature setStrand(int strand) {
        this.strand = strand;
        return this;
    }

    public String getSequenceType() {
        return sequenceType;
    }

    public GenomeSequenceFeature setSequenceType(String sequenceType) {
        this.sequenceType = sequenceType;
        return this;
    }

    public String getAssembly() {
        return assembly;
    }

    public GenomeSequenceFeature setAssembly(String assembly) {
        this.assembly = assembly;
        return this;
    }

    public String getSequence() {
        return sequence;
    }

    public GenomeSequenceFeature setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }
}
