package org.opencb.biodata.models.feature;

import java.util.ArrayList;
import java.util.LinkedList;
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

    public ConservedRegionFeature() {
        sources = new ArrayList<>();
    }

    public ConservedRegionFeature(String chromosome, int start, int end, int chunk) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.chunk = chunk;
        this.sources = new ArrayList<>();
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

    public void addSource(String type, List<Float> values) {
        this.sources.add(new ConservedRegionSource(type, values));
    }

    public Integer compareTo(Object o) {
        Integer result = null;
        ConservedRegionFeature objC = (ConservedRegionFeature)o;

        if (this.getChromosome().equalsIgnoreCase(objC.getChromosome())){
            result = this.getStart()-objC.getStart();
        }

        return result;
    }

    public String toString(){
        StringBuilder result = new StringBuilder("{\"chunk\":\""+this.getChunk()+"\", \"chr\":\""+this.getChromosome()+"\", \"start\":\""+this.getStart()+"\", " +
                "\"end\":\""+this.getEnd()+"\", sources[");

       for(ConservedRegionSource source: this.sources){
            result.append("{\"type\":\""+source.getType()+"\", \"values\":[");

            for(Float f: source.values){
                if(f == null){
                    result.append("null,");
                } else {
                    result.append(f.toString()+",");
                }
            }

            result.deleteCharAt(result.length()-1);

            result.append("]}");
        }

        result.append("]}");

        return result.toString();
    }

    public static class ConservedRegionSource{
        private String type;
        private List<Float>values;

        public ConservedRegionSource (){
            values = new LinkedList<>();
        }

        public ConservedRegionSource(String type, List<Float> values) {
            this.type = type;
            this.values = values;
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
            this.values.addAll(values);
        }

        public void addValue(Float value) {
            this.values.add(value);
        }
    }
}