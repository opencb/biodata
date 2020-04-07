package org.opencb.biodata.models.core;

import java.util.List;

public class RegulatoryPfm {

    private String id;
    private String name;
    private List<String> transcriptionFactors;
    private float threshold;
    private String source;
    private String unit;
    private int maxPositionSum;
    private int length;
    private List<Element> elements;

    public RegulatoryPfm() {
    }

    public RegulatoryPfm(String id, String name, List<String> transcriptionFactors, float threshold, String source, String unit,
                         int maxPositionSum, int length, List<Element> elements) {
        this.id = id;
        this.name = name;
        this.transcriptionFactors = transcriptionFactors;
        this.threshold = threshold;
        this.source = source;
        this.unit = unit;
        this.maxPositionSum = maxPositionSum;
        this.length = length;
        this.elements = elements;
    }

    static class Element {

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegulatoryPfm{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", transcriptionFactors=").append(transcriptionFactors);
        sb.append(", threshold=").append(threshold);
        sb.append(", source='").append(source).append('\'');
        sb.append(", unit='").append(unit).append('\'');
        sb.append(", maxPositionSum=").append(maxPositionSum);
        sb.append(", length=").append(length);
        sb.append(", elements=").append(elements);
        sb.append('}');
        return sb.toString();
    }

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

    public List<Element> getElements() {
        return elements;
    }

    public RegulatoryPfm setElements(List<Element> elements) {
        this.elements = elements;
        return this;
    }
}
