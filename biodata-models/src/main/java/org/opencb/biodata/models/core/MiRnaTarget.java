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

import java.util.List;

public class MiRnaTarget {

    String id;
    String sourceId; // tarbase ID
    List<TargetGene> targets;
    String source; // mirTarbase

    public MiRnaTarget(String id, String sourceId, List<TargetGene> targets, String source) {
        this.id = id;
        this.sourceId = sourceId;
        this.targets = targets;
        this.source = source;
    }

    public MiRnaTarget() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MiRnaTarget{");
        sb.append("id='").append(id).append('\'');
        sb.append(", sourceId='").append(sourceId).append('\'');
        sb.append(", targets=").append(targets);
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public MiRnaTarget setId(String id) {
        this.id = id;
        return this;
    }

    public String getSourceId() {
        return sourceId;
    }

    public MiRnaTarget setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public List<TargetGene> getTargets() {
        return targets;
    }

    public MiRnaTarget setTargets(List<TargetGene> targets) {
        this.targets = targets;
        return this;
    }

    public String getSource() {
        return source;
    }

    public MiRnaTarget setSource(String source) {
        this.source = source;
        return this;
    }
}
