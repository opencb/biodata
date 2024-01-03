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

import org.opencb.biodata.models.variant.avro.OntologyTermAnnotation;
import org.opencb.biodata.models.variant.avro.PubmedReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonPolygenicScore {
    private String id;
    private String name;
    private String source;
    private String version;
    private List<PubmedReference> pubmedRefs;
    private List<OntologyTermAnnotation> traits;
    private List<PgsCohort> cohorts;
    private List<Map<String, String>> values;

    public CommonPolygenicScore() {
        this.pubmedRefs = new ArrayList<>();
        this.traits = new ArrayList<>();
        this.cohorts = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public CommonPolygenicScore(String id, String name, String source, String version, List<PubmedReference> pubmedRefs,
                                List<OntologyTermAnnotation> traits, List<PgsCohort> cohorts, List<Map<String, String>> values) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.version = version;
        this.pubmedRefs = pubmedRefs;
        this.traits = traits;
        this.cohorts = cohorts;
        this.values = values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommonPolygenicScore{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", pubmedRefs=").append(pubmedRefs);
        sb.append(", traits=").append(traits);
        sb.append(", cohorts=").append(cohorts);
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public CommonPolygenicScore setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CommonPolygenicScore setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public CommonPolygenicScore setSource(String source) {
        this.source = source;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public CommonPolygenicScore setVersion(String version) {
        this.version = version;
        return this;
    }

    public List<PubmedReference> getPubmedRefs() {
        return pubmedRefs;
    }

    public CommonPolygenicScore setPubmedRefs(List<PubmedReference> pubmedRefs) {
        this.pubmedRefs = pubmedRefs;
        return this;
    }

    public List<OntologyTermAnnotation> getTraits() {
        return traits;
    }

    public CommonPolygenicScore setTraits(List<OntologyTermAnnotation> traits) {
        this.traits = traits;
        return this;
    }

    public List<PgsCohort> getCohorts() {
        return cohorts;
    }

    public CommonPolygenicScore setCohorts(List<PgsCohort> cohorts) {
        this.cohorts = cohorts;
        return this;
    }

    public List<Map<String, String>> getValues() {
        return values;
    }

    public CommonPolygenicScore setValues(List<Map<String, String>> values) {
        this.values = values;
        return this;
    }
}
