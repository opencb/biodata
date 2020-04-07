package org.opencb.biodata.models.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegulatoryPfm {

    @JsonProperty("stable_id")
    private String id;
    private String name;
    @JsonProperty("associated_transcription_factor_complexes")
    private List<String> transcriptionFactors;
    private float threshold;
    private String source;
    private String unit;
    @JsonProperty("max_position_sum")
    private int maxPositionSum;
    private int length;
    private Map<String, Map<String, Integer>> elements;
    @JsonProperty("elements_string")
    private String elementsString;

    public RegulatoryPfm() {
    }

    public RegulatoryPfm(String id, String name, List<String> transcriptionFactors, float threshold, String source, String unit,
                         int maxPositionSum, int length, Map<String, Map<String, Integer>> elements, String elementsString) {
        this.id = id;
        this.name = name;
        this.transcriptionFactors = transcriptionFactors;
        this.threshold = threshold;
        this.source = source;
        this.unit = unit;
        this.maxPositionSum = maxPositionSum;
        this.length = length;
        this.elements = elements;
        this.elementsString = elementsString;
    }

//    protected class Element {
//
//        private String base;
//        private int count;
//
//        public String getBase() {
//            return base;
//        }
//
//        public Element setBase(String base) {
//            this.base = base;
//            return this;
//        }
//
//        public int getCount() {
//            return count;
//        }
//
//        public Element setCount(int count) {
//            this.count = count;
//            return this;
//        }
//    }

    public String getId() {
        return id;
    }

    public RegulatoryPfm setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RegulatoryPfm setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getTranscriptionFactors() {
        return transcriptionFactors;
    }

    public RegulatoryPfm setTranscriptionFactors(List<String> transcriptionFactors) {
        this.transcriptionFactors = transcriptionFactors;
        return this;
    }

    public float getThreshold() {
        return threshold;
    }

    public RegulatoryPfm setThreshold(float threshold) {
        this.threshold = threshold;
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

    public int getMaxPositionSum() {
        return maxPositionSum;
    }

    public RegulatoryPfm setMaxPositionSum(int maxPositionSum) {
        this.maxPositionSum = maxPositionSum;
        return this;
    }

    public int getLength() {
        return length;
    }

    public RegulatoryPfm setLength(int length) {
        this.length = length;
        return this;
    }

    public Map<String, Map<String, Integer>> getElements() {
        return elements;
    }

    public RegulatoryPfm setElements(Map<String, Map<String, Integer>> elements) {
        this.elements = elements;
        return this;
    }

    public String getElementsString() {
        return elementsString;
    }

    public RegulatoryPfm setElementsString(String elementsString) {
        this.elementsString = elementsString;
        return this;
    }
}
