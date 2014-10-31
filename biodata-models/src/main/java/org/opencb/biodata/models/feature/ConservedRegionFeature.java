package org.opencb.biodata.models.feature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lcruz
 * @since 31/10/2014
 */
public class ConservedRegionFeature {
    private String chromosome;
    private int start;
    private int end;
    private int chunk;
    private List<ConservedRegionSource>sources;

    public ConservedRegionFeature(String chromosome, int start, int end, int chunk) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.chunk = chunk;
        sources = new ArrayList<>();
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

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public List<ConservedRegionSource> getSources() {
        return sources;
    }

    public void setSources(List<ConservedRegionSource> sources) {
        this.sources = sources;
    }

    public void addSources(List<ConservedRegionSource> sources) {
        for (ConservedRegionSource source : sources) {
            this.addSource(source);
        }
    }

    public void addSource(ConservedRegionSource source) {
        this.sources.add(source);
    }

    public class ConservedRegionSource {
        private String type;
        private List<Float>values;

        public ConservedRegionSource(String type){
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Float> getValues() {
            return values;
        }

        public void setValues(List<Float> values) {
            this.values = values;
        }

        public void addValues(List<Float> values) {
            for (Float value : values) {
                this.addValue(value);
            }
        }

        public void addValue(Float value) {
            this.values.add(value);
        }
    }
}