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

import java.util.List;

/**
 * Created by imedina on 12/11/15.
 */
public class GeneAnnotation {

    private List<Expression> expressionValues;
    private List<Disease> diseases;
    private List<GeneDrugInteraction> drugInteractions;

    public GeneAnnotation() {
    }

    public GeneAnnotation(List<Expression> expressionValues, List<Disease> diseases, List<GeneDrugInteraction> drugInteractions) {
        this.expressionValues = expressionValues;
        this.diseases = diseases;
        this.drugInteractions = drugInteractions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneAnnotation{");
        sb.append("expressionValues=").append(expressionValues);
        sb.append(", diseases=").append(diseases);
        sb.append(", drugInteractions=").append(drugInteractions);
        sb.append('}');
        return sb.toString();
    }

    public List<Expression> getExpressionValues() {
        return expressionValues;
    }

    public void setExpressionValues(List<Expression> expressionValues) {
        this.expressionValues = expressionValues;
    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<Disease> diseases) {
        this.diseases = diseases;
    }

    public List<GeneDrugInteraction> getDrugInteractions() {
        return drugInteractions;
    }

    public void setDrugInteractions(List<GeneDrugInteraction> drugInteractions) {
        this.drugInteractions = drugInteractions;
    }
}
