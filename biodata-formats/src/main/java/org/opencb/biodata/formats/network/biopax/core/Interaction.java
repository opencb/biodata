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

public class Interaction extends Entity {

    private List<String> interactionType;
    private List<String> participant;

    public Interaction(List<String> availability, List<String> comment,
                       List<String> dataSource, List<String> evidence, List<String> name,
                       List<String> xref, List<String> interactionType,
                       List<String> participant) {
        super(availability, comment, dataSource, evidence, name, xref);
        this.interactionType = interactionType;
        this.participant = participant;
    }

    public List<String> getPathwayComponent() {
        return interactionType;
    }

    public void setPathwayComponent(List<String> pathwayComponent) {
        this.interactionType = pathwayComponent;
    }

    public List<String> getPathwayOrder() {
        return participant;
    }

    public void setPathwayOrder(List<String> pathwayOrder) {
        this.participant = pathwayOrder;
    }


}
