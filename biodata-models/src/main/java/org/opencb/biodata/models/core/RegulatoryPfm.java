package org.opencb.biodata.models.core;

import java.util.List;

public class RegulatoryPfm {

    private String name;
    private Float threshold;
    private List<String> associatedTranscriptionFactorComplexes;
    private String source;
    private String unit;
    private Integer maxPositionSum;
    private Integer length;
    private String stableId;
    private String elementsString;
    private List<Element> elements;

    public String getName() {
        return name;
    }

    public RegulatoryPfm setName(String name) {
        this.name = name;
        return this;
    }

    public Float getThreshold() {
        return threshold;
    }

    public RegulatoryPfm setThreshold(Float threshold) {
        this.threshold = threshold;
        return this;
    }

    public List<String> getAssociatedTranscriptionFactorComplexes() {
        return associatedTranscriptionFactorComplexes;
    }

    public RegulatoryPfm setAssociatedTranscriptionFactorComplexes(List<String> associatedTranscriptionFactorComplexes) {
        this.associatedTranscriptionFactorComplexes = associatedTranscriptionFactorComplexes;
        return this;
    }

    public String getSource() {
        return source;
    }

    public RegulatoryPfm setSource(String source) {
        this.source = source;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public RegulatoryPfm setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Integer getMaxPositionSum() {
        return maxPositionSum;
    }

    public RegulatoryPfm setMaxPositionSum(Integer maxPositionSum) {
        this.maxPositionSum = maxPositionSum;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public RegulatoryPfm setLength(Integer length) {
        this.length = length;
        return this;
    }

    public String getStableId() {
        return stableId;
    }

    public RegulatoryPfm setStableId(String stableId) {
        this.stableId = stableId;
        return this;
    }

    public String getElementsString() {
        return elementsString;
    }

    public RegulatoryPfm setElementsString(String elementsString) {
        this.elementsString = elementsString;
        return this;
    }

    public List<Element> getElements() {
        return elements;
    }

    public RegulatoryPfm setElements(List<Element> elements) {
        this.elements = elements;
        return this;
    }
}


class Element {
    private char base;
    private int count;

    public char getBase() {
        return base;
    }

    public Element setBase(char base) {
        this.base = base;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Element setCount(int count) {
        this.count = count;
        return this;
    }
}


