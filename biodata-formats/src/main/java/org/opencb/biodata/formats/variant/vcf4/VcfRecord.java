package org.opencb.biodata.formats.variant.vcf4;

import java.util.*;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.annotation.VariantEffect;
import org.opencb.biodata.models.variant.stats.VariantStats;

public class VcfRecord {

    private String chromosome;
    private int position;
    private String id;
    private String reference;
    private String alternate;
    private String quality;
    private String filter;
    private String info;
    private String format;
    private List<String> sampleOrder;
    private Map<String, String> sampleRawData;
    private Map<String, Map<String, String>> sampleData;
    private VariantStats stats;
    private List<VariantEffect> effects;

    /**
     * @param chromosome
     * @param position
     * @param id
     * @param reference
     * @param alternate
     * @param quality
     * @param filter
     * @param info
     */
    public VcfRecord(String chromosome,
            Integer position,
            String id,
            String reference,
            String alternate,
            String quality,
            String filter,
            String info) {

        this.chromosome = parseChromosome(chromosome);
        this.position = position;
        this.id = id;
        this.reference = reference;
        this.alternate = alternate;
        this.quality = quality;
        this.filter = filter;
        this.info = info;

        this.sampleRawData = new HashMap<>();
        this.sampleData = new HashMap<>();
        this.sampleOrder = new ArrayList<>();
        this.effects = null;
        this.stats = null;
    }

    /**
     * @param chromosome
     * @param position
     * @param id
     * @param reference
     * @param alternate
     * @param quality
     * @param filter
     * @param info
     */
    public VcfRecord(String chromosome, Integer position, String id, String reference, String alternate, String quality, String filter, String info, String format) {
        this(chromosome, position, id, reference, alternate, quality, filter, info);
        this.format = format;
    }

    /**
     * @param chromosome
     * @param position
     * @param id
     * @param reference
     * @param alternate
     * @param quality
     * @param filter
     * @param info
     * @param format
     * @param sampleList
     */
    public VcfRecord(String chromosome, Integer position, String id, String reference, String alternate, String quality, String filter, String info, String format, String... sampleList) {
        this(chromosome, position, id, reference, alternate, quality, filter, info, format);

    }

    public VcfRecord(String[] fields, List<String> sampleNames) {
        this(fields[0], Integer.parseInt(fields[1]), fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]);
        this.sampleOrder = sampleNames;

        for (int i = 9; i < fields.length; i++) {
            sampleRawData.put(sampleNames.get(i - 9), fields[i]);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(chromosome).append("\t");
        builder.append(position).append("\t");
        builder.append(id).append("\t");
        builder.append(reference).append("\t");
        builder.append(alternate).append("\t");
        builder.append(quality).append("\t");
        builder.append(filter).append("\t");
        builder.append(info);
        if (format != null) {
            builder.append("\t").append(format);
        }
        if (sampleOrder.size() > 0) {

            for (String sample : sampleOrder) {
                builder.append("\t").append(sampleRawData.get(sample));
            }
        }

        return builder.toString();
    }

    /**
     * @return the chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the alternate
     */
    public String getAlternate() {
        return alternate;
    }

    /**
     * @param alternate the alternate to set
     */
    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    /**
     * @return the quality
     */
    public String getQuality() {
        return quality;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(String quality) {
        this.quality = quality;
    }

    /**
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public String[] getAltAlleles() {
        return this.getAlternate().split(",");
    }

    public String getValueFormatSample(String sample, String key) {
        if (sampleData.isEmpty()) {
            initializeSamplesValues();
        }

        return sampleData.get(sample).get(key);
    }

    public Genotype getSampleGenotype(String sample) {
        Genotype g = null;
        String gtVal = getValueFormatSample(sample, "GT");
        if (gtVal != null) {
            g = new Genotype(gtVal, reference, alternate);
        }
        return g;
    }

    public Set<String> getSampleNames() {
        return sampleRawData.keySet();
    }

    public Map<String, String> getSampleData(String sampleName) {
        if (sampleData.isEmpty()) {
            initializeSamplesValues();
        }

        return sampleData.get(sampleName);
    }

    public Map<String, Map<String, String>> getSampleData() {
        if (sampleData.isEmpty()) {
            initializeSamplesValues();
        }

        return sampleData;
    }

    public String getSampleRawData(String sampleName) {
        return sampleRawData.get(sampleName);
    }

    public Map<String, String> getSampleRawData() {
        return sampleRawData;
    }

    private void initializeSamplesValues() {
        String[] fields = this.format.split(":");

        for (Map.Entry<String, String> entry : sampleRawData.entrySet()) {
            String sampleName = entry.getKey();
            String[] values = entry.getValue().split(":");

            Map<String, String> sampleValuesMap = new HashMap<>(fields.length);

            for (int i = 0; i < fields.length; i++) {
                sampleValuesMap.put(fields[i], values[i]);
            }

            sampleData.put(sampleName, sampleValuesMap);
        }

    }

    public void addInfoField(String info) {
        if (this.info.equals(".")) {
            this.info = info;
        } else {
            this.info += ";" + info;
        }
    }

    public void addSnp(String snp) {
        if (this.id.equals(".")) {
            this.id = snp;
        } else {
            this.id += ";" + snp;
        }
    }

    public VariantStats getStats() {
        return stats;
    }

    public void setStats(VariantStats stats) {
        this.stats = stats;
    }

    public List<VariantEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<VariantEffect> effects) {
        this.effects = effects;
    }

    public void addEffect(VariantEffect effect) {
        if (this.effects != null) {
            this.effects.add(effect);
        }
    }

    public boolean isIndel() {
        return this.reference.length() > 1 || this.alternate.length() > 1;
    }

    /**
     * Erase the prefix 'chr' or 'chrom' from the chromosome.
     *
     * @param chromosome
     * @return String without prefix
     */
    private String parseChromosome(String chromosome) {
        return chromosome.replaceAll("chrom|chr", "");
    }

}
