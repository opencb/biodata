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

package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fsalavert.
 * Date: 4/10/13
 * Time: 12:09 PM
 */
public class RegulatoryFeature {

    private String id;
    private String chromosome;
    private String source;
    private String featureType;
    private int start;
    private int end;
    private String score;
    private String strand;
    private String frame;
    private String itemRGB;
    private String name;
    private String featureClass;
    private String alias;
    private List<String> cellTypes = new ArrayList<>();
    private String matrix;

    public RegulatoryFeature() {
    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegulatoryFeature{");
        sb.append("id='").append(id).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", featureType='").append(featureType).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", score='").append(score).append('\'');
        sb.append(", strand='").append(strand).append('\'');
        sb.append(", frame='").append(frame).append('\'');
        sb.append(", itemRGB='").append(itemRGB).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", featureClass='").append(featureClass).append('\'');
        sb.append(", alias='").append(alias).append('\'');
        sb.append(", cellTypes=").append(cellTypes);
        sb.append(", matrix='").append(matrix).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getItemRGB() {
        return itemRGB;
    }

    public void setItemRGB(String itemRGB) {
        this.itemRGB = itemRGB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeatureClass() {
        return featureClass;
    }

    public void setFeatureClass(String featureClass) {
        this.featureClass = featureClass;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public List<String> getCellTypes() {
        return cellTypes;
    }

    public void setCellTypes(List<String> cellTypes) {
        this.cellTypes = cellTypes;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

}
