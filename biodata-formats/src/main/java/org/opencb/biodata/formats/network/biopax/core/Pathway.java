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

public class Pathway extends Entity {

    private String organism;
    private List<String> pathwayComponent;
    private List<String> pathwayOrder;

    public Pathway(List<String> availability, List<String> comment,
                   List<String> dataSource, List<String> evidence, List<String> name,
                   List<String> xref, String organism, List<String> pathwayComponent,
                   List<String> pathwayOrder) {
        super(availability, comment, dataSource, evidence, name, xref);
        this.organism = organism;
        this.pathwayComponent = pathwayComponent;
        this.pathwayOrder = pathwayOrder;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public List<String> getPathwayComponent() {
        return pathwayComponent;
    }

    public void setPathwayComponent(List<String> pathwayComponent) {
        this.pathwayComponent = pathwayComponent;
    }

    public List<String> getPathwayOrder() {
        return pathwayOrder;
    }

    public void setPathwayOrder(List<String> pathwayOrder) {
        this.pathwayOrder = pathwayOrder;
    }


}
