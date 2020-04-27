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

package org.opencb.biodata.models.variant.annotation;

import java.util.List;

/**
 * Created by fjlopez on 16/09/15.
 */
@Deprecated
public class VariantTraitAssociation {

    private List<Cosmic> cosmicList;
    private List<Gwas> gwasList;
    private List<Clinvar> clinvarList;

    public VariantTraitAssociation() {}

    public VariantTraitAssociation(List<Cosmic> cosmicList, List<Gwas> gwasList, List<Clinvar> clinvarList) {
        this.cosmicList = cosmicList;
        this.gwasList = gwasList;
        this.clinvarList = clinvarList;
    }

    public List<Cosmic> getCosmicList() { return cosmicList; }

    public void setCosmicList(List<Cosmic> cosmicList) {
        this.cosmicList = cosmicList;
    }

    public List<Gwas> getGwasList() {
        return gwasList;
    }

    public void setGwasList(List<Gwas> gwasList) {
        this.gwasList = gwasList;
    }

    public List<Clinvar> getClinvarList() {
        return clinvarList;
    }

    public void setClinvarList(List<Clinvar> clinvarList) {
        this.clinvarList = clinvarList;
    }

    public int size() {
        return (cosmicList != null? cosmicList.size() : 0)
                + (gwasList != null? gwasList.size() : 0)
                + (clinvarList != null? clinvarList.size() : 0);
    }
}
