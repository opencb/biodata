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

import java.util.Objects;

public class Constraint {

    private String source;
    private String method;
    private String name;
    private double value;

    public Constraint() {
    }

    public Constraint(String source, String method, String name, double value) {
        this.source = source;
        this.method = method;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Constraint{");
        sb.append("source='").append(source).append('\'');
        sb.append(", method='").append(method).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constraint that = (Constraint) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(source, that.source) &&
                Objects.equals(method, that.method) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, method, name, value);
    }

    public String getSource() {
        return source;
    }

    public Constraint setSource(String source) {
        this.source = source;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Constraint setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getName() {
        return name;
    }

    public Constraint setName(String name) {
        this.name = name;
        return this;
    }

    public double getValue() {
        return value;
    }

    public Constraint setValue(double value) {
        this.value = value;
        return this;
    }
}
