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

public class MiRnaMature {

    private String accession;
    private String id;
    private String sequence;
    private int start;
    private int end;

    public MiRnaMature() {

    }

    public MiRnaMature(String accession, String id, String sequence, int start, int end) {
        this.accession = accession;
        this.id = id;
        this.sequence = sequence;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MiRnaMature{");
        sb.append("accession='").append(accession).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", sequence='").append(sequence).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }

    public String getAccession() {
        return accession;
    }

    public MiRnaMature setAccession(String accession) {
        this.accession = accession;
        return this;
    }

    public String getId() {
        return id;
    }

    public MiRnaMature setId(String id) {
        this.id = id;
        return this;
    }

    public String getSequence() {
        return sequence;
    }

    public MiRnaMature setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }

    public int getStart() {
        return start;
    }

    public MiRnaMature setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public MiRnaMature setEnd(int end) {
        this.end = end;
        return this;
    }
}
