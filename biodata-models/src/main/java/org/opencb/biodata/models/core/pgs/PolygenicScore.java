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

package org.opencb.biodata.models.core.pgs;

import java.util.HashMap;
import java.util.Map;

public class PolygenicScore {

    private String id;
    private Map<String, String> values;

    public PolygenicScore() {
        this.values = new HashMap<>();
    }

    public PolygenicScore(String id, Map<String, String> values) {
        this.id = id;
        this.values = values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolygenicScore{");
        sb.append("id='").append(id).append('\'');
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PolygenicScore setId(String id) {
        this.id = id;
        return this;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public PolygenicScore setValues(Map<String, String> values) {
        this.values = values;
        return this;
    }
}
