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

public class Entity {

    private List<String> availability;
    private List<String> comment;
    private List<String> dataSource;
    private List<String> evidence;
    private List<String> name;
    private List<String> xref;

    public Entity(List<String> availability, List<String> comment,
                  List<String> dataSource, List<String> evidence, List<String> name,
                  List<String> xref) {
        super();
        this.availability = availability;
        this.comment = comment;
        this.dataSource = dataSource;
        this.evidence = evidence;
        this.name = name;
        this.xref = xref;
    }

    public List<String> getAvailability() {
        return availability;
    }

    public void setAvailability(List<String> availability) {
        this.availability = availability;
    }

    public List<String> getComment() {
        return comment;
    }

    public void setComment(List<String> comment) {
        this.comment = comment;
    }

    public List<String> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<String> dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getXref() {
        return xref;
    }

    public void setXref(List<String> xref) {
        this.xref = xref;
    }


}
