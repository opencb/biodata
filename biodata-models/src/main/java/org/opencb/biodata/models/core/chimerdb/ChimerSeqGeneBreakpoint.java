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

package org.opencb.biodata.models.core.chimerdb;

public class ChimerSeqGeneBreakpoint extends ChimerKbGeneBreakpoint {

    private String locus;
    private boolean kinase;
    private boolean oncogene;
    private boolean tumorSuppressor;
    private boolean receptor;
    private boolean transcriptionFactor;

    public ChimerSeqGeneBreakpoint() {
        super();
    }

    public ChimerSeqGeneBreakpoint(String geneName, String chromosome, int position, String strand, String locus, boolean kinase,
                                   boolean oncogene, boolean tumorSuppressor, boolean receptor, boolean transcriptionFactor) {
        super(geneName, chromosome, position, strand);
        this.locus = locus;
        this.kinase = kinase;
        this.oncogene = oncogene;
        this.tumorSuppressor = tumorSuppressor;
        this.receptor = receptor;
        this.transcriptionFactor = transcriptionFactor;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChimerSeqGeneBreakpoint{");
        sb.append("locus='").append(locus).append('\'');
        sb.append(", kinase=").append(kinase);
        sb.append(", oncogene=").append(oncogene);
        sb.append(", tumorSuppressor=").append(tumorSuppressor);
        sb.append(", receptor=").append(receptor);
        sb.append(", transcriptionFactor=").append(transcriptionFactor);
        sb.append(", geneName='").append(geneName).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", strand='").append(strand).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getLocus() {
        return locus;
    }

    public ChimerSeqGeneBreakpoint setLocus(String locus) {
        this.locus = locus;
        return this;
    }

    public boolean isKinase() {
        return kinase;
    }

    public ChimerSeqGeneBreakpoint setKinase(boolean kinase) {
        this.kinase = kinase;
        return this;
    }

    public boolean isOncogene() {
        return oncogene;
    }

    public ChimerSeqGeneBreakpoint setOncogene(boolean oncogene) {
        this.oncogene = oncogene;
        return this;
    }

    public boolean isTumorSuppressor() {
        return tumorSuppressor;
    }

    public ChimerSeqGeneBreakpoint setTumorSuppressor(boolean tumorSuppressor) {
        this.tumorSuppressor = tumorSuppressor;
        return this;
    }

    public boolean isReceptor() {
        return receptor;
    }

    public ChimerSeqGeneBreakpoint setReceptor(boolean receptor) {
        this.receptor = receptor;
        return this;
    }

    public boolean isTranscriptionFactor() {
        return transcriptionFactor;
    }

    public ChimerSeqGeneBreakpoint setTranscriptionFactor(boolean transcriptionFactor) {
        this.transcriptionFactor = transcriptionFactor;
        return this;
    }
}


