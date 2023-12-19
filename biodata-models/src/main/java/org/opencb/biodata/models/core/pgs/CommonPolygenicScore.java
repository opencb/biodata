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

import java.util.ArrayList;
import java.util.List;

public class CommonPolygenicScore {
    private String id;
    private String name;
    private String source;
    private String version;
    private List<String> pubmedIds;
    private List<EfoTrait> efoTraits;
    private List<Cohort> cohorts;
    private List<PerformanceMetrics> performanceMetrics;

    public CommonPolygenicScore() {
        pubmedIds = new ArrayList<>();
        efoTraits = new ArrayList<>();
        cohorts = new ArrayList<>();
        performanceMetrics = new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommonPolygenicScore{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", pubmedIds=").append(pubmedIds);
        sb.append(", efoTraits=").append(efoTraits);
        sb.append(", cohorts=").append(cohorts);
        sb.append(", performanceMetrics=").append(performanceMetrics);
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

    public List<String> getPubmedIds() {
        return pubmedIds;
    }

    public CommonPolygenicScore setPubmedIds(List<String> pubmedIds) {
        this.pubmedIds = pubmedIds;
        return this;
    }

    public List<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public CommonPolygenicScore setEfoTraits(List<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
        return this;
    }

    public List<Cohort> getCohorts() {
        return cohorts;
    }

    public CommonPolygenicScore setCohorts(List<Cohort> cohorts) {
        this.cohorts = cohorts;
        return this;
    }

    public List<PerformanceMetrics> getPerformanceMetrics() {
        return performanceMetrics;
    }

    public CommonPolygenicScore setPerformanceMetrics(List<PerformanceMetrics> performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
        return this;
    }
}
