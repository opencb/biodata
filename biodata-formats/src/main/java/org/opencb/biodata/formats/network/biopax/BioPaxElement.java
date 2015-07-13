/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.network.biopax;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioPaxElement {

    private String Id;
    private String bioPaxClassName;
    private Map<String, List<String>> params = new HashMap<String, List<String>>();

    public BioPaxElement(String Id, String bioPaxClassName) {
        this.Id = Id;
        this.bioPaxClassName = bioPaxClassName;
    }

    public void put(String name, String value) {
        if (!params.containsKey(name)) {
            params.put(name, new ArrayList<String>());
        }
        params.get(name).add(value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bioPaxClassName).append(", id = ").append(Id).append("\n");
        for (String key : params.keySet()) {
            sb.append("\t").append(key).append("\t").append(Joiner.on(",").join(params.get(key))).append("\n");
        }
        return sb.toString();
    }

    public void setBioPaxClassName(String bioPaxClassName) {
        this.bioPaxClassName = bioPaxClassName;
    }

    public String getBioPaxClassName() {
        return bioPaxClassName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }
}
