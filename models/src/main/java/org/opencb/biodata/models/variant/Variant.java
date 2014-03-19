package org.opencb.biodata.models.variant;

import java.util.*;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 11/20/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Variant {
    private String chromosome;
    private int position;
    private String reference;
    private String alternate;
    private String id;
    private String format;
    private Map<String, Map<String, String>> samplesData;
    private VariantStats stats;
    private List<VariantEffect> effect;

    /**
     * Optional attributes that probably depend on the format of the file the
     * variant was initially read.
     */
    private Map<String, String> attributes;

    public Variant(String chromosome, int position, String reference, String alternate) {
        this.chromosome = chromosome;
        this.position = position;
        this.reference = reference;
        this.alternate = alternate;
        this.samplesData = new LinkedHashMap<>();
        this.effect = new LinkedList<>();
        this.attributes = new LinkedHashMap<>();
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, Map<String, String>> getSamplesData() {
        return samplesData;
    }

    public Map<String, String> getSampleData(String sampleName) {
        return samplesData.get(sampleName);
    }

    public VariantStats getStats() {
        return stats;
    }

    public void setStats(VariantStats stats) {
        this.stats = stats;
    }

    public List<VariantEffect> getEffect() {
        return effect;
    }

    public void setEffect(List<VariantEffect> effect) {
        this.effect = effect;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public boolean addEffect(VariantEffect e) {
        return this.effect.add(e);
    }

    public void addId(String newId) {
        if (!this.id.contains(newId)) {
            if (this.id.equals(".")) {
                this.id = newId;
            } else {
                this.id += ";" + newId;
            }
        }
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public boolean containsAttribute(String key) {
        return this.attributes.containsKey(key);
    }

    public void addSampleData(String sampleName, Map<String, String> sampleData) {
        this.samplesData.put(sampleName, sampleData);
    }

    public String getSampleData(String sampleName, String field) {
        return this.samplesData.get(sampleName).get(field.toUpperCase());
    }

    public Iterable<String> getSampleNames() {
        return this.samplesData.keySet();
    }

    @Override
    public String toString() {
        return "Variant{" +
                "chromosome='" + chromosome + '\'' +
                ", position=" + position +
                ", reference='" + reference + '\'' +
                ", alternate='" + alternate + '\'' +
                ", id='" + id + '\'' +
                ", format='" + format + '\'' +
                ", samplesData=" + samplesData +
                ", stats=" + stats +
                ", effect=" + effect +
                ", attributes=" + attributes +
                '}';
    }

    public String[] getAltAlleles() {
        return this.getAlternate().split(",");
    }

    public boolean isIndel() {
        return (this.reference.length() > 1 || this.alternate.length() > 1) && (this.reference.length() != this.alternate.length());
    }

}
