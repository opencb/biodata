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

import org.opencb.biodata.models.constants.FieldConstants;
import org.opencb.commons.annotations.DataField;
import org.opencb.commons.datastore.core.ObjectMap;

import java.util.List;

public class Signature {

    @DataField(id = "id", indexed = true,
            description = FieldConstants.GENERIC_ID_DESCRIPTION)
    private String id;

    @DataField(id = "description", indexed = true,
            description = FieldConstants.GENERIC_DESCRIPTION_DESCRIPTION)
    private String description;

    @DataField(id = "query", indexed = true, uncommentedClasses = {"ObjectMap"},
            description = FieldConstants.GENERIC_QUERY_DESCRIPTION)
    private ObjectMap query;

    @DataField(id = "type", indexed = true,
            description = FieldConstants.SIGNATURE_TYPE_DESCRIPTION)
    private String type;

    @DataField(id = "counts", indexed = true,
            description = FieldConstants.SIGNATURE_COUNTS_DESCRIPTION)
    private List<GenomeContextCount> counts;

    @DataField(id = "files", indexed = true,
            description = FieldConstants.SIGNATURE_FILES_DESCRIPTION)
    private List<String> files;

    @Deprecated
    @DataField(id = "fittingScore", indexed = true,
            description = FieldConstants.SIGNATURE_SIGNATURE_FITTING_SCORE_DESCRIPTION)
    private SignatureFittingScore fittingScore;

    @DataField(id = "fittingScores", indexed = true,
            description = FieldConstants.SIGNATURE_SIGNATURE_FITTING_SCORES_DESCRIPTION)
    private List<SignatureFittingScore> fittingScores;

    public Signature() {
    }

    @Deprecated
    public Signature(String id, String description, ObjectMap query, String type, List<GenomeContextCount> counts, List<String> files,
                     SignatureFittingScore fittingScore) {
        this.id = id;
        this.description = description;
        this.query = query;
        this.type = type;
        this.counts = counts;
        this.files = files;
        this.fittingScore = fittingScore;
    }

    public Signature(String id, String description, ObjectMap query, String type, List<GenomeContextCount> counts, List<String> files,
                     List<SignatureFittingScore> fittingScores) {
        this.id = id;
        this.description = description;
        this.query = query;
        this.type = type;
        this.counts = counts;
        this.files = files;
        this.fittingScores = fittingScores;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Signature{");
        sb.append("id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", query=").append(query);
        sb.append(", type='").append(type).append('\'');
        sb.append(", counts=").append(counts);
        sb.append(", files=").append(files);
        sb.append(", fittingScores=").append(fittingScores);
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

    public List<GenomeContextCount> getCounts() {
        return counts;
    }

    public Signature setCounts(List<GenomeContextCount> counts) {
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

    @Deprecated
    public SignatureFittingScore getFittingScore() {
        return fittingScore;
    }

    @Deprecated
    public Signature setFitting(SignatureFittingScore fittingScore) {
        this.fittingScore = fittingScore;
        return this;
    }

    public List<SignatureFittingScore> getFittingScores() {
        return fittingScores;
    }

    public Signature setFittingScores(List<SignatureFittingScore> fittingScores) {
        this.fittingScores = fittingScores;
        return this;
    }

    public static class GenomeContextCount {

        @DataField(id = "context", indexed = true,
                description = FieldConstants.GENOME_CONTEXT_COUNT_CONTEXT_DESCRIPTION)
        private String context;
        @DataField(id = "total", indexed = true,
                description = FieldConstants.GENOME_CONTEXT_COUNT_TOTAL_DESCRIPTION)
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
