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

package org.opencb.biodata.models.clinical.interpretation;

import org.opencb.biodata.models.alignment.RegionCoverage;

public class ReportedLowCoverage {
    private String geneName;
    private String chromosome;
    private int start;
    private int end;
    private double meanCoverage;
    private String id;
    private String type;

    public ReportedLowCoverage() {
    }

    public ReportedLowCoverage(RegionCoverage regionCoverage) {
        this.chromosome = regionCoverage.getChromosome();
        this.start = regionCoverage.getStart();
        this.end = regionCoverage.getEnd();

        double coverage = 0;
        for (double value: regionCoverage.getValues()) {
            coverage += value;
        }
        this.meanCoverage = coverage / regionCoverage.getValues().length;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReportedLowCoverage{");
        sb.append("geneName='").append(geneName).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", meanCoverage=").append(meanCoverage);
        sb.append(", id='").append(id).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getGeneName() {
        return geneName;
    }

    public ReportedLowCoverage setGeneName(String geneName) {
        this.geneName = geneName;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public ReportedLowCoverage setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public ReportedLowCoverage setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public ReportedLowCoverage setEnd(int end) {
        this.end = end;
        return this;
    }

    public double getMeanCoverage() {
        return meanCoverage;
    }

    public ReportedLowCoverage setMeanCoverage(double meanCoverage) {
        this.meanCoverage = meanCoverage;
        return this;
    }

    public String getId() {
        return id;
    }

    public ReportedLowCoverage setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public ReportedLowCoverage setType(String type) {
        this.type = type;
        return this;
    }
}
