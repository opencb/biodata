/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.core;

import org.opencb.biodata.models.variant.avro.Expression;
import org.opencb.biodata.models.variant.avro.GeneDrugInteraction;
import org.opencb.biodata.models.variant.avro.GeneTraitAssociation;

import java.util.List;

/**
 * Created by imedina on 12/11/15.
 */
public class GeneAnnotation {

    private List<Expression> expression;
    private List<GeneTraitAssociation> diseases;
    private List<GeneDrugInteraction> drugs;

    public GeneAnnotation() {
    }

    public GeneAnnotation(List<Expression> expression, List<GeneTraitAssociation> diseases, List<GeneDrugInteraction> drugs) {
        this.expression = expression;
        this.diseases = diseases;
        this.drugs = drugs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneAnnotation{");
        sb.append("expression=").append(expression);
        sb.append(", diseases=").append(diseases);
        sb.append(", drugs=").append(drugs);
        sb.append('}');
        return sb.toString();
    }

    public List<Expression> getExpression() {
        return expression;
    }

    public void setExpression(List<Expression> expression) {
        this.expression = expression;
    }

    public List<GeneTraitAssociation> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<GeneTraitAssociation> diseases) {
        this.diseases = diseases;
    }

    public List<GeneDrugInteraction> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<GeneDrugInteraction> drugs) {
        this.drugs = drugs;
    }
}
