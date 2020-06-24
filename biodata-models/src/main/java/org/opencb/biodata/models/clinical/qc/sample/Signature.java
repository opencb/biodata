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

package org.opencb.biodata.models.clinical.qc.sample;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Signature {

    private String id;
    private Map<String, String> query;
    /**
     * Variant type, e.g. SNV, INDEL, ...
     */
    private String type;
    private SignatureCount[] counts;
    private List<String> files;

    public Signature() {
    }

    @Deprecated
    public Signature(String type, SignatureCount[] counts) {
        this.type = type;
        this.counts = counts;
    }

    public Signature(String id, Map<String, String> query, String type, SignatureCount[] counts, List<String> files) {
        this.id = id;
        this.query = query;
        this.type = type;
        this.counts = counts;
        this.files = files;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Signature{");
        sb.append("id='").append(id).append('\'');
        sb.append(", query=").append(query);
        sb.append(", type='").append(type).append('\'');
        sb.append(", counts=").append(Arrays.toString(counts));
        sb.append(", files=").append(files);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Signature setId(String id) {
        this.id = id;
        return this;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public Signature setQuery(Map<String, String> query) {
        this.query = query;
        return this;
    }

    public String getType() {
        return type;
    }

    public Signature setType(String type) {
        this.type = type;
        return this;
    }

    public SignatureCount[] getCounts() {
        return counts;
    }

    public Signature setCounts(SignatureCount[] counts) {
        this.counts = counts;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public Signature setFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public static class SignatureCount {

        private String context;
        private int total;

        public SignatureCount() {
        }

        public SignatureCount(String context, int total) {
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

        public SignatureCount setContext(String context) {
            this.context = context;
            return this;
        }

        public int getTotal() {
            return total;
        }

        public SignatureCount setTotal(int total) {
            this.total = total;
            return this;
        }
    }
}
