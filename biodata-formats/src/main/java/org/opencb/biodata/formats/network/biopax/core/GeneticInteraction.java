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

package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class GeneticInteraction extends Interaction {

    private List<String> interactionScore;
    private List<String> phenotype;

    public GeneticInteraction(List<String> availability, List<String> comment,
                              List<String> dataSource, List<String> evidence, List<String> name,
                              List<String> xref, List<String> interactionType,
                              List<String> participant, List<String> interactionScore,
                              List<String> phenotype) {
        super(availability, comment, dataSource, evidence, name, xref,
                interactionType, participant);
        this.interactionScore = interactionScore;
        this.phenotype = phenotype;
    }

    public List<String> getInteractionScore() {
        return interactionScore;
    }

    public void setInteractionScore(List<String> interactionScore) {
        this.interactionScore = interactionScore;
    }

    public List<String> getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(List<String> phenotype) {
        this.phenotype = phenotype;
    }


}
