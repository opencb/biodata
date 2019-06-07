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

package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.core.Xref;

import java.util.List;

public class GenomicFeature {
    private String id;
    private String type; // GENE, VARIANT, REGION,...
    private String transcriptId;
    private String geneName;
    private List<Xref> xrefs;

    public GenomicFeature() {
    }

    public GenomicFeature(String id, String type, String transcriptId, String geneName, List<Xref> xrefs) {
        this.id = id;
        this.type = type;
        this.transcriptId = transcriptId;
        this.geneName = geneName;
        this.xrefs = xrefs;
    }

    public String getId() {
        return id;
    }

    public GenomicFeature setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public GenomicFeature setType(String type) {
        this.type = type;
        return this;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public GenomicFeature setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
        return this;
    }

    public String getGeneName() {
        return geneName;
    }

    public GenomicFeature setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public List<Xref> getXrefs() {
        return xrefs;
    }

    public GenomicFeature setXrefs(List<Xref> xrefs) {
        this.xrefs = xrefs;
        return this;
    }
}
