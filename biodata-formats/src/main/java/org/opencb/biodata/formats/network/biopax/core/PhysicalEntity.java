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

public class PhysicalEntity extends Entity {

    private List<String> bindsTo;
    private List<String> cellularLocation;
    private List<String> feature;
    private List<String> notFeature;
    private List<String> referenceEntity;


    public PhysicalEntity(List<String> availability, List<String> comment,
                          List<String> dataSource, List<String> evidence, List<String> name,
                          List<String> xref, List<String> bindsTo,
                          List<String> cellularLocation, List<String> feature,
                          List<String> notFeature, List<String> referenceEntity) {
        super(availability, comment, dataSource, evidence, name, xref);
        this.bindsTo = bindsTo;
        this.cellularLocation = cellularLocation;
        this.setFeature(feature);
        this.notFeature = notFeature;
        this.referenceEntity = referenceEntity;
    }


    public void setFeature(List<String> feature) {
        this.feature = feature;
    }

    public List<String> getFeature() {
        return feature;
    }

    public List<String> getBindsTo() {
        return bindsTo;
    }

    public void setBindsTo(List<String> bindsTo) {
        this.bindsTo = bindsTo;
    }

    public List<String> getCellularLocation() {
        return cellularLocation;
    }

    public void setCellularLocation(List<String> cellularLocation) {
        this.cellularLocation = cellularLocation;
    }

    public List<String> getNotFeature() {
        return notFeature;
    }

    public void setNotFeature(List<String> notFeature) {
        this.notFeature = notFeature;
    }

    public List<String> getReferenceEntity() {
        return referenceEntity;
    }

    public void setReferenceEntity(List<String> referenceEntity) {
        this.referenceEntity = referenceEntity;
    }


}
