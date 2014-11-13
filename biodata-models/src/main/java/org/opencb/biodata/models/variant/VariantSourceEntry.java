package org.opencb.biodata.models.variant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.opencb.biodata.models.variant.stats.VariantStats;

/** 
 * Entry that associates a variant and a file in a variant archive. It contains 
 * information related to samples, statistics and specifics of the file format.
 * 
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantSourceEntry {
    
    /**
     * Unique identifier of the archived file.
     */
    private String fileId;
    
    /**
     * Unique identifier of the study containing the archived file.
     */
    private String studyId;
    
    /**
     * Alternate alleles that appear along with a variant alternate.
     */
    private String[] secondaryAlternates;
    
    /**
     * Fields stored for each sample.
     */
    private String format;
    
    /**
     * Genotypes and other sample-related information. The keys are the names
     * of the samples. The values are pairs (field name, field value), such as
     * (GT, A/C).
     */
    private Map<String, Map<String, String>> samplesData;
    
    /**
     * Statistics of the genomic variation, such as its alleles/genotypes count 
     * or its minimum allele frequency.
     */
    private VariantStats stats;
    
    /**
     * Optional attributes that probably depend on the format of the file the
     * variant was initially read from.
     */
    private Map<String, String> attributes;

    
    VariantSourceEntry() { 
        this(null, null);
    }
    
    public VariantSourceEntry(String fileId, String studyId) {
        this.fileId = fileId;
        this.studyId = studyId;
        
        this.samplesData = new LinkedHashMap<>();
        this.attributes = new LinkedHashMap<>();
    }

    
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String[] getSecondaryAlternates() {
        return secondaryAlternates;
    }

    public void setSecondaryAlternates(String[] secondaryAlternates) {
        this.secondaryAlternates = secondaryAlternates;
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

    public String getSampleData(String sampleName, String field) {
        Map<String, String> sampleData = samplesData.get(sampleName);
        if (sampleData == null) {
            return null;
        }
        return samplesData.get(sampleName).get(field.toUpperCase());
    }

    public Map<String, String> getSampleData(String sampleName) {
        return samplesData.get(sampleName);
    }

    public void addSampleData(String sampleName, Map<String, String> sampleData) {
        this.samplesData.put(sampleName, sampleData);
    }

    public Set<String> getSampleNames() {
        return this.samplesData.keySet();
    }

    public VariantStats getStats() {
        return stats;
    }

    public void setStats(VariantStats stats) {
        this.stats = stats;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public boolean hasAttribute(String key) {
        return this.attributes.containsKey(key);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.fileId);
        hash = 41 * hash + Objects.hashCode(this.studyId);
        hash = 41 * hash + Objects.hashCode(this.secondaryAlternates);
        hash = 41 * hash + Objects.hashCode(this.format);
        hash = 41 * hash + Objects.hashCode(this.samplesData);
        hash = 41 * hash + Objects.hashCode(this.attributes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariantSourceEntry other = (VariantSourceEntry) obj;
        if (!Objects.equals(this.fileId, other.fileId)) {
            return false;
        }
        if (!Objects.equals(this.studyId, other.studyId)) {
            return false;
        }
        if (!Objects.equals(this.secondaryAlternates, other.secondaryAlternates)) {
            return false;
        }
        if (!Objects.equals(this.format, other.format)) {
            return false;
        }
        if (!Objects.equals(this.samplesData, other.samplesData)) {
            return false;
        }
        if (!Objects.equals(this.attributes, other.attributes)) {
            return false;
        }
        return true;
    }

}
