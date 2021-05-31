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

import org.opencb.commons.datastore.core.ObjectMap;

import java.util.Arrays;
import java.util.List;

public class Signature {

    private String id;
    private String description;
    private ObjectMap query;
    /**
     * Variant type, e.g. SNV, INDEL, ...
     */
    private String type;
    private GenomeContextCount[] counts;
    private List<String> files;

    public Signature() {
    }

    public Signature(String id, String description, ObjectMap query, String type, GenomeContextCount[] counts, List<String> files) {
        this.id = id;
        this.description = description;
        this.query = query;
        this.type = type;
        this.counts = counts;
        this.files = files;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Signature{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
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

    public String getDescription() {
        return description;
    }

    public Signature setDescription(String description) {
        this.description = description;
        return this;
    }

    public ObjectMap getQuery() {
        return query;
    }

    public Signature setQuery(ObjectMap query) {
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

    public GenomeContextCount[] getCounts() {
        return counts;
    }

    public Signature setCounts(GenomeContextCount[] counts) {
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

    public static class GenomeContextCount {

        private String context;
        private int total;

        public GenomeContextCount() {
        }

        public GenomeContextCount(String context, int total) {
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

        public GenomeContextCount setContext(String context) {
            this.context = context;
            return this;
        }

        public int getTotal() {
            return total;
        }

        public GenomeContextCount setTotal(int total) {
            this.total = total;
            return this;
        }
    }
}
