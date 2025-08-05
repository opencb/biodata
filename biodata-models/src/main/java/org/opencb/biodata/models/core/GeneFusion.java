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

import org.opencb.biodata.models.core.chimerdb.ChimerKb;
import org.opencb.biodata.models.core.chimerdb.ChimerPub;
import org.opencb.biodata.models.core.chimerdb.ChimerSeq;

import java.util.ArrayList;
import java.util.List;

public class GeneFusion {

    private List<ChimerKb> chimerKb;
    private List<ChimerPub> chimerPub;
    private List<ChimerSeq> chimerSeq;

    public GeneFusion() {
        this.chimerKb = new ArrayList<>();
        this.chimerPub = new ArrayList<>();
        this.chimerSeq = new ArrayList<>();
    }

    public GeneFusion(List<ChimerKb> chimerKb, List<ChimerPub> chimerPub, List<ChimerSeq> chimerSeq) {
        this.chimerKb = chimerKb;
        this.chimerPub = chimerPub;
        this.chimerSeq = chimerSeq;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneFusion{");
        sb.append("chimerKb=").append(chimerKb);
        sb.append(", chimerPub=").append(chimerPub);
        sb.append(", chimerSeq=").append(chimerSeq);
        sb.append('}');
        return sb.toString();
    }

    public List<ChimerKb> getChimerKb() {
        return chimerKb;
    }

    public GeneFusion setChimerKb(List<ChimerKb> chimerKb) {
        this.chimerKb = chimerKb;
        return this;
    }

    public List<ChimerPub> getChimerPub() {
        return chimerPub;
    }

    public GeneFusion setChimerPub(List<ChimerPub> chimerPub) {
        this.chimerPub = chimerPub;
        return this;
    }

    public List<ChimerSeq> getChimerSeq() {
        return chimerSeq;
    }

    public GeneFusion setChimerSeq(List<ChimerSeq> chimerSeq) {
        this.chimerSeq = chimerSeq;
        return this;
    }
}
