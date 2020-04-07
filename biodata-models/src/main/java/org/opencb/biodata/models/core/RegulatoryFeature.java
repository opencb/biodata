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

package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.List;


public class RegulatoryFeature {

    private String id;
    private String chromosome;
    private int start;
    private int end;
    private String strand;
    private String source;
    private String featureType;
    private String score;
    @Deprecated
    private String frame;
    @Deprecated
    private String itemRGB;
    @Deprecated
    private String name;
    @Deprecated
    private String featureClass;
    @Deprecated
    private String alias;
    @Deprecated
    private List<String> cellTypes = new ArrayList<>();
    @Deprecated
    private String matrix;

    public RegulatoryFeature() {
    }

    public RegulatoryFeature(String id, String chromosome, String featureType, int start, int end) {
        this.id = id;
        this.chromosome = chromosome;
        this.featureType = featureType;
        this.start = start;
        this.end = end;
    }

    @Deprecated
    public RegulatoryFeature(String id, String chromosome, String source, String featureType, int start, int end, String score,
                             String strand, String frame, String itemRGB, String name, String featureClass, String alias,
                             List<String> cellTypes, String matrix) {
        this.id = id;
        this.chromosome = chromosome;
        this.source = source;
        this.featureType = featureType;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.frame = frame;
        this.itemRGB = itemRGB;
        this.name = name;
        this.featureClass = featureClass;
        this.alias = alias;
        this.cellTypes = cellTypes;
        this.matrix = matrix;
    }

    public RegulatoryFeature(String id, String chromosome, int start, int end, String strand, String source, String featureType,
                             String score) {
        this.id = id;
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.source = source;
        this.featureType = featureType;
        this.score = score;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegulatoryFeature{");
        sb.append("id='").append(id).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", strand='").append(strand).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", featureType='").append(featureType).append('\'');
        sb.append(", score='").append(score).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public RegulatoryFeature setId(String id) {
        this.id = id;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public RegulatoryFeature setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getStart() {
        return start;
    }

    public RegulatoryFeature setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public RegulatoryFeature setEnd(int end) {
        this.end = end;
        return this;
    }

    public String getStrand() {
        return strand;
    }

    public RegulatoryFeature setStrand(String strand) {
        this.strand = strand;
        return this;
    }

    public String getSource() {
        return source;
    }

    public RegulatoryFeature setSource(String source) {
        this.source = source;
        return this;
    }

    public String getFeatureType() {
        return featureType;
    }

    public RegulatoryFeature setFeatureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

    public String getScore() {
        return score;
    }

    public RegulatoryFeature setScore(String score) {
        this.score = score;
        return this;
    }

    @Deprecated
    public String getFrame() {
        return frame;
    }

    @Deprecated
    public RegulatoryFeature setFrame(String frame) {
        this.frame = frame;
        return this;
    }

    @Deprecated
    public String getItemRGB() {
        return itemRGB;
    }

    @Deprecated
    public RegulatoryFeature setItemRGB(String itemRGB) {
        this.itemRGB = itemRGB;
        return this;
    }

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public RegulatoryFeature setName(String name) {
        this.name = name;
        return this;
    }

    @Deprecated
    public String getFeatureClass() {
        return featureClass;
    }

    @Deprecated
    public RegulatoryFeature setFeatureClass(String featureClass) {
        this.featureClass = featureClass;
        return this;
    }

    @Deprecated
    public String getAlias() {
        return alias;
    }

    @Deprecated
    public RegulatoryFeature setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Deprecated
    public List<String> getCellTypes() {
        return cellTypes;
    }

    @Deprecated
    public RegulatoryFeature setCellTypes(List<String> cellTypes) {
        this.cellTypes = cellTypes;
        return this;
    }

    @Deprecated
    public String getMatrix() {
        return matrix;
    }

    @Deprecated
    public RegulatoryFeature setMatrix(String matrix) {
        this.matrix = matrix;
        return this;
    }
}
