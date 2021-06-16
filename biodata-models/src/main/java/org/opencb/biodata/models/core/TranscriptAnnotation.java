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

import java.util.List;
import org.opencb.biodata.models.variant.avro.Constraint;

public class TranscriptAnnotation {

    private List<FeatureOntologyTermAnnotation> ontologies;
    private List<Constraint> constraints;

    public TranscriptAnnotation() {
    }

    public TranscriptAnnotation(List<FeatureOntologyTermAnnotation> ontologies, List<Constraint> constraints) {
        this.constraints = constraints;
        this.ontologies = ontologies;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranscriptAnnotation{");
        sb.append("ontologies=").append(ontologies);
        sb.append(", constraints=").append(constraints);
        sb.append('}');
        return sb.toString();
    }

    public List<FeatureOntologyTermAnnotation> getOntologies() {
        return ontologies;
    }

    public TranscriptAnnotation setOntologies(List<FeatureOntologyTermAnnotation> ontologies) {
        this.ontologies = ontologies;
        return this;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public TranscriptAnnotation setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
        return this;
    }
}
