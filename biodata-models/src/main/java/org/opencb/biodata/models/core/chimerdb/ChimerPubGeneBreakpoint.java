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

public class ChimerPubGeneBreakpoint {

    private String geneName;
    private String highlight;

    public ChimerPubGeneBreakpoint() {
    }

    public ChimerPubGeneBreakpoint(String geneName, String highlight) {
        this.geneName = geneName;
        this.highlight = highlight;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChimerPubGeneBreakpoint{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", highlight='").append(highlight).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getGeneName() {
        return geneName;
    }

    public ChimerPubGeneBreakpoint setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getHighlight() {
        return highlight;
    }

    public ChimerPubGeneBreakpoint setHighlight(String highlight) {
        this.highlight = highlight;
        return this;
    }
}
