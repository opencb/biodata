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

import org.opencb.biodata.models.variant.avro.Expression;
import org.opencb.biodata.models.variant.avro.GeneDrugInteraction;
import org.opencb.biodata.models.variant.avro.GeneTraitAssociation;
import org.opencb.biodata.models.variant.avro.Constraint;


import java.util.List;


public class GeneAnnotation {

    private List<Expression> expression;
    private List<GeneTraitAssociation> diseases;
    private List<GeneDrugInteraction> drugs;
    private List<Constraint> constraints;
    private List<MirnaTarget> targets;

    public GeneAnnotation() {
    }

    public GeneAnnotation(List<Expression> expression, List<GeneTraitAssociation> diseases,
                          List<GeneDrugInteraction> drugs, List<Constraint> constraints, List<MirnaTarget> targets) {
        this.expression = expression;
        this.diseases = diseases;
        this.drugs = drugs;
        this.constraints = constraints;
        this.targets = targets;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneAnnotation{");
        sb.append("expression=").append(expression);
        sb.append(", geneTraits=").append(diseases);
        sb.append(", drugs=").append(drugs);
        sb.append(", constraints=").append(constraints);
        sb.append(", targets=").append(targets);
        sb.append('}');
        return sb.toString();
    }

    public List<Expression> getExpression() {
        return expression;
    }

    public GeneAnnotation setExpression(List<Expression> expression) {
        this.expression = expression;
        return this;
    }

    public List<GeneTraitAssociation> getDiseases() {
        return diseases;
    }

    public GeneAnnotation setDiseases(List<GeneTraitAssociation> diseases) {
        this.diseases = diseases;
        return this;
    }

    public List<GeneDrugInteraction> getDrugs() {
        return drugs;
    }

    public GeneAnnotation setDrugs(List<GeneDrugInteraction> drugs) {
        this.drugs = drugs;
        return this;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public GeneAnnotation setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
        return this;
    }

    public List<MirnaTarget> getTargets() {
        return targets;
    }

    public GeneAnnotation setTargets(List<MirnaTarget> targets) {
        this.targets = targets;
        return this;
    }
}
