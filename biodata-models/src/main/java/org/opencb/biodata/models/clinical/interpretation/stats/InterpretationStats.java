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

package org.opencb.biodata.models.clinical.interpretation.stats;

import java.util.HashMap;
import java.util.Map;

public class InterpretationStats {

    private Map<String, Long> panels;
    private Map<String, Long> methods;

    public InterpretationStats() {
        this.panels = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public InterpretationStats(Map<String, Long> panels, Map<String, Long> methods) {
        this.panels = panels;
        this.methods = methods;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InterpretationStats{");
        sb.append("panels=").append(panels);
        sb.append(", methods=").append(methods);
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Long> getPanels() {
        return panels;
    }

    public InterpretationStats setPanels(Map<String, Long> panels) {
        this.panels = panels;
        return this;
    }

    public Map<String, Long> getMethods() {
        return methods;
    }

    public InterpretationStats setMethods(Map<String, Long> methods) {
        this.methods = methods;
        return this;
    }
}
