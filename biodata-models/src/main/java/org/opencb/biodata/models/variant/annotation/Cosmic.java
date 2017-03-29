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

/**
 * Created by antonior on 21/11/14.
 */
public class Cosmic {
    private String mutationID;
    private String primarySite;
    private String siteSubtype;
    private String primaryHistology;
    private String histologySubtype;
    private String sampleSource;
    private String tumourOrigin;
    private String geneName;
    private String mutationSomaticStatus;


    Cosmic() { }

    public Cosmic(String mutationID, String primarySite, String siteSubtype, String primaryHistology, String histologySubtype, String sampleSource, String tumourOrigin, String geneName, String mutationSomaticStatus) {
        this.mutationID = mutationID;
        this.primarySite = primarySite;
        this.siteSubtype = siteSubtype;
        this.primaryHistology = primaryHistology;
        this.histologySubtype = histologySubtype;
        this.sampleSource = sampleSource;
        this.tumourOrigin = tumourOrigin;
        this.geneName = geneName;
        this.mutationSomaticStatus = mutationSomaticStatus;
    }

    public String getMutationID() {
        return mutationID;
    }

    public void setMutationID(String mutationID) {
        this.mutationID = mutationID;
    }

    public String getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(String primarySite) {
        this.primarySite = primarySite;
    }

    public String getSiteSubtype() {
        return siteSubtype;
    }

    public void setSiteSubtype(String siteSubtype) {
        this.siteSubtype = siteSubtype;
    }

    public String getPrimaryHistology() {
        return primaryHistology;
    }

    public void setPrimaryHistology(String primaryHistology) {
        this.primaryHistology = primaryHistology;
    }

    public String getHistologySubtype() {
        return histologySubtype;
    }

    public void setHistologySubtype(String histologySubtype) {
        this.histologySubtype = histologySubtype;
    }

    public String getSampleSource() {
        return sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    public String getTumourOrigin() {
        return tumourOrigin;
    }

    public void setTumourOrigin(String tumourOrigin) {
        this.tumourOrigin = tumourOrigin;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getMutationSomaticStatus() {
        return mutationSomaticStatus;
    }

    public void setMutationSomaticStatus(String mutationSomaticStatus) {
        this.mutationSomaticStatus = mutationSomaticStatus;
    }

}
