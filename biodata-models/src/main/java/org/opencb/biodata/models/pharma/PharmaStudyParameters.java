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

package org.opencb.biodata.models.pharma;

public class PharmaStudyParameters {
    private String studyType;
    private String studyCases;
    private String studyControls;
    private String characteristics;
    private String characteristicsType;
    private String frequencyInCases;
    private String alleleOfFrequencyInCases;
    private String frequencyInControls;
    private String alleleOfFrequencyInControls;
    private String pValue;
    private String ratioStatType;
    private String ratioStat;
    private String confidenceIntervalStart;
    private String confidenceIntervalStop;
    private String biogeographicalGroups;

    public PharmaStudyParameters() {
    }

    public PharmaStudyParameters(String studyType, String studyCases, String studyControls, String characteristics,
                                 String characteristicsType, String frequencyInCases, String alleleOfFrequencyInCases,
                                 String frequencyInControls, String alleleOfFrequencyInControls, String pValue, String ratioStatType,
                                 String ratioStat, String confidenceIntervalStart, String confidenceIntervalStop,
                                 String biogeographicalGroups) {
        this.studyType = studyType;
        this.studyCases = studyCases;
        this.studyControls = studyControls;
        this.characteristics = characteristics;
        this.characteristicsType = characteristicsType;
        this.frequencyInCases = frequencyInCases;
        this.alleleOfFrequencyInCases = alleleOfFrequencyInCases;
        this.frequencyInControls = frequencyInControls;
        this.alleleOfFrequencyInControls = alleleOfFrequencyInControls;
        this.pValue = pValue;
        this.ratioStatType = ratioStatType;
        this.ratioStat = ratioStat;
        this.confidenceIntervalStart = confidenceIntervalStart;
        this.confidenceIntervalStop = confidenceIntervalStop;
        this.biogeographicalGroups = biogeographicalGroups;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaStudyParameters{");
        sb.append("studyType='").append(studyType).append('\'');
        sb.append(", studyCases='").append(studyCases).append('\'');
        sb.append(", studyControls='").append(studyControls).append('\'');
        sb.append(", characteristics='").append(characteristics).append('\'');
        sb.append(", characteristicsType='").append(characteristicsType).append('\'');
        sb.append(", frequencyInCases='").append(frequencyInCases).append('\'');
        sb.append(", alleleOfFrequencyInCases='").append(alleleOfFrequencyInCases).append('\'');
        sb.append(", frequencyInControls='").append(frequencyInControls).append('\'');
        sb.append(", alleleOfFrequencyInControls='").append(alleleOfFrequencyInControls).append('\'');
        sb.append(", pValue='").append(pValue).append('\'');
        sb.append(", ratioStatType='").append(ratioStatType).append('\'');
        sb.append(", ratioStat='").append(ratioStat).append('\'');
        sb.append(", confidenceIntervalStart='").append(confidenceIntervalStart).append('\'');
        sb.append(", confidenceIntervalStop='").append(confidenceIntervalStop).append('\'');
        sb.append(", biogeographicalGroups='").append(biogeographicalGroups).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getStudyType() {
        return studyType;
    }

    public PharmaStudyParameters setStudyType(String studyType) {
        this.studyType = studyType;
        return this;
    }

    public String getStudyCases() {
        return studyCases;
    }

    public PharmaStudyParameters setStudyCases(String studyCases) {
        this.studyCases = studyCases;
        return this;
    }

    public String getStudyControls() {
        return studyControls;
    }

    public PharmaStudyParameters setStudyControls(String studyControls) {
        this.studyControls = studyControls;
        return this;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public PharmaStudyParameters setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
        return this;
    }

    public String getCharacteristicsType() {
        return characteristicsType;
    }

    public PharmaStudyParameters setCharacteristicsType(String characteristicsType) {
        this.characteristicsType = characteristicsType;
        return this;
    }

    public String getFrequencyInCases() {
        return frequencyInCases;
    }

    public PharmaStudyParameters setFrequencyInCases(String frequencyInCases) {
        this.frequencyInCases = frequencyInCases;
        return this;
    }

    public String getAlleleOfFrequencyInCases() {
        return alleleOfFrequencyInCases;
    }

    public PharmaStudyParameters setAlleleOfFrequencyInCases(String alleleOfFrequencyInCases) {
        this.alleleOfFrequencyInCases = alleleOfFrequencyInCases;
        return this;
    }

    public String getFrequencyInControls() {
        return frequencyInControls;
    }

    public PharmaStudyParameters setFrequencyInControls(String frequencyInControls) {
        this.frequencyInControls = frequencyInControls;
        return this;
    }

    public String getAlleleOfFrequencyInControls() {
        return alleleOfFrequencyInControls;
    }

    public PharmaStudyParameters setAlleleOfFrequencyInControls(String alleleOfFrequencyInControls) {
        this.alleleOfFrequencyInControls = alleleOfFrequencyInControls;
        return this;
    }

    public String getpValue() {
        return pValue;
    }

    public PharmaStudyParameters setpValue(String pValue) {
        this.pValue = pValue;
        return this;
    }

    public String getRatioStatType() {
        return ratioStatType;
    }

    public PharmaStudyParameters setRatioStatType(String ratioStatType) {
        this.ratioStatType = ratioStatType;
        return this;
    }

    public String getRatioStat() {
        return ratioStat;
    }

    public PharmaStudyParameters setRatioStat(String ratioStat) {
        this.ratioStat = ratioStat;
        return this;
    }

    public String getConfidenceIntervalStart() {
        return confidenceIntervalStart;
    }

    public PharmaStudyParameters setConfidenceIntervalStart(String confidenceIntervalStart) {
        this.confidenceIntervalStart = confidenceIntervalStart;
        return this;
    }

    public String getConfidenceIntervalStop() {
        return confidenceIntervalStop;
    }

    public PharmaStudyParameters setConfidenceIntervalStop(String confidenceIntervalStop) {
        this.confidenceIntervalStop = confidenceIntervalStop;
        return this;
    }

    public String getBiogeographicalGroups() {
        return biogeographicalGroups;
    }

    public PharmaStudyParameters setBiogeographicalGroups(String biogeographicalGroups) {
        this.biogeographicalGroups = biogeographicalGroups;
        return this;
    }
}
