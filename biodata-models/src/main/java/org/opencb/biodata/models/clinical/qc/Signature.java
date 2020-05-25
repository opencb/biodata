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

package org.opencb.biodata.models.clinical.qc;

import java.util.Arrays;

public class Signature {

    /**
     * Variant type, e.g. SNV, INDEL, ...
     */
    private String type;
    private Count[] counts;

    public Signature() {
    }

    public Signature(String type, Count[] counts) {
        this.type = type;
        this.counts = counts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Signature{");
        sb.append("type='").append(type).append('\'');
        sb.append(", counts=").append(Arrays.toString(counts));
        sb.append('}');
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public Signature setType(String type) {
        this.type = type;
        return this;
    }

    public Count[] getCounts() {
        return counts;
    }

    public Signature setCounts(Count[] counts) {
        this.counts = counts;
        return this;
    }

    public static class Count {

        private String context;
        private int total;

        public Count() {
        }

        public Count(String context, int total) {
            this.context = context;
            this.total = total;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Count{");
            sb.append("context='").append(context).append('\'');
            sb.append(", total=").append(total);
            sb.append('}');
            return sb.toString();
        }

        public String getContext() {
            return context;
        }

        public Count setContext(String context) {
            this.context = context;
            return this;
        }

        public int getTotal() {
            return total;
        }

        public Count setTotal(int total) {
            this.total = total;
            return this;
        }
    }
}
