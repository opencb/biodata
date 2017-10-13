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

package org.opencb.biodata.models.core.pedigree;

/**
 * Created by imedina on 10/10/16.
 */
@Deprecated
public class VariableField {

    private String id;
    private VariableType type;

    public enum VariableType {
        BOOLEAN,
        INTEGER,
        DOUBLE,
        STRING
    }

    public VariableField() {
    }

    public VariableField(String id, VariableType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariableField{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public VariableField setId(String id) {
        this.id = id;
        return this;
    }

    public VariableType getType() {
        return type;
    }

    public VariableField setType(VariableType type) {
        this.type = type;
        return this;
    }
}
